package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WithdrawalRecord
import com.example.ui.theme.*
import com.example.viewmodel.DasaniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawScreen(
    viewModel: DasaniViewModel,
    onBack: () -> Unit
) {
    val userAccount by viewModel.userAccount.collectAsState()
    val withdrawalHistory by viewModel.withdrawalHistory.collectAsState()
    val tutorialVideo by viewModel.tutorialVideo.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Withdraw, 1: Records
    var amountInput by remember { mutableStateOf("") }
    var useBankByChoice by remember { mutableStateOf(false) } // Default to direct UPI withdrawal

    var bankName by remember { mutableStateOf(userAccount.boundBankName) }
    var accountNo by remember { mutableStateOf(userAccount.boundAccountNo) }
    var holderName by remember { mutableStateOf(userAccount.boundHolderName) }
    var ifscCode by remember { mutableStateOf(userAccount.boundIfsc) }
    var upiId by remember { mutableStateOf(userAccount.boundUpiId) }
    LaunchedEffect(userAccount.boundUpiId) {
        if (userAccount.boundUpiId.isNotBlank()) {
            upiId = userAccount.boundUpiId
        }
    }
    var withdrawPasswordInput by remember { mutableStateOf("") }
    var withdrawPassVisible by remember { mutableStateOf(false) }

    // Video Player state
    var isVideoPlaying by remember { mutableStateOf(false) }
    var showEditVideoDialog by remember { mutableStateOf(false) }
    var editVideoUrl by remember { mutableStateOf(tutorialVideo.videoUrl) }
    var editVideoTitle by remember { mutableStateOf(tutorialVideo.title) }

    val amountDouble = amountInput.toDoubleOrNull() ?: 0.0
    val fee = amountDouble * 0.05
    val netReceive = (amountDouble - fee).coerceAtLeast(0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Withdraw Funds", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F2624)
                )
            )
        },
        containerColor = DasaniBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector: Withdraw | Withdrawal Records
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F1E1D))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TabPill(
                    title = "Withdraw",
                    selected = activeTab == 0,
                    modifier = Modifier.weight(1f),
                    onClick = { activeTab = 0 }
                )
                TabPill(
                    title = "Withdrawal Records",
                    selected = activeTab == 1,
                    modifier = Modifier.weight(1f),
                    onClick = { activeTab = 1 }
                )
            }

            if (activeTab == 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // Withdrawable Balance Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF132826)
                            ),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Withdrawable Balance", color = DasaniTextSecondary, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "₹ ${"%,.2f".format(userAccount.walletBalance)}",
                                        color = DasaniPrimary,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "Min withdrawal: ₹200 | 24/7 Fast Payout",
                                        color = DasaniTextMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        amountInput = userAccount.walletBalance.toInt().toString()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DasaniPrimary.copy(alpha = 0.2f),
                                        contentColor = DasaniPrimary
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("All In", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // Withdrawal Amount Field
                    item {
                        Text(
                            text = "Withdrawal Amount",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = amountInput,
                            onValueChange = { amountInput = it.filter { ch -> ch.isDigit() } },
                            placeholder = { Text("Enter Amount (Min ₹200)", color = Color(0xFF6B7D82)) },
                            prefix = { Text("₹ ", color = DasaniPrimary, fontWeight = FontWeight.Bold) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF142423),
                                unfocusedContainerColor = Color(0xFF142423),
                                focusedBorderColor = DasaniPrimary,
                                unfocusedBorderColor = DasaniCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_amount_input")
                        )

                        if (amountDouble > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("5% Tax / Fee: ₹ ${"%,.2f".format(fee)}", color = DasaniTextSecondary, fontSize = 12.sp)
                                Text("Net To Receive: ₹ ${"%,.2f".format(netReceive)}", color = DasaniSuccess, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Payout Destination Choice
                    item {
                        Text(
                            text = "Receiving Account",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = !useBankByChoice,
                                onClick = { useBankByChoice = false },
                                label = { Text("Direct UPI ID (Instant)") },
                                leadingIcon = {
                                    Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DasaniPrimary,
                                    selectedLabelColor = Color(0xFF041C1A)
                                )
                            )

                            FilterChip(
                                selected = useBankByChoice,
                                onClick = { useBankByChoice = true },
                                label = { Text("Bank Account") },
                                leadingIcon = {
                                    Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DasaniPrimary,
                                    selectedLabelColor = Color(0xFF041C1A)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (useBankByChoice) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = holderName,
                                    onValueChange = { holderName = it },
                                    label = { Text("Account Holder Name") },
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFF142423),
                                        unfocusedContainerColor = Color(0xFF142423),
                                        focusedBorderColor = DasaniPrimary,
                                        unfocusedBorderColor = DasaniCardBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = accountNo,
                                    onValueChange = { accountNo = it },
                                    label = { Text("Bank Account Number") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFF142423),
                                        unfocusedContainerColor = Color(0xFF142423),
                                        focusedBorderColor = DasaniPrimary,
                                        unfocusedBorderColor = DasaniCardBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedTextField(
                                        value = bankName,
                                        onValueChange = { bankName = it },
                                        label = { Text("Bank Name") },
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color(0xFF142423),
                                            unfocusedContainerColor = Color(0xFF142423),
                                            focusedBorderColor = DasaniPrimary,
                                            unfocusedBorderColor = DasaniCardBorder,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )

                                    OutlinedTextField(
                                        value = ifscCode,
                                        onValueChange = { ifscCode = it.uppercase() },
                                        label = { Text("IFSC Code") },
                                        shape = RoundedCornerShape(10.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = Color(0xFF142423),
                                            unfocusedContainerColor = Color(0xFF142423),
                                            focusedBorderColor = DasaniPrimary,
                                            unfocusedBorderColor = DasaniCardBorder,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF162D27)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DasaniPrimary.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = DasaniPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Payout UPI ID",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Funds will be credited directly to your linked UPI ID",
                                            color = DasaniTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = upiId,
                                onValueChange = { upiId = it.trim() },
                                label = { Text("Receiving UPI ID") },
                                leadingIcon = {
                                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = DasaniPrimary)
                                },
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF142423),
                                    unfocusedContainerColor = Color(0xFF142423),
                                    focusedBorderColor = DasaniPrimary,
                                    unfocusedBorderColor = DasaniCardBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("withdraw_upi_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Withdrawal Password Input
                        OutlinedTextField(
                            value = withdrawPasswordInput,
                            onValueChange = { withdrawPasswordInput = it },
                            label = { Text("Withdrawal Password (PIN)") },
                            trailingIcon = {
                                IconButton(onClick = { withdrawPassVisible = !withdrawPassVisible }) {
                                    Icon(
                                        imageVector = if (withdrawPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = DasaniPrimary
                                    )
                                }
                            },
                            visualTransformation = if (withdrawPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF142423),
                                unfocusedContainerColor = Color(0xFF142423),
                                focusedBorderColor = DasaniPrimary,
                                unfocusedBorderColor = DasaniCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_password_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Withdrawal Button
                        Button(
                            onClick = {
                                val amt = amountDouble
                                viewModel.submitWithdrawal(
                                    amount = amt,
                                    bankName = bankName,
                                    accountNo = accountNo,
                                    holderName = holderName,
                                    ifsc = ifscCode,
                                    upiId = if (useBankByChoice) "" else upiId,
                                    withdrawalPasswordInput = withdrawPasswordInput,
                                    videoProofUrl = tutorialVideo.videoUrl
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_withdrawal_button"),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DasaniPrimary,
                                contentColor = Color(0xFF041C1A)
                            )
                        ) {
                            Icon(Icons.Default.CurrencyExchange, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirm Withdrawal", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // WITHDRAWAL VIDEO TUTORIAL & PROOF SECTION
                    // (Directly fulfilling user request: "aur withdrawal mein Ham sab uska video bhi Dal sakte")
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF0F2220)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DasaniPrimary.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.PlayCircle,
                                            contentDescription = null,
                                            tint = DasaniPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Withdrawal Video Guide & Proof",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    TextButton(onClick = { showEditVideoDialog = true }) {
                                        Icon(Icons.Default.Edit, contentDescription = null, tint = DasaniPrimary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Update Video", color = DasaniPrimary, fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Interactive Video Player Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color(0xFF021B19), Color(0xFF082E2B))
                                            )
                                        )
                                        .clickable { isVideoPlaying = !isVideoPlaying },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isVideoPlaying) DasaniSuccess else DasaniPrimary,
                                            modifier = Modifier.size(54.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = if (isVideoPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                    contentDescription = "Play withdrawal tutorial",
                                                    tint = Color(0xFF041C1A),
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = if (isVideoPlaying) "▶ Playing Guide..." else "Watch Withdrawal Guide Video",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            text = "Duration: ${tutorialVideo.duration} • 100% Real Bank Payouts",
                                            color = DasaniTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }

                                    // Playback Progress Bar on Bottom
                                    if (isVideoPlaying) {
                                        LinearProgressIndicator(
                                            progress = { 0.45f },
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .fillMaxWidth()
                                                .height(4.dp),
                                            color = DasaniPrimary,
                                            trackColor = Color(0xFF041C1A),
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Video Explanation Cards
                                Text(
                                    text = tutorialVideo.description,
                                    color = DasaniTextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF152A28), RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = DasaniSuccess,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "100% Safe & Instant Gateway • Payout arrives in 10-30 mins",
                                        color = DasaniSuccess,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Withdrawal Records Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    if (withdrawalHistory.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No withdrawal records yet", color = DasaniTextSecondary)
                            }
                        }
                    } else {
                        items(withdrawalHistory.size) { index ->
                            val record = withdrawalHistory[index]
                            WithdrawalRecordItem(record)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        // Edit Video Proof Link Dialog
        if (showEditVideoDialog) {
            AlertDialog(
                onDismissRequest = { showEditVideoDialog = false },
                containerColor = Color(0xFF132221),
                title = {
                    Text("Add / Update Video Proof URL", color = Color.White, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Enter the video link (YouTube, MP4, or cloud link) for withdrawal proof and tutorial:",
                            color = DasaniTextSecondary,
                            fontSize = 13.sp
                        )
                        OutlinedTextField(
                            value = editVideoTitle,
                            onValueChange = { editVideoTitle = it },
                            label = { Text("Video Title") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF192C2A),
                                unfocusedContainerColor = Color(0xFF192C2A),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editVideoUrl,
                            onValueChange = { editVideoUrl = it },
                            label = { Text("Video Proof URL") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF192C2A),
                                unfocusedContainerColor = Color(0xFF192C2A),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateWithdrawalVideo(
                                title = editVideoTitle,
                                url = editVideoUrl,
                                desc = "Custom uploaded withdrawal guide & video proof. Learn how to verify payouts and bank credits."
                            )
                            showEditVideoDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DasaniPrimary, contentColor = Color(0xFF041C1A))
                    ) {
                        Text("Save Video", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditVideoDialog = false }) {
                        Text("Cancel", color = DasaniTextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
fun WithdrawalRecordItem(record: WithdrawalRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF142423))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order: ${record.orderNo}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "-₹ ${record.amount.toLong()}",
                    color = DasaniPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Method: ${record.payoutMethod}",
                    color = DasaniTextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "Net: ₹ ${"%,.2f".format(record.finalAmount)}",
                    color = DasaniSuccess,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = record.date,
                    color = DasaniTextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = "Status: ${record.status}",
                    color = DasaniSuccess,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
