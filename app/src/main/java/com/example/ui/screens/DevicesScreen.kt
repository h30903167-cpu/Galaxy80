package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PurchasedProduct
import com.example.ui.theme.*
import com.example.viewmodel.DasaniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
    viewModel: DasaniViewModel,
    onBack: () -> Unit,
    onExploreProducts: () -> Unit
) {
    val myDevices by viewModel.myDevices.collectAsState()
    val userAccount by viewModel.userAccount.collectAsState()

    val pendingToClaim = remember(myDevices) {
        myDevices.filter { it.canClaimToday }.sumOf { it.dailyProfit }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Devices / Equipment", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F2624))
            )
        },
        containerColor = DasaniBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Overview Summary Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF132826))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Active Equipment", color = DasaniTextSecondary, fontSize = 12.sp)
                                    Text("${myDevices.size} Units", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Total Earned", color = DasaniTextSecondary, fontSize = 12.sp)
                                    Text("₹ ${userAccount.totalIncome.toLong()}", color = DasaniSuccess, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (pendingToClaim > 0) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { viewModel.claimAllDailyProfits() },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DasaniPrimary,
                                        contentColor = Color(0xFF041C1A)
                                    )
                                ) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Collect All Today's Profit (+₹${pendingToClaim.toLong()})", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                }

                if (myDevices.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF14191C))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Agriculture,
                                    contentDescription = null,
                                    tint = DasaniPrimary,
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No Active Seeder Equipment Yet",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Purchase GL-12 Manual Seeder or VIP Seeder plans to start earning guaranteed daily profits.",
                                    color = DasaniTextSecondary,
                                    fontSize = 13.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                Button(
                                    onClick = onExploreProducts,
                                    colors = ButtonDefaults.buttonColors(containerColor = DasaniPrimary, contentColor = Color(0xFF041C1A)),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text("Browse Products", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    items(myDevices) { device ->
                        PurchasedDeviceCard(
                            device = device,
                            onClaim = { viewModel.claimDailyProfit(device.id) }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PurchasedDeviceCard(
    device: PurchasedProduct,
    onClaim: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("device_card_${device.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF142423))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = device.drawableRes),
                    contentDescription = device.name,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(device.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Purchased: ₹${device.price.toLong()}", color = DasaniTextSecondary, fontSize = 12.sp)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF00E676).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Active",
                        color = Color(0xFF00E676),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DasaniCardBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Daily Profit", color = DasaniTextSecondary, fontSize = 11.sp)
                    Text("₹ ${device.dailyProfit.toLong()}/day", color = DasaniPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column {
                    Text("Days Left", color = DasaniTextSecondary, fontSize = 11.sp)
                    Text("${device.daysRemaining}/${device.totalDays} Days", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Claimed", color = DasaniTextSecondary, fontSize = 11.sp)
                    Text("₹ ${device.totalClaimed.toLong()}", color = DasaniSuccess, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (device.canClaimToday) {
                Button(
                    onClick = onClaim,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(21.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DasaniPrimary, contentColor = Color(0xFF041C1A))
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Claim Today's Profit (₹${device.dailyProfit.toLong()})", fontWeight = FontWeight.Bold)
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(21.dp),
                    color = Color(0xFF192C2A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = DasaniSuccess, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Profit Claimed Today (Next in 24h)", color = DasaniSuccess, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
