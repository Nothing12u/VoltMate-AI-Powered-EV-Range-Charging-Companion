package com.example.engine

import com.example.model.Charger
import com.example.model.ChargerAvailability
import com.example.model.ChargerType
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class GeoLocation(
    val name: String,
    val lat: Double,
    val lng: Double,
    val elevationM: Int
)

data class CalculatedRouteInfo(
    val originName: String,
    val destName: String,
    val distanceKm: Double,
    val elevationGainMeters: Int,
    val travelTimeMinutes: Int,
    val isLongDistanceHighway: Boolean,
    val totalChargeStopsNeeded: Int,
    val recommendedCharger: Charger,
    val routeExplanation: String
)

object CityGeoLocator {

    private val cityDatabase = mapOf(
        "ongole" to GeoLocation("Ongole", 15.5057, 80.0499, 10),
        "delhi" to GeoLocation("Delhi", 28.6139, 77.2090, 216),
        "new delhi" to GeoLocation("New Delhi", 28.6139, 77.2090, 216),
        "ncr" to GeoLocation("Delhi NCR", 28.6139, 77.2090, 216),
        "bengaluru" to GeoLocation("Bengaluru", 12.9716, 77.5946, 920),
        "bangalore" to GeoLocation("Bengaluru", 12.9716, 77.5946, 920),
        "nandi hills" to GeoLocation("Nandi Hills", 13.3702, 77.6835, 1478),
        "nandi" to GeoLocation("Nandi Hills", 13.3702, 77.6835, 1478),
        "chennai" to GeoLocation("Chennai", 13.0827, 80.2707, 6),
        "hyderabad" to GeoLocation("Hyderabad", 17.3850, 78.4867, 542),
        "mumbai" to GeoLocation("Mumbai", 19.0760, 72.8777, 14),
        "pune" to GeoLocation("Pune", 18.5204, 73.8567, 560),
        "kolkata" to GeoLocation("Kolkata", 22.5726, 88.3639, 9),
        "vijayawada" to GeoLocation("Vijayawada", 16.5062, 80.6480, 23),
        "guntur" to GeoLocation("Guntur", 16.3067, 80.4365, 33),
        "visakhapatnam" to GeoLocation("Visakhapatnam", 17.6868, 83.2185, 45),
        "vizag" to GeoLocation("Visakhapatnam", 17.6868, 83.2185, 45),
        "tirupati" to GeoLocation("Tirupati", 13.6288, 79.4192, 162),
        "nellore" to GeoLocation("Nellore", 14.4426, 79.9865, 19),
        "kurnool" to GeoLocation("Kurnool", 15.8281, 78.0373, 273),
        "anantapur" to GeoLocation("Anantapur", 14.6819, 77.6006, 335),
        "kempegowda airport" to GeoLocation("Kempegowda International Airport", 13.1986, 77.7066, 915),
        "airport" to GeoLocation("Kempegowda Airport", 13.1986, 77.7066, 915),
        "mysuru" to GeoLocation("Mysuru", 12.2958, 76.6394, 763),
        "mysore" to GeoLocation("Mysuru", 12.2958, 76.6394, 763),
        "electronic city" to GeoLocation("Electronic City", 12.8399, 77.6770, 910),
        "whitefield" to GeoLocation("Whitefield ITPL", 12.9698, 77.7499, 905),
        "indiranagar" to GeoLocation("Indiranagar", 12.9784, 77.6408, 918),
        "koramangala" to GeoLocation("Koramangala", 12.9352, 77.6245, 912),
        "jaipur" to GeoLocation("Jaipur", 26.9124, 75.7873, 431),
        "ahmedabad" to GeoLocation("Ahmedabad", 23.0225, 72.5714, 53),
        "kochi" to GeoLocation("Kochi", 9.9312, 76.2673, 4),
        "coimbatore" to GeoLocation("Coimbatore", 11.0168, 76.9558, 411),
        "goa" to GeoLocation("Goa", 15.2993, 74.1240, 10),
        "nagpur" to GeoLocation("Nagpur", 21.1458, 79.0882, 310),
        "bhopal" to GeoLocation("Bhopal", 23.2599, 77.4126, 527),
        "agra" to GeoLocation("Agra", 27.1767, 78.0081, 169),
        "lucknow" to GeoLocation("Lucknow", 26.8467, 80.9462, 123),
        "chandigarh" to GeoLocation("Chandigarh", 30.7333, 76.7794, 321)
    )

    private fun findCity(query: String): GeoLocation? {
        val clean = query.trim().lowercase()
        // Exact match or substring
        for ((key, loc) in cityDatabase) {
            if (clean.contains(key) || key.contains(clean)) {
                return loc
            }
        }
        return null
    }

