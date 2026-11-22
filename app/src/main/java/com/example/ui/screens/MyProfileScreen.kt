package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import com.example.R
import com.example.ui.theme.*
import com.example.viewmodel.DasaniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    viewModel: DasaniViewModel,
    onNavigateToRecharge: () -> Unit,
    onNavigateToWithdraw: () -> Unit,
    onNavigateToDevices: () -> Unit,
    onNavigateToTeam: () -> Unit,
    onNavigateToService: () -> Unit,
    onLogout: () -> Unit
) {
    val userAccount by viewModel.userAccount.collectAsState()
    val scrollState = rememberScrollState()

    var showBankDialog by remember { mutableStateOf(false) }
    var showUpiDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Account", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F2624))
            )
        },
        containerColor = DasaniBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Profile Info Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF132826))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(2.dp, DasaniPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.galaxy_circle_logo),
                            contentDescription = "User Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val displayTitle = when {
                                userAccount.boundUpiId.isNotBlank() -> userAccount.boundUpiId
                                userAccount.phone.length >= 4 -> "Galaxy Member (••••${userAccount.phone.takeLast(4)})"
                                else -> "Galaxy Member"
                            }
                            Text(
                                text = displayTitle,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DasaniAccentGold
                            ) {
                                Text(
                                    text = "VIP 1",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Linked UPI: ${userAccount.boundUpiId.ifBlank { "Not Linked" }}",
                            color = DasaniPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Invitation Code: ${userAccount.inviteCode}",
                            color = DasaniTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Assets & Income Overview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101F1E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, DasaniPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Wallet Balance", color = DasaniTextSecondary, fontSize = 12.sp)
                            Text(
                                text = "₹ ${"%,.2f".format(userAccount.walletBalance)}",
                                color = DasaniPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onNavigateToRecharge,
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DasaniPrimary, contentColor = Color(0xFF041C1A)),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Recharge", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onNavigateToWithdraw,
                                shape = RoundedCornerShape(18.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DasaniPrimary),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Withdraw", color = DasaniPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = DasaniCardBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Income", color = DasaniTextSecondary, fontSize = 11.sp)
                            Text("₹ ${userAccount.totalIncome.toLong()}", color = DasaniSuccess, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Column {
                            Text("Total Recharged", color = DasaniTextSecondary, fontSize = 11.sp)
                            Text("₹ ${userAccount.totalRecharged.toLong()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Withdrawn", color = DasaniTextSecondary, fontSize = 11.sp)
                            Text("₹ ${userAccount.totalWithdrawn.toLong()}", color = DasaniPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickProfileNavButton(
                    title = "Devices",
                    icon = Icons.Default.PrecisionManufacturing,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToDevices
                )
                QuickProfileNavButton(
                    title = "Team",
                    icon = Icons.Default.Groups,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTeam
                )
                QuickProfileNavButton(
                    title = "Service",
                    icon = Icons.Default.HeadsetMic,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToService
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Account Settings List
            Text(
                text = "Account Security & Settings",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF142423))
            ) {
                Column {
                    ProfileMenuRow(
                        title = "Bind / Change Payout UPI ID",
                        subtitle = userAccount.boundUpiId.ifBlank { "Tap to link or change your UPI ID" },
                        icon = Icons.Default.QrCode,
                        onClick = { showUpiDialog = true }
                    )
                    HorizontalDivider(color = DasaniCardBorder, modifier = Modifier.padding(horizontal = 14.dp))

                    ProfileMenuRow(
                        title = "Bind Bank Account",
                        subtitle = if (userAccount.boundBankName.isNotBlank()) {
                            "${userAccount.boundBankName} (${userAccount.boundAccountNo.takeLast(4)})"
                        } else {
                            "Tap to link your bank account"
                        },
                        icon = Icons.Default.AccountBalance,
                        onClick = { showBankDialog = true }
                    )
                    HorizontalDivider(color = DasaniCardBorder, modifier = Modifier.padding(horizontal = 14.dp))

                    ProfileMenuRow(
                        title = "Withdrawal Tutorial & Proof Video",
                        subtitle = "Watch step-by-step video instructions",
                        icon = Icons.Default.PlayCircle,
                        onClick = onNavigateToWithdraw
                    )
                    HorizontalDivider(color = DasaniCardBorder, modifier = Modifier.padding(horizontal = 14.dp))

                    ProfileMenuRow(
                        title = "Customer Care Service",
                        subtitle = "24/7 dedicated assistance",
                        icon = Icons.Default.SupportAgent,
                        onClick = onNavigateToService
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("logout_button"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF261919),
                    contentColor = DasaniError
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, DasaniError.copy(alpha = 0.5f))
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout from Account", fontWeight = FontWeight.Bold)
            }
        }

        // Bank Card Binding Dialog
        if (showBankDialog) {
            var bName by remember { mutableStateOf(userAccount.boundBankName) }
            var accNo by remember { mutableStateOf(userAccount.boundAccountNo) }
            var hName by remember { mutableStateOf(userAccount.boundHolderName) }
            var ifsc by remember { mutableStateOf(userAccount.boundIfsc) }

            AlertDialog(
                onDismissRequest = { showBankDialog = false },
                containerColor = Color(0xFF132221),
                title = { Text("Bind Bank Account", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = hName,
                            onValueChange = { hName = it },
                            label = { Text("Holder Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = bName,
                            onValueChange = { bName = it },
                            label = { Text("Bank Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = accNo,
                            onValueChange = { accNo = it },
                            label = { Text("Account Number") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = ifsc,
                            onValueChange = { ifsc = it },
                            label = { Text("IFSC Code") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateBoundBank(hName, bName, accNo, ifsc)
                            showBankDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DasaniPrimary, contentColor = Color(0xFF041C1A))
                    ) {
                        Text("Save Details", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBankDialog = false }) {
                        Text("Cancel", color = DasaniTextSecondary)
                    }
                }
            )
        }

        if (showUpiDialog) {
            var newUpi by remember { mutableStateOf(userAccount.boundUpiId) }
            AlertDialog(
                onDismissRequest = { showUpiDialog = false },
                containerColor = Color(0xFF132221),
                title = { Text("Bind / Change UPI ID", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = newUpi,
                        onValueChange = { newUpi = it.trim() },
                        label = { Text("Your Payout UPI ID") },
                        placeholder = { Text("e.g. yourname@okaxis") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (viewModel.updateBoundUpi(newUpi)) {
                                showUpiDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DasaniPrimary, contentColor = Color(0xFF041C1A))
                    ) {
                        Text("Save UPI ID", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUpiDialog = false }) {
                        Text("Cancel", color = DasaniTextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
fun QuickProfileNavButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(72.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF142423))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = DasaniPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ProfileMenuRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(DasaniPrimary.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = DasaniPrimary, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = DasaniTextSecondary, fontSize = 11.sp)
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = DasaniTextMuted,
            modifier = Modifier.size(14.dp)
        )
    }
}
