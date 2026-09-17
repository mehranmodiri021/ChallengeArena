package com.example.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.database.entity.AchievementEntity
import com.example.data.database.entity.ChallengeEntity
import com.example.data.database.entity.DailyStreakEntity
import com.example.data.database.entity.MatchHistoryEntity
import com.example.data.database.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = :id LIMIT 1")
    fun getUserProfile(id: String = "primary_user"): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET xp = xp + :xpGained, coins = coins + :coinsGained WHERE id = :id")
    suspend fun addXpAndCoins(xpGained: Int, coinsGained: Int, id: String = "primary_user")

    @Query("UPDATE user_profile SET coins = coins - :amount WHERE id = :id AND coins >= :amount")
    suspend fun deductCoins(amount: Int, id: String = "primary_user"): Int

    @Query("UPDATE user_profile SET tickets = tickets - 1 WHERE id = :id AND tickets > 0")
    suspend fun deductTicket(id: String = "primary_user"): Int

    @Query("UPDATE user_profile SET tickets = tickets + :count WHERE id = :id")
    suspend fun addTickets(count: Int, id: String = "primary_user")

    @Query("UPDATE user_profile SET isVip = :isVip, vipExpiryTimestamp = :expiry WHERE id = :id")
    suspend fun updateVipStatus(isVip: Boolean, expiry: Long, id: String = "primary_user")
}

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE typeName = :typeName")
    fun getChallengesByType(typeName: String): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE id = :id LIMIT 1")
    suspend fun getChallengeById(id: String): ChallengeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(challenges: List<ChallengeEntity>)

    @Query("UPDATE challenges SET isCompleted = 1 WHERE id = :id")
    suspend fun markCompleted(id: String)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE code = :code LIMIT 1")
    suspend fun getAchievementByCode(code: String): AchievementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("UPDATE achievements SET currentProgress = :progress, isUnlocked = CASE WHEN :progress >= targetProgress THEN 1 ELSE isUnlocked END WHERE code = :code")
    suspend fun updateProgress(code: String, progress: Int)
}

@Dao
interface DailyStreakDao {
    @Query("SELECT * FROM daily_streaks ORDER BY dayNumber ASC")
    fun getAllDailyStreaks(): Flow<List<DailyStreakEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(streaks: List<DailyStreakEntity>)

    @Query("UPDATE daily_streaks SET isClaimed = 1 WHERE dayNumber = :dayNumber")
    suspend fun markClaimed(dayNumber: Int)
}

@Dao
interface MatchHistoryDao {
    @Query("SELECT * FROM match_history ORDER BY timestamp DESC LIMIT 25")
    fun getRecentMatches(): Flow<List<MatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchHistoryEntity)
}
