package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Charger
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArFinderOverlay(
    targetCharger: Charger,
    onClose: () -> Unit,
    onReserve: (Charger) -> Unit,
    onOpenRoute: (Charger) -> Unit,
    modifier: Modifier = Modifier
) {
    var isCameraMode by remember { mutableStateOf(false) }

    // Compass & Radar rotation animation
    val radarAngle = remember { Animatable(0f) }
    val arrowPulse = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        radarAngle.animateTo(
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    LaunchedEffect(Unit) {
        arrowPulse.animateTo(
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isCameraMode) Color(0xFF030712) else DarkBg)
    ) {
        // Radar HUD Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2, h * 0.42f)

            // Radar rings
            val radii = listOf(60.dp.toPx(), 120.dp.toPx(), 180.dp.toPx())
            radii.forEach { r ->
                drawCircle(
                    color = ElectricLime.copy(alpha = 0.15f),
                    radius = r,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Crosshairs
            drawLine(
                color = ElectricLime.copy(alpha = 0.2f),
                start = Offset(center.x - 200.dp.toPx(), center.y),
                end = Offset(center.x + 200.dp.toPx(), center.y),
                strokeWidth = 1f
            )
            drawLine(
                color = ElectricLime.copy(alpha = 0.2f),
                start = Offset(center.x, center.y - 200.dp.toPx()),
                end = Offset(center.x, center.y + 200.dp.toPx()),
                strokeWidth = 1f
            )

            // Sweeping radar beam
            rotate(degrees = radarAngle.value, pivot = center) {
                val sweepBrush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        ElectricLime.copy(alpha = 0.05f),
                        ElectricLime.copy(alpha = 0.35f)
                    ),
                    center = center
                )
                drawCircle(
                    brush = sweepBrush,
                    radius = 180.dp.toPx(),
                    center = center
                )
            }

            // Direction Arrow pointing to charger (target heading 38° North-East)
            rotate(degrees = 38f, pivot = center) {
                val arrowDist = 110.dp.toPx() * arrowPulse.value
                val arrowTip = Offset(center.x, center.y - arrowDist)

                val arrowPath = Path().apply {
                    moveTo(arrowTip.x, arrowTip.y)
                    lineTo(arrowTip.x - 16.dp.toPx(), arrowTip.y + 32.dp.toPx())
                    lineTo(arrowTip.x, arrowTip.y + 24.dp.toPx())
                    lineTo(arrowTip.x + 16.dp.toPx(), arrowTip.y + 32.dp.toPx())
                    close()
                }

                drawPath(
                    path = arrowPath,
                    color = ElectricLime
                )
            }

            // Target Blip (Charger Node)
            val blipDist = 120.dp.toPx()
            val rad = (38f - 90f) * (PI / 180f)
            val blipPos = Offset(
                center.x + (blipDist * cos(rad)).toFloat(),
                center.y + (blipDist * sin(rad)).toFloat()
            )

            drawCircle(color = ElectricLime.copy(alpha = 0.4f), radius = 18.dp.toPx() * arrowPulse.value, center = blipPos)
            drawCircle(color = ElectricLime, radius = 8.dp.toPx(), center = blipPos)
            drawCircle(color = DarkBg, radius = 4.dp.toPx(), center = blipPos)
        }

        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DarkSurface.copy(alpha = 0.85f))
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close AR",
                    tint = TextPrimary
                )
            }

            // Heading & Mode Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface.copy(alpha = 0.85f))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = "Heading",
                        tint = ElectricLime,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HEADING 038° NE • LOCKED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricLime,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Camera / HUD Toggle
            IconButton(
                onClick = { isCameraMode = !isCameraMode },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DarkSurface.copy(alpha = 0.85f))
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Toggle Camera",
                    tint = if (isCameraMode) ElectricLime else TextSecondary
                )
            }
        }

        // Bottom Target Lock Information Card
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(DarkSurface.copy(alpha = 0.95f))
                .border(1.dp, ElectricLime.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElectricLime.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "TARGET ACQUIRED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = ElectricLime
                        )
                    }

                    Text(
                        text = "${targetCharger.detourKm} km away",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricLime
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = targetCharger.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "${targetCharger.connectorType} • ${targetCharger.powerKw} kW • ₹${targetCharger.pricePerKwh.toInt()}/kWh • 0 min wait",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SecondaryButton(
                        text = "Open Route",
                        onClick = { onOpenRoute(targetCharger) },
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButton(
                        text = "Reserve Now",
                        onClick = { onReserve(targetCharger) },
                        modifier = Modifier.weight(1.3f)
                    )
                }
            }
        }
    }
}
