package com.example.config

/**
 * Centralized Economy Configuration for Challenge Arena.
 *
 * Controls all gameplay progression balance:
 * - Coin and XP rewards for matches
 * - VIP economic multipliers
 * - Ticket requirements and regeneration
 * - Level calculation formulas
 * - Store pricing and conversions
 */
object EconomyConfig {

    // =========================================================================
    // MATCH COSTS & REQUIREMENTS
    // =========================================================================
    const val QUICK_MATCH_TICKET_COST = 0 // Free entry for rapid practice
    const val DAILY_CHALLENGE_TICKET_COST = 1
    const val TOURNAMENT_TICKET_COST = 2
    const val SPECIAL_EVENT_TICKET_COST = 3

    // =========================================================================
    // BASE MATCH REWARDS
    // =========================================================================
    const val BASE_WIN_SCORE_THRESHOLD = 200
    const val BASE_TARGET_TAP_POINTS = 50
    const val GOLDEN_TARGET_TAP_POINTS = 150
    const val MATH_CORRECT_POINTS = 100
    const val MAX_COMBO_MULTIPLIER = 10

    // Standard Match Rewards
    const val QUICK_MATCH_BASE_XP = 80
    const val QUICK_MATCH_BASE_COINS = 50

    const val DAILY_MATCH_BASE_XP = 150
    const val DAILY_MATCH_BASE_COINS = 120

    const val TOURNAMENT_BASE_XP = 300
    const val TOURNAMENT_BASE_COINS = 250

    // =========================================================================
    // VIP ADVANTAGES & MULTIPLIERS
    // =========================================================================
    const val VIP_XP_MULTIPLIER = 2.0f
    const val VIP_COIN_MULTIPLIER = 1.5f
    const val VIP_BONUS_INITIAL_TICKETS = 10
    const val VIP_DAILY_STREAK_BONUS_MULTIPLIER = 1.25f

    // =========================================================================
    // LEVELING SYSTEM FORMULA
    // =========================================================================
    // XP needed to advance to level N: Base 500 * (Level ^ 1.2)
    fun getRequiredXpForNextLevel(currentLevel: Int): Int {
        return (500 * Math.pow(currentLevel.toDouble(), 1.15)).toInt()
    }

    // =========================================================================
    // ANTI-CHEAT BOUNDARIES
    // =========================================================================
    // Physical human limits: impossible to tap valid random targets in under 70 milliseconds consistently
    const val MIN_TARGET_REACTION_MS = 70L
    // Maximum physically achievable score in a 30-second reflex match
    const val MAX_POSSIBLE_SCORE_30S = 12000
    // Maximum score rate per second
    const val MAX_POINTS_PER_SECOND = 400
}
