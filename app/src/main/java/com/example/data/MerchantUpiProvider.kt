package com.example.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.Settings
import java.security.MessageDigest
import java.util.Locale
import java.util.UUID

object MerchantUpiProvider {
    private const val PREFS_NAME = "galaxy_merchant_prefs_v2"
    private const val KEY_ASSIGNED_UPI = "assigned_merchant_upi"
    private const val KEY_ROTATION_INDEX = "rotation_upi_index"

    val MERCHANT_UPI_POOL = listOf(
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

    @SuppressLint("HardwareIds")
    fun getAssignedMerchantUpi(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_ASSIGNED_UPI, null)
        if (!saved.isNullOrBlank() && saved in MERCHANT_UPI_POOL) {
            return saved
        }
        return rotateMerchantUpi(context)
    }

    @SuppressLint("HardwareIds")
    fun rotateMerchantUpi(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val poolSize = MERCHANT_UPI_POOL.size
        val savedUpi = prefs.getString(KEY_ASSIGNED_UPI, null)
        val savedPos = if (!savedUpi.isNullOrBlank()) MERCHANT_UPI_POOL.indexOf(savedUpi) else -1

        val nextIndex = if (savedPos >= 0) {
            (savedPos + 1) % poolSize
        } else if (prefs.contains(KEY_ROTATION_INDEX)) {
            (prefs.getInt(KEY_ROTATION_INDEX, 0) + 1) % poolSize
        } else {
            val androidId = runCatching {
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            }.getOrNull()
            val seed = if (!androidId.isNullOrBlank() && androidId != "9774d56d682e549c") {
                "$androidId-${System.currentTimeMillis()}"
            } else {
                UUID.randomUUID().toString()
            }
            val initialUpi = selectUpiForDeviceKey(seed)
            MERCHANT_UPI_POOL.indexOf(initialUpi).coerceAtLeast(0)
        }

        val nextUpi = MERCHANT_UPI_POOL[nextIndex]
        prefs.edit()
            .putInt(KEY_ROTATION_INDEX, nextIndex)
            .putString(KEY_ASSIGNED_UPI, nextUpi)
            .apply()
        return nextUpi
    }

    fun selectUpiForDeviceKey(deviceKey: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(deviceKey.toByteArray(Charsets.UTF_8))
        val unsignedInt =
            ((digest[0].toLong() and 0xFFL) shl 24) or
            ((digest[1].toLong() and 0xFFL) shl 16) or
            ((digest[2].toLong() and 0xFFL) shl 8) or
            (digest[3].toLong() and 0xFFL)
        val index = (unsignedInt % MERCHANT_UPI_POOL.size).toInt()
        return MERCHANT_UPI_POOL[index]
    }

    fun buildUpiPaymentUri(
        upiId: String,
        amount: Double,
        scheme: String = "upi",
        host: String = "pay"
    ): Uri {
        val txnRef = "GLX${System.currentTimeMillis()}"
        val formattedAmount = String.format(Locale.US, "%.2f", amount)
        return Uri.Builder()
            .scheme(scheme)
            .authority(host)
            .appendQueryParameter("pa", upiId)
            .appendQueryParameter("pn", "Galaxy Seeder")
            .appendQueryParameter("tn", "Galaxy Equipment Recharge")
            .appendQueryParameter("tr", txnRef)
            .appendQueryParameter("am", formattedAmount)
            .appendQueryParameter("cu", "INR")
            .build()
    }
}
