package com.example.data

import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.model.Charger
import com.example.model.ChargerAvailability
import com.example.model.ChargerType
import com.example.model.EcoReport
import com.example.model.RoutePlan
import com.example.model.RoutePreference
import com.example.model.TripHistoryItem
import com.example.model.UserProfile
import com.example.model.Vehicle

object SeedData {

    val demoVehicle = Vehicle(
        id = "veh-tesla-3",
        make = "Tesla",
        model = "Model 3 Standard Range",
        batteryCapacityKwh = 60.0,
        currentSocPercent = 32,
        baselineEfficiencyWhPerKm = 145.0,
        connectorType = "CCS2 / Type 2",
        vehicleMassKg = 1760.0,
        licensePlate = "KA 05 EV 7291"
    )

    val altVehicles = listOf(
        demoVehicle,
        Vehicle(
            id = "veh-tata-nexon",
            make = "Tata",
            model = "Nexon EV Long Range",
            batteryCapacityKwh = 40.5,
            currentSocPercent = 45,
            baselineEfficiencyWhPerKm = 158.0,
            connectorType = "CCS2",
            vehicleMassKg = 1400.0,
            licensePlate = "KA 01 EV 1024"
        ),
        Vehicle(
            id = "veh-hyundai-kona",
            make = "Hyundai",
            model = "Kona Electric",
            batteryCapacityKwh = 39.2,
            currentSocPercent = 60,
            baselineEfficiencyWhPerKm = 140.0,
            connectorType = "CCS2",
            vehicleMassKg = 1535.0,
            licensePlate = "KA 03 EV 4590"
        )
    )

    // Recommended VoltShare charger for demo
    val ananyaCharger = Charger(
        id = "vs-ananya-hebbal",
        name = "Ananya's Home Charger",
        hostName = "Ananya Rao",
        type = ChargerType.VOLTSHARE,
        connectorType = "Type 2",
        powerKw = 7.2,
        pricePerKwh = 10.0,
        estimatedWaitMinutes = 0,
        carbonIntensityGCo2PerKwh = 410,
        availability = ChargerAvailability.AVAILABLE,
        rating = 4.9,
        reviewCount = 48,
        amenities = listOf("Gated parking", "Washroom", "Drinking water", "Wi-Fi", "CCTV"),
        detourKm = 2.3,
        latitude = 13.0382,
        longitude = 77.5891,
        address = "14 Orchid Court, Bellary Rd, Hebbal, Bengaluru",
        isHostVerified = true,
        hostAvatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=200&q=80",
        houseRules = "Please park carefully inside the left bay. Charging cable is on the wall mount."
    )

    // Alternative Public Charger
    val chargeGridHebbal = Charger(
        id = "pub-chargegrid-hebbal",
        name = "ChargeGrid Hebbal",
        hostName = "Tata Power & Shell",
        type = ChargerType.PUBLIC,
        connectorType = "CCS2",
        powerKw = 60.0,
        pricePerKwh = 17.0,
        estimatedWaitMinutes = 18,
        carbonIntensityGCo2PerKwh = 620,
        availability = ChargerAvailability.BUSY,
        rating = 4.2,
        reviewCount = 132,
        amenities = listOf("Cafe", "Restroom", "Wi-Fi", "Convenience Store"),
        detourKm = 1.1,
        latitude = 13.0450,
        longitude = 77.5925,
        address = "Shell Service Station, Bellary Road, Hebbal",
        isHostVerified = false
    )

    // 2 more VoltShare chargers
    val rohitCharger = Charger(
        id = "vs-rohit-sahakar",
        name = "Rohit's Solar Charger",
        hostName = "Rohit Varma",
        type = ChargerType.VOLTSHARE,
        connectorType = "Type 2",
        powerKw = 11.0,
        pricePerKwh = 11.5,
        estimatedWaitMinutes = 0,
        carbonIntensityGCo2PerKwh = 380,
        availability = ChargerAvailability.AVAILABLE,
        rating = 4.8,
        reviewCount = 29,
        amenities = listOf("Solar Powered", "Shaded Carport", "High Speed Wi-Fi"),
        detourKm = 3.8,
        latitude = 13.0620,
        longitude = 77.5850,
        address = "88 Green View, Sahakara Nagar, Bengaluru",
        isHostVerified = true
    )

