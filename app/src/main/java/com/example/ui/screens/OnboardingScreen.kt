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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.SeedData
import com.example.model.RoutePreference
import com.example.model.Vehicle
import com.example.ui.components.GlassCard
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecondaryButton
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
fun OnboardingScreen(
    onFinished: (Vehicle, String, RoutePreference, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) }

    // Step 1: Vehicle selection
    var selectedVehicle by remember { mutableStateOf(SeedData.demoVehicle) }
    var isCustomVehicle by remember { mutableStateOf(false) }
    var customModelName by remember { mutableStateOf("") }
    var customBatteryKwh by remember { mutableStateOf("50") }

    // Step 2: Location
    var homeCity by remember { mutableStateOf("Bengaluru") }
    var hasHomeCharger by remember { mutableStateOf(false) }

    // Step 3: Preferences
    var selectedPreference by remember { mutableStateOf(RoutePreference.CHEAPEST) }
    var safetyReservePercent by remember { mutableIntStateOf(15) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Back button & Step Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1) {
                    IconButton(onClick = { step -= 1 }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                // Step Dots
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (1..3).forEach { s ->
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (s == step) 28.dp else 10.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (s <= step) ElectricLime else Color(0xFF1D3557))
                        )
                    }
                }

                Text(
                    text = "Step $step of 3",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (step) {
                1 -> {
                    // STEP 1: VEHICLE
                    Text(
                        text = "Choose Your Electric Vehicle",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "VoltMate uses your vehicle battery chemistry & aerodynamics for physics-informed range prediction.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    SeedData.altVehicles.forEach { vehicle ->
                        val isSelected = !isCustomVehicle && selectedVehicle.id == vehicle.id
                        GlassCard(
                            borderColor = if (isSelected) ElectricLime else DarkCardBorder,
                            backgroundColor = if (isSelected) Color(0xFF102738) else DarkSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isCustomVehicle = false
                                    selectedVehicle = vehicle
                                }
                                .padding(vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) ElectricLime else Color(0xFF162D4A)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsCar,
                                            contentDescription = null,
                                            tint = if (isSelected) DarkBg else TextPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "${vehicle.make} ${vehicle.model}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${vehicle.batteryCapacityKwh} kWh • ${vehicle.connectorType}",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(ElectricLime),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = DarkBg,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Custom Vehicle Option
                    GlassCard(
                        borderColor = if (isCustomVehicle) ElectricLime else DarkCardBorder,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isCustomVehicle = true }
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(if (isCustomVehicle) ElectricLime else Color(0xFF162D4A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCustomVehicle) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = DarkBg,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Custom / Other EV Model",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            if (isCustomVehicle) {
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = customModelName,
                                    onValueChange = { customModelName = it },
                                    label = { Text("Model Name (e.g. MG ZS EV)", color = TextSecondary) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElectricLime,
                                        unfocusedBorderColor = DarkBorderSubtle,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = customBatteryKwh,
                                    onValueChange = { customBatteryKwh = it },
                                    label = { Text("Battery Capacity (kWh)", color = TextSecondary) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElectricLime,
                                        unfocusedBorderColor = DarkBorderSubtle,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    PrimaryButton(
                        text = "Continue to Location",
                        onClick = {
                            if (isCustomVehicle && customModelName.isNotBlank()) {
                                selectedVehicle = Vehicle(
                                    id = "veh-custom",
                                    make = "Custom",
                                    model = customModelName,
                                    batteryCapacityKwh = customBatteryKwh.toDoubleOrNull() ?: 50.0,
                                    currentSocPercent = 50,
                                    baselineEfficiencyWhPerKm = 150.0,
                                    connectorType = "CCS2"
                                )
                            }
                            step = 2
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                2 -> {
                    // STEP 2: LOCATION
                    Text(
                        text = "Your Home Base",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "VoltMate optimizes your morning commute and shows peer-to-peer chargers near you.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = homeCity,
                        onValueChange = { homeCity = it },
                        label = { Text("Home City", color = TextSecondary) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = ElectricLime)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricLime,
                            unfocusedBorderColor = DarkBorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SecondaryButton(
                        text = "Use Current Location (Bengaluru)",
                        onClick = { homeCity = "Bengaluru" },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Home Charger Question
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(
                                text = "Do you have a dedicated charger at home?",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "You can earn passive income by sharing it on VoltShare when not in use.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (hasHomeCharger) ElectricLime else Color(0xFF13253E))
                                        .clickable { hasHomeCharger = true }
                                        .padding(horizontal = 20.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "Yes, I have one",
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasHomeCharger) DarkBg else TextPrimary,
                                        fontSize = 13.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (!hasHomeCharger) ElectricLime else Color(0xFF13253E))
                                        .clickable { hasHomeCharger = false }
                                        .padding(horizontal = 20.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "No, street / public",
                                        fontWeight = FontWeight.Bold,
                                        color = if (!hasHomeCharger) DarkBg else TextPrimary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    PrimaryButton(
                        text = "Continue to Preferences",
                        onClick = { step = 3 },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                3 -> {
                    // STEP 3: PREFERENCES
                    Text(
                        text = "Charging Priorities",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tailor how VoltMate balances detour time, electricity tariffs, and grid carbon emissions.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    listOf(
                        Triple(RoutePreference.CHEAPEST, "Cheapest (Recommended)", "Prioritizes low tariffs and VoltShare home chargers (avg 40% savings)"),
                        Triple(RoutePreference.FASTEST, "Fastest", "Prioritizes high-power 50kW+ DC Fast chargers with minimal detour time"),
                        Triple(RoutePreference.GREENEST, "Greenest", "Prioritizes solar-buffered and low-carbon grid charging stations")
                    ).forEach { (pref, title, desc) ->
                        val isSelected = selectedPreference == pref
                        GlassCard(
                            borderColor = if (isSelected) ElectricLime else DarkCardBorder,
                            backgroundColor = if (isSelected) Color(0xFF102738) else DarkSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPreference = pref }
                                .padding(vertical = 6.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) ElectricLime else TextPrimary
                                    )
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(ElectricLime),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = DarkBg,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = desc,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Safety Reserve Slider
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Safe Arrival Reserve Buffer",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "$safetyReservePercent%",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = WarningAmber
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "VoltMate warns you whenever arrival battery is projected to fall below this buffer.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Slider(
                                value = safetyReservePercent.toFloat(),
                                onValueChange = { safetyReservePercent = it.toInt() },
                                valueRange = 10f..30f,
                                steps = 3,
                                colors = SliderDefaults.colors(
                                    thumbColor = ElectricLime,
                                    activeTrackColor = ElectricLime,
                                    inactiveTrackColor = Color(0xFF1D3557)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    PrimaryButton(
                        text = "Start driving smarter",
                        onClick = {
                            onFinished(selectedVehicle, homeCity, selectedPreference, safetyReservePercent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
