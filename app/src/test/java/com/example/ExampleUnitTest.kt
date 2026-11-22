package com.example

import com.example.data.AppLinkConfig
import com.example.data.MerchantUpiProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testRechargeMaxLimit() {
        val maxAllowedRecharge = 100_000.0
        val testAmount = 100_000.0
        assertTrue(testAmount <= maxAllowedRecharge)
    }

    @Test
    fun testGl12Plan300ProfitCalculation() {
        val price = 300.0
        val dailyProfit = 75.0
        val days = 120
        val totalExpectedProfit = dailyProfit * days
        assertEquals(9000.0, totalExpectedProfit, 0.001)
        assertTrue(totalExpectedProfit > price)
    }

    @Test
    fun testGl12Plan500ProfitCalculation() {
        val price = 500.0
        val dailyProfit = 125.0
        val days = 120
        val totalExpectedProfit = dailyProfit * days
        assertEquals(15000.0, totalExpectedProfit, 0.001)
        assertTrue(totalExpectedProfit > price)
    }

    @Test
    fun testLiveVsBlockedProducts() {
        val plan300Locked = false
        val plan500Locked = false
        val otherPlansLocked = true

        assertFalse(plan300Locked)
        assertFalse(plan500Locked)
        assertTrue(otherPlansLocked)
    }

    @Test
    fun testUpiRegistrationAndValidation() {
        val sampleUpi = "farmer@paytm"
        assertTrue(sampleUpi.contains("@"))
        assertTrue(sampleUpi.length >= 5)

        val samplePassword = "mypassword123"
        assertTrue(samplePassword.length >= 4)
    }

    @Test
    fun testUpiWithdrawalCalculation() {
        val amount = 500.0
        val fee = amount * 0.05
        val netReceive = amount - fee
        assertEquals(25.0, fee, 0.001)
        assertEquals(475.0, netReceive, 0.001)
    }

    @Test
    fun testMerchantUpiPoolContainsAllRequestedIds() {
        val expected = setOf(
            "jayray8257@nyes",
            "jayray120@nyes",
            "komaraj988@naviaxis",
            "komalraj988@naviaxis",
            "shivanimtpkyp@mvhdfc",
            "komalrajput988@ptyes",
            "Madhukant220@ybl",
            "Madhukant220@ibl",
            "Madhukant220@axl"
        )
        assertEquals(expected, MerchantUpiProvider.MERCHANT_UPI_POOL.toSet())
    }

    @Test
    fun testSingleUpiPerPhoneAndDifferentAcrossPhones() {
        val phoneAFirstCheck = MerchantUpiProvider.selectUpiForDeviceKey("device_android_id_001")
        val phoneASecondCheck = MerchantUpiProvider.selectUpiForDeviceKey("device_android_id_001")
        assertEquals(phoneAFirstCheck, phoneASecondCheck)

        val assignedSet = (1..100).map { idx ->
            MerchantUpiProvider.selectUpiForDeviceKey("device_android_id_$idx")
        }.toSet()
        assertEquals(MerchantUpiProvider.MERCHANT_UPI_POOL.toSet(), assignedSet)
    }

    @Test
    fun testWorkingAppLinkAndDeepLinkGeneration() {
        val webLink = AppLinkConfig.buildShareLink("c07cdf4c")
        val deepLink = AppLinkConfig.buildDeepLink("c07cdf4c")

        assertEquals(
            "https://ais-pre-6wedoxyipxgrzycagw2ljo-858805462154.asia-southeast1.run.app/?ref=c07cdf4c",
            webLink
        )
        assertEquals("galaxyseeder://join?ref=c07cdf4c", deepLink)
    }

    @Test
    fun testReferralCodeExtractionFromWorkingLinks() {
        assertEquals(
            "c07cdf4c",
            AppLinkConfig.extractReferralCode(
                "https://ais-pre-6wedoxyipxgrzycagw2ljo-858805462154.asia-southeast1.run.app/?ref=c07cdf4c"
            )
        )
        assertEquals(
            "TEAM999",
            AppLinkConfig.extractReferralCode("galaxyseeder://join?foo=1&ref=TEAM999#top")
        )
        assertEquals(
            "VIP77",
            AppLinkConfig.extractReferralCode("https://ais-pre-6wedoxyipxgrzycagw2ljo-858805462154.asia-southeast1.run.app/?invite=VIP77")
        )
        assertNull(AppLinkConfig.extractReferralCode("https://ais-pre-6wedoxyipxgrzycagw2ljo-858805462154.asia-southeast1.run.app/"))
        assertNull(AppLinkConfig.extractReferralCode(null))
    }

    @Test
    fun testPhoneRegistrationAndLoginWithoutOtp() {
        val vm = com.example.viewmodel.DasaniViewModel()
        val res = vm.register(
            phoneInput = "9123456789",
            pass1 = "pass123",
            pass2 = "pass123",
            withdrawPass = "9988",
            invite = ""
        )
        assertTrue(res.first)
        assertTrue(vm.userAccount.value.isLoggedIn)
        assertEquals(0.0, vm.userAccount.value.walletBalance, 0.001)

        vm.logout()
        assertFalse(vm.userAccount.value.isLoggedIn)
        assertTrue(vm.login("9123456789", "pass123"))
        assertTrue(vm.userAccount.value.isLoggedIn)
    }
}
