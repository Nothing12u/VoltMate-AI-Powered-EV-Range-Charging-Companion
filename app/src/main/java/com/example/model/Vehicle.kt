package com.example.model

data class Vehicle(
    val id: String,
    val make: String,
    val model: String,
    val batteryCapacityKwh: Double,
    val currentSocPercent: Int,
    val baselineEfficiencyWhPerKm: Double,
    val connectorType: String,
    val vehicleMassKg: Double = 1800.0,
    val licensePlate: String = "KA 01 EV 8492"
) {
    val usableEnergyKwh: Double
        get() = (currentSocPercent / 100.0) * batteryCapacityKwh
}
