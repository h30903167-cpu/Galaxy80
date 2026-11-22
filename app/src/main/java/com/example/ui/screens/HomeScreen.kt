package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DasaniProduct
import com.example.model.ProductCategory
import com.example.ui.theme.*
import com.example.viewmodel.DasaniViewModel

@Composable
fun HomeScreen(
    viewModel: DasaniViewModel,
    onNavigateToRecharge: () -> Unit,
    onNavigateToWithdraw: () -> Unit,
    onNavigateToService: () -> Unit,
    onOpenTelegramChannel: () -> Unit,
    onNavigateToDevices: () -> Unit
) {
    val userAccount by viewModel.userAccount.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    var productToBuy by remember { mutableStateOf<DasaniProduct?>(null) }
    var insufficientBalanceProduct by remember { mutableStateOf<DasaniProduct?>(null) }
    var purchaseSuccessProduct by remember { mutableStateOf<DasaniProduct?>(null) }
    var blockedProductSelected by remember { mutableStateOf<DasaniProduct?>(null) }

    val filteredProducts = remember(allProducts, selectedCategory) {
        allProducts.filter { it.category == selectedCategory }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DasaniBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
        ) {
            // Top Hero Banner (Matching Screenshot 2)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.galaxy_banner_top),
                        contentDescription = "Galaxy Manual Seeder GL-12 Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quick Balance & Assets Strip
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0F2B28), Color(0xFF143B37))
                            ),
                            RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, DasaniPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Wallet Balance",
                            color = DasaniTextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "₹ ${"%,.2f".format(userAccount.walletBalance)}",
                            color = DasaniPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onNavigateToRecharge,
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(17.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DasaniPrimary,
                                contentColor = Color(0xFF041C1A)
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Recharge", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onNavigateToDevices,
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(17.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DasaniPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Text("My Devices", fontSize = 12.sp, color = DasaniPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 4 Action Buttons Grid (Matching Screenshot 2)
            // Recharge | Withdraw | Service | Channel
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Recharge
                    ActionTealCard(
                        title = "Recharge",
                        icon = Icons.Default.AccountBalanceWallet,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_recharge_button"),
                        onClick = onNavigateToRecharge
                    )

                    // 2. Withdraw
                    ActionTealCard(
                        title = "Withdraw",
                        icon = Icons.Default.CurrencyExchange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_withdraw_button"),
                        onClick = onNavigateToWithdraw
                    )

                    // 3. Service (Explicit user requirement: "or ek service bhi add karna hai jo service likha ho aur kuchh nahin extra")
                    ActionTealCard(
                        title = "Service",
                        icon = Icons.Default.Headphones,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_service_button"),
                        onClick = onNavigateToService
                    )

                    // 4. Channel
                    ActionTealCard(
                        title = "Channel",
                        icon = Icons.AutoMirrored.Filled.Send,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_channel_button"),
                        onClick = onOpenTelegramChannel
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Income Tabs: Day Income | VIP Income (Matching Screenshot 2)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F1E1D), RoundedCornerShape(24.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isDayIncome = selectedCategory == ProductCategory.DAY_INCOME
                    Button(
                        onClick = { viewModel.selectCategory(ProductCategory.DAY_INCOME) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("tab_day_income"),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDayIncome) DasaniPrimary else Color.Transparent,
                            contentColor = if (isDayIncome) Color(0xFF041C1A) else DasaniTextSecondary
                        )
                    ) {
                        Text(
                            text = "Day Income",
                            fontSize = 15.sp,
                            fontWeight = if (isDayIncome) FontWeight.Bold else FontWeight.Medium
                        )
                    }

                    val isVipIncome = selectedCategory == ProductCategory.VIP_INCOME
                    Button(
                        onClick = { viewModel.selectCategory(ProductCategory.VIP_INCOME) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("tab_vip_income"),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isVipIncome) DasaniPrimary else Color.Transparent,
                            contentColor = if (isVipIncome) Color(0xFF041C1A) else DasaniTextSecondary
                        )
                    ) {
                        Text(
                            text = "VIP Income",
                            fontSize = 15.sp,
                            fontWeight = if (isVipIncome) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Status Notice Banner (Explaining ₹300 & ₹500 Live status)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F2B22), RoundedCornerShape(12.dp))
                        .border(1.dp, DasaniPrimary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = DasaniPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Notice: Only ₹300 & ₹500 Plans Live",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Top ₹300 and ₹500 seeder plans are active for buying. Other plans are temporarily blocked.",
                            color = DasaniTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Products Cards List
            items(filteredProducts) { product ->
                ProductCardItem(
                    product = product,
                    onBuyClick = {
                        if (userAccount.walletBalance >= product.price) {
                            productToBuy = product
                        } else {
                            insufficientBalanceProduct = product
                        }
                    },
                    onLockedClick = {
                        blockedProductSelected = product
                    }
                )
                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // Buy Confirmation Dialog
        productToBuy?.let { prod ->
            AlertDialog(
                onDismissRequest = { productToBuy = null },
                containerColor = Color(0xFF132221),
                titleContentColor = Color.White,
                textContentColor = DasaniTextSecondary,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = DasaniPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirm Purchase", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Are you sure you want to buy ${prod.name}?",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        HorizontalDivider(color = DasaniCardBorder)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Price:", color = DasaniTextSecondary)
                            Text("₹ ${prod.price.toLong()}", color = DasaniPrimary, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Daily Profit:", color = DasaniTextSecondary)
                            Text("₹ ${prod.dailyProfit.toLong()}/day", color = DasaniSuccess, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Duration:", color = DasaniTextSecondary)
                            Text("${prod.durationDays} Days", color = Color.White)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Return:", color = DasaniTextSecondary)
                            Text("₹ ${prod.totalProfit.toLong()}", color = DasaniAccentGold, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = DasaniCardBorder)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Wallet Balance After:", color = DasaniTextSecondary)
                            Text("₹ ${(userAccount.walletBalance - prod.price).toLong()}", color = Color.White)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val res = viewModel.buyProduct(prod)
                            productToBuy = null
                            if (res.first) {
                                purchaseSuccessProduct = prod
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DasaniPrimary,
                            contentColor = Color(0xFF041C1A)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Confirm & Buy", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { productToBuy = null }) {
                        Text("Cancel", color = DasaniTextSecondary)
                    }
                }
            )
        }

        // Insufficient Balance Dialog (Friendly redirect to Recharge)
        insufficientBalanceProduct?.let { prod ->
            val needed = prod.price - userAccount.walletBalance
            AlertDialog(
                onDismissRequest = { insufficientBalanceProduct = null },
                containerColor = Color(0xFF132221),
                titleContentColor = DasaniWarning,
                textContentColor = DasaniTextSecondary,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = DasaniWarning)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Insufficient Balance", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "You need ₹${needed.toLong()} more to purchase ${prod.name}.",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Product Price:", color = DasaniTextSecondary)
                            Text("₹ ${prod.price.toLong()}", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Your Balance:", color = DasaniTextSecondary)
                            Text("₹ ${userAccount.walletBalance.toLong()}", color = DasaniError, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "Please recharge your wallet to continue earning daily returns.",
                            color = DasaniTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            insufficientBalanceProduct = null
                            onNavigateToRecharge()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DasaniPrimary,
                            contentColor = Color(0xFF041C1A)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Recharge Now", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { insufficientBalanceProduct = null }) {
                        Text("Later", color = DasaniTextSecondary)
                    }
                }
            )
        }

        // Purchase Success Dialog
        purchaseSuccessProduct?.let { prod ->
            AlertDialog(
                onDismissRequest = { purchaseSuccessProduct = null },
                containerColor = Color(0xFF132221),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DasaniSuccess)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Product Activated!", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Text(
                        text = "${prod.name} has been added to your Devices! You will earn ₹${prod.dailyProfit.toLong()} every day.",
                        color = DasaniTextSecondary,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            purchaseSuccessProduct = null
                            onNavigateToDevices()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DasaniPrimary,
                            contentColor = Color(0xFF041C1A)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("View My Devices", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { purchaseSuccessProduct = null }) {
                        Text("Close", color = DasaniTextSecondary)
                    }
                }
            )
        }

        // Blocked Product Notice Dialog
        blockedProductSelected?.let { prod ->
            AlertDialog(
                onDismissRequest = { blockedProductSelected = null },
                containerColor = Color(0xFF1B2624),
                titleContentColor = Color(0xFFFF5252),
                textContentColor = DasaniTextSecondary,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFF5252))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Plan Currently Blocked", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "${prod.name} (₹${prod.price.toLong()}) is currently blocked by administration.",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Only the top ₹300 and ₹500 Galaxy GL-12 Seeder equipment plans are open and live for purchasing right now. All other plans are temporarily locked.",
                            color = DasaniTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            blockedProductSelected = null
                            viewModel.selectCategory(ProductCategory.DAY_INCOME)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DasaniPrimary,
                            contentColor = Color(0xFF041C1A)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("View Live ₹300 / ₹500 Plans", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { blockedProductSelected = null }) {
                        Text("Close", color = DasaniTextSecondary)
                    }
                }
            )
        }
    }
}

