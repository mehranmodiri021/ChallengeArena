package com.example.config

import com.example.BuildConfig

/**
 * Tapsell Advertisement Network Configuration.
 *
 * Centralized Zone IDs and keys for Tapsell integration.
 * Securely reads credentials from BuildConfig (injected via .env / Secrets panel)
 * with zero hardcoded secret data.
 */
object TapsellConfig {

    // Status indicator: Explicit NOT CONFIGURED label for development/unconfigured environment
    const val STATUS_NOT_CONFIGURED = "NOT CONFIGURED"

    // =========================================================================
    // TAPSELL APP KEY (Injected safely via BuildConfig)
    // =========================================================================
    val TAPSELL_APP_KEY: String
        get() = try {
            val key = BuildConfig::class.java.getField("TAPSELL_APP_KEY").get(null) as? String
            if (!key.isNullOrBlank() && key != STATUS_NOT_CONFIGURED) key else STATUS_NOT_CONFIGURED
        } catch (_: Throwable) {
            STATUS_NOT_CONFIGURED
        }

    // =========================================================================
    // TAPSELL AD ZONE IDS (Injected safely via BuildConfig)
    // =========================================================================
    val TAPSELL_REWARDED_ZONE_ID: String
        get() = try {
            val zone = BuildConfig::class.java.getField("TAPSELL_REWARDED_ZONE_ID").get(null) as? String
            if (!zone.isNullOrBlank() && zone != STATUS_NOT_CONFIGURED) zone else STATUS_NOT_CONFIGURED
        } catch (_: Throwable) {
            STATUS_NOT_CONFIGURED
        }

    val TAPSELL_BANNER_ZONE_ID: String
        get() = try {
            val zone = BuildConfig::class.java.getField("TAPSELL_BANNER_ZONE_ID").get(null) as? String
            if (!zone.isNullOrBlank() && zone != STATUS_NOT_CONFIGURED) zone else STATUS_NOT_CONFIGURED
        } catch (_: Throwable) {
            STATUS_NOT_CONFIGURED
        }

    val TAPSELL_INTERSTITIAL_ZONE_ID: String
        get() = try {
            val zone = BuildConfig::class.java.getField("TAPSELL_INTERSTITIAL_ZONE_ID").get(null) as? String
            if (!zone.isNullOrBlank() && zone != STATUS_NOT_CONFIGURED) zone else STATUS_NOT_CONFIGURED
        } catch (_: Throwable) {
            STATUS_NOT_CONFIGURED
        }

    // Rewards granted when user finishes watching a rewarded video ad
    const val REWARDED_AD_COIN_BONUS = 150
    const val REWARDED_AD_XP_BONUS = 50
    const val REWARDED_AD_TICKET_BONUS = 1

    // Operational mode: Flag indicates whether real credentials are present
    val isConfigured: Boolean
        get() = TAPSELL_APP_KEY != STATUS_NOT_CONFIGURED && TAPSELL_APP_KEY.isNotBlank()

    val isAdTestMode: Boolean
        get() = !isConfigured
}
