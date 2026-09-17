package com.example.domain.model

enum class LeaderboardType(val titleEn: String, val titleFa: String) {
    GLOBAL("Global", "جهانی"),
    WEEKLY("Weekly", "هفتگی"),
    DAILY("Daily", "روزانه"),
    FRIENDS("Friends", "دوستان")
}

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val score: Int,
    val level: Int,
    val rankTier: RankTier,
    val avatarEmoji: String,
    val isCurrentUser: Boolean = false,
    val isFriend: Boolean = false,
    val badge: String? = null,
    val winsCount: Int = 0
)
