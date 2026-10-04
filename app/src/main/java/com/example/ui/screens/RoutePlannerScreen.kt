package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Charger
import com.example.model.ChargerType
import com.example.model.RoutePlan
import com.example.model.RoutePreference
import com.example.ui.components.GlassCard
import com.example.ui.components.MapContainer
import com.example.ui.components.PrimaryButton
import com.example.ui.components.WarningBanner
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
fun RoutePlannerScreen(
    routePlan: RoutePlan,
    allChargers: List<Charger>,
    onPreferenceChange: (RoutePreference) -> Unit,
    onReserveStop: (Charger) -> Unit,
    onUpdateRoute: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var originText by remember { mutableStateOf(routePlan.origin) }
    var destText by remember { mutableStateOf(routePlan.destination) }
    var isWhyExpanded by remember { mutableStateOf(true) }
    var showMapPreview by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    var isCalculating by remember { mutableStateOf(false) }
    var calculatedBanner by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(routePlan.origin, routePlan.destination) {
        originText = routePlan.origin
        destText = routePlan.destination
    }

    val popularCorridors = remember {
        listOf(
            Triple("Ongole", "Delhi", "1,850 km"),
            Triple("Bengaluru City Center", "Nandi Hills", "96 km"),
            Triple("Bengaluru", "Hyderabad", "570 km"),
            Triple("Bengaluru", "Kempegowda Airport", "42 km"),
            Triple("Bengaluru", "Mysuru", "145 km")
        )
    }

    fun executeRouteCalculation(from: String, to: String) {
        keyboardController?.hide()
        coroutineScope.launch {
            isCalculating = true
            delay(400)
            onUpdateRoute(from, to)
            isCalculating = false
            calculatedBanner = "Route planned: $from ➔ $to"
        }
    }

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
            // Screen Title
            Text(
                text = "Smart Route Planner",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "AI-optimized routing with terrain, temperature & tariff analysis",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Origin / Destination Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    // Origin input row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(ElectricLime)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = originText,
                            onValueChange = { originText = it },
                            label = { Text("Starting Point / Origin", color = TextSecondary, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                executeRouteCalculation(originText, destText)
                            }),
                            trailingIcon = {
                                if (originText.isNotEmpty()) {
                                    IconButton(onClick = { originText = "" }) {
                                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricLime,
                                unfocusedBorderColor = Color(0xFF1D3557),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = Color(0xFF0F1E33),
                                unfocusedContainerColor = Color(0xFF0F1E33)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                val temp = originText
                                originText = destText
                                destText = temp
                                executeRouteCalculation(originText, destText)
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF132742))
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap Locations",
                                tint = ElectricLime,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Destination input row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(WarningAmber)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = destText,
                            onValueChange = { destText = it },
                            label = { Text("Destination", color = TextSecondary, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                executeRouteCalculation(originText, destText)
                            }),
                            trailingIcon = {
                                if (destText.isNotEmpty()) {
                                    IconButton(onClick = { destText = "" }) {
                                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WarningAmber,
                                unfocusedBorderColor = Color(0xFF1D3557),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = Color(0xFF0F1E33),
                                unfocusedContainerColor = Color(0xFF0F1E33)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF132A46))
                                .padding(horizontal = 10.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${routePlan.distanceKm.toInt()} km",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricLime
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Suggestion Chips
                    Text(
                        text = "POPULAR CORRIDORS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        popularCorridors.forEach { (orig, dest, dist) ->
                            val isSelected = destText.equals(dest, ignoreCase = true) && originText.equals(orig, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) ElectricLime else Color(0xFF13253E))
                                    .border(1.dp, if (isSelected) ElectricLime else DarkBorderSubtle, RoundedCornerShape(10.dp))
                                    .clickable {
                                        originText = orig
                                        destText = dest
                                        executeRouteCalculation(orig, dest)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$orig ➔ $dest ($dist)",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) DarkBg else TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Recalculate Route Action Button with Progress State
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ElectricLime)
                            .clickable(enabled = !isCalculating) {
                                executeRouteCalculation(originText, destText)
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCalculating) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    color = DarkBg,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Optimizing Highway Corridors & Stalls...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkBg
                                )
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = DarkBg,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Calculate AI Route Plan",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkBg
                                )
                            }
                        }
                    }
                }
            }

            // Calculation Success Confirmation Banner
            if (calculatedBanner != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldGreen.copy(alpha = 0.15f))
                        .border(1.dp, EmeraldGreen, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Route calculated: ${routePlan.origin} ➔ ${routePlan.destination} (${routePlan.distanceKm.toInt()} km)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Trip Conditions Pills (Formatted Hours and Minutes)
            val hours = routePlan.totalEstimatedTimeMinutes / 60
            val mins = routePlan.totalEstimatedTimeMinutes % 60
            val formattedTime = if (hours > 0) "${hours}h ${mins}m" else "${mins} min"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ConditionPill(label = "+${routePlan.elevationGainMeters}m Elev", color = ElectricBlue, modifier = Modifier.weight(1f))
                ConditionPill(label = formattedTime, color = ElectricLime, modifier = Modifier.weight(1f))
                ConditionPill(label = "31°C AC On", color = WarningAmber, modifier = Modifier.weight(1f))
                ConditionPill(label = if (routePlan.distanceKm > 200.0) "NH Express" else "NH44 North", color = EmeraldGreen, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Segmented Preference Selector: Cheapest / Fastest / Greenest
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkBorderSubtle, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    RoutePreference.CHEAPEST to "Cheapest",
                    RoutePreference.FASTEST to "Fastest",
                    RoutePreference.GREENEST to "Greenest"
                ).forEach { (pref, title) ->
                    val isSelected = routePlan.preference == pref
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ElectricLime else Color.Transparent)
                            .clickable { onPreferenceChange(pref) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) DarkBg else TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Safety Warning Banner
            WarningBanner(
                title = "Range Risk Detected on NH44 Incline",
                description = "Predicted arrival battery without stopping is only ${routePlan.predictedArrivalSocPercent}%, failing your safe reserve target of ${routePlan.safetyReserveTargetPercent}%. You must add 1 charge stop."
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recommended Stop Card
            val recCharger = routePlan.recommendedCharger
            val isVoltShare = recCharger.type == ChargerType.VOLTSHARE

            GlassCard(
                borderColor = if (isVoltShare) ElectricLime else DarkCardBorder,
                backgroundColor = Color(0xFF0C1B30),
                modifier = Modifier.fillMaxWidth()
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
                                .background(if (isVoltShare) ElectricLime.copy(alpha = 0.2f) else EmeraldGreen.copy(alpha = 0.2f))
                                .border(1.dp, if (isVoltShare) ElectricLime else EmeraldGreen, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isVoltShare) "RECOMMENDED • VOLTSHARE" else "RECOMMENDED • PUBLIC FAST",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVoltShare) ElectricLime else EmeraldGreen
                            )
                        }

                        Text(
                            text = "+${recCharger.detourKm} km detour",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = recCharger.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    if (recCharger.hostName.isNotEmpty()) {
                        Text(
                            text = "Hosted by ${recCharger.hostName} • Verified 4.9 ★ (${recCharger.reviewCount} reviews)",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(title = "Connector", value = recCharger.connectorType)
                        StatItem(title = "Power", value = "${recCharger.powerKw} kW")
                        StatItem(title = "Price", value = "₹${recCharger.pricePerKwh.toInt()}/kWh")
                        StatItem(title = "Wait Queue", value = if (recCharger.estimatedWaitMinutes == 0) "0 min" else "${recCharger.estimatedWaitMinutes} min")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Trip Outcome with Stop
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceVariant)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Final Arrival SoC", fontSize = 10.sp, color = TextSecondary)
                                Text(
                                    text = "${routePlan.estimatedArrivalSocWithStop}% (Safe)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                            }
                            Column {
                                Text(text = "Estimated Cost", fontSize = 10.sp, color = TextSecondary)
                                Text(
                                    text = "₹${routePlan.totalCostInr.toInt()}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricLime
                                )
                            }
                            Column {
                                Text(text = "Total Travel", fontSize = 10.sp, color = TextSecondary)
                                val totH = routePlan.totalEstimatedTimeMinutes / 60
                                val totM = routePlan.totalEstimatedTimeMinutes % 60
                                val dispTime = if (totH > 0) "${totH}h ${totM}m" else "${totM} min"
                                Text(
                                    text = dispTime,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // "Why this stop?" Expandable
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isWhyExpanded = !isWhyExpanded },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Why this stop?",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricLime
                        )
                        Icon(
                            imageVector = if (isWhyExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = ElectricLime,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    AnimatedVisibility(visible = isWhyExpanded) {
                        Column {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = routePlan.whyExplanation,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    PrimaryButton(
                        text = if (isVoltShare) "Reserve Ananya's Charger (₹84)" else "Reserve Plug at ChargeGrid",
                        onClick = { onReserveStop(recCharger) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Map Toggle / Preview
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showMapPreview = !showMapPreview }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = ElectricLime)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (showMapPreview) "Hide Interactive Map View" else "View Interactive Route & Charger Pins",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Icon(
                        imageVector = if (showMapPreview) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }

            if (showMapPreview) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp))
                ) {
                    MapContainer(
                        chargers = allChargers,
                        selectedCharger = recCharger,
                        onSelectCharger = {},
                        onReserveCharger = onReserveStop,
                        showRouteToNandiHills = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ConditionPill(label: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkBorderSubtle, RoundedCornerShape(10.dp))
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StatItem(title: String, value: String) {
    Column {
        Text(text = title, fontSize = 10.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
