package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MerchantUpiProvider
import com.example.model.RechargeRecord
import com.example.ui.theme.*
import com.example.viewmodel.DasaniViewModel

enum class PaymentMethodType(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val isRecommended: Boolean = false
) {
    UPI_FASTPAY(
        id = "upi",
        title = "UPI FastPay (PhonePe / GPay / Paytm)",
        subtitle = "Instant credit in 10-30 seconds • 0% Fee",
        badge = "FASTEST",
        isRecommended = true
    ),
    DYNAMIC_QR(
        id = "qr",
        title = "Dynamic Scan & Pay QR",
        subtitle = "Scan with any banking or UPI app",
        badge = "AUTO VERIFY"
    ),
    BANK_IMPS_RTGS(
        id = "bank",
        title = "Direct Bank IMPS / RTGS Transfer",
        subtitle = "Recommended for high amounts up to ₹1,00,000",
        badge = "HIGH LIMIT"
    ),
    CARD_NETBANKING(
        id = "card",
        title = "Net Banking & Debit Card Gateway",
        subtitle = "Secure 256-Bit encrypted bank gateway",
        badge = "SECURE"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechargeScreen(
    viewModel: DasaniViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userAccount by viewModel.userAccount.collectAsState()
    val rechargeHistory by viewModel.rechargeHistory.collectAsState()

    var selectedAmount by remember { mutableStateOf(300.0) }
    var customAmountText by remember { mutableStateOf("300") }
    var selectedMethod by remember { mutableStateOf(PaymentMethodType.UPI_FASTPAY) }
    var activeTab by remember { mutableStateOf(0) } // 0: Recharge, 1: Records
    var amountErrorMessage by remember { mutableStateOf<String?>(null) }

    // Maximum recharge limit up to 1 Lakh (100,000)
    val maxRechargeLimit = 100000.0
    val minRechargeLimit = 100.0

    // Preset amounts starting with ₹300, ₹500, up to 1 Lakh (₹1,00,000)
    val presetAmounts = listOf(
        300.0,
        500.0,
        1800.0,
        4900.0,
        12000.0,
        35000.0,
        50000.0,
        100000.0 // 1 Lakh (Maximum transaction limit)
    )

    var officialUpiId by remember { mutableStateOf(MerchantUpiProvider.rotateMerchantUpi(context)) }

    fun validateAmount(text: String): Double {
        val num = text.toDoubleOrNull() ?: 0.0
        amountErrorMessage = when {
            num > maxRechargeLimit -> "Maximum single recharge limit is ₹1,00,000 (1 Lakh)"
            num in 1.0..99.0 -> "Minimum recharge amount is ₹100"
            else -> null
        }
        return num
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Secure Recharge", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF00E676).copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFF00E676),
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("256-Bit SSL", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            text = "Limits: ₹100 up to ₹1,00,000 (1 Lakh)",
                            color = DasaniTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0E1F1E))
            )
        },
        containerColor = DasaniBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector: Recharge Now | Recharge Records
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F1E1D))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TabPill(
                    title = "Recharge Now",
                    selected = activeTab == 0,
                    modifier = Modifier.weight(1f),
                    onClick = { activeTab = 0 }
                )
                TabPill(
                    title = "Recharge Records",
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
                    contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp)
                ) {
                    // Security Guarantee Banner
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF102826)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DasaniPrimary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(DasaniPrimary.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = DasaniPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "NPCI & Bank Verified Payment Gateway",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Official merchant account • 100% instant UPI credit",
                                        color = DasaniTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Current Wallet Balance Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF132826)),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Current Wallet Balance", color = DasaniTextSecondary, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "₹ ${"%,.2f".format(userAccount.walletBalance)}",
                                        color = DasaniPrimary,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF1A3834)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = DasaniSuccess, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Active VIP", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // Section: Amount Selection up to 1 Lakh
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Select Recharge Amount",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Max: ₹1,00,000",
                                color = DasaniAccentGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Preset chips grid
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Row 1: 300, 500, 1800
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(300.0, 500.0, 1800.0).forEach { amt ->
                                    AmountPresetButton(
                                        amount = amt,
                                        isSelected = selectedAmount == amt,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            selectedAmount = amt
                                            customAmountText = amt.toLong().toString()
                                            validateAmount(customAmountText)
                                        }
                                    )
                                }
                            }

                            // Row 2: 4900, 12000, 35000
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(4900.0, 12000.0, 35000.0).forEach { amt ->
                                    AmountPresetButton(
                                        amount = amt,
                                        isSelected = selectedAmount == amt,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            selectedAmount = amt
                                            customAmountText = amt.toLong().toString()
                                            validateAmount(customAmountText)
                                        }
                                    )
                                }
                            }

                            // Row 3: Full-width Highlighted ₹1,00,000 (1 Lakh) Button
                            val is1Lakh = selectedAmount == 100000.0
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(58.dp)
                                    .clickable {
                                        selectedAmount = 100000.0
                                        customAmountText = "100000"
                                        validateAmount(customAmountText)
                                    }
                                    .testTag("recharge_amt_100000"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (is1Lakh) Color(0xFF2E2606) else Color(0xFF142423)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (is1Lakh) 2.dp else 1.dp,
                                    color = if (is1Lakh) DasaniAccentGold else DasaniAccentGold.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Stars,
                                            contentDescription = null,
                                            tint = DasaniAccentGold,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "₹ 1,00,000 (1 Lakh Max Limit)",
                                                color = DasaniAccentGold,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Text(
                                                text = "Maximum return rate • VIP Machine Tier",
                                                color = DasaniTextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    if (is1Lakh) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = DasaniAccentGold,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Custom Amount TextField with dynamic validation up to 1 Lakh
                        OutlinedTextField(
                            value = customAmountText,
                            onValueChange = { input ->
                                val digitsOnly = input.filter { it.isDigit() }
                                customAmountText = digitsOnly
                                val parsed = validateAmount(digitsOnly)
                                if (parsed <= maxRechargeLimit) {
                                    selectedAmount = parsed
                                }
                            },
                            label = { Text("Enter Custom Amount (Up to ₹1,00,000)") },
                            prefix = { Text("₹ ", color = DasaniPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            isError = amountErrorMessage != null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF142423),
                                unfocusedContainerColor = Color(0xFF142423),
                                focusedBorderColor = DasaniPrimary,
                                unfocusedBorderColor = DasaniCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                errorBorderColor = DasaniError
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_recharge_input")
                        )

                        amountErrorMessage?.let { err ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = err,
                                color = DasaniError,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Section: Select Payment Method
                    item {
                        Text(
                            text = "Select Payment Method",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PaymentMethodType.values().forEach { method ->
                                PaymentMethodCard(
                                    method = method,
                                    isSelected = selectedMethod == method,
                                    onSelect = {
                                        selectedMethod = method
                                        if (method == PaymentMethodType.UPI_FASTPAY || method == PaymentMethodType.DYNAMIC_QR) {
                                            officialUpiId = MerchantUpiProvider.rotateMerchantUpi(context)
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // Dynamic Payment Gateway Details Section
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF10201F)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DasaniPrimary.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Payment Checkout Details",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DasaniPrimary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "Recharge: ₹${"%,d".format(selectedAmount.toLong())}",
                                            color = DasaniPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                when (selectedMethod) {
                                    PaymentMethodType.UPI_FASTPAY, PaymentMethodType.DYNAMIC_QR -> {
                                        // Official Merchant UPI ID Copy & Rotate Box
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Merchant UPI Payment:",
                                                color = DasaniPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            OutlinedButton(
                                                onClick = {
                                                    officialUpiId = MerchantUpiProvider.rotateMerchantUpi(context)
                                                    viewModel.showToast("Switched UPI: $officialUpiId")
                                                },
                                                shape = RoundedCornerShape(14.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, DasaniPrimary.copy(alpha = 0.6f)),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                modifier = Modifier.height(30.dp)
                                            ) {
                                                Icon(Icons.Default.Refresh, contentDescription = "Change UPI ID", tint = DasaniPrimary, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Change UPI", fontSize = 11.sp, color = DasaniPrimary, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFF182D2B), RoundedCornerShape(10.dp))
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Assigned Merchant UPI ID", color = DasaniTextSecondary, fontSize = 11.sp)
                                                Text(
                                                    text = officialUpiId,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    modifier = Modifier.testTag("merchant_upi_id_text")
                                                )
                                            }

                                            Button(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("Galaxy UPI", officialUpiId))
                                                    viewModel.showToast("Merchant UPI copied: $officialUpiId")
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = DasaniPrimary,
                                                    contentColor = Color(0xFF041C1A)
                                                ),
                                                shape = RoundedCornerShape(14.dp),
                                                modifier = Modifier.height(32.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Copy UPI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // UPI Apps Quick Launcher (PhonePe, GPay, Paytm, BHIM)
                                        Text(
                                            text = "Step 1: Pay via UPI App:",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            listOf(
                                                Triple("PhonePe", "phonepe", "pay"),
                                                Triple("GPay", "tez", "upi"),
                                                Triple("Paytm", "paytmmp", "pay"),
                                                Triple("BHIM", "upi", "pay")
                                            ).forEach { (appName, scheme, host) ->
                                                OutlinedButton(
                                                    onClick = {
                                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                        clipboard.setPrimaryClip(ClipData.newPlainText("Galaxy UPI", officialUpiId))
                                                        val appUri = if (appName == "GPay") {
                                                            MerchantUpiProvider.buildUpiPaymentUri(officialUpiId, selectedAmount, "tez", "upi")
                                                                .buildUpon().appendPath("pay").build()
                                                        } else {
                                                            MerchantUpiProvider.buildUpiPaymentUri(officialUpiId, selectedAmount, scheme, host)
                                                        }
                                                        val genericUri = MerchantUpiProvider.buildUpiPaymentUri(officialUpiId, selectedAmount)
                                                        try {
                                                            context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, appUri))
                                                        } catch (_: Exception) {
                                                            try {
                                                                context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, genericUri))
                                                            } catch (_: Exception) {
                                                                viewModel.showToast("UPI ID copied ($officialUpiId). Paste in $appName to pay.")
                                                            }
                                                        }
                                                    },
                                                    modifier = Modifier.weight(1f).height(36.dp),
                                                    shape = RoundedCornerShape(8.dp),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, DasaniPrimary.copy(alpha = 0.5f)),
                                                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
                                                ) {
                                                    Text(appName, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        if (selectedMethod == PaymentMethodType.DYNAMIC_QR) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.White, RoundedCornerShape(12.dp))
                                                    .padding(14.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Icon(
                                                        imageVector = Icons.Default.QrCode2,
                                                        contentDescription = "Dynamic UPI QR",
                                                        tint = Color.Black,
                                                        modifier = Modifier.size(140.dp)
                                                    )
                                                    Text(
                                                        text = "Scan to Pay ₹${"%,d".format(selectedAmount.toLong())}",
                                                        color = Color.Black,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = "UPI ID: $officialUpiId",
                                                        color = Color(0xFF00695C),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    )
                                                    Text(
                                                        text = "Works with GPay, PhonePe, Paytm & BHIM",
                                                        color = Color.Gray,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                        }

                                    }

                                    PaymentMethodType.BANK_IMPS_RTGS -> {
                                        Text(
                                            text = "High-Limit IMPS / RTGS Payment Desk",
                                            color = DasaniPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFF182D2B), RoundedCornerShape(10.dp))
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "Pay via active Merchant UPI ($officialUpiId) or contact Official Telegram Care (@GalaxySeederCare) for IMPS/RTGS bank transfer details.",
                                                color = DasaniTextSecondary,
                                                fontSize = 12.sp
                                            )
                                        }

                                    }

                                    PaymentMethodType.CARD_NETBANKING -> {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFF182D2B), RoundedCornerShape(10.dp))
                                                .padding(14.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = DasaniPrimary, modifier = Modifier.size(36.dp))
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Direct Bank Payment Gateway",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "You will be securely redirected to your bank's OTP authorization page.",
                                                color = DasaniTextSecondary,
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // Pay directly via UPI App (no Confirm Recharge button)
                                Button(
                                    onClick = {
                                        if (selectedAmount < minRechargeLimit || selectedAmount > maxRechargeLimit) {
                                            validateAmount(customAmountText)
                                            return@Button
                                        }
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Galaxy UPI", officialUpiId))
                                        val genericUri = MerchantUpiProvider.buildUpiPaymentUri(officialUpiId, selectedAmount)
                                        try {
                                            context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, genericUri))
                                        } catch (_: Exception) {
                                            viewModel.showToast("UPI copied ($officialUpiId). Pay ₹${"%,d".format(selectedAmount.toLong())} via any UPI app.")
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("pay_upi_button"),
                                    shape = RoundedCornerShape(25.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DasaniPrimary,
                                        contentColor = Color(0xFF041C1A)
                                    )
                                ) {
                                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Pay ₹${"%,d".format(selectedAmount.toLong())} via UPI App",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Recharge Records Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    if (rechargeHistory.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No recharge records yet", color = DasaniTextSecondary)
                            }
                        }
                    } else {
                        items(rechargeHistory) { record ->
                            RechargeRecordItem(record)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AmountPresetButton(
    amount: Double,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(52.dp)
            .clickable { onClick() }
            .testTag("preset_${amount.toLong()}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DasaniPrimary.copy(alpha = 0.25f) else Color(0xFF142423)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) DasaniPrimary else DasaniCardBorder
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "₹ ${"%,d".format(amount.toLong())}",
                color = if (isSelected) DasaniPrimary else Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PaymentMethodCard(
    method: PaymentMethodType,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("method_${method.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF14302D) else Color(0xFF132221)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) DasaniPrimary else DasaniCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = DasaniPrimary,
                    unselectedColor = Color(0xFF6B7D82)
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = method.title,
                        color = if (isSelected) Color.White else DasaniTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = method.subtitle,
                    color = DasaniTextMuted,
                    fontSize = 11.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isSelected) DasaniPrimary else Color(0xFF1E3533)
            ) {
                Text(
                    text = method.badge,
                    color = if (isSelected) Color(0xFF041C1A) else DasaniTextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun BankDetailRow(label: String, value: String, context: Context) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(label, color = DasaniTextSecondary, fontSize = 11.sp)
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        IconButton(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText(label, value))
            },
            modifier = Modifier.size(28.dp)
        ) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy $label", tint = DasaniPrimary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun TabPill(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .background(
                if (selected) DasaniPrimary else Color.Transparent,
                RoundedCornerShape(19.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (selected) Color(0xFF041C1A) else DasaniTextSecondary,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun RechargeRecordItem(record: RechargeRecord) {
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
                    text = "+₹ ${record.amount.toLong()}",
                    color = DasaniSuccess,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = record.channel,
                    color = DasaniTextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = record.status,
                    color = DasaniPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = record.date,
                    color = DasaniTextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

