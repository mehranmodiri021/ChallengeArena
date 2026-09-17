package com.example.data.repository

import com.example.domain.model.ChallengeItem
import com.example.domain.model.LeaderboardEntry
import com.example.domain.model.LeaderboardType
import com.example.domain.model.UserProfile

/**
 * =============================================================================
 * ONLINE BACKEND ARCHITECTURE CONTRACTS (Clean Remote Data Source)
 * =============================================================================
 *
 * This file defines the exact contracts, endpoints, and data exchange models
 * needed to connect Challenge Arena to a production backend (such as Node.js,
 * Go, Ktor, Firebase Firestore, or custom WebSocket servers).
 *
 * Requirements for real multiplayer & online sync in future:
 * 1. Authentication Service (JWT / OAuth2 / Bazaar Auth)
 * 2. Real-time Matchmaking Server (WebSockets or gRPC)
 * 3. Centralized Leaderboard Redis/Spanner cluster
 * 4. Anti-cheat score validation engine
 */
interface RemoteChallengeDataSource {
    suspend fun fetchActiveChallenges(): Result<List<ChallengeItem>>
    suspend fun submitChallengeScore(challengeId: String, score: Int, completionTimeSeconds: Int): Result<Boolean>
}

interface RemoteLeaderboardDataSource {
    suspend fun fetchLeaderboard(type: LeaderboardType, page: Int = 1, pageSize: Int = 50): Result<List<LeaderboardEntry>>
    suspend fun submitScore(score: Int, leaderboardType: LeaderboardType): Result<LeaderboardEntry>
}

interface RemoteUserSyncDataSource {
    suspend fun syncProfile(localProfile: UserProfile): Result<UserProfile>
    suspend fun verifyPurchase(purchaseToken: String, productId: String): Result<Boolean>
}

/**
 * Mock/Demo Fallback Remote Implementation for offline & development execution.
 * When real backend servers are deployed, replace this with Retrofit/Ktor instances.
 */
class DefaultRemoteDataSourceStub : RemoteChallengeDataSource, RemoteLeaderboardDataSource, RemoteUserSyncDataSource {
    override suspend fun fetchActiveChallenges(): Result<List<ChallengeItem>> {
        return Result.success(emptyList())
    }

    override suspend fun submitChallengeScore(challengeId: String, score: Int, completionTimeSeconds: Int): Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun fetchLeaderboard(type: LeaderboardType, page: Int, pageSize: Int): Result<List<LeaderboardEntry>> {
        return Result.success(emptyList())
    }

    override suspend fun submitScore(score: Int, leaderboardType: LeaderboardType): Result<LeaderboardEntry> {
        return Result.failure(NotImplementedError("Real backend server not configured yet."))
    }

    override suspend fun syncProfile(localProfile: UserProfile): Result<UserProfile> {
        return Result.success(localProfile)
    }

    override suspend fun verifyPurchase(purchaseToken: String, productId: String): Result<Boolean> {
        return Result.success(true)
    }
}
