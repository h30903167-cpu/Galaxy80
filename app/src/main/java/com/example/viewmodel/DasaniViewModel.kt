package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.AppLinkConfig
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DasaniViewModel : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    private fun generateAlphaCode(prefix: String, length: Int): String {
        val letters = "ABCDEFGHJKLMNPQRSTUVWXYZ"
        val suffix = (1..length).map { letters.random() }.joinToString("")
        return "$prefix$suffix"
    }

    private val registeredAccounts = mutableMapOf<String, UserAccount>()

    private val _userAccount = MutableStateFlow(
        UserAccount(
            accountId = generateAlphaCode("GLX-", 6),
            phone = "",
            loginPassword = "",
            withdrawalPassword = "",
            inviteCode = generateAlphaCode("INV-", 5),
            walletBalance = 0.0,
            totalIncome = 0.0,
            todayIncome = 0.0,
            totalRecharged = 0.0,
            totalWithdrawn = 0.0,
            isLoggedIn = false
        )
    )
    val userAccount: StateFlow<UserAccount> = _userAccount.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ProductCategory.DAY_INCOME)
    val selectedCategory: StateFlow<ProductCategory> = _selectedCategory.asStateFlow()

    private val _allProducts = MutableStateFlow(
        listOf(
            // Top 1: ₹300 Plan (LIVE)
            DasaniProduct(
                id = "prod_gl12_300",
                name = "GL-12 Seeder (300 Plan)",
                category = ProductCategory.DAY_INCOME,
                price = 300.0,
                dailyProfit = 75.0,
                durationDays = 120,
                totalProfit = 9000.0,
                limitPerUser = 1,
                drawableRes = R.drawable.galaxy_product_gl12,
                description = "Galaxy GL-12 Manual Seeder Starter Machine (₹300 Plan • ₹75 Daily Profit)",
                isLocked = false
            ),
            // Top 2: ₹500 Plan (LIVE)
            DasaniProduct(
                id = "prod_gl12_500",
                name = "GL-12 Seeder (500 Plan)",
                category = ProductCategory.DAY_INCOME,
                price = 500.0,
                dailyProfit = 125.0,
                durationDays = 120,
                totalProfit = 15000.0,
                limitPerUser = 1,
                drawableRes = R.drawable.galaxy_product_gl12,
                description = "Galaxy GL-12 Manual Seeder Standard Machine (₹500 Plan • ₹125 Daily Profit)",
                isLocked = false
            ),
            // Blocked / Locked Plans
            DasaniProduct(
                id = "prod_gl10",
                name = "GL-10 Galaxy Seeder",
                category = ProductCategory.DAY_INCOME,
                price = 1800.0,
                dailyProfit = 450.0,
                durationDays = 120,
                totalProfit = 54000.0,
                limitPerUser = 2,
                drawableRes = R.drawable.galaxy_product_gl10,
                description = "Galaxy GL-10 Automatic Seed Planter & Cultivator",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. Only ₹300 and ₹500 plans are live for buying."
            ),
            DasaniProduct(
                id = "prod_gl14",
                name = "GL-14 Multi-Crop Seeder",
                category = ProductCategory.DAY_INCOME,
                price = 4900.0,
                dailyProfit = 1350.0,
                durationDays = 120,
                totalProfit = 162000.0,
                limitPerUser = 3,
                drawableRes = R.drawable.galaxy_product_gl14,
                description = "Multi-Crop Corn, Peanut, Soybean & Wheat Seeder Unit",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. Only ₹300 and ₹500 plans are live for buying."
            ),
            DasaniProduct(
                id = "prod_gl16",
                name = "GL-16 Heavy Seeder",
                category = ProductCategory.DAY_INCOME,
                price = 12000.0,
                dailyProfit = 3600.0,
                durationDays = 120,
                totalProfit = 432000.0,
                limitPerUser = 3,
                drawableRes = R.drawable.galaxy_product_vip,
                description = "Heavy Duty Stainless Steel Agricultural Seeding Rig",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. Only ₹300 and ₹500 plans are live for buying."
            ),
            DasaniProduct(
                id = "prod_tractor_seeder",
                name = "Galaxy Tractor Seeder",
                category = ProductCategory.DAY_INCOME,
                price = 35000.0,
                dailyProfit = 11200.0,
                durationDays = 120,
                totalProfit = 1344000.0,
                limitPerUser = 2,
                drawableRes = R.drawable.galaxy_seeder_box,
                description = "Commercial High-Yield Tractor Attachable Seeding Machine",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. Only ₹300 and ₹500 plans are live for buying."
            ),
            DasaniProduct(
                id = "prod_ultra_1lakh",
                name = "Galaxy Mega Seeder (1 Lakh)",
                category = ProductCategory.DAY_INCOME,
                price = 100000.0,
                dailyProfit = 34000.0,
                durationDays = 120,
                totalProfit = 4080000.0,
                limitPerUser = 1,
                drawableRes = R.drawable.galaxy_product_gl12,
                description = "Galaxy Industrial Farm Seeding Fleet (₹1 Lakh Investment)",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. Only ₹300 and ₹500 plans are live for buying."
            ),
            // VIP Seeder Category Products (All Blocked)
            DasaniProduct(
                id = "vip_seeder_1",
                name = "VIP Seeder 1",
                category = ProductCategory.VIP_INCOME,
                price = 1200.0,
                dailyProfit = 600.0,
                durationDays = 3,
                totalProfit = 1800.0,
                limitPerUser = 1,
                drawableRes = R.drawable.galaxy_product_vip,
                description = "High Speed 3-Day Return Galaxy Seeder VIP Plan",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. VIP plans are currently closed. Only ₹300 and ₹500 are live."
            ),
            DasaniProduct(
                id = "vip_seeder_2",
                name = "VIP Seeder 2",
                category = ProductCategory.VIP_INCOME,
                price = 3800.0,
                dailyProfit = 2200.0,
                durationDays = 3,
                totalProfit = 6600.0,
                limitPerUser = 1,
                drawableRes = R.drawable.galaxy_product_gl10,
                description = "Advanced VIP Rapid Yield Galaxy Agricultural Plan",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. VIP plans are currently closed. Only ₹300 and ₹500 are live."
            ),
            DasaniProduct(
                id = "vip_seeder_3",
                name = "VIP Seeder 3",
                category = ProductCategory.VIP_INCOME,
                price = 10000.0,
                dailyProfit = 6500.0,
                durationDays = 3,
                totalProfit = 19500.0,
                limitPerUser = 1,
                drawableRes = R.drawable.galaxy_product_gl14,
                description = "VIP Executive Farm Equipment Yield Program",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. VIP plans are currently closed. Only ₹300 and ₹500 are live."
            ),
            DasaniProduct(
                id = "vip_mega_1lakh",
                name = "VIP Mega Seeder (1 Lakh)",
                category = ProductCategory.VIP_INCOME,
                price = 100000.0,
                dailyProfit = 75000.0,
                durationDays = 3,
                totalProfit = 225000.0,
                limitPerUser = 1,
                drawableRes = R.drawable.galaxy_seeder_box,
                description = "Exclusive 1 Lakh Fast-Yield VIP Machine",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. VIP plans are currently closed. Only ₹300 and ₹500 are live."
            )
        )
    )
    val allProducts: StateFlow<List<DasaniProduct>> = _allProducts.asStateFlow()

    private val _myDevices = MutableStateFlow<List<PurchasedProduct>>(emptyList())
    val myDevices: StateFlow<List<PurchasedProduct>> = _myDevices.asStateFlow()

    private val _rechargeHistory = MutableStateFlow<List<RechargeRecord>>(emptyList())
    val rechargeHistory: StateFlow<List<RechargeRecord>> = _rechargeHistory.asStateFlow()

    private val _withdrawalHistory = MutableStateFlow<List<WithdrawalRecord>>(emptyList())
    val withdrawalHistory: StateFlow<List<WithdrawalRecord>> = _withdrawalHistory.asStateFlow()

    private val _teamMembers = MutableStateFlow<List<TeamMember>>(emptyList())
    val teamMembers: StateFlow<List<TeamMember>> = _teamMembers.asStateFlow()

    private val _currentTeamLevel = MutableStateFlow(1)
    val currentTeamLevel: StateFlow<Int> = _currentTeamLevel.asStateFlow()

    private val _tutorialVideo = MutableStateFlow(
        WithdrawalTutorialVideo(
            id = "vid_1",
            title = "Official Galaxy Seeder Withdrawal Guide",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            duration = "02:45",
            description = "Learn how to bind your bank card / UPI, submit withdrawals from ₹200 to ₹1,00,000, and receive funds directly within 10-30 minutes."
        )
    )
    val tutorialVideo: StateFlow<WithdrawalTutorialVideo> = _tutorialVideo.asStateFlow()

    private val _serviceMessages = MutableStateFlow(
        listOf(
            ServiceChatMessage(
                id = "msg_1",
                sender = "Galaxy Seeder Support",
                message = "Namaste! Welcome to Galaxy Manual Seeder 24/7 Official Customer Support. How can we assist you with Recharge, Equipment Buying, or Bank Withdrawal today?",
                time = "Online",
                isFromUser = false
            )
        )
    )
    val serviceMessages: StateFlow<List<ServiceChatMessage>> = _serviceMessages.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _incomingReferralCode = MutableStateFlow<String?>(null)
    val incomingReferralCode: StateFlow<String?> = _incomingReferralCode.asStateFlow()

    fun applyReferralLink(rawUri: String?) {
        val code = AppLinkConfig.extractReferralCode(rawUri) ?: return
        _incomingReferralCode.value = code
        _toastMessage.value = "Referral link applied: $code"
    }

    fun selectCategory(category: ProductCategory) {
        _selectedCategory.value = category
    }

    fun selectTeamLevel(level: Int) {
        _currentTeamLevel.value = level
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    private fun normalizePhone(raw: String): String {
        val digits = raw.filter { it.isDigit() }
        return if (digits.length > 10 && digits.startsWith("91")) digits.takeLast(10) else digits
    }

    fun login(phoneInput: String, passInput: String): Boolean {
        val cleanPhone = normalizePhone(phoneInput)
        if (cleanPhone.length != 10 || passInput.isBlank()) {
            _toastMessage.value = "Please enter valid 10-digit phone number and password."
            return false
        }
        val stored = registeredAccounts[cleanPhone]
        if (stored == null) {
            _toastMessage.value = "Number not registered yet. Please Register first."
            return false
        }
        if (stored.loginPassword != passInput) {
            _toastMessage.value = "Incorrect password. Please try again."
            return false
        }
        _userAccount.value = stored.copy(isLoggedIn = true)
        _toastMessage.value = "Login Successful! Welcome to Galaxy Seeder."
        return true
    }

    fun register(
        phoneInput: String,
        pass1: String,
        pass2: String,
        withdrawPass: String,
        invite: String,
        upiIdInput: String = ""
    ): Pair<Boolean, String> {
        val cleanPhone = normalizePhone(phoneInput)

        if (cleanPhone.length != 10) {
            return Pair(false, "Please enter a valid 10-digit phone number")
        }
        if (pass1.length < 4) {
            return Pair(false, "Login password must be at least 4 characters")
        }
        if (pass1 != pass2) {
            return Pair(false, "Passwords do not match")
        }
        if (withdrawPass.length < 4) {
            return Pair(false, "Withdrawal password must be at least 4 characters")
        }
        if (registeredAccounts.containsKey(cleanPhone)) {
            return Pair(false, "Number already registered. Please Login.")
        }

        val newAccountId = generateAlphaCode("GLX-", 6)
        val newAccount = UserAccount(
            accountId = newAccountId,
            phone = cleanPhone,
            boundUpiId = upiIdInput.trim(),
            registeredWithUpi = false,
            loginPassword = pass1,
            withdrawalPassword = withdrawPass,
            inviteCode = if (invite.isNotBlank()) invite.trim() else generateAlphaCode("INV-", 5),
            walletBalance = 0.0,
            totalIncome = 0.0,
            todayIncome = 0.0,
            totalRecharged = 0.0,
            totalWithdrawn = 0.0,
            isLoggedIn = true
        )
        registeredAccounts[cleanPhone] = newAccount
        registeredAccounts[newAccountId.lowercase(Locale.ROOT)] = newAccount
        _userAccount.value = newAccount
        _toastMessage.value = "Registration Successful!"
        return Pair(true, "Success")
    }

    fun logout() {
        val current = _userAccount.value
        val phoneKey = normalizePhone(current.phone)
        if (phoneKey.isNotBlank()) {
            registeredAccounts[phoneKey] = current.copy(isLoggedIn = false)
        }
        if (current.accountId.isNotBlank()) {
            registeredAccounts[current.accountId.lowercase(Locale.ROOT)] = current.copy(isLoggedIn = false)
        }
        _userAccount.value = UserAccount(
            accountId = generateAlphaCode("GLX-", 6),
            inviteCode = generateAlphaCode("INV-", 5),
            isLoggedIn = false
        )
        _toastMessage.value = "Logged out successfully"
    }

    // Recharge flow with support up to 1 Lakh and beyond
    fun recharge(amount: Double, channel: String, utr: String = ""): Boolean {
        if (amount < 100) {
            _toastMessage.value = "Minimum recharge amount is ₹100"
            return false
        }
        val order = "RC${System.currentTimeMillis() % 10000000}"
        val record = RechargeRecord(
            id = UUID.randomUUID().toString(),
            orderNo = order,
            amount = amount,
            channel = channel,
            utr = utr.ifBlank { order },
            date = dateFormat.format(Date()),
            status = "Success"
        )
        val currentAcc = _userAccount.value
        _userAccount.value = currentAcc.copy(
            walletBalance = currentAcc.walletBalance + amount,
            totalRecharged = currentAcc.totalRecharged + amount
        )
        _rechargeHistory.value = listOf(record) + _rechargeHistory.value
        _toastMessage.value = "Recharge of ₹${amount.toLong()} Successful! Balance updated."
        return true
    }

    // Purchase product
    fun buyProduct(product: DasaniProduct): Pair<Boolean, String> {
        if (product.isLocked) {
            _toastMessage.value = "⚠️ This plan is blocked! Only ₹300 and ₹500 plans are live."
            return Pair(false, "This equipment is currently blocked. Only ₹300 and ₹500 plans are available for live purchase.")
        }

        val currentAcc = _userAccount.value
        if (currentAcc.walletBalance < product.price) {
            val needed = product.price - currentAcc.walletBalance
            return Pair(false, "Insufficient balance! Need ₹${needed.toLong()} more. Please recharge.")
        }

        // Check user limit
        val existingCount = _myDevices.value.count { it.productId == product.id }
        if (existingCount >= product.limitPerUser) {
            return Pair(false, "Purchase limit of ${product.limitPerUser} reached for ${product.name}.")
        }

        // Deduct price and add to devices
        val newBalance = currentAcc.walletBalance - product.price
        val newDevice = PurchasedProduct(
            id = UUID.randomUUID().toString(),
            productId = product.id,
            name = product.name,
            price = product.price,
            dailyProfit = product.dailyProfit,
            daysRemaining = product.durationDays,
            totalDays = product.durationDays,
            totalClaimed = 0.0,
            purchaseDate = dateFormat.format(Date()),
            canClaimToday = true,
            drawableRes = product.drawableRes
        )

        _userAccount.value = currentAcc.copy(
            walletBalance = newBalance
        )
        _myDevices.value = listOf(newDevice) + _myDevices.value
        _toastMessage.value = "Congratulations! ${product.name} purchased successfully."
        return Pair(true, "Success")
    }

    fun claimDailyProfit(deviceId: String) {
        val device = _myDevices.value.find { it.id == deviceId } ?: return
        if (!device.canClaimToday) {
            _toastMessage.value = "Today's profit already claimed for this device!"
            return
        }

        val profit = device.dailyProfit
        val updatedDevices = _myDevices.value.map {
            if (it.id == deviceId) {
                it.copy(
                    totalClaimed = it.totalClaimed + profit,
                    daysRemaining = (it.daysRemaining - 1).coerceAtLeast(0),
                    canClaimToday = false
                )
            } else it
        }
        _myDevices.value = updatedDevices

        val acc = _userAccount.value
        _userAccount.value = acc.copy(
            walletBalance = acc.walletBalance + profit,
            totalIncome = acc.totalIncome + profit,
            todayIncome = acc.todayIncome + profit
        )
        _toastMessage.value = "₹${profit.toLong()} daily profit claimed and added to wallet!"
    }

    fun claimAllDailyProfits() {
        val claimable = _myDevices.value.filter { it.canClaimToday }
        if (claimable.isEmpty()) {
            _toastMessage.value = "No pending profits to claim right now."
            return
        }
        val totalProfit = claimable.sumOf { it.dailyProfit }
        val updated = _myDevices.value.map {
            if (it.canClaimToday) {
                it.copy(
                    totalClaimed = it.totalClaimed + it.dailyProfit,
                    daysRemaining = (it.daysRemaining - 1).coerceAtLeast(0),
                    canClaimToday = false
                )
            } else it
        }
        _myDevices.value = updated

        val acc = _userAccount.value
        _userAccount.value = acc.copy(
            walletBalance = acc.walletBalance + totalProfit,
            totalIncome = acc.totalIncome + totalProfit,
            todayIncome = acc.todayIncome + totalProfit
        )
        _toastMessage.value = "Total ₹${totalProfit.toLong()} collected to your wallet!"
    }

    // Submit withdrawal with withdrawal password check & optional video proof
    fun submitWithdrawal(
        amount: Double,
        bankName: String,
        accountNo: String,
        holderName: String,
        ifsc: String,
        upiId: String,
        withdrawalPasswordInput: String,
        videoProofUrl: String?
    ): Pair<Boolean, String> {
        val currentAcc = _userAccount.value
        if (amount < 200.0) {
            return Pair(false, "Minimum withdrawal amount is ₹200")
        }
        if (amount > currentAcc.walletBalance) {
            return Pair(false, "Withdrawal amount cannot exceed available balance of ₹${currentAcc.walletBalance.toLong()}")
        }
        val activePin = currentAcc.withdrawalPassword
        if (withdrawalPasswordInput.length < 4) {
            return Pair(false, "Enter Withdrawal Password (min 4 characters)")
        }
        if (activePin.isNotBlank() && withdrawalPasswordInput != activePin) {
            return Pair(false, "Incorrect Withdrawal Password! Please try again.")
        }
        if (accountNo.isBlank() && upiId.isBlank()) {
            return Pair(false, "Please provide Bank Account Number or UPI ID")
        }

        val fee = amount * 0.05 // 5% fee
        val net = amount - fee
        val order = "WD${System.currentTimeMillis() % 1000000}"

        val record = WithdrawalRecord(
            id = UUID.randomUUID().toString(),
            orderNo = order,
            amount = amount,
            fee = fee,
            finalAmount = net,
            payoutMethod = if (upiId.isNotBlank()) "UPI: $upiId" else "Bank: $bankName",
            accountNumberOrUpi = if (upiId.isNotBlank()) upiId else accountNo,
            holderName = holderName,
            ifsc = ifsc,
            status = "Success",
            date = dateFormat.format(Date()),
            videoProofUrl = videoProofUrl
        )

        _userAccount.value = currentAcc.copy(
            walletBalance = currentAcc.walletBalance - amount,
            totalWithdrawn = currentAcc.totalWithdrawn + amount,
            boundBankName = bankName.ifBlank { currentAcc.boundBankName },
            boundAccountNo = accountNo.ifBlank { currentAcc.boundAccountNo },
            boundHolderName = holderName.ifBlank { currentAcc.boundHolderName },
            boundIfsc = ifsc.ifBlank { currentAcc.boundIfsc },
            boundUpiId = upiId.ifBlank { currentAcc.boundUpiId }
        )
        _withdrawalHistory.value = listOf(record) + _withdrawalHistory.value
        _toastMessage.value = "Withdrawal of ₹${amount.toLong()} Submitted! Net ₹${net.toLong()} dispatched."
        return Pair(true, "Success")
    }

    fun updateWithdrawalVideo(title: String, url: String, desc: String) {
        _tutorialVideo.value = WithdrawalTutorialVideo(
            title = title,
            videoUrl = url,
            description = desc
        )
        _toastMessage.value = "Withdrawal Tutorial Video updated successfully!"
    }

    fun sendServiceMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ServiceChatMessage(
            id = UUID.randomUUID().toString(),
            sender = "You",
            message = text,
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
            isFromUser = true
        )
        _serviceMessages.value = _serviceMessages.value + userMsg

        // Automated smart support reply
        viewModelScope.launch {
            val replyText = when {
                text.contains("recharge", ignoreCase = true) || text.contains("1 lakh", ignoreCase = true) || text.contains("100000", ignoreCase = true) ->
                    "To recharge, tap 'Recharge' on the Home screen. Select ₹300 up to ₹1,00,000, copy the assigned Merchant UPI ID, and pay directly via GPay/PhonePe/Paytm."
                text.contains("withdraw", ignoreCase = true) || text.contains("video", ignoreCase = true) || text.contains("paisa", ignoreCase = true) ->
                    "Withdrawals are open 24/7. Minimum is ₹200. Bind your UPI ID or Bank, enter your Withdrawal PIN, and watch our Withdrawal Guide Video right on the Withdraw page."
                text.contains("buy", ignoreCase = true) || text.contains("product", ignoreCase = true) || text.contains("income", ignoreCase = true) || text.contains("seeder", ignoreCase = true) ->
                    "GL-12 Manual Seeder ₹300 plan gives ₹75 daily and ₹500 plan gives ₹125 daily for 120 days. Ensure your wallet has sufficient balance, then tap 'Buy now'!"
                else ->
                    "Thank you for contacting Galaxy Seeder 24/7 Care. Our support team is here to help, or join our Official Telegram Care channel above."
            }
            val botMsg = ServiceChatMessage(
                id = UUID.randomUUID().toString(),
                sender = "Galaxy Care",
                message = replyText,
                time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                isFromUser = false
            )
            _serviceMessages.value = _serviceMessages.value + botMsg
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun updateBoundUpi(newUpi: String): Boolean {
        val clean = newUpi.trim()
        if (!clean.contains("@") || clean.length < 5) {
            _toastMessage.value = "Please enter a valid UPI ID (e.g. yourname@okaxis)"
            return false
        }
        val oldUpiKey = _userAccount.value.boundUpiId.lowercase(Locale.ROOT)
        val updated = _userAccount.value.copy(
            boundUpiId = clean,
            registeredWithUpi = true
        )
        _userAccount.value = updated
        if (oldUpiKey.isNotBlank() && oldUpiKey != clean.lowercase(Locale.ROOT)) {
            registeredAccounts.remove(oldUpiKey)
        }
        val phoneKey = normalizePhone(updated.phone)
        if (phoneKey.isNotBlank()) {
            registeredAccounts[phoneKey] = updated
        }
        if (updated.accountId.isNotBlank()) {
            registeredAccounts[updated.accountId.lowercase(Locale.ROOT)] = updated
        }
        _toastMessage.value = "Linked UPI ID updated to: $clean"
        return true
    }

    fun updateBoundBank(holderName: String, bankName: String, accountNo: String, ifsc: String) {
        val updated = _userAccount.value.copy(
            boundHolderName = holderName.trim(),
            boundBankName = bankName.trim(),
            boundAccountNo = accountNo.trim(),
            boundIfsc = ifsc.trim().uppercase()
        )
        _userAccount.value = updated
        val phoneKey = normalizePhone(updated.phone)
        if (phoneKey.isNotBlank()) {
            registeredAccounts[phoneKey] = updated
        }
        if (updated.accountId.isNotBlank()) {
            registeredAccounts[updated.accountId.lowercase(Locale.ROOT)] = updated
        }
        _toastMessage.value = "Bank account details saved!"
    }
}
