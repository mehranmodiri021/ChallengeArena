package com.example.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "primary_user",
    val username: String = "ArenaPlayer",
    val level: Int = 1,
    val xp: Int = 150,
    val rankTierName: String = "BRONZE",
    val coins: Int = 500,
    val tickets: Int = 5,
    val dailyStreak: Int = 3,
    val lastLoginDate: String = "",
    val wins: Int = 12,
    val losses: Int = 3,
    val bestScore: Int = 2450,
    val isVip: Boolean = false,
    val vipExpiryTimestamp: Long = 0L,
    val avatarId: String = "avatar_01"
)

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleFa: String,
    val descEn: String,
    val descFa: String,
    val typeName: String,
    val difficultyName: String,
    val durationSeconds: Int,
    val xpReward: Int,
    val coinReward: Int,
    val ticketCost: Int,
    val participantsCount: Int,
    val isCompleted: Boolean,
    val isLocked: Boolean,
    val rankRequiredTier: String
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val code: String,
    val titleEn: String,
    val titleFa: String,
    val descEn: String,
    val descFa: String,
    val iconSymbol: String,
    val isUnlocked: Boolean,
    val currentProgress: Int,
    val targetProgress: Int,
    val rewardXp: Int,
    val rewardCoins: Int
)

@Entity(tableName = "daily_streaks")
data class DailyStreakEntity(
    @PrimaryKey val dayNumber: Int,
    val rewardXp: Int,
    val rewardCoins: Int,
    val rewardTickets: Int,
    val isClaimed: Boolean,
    val isToday: Boolean
)

@Entity(tableName = "match_history")
data class MatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0L,
    val matchId: String,
    val challengeTitleEn: String,
    val challengeTitleFa: String,
    val score: Int,
    val isWin: Boolean,
    val timestamp: Long,
    val dateFormatted: String,
    val xpEarned: Int,
    val coinsEarned: Int
)