    val priyaCharger = Charger(
        id = "vs-priya-yelahanka",
        name = "Priya's Fast Type-2 Hub",
        hostName = "Priya Kulkarni",
        type = ChargerType.VOLTSHARE,
        connectorType = "Type 2",
        powerKw = 7.2,
        pricePerKwh = 9.5,
        estimatedWaitMinutes = 0,
        carbonIntensityGCo2PerKwh = 425,
        availability = ChargerAvailability.AVAILABLE,
        rating = 5.0,
        reviewCount = 19,
        amenities = listOf("Gated Community", "Water Filter", "Pet Friendly"),
        detourKm = 4.5,
        latitude = 13.0990,
        longitude = 77.5960,
        address = "A-204 Provident Harmony, Yelahanka, Bengaluru",
        isHostVerified = true
    )

    // 9 more Public Chargers across Bangalore
    val publicChargers = listOf(
        chargeGridHebbal,
        Charger(
            id = "pub-zeon-airport",
            name = "Zeon Fast Charger Yelahanka",
            type = ChargerType.PUBLIC,
            connectorType = "CCS2",
            powerKw = 50.0,
            pricePerKwh = 18.5,
            estimatedWaitMinutes = 5,
            carbonIntensityGCo2PerKwh = 610,
            availability = ChargerAvailability.AVAILABLE,
            rating = 4.4,
            reviewCount = 89,
            amenities = listOf("Coffee Day", "Restrooms", "Free Air"),
            detourKm = 4.2,
            latitude = 13.1020,
            longitude = 77.5940,
            address = "NH 44 Highway Rest Area, Yelahanka"
        ),
        Charger(
            id = "pub-jio-bp-deva",
            name = "Jio-bp Pulse Devanahalli",
            type = ChargerType.PUBLIC,
            connectorType = "CCS2",
            powerKw = 120.0,
            pricePerKwh = 21.0,
            estimatedWaitMinutes = 0,
            carbonIntensityGCo2PerKwh = 640,
            availability = ChargerAvailability.AVAILABLE,
            rating = 4.6,
            reviewCount = 210,
            amenities = listOf("Wild Bean Cafe", "Fast Food", "Clean Washrooms"),
            detourKm = 8.1,
            latitude = 13.2450,
            longitude = 77.7120,
            address = "Near Toll Plaza, Devanahalli, Bengaluru"
        ),
        Charger(
            id = "pub-ather-indira",
            name = "Ather Grid Indiranagar",
            type = ChargerType.PUBLIC,
            connectorType = "Type 2",
            powerKw = 3.3,
            pricePerKwh = 12.0,
            estimatedWaitMinutes = 25,
            carbonIntensityGCo2PerKwh = 590,
            availability = ChargerAvailability.BUSY,
            rating = 4.1,
            reviewCount = 65,
            amenities = listOf("Cafe", "Shopping"),
            detourKm = 6.4,
            latitude = 12.9784,
            longitude = 77.6408,
            address = "100ft Road, Indiranagar, Bengaluru"
        ),
        Charger(
            id = "pub-tata-koramangala",
            name = "Tata Power EZ Charge Kormangala",
            type = ChargerType.PUBLIC,
            connectorType = "CCS2",
            powerKw = 30.0,
            pricePerKwh = 16.0,
            estimatedWaitMinutes = 35,
            carbonIntensityGCo2PerKwh = 630,
            availability = ChargerAvailability.BUSY,
            rating = 3.9,
            reviewCount = 140,
            amenities = listOf("Forum Mall", "Food Court"),
            detourKm = 9.2,
            latitude = 12.9352,
            longitude = 77.6245,
            address = "Hosur Rd, Koramangala, Bengaluru"
        ),
        Charger(
            id = "pub-statiq-mgroad",
            name = "Statiq EV Station MG Road",
            type = ChargerType.PUBLIC,
            connectorType = "CCS2",
            powerKw = 50.0,
            pricePerKwh = 17.5,
            estimatedWaitMinutes = 12,
            carbonIntensityGCo2PerKwh = 615,
            availability = ChargerAvailability.AVAILABLE,
            rating = 4.3,
            reviewCount = 98,
            amenities = listOf("Metro Station", "ATM", "Coffee"),
            detourKm = 1.8,
            latitude = 12.9756,
            longitude = 77.6066,
            address = "Trinity Circle, MG Road, Bengaluru"
        ),
        Charger(
            id = "pub-relux-whitefield",
            name = "Relux Electric Whitefield",
            type = ChargerType.PUBLIC,
            connectorType = "CCS2",
            powerKw = 60.0,
            pricePerKwh = 18.0,
            estimatedWaitMinutes = 0,
            carbonIntensityGCo2PerKwh = 625,
            availability = ChargerAvailability.AVAILABLE,
            rating = 4.5,
            reviewCount = 77,
            amenities = listOf("Mall Parking", "Restroom"),
            detourKm = 14.0,
            latitude = 12.9698,
            longitude = 77.7499,
            address = "ITPL Main Rd, Whitefield"
        ),
        Charger(
            id = "pub-electree-mallesh",
            name = "ElectreeFi Hub Malleshwaram",
            type = ChargerType.PUBLIC,
            connectorType = "Type 2",
            powerKw = 7.4,
            pricePerKwh = 11.0,
            estimatedWaitMinutes = 0,
            carbonIntensityGCo2PerKwh = 580,
            availability = ChargerAvailability.AVAILABLE,
            rating = 4.7,
            reviewCount = 42,
            amenities = listOf("Market", "Temples", "Snack stall"),
            detourKm = 3.1,
            latitude = 13.0031,
            longitude = 77.5684,
            address = "8th Cross, Sampige Rd, Malleshwaram"
        ),
        Charger(
            id = "pub-bescom-vidhana",
            name = "BESCOM Fast Charge Secretariat",
            type = ChargerType.PUBLIC,
            connectorType = "CCS2",
            powerKw = 25.0,
            pricePerKwh = 14.5,
            estimatedWaitMinutes = 45,
            carbonIntensityGCo2PerKwh = 650,
            availability = ChargerAvailability.UNAVAILABLE,
            rating = 3.6,
            reviewCount = 18,
            amenities = listOf("Government Office"),
            detourKm = 0.5,
            latitude = 12.9791,
            longitude = 77.5913,
            address = "Near Vidhana Soudha, Bengaluru"
        ),
        Charger(
            id = "pub-bolt-rtnagar",
            name = "Bolt.Earth Fast Hub RT Nagar",
            type = ChargerType.PUBLIC,
            connectorType = "Type 2",
            powerKw = 7.2,
            pricePerKwh = 10.5,
            estimatedWaitMinutes = 0,
            carbonIntensityGCo2PerKwh = 560,
            availability = ChargerAvailability.AVAILABLE,
            rating = 4.6,
            reviewCount = 33,
            amenities = listOf("Bakery", "Pharmacies"),
            detourKm = 2.0,
            latitude = 13.0245,
            longitude = 77.5950,
            address = "Main Rd, RT Nagar, Bengaluru"
        )
    )

