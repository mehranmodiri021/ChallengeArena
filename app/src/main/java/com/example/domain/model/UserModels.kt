package com.example.domain.model

data class UserProfile(
    val id: String = "user_arena_01",
    val username: String = "ArenaMaster",
    val level: Int = 1,
    val xp: Int = 120,
    val currentRank: RankTier = RankTier.BRONZE,
    val coins: Int = 350,
    val tickets: Int = 5,
    val dailyStreak: Int = 3,
    val lastLoginDate: String = "",
    val wins: Int = 14,
    val losses: Int = 4,
    val bestScore: Int = 2850,
    val isVip: Boolean = false,
    val vipExpiryTimestamp: Long = 0L,
    val avatarId: String = "avatar_cyber_ninja"
) {
    val totalMatches: Int get() = wins + losses
    val winRate: Int get() = if (totalMatches > 0) (wins * 100) / totalMatches else 0
    val xpForCurrentLevel: Int get() = level * 250
    val currentLevelProgress: Float get() {
        val currentLevelBase = (level - 1) * 250
        val nextLevelBase = level * 250
        val diff = nextLevelBase - currentLevelBase
        val current = xp - currentLevelBase
        return (current.toFloat() / diff.coerceAtLeast(1)).coerceIn(0f, 1f)
    }
}

data class MatchHistoryItem(
    val id: String,
    val challengeTitleEn: String,
    val challengeTitleFa: String,
    val score: Int,
    val isWin: Boolean,
    val dateFormatted: String,
    val xpEarned: Int,
    val coinsEarned: Int
)