    private fun haversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c * 1.24 // Highway road curve coefficient
    }

    fun calculateRoute(
        originQuery: String,
        destQuery: String,
        preference: String = "CHEAPEST"
    ): CalculatedRouteInfo {
        val originLoc = findCity(originQuery)
        val destLoc = findCity(destQuery)

        val distanceKm: Double
        val elevationGainMeters: Int
        val travelTimeMinutes: Int

        if (originLoc != null && destLoc != null) {
            val rawDist = haversineDistanceKm(originLoc.lat, originLoc.lng, destLoc.lat, destLoc.lng)
            distanceKm = if (rawDist < 5.0) 25.0 else rawDist
            elevationGainMeters = (destLoc.elevationM - originLoc.elevationM).coerceAtLeast(0)
            // Highway average 70 km/h + 10% traffic
            val drivingMinutes = ((distanceKm / 72.0) * 60).toInt()
            travelTimeMinutes = drivingMinutes
        } else {
            // Sensible fallback based on string length or keywords
            val lower = (originQuery + destQuery).lowercase()
            distanceKm = when {
                lower.contains("delhi") && lower.contains("ongole") -> 1850.0
                lower.contains("delhi") || lower.contains("mumbai") -> 1200.0
                lower.contains("hyderabad") -> 570.0
                lower.contains("airport") -> 42.0
                lower.contains("mysuru") || lower.contains("mysore") -> 145.0
                else -> 85.0
            }
            elevationGainMeters = if (lower.contains("nandi")) 620 else 80
            travelTimeMinutes = ((distanceKm / 65.0) * 60).toInt() + 15
        }

        val isLongDistance = distanceKm > 150.0
        // EV battery 60 kWh with 32% start gives ~104 km.
        // After that, each full charge gives ~220 km highway range.
        val totalChargeStopsNeeded = if (distanceKm <= 75.0) {
            0
        } else if (distanceKm <= 160.0) {
            1
        } else {
            1 + ((distanceKm - 104.0) / 220.0).toInt().coerceAtLeast(1)
        }

        // Generate tailored recommended charger for this specific route
        val recommendedCharger = when {
            distanceKm > 500.0 -> {
                // National Highway Supercharger corridor (e.g. Ongole to Delhi)
                Charger(
                    id = "nh-pulse-corridor",
                    name = "Jio-bp Pulse Highway Supercharger (NH44 Corridor)",
                    hostName = "National Highway EV Express Hub",
                    type = ChargerType.PUBLIC,
                    connectorType = "CCS2 (120 kW Dual)",
                    powerKw = 120.0,
                    pricePerKwh = 18.5,
                    estimatedWaitMinutes = 0,
                    carbonIntensityGCo2PerKwh = 520,
                    availability = ChargerAvailability.AVAILABLE,
                    rating = 4.8,
                    reviewCount = 312,
                    amenities = listOf("24/7 Food Plaza", "Clean Restrooms", "Security Guard", "Wi-Fi"),
                    detourKm = 0.5,
                    latitude = 16.5000,
                    longitude = 80.5000,
                    address = "NH16 / NH44 Interchange Plaza, Guntur Bypass",
                    isHostVerified = true
                )
            }
            distanceKm > 120.0 -> {
                // Regional Highway Fast Charger
                Charger(
                    id = "reg-highway-fast",
                    name = "Tata Power EZ Supercharge (Highway Hub)",
                    hostName = "Tata Power & BPCL",
                    type = ChargerType.PUBLIC,
                    connectorType = "CCS2 (60 kW)",
                    powerKw = 60.0,
                    pricePerKwh = 16.5,
                    estimatedWaitMinutes = 5,
                    carbonIntensityGCo2PerKwh = 580,
                    availability = ChargerAvailability.AVAILABLE,
                    rating = 4.5,
                    reviewCount = 175,
                    amenities = listOf("Cafe Coffee Day", "Restrooms", "Free Tyre Air"),
                    detourKm = 1.0,
                    latitude = 14.5000,
                    longitude = 78.5000,
                    address = "BPCL Highway Oasis, NH44 Corridor",
                    isHostVerified = true
                )
            }
            else -> {
                // Local VoltShare or City charger (Default Bangalore / Nandi Hills)
                com.example.data.SeedData.ananyaCharger
            }
        }

        val explanation = if (isLongDistance) {
            "Long-Distance Intercity Route (${distanceKm.toInt()} km): VoltMate scheduled $totalChargeStopsNeeded strategic 60kW–120kW fast DC stops spaced every ~210 km along the national highway corridor to ensure your battery stays above the 15% safety buffer."
        } else {
            "Ananya's Home Charger (VoltShare) is ~40% cheaper (₹10 vs ₹17/kWh), with 0 min wait time and 34% lower carbon intensity than public fast chargers."
        }

        return CalculatedRouteInfo(
            originName = originLoc?.name ?: originQuery.ifBlank { "Starting Location" },
            destName = destLoc?.name ?: destQuery.ifBlank { "Destination" },
            distanceKm = distanceKm,
            elevationGainMeters = elevationGainMeters,
            travelTimeMinutes = travelTimeMinutes,
            isLongDistanceHighway = isLongDistance,
            totalChargeStopsNeeded = totalChargeStopsNeeded,
            recommendedCharger = recommendedCharger,
            routeExplanation = explanation
        )
    }
}
