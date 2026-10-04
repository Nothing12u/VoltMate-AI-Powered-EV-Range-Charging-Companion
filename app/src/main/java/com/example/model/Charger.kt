package com.example.model

enum class ChargerType {
    PUBLIC,
    VOLTSHARE
}

enum class ChargerAvailability {
    AVAILABLE,
    BUSY,
    UNAVAILABLE
}

data class Charger(
    val id: String,
    val name: String,
    val hostName: String = "",
    val type: ChargerType,
    val connectorType: String, // e.g. "CCS2", "Type 2"
    val powerKw: Double,
    val pricePerKwh: Double,
    val estimatedWaitMinutes: Int = 0,
    val carbonIntensityGCo2PerKwh: Int = 500,
    val availability: ChargerAvailability = ChargerAvailability.AVAILABLE,
    val rating: Double = 4.8,
    val reviewCount: Int = 24,
    val amenities: List<String> = emptyList(),
    val detourKm: Double = 0.0,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val isHostVerified: Boolean = false,
    val hostAvatarUrl: String = "",
    val houseRules: String = "Please park within the driveway and avoid revving or loud music after 9 PM."
)
