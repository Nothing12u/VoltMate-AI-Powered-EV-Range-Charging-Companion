package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Booking
import com.example.model.RoutePreference
import com.example.model.TripHistoryItem
import com.example.model.UserProfile
import com.example.model.Vehicle
import com.example.ui.components.GlassCard
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecondaryButton
import com.example.ui.components.SectionHeader
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorderSubtle
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    vehicle: Vehicle,
    upcomingBookings: List<Booking>,
    pastTrips: List<TripHistoryItem>,
    onToggleHighContrast: (Boolean) -> Unit,
    onToggleMapFallback: (Boolean) -> Unit,
    onUpdateSafetyReserve: (Int) -> Unit,
    onPreferenceChange: (RoutePreference) -> Unit,
    onResetDemo: () -> Unit,
    onRestartOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var notificationsEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Profile Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(ElectricLime),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = DarkBg, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userProfile.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${userProfile.city} • VoltShare Pioneer",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Level 4 Eco-Driver (Score 87)",
                            fontSize = 11.sp,
                            color = ElectricLime,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vehicle Specs Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVE EV VEHICLE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = vehicle.licensePlate,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricLime
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${vehicle.make} ${vehicle.model}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Capacity: ${vehicle.batteryCapacityKwh} kWh • Baseline: ${vehicle.baselineEfficiencyWhPerKm.toInt()} Wh/km • Port: ${vehicle.connectorType}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Upcoming VoltShare Bookings
            if (upcomingBookings.isNotEmpty()) {
                SectionHeader(title = "Upcoming VoltShare Sessions")
                Spacer(modifier = Modifier.height(10.dp))

                upcomingBookings.forEach { booking ->
                    GlassCard(
                        borderColor = ElectricLime.copy(alpha = 0.4f),
                        backgroundColor = Color(0xFF0D2138),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "CONFIRMED SESSION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                                }
                                Text(text = booking.bookingCode, fontSize = 12.sp, fontWeight = FontWeight.Black, color = ElectricLime)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = booking.chargerName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "${booking.timeSlotText} • ${booking.hostName}", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "Total: ₹${booking.totalCostInr.toInt()} (${booking.energyKwh} kWh delivered)", fontSize = 12.sp, color = ElectricLime, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Past Trips History
            SectionHeader(title = "Recent Trip Logs")
            Spacer(modifier = Modifier.height(10.dp))

            pastTrips.forEach { trip ->
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = trip.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "${trip.distanceKm} km • ${trip.dateText}", fontSize = 11.sp, color = TextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "${trip.ecoScore} Eco", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                            Text(text = "-${trip.carbonSavedKg} kg CO₂", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Settings Section
            SectionHeader(title = "App & EV Settings")
            Spacer(modifier = Modifier.height(10.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    // Safety Reserve Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Safety Reserve Target", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Text(text = "${userProfile.safetyReservePercent}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                    }
                    Slider(
                        value = userProfile.safetyReservePercent.toFloat(),
                        onValueChange = { onUpdateSafetyReserve(it.toInt()) },
                        valueRange = 10f..30f,
                        steps = 3,
                        colors = SliderDefaults.colors(thumbColor = ElectricLime, activeTrackColor = ElectricLime)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Route Preference Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Default Route Strategy", fontSize = 13.sp, color = TextPrimary)
                        Text(
                            text = userProfile.preferredRouteMode.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricLime
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // High Contrast Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "High Contrast Mode", fontSize = 13.sp, color = TextPrimary)
                        Switch(
                            checked = userProfile.highContrastMode,
                            onCheckedChange = onToggleHighContrast,
                            colors = SwitchDefaults.colors(checkedThumbColor = ElectricLime, checkedTrackColor = ElectricLime.copy(alpha = 0.4f))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Force Map Fallback Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Force Map Fallback Mode", fontSize = 13.sp, color = TextPrimary)
                            Text(text = "Guarantees 100% demo reliability without external keys", fontSize = 10.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = userProfile.forceMapFallback,
                            onCheckedChange = onToggleMapFallback,
                            colors = SwitchDefaults.colors(checkedThumbColor = ElectricLime, checkedTrackColor = ElectricLime.copy(alpha = 0.4f))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Notifications Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Charging Session Notifications", fontSize = 13.sp, color = TextPrimary)
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ElectricLime, checkedTrackColor = ElectricLime.copy(alpha = 0.4f))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Demo Actions
            SecondaryButton(
                text = "Re-run Onboarding Setup",
                onClick = onRestartOnboarding,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            SecondaryButton(
                text = "Reset Demo Data (Bengaluru State)",
                onClick = onResetDemo,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
