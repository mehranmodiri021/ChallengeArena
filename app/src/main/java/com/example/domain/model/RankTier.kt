package com.example.domain.model

import androidx.compose.ui.graphics.Color

enum class RankTier(
    val tierLevel: Int,
    val titleEn: String,
    val titleFa: String,
    val minXp: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
    val iconSymbol: String,
    val perksEn: String,
    val perksFa: String
) {
    BRONZE(
        tierLevel = 1,
        titleEn = "Bronze",
        titleFa = "برنز",
        minXp = 0,
        primaryColor = Color(0xFFCD7F32),
        secondaryColor = Color(0xFF8C5320),
        iconSymbol = "🛡️",
        perksEn = "Standard rewards and baseline arena access",
        perksFa = "پاداش‌های پایه و دسترسی به آرنای مقدماتی"
    ),
    SILVER(
        tierLevel = 2,
        titleEn = "Silver",
        titleFa = "نقره‌ای",
        minXp = 500,
        primaryColor = Color(0xFFC0C0C0),
        secondaryColor = Color(0xFF7A8B99),
        iconSymbol = "⚔️",
        perksEn = "+5% XP boost and daily challenge unlocked",
        perksFa = "۵٪ افزایش XP و باز شدن چالش‌های روزانه"
    ),
    GOLD(
        tierLevel = 3,
        titleEn = "Gold",
        titleFa = "طلایی",
        minXp = 1500,
        primaryColor = Color(0xFFFFD700),
        secondaryColor = Color(0xFFB8860B),
        iconSymbol = "🏆",
        perksEn = "+10% XP bonus and weekly tournament entry",
        perksFa = "۱۰٪ پاداش XP و ورود به تورنمنت‌های هفتگی"
    ),
    PLATINUM(
        tierLevel = 4,
        titleEn = "Platinum",
        titleFa = "پلاتین",
        minXp = 3500,
        primaryColor = Color(0xFF00E5FF),
        secondaryColor = Color(0xFF00838F),
        iconSymbol = "💎",
        perksEn = "+15% XP bonus and advanced challenges",
        perksFa = "۱۵٪ پاداش XP و دسترسی به چالش‌های پیشرفته"
    ),
    DIAMOND(
        tierLevel = 5,
        titleEn = "Diamond",
        titleFa = "الماس",
        minXp = 7000,
        primaryColor = Color(0xFF7C4DFF),
        secondaryColor = Color(0xFF4527A0),
        iconSymbol = "✨",
        perksEn = "+20% XP bonus, diamond badge and special events",
        perksFa = "۲۰٪ پاداش XP، نشان ویژه الماس و رویدادهای فصلی"
    ),
    MASTER(
        tierLevel = 6,
        titleEn = "Master",
        titleFa = "استاد",
        minXp = 12000,
        primaryColor = Color(0xFFFF3D00),
        secondaryColor = Color(0xFFBF360C),
        iconSymbol = "🔥",
        perksEn = "+25% XP bonus and master tier leaderboards",
        perksFa = "۲۵٪ پاداش XP و رقابت در جدول اساتید"
    ),
    GRANDMASTER(
        tierLevel = 7,
        titleEn = "Grandmaster",
        titleFa = "استاد بزرگ",
        minXp = 20000,
        primaryColor = Color(0xFFFF1744),
        secondaryColor = Color(0xFF880E4F),
        iconSymbol = "👑",
        perksEn = "+30% XP bonus and grandmaster aura in arena",
        perksFa = "۳۰٪ پاداش XP و هاله اختصاصی در آرنا"
    ),
    LEGEND(
        tierLevel = 8,
        titleEn = "Legend",
        titleFa = "افسانه",
        minXp = 35000,
        primaryColor = Color(0xFFFFD700),
        secondaryColor = Color(0xFFFF007F),
        iconSymbol = "⚡",
        perksEn = "Supreme rank, maximum rewards and Hall of Fame",
        perksFa = "بالاترین رتبه، نهایت پاداش‌ها و ورود به تالار مشاهیر"
    );

    companion object {
        fun fromXp(xp: Int): RankTier {
            val sorted = entries.sortedByDescending { it.minXp }
            return sorted.firstOrNull { xp >= it.minXp } ?: BRONZE
        }

        fun nextTier(current: RankTier): RankTier? {
            val idx = entries.indexOf(current)
            return if (idx < entries.size - 1) entries[idx + 1] else null
        }
    }
}
