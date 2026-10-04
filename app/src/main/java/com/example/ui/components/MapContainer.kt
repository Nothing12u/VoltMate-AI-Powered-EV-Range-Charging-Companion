package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Charger
import com.example.model.ChargerAvailability
import com.example.model.ChargerType
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorderSubtle
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricLime
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun MapContainer(
    chargers: List<Charger>,
    selectedCharger: Charger?,
    onSelectCharger: (Charger) -> Unit,
    onReserveCharger: (Charger) -> Unit,
    showRouteToNandiHills: Boolean = true,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var detailCharger by remember { mutableStateOf<Charger?>(selectedCharger) }
    var navigatingState by remember { mutableStateOf(false) }

    LaunchedEffect(selectedCharger) {
        if (selectedCharger != null) {
            detailCharger = selectedCharger
        }
    }

    val filteredChargers = remember(chargers, selectedFilter) {
        when (selectedFilter) {
            "Available now" -> chargers.filter { it.availability == ChargerAvailability.AVAILABLE }
            "VoltShare" -> chargers.filter { it.type == ChargerType.VOLTSHARE }
            "Fast charge" -> chargers.filter { it.powerKw >= 50.0 }
            "Under ₹12/kWh" -> chargers.filter { it.pricePerKwh <= 12.0 }
            "Type 2" -> chargers.filter { it.connectorType.contains("Type 2", ignoreCase = true) }
            "CCS2" -> chargers.filter { it.connectorType.contains("CCS2", ignoreCase = true) }
            else -> chargers
        }
    }

    Box(modifier = modifier.fillMaxSize().background(DarkBg)) {
        // Fallback Dark Vector Map Canvas
        MapFallbackView(
            chargers = filteredChargers,
            selectedCharger = detailCharger,
            onPinClick = { charger ->
                detailCharger = charger
                onSelectCharger(charger)
            },
            showRoute = showRouteToNandiHills
        )

        // Top Filter Chips Row
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 12.dp, end = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "All",
                    "VoltShare",
                    "Available now",
                    "Fast charge",
                    "Under ₹12/kWh",
                    "Type 2",
                    "CCS2"
                ).forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ElectricLime else DarkSurfaceVariant)
                            .border(1.dp, if (isSelected) ElectricLime else DarkBorderSubtle, RoundedCornerShape(12.dp))
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) DarkBg else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            if (navigatingState) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(EmeraldGreen.copy(alpha = 0.2f))
                        .border(1.dp, EmeraldGreen, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = "Navigating",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simulated Route Active: Follow NH44 North",
                            color = EmeraldGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Exit",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { navigatingState = false }
                        )
                    }
                }
            }
        }

        // Bottom Sheet for Selected Charger
        AnimatedVisibility(
            visible = detailCharger != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            detailCharger?.let { charger ->
                ChargerDetailCard(
                    charger = charger,
                    onClose = { detailCharger = null },
                    onReserve = {
                        onReserveCharger(charger)
                    },
                    onNavigate = {
                        navigatingState = true
                    }
                )
            }
        }
    }
}

