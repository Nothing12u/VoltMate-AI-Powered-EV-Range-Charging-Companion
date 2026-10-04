package com.example.model

enum class BookingStatus {
    CONFIRMED,
    ACTIVE,
    COMPLETED,
    CANCELLED
}

data class Booking(
    val id: String,
    val chargerId: String,
    val chargerName: String,
    val hostName: String,
    val dateText: String = "Today",
    val timeSlotText: String = "2:30 PM - 3:30 PM",
    val durationMinutes: Int = 60,
    val energyKwh: Double = 7.2,
    val electricityCostInr: Double = 72.0,
    val platformFeeInr: Double = 12.0,
    val totalCostInr: Double = 84.0,
    val status: BookingStatus = BookingStatus.CONFIRMED,
    val bookingCode: String = "VM-BLR-8492",
    val address: String = "Villa 14, Hebbal Orchards, Bengaluru",
    val connectorType: String = "Type 2 (7.2 kW)",
    val cancellationPolicy: String = "Free cancellation up to 15 minutes before scheduled session."
)
