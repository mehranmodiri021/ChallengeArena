package com.example.domain.repository

import com.example.domain.model.AchievementItem
import com.example.domain.model.DailyStreakReward
import com.example.domain.model.RewardItem
import kotlinx.coroutines.flow.Flow

interface RewardRepository {
    fun getDailyStreakRewards(): Flow<List<DailyStreakReward>>
    suspend fun claimDailyStreak(dayNumber: Int): DailyStreakReward?
    fun getAchievements(): Flow<List<AchievementItem>>
    suspend fun updateAchievementProgress(code: String, progressIncrement: Int)
    suspend fun claimAchievement(code: String)
    fun getStoreRewards(): Flow<List<RewardItem>>
    suspend fun claimStoreReward(rewardId: String): Boolean
}
