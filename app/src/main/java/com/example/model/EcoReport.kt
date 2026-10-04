package com.example.model

data class EcoInsight(
    val title: String,
    val description: String,
    val impactText: String,
    val iconName: String = "bolt"
)

data class DriverBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val unlockedAt: String = "Active"
)

data class EcoReport(
    val score: Int = 87,
    val tripDistanceKm: Double = 96.0,
    val energyConsumedKwh: Double = 14.8,
    val efficiencyKwhPer100Km: Double = 15.4,
    val estimatedCostInr: Double = 148.0,
    val carbonAvoidedKg: Double = 18.4,
    val predictedConsumptionKwh: Double = 16.2,
    val actualConsumptionKwh: Double = 14.8,
    val insights: List<EcoInsight> = listOf(
        EcoInsight(
            title = "Smooth Acceleration",
            description = "Gentle throttle response on the NH44 incline saved ~1.2 kWh.",
            impactText = "+1.2 kWh Saved"
        ),
        EcoInsight(
            title = "Eco Climate Control",
            description = "Using pre-cooled cabin and 24°C eco mode extended range by 9 km.",
            impactText = "+9 km Range"
        ),
        EcoInsight(
            title = "Clean Energy Charging",
            description = "Your VoltShare booking used a solar-buffered home charger.",
            impactText = "-1.8 kg CO₂"
        )
    ),
    val badges: List<DriverBadge> = listOf(
        DriverBadge("1", "Smooth Operator", "Top 10% regenerative braking consistency", "⚡"),
        DriverBadge("2", "Green Navigator", "Opted for optimal low-carbon charging detour", "🌿"),
        DriverBadge("3", "Smart Charger", "Charged off-peak and avoided high-demand tariffs", "🔋")
    )
)

data class TripHistoryItem(
    val id: String,
    val title: String,
    val dateText: String,
    val distanceKm: Double,
    val energyKwh: Double,
    val efficiencyWhKm: Int,
    val carbonSavedKg: Double,
    val ecoScore: Int
)

data class UserProfile(
    val name: String = "Maya Sharma",
    val email: String = "maya.sharma@example.com",
    val city: String = "Bengaluru",
    val totalCarbonAvoidedKg: Double = 248.5,
    val totalSavingsInr: Double = 4850.0,
    val voltShareEarningsInr: Double = 2100.0,
    val safetyReservePercent: Int = 15,
    val preferredRouteMode: RoutePreference = RoutePreference.CHEAPEST,
    val highContrastMode: Boolean = false,
    val forceMapFallback: Boolean = false,
    val demoMode: Boolean = true
)
