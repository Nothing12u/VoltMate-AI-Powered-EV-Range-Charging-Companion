package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Charger
import com.example.ui.components.ArFinderOverlay
import com.example.ui.components.GeminiAssistantSheet
import com.example.ui.screens.ChargerMapScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EcoCoachScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RoutePlannerScreen
import com.example.ui.screens.VoltShareScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorderSubtle
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoltMateTheme
import com.example.viewmodel.VoltMateViewModel

enum class AppTab(val title: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    PLANNER("Planner", Icons.Default.Navigation),
    MAP("Map", Icons.Default.Map),
    VOLTSHARE("VoltShare", Icons.Default.ElectricBolt),
    PROFILE("Profile", Icons.Default.Person)
}

enum class SubScreen {
    NONE,
    ECO_COACH,
    AR_FINDER
}

class MainActivity : ComponentActivity() {

    private val viewModel: VoltMateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userProfile by viewModel.userProfile.collectAsState()
            val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()
            val currentVehicle by viewModel.currentVehicle.collectAsState()
            val routePlan by viewModel.routePlan.collectAsState()
            val rangePrediction by viewModel.rangePrediction.collectAsState()
            val chargers by viewModel.chargers.collectAsState()
            val selectedCharger by viewModel.selectedCharger.collectAsState()
            val voltShareListings by viewModel.voltShareListings.collectAsState()
            val bookings by viewModel.bookings.collectAsState()
            val ecoReport by viewModel.ecoReport.collectAsState()
            val tripHistory by viewModel.tripHistory.collectAsState()
            val isGeminiLoading by viewModel.isGeminiLoading.collectAsState()
            val geminiResponse by viewModel.geminiResponse.collectAsState()

            var currentTab by remember { mutableStateOf(AppTab.DASHBOARD) }
            var currentSubScreen by remember { mutableStateOf(SubScreen.NONE) }
            var isGeminiSheetVisible by remember { mutableStateOf(false) }

            VoltMateTheme(highContrast = userProfile.highContrastMode) {
                if (!onboardingCompleted) {
                    OnboardingScreen(
                        onFinished = { veh, city, pref, reserve ->
                            viewModel.completeOnboarding(veh, city, pref, reserve)
                        },
                        modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                    )
                } else if (currentSubScreen == SubScreen.AR_FINDER) {
                    BackHandler { currentSubScreen = SubScreen.NONE }
                    ArFinderOverlay(
                        targetCharger = selectedCharger ?: chargers.first(),
                        onClose = { currentSubScreen = SubScreen.NONE },
                        onReserve = { charger ->
                            viewModel.bookCharger(charger)
                            currentSubScreen = SubScreen.NONE
                            currentTab = AppTab.PROFILE
                        },
                        onOpenRoute = {
                            currentSubScreen = SubScreen.NONE
                            currentTab = AppTab.PLANNER
                        }
                    )
                } else if (currentSubScreen == SubScreen.ECO_COACH) {
                    BackHandler { currentSubScreen = SubScreen.NONE }
                    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                        EcoCoachScreen(
                            ecoReport = ecoReport,
                            onPlanNextTrip = {
                                currentSubScreen = SubScreen.NONE
                                currentTab = AppTab.PLANNER
                            }
                        )
                    }
                } else {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkBg),
                        bottomBar = {
                            NavigationBar(
                                containerColor = DarkSurface,
                                tonalElevation = 8.dp,
                                modifier = Modifier
                                    .border(1.dp, DarkBorderSubtle)
                                    .navigationBarsPadding()
                            ) {
                                AppTab.values().forEach { tab ->
                                    val isSelected = currentTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { currentTab = tab },
                                        icon = {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = tab.title,
                                                tint = if (isSelected) ElectricLime else TextSecondary
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = tab.title,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) ElectricLime else TextSecondary
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = Color(0xFF132742)
                                        ),
                                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .statusBarsPadding()
                        ) {
                            when (currentTab) {
                                AppTab.DASHBOARD -> {
                                    DashboardScreen(
                                        userProfile = userProfile,
                                        vehicle = currentVehicle,
                                        rangePrediction = rangePrediction,
                                        recentTrip = tripHistory.firstOrNull(),
                                        onPlanTripClick = { currentTab = AppTab.PLANNER },
                                        onFindChargerClick = { currentTab = AppTab.MAP },
                                        onShareChargerClick = { currentTab = AppTab.VOLTSHARE },
                                        onArFinderClick = { currentSubScreen = SubScreen.AR_FINDER },
                                        onOpenEcoCoach = { currentSubScreen = SubScreen.ECO_COACH },
                                        onOpenGeminiChat = { isGeminiSheetVisible = true }
                                    )
                                }
                                AppTab.PLANNER -> {
                                    RoutePlannerScreen(
                                        routePlan = routePlan,
                                        allChargers = chargers,
                                        onPreferenceChange = { viewModel.setRoutePreference(it) },
                                        onReserveStop = { charger ->
                                            viewModel.bookCharger(charger)
                                            currentTab = AppTab.PROFILE
                                        },
                                        onUpdateRoute = { origin, dest ->
                                            viewModel.updateRouteLocations(origin, dest)
                                        }
                                    )
                                }
                                AppTab.MAP -> {
                                    ChargerMapScreen(
                                        chargers = chargers,
                                        selectedCharger = selectedCharger,
                                        onSelectCharger = { viewModel.setSelectedCharger(it) },
                                        onReserveCharger = { charger ->
                                            viewModel.bookCharger(charger)
                                            currentTab = AppTab.PROFILE
                                        },
                                        onArFinderClick = { currentSubScreen = SubScreen.AR_FINDER }
                                    )
                                }
                                AppTab.VOLTSHARE -> {
                                    VoltShareScreen(
                                        listings = voltShareListings,
                                        onBookSession = { charger, slot, duration ->
                                            viewModel.bookCharger(charger, slot, duration)
                                        },
                                        onAddListing = { name, pwr, prc, addr, amens ->
                                            viewModel.addHostListing(name, pwr, prc, addr, amens)
                                        }
                                    )
                                }
                                AppTab.PROFILE -> {
                                    ProfileScreen(
                                        userProfile = userProfile,
                                        vehicle = currentVehicle,
                                        upcomingBookings = bookings,
                                        pastTrips = tripHistory,
                                        onToggleHighContrast = { viewModel.toggleHighContrast(it) },
                                        onToggleMapFallback = { viewModel.toggleForceMapFallback(it) },
                                        onUpdateSafetyReserve = { viewModel.setSafetyReserve(it) },
                                        onPreferenceChange = { viewModel.setRoutePreference(it) },
                                        onResetDemo = { viewModel.resetDemoData() },
                                        onRestartOnboarding = { viewModel.openOnboarding() }
                                    )
                                }
                            }

                            // Gemini AI Assistant Bottom Sheet
                            AnimatedVisibility(
                                visible = isGeminiSheetVisible,
                                enter = slideInVertically(initialOffsetY = { it }),
                                exit = slideOutVertically(targetOffsetY = { it }),
                                modifier = Modifier.align(Alignment.BottomCenter)
                            ) {
                                GeminiAssistantSheet(
                                    response = geminiResponse,
                                    isLoading = isGeminiLoading,
                                    onQuery = { viewModel.askGemini(it) },
                                    onClose = { isGeminiSheetVisible = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
