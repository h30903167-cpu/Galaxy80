package com.example.data

import java.net.URLDecoder
import java.net.URLEncoder

object AppLinkConfig {
    const val BASE_APP_URL = "https://ais-pre-6wedoxyipxgrzycagw2ljo-858805462154.asia-southeast1.run.app"
    const val DEEP_LINK_HOST = "ais-pre-6wedoxyipxgrzycagw2ljo-858805462154.asia-southeast1.run.app"
    const val CUSTOM_SCHEME = "galaxyseeder"
    const val CUSTOM_HOST = "join"
    const val DEFAULT_REF_CODE = "c07cdf4c"

    fun sanitizeCode(inviteCode: String): String {
        val trimmed = inviteCode.trim()
        return if (trimmed.isNotEmpty()) trimmed else DEFAULT_REF_CODE
    }

    fun buildShareLink(inviteCode: String): String {
        val safeCode = URLEncoder.encode(sanitizeCode(inviteCode), Charsets.UTF_8.name())
        return "$BASE_APP_URL/?ref=$safeCode"
    }

    fun buildDeepLink(inviteCode: String): String {
        val safeCode = URLEncoder.encode(sanitizeCode(inviteCode), Charsets.UTF_8.name())
        return "$CUSTOM_SCHEME://$CUSTOM_HOST?ref=$safeCode"
    }

    fun buildShareMessage(inviteCode: String): String {
        val code = sanitizeCode(inviteCode)
        val webLink = buildShareLink(code)
        val appLink = buildDeepLink(code)
        return buildString {
            appendLine("Galaxy Manual Seeder (GL-12) App Join Karein!")
            appendLine("Instant ₹300 & ₹500 Plans | Daily Income | UPI Recharge & Withdraw")
            appendLine()
            appendLine("Working App Link: $webLink")
            appendLine("Direct App Open Link: $appLink")
            append("Invitation Code: $code")
        }
    }

    fun extractReferralCode(rawUrl: String?): String? {
        if (rawUrl.isNullOrBlank()) return null
        val withoutFragment = rawUrl.substringBefore('#')
        val query = withoutFragment.substringAfter('?', "")
        if (query.isBlank()) return null

        for (pair in query.split('&')) {
            val idx = pair.indexOf('=')
            if (idx <= 0) continue
            val key = pair.substring(0, idx).trim()
            if (key.equals("ref", ignoreCase = true) || key.equals("invite", ignoreCase = true)) {
                val rawVal = pair.substring(idx + 1).trim()
                if (rawVal.isNotEmpty()) {
                    return try {
                        URLDecoder.decode(rawVal, Charsets.UTF_8.name()).trim().ifEmpty { null }
                    } catch (_: Exception) {
                        rawVal
                    }
                }
            }
        }
        return null
    }
}
