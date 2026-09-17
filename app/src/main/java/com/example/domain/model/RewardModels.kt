package com.example.domain.model

enum class RewardType {
    COINS,
    TICKETS,
    XP,
    MYSTERY_CHEST,
    VIP_BADGE
}

data class DailyStreakReward(
    val dayNumber: Int,
    val rewardXp: Int,
    val rewardCoins: Int,
    val rewardTickets: Int,
    val isClaimed: Boolean = false,
    val isToday: Boolean = false
)

data class RewardItem(
    val id: String,
    val titleEn: String,
    val titleFa: String,
    val descEn: String,
    val descFa: String,
    val type: RewardType,
    val amount: Int,
    val iconSymbol: String,
    val coinCost: Int = 0,
    val isClaimed: Boolean = false,
    val isVipOnly: Boolean = false
)