    val allChargers: List<Charger> = listOf(
        ananyaCharger,
        rohitCharger,
        priyaCharger
    ) + publicChargers

    val voltShareListings: List<Charger> = listOf(
        ananyaCharger,
        rohitCharger,
        priyaCharger
    )

    // Route Plans for demo
    val cheapestRoutePlan = RoutePlan(
        origin = "Bengaluru City Center",
        destination = "Nandi Hills",
        distanceKm = 96.0,
        elevationGainMeters = 620,
        temperatureCelsius = 31,
        trafficCondition = "Moderate",
        acOn = true,
        preference = RoutePreference.CHEAPEST,
        predictedArrivalSocPercent = 8,
        safetyReserveTargetPercent = 15,
        isChargeStopRequired = true,
        recommendedCharger = ananyaCharger,
        alternativeChargers = listOf(chargeGridHebbal, rohitCharger),
        totalEstimatedTimeMinutes = 115,
        estimatedArrivalSocWithStop = 52,
        totalCostInr = 84.0,
        whyExplanation = "Recommended: Ananya's Home Charger (VoltShare) is ~40% cheaper than public DC fast charging (₹10 vs ₹17/kWh). Zero queue delay, gated residential comfort, and 34% lower carbon intensity."
    )

    val fastestRoutePlan = RoutePlan(
        origin = "Bengaluru City Center",
        destination = "Nandi Hills",
        distanceKm = 96.0,
        elevationGainMeters = 620,
        temperatureCelsius = 31,
        trafficCondition = "Moderate",
        acOn = true,
        preference = RoutePreference.FASTEST,
        predictedArrivalSocPercent = 8,
        safetyReserveTargetPercent = 15,
        isChargeStopRequired = true,
        recommendedCharger = chargeGridHebbal,
        alternativeChargers = listOf(ananyaCharger, priyaCharger),
        totalEstimatedTimeMinutes = 98,
        estimatedArrivalSocWithStop = 65,
        totalCostInr = 210.0,
        whyExplanation = "Fastest Route: 60 kW DC charging at ChargeGrid Hebbal injects 18 kWh in 18 minutes. Minimal detour (1.1 km), though higher tariff (₹17/kWh)."
    )

