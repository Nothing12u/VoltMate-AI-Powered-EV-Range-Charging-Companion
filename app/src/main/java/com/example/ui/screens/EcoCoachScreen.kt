package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EcoReport
import com.example.ui.components.EcoScoreRing
import com.example.ui.components.GlassCard
import com.example.ui.components.MetricBadge
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
fun EcoCoachScreen(
    ecoReport: EcoReport,
    onPlanNextTrip: () -> Unit,
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
            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Eco-Coach Report",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Bengaluru ➔ Nandi Hills (96 km)",
                        fontSize = 13.sp,
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
                        text = "Top 12% Drivers",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Eco Score Ring
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                EcoScoreRing(score = ecoReport.score)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Trip Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricBadge(
                    label = "Efficiency",
                    value = "${ecoReport.efficiencyKwhPer100Km} kWh/100km",
                    accentColor = ElectricLime,
                    modifier = Modifier.weight(1f)
                )
                MetricBadge(
                    label = "Consumed",
                    value = "${ecoReport.energyConsumedKwh} kWh",
                    accentColor = ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricBadge(
                    label = "CO₂ Avoided",
                    value = "${ecoReport.carbonAvoidedKg} kg",
                    accentColor = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricBadge(
                    label = "Trip Cost",
                    value = "₹${ecoReport.estimatedCostInr.toInt()}",
                    accentColor = WarningAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Predicted vs Actual Consumption Comparison Chart
            SectionHeader(title = "Predicted vs. Actual Consumption")
            Spacer(modifier = Modifier.height(10.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "You used 1.4 kWh less than predicted (-8.6% delta) due to smooth uphill driving.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Comparison Bar Chart Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val barHeight = 22.dp.toPx()
                            val maxKwh = 20.0

                            // Predicted Bar
                            val predWidth = (w * (ecoReport.predictedConsumptionKwh / maxKwh)).toFloat()
                            drawRoundRect(
                                color = Color(0xFF1F385C),
                                topLeft = Offset(0f, 10f),
                                size = Size(w, barHeight),
                                cornerRadius = CornerRadius(8f, 8f)
                            )
                            drawRoundRect(
                                color = ElectricBlue,
                                topLeft = Offset(0f, 10f),
                                size = Size(predWidth, barHeight),
                                cornerRadius = CornerRadius(8f, 8f)
                            )

                            // Actual Bar
                            val actWidth = (w * (ecoReport.actualConsumptionKwh / maxKwh)).toFloat()
                            drawRoundRect(
                                color = Color(0xFF1F385C),
                                topLeft = Offset(0f, 54f),
                                size = Size(w, barHeight),
                                cornerRadius = CornerRadius(8f, 8f)
                            )
                            drawRoundRect(
                                color = ElectricLime,
                                topLeft = Offset(0f, 54f),
                                size = Size(actWidth, barHeight),
                                cornerRadius = CornerRadius(8f, 8f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ElectricBlue))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Predicted: ${ecoReport.predictedConsumptionKwh} kWh", fontSize = 11.sp, color = TextSecondary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ElectricLime))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Actual: ${ecoReport.actualConsumptionKwh} kWh (Better)", fontSize = 11.sp, color = ElectricLime, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // AI Eco-Coach Insights
            SectionHeader(title = "Eco-Coach Insights")
            Spacer(modifier = Modifier.height(10.dp))

            ecoReport.insights.forEach { insight ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = insight.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = insight.description,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(ElectricLime.copy(alpha = 0.2f))
                                .border(1.dp, ElectricLime, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = insight.impactText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricLime
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Achievement Badges
            SectionHeader(title = "Driver Badges Unlocked")
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ecoReport.badges.forEach { badge ->
                    GlassCard(
                        modifier = Modifier.weight(1f),
                        borderColor = EmeraldGreen.copy(alpha = 0.4f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = badge.iconEmoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = badge.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = badge.description,
                                fontSize = 9.sp,
                                color = TextSecondary,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton(
                    text = "Share Score 87",
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    text = "Plan Next Trip",
                    onClick = onPlanNextTrip,
                    modifier = Modifier.weight(1.2f)
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