@Composable
fun MapFallbackView(
    chargers: List<Charger>,
    selectedCharger: Charger?,
    onPinClick: (Charger) -> Unit,
    showRoute: Boolean
) {
    // Pulse animation for user location
    val pulseAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        pulseAnim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    // Relative map coordinates normalization
    // Bangalore latitude bounds: 12.92 to 13.38 (Nandi Hills)
    // Longitude bounds: 77.54 to 77.72
    fun toNormalized(lat: Double, lng: Double): Offset {
        val minLat = 12.92
        val maxLat = 13.38
        val minLng = 77.52
        val maxLng = 77.74

        val yNorm = 1.0 - ((lat - minLat) / (maxLat - minLat)).coerceIn(0.0, 1.0)
        val xNorm = ((lng - minLng) / (maxLng - minLng)).coerceIn(0.0, 1.0)
        return Offset(xNorm.toFloat(), yNorm.toFloat())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(chargers) {
                detectTapGestures { tapOffset ->
                    val width = size.width
                    val height = size.height

                    // Hit test pins
                    chargers.forEach { charger ->
                        val norm = toNormalized(charger.latitude, charger.longitude)
                        val pinX = norm.x * width
                        val pinY = norm.y * height
                        val distance = (Offset(pinX, pinY) - tapOffset).getDistance()
                        if (distance <= 40f) {
                            onPinClick(charger)
                            return@detectTapGestures
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Grid Background lines
            val gridStep = 40.dp.toPx()
            for (x in 0..(w / gridStep).toInt()) {
                drawLine(
                    color = Color(0xFF0F1E36),
                    start = Offset(x * gridStep, 0f),
                    end = Offset(x * gridStep, h),
                    strokeWidth = 1f
                )
            }
            for (y in 0..(h / gridStep).toInt()) {
                drawLine(
                    color = Color(0xFF0F1E36),
                    start = Offset(0f, y * gridStep),
                    end = Offset(w, y * gridStep),
                    strokeWidth = 1f
                )
            }

            // 2. Simulated Major Roads & Highway NH44 Corridor
            // Outer Ring Road (curved path)
            val orrPath = Path().apply {
                moveTo(w * 0.15f, h * 0.78f)
                cubicTo(w * 0.35f, h * 0.72f, w * 0.65f, h * 0.74f, w * 0.88f, h * 0.82f)
            }
            drawPath(
                path = orrPath,
                color = Color(0xFF162C4E),
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Bellary Road / NH44 Corridor (Bangalore Center -> Hebbal -> Devanahalli -> Nandi Hills)
            val nh44Path = Path().apply {
                moveTo(w * 0.45f, h * 0.82f) // Bangalore City Center
                lineTo(w * 0.44f, h * 0.68f) // Hebbal
                lineTo(w * 0.46f, h * 0.48f) // Yelahanka
                lineTo(w * 0.58f, h * 0.28f) // Devanahalli
                lineTo(w * 0.62f, h * 0.12f) // Nandi Hills
            }
            drawPath(
                path = nh44Path,
                color = Color(0xFF1D3B66),
                style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3. Active Route Polyline (Bengaluru -> Ananya's Charger -> Nandi Hills)
            if (showRoute) {
                val routePath = Path().apply {
                    val start = toNormalized(12.9716, 77.5946) // City Center
                    val ananya = toNormalized(13.0382, 77.5891) // Ananya Hebbal
                    val nandi = toNormalized(13.3702, 77.6835) // Nandi Hills

                    moveTo(start.x * w, start.y * h)
                    lineTo(start.x * w - 8f, (start.y * h + ananya.y * h) / 2)
                    lineTo(ananya.x * w, ananya.y * h)
                    lineTo(ananya.x * w + 12f, ananya.y * h - 40f)
                    lineTo(nandi.x * w, nandi.y * h)
                }

                // Route glow
                drawPath(
                    path = routePath,
                    brush = Brush.linearGradient(listOf(ElectricLime.copy(alpha = 0.4f), EmeraldGreen.copy(alpha = 0.4f))),
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )

                // Route core
                drawPath(
                    path = routePath,
                    brush = Brush.linearGradient(listOf(ElectricLime, EmeraldGreen)),
                    style = Stroke(
                        width = 4.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(25f, 15f), 0f)
                    )
                )
            }

            // 4. Destination Marker: Nandi Hills
            val nandiNorm = toNormalized(13.3702, 77.6835)
            val nandiPos = Offset(nandiNorm.x * w, nandiNorm.y * h)
            drawCircle(color = WarningAmber.copy(alpha = 0.3f), radius = 18.dp.toPx(), center = nandiPos)
            drawCircle(color = WarningAmber, radius = 7.dp.toPx(), center = nandiPos)

            // 5. User Location Marker (Bengaluru City Center) with pulse
            val userNorm = toNormalized(12.9716, 77.5946)
            val userPos = Offset(userNorm.x * w, userNorm.y * h)
            val pulseRadius = (12 + (pulseAnim.value * 24)).dp.toPx()
            drawCircle(
                color = ElectricBlue.copy(alpha = (1f - pulseAnim.value) * 0.6f),
                radius = pulseRadius,
                center = userPos
            )
            drawCircle(color = ElectricBlue, radius = 7.dp.toPx(), center = userPos)
            drawCircle(color = Color.White, radius = 3.dp.toPx(), center = userPos)

            // 6. Draw Charger Pins
            chargers.forEach { charger ->
                val norm = toNormalized(charger.latitude, charger.longitude)
                val pos = Offset(norm.x * w, norm.y * h)
                val isSelected = selectedCharger?.id == charger.id

                val pinColor = when {
                    charger.type == ChargerType.VOLTSHARE -> ElectricLime
                    charger.availability == ChargerAvailability.AVAILABLE -> EmeraldGreen
                    charger.availability == ChargerAvailability.BUSY -> WarningAmber
                    else -> Color(0xFFFF6B6B)
                }

                // Selection highlight ring
                if (isSelected) {
                    drawCircle(
                        color = Color.White,
                        radius = 16.dp.toPx(),
                        center = pos,
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                    drawCircle(
                        color = pinColor.copy(alpha = 0.3f),
                        radius = 22.dp.toPx(),
                        center = pos
                    )
                }

                // Main Pin Circle
                drawCircle(
                    color = pinColor,
                    radius = if (isSelected) 10.dp.toPx() else 8.dp.toPx(),
                    center = pos
                )
                drawCircle(
                    color = DarkBg,
                    radius = if (isSelected) 5.dp.toPx() else 4.dp.toPx(),
                    center = pos
                )
            }
        }
    }
}

@Composable
fun ChargerDetailCard(
    charger: Charger,
    onClose: () -> Unit,
    onReserve: () -> Unit,
    onNavigate: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(DarkSurface)
            .border(1.dp, DarkCardBorder, RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val badgeColor = if (charger.type == ChargerType.VOLTSHARE) ElectricLime else EmeraldGreen
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(badgeColor.copy(alpha = 0.2f))
                                .border(1.dp, badgeColor, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (charger.type == ChargerType.VOLTSHARE) "VOLTSHARE HOME" else "PUBLIC DC FAST",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        }
                        if (charger.isHostVerified) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Verified Host ✓",
                                fontSize = 11.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = charger.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    if (charger.hostName.isNotEmpty()) {
                        Text(
                            text = "Hosted by ${charger.hostName}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Specs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(label = "Power", value = "${charger.powerKw} kW", icon = Icons.Default.ElectricBolt)
                MetricItem(label = "Price", value = "₹${charger.pricePerKwh.toInt()}/kWh", icon = null)
                MetricItem(label = "Wait", value = if (charger.estimatedWaitMinutes == 0) "0 min" else "${charger.estimatedWaitMinutes} min", icon = null)
                MetricItem(label = "Rating", value = "${charger.rating} ★", icon = null)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Amenities
            if (charger.amenities.isNotEmpty()) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    charger.amenities.take(4).forEach { amenity ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = amenity, fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // CTAs
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton(
                    text = "Navigate",
                    onClick = onNavigate,
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    text = if (charger.type == ChargerType.VOLTSHARE) "Book Session" else "Reserve Plug",
                    onClick = onReserve,
                    modifier = Modifier.weight(1.3f)
                )
            }
        }
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
