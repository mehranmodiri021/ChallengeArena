package com.example.config

import com.example.BuildConfig

/**
 * Cafe Bazaar In-App Billing Configuration.
 *
 * Keep all Bazaar Product IDs and Keys centralized here for easy configuration
 * prior to publishing on Cafe Bazaar (کافه‌بازار).
 * Reads BAZAAR_PUBLIC_KEY securely from BuildConfig (.env / Secrets panel).
 */
object BazaarConfig {

    // Status indicator: In sandbox/unconfigured environment, explicit NOT_CONFIGURED label is required.
    const val STATUS_NOT_CONFIGURED = "NOT CONFIGURED"

    // =========================================================================
    // CAFE BAZAAR PUBLIC KEY
    // =========================================================================
    // Real RSA Public Key obtained from Cafe Bazaar Developer Console.
    // Explicitly labeled as NOT_CONFIGURED until set in deployment secrets.
    val BAZAAR_PUBLIC_KEY: String
        get() = try {
            val key = BuildConfig::class.java.getField("BAZAAR_PUBLIC_KEY").get(null) as? String
            if (!key.isNullOrBlank() && key != STATUS_NOT_CONFIGURED) key else STATUS_NOT_CONFIGURED
        } catch (_: Throwable) {
            STATUS_NOT_CONFIGURED
        }

    // =========================================================================
    // VIP SUBSCRIPTION PRODUCT IDS (Registered in Cafe Bazaar Developer Console)
    // =========================================================================
    const val PRODUCT_ID_VIP_MONTHLY = "challenge_arena_vip_monthly"
    const val PRODUCT_ID_VIP_YEARLY = "challenge_arena_vip_yearly"

    // =========================================================================
    // IN-GAME CURRENCY CONSUMABLE PRODUCT IDS (Registered in Cafe Bazaar Console)
    // =========================================================================
    const val PRODUCT_ID_COINS_PACK_SMALL = "challenge_arena_coins_1000"
    const val PRODUCT_ID_COINS_PACK_LARGE = "challenge_arena_coins_5000"
    const val PRODUCT_ID_TICKETS_PACK = "challenge_arena_tickets_10"

    // Operational mode: In development without active Cafe Bazaar connection, operates in safe sandbox simulation
    val isConfigured: Boolean
        get() = BAZAAR_PUBLIC_KEY != STATUS_NOT_CONFIGURED && BAZAAR_PUBLIC_KEY.isNotBlank()

    val isSandboxMode: Boolean
        get() = !isConfigured
}
