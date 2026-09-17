package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.database.dao.AchievementDao
import com.example.data.database.dao.ChallengeDao
import com.example.data.database.dao.DailyStreakDao
import com.example.data.database.dao.MatchHistoryDao
import com.example.data.database.dao.UserDao
import com.example.data.database.entity.AchievementEntity
import com.example.data.database.entity.ChallengeEntity
import com.example.data.database.entity.DailyStreakEntity
import com.example.data.database.entity.MatchHistoryEntity
import com.example.data.database.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        ChallengeEntity::class,
        AchievementEntity::class,
        DailyStreakEntity::class,
        MatchHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun achievementDao(): AchievementDao
    abstract fun dailyStreakDao(): DailyStreakDao
    abstract fun matchHistoryDao(): MatchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "challenge_arena_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
