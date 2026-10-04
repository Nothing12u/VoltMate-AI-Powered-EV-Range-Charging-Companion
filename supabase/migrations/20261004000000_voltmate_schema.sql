-- ==============================================================================
-- VoltMate Supabase Postgres Database Schema & Seed Data
-- ==============================================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Profiles Table
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email TEXT UNIQUE NOT NULL,
    full_name TEXT NOT NULL,
    avatar_url TEXT,
    city TEXT DEFAULT 'Bengaluru',
    total_carbon_avoided_kg NUMERIC(10, 2) DEFAULT 0.00,
    total_savings_inr NUMERIC(10, 2) DEFAULT 0.00,
    voltshare_earnings_inr NUMERIC(10, 2) DEFAULT 0.00,
    preferred_route_mode TEXT DEFAULT 'CHEAPEST' CHECK (preferred_route_mode IN ('CHEAPEST', 'FASTEST', 'GREENEST')),
    safety_reserve_soc INT DEFAULT 15 CHECK (safety_reserve_soc BETWEEN 5 AND 35),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 2. Vehicles Table
CREATE TABLE IF NOT EXISTS public.vehicles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    make TEXT NOT NULL,
    model TEXT NOT NULL,
    license_plate TEXT,
    battery_capacity_kwh NUMERIC(6, 2) NOT NULL,
    baseline_efficiency_wh_km NUMERIC(6, 2) NOT NULL,
    current_soc_percent INT NOT NULL CHECK (current_soc_percent BETWEEN 0 AND 100),
    connector_type TEXT NOT NULL,
    vehicle_mass_kg NUMERIC(6, 1) DEFAULT 1800.0,
    is_primary BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 3. Chargers Table (Public & VoltShare Hosts)
CREATE TABLE IF NOT EXISTS public.chargers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    host_user_id UUID REFERENCES public.profiles(id) ON DELETE SET NULL,
    name TEXT NOT NULL,
    type TEXT NOT NULL CHECK (type IN ('PUBLIC', 'VOLTSHARE')),
    connector_type TEXT NOT NULL,
    power_kw NUMERIC(5, 1) NOT NULL,
    price_per_kwh_inr NUMERIC(6, 2) NOT NULL CHECK (price_per_kwh_inr >= 0),
    estimated_wait_minutes INT DEFAULT 0,
    carbon_intensity_gco2_kwh INT DEFAULT 500,
    availability_status TEXT DEFAULT 'AVAILABLE' CHECK (availability_status IN ('AVAILABLE', 'BUSY', 'UNAVAILABLE')),
    rating NUMERIC(3, 2) DEFAULT 4.8 CHECK (rating BETWEEN 0 AND 5),
    review_count INT DEFAULT 0,
    latitude NUMERIC(10, 7) NOT NULL,
    longitude NUMERIC(10, 7) NOT NULL,
    address TEXT NOT NULL,
    is_host_verified BOOLEAN DEFAULT false,
    house_rules TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 4. Charger Amenities
CREATE TABLE IF NOT EXISTS public.charger_amenities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    charger_id UUID NOT NULL REFERENCES public.chargers(id) ON DELETE CASCADE,
    amenity TEXT NOT NULL
);

-- 5. Bookings Table (VoltShare & Reserved Public Stalls)
CREATE TABLE IF NOT EXISTS public.bookings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    charger_id UUID NOT NULL REFERENCES public.chargers(id) ON DELETE RESTRICT,
    renter_user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    booking_code TEXT UNIQUE NOT NULL,
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ NOT NULL,
    duration_minutes INT NOT NULL CHECK (duration_minutes > 0),
    energy_kwh NUMERIC(6, 2) NOT NULL,
    electricity_cost_inr NUMERIC(8, 2) NOT NULL,
    platform_fee_inr NUMERIC(8, 2) NOT NULL,
    total_cost_inr NUMERIC(8, 2) NOT NULL,
    status TEXT NOT NULL DEFAULT 'CONFIRMED' CHECK (status IN ('PENDING', 'CONFIRMED', 'ACTIVE', 'COMPLETED', 'CANCELLED')),
    stripe_payment_intent_id TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT no_overlap_booking EXCLUDE USING gist (
        charger_id WITH =,
        tstzrange(start_time, end_time) WITH &&
    )
);