// 4 Top Action Cards (Recharge, Withdraw, Service, Channel)
@Composable
fun ActionTealCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(84.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF00D2C4) // Vibrant cyan matching screenshot
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF05211E),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = Color(0xFF05211E),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Product Card Item (Supporting Live ₹300 & ₹500 and Blocked others)
@Composable
fun ProductCardItem(
    product: DasaniProduct,
    onBuyClick: () -> Unit,
    onLockedClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (product.isLocked) Color(0xFF0D1716) else Color(0xFF101B1A)
        ),
        border = if (!product.isLocked) androidx.compose.foundation.BorderStroke(1.dp, DasaniPrimary.copy(alpha = 0.5f)) else null,
        elevation = CardDefaults.cardElevation(if (product.isLocked) 2.dp else 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Product Image Banner with Limit / Live / Blocked Badge and Title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                Image(
                    painter = painterResource(id = product.drawableRes),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Top-Right Status Badge (Live vs Blocked)
                if (!product.isLocked) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Glowing Green "LIVE BUY" Badge
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF00E676), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF041C1A), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "LIVE BUY",
                                    color = Color(0xFF041C1A),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        // Limit Badge
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF003833).copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                                .border(1.dp, DasaniPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Limit ${product.limitPerUser}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Red "BLOCKED" Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .background(Color(0xFFD32F2F), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "BLOCKED",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    // Translucent Dark Overlay for Locked Products
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFF1F1111).copy(alpha = 0.9f), RoundedCornerShape(20.dp))
                                .border(1.dp, Color(0xFFFF5252).copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Locked / Blocked",
                                color = Color(0xFFFFCDD2),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Product Name on Bottom-Left
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = product.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black,
                                blurRadius = 8f
                            )
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Stats in 2x2 Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Price Box
                StatTile(
                    modifier = Modifier.weight(1f),
                    value = "₹ ${product.price.toLong()}",
                    label = "Price",
                    valueColor = if (product.isLocked) Color(0xFF88A39E) else DasaniPrimary
                )

                // Daily Profit Box
                StatTile(
                    modifier = Modifier.weight(1f),
                    value = "₹ ${product.dailyProfit.toLong()}",
                    label = "Daily profit",
                    valueColor = if (product.isLocked) Color(0xFF88A39E) else DasaniPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Days Box
                StatTile(
                    modifier = Modifier.weight(1f),
                    value = "${product.durationDays}",
                    label = "Day",
                    valueColor = if (product.isLocked) Color(0xFF88A39E) else DasaniPrimary
                )

                // Total Profit Box
                StatTile(
                    modifier = Modifier.weight(1f),
                    value = "₹ ${product.totalProfit.toLong()}",
                    label = "Total profit",
                    valueColor = if (product.isLocked) Color(0xFF88A39E) else DasaniPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button: Live "Buy now" vs "🔒 Blocked / Locked"
            if (!product.isLocked) {
                Button(
                    onClick = onBuyClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("buy_now_${product.id}"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DasaniPrimary,
                        contentColor = Color(0xFF05211E)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Buy now",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = onLockedClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("buy_locked_${product.id}"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E2D2A),
                        contentColor = Color(0xFFA5B8B4)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFFF8A80),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🔒 Blocked / Locked",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFCDD2)
                    )
                }
            }
        }
    }
}

@Composable
fun StatTile(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    valueColor: Color
) {
    Card(
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF142423)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 6.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                color = valueColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = DasaniTextSecondary,
                fontSize = 12.sp
            )
        }
    }
}
