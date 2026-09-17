package com.example.domain.model

enum class ChallengeType(
    val titleEn: String,
    val titleFa: String,
    val iconSymbol: String
) {
    QUICK("Quick Challenge", "چالش سریع", "⚡"),
    DAILY("Daily Challenge", "چالش روزانه", "📅"),
    TIME_ATTACK("Time Attack", "حمله به زمان", "⏱️"),
    ACCURACY("Accuracy", "تمرکز و دقت", "🎯"),
    ENDLESS("Endless", "بی‌پایان", "♾️"),
    TOURNAMENT("Weekly Tournament", "تورنمنت هفتگی", "🏆"),
    SPECIAL_EVENT("Special Event", "رویداد ویژه", "🌟")
}

enum class ChallengeDifficulty(
    val titleEn: String,
    val titleFa: String,
    val multiplier: Float
) {
    EASY("Easy", "آسان", 1.0f),
    MEDIUM("Medium", "متوسط", 1.5f),
    HARD("Hard", "سخت", 2.0f),
    EPIC("Epic", "افسانه‌ای", 3.0f)
}

data class ChallengeItem(
    val id: String,
    val titleEn: String,
    val titleFa: String,
    val descEn: String,
    val descFa: String,
    val type: ChallengeType,
    val difficulty: ChallengeDifficulty,
    val durationSeconds: Int,
    val xpReward: Int,
    val coinReward: Int,
    val ticketCost: Int,
    val participantsCount: Int,
    val isCompleted: Boolean = false,
    val isLocked: Boolean = false,
    val rankRequired: RankTier = RankTier.BRONZE
)