    val greenestRoutePlan = RoutePlan(
        origin = "Bengaluru City Center",
        destination = "Nandi Hills",
        distanceKm = 96.0,
        elevationGainMeters = 620,
        temperatureCelsius = 31,
        trafficCondition = "Moderate",
        acOn = true,
        preference = RoutePreference.GREENEST,
        predictedArrivalSocPercent = 8,
        safetyReserveTargetPercent = 15,
        isChargeStopRequired = true,
        recommendedCharger = rohitCharger,
        alternativeChargers = listOf(ananyaCharger, chargeGridHebbal),
        totalEstimatedTimeMinutes = 110,
        estimatedArrivalSocWithStop = 58,
        totalCostInr = 95.0,
        whyExplanation = "Greenest Route: Rohit's Solar Charger has the lowest grid carbon intensity (380 gCO2/kWh) with 100% rooftop solar buffering during daytime charging."
    )

    val initialBooking = Booking(
        id = "bk-ananya-8492",
        chargerId = ananyaCharger.id,
        chargerName = ananyaCharger.name,
        hostName = ananyaCharger.hostName,
        dateText = "Today",
        timeSlotText = "2:30 PM - 3:30 PM",
        durationMinutes = 60,
        energyKwh = 7.2,
        electricityCostInr = 72.0,
        platformFeeInr = 12.0,
        totalCostInr = 84.0,
        status = BookingStatus.CONFIRMED,
        bookingCode = "VM-BLR-8492",
        address = ananyaCharger.address,
        connectorType = "Type 2 (7.2 kW)"
    )

    val pastTrips = listOf(
        TripHistoryItem(
            id = "trip-1",
            title = "Bengaluru to Nandi Hills",
            dateText = "Yesterday, 3:45 PM",
            distanceKm = 96.0,
            energyKwh = 14.8,
            efficiencyWhKm = 154,
            carbonSavedKg = 18.4,
            ecoScore = 87
        ),
        TripHistoryItem(
            id = "trip-2",
            title = "MG Road to Electronic City",
            dateText = "2 Oct 2026, 9:15 AM",
            distanceKm = 24.2,
            energyKwh = 3.4,
            efficiencyWhKm = 140,
            carbonSavedKg = 4.8,
            ecoScore = 92
        ),
        TripHistoryItem(
            id = "trip-3",
            title = "Indiranagar to Kempegowda Airport",
            dateText = "28 Sep 2026, 6:00 AM",
            distanceKm = 41.0,
            energyKwh = 5.9,
            efficiencyWhKm = 144,
            carbonSavedKg = 7.9,
            ecoScore = 89
        )
    )

    val defaultEcoReport = EcoReport()
    val defaultUserProfile = UserProfile()
}
