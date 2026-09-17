package com.example.domain.repository

import com.example.domain.model.ChallengeItem
import com.example.domain.model.ChallengeType
import kotlinx.coroutines.flow.Flow

interface ChallengeRepository {
    fun getChallenges(): Flow<List<ChallengeItem>>
    fun getChallengesByType(type: ChallengeType): Flow<List<ChallengeItem>>
    suspend fun getChallengeById(id: String): ChallengeItem?
    suspend fun markChallengeCompleted(id: String)
    suspend fun refreshChallenges()
}
