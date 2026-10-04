package com.example.engine

data class GeminiTripResponse(
    val summary: String,
    val recommendation: String,
    val predictedArrivalSoc: Int,
    val safetyReserveSoc: Int,
    val totalDistanceKm: Double,
    val estimatedTravelMinutes: Int,
    val chargeStops: List<GeminiChargeStop>,
    val scenicDetours: List<GeminiScenicDetour>,
    val tips: List<String>
)

data class GeminiChargeStop(
    val chargerId: String,
    val chargerName: String,
    val reason: String,
    val arrivalSoc: Int,
    val targetSoc: Int,
    val chargeMinutes: Int,
    val estimatedCost: Double,
    val carbonIntensity: Int,
    val amenities: List<String>
)

data class GeminiScenicDetour(
    val title: String,
    val description: String,
    val extraMinutes: Int
)

object GeminiTripAssistant {

    fun getSuggestedPrompts(): List<String> = listOf(
        "Why is Ananya's charger best for Nandi Hills?",
        "Can I reach Nandi Hills without stopping if I turn off AC?",
        "Compare VoltShare vs Public fast charging costs",
        "What are the best coffee spots near Hebbal chargers?"
    )

    fun answerQuery(query: String): GeminiTripResponse {
        val lower = query.lowercase()
        return when {
            lower.contains("without stopping") || lower.contains("turn off ac") -> {
                GeminiTripResponse(
                    summary = "Turning off AC saves ~1.9 kWh (+12 km), arriving at ~11% SoC. However, this is still 4% below your 15% safety reserve.",
                    recommendation = "Do not skip charging. Even with HVAC disabled, the +620m elevation climb to Nandi Hills carries high risk if traffic stalls.",
                    predictedArrivalSoc = 11,
                    safetyReserveSoc = 15,
                    totalDistanceKm = 96.0,
                    estimatedTravelMinutes = 110,
                    chargeStops = listOf(
                        GeminiChargeStop(
                            chargerId = "vs-ananya-hebbal",
                            chargerName = "Ananya's Home Charger",
                            reason = "A 25-minute top-up gives +18% buffer safely.",
                            arrivalSoc = 22,
                            targetSoc = 40,
                            chargeMinutes = 25,
                            estimatedCost = 35.0,
                            carbonIntensity = 410,
                            amenities = listOf("Driveway shade", "Wi-Fi")
                        )
                    ),
                    scenicDetours = listOf(),
                    tips = listOf(
                        "Set regenerative braking to Maximum for downhill returns",
                        "Drive under 75 km/h on NH44 to conserve battery"
                    )
                )
            }
            lower.contains("compare") || lower.contains("vs") || lower.contains("cost") -> {
                GeminiTripResponse(
                    summary = "VoltShare saves ~42% compared to Commercial DC Fast charging on this corridor.",
                    recommendation = "Choose Ananya's Home Charger at ₹10/kWh vs ChargeGrid Hebbal at ₹17/kWh. You save ₹50-₹90 per session with zero queue delay.",
                    predictedArrivalSoc = 8,
                    safetyReserveSoc = 15,
                    totalDistanceKm = 96.0,
                    estimatedTravelMinutes = 115,
                    chargeStops = listOf(
                        GeminiChargeStop(
                            chargerId = "vs-ananya-hebbal",
                            chargerName = "Ananya's Home Charger",
                            reason = "₹10/kWh flat tariff, 0 min queue wait, solar-buffered",
                            arrivalSoc = 22,
                            targetSoc = 52,
                            chargeMinutes = 45,
                            estimatedCost = 84.0,
                            carbonIntensity = 410,
                            amenities = listOf("Gated parking", "Washroom", "Wi-Fi")
                        )
                    ),
                    scenicDetours = listOf(
                        GeminiScenicDetour("Chikka Jala Fort", "Historic stone fort ruin along NH44", 10)
                    ),
                    tips = listOf(
                        "VoltShare hosts offer domestic charging rates under local green-energy tariffs",
                        "Public DC fast chargers charge higher peak demand surcharges"
                    )
                )
            }
            else -> {
                // Default Nandi Hills recommendation
                GeminiTripResponse(
                    summary = "Your 96 km trip to Nandi Hills with +620m elevation requires 1 charge stop to protect your 15% reserve buffer.",
                    recommendation = "Reserve Ananya's Home Charger in Hebbal. It provides a relaxed, 40% cheaper charge stop with zero wait time right before the highway climb.",
                    predictedArrivalSoc = 8,
                    safetyReserveSoc = 15,
                    totalDistanceKm = 96.0,
                    estimatedTravelMinutes = 115,
                    chargeStops = listOf(
                        GeminiChargeStop(
                            chargerId = "vs-ananya-hebbal",
                            chargerName = "Ananya's Home Charger (VoltShare)",
                            reason = "Optimal geographic placement before NH44 elevation gain. Verified host with 4.9 rating.",
                            arrivalSoc = 24,
                            targetSoc = 52,
                            chargeMinutes = 45,
                            estimatedCost = 84.0,
                            carbonIntensity = 410,
                            amenities = listOf("Gated parking", "Washroom", "Drinking water", "Wi-Fi")
                        )
                    ),
                    scenicDetours = listOf(
                        GeminiScenicDetour("Devanahalli Fort", "Birthplace of Tipu Sultan, 4 km off NH44", 15)
                    ),
                    tips = listOf(
                        "Pre-cool the vehicle while plugged into Ananya's charger to save battery on the ascent",
                        "Nandi Hills summit parking gets crowded after 4 PM; early arrival recommended",
                        "Descent from Nandi Hills will regenerate ~2.5 kWh back into your battery"
                    )
                )
            }
        }
    }
}
