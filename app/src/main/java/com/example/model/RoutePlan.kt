package com.example.model

enum class RoutePreference {
    CHEAPEST,
    FASTEST,
    GREENEST
}

data class RoutePlan(
    val origin: String = "Bengaluru City Center",
    val destination: String = "Nandi Hills",
    val distanceKm: Double = 96.0,
    val elevationGainMeters: Int = 620,
    val temperatureCelsius: Int = 31,
    val trafficCondition: String = "Moderate",
    val acOn: Boolean = true,
    val preference: RoutePreference = RoutePreference.CHEAPEST,
    val predictedArrivalSocPercent: Int = 8,
    val safetyReserveTargetPercent: Int = 15,
    val isChargeStopRequired: Boolean = true,
    val recommendedCharger: Charger,
    val alternativeChargers: List<Charger> = emptyList(),
    val totalEstimatedTimeMinutes: Int = 115,
    val estimatedArrivalSocWithStop: Int = 54,
    val totalCostInr: Double = 84.0,
    val whyExplanation: String = "Ananya's Home Charger is ~40% cheaper (₹10 vs ₹17/kWh), 0 min wait time, and 34% lower carbon intensity than public fast charging with only a 2.3 km detour."
)
