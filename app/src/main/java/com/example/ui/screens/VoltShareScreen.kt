package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Booking
import com.example.model.Charger
import com.example.ui.components.GlassCard
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecondaryButton
import com.example.ui.components.SectionHeader
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
fun VoltShareScreen(
    listings: List<Charger>,
    onBookSession: (Charger, String, Int) -> Booking,
    onAddListing: (String, Double, Double, String, List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var activeDetailListing by remember { mutableStateOf<Charger?>(null) }
    var bookingCharger by remember { mutableStateOf<Charger?>(null) }
    var confirmedBooking by remember { mutableStateOf<Booking?>(null) }
    var showHostDialog by remember { mutableStateOf(false) }

    val filteredListings = remember(listings, searchQuery, selectedFilter) {
        listings.filter { listing ->
            val matchQuery = listing.name.contains(searchQuery, ignoreCase = true) ||
                    listing.hostName.contains(searchQuery, ignoreCase = true) ||
                    listing.address.contains(searchQuery, ignoreCase = true)
            val matchFilter = when (selectedFilter) {
                "Solar" -> listing.amenities.any { it.contains("Solar", ignoreCase = true) }
                "Under ₹10" -> listing.pricePerKwh <= 10.0
                "7kW+" -> listing.powerKw >= 7.0
                else -> true
            }
            matchQuery && matchFilter
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VoltShare Hub",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Peer-to-peer EV home charging network",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                // "List Charger" Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(ElectricLime)
                        .clickable { showHostDialog = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = DarkBg, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Host", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBg)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by locality, host, or charger...", color = TextSecondary, fontSize = 13.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = ElectricLime) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricLime,
                    unfocusedBorderColor = DarkBorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurfaceVariant,
                    unfocusedContainerColor = DarkSurfaceVariant
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Under ₹10", "Solar", "7kW+").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ElectricLime else DarkSurfaceVariant)
                            .border(1.dp, if (isSelected) ElectricLime else DarkBorderSubtle, RoundedCornerShape(12.dp))
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) DarkBg else TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Marketplace Listings
            Text(
                text = "COMMUNITY CHARGERS NEAR YOU",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            filteredListings.forEach { listing ->
                VoltShareListingCard(
                    charger = listing,
                    onViewDetail = { activeDetailListing = listing },
                    onBookNow = { bookingCharger = listing }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Listing Detail Modal
        if (activeDetailListing != null) {
            ListingDetailSheet(
                charger = activeDetailListing!!,
                onClose = { activeDetailListing = null },
                onBook = {
                    val target = activeDetailListing!!
                    activeDetailListing = null
                    bookingCharger = target
                }
            )
        }

        // Booking Modal (60 min default, price breakdown, mock Stripe checkout)
        if (bookingCharger != null) {
            BookingModal(
                charger = bookingCharger!!,
                onClose = { bookingCharger = null },
                onConfirm = { slot, durationMin ->
                    val booking = onBookSession(bookingCharger!!, slot, durationMin)
                    bookingCharger = null
                    confirmedBooking = booking
                }
            )
        }

        // Booking Confirmation Screen (with QR code)
        if (confirmedBooking != null) {
            BookingConfirmationModal(
                booking = confirmedBooking!!,
                onClose = { confirmedBooking = null }
            )
        }

        // Host Listing Dialog
        if (showHostDialog) {
            HostListingDialog(
                onClose = { showHostDialog = false },
                onSubmit = { name, power, price, addr, amens ->
                    onAddListing(name, power, price, addr, amens)
                    showHostDialog = false
                }
            )
        }
    }
}

@Composable
fun VoltShareListingCard(
    charger: Charger,
    onViewDetail: () -> Unit,
    onBookNow: () -> Unit
) {
    GlassCard(
        borderColor = ElectricLime.copy(alpha = 0.35f),
        backgroundColor = DarkSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewDetail)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF132742)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = ElectricLime, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = charger.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (charger.isHostVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "Hosted by ${charger.hostName} • ${charger.detourKm} km away",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Price pill
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${charger.pricePerKwh.toInt()}/kWh",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricLime
                    )
                    Text(
                        text = "~₹${(charger.powerKw * charger.pricePerKwh).toInt()}/hr",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Specs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Power: ${charger.powerKw} kW", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text(text = "Type: ${charger.connectorType}", fontSize = 12.sp, color = TextSecondary)
                Text(text = "${charger.rating} ★ (${charger.reviewCount})", fontSize = 12.sp, color = WarningAmber, fontWeight = FontWeight.Bold)
                Text(text = "0 min wait", fontSize = 12.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryButton(
                    text = "Details & Rules",
                    onClick = onViewDetail,
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    text = "Book Session",
                    onClick = onBookNow,
                    modifier = Modifier.weight(1.2f)
                )
            }
        }
    }
}

@Composable
fun ListingDetailSheet(
    charger: Charger,
    onClose: () -> Unit,
    onBook: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(onClick = onClose)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(DarkSurface)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .clickable(enabled = false) {}
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = charger.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Text(text = "Hosted by ${charger.hostName} • Verified Resident", fontSize = 13.sp, color = EmeraldGreen)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Address: ${charger.address}", fontSize = 13.sp, color = TextSecondary)

                Spacer(modifier = Modifier.height(16.dp))

                // House rules
                Text(text = "HOUSE RULES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = charger.houseRules, fontSize = 13.sp, color = TextPrimary, lineHeight = 18.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // Amenities
                Text(text = "AMENITIES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    charger.amenities.forEach { amenity ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = amenity, fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(
                    text = "Proceed to Booking (₹${charger.pricePerKwh.toInt()}/kWh)",
                    onClick = onBook,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun BookingModal(
    charger: Charger,
    onClose: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var selectedDurationMin by remember { mutableIntStateOf(60) }
    var selectedSlot by remember { mutableStateOf("2:30 PM - 3:30 PM") }
    var isCheckingOut by remember { mutableStateOf(false) }

    val energyKwh = charger.powerKw * (selectedDurationMin / 60.0)
    val energyCost = energyKwh * charger.pricePerKwh
    val platformFee = 12.0
    val totalCost = energyCost + platformFee

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(onClick = onClose)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(DarkSurface)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .clickable(enabled = false) {}
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Reserve Charging Session", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Text(text = "${charger.name} • ${charger.hostName}", fontSize = 13.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(16.dp))

                // Time Slots
                Text(text = "SELECT TIME SLOT (TODAY)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                listOf("2:30 PM - 3:30 PM", "3:30 PM - 4:30 PM", "4:30 PM - 5:30 PM").forEach { slot ->
                    val isSelected = selectedSlot == slot
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ElectricLime.copy(alpha = 0.15f) else DarkSurfaceVariant)
                            .border(1.dp, if (isSelected) ElectricLime else DarkBorderSubtle, RoundedCornerShape(12.dp))
                            .clickable { selectedSlot = slot }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = slot, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isSelected) ElectricLime else TextPrimary)
                            if (isSelected) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = ElectricLime, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Duration Selector
                Text(text = "DURATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(30 to "30 Min", 60 to "60 Min (Default)", 90 to "90 Min").forEach { (duration, label) ->
                        val isSelected = selectedDurationMin == duration
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ElectricLime else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) ElectricLime else DarkBorderSubtle, RoundedCornerShape(10.dp))
                                .clickable { selectedDurationMin = duration }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) DarkBg else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price Breakdown Card
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(text = "PRICE BREAKDOWN (STRIPE TEST ARCHITECTURE)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Est. Energy (${String.format("%.1f", energyKwh)} kWh @ ₹${charger.pricePerKwh.toInt()}/kWh)", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "₹${energyCost.toInt()}", fontSize = 12.sp, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "VoltShare Platform Fee", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "₹${platformFee.toInt()}", fontSize = 12.sp, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkBorderSubtle))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Total Session Cost", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "₹${totalCost.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = ElectricLime)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isCheckingOut) {
                    Box(modifier = Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ElectricLime, modifier = Modifier.size(28.dp))
                    }
                } else {
                    PrimaryButton(
                        text = "Confirm & Pay ₹${totalCost.toInt()} (Mock Checkout)",
                        onClick = {
                            isCheckingOut = true
                            onConfirm(selectedSlot, selectedDurationMin)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun BookingConfirmationModal(
    booking: Booking,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(onClick = onClose)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(DarkSurface)
                .border(1.dp, ElectricLime, RoundedCornerShape(24.dp))
                .clickable(enabled = false) {}
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Booking Confirmed!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Reservation code sent to ${booking.hostName}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Simulated QR Code Graphic
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val step = size.width / 7
                        // Simple stylized QR pattern
                        for (i in 0..6) {
                            for (j in 0..6) {
                                if ((i + j) % 2 == 0 || (i in 0..1 && j in 0..1) || (i in 5..6 && j in 0..1)) {
                                    drawRect(
                                        color = Color.Black,
                                        topLeft = Offset(i * step, j * step),
                                        size = Size(step * 0.9f, step * 0.9f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = booking.bookingCode,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = ElectricLime,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Details Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceVariant)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "Host: ${booking.hostName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "Time: ${booking.timeSlotText}", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "Address: ${booking.address}", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "Free cancellation up to 15 min prior", fontSize = 11.sp, color = EmeraldGreen)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    text = "Done & View in Profile",
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun HostListingDialog(
    onClose: () -> Unit,
    onSubmit: (String, Double, Double, String, List<String>) -> Unit
) {
    var chargerName by remember { mutableStateOf("Maya's Home Wallbox") }
    var powerKw by remember { mutableStateOf("7.2") }
    var pricePerKwh by remember { mutableStateOf("10.0") }
    var address by remember { mutableStateOf("12 Koramangala 4th Block, Bengaluru") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(onClick = onClose)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(DarkSurface)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(24.dp))
                .clickable(enabled = false) {}
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "List Your Home Charger", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Text(text = "Earn passive income by sharing your home EV charger.", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = chargerName,
                    onValueChange = { chargerName = it },
                    label = { Text("Listing Title", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricLime, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = powerKw,
                        onValueChange = { powerKw = it },
                        label = { Text("Power (kW)", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricLime, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = pricePerKwh,
                        onValueChange = { pricePerKwh = it },
                        label = { Text("Price (₹/kWh)", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricLime, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address / Neighborhood", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricLime, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    text = "Publish Listing",
                    onClick = {
                        val p = powerKw.toDoubleOrNull() ?: 7.2
                        val pr = pricePerKwh.toDoubleOrNull() ?: 10.0
                        onSubmit(chargerName, p, pr, address, listOf("Gated Parking", "Wi-Fi", "CCTV"))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
