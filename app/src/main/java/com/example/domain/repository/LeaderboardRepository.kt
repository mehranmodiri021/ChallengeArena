package com.example.domain.repository

import com.example.domain.model.LeaderboardEntry
import com.example.domain.model.LeaderboardType
import kotlinx.coroutines.flow.Flow

interface LeaderboardRepository {
    fun getLeaderboard(type: LeaderboardType): Flow<List<LeaderboardEntry>>
    suspend fun submitUserScore(score: Int, type: LeaderboardType)
    suspend fun refreshLeaderboard(type: LeaderboardType)
}
