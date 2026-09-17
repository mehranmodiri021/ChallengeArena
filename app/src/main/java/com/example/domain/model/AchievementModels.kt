package com.example.domain.model

data class AchievementItem(
    val id: String,
    val code: String,
    val titleEn: String,
    val titleFa: String,
    val descEn: String,
    val descFa: String,
    val iconSymbol: String,
    val isUnlocked: Boolean = false,
    val currentProgress: Int = 0,
    val targetProgress: Int = 1,
    val rewardXp: Int = 100,
    val rewardCoins: Int = 50
) {
    val progressFraction: Float get() = (currentProgress.toFloat() / targetProgress.coerceAtLeast(1)).coerceIn(0f, 1f)
}
