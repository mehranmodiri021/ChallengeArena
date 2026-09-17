package com.example.domain.repository

import com.example.domain.model.MatchHistoryItem
import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(): Flow<UserProfile>
    suspend fun updateUserProfile(profile: UserProfile)
    suspend fun addXpAndCoins(xpGained: Int, coinsGained: Int)
    suspend fun spendCoins(amount: Int): Boolean
    suspend fun spendTicket(): Boolean
    suspend fun addTickets(count: Int)
    suspend fun activateVip(durationDays: Int)
    suspend fun recordMatchResult(historyItem: MatchHistoryItem)
    fun getMatchHistory(): Flow<List<MatchHistoryItem>>
    suspend fun resetProgress()
}