-- 6. Trips Table
CREATE TABLE IF NOT EXISTS public.trips (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    vehicle_id UUID NOT NULL REFERENCES public.vehicles(id) ON DELETE CASCADE,
    origin_name TEXT NOT NULL,
    destination_name TEXT NOT NULL,
    distance_km NUMERIC(6, 2) NOT NULL,
    energy_consumed_kwh NUMERIC(6, 2) NOT NULL,
    average_efficiency_wh_km INT NOT NULL,
    eco_score INT CHECK (eco_score BETWEEN 0 AND 100),
    carbon_avoided_kg NUMERIC(6, 2) NOT NULL,
    estimated_cost_inr NUMERIC(8, 2) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 7. Reviews Table
CREATE TABLE IF NOT EXISTS public.reviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    booking_id UUID NOT NULL REFERENCES public.bookings(id) ON DELETE CASCADE,
    charger_id UUID NOT NULL REFERENCES public.chargers(id) ON DELETE CASCADE,
    reviewer_user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Enable Row Level Security (RLS) on all exposed tables
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.vehicles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.chargers ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.charger_amenities ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.bookings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.trips ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.reviews ENABLE ROW LEVEL SECURITY;

-- RLS Policies
CREATE POLICY "Public profiles are viewable by everyone" ON public.profiles FOR SELECT USING (true);
CREATE POLICY "Users can update their own profile" ON public.profiles FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Users manage their own vehicles" ON public.vehicles FOR ALL USING (auth.uid() = user_id);

CREATE POLICY "Chargers are viewable by authenticated users" ON public.chargers FOR SELECT USING (true);
CREATE POLICY "Hosts can insert and update their own chargers" ON public.chargers FOR ALL USING (auth.uid() = host_user_id);

CREATE POLICY "Amenities viewable by everyone" ON public.charger_amenities FOR SELECT USING (true);

CREATE POLICY "Renters can view and create their own bookings" ON public.bookings FOR ALL USING (auth.uid() = renter_user_id);
CREATE POLICY "Hosts can view bookings on their chargers" ON public.bookings FOR SELECT USING (
    EXISTS (SELECT 1 FROM public.chargers WHERE chargers.id = bookings.charger_id AND chargers.host_user_id = auth.uid())
);

CREATE POLICY "Users can manage their trips" ON public.trips FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Reviews viewable by everyone" ON public.reviews FOR SELECT USING (true);
CREATE POLICY "Reviews writable by verified booking renter" ON public.reviews FOR INSERT WITH CHECK (auth.uid() = reviewer_user_id);

-- ==============================================================================
-- SQL SEED DATA (Maya Sharma, Tesla Model 3, Bengaluru Chargers, Bookings)
-- ==============================================================================

-- 1. Insert Demo Profile: Maya Sharma
INSERT INTO public.profiles (id, email, full_name, city, total_carbon_avoided_kg, total_savings_inr, voltshare_earnings_inr, preferred_route_mode, safety_reserve_soc)
VALUES ('00000000-0000-0000-0000-000000000001', 'ab6048616@gmail.com', 'Maya Sharma', 'Bengaluru', 248.50, 4850.00, 2100.00, 'CHEAPEST', 15)
ON CONFLICT (id) DO NOTHING;

-- 2. Insert Host Profile: Ananya Rao
INSERT INTO public.profiles (id, email, full_name, city, total_carbon_avoided_kg, total_savings_inr, voltshare_earnings_inr, preferred_route_mode, safety_reserve_soc)
VALUES ('00000000-0000-0000-0000-000000000002', 'ananya.rao@example.com', 'Ananya Rao', 'Bengaluru', 180.00, 3200.00, 8400.00, 'CHEAPEST', 15)
ON CONFLICT (id) DO NOTHING;

-- 3. Insert Vehicle: Tesla Model 3
INSERT INTO public.vehicles (id, user_id, make, model, license_plate, battery_capacity_kwh, baseline_efficiency_wh_km, current_soc_percent, connector_type, vehicle_mass_kg)
VALUES (
    '10000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    'Tesla',
    'Model 3 Standard Range',
    'KA 05 EV 7291',
    60.00,
    145.00,
    32,
    'CCS2 / Type 2',
    1760.0
) ON CONFLICT (id) DO NOTHING;

-- 4. Insert Chargers (Ananya's Home Charger + ChargeGrid Hebbal + 8 others)
INSERT INTO public.chargers (id, host_user_id, name, type, connector_type, power_kw, price_per_kwh_inr, estimated_wait_minutes, carbon_intensity_gco2_kwh, availability_status, rating, review_count, latitude, longitude, address, is_host_verified, house_rules)
VALUES 
(
    '20000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000002',
    'Ananya''s Home Charger',
    'VOLTSHARE',
    'Type 2',
    7.2,
    10.00,
    0,
    410,
    'AVAILABLE',
    4.9,
    48,
    13.0382,
    77.5891,
    '14 Orchid Court, Bellary Rd, Hebbal, Bengaluru',
    true,
    'Please park within the left driveway bay. Cable is wall-mounted.'
),
(
    '20000000-0000-0000-0000-000000000002',
    NULL,
    'ChargeGrid Hebbal',
    'PUBLIC',
    'CCS2',
    60.0,
    17.00,
    18,
    620,
    'BUSY',
    4.2,
    132,
    13.0450,
    77.5925,
    'Shell Service Station, Bellary Road, Hebbal',
    false,
    NULL
),
(
    '20000000-0000-0000-0000-000000000003',
    NULL,
    'Zeon Fast Charger Yelahanka',
    'PUBLIC',
    'CCS2',
    50.0,
    18.50,
    5,
    610,
    'AVAILABLE',
    4.4,
    89,
    13.1020,
    77.5940,
    'NH 44 Highway Rest Area, Yelahanka',
    false,
    NULL
);

-- 5. Insert Confirmed Booking
INSERT INTO public.bookings (id, charger_id, renter_user_id, booking_code, start_time, end_time, duration_minutes, energy_kwh, electricity_cost_inr, platform_fee_inr, total_cost_inr, status)
VALUES (
    '30000000-0000-0000-0000-000000000001',
    '20000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    'VM-BLR-8492',
    NOW() + INTERVAL '2 hours',
    NOW() + INTERVAL '3 hours',
    60,
    7.2,
    72.00,
    12.00,
    84.00,
    'CONFIRMED'
) ON CONFLICT (id) DO NOTHING;
