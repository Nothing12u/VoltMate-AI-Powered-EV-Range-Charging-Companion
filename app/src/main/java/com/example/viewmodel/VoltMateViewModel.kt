package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SeedData
import com.example.engine.CityGeoLocator
import com.example.engine.GeminiTripAssistant
import com.example.engine.GeminiTripResponse
import com.example.engine.RangePredictionResult
import com.example.engine.RangePredictor
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class VoltMateViewModel : ViewModel() {

    // User & Preferences
    private val _userProfile = MutableStateFlow(SeedData.defaultUserProfile)
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(true)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    // Vehicle
    private val _currentVehicle = MutableStateFlow(SeedData.demoVehicle)
    val currentVehicle: StateFlow<Vehicle> = _currentVehicle.asStateFlow()

    // Route & Range Prediction
    private val _routePlan = MutableStateFlow(SeedData.cheapestRoutePlan)
    val routePlan: StateFlow<RoutePlan> = _routePlan.asStateFlow()

    private val _rangePrediction = MutableStateFlow(
        RangePredictor.predictRange(
            vehicle = SeedData.demoVehicle,
            tripDistanceKm = 96.0,
            elevationGainMeters = 620.0,
            ambientTempCelsius = 31.0,
            isAcOn = true,
            safetyReservePercent = 15
        )
    )
    val rangePrediction: StateFlow<RangePredictionResult> = _rangePrediction.asStateFlow()

    // Chargers & Map
    private val _chargers = MutableStateFlow(SeedData.allChargers)
    val chargers: StateFlow<List<Charger>> = _chargers.asStateFlow()

    private val _selectedCharger = MutableStateFlow<Charger?>(SeedData.ananyaCharger)
    val selectedCharger: StateFlow<Charger?> = _selectedCharger.asStateFlow()

    // VoltShare & Bookings
    private val _voltShareListings = MutableStateFlow(SeedData.voltShareListings)
    val voltShareListings: StateFlow<List<Charger>> = _voltShareListings.asStateFlow()

    private val _bookings = MutableStateFlow(listOf(SeedData.initialBooking))
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _latestConfirmedBooking = MutableStateFlow<Booking?>(SeedData.initialBooking)
    val latestConfirmedBooking: StateFlow<Booking?> = _latestConfirmedBooking.asStateFlow()

    // Eco-Coach
    private val _ecoReport = MutableStateFlow(SeedData.defaultEcoReport)
    val ecoReport: StateFlow<EcoReport> = _ecoReport.asStateFlow()

    private val _tripHistory = MutableStateFlow(SeedData.pastTrips)
    val tripHistory: StateFlow<List<TripHistoryItem>> = _tripHistory.asStateFlow()

    // Gemini Trip Assistant
    private val _isGeminiLoading = MutableStateFlow(false)
    val isGeminiLoading: StateFlow<Boolean> = _isGeminiLoading.asStateFlow()

    private val _geminiResponse = MutableStateFlow<GeminiTripResponse?>(null)
    val geminiResponse: StateFlow<GeminiTripResponse?> = _geminiResponse.asStateFlow()

    fun selectVehicle(vehicle: Vehicle) {
        _currentVehicle.value = vehicle
        recalculateRange()
    }

    fun setRoutePreference(pref: RoutePreference) {
        _userProfile.value = _userProfile.value.copy(preferredRouteMode = pref)
        _routePlan.value = when (pref) {
            RoutePreference.CHEAPEST -> SeedData.cheapestRoutePlan
            RoutePreference.FASTEST -> SeedData.fastestRoutePlan
            RoutePreference.GREENEST -> SeedData.greenestRoutePlan
        }
        recalculateRange()
    }

    fun setSelectedCharger(charger: Charger?) {
        _selectedCharger.value = charger
    }

    fun completeOnboarding(vehicle: Vehicle, homeCity: String, pref: RoutePreference, reserve: Int) {
        _currentVehicle.value = vehicle
        _userProfile.value = _userProfile.value.copy(
            city = homeCity,
            preferredRouteMode = pref,
            safetyReservePercent = reserve
        )
        setRoutePreference(pref)
        _onboardingCompleted.value = true
    }

    fun openOnboarding() {
        _onboardingCompleted.value = false
    }

    fun bookCharger(
        charger: Charger,
        timeSlot: String = "2:30 PM - 3:30 PM",
        durationMin: Int = 60
    ): Booking {
        val energyDelivered = (charger.powerKw * (durationMin / 60.0))
        val electricityCost = energyDelivered * charger.pricePerKwh
        val platformFee = 12.0
        val totalCost = electricityCost + platformFee

        val booking = Booking(
            id = "bk-${UUID.randomUUID().toString().take(8)}",
            chargerId = charger.id,
            chargerName = charger.name,
            hostName = charger.hostName.ifEmpty { "Certified Host" },
            dateText = "Today",
            timeSlotText = timeSlot,
            durationMinutes = durationMin,
            energyKwh = energyDelivered,
            electricityCostInr = electricityCost,
            platformFeeInr = platformFee,
            totalCostInr = totalCost,
            status = BookingStatus.CONFIRMED,
            bookingCode = "VM-BLR-${(1000..9999).random()}",
            address = charger.address,
            connectorType = "${charger.connectorType} (${charger.powerKw} kW)"
        )

        // Optimistic store update
        _bookings.value = listOf(booking) + _bookings.value
        _latestConfirmedBooking.value = booking

        // Update profile savings
        _userProfile.value = _userProfile.value.copy(
            totalSavingsInr = _userProfile.value.totalSavingsInr + 48.0,
            totalCarbonAvoidedKg = _userProfile.value.totalCarbonAvoidedKg + 3.2
        )

        return booking
    }

    fun addHostListing(
        name: String,
        powerKw: Double,
        pricePerKwh: Double,
        address: String,
        amenities: List<String>
    ) {
        val newListing = Charger(
            id = "vs-${UUID.randomUUID().toString().take(6)}",
            name = name,
            hostName = _userProfile.value.name,
            type = ChargerType.VOLTSHARE,
            connectorType = "Type 2",
            powerKw = powerKw,
            pricePerKwh = pricePerKwh,
            estimatedWaitMinutes = 0,
            carbonIntensityGCo2PerKwh = 400,
            availability = ChargerAvailability.AVAILABLE,
            rating = 5.0,
            reviewCount = 0,
            amenities = amenities,
            detourKm = 1.5,
            latitude = 12.9800,
            longitude = 77.6000,
            address = address,
            isHostVerified = true
        )
        _voltShareListings.value = listOf(newListing) + _voltShareListings.value
        _chargers.value = listOf(newListing) + _chargers.value
    }

    fun toggleHighContrast(enabled: Boolean) {
        _userProfile.value = _userProfile.value.copy(highContrastMode = enabled)
    }

    fun toggleForceMapFallback(enabled: Boolean) {
        _userProfile.value = _userProfile.value.copy(forceMapFallback = enabled)
    }

    fun setSafetyReserve(percent: Int) {
        _userProfile.value = _userProfile.value.copy(safetyReservePercent = percent)
        recalculateRange()
    }

    fun askGemini(query: String) {
        viewModelScope.launch {
            _isGeminiLoading.value = true
            delay(650) // Smooth conversational feel
            _geminiResponse.value = GeminiTripAssistant.answerQuery(query)
            _isGeminiLoading.value = false
        }
    }

    fun clearGemini() {
        _geminiResponse.value = null
    }

    fun updateRouteLocations(origin: String, destination: String) {
        val prefName = _userProfile.value.preferredRouteMode.name
        val routeInfo = CityGeoLocator.calculateRoute(origin, destination, prefName)

        val totalCost = if (routeInfo.isLongDistanceHighway) {
            (routeInfo.distanceKm * 0.17 * 18.0).coerceAtLeast(120.0)
        } else {
            84.0
        }

        val estimatedArrivalWithStops = if (routeInfo.isLongDistanceHighway) 60 else 52

        _routePlan.value = _routePlan.value.copy(
            origin = routeInfo.originName,
            destination = routeInfo.destName,
            distanceKm = routeInfo.distanceKm,
            elevationGainMeters = routeInfo.elevationGainMeters,
            totalEstimatedTimeMinutes = routeInfo.travelTimeMinutes,
            recommendedCharger = routeInfo.recommendedCharger,
            totalCostInr = totalCost,
            estimatedArrivalSocWithStop = estimatedArrivalWithStops,
            whyExplanation = routeInfo.routeExplanation
        )
        recalculateRange()
    }

    fun resetDemoData() {
        _userProfile.value = SeedData.defaultUserProfile
        _currentVehicle.value = SeedData.demoVehicle
        _routePlan.value = SeedData.cheapestRoutePlan
        _selectedCharger.value = SeedData.ananyaCharger
        _chargers.value = SeedData.allChargers
        _voltShareListings.value = SeedData.voltShareListings
        _bookings.value = listOf(SeedData.initialBooking)
        _latestConfirmedBooking.value = SeedData.initialBooking
        _ecoReport.value = SeedData.defaultEcoReport
        _tripHistory.value = SeedData.pastTrips
        _geminiResponse.value = null
        recalculateRange()
    }

    private fun recalculateRange() {
        val plan = _routePlan.value
        val vehicle = _currentVehicle.value
        val reserve = _userProfile.value.safetyReservePercent

        val result = RangePredictor.predictRange(
            vehicle = vehicle,
            tripDistanceKm = plan.distanceKm,
            elevationGainMeters = plan.elevationGainMeters.toDouble(),
            ambientTempCelsius = plan.temperatureCelsius.toDouble(),
            isAcOn = plan.acOn,
            safetyReservePercent = reserve
        )
        _rangePrediction.value = result

        _routePlan.value = _routePlan.value.copy(
            predictedArrivalSocPercent = result.predictedArrivalSocPercent,
            isChargeStopRequired = result.isSafetyReserveBreached
        )
    }
}
