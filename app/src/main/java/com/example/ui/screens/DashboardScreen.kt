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
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.RangePredictionResult
import com.example.model.TripHistoryItem
import com.example.model.UserProfile
import com.example.model.Vehicle
import com.example.ui.components.GlassCard
import com.example.ui.components.RangeRing
import com.example.ui.components.SectionHeader
import com.example.ui.components.WarningBanner
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
fun DashboardScreen(
    userProfile: UserProfile,
    vehicle: Vehicle,
    rangePrediction: RangePredictionResult,
    recentTrip: TripHistoryItem?,
    onPlanTripClick: () -> Unit,
    onFindChargerClick: () -> Unit,
    onShareChargerClick: () -> Unit,
    onArFinderClick: () -> Unit,
    onOpenEcoCoach: () -> Unit,
    onOpenGeminiChat: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            // Header Greeting
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good afternoon,",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = userProfile.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }

                // Vehicle Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = ElectricLime,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${vehicle.make} ${vehicle.model.take(7)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Animated Battery & Range Ring
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                RangeRing(
                    socPercent = rangePrediction.currentSocPercent,
                    usableRangeKm = rangePrediction.usableRangeKm,
                    confidencePercent = rangePrediction.confidencePercent,
                    safetyReservePercent = rangePrediction.safetyReserveTargetPercent
                )
            }

            // Confidence description
            Text(
                text = "High confidence — based on live traffic, 31°C ambient weather, +620m elevation to Nandi Hills, and cabin AC load.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Safety Warning Banner
            if (rangePrediction.isSafetyReserveBreached) {
                Spacer(modifier = Modifier.height(10.dp))
                WarningBanner(
                    title = "Safety Buffer Warning: Charge Stop Needed",
                    description = "Projected arrival at Nandi Hills is ${rangePrediction.predictedArrivalSocPercent}%, which breaches your ${rangePrediction.safetyReserveTargetPercent}% reserve. A stop at Ananya's Home Charger is recommended."
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Next Charge Card
            GlassCard(
                borderColor = ElectricLime.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = ElectricLime,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "NEXT CHARGE RECOMMENDATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricLime,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Charge in 54 km to keep safety buffer",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Stop at Ananya's Home Charger (Hebbal) • 45 min session",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElectricLime)
                            .clickable(onClick = onPlanTripClick)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "View Route",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBg
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Actions
            SectionHeader(title = "Quick Actions")
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.Navigation,
                    label = "Plan Trip",
                    color = ElectricLime,
                    onClick = onPlanTripClick,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Default.Map,
                    label = "Find Charger",
                    color = EmeraldGreen,
                    onClick = onFindChargerClick,
                    modifier = Modifier.weight(1f)
                )
                QuickActionButton(
                    icon = Icons.Default.Share,
                    label = "Share Charger",
                    color = ElectricBlue,
                    onClick = onShareChargerClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AR Radar Banner Action
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onArFinderClick)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(ElectricLime.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ElectricLime,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "AR-Style Charger Finder",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Camera & radar HUD to spot nearest charger",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Text(
                        text = "Open HUD ➔",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricLime
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Impact Cards
            SectionHeader(title = "Your EV Impact This Month")
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ImpactCard(
                    icon = Icons.Default.Eco,
                    value = "${userProfile.totalCarbonAvoidedKg} kg",
                    label = "CO₂ Avoided",
                    color = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
                ImpactCard(
                    icon = Icons.Default.Paid,
                    value = "₹${userProfile.totalSavingsInr.toInt()}",
                    label = "Fuel Saved",
                    color = ElectricLime,
                    modifier = Modifier.weight(1f)
                )
                ImpactCard(
                    icon = Icons.Default.AddBusiness,
                    value = "₹${userProfile.voltShareEarningsInr.toInt()}",
                    label = "VoltShare Earned",
                    color = ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recent Trip Card
            if (recentTrip != null) {
                SectionHeader(
                    title = "Recent Trip",
                    actionText = "Eco-Coach Report",
                    onActionClick = onOpenEcoCoach
                )
                Spacer(modifier = Modifier.height(10.dp))
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenEcoCoach)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = recentTrip.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${recentTrip.distanceKm} km • ${recentTrip.efficiencyWhKm} Wh/km • ${recentTrip.dateText}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmeraldGreen.copy(alpha = 0.2f))
                                .border(1.dp, EmeraldGreen, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${recentTrip.ecoScore} Eco",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Floating Gemini Assistant FAB
        FloatingActionButton(
            onClick = onOpenGeminiChat,
            containerColor = ElectricLime,
            contentColor = DarkBg,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 20.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Assistant")
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Ask AI", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkBorderSubtle, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun ImpactCard(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkBorderSubtle, RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}
