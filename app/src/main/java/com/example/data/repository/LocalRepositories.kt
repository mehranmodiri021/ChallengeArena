package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.database.entity.AchievementEntity
import com.example.data.database.entity.ChallengeEntity
import com.example.data.database.entity.DailyStreakEntity
import com.example.data.database.entity.MatchHistoryEntity
import com.example.data.database.entity.UserProfileEntity
import com.example.domain.model.AchievementItem
import com.example.domain.model.ChallengeDifficulty
import com.example.domain.model.ChallengeItem
import com.example.domain.model.ChallengeType
import com.example.domain.model.DailyStreakReward
import com.example.domain.model.LeaderboardEntry
import com.example.domain.model.LeaderboardType
import com.example.domain.model.MatchHistoryItem
import com.example.domain.model.RankTier
import com.example.domain.model.RewardItem
import com.example.domain.model.RewardType
import com.example.domain.model.UserProfile
import com.example.domain.repository.ChallengeRepository
import com.example.domain.repository.LeaderboardRepository
import com.example.domain.repository.RewardRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class LocalUserRepository(private val database: AppDatabase) : UserRepository {
    private val userDao = database.userDao()
    private val matchDao = database.matchHistoryDao()

    override fun getUserProfile(): Flow<UserProfile> {
        return userDao.getUserProfile().map { entity ->
            if (entity == null) {
                // Return default initial profile
                val initial = UserProfileEntity()
                userDao.insertOrUpdate(initial)
                mapEntityToProfile(initial)
            } else {
                mapEntityToProfile(entity)
            }
        }
    }

    override suspend fun updateUserProfile(profile: UserProfile) {
        userDao.insertOrUpdate(mapProfileToEntity(profile))
    }

    override suspend fun addXpAndCoins(xpGained: Int, coinsGained: Int) {
        userDao.addXpAndCoins(xpGained, coinsGained)
    }

    override suspend fun spendCoins(amount: Int): Boolean {
        val updated = userDao.deductCoins(amount)
        return updated > 0
    }

    override suspend fun spendTicket(): Boolean {
        val updated = userDao.deductTicket()
        return updated > 0
    }

    override suspend fun addTickets(count: Int) {
        userDao.addTickets(count)
    }

    override suspend fun activateVip(durationDays: Int) {
        val expiry = System.currentTimeMillis() + (durationDays.toLong() * 24 * 60 * 60 * 1000)
        userDao.updateVipStatus(isVip = true, expiry = expiry)
    }

    override suspend fun recordMatchResult(historyItem: MatchHistoryItem) {
        matchDao.insertMatch(
            MatchHistoryEntity(
                matchId = historyItem.id,
                challengeTitleEn = historyItem.challengeTitleEn,
                challengeTitleFa = historyItem.challengeTitleFa,
                score = historyItem.score,
                isWin = historyItem.isWin,
                timestamp = System.currentTimeMillis(),
                dateFormatted = historyItem.dateFormatted,
                xpEarned = historyItem.xpEarned,
                coinsEarned = historyItem.coinsEarned
            )
        )
    }

    override fun getMatchHistory(): Flow<List<MatchHistoryItem>> {
        return matchDao.getRecentMatches().map { list ->
            list.map {
                MatchHistoryItem(
                    id = it.matchId,
                    challengeTitleEn = it.challengeTitleEn,
                    challengeTitleFa = it.challengeTitleFa,
                    score = it.score,
                    isWin = it.isWin,
                    dateFormatted = it.dateFormatted,
                    xpEarned = it.xpEarned,
                    coinsEarned = it.coinsEarned
                )
            }
        }
    }

    override suspend fun resetProgress() {
        userDao.insertOrUpdate(UserProfileEntity())
    }

    private fun mapEntityToProfile(e: UserProfileEntity): UserProfile {
        val rank = try {
            RankTier.valueOf(e.rankTierName)
        } catch (_: Exception) {
            RankTier.fromXp(e.xp)
        }
        val calculatedRank = RankTier.fromXp(e.xp)
        val finalRank = if (calculatedRank.tierLevel > rank.tierLevel) calculatedRank else rank
        val calcLevel = (e.xp / 250) + 1
        return UserProfile(
            id = e.id,
            username = e.username,
            level = calcLevel,
            xp = e.xp,
            currentRank = finalRank,
            coins = e.coins,
            tickets = e.tickets,
            dailyStreak = e.dailyStreak,
            lastLoginDate = e.lastLoginDate,
            wins = e.wins,
            losses = e.losses,
            bestScore = e.bestScore,
            isVip = e.isVip,
            vipExpiryTimestamp = e.vipExpiryTimestamp,
            avatarId = e.avatarId
        )
    }

    private fun mapProfileToEntity(p: UserProfile): UserProfileEntity {
        return UserProfileEntity(
            id = p.id,
            username = p.username,
            level = p.level,
            xp = p.xp,
            rankTierName = p.currentRank.name,
            coins = p.coins,
            tickets = p.tickets,
            dailyStreak = p.dailyStreak,
            lastLoginDate = p.lastLoginDate,
            wins = p.wins,
            losses = p.losses,
            bestScore = p.bestScore,
            isVip = p.isVip,
            vipExpiryTimestamp = p.vipExpiryTimestamp,
            avatarId = p.avatarId
        )
    }
}

class LocalChallengeRepository(private val database: AppDatabase) : ChallengeRepository {
    private val challengeDao = database.challengeDao()

    init {
        // Prepopulate standard challenges
    }

    override fun getChallenges(): Flow<List<ChallengeItem>> {
        return challengeDao.getAllChallenges().map { list ->
            if (list.isEmpty()) {
                val seed = getSeedChallenges()
                challengeDao.insertAll(seed.map { mapItemToEntity(it) })
                seed
            } else {
                list.map { mapEntityToItem(it) }
            }
        }
    }

    override fun getChallengesByType(type: ChallengeType): Flow<List<ChallengeItem>> {
        return challengeDao.getChallengesByType(type.name).map { list ->
            list.map { mapEntityToItem(it) }
        }
    }

    override suspend fun getChallengeById(id: String): ChallengeItem? {
        val entity = challengeDao.getChallengeById(id) ?: return null
        return mapEntityToItem(entity)
    }

    override suspend fun markChallengeCompleted(id: String) {
        challengeDao.markCompleted(id)
    }

    override suspend fun refreshChallenges() {
        // Will refresh from remote API when online
    }

    private fun getSeedChallenges(): List<ChallengeItem> {
        return listOf(
            ChallengeItem(
                id = "ch_quick_01",
                titleEn = "Lightning Reflexes",
                titleFa = "واکنش رعدآسا",
                descEn = "Tap target nodes with precision within 30 seconds",
                descFa = "با سرعت و دقت روی اهداف قبل از انقضای زمان ضربه بزن",
                type = ChallengeType.QUICK,
                difficulty = ChallengeDifficulty.EASY,
                durationSeconds = 30,
                xpReward = 120,
                coinReward = 80,
                ticketCost = 1,
                participantsCount = 1420,
                rankRequired = RankTier.BRONZE
            ),
            ChallengeItem(
                id = "ch_daily_01",
                titleEn = "Daily Math Rush",
                titleFa = "محاسبه سریع روزانه",
                descEn = "Solve 10 rapid mental arithmetic equations without error",
                descFa = "۱۰ معادله ریاضی ذهنی را بدون اشتباه حل کن",
                type = ChallengeType.DAILY,
                difficulty = ChallengeDifficulty.MEDIUM,
                durationSeconds = 45,
                xpReward = 250,
                coinReward = 150,
                ticketCost = 0,
                participantsCount = 3890,
                rankRequired = RankTier.BRONZE
            ),
            ChallengeItem(
                id = "ch_time_01",
                titleEn = "Speed Blitz 60s",
                titleFa = "حمله زمانی ۶۰ ثانیه",
                descEn = "Score maximum points against dynamic obstacles",
                descFa = "در طول ۶۰ ثانیه بالاترین امتیاز ممکن را ثبت کن",
                type = ChallengeType.TIME_ATTACK,
                difficulty = ChallengeDifficulty.HARD,
                durationSeconds = 60,
                xpReward = 380,
                coinReward = 220,
                ticketCost = 2,
                participantsCount = 2190,
                rankRequired = RankTier.SILVER
            ),
            ChallengeItem(
                id = "ch_acc_01",
                titleEn = "Bullseye Precision",
                titleFa = "دقت نقطه‌زن",
                descEn = "High accuracy reaction test. Every mistake deducts health!",
                descFa = "تست دقت واکنش؛ هر اشتباه از جان شما کم می‌کند!",
                type = ChallengeType.ACCURACY,
                difficulty = ChallengeDifficulty.HARD,
                durationSeconds = 40,
                xpReward = 450,
                coinReward = 280,
                ticketCost = 2,
                participantsCount = 980,
                rankRequired = RankTier.GOLD
            ),
            ChallengeItem(
                id = "ch_endless_01",
                titleEn = "The Infinite Gauntlet",
                titleFa = "نبرد بی‌پایان",
                descEn = "Survive as the speed ramps up. 1 life, limitless glory.",
                descFa = "تا جایی که می‌توانی دوام بیاور؛ سرعت پیوسته بیشتر می‌شود!",
                type = ChallengeType.ENDLESS,
                difficulty = ChallengeDifficulty.EPIC,
                durationSeconds = 120,
                xpReward = 700,
                coinReward = 500,
                ticketCost = 3,
                participantsCount = 5420,
                rankRequired = RankTier.GOLD
            ),
            ChallengeItem(
                id = "ch_tourney_01",
                titleEn = "Grand Arena Championship",
                titleFa = "جام بزرگ قهرمانان آرنا",
                descEn = "Weekly national championship. Compete for the Golden Crown!",
                descFa = "تورنمنت هفتگی سرتاسری؛ رقابت برای کسب تاج طلایی!",
                type = ChallengeType.TOURNAMENT,
                difficulty = ChallengeDifficulty.EPIC,
                durationSeconds = 90,
                xpReward = 1200,
                coinReward = 1000,
                ticketCost = 5,
                participantsCount = 12400,
                rankRequired = RankTier.PLATINUM
            ),
            ChallengeItem(
                id = "ch_special_01",
                titleEn = "Cyber Neon Festival",
                titleFa = "جشنواره سایبر نئون",
                descEn = "Limited time festive challenge with double rewards and exclusive badge",
                descFa = "رویداد ویژه با پاداش دوبرابر و نشان اختصاصی نئون",
                type = ChallengeType.SPECIAL_EVENT,
                difficulty = ChallengeDifficulty.MEDIUM,
                durationSeconds = 50,
                xpReward = 600,
                coinReward = 450,
                ticketCost = 2,
                participantsCount = 8900,
                rankRequired = RankTier.SILVER
            )
        )
    }

    private fun mapEntityToItem(e: ChallengeEntity): ChallengeItem {
        return ChallengeItem(
            id = e.id,
            titleEn = e.titleEn,
            titleFa = e.titleFa,
            descEn = e.descEn,
            descFa = e.descFa,
            type = try { ChallengeType.valueOf(e.typeName) } catch (_: Exception) { ChallengeType.QUICK },
            difficulty = try { ChallengeDifficulty.valueOf(e.difficultyName) } catch (_: Exception) { ChallengeDifficulty.MEDIUM },
            durationSeconds = e.durationSeconds,
            xpReward = e.xpReward,
            coinReward = e.coinReward,
            ticketCost = e.ticketCost,
            participantsCount = e.participantsCount,
            isCompleted = e.isCompleted,
            isLocked = e.isLocked,
            rankRequired = try { RankTier.valueOf(e.rankRequiredTier) } catch (_: Exception) { RankTier.BRONZE }
        )
    }

    private fun mapItemToEntity(i: ChallengeItem): ChallengeEntity {
        return ChallengeEntity(
            id = i.id,
            titleEn = i.titleEn,
            titleFa = i.titleFa,
            descEn = i.descEn,
            descFa = i.descFa,
            typeName = i.type.name,
            difficultyName = i.difficulty.name,
            durationSeconds = i.durationSeconds,
            xpReward = i.xpReward,
            coinReward = i.coinReward,
            ticketCost = i.ticketCost,
            participantsCount = i.participantsCount,
            isCompleted = i.isCompleted,
            isLocked = i.isLocked,
            rankRequiredTier = i.rankRequired.name
        )
    }
}

class LocalLeaderboardRepository : LeaderboardRepository {
    override fun getLeaderboard(type: LeaderboardType): Flow<List<LeaderboardEntry>> = flow {
        emit(generateLeaderboard(type))
    }

    override suspend fun submitUserScore(score: Int, type: LeaderboardType) {
        // Will send to remote backend when connected
    }

    override suspend fun refreshLeaderboard(type: LeaderboardType) {
        // Will re-fetch from remote backend when connected
    }

    private fun generateLeaderboard(type: LeaderboardType): List<LeaderboardEntry> {
        val multiplier = when (type) {
            LeaderboardType.GLOBAL -> 1.0
            LeaderboardType.WEEKLY -> 0.6
            LeaderboardType.DAILY -> 0.25
            LeaderboardType.FRIENDS -> 0.5
        }

        return listOf(
            LeaderboardEntry(
                rank = 1,
                username = "Soroush_Striker",
                score = (14850 * multiplier).toInt(),
                level = 42,
                rankTier = RankTier.LEGEND,
                avatarEmoji = "👑",
                badge = "Champion",
                winsCount = 312
            ),
            LeaderboardEntry(
                rank = 2,
                username = "Arman_Ninja",
                score = (13200 * multiplier).toInt(),
                level = 38,
                rankTier = RankTier.GRANDMASTER,
                avatarEmoji = "⚡",
                badge = "Master Duelist",
                winsCount = 285
            ),
            LeaderboardEntry(
                rank = 3,
                username = "Yasaman_Apex",
                score = (11900 * multiplier).toInt(),
                level = 35,
                rankTier = RankTier.MASTER,
                avatarEmoji = "🔥",
                badge = "Sharp Shooter",
                winsCount = 240
            ),
            LeaderboardEntry(
                rank = 4,
                username = "Kaveh_Falcon",
                score = (10400 * multiplier).toInt(),
                level = 31,
                rankTier = RankTier.DIAMOND,
                avatarEmoji = "🦅",
                badge = null,
                winsCount = 198
            ),
            LeaderboardEntry(
                rank = 5,
                username = "ArenaMaster",
                score = (8900 * multiplier).toInt(),
                level = 24,
                rankTier = RankTier.PLATINUM,
                avatarEmoji = "🎮",
                isCurrentUser = true,
                badge = "Rising Star",
                winsCount = 145
            ),
            LeaderboardEntry(
                rank = 6,
                username = "Bahar_Storm",
                score = (7800 * multiplier).toInt(),
                level = 22,
                rankTier = RankTier.GOLD,
                avatarEmoji = "🌪️",
                isFriend = true,
                badge = null,
                winsCount = 120
            ),
            LeaderboardEntry(
                rank = 7,
                username = "Nima_Pixel",
                score = (6500 * multiplier).toInt(),
                level = 19,
                rankTier = RankTier.SILVER,
                avatarEmoji = "👾",
                badge = null,
                winsCount = 95
            ),
            LeaderboardEntry(
                rank = 8,
                username = "Danyal_Speed",
                score = (5200 * multiplier).toInt(),
                level = 16,
                rankTier = RankTier.SILVER,
                avatarEmoji = "🚀",
                badge = null,
                winsCount = 74
            )
        )
    }
}

class LocalRewardRepository(private val database: AppDatabase) : RewardRepository {
    private val streakDao = database.dailyStreakDao()
    private val achievementDao = database.achievementDao()

    override fun getDailyStreakRewards(): Flow<List<DailyStreakReward>> {
        return streakDao.getAllDailyStreaks().map { list ->
            if (list.isEmpty()) {
                val seed = (1..7).map { day ->
                    DailyStreakEntity(
                        dayNumber = day,
                        rewardXp = day * 100,
                        rewardCoins = day * 75,
                        rewardTickets = if (day % 3 == 0 || day == 7) 2 else 1,
                        isClaimed = day < 3,
                        isToday = day == 3
                    )
                }
                streakDao.insertAll(seed)
                seed.map { mapStreak(it) }
            } else {
                list.map { mapStreak(it) }
            }
        }
    }

    override suspend fun claimDailyStreak(dayNumber: Int): DailyStreakReward? {
        streakDao.markClaimed(dayNumber)
        return DailyStreakReward(
            dayNumber = dayNumber,
            rewardXp = dayNumber * 100,
            rewardCoins = dayNumber * 75,
            rewardTickets = if (dayNumber % 3 == 0 || dayNumber == 7) 2 else 1,
            isClaimed = true,
            isToday = true
        )
    }

    override fun getAchievements(): Flow<List<AchievementItem>> {
        return achievementDao.getAllAchievements().map { list ->
            if (list.isEmpty()) {
                val seed = getSeedAchievements()
                achievementDao.insertAll(seed.map { mapAchEntity(it) })
                seed
            } else {
                list.map { mapAchItem(it) }
            }
        }
    }

    override suspend fun updateAchievementProgress(code: String, progressIncrement: Int) {
        val existing = achievementDao.getAchievementByCode(code) ?: return
        val newProgress = existing.currentProgress + progressIncrement
        achievementDao.updateProgress(code, newProgress)
    }

    override suspend fun claimAchievement(code: String) {
        val existing = achievementDao.getAchievementByCode(code) ?: return
        achievementDao.updateAchievement(existing.copy(isUnlocked = true))
    }

    override fun getStoreRewards(): Flow<List<RewardItem>> = flow {
        emit(
            listOf(
                RewardItem(
                    id = "rew_chest_bronze",
                    titleEn = "Bronze Supply Chest",
                    titleFa = "صندوق تدارکات برنز",
                    descEn = "Contains 250 Coins + 100 XP",
                    descFa = "حاوی ۲۵۰ سکه + ۱۰۰ امتیاز تجربه",
                    type = RewardType.MYSTERY_CHEST,
                    amount = 1,
                    iconSymbol = "📦",
                    coinCost = 200
                ),
                RewardItem(
                    id = "rew_tickets_5",
                    titleEn = "5 Tournament Tickets",
                    titleFa = "۵ بلیط تورنمنت",
                    descEn = "Enter 5 championship battles",
                    descFa = "ورود به ۵ نبرد قهرمانی",
                    type = RewardType.TICKETS,
                    amount = 5,
                    iconSymbol = "🎟️",
                    coinCost = 450
                ),
                RewardItem(
                    id = "rew_chest_gold",
                    titleEn = "Golden Champion Chest",
                    titleFa = "صندوق طلایی قهرمانان",
                    descEn = "Contains 1,500 Coins, 500 XP & 3 Tickets",
                    descFa = "حاوی ۱۵۰۰ سکه، ۵۰۰ XP و ۳ بلیط تورنمنت",
                    type = RewardType.MYSTERY_CHEST,
                    amount = 1,
                    iconSymbol = "🏆",
                    coinCost = 950
                ),
                RewardItem(
                    id = "rew_vip_badge",
                    titleEn = "VIP Elite Aura",
                    titleFa = "هاله اختصاصی VIP",
                    descEn = "Exclusive glowing border for your avatar",
                    descFa = "کادر درخشان و اختصاصی برای آواتار شما",
                    type = RewardType.VIP_BADGE,
                    amount = 1,
                    iconSymbol = "👑",
                    coinCost = 0,
                    isVipOnly = true
                )
            )
        )
    }

    override suspend fun claimStoreReward(rewardId: String): Boolean {
        return true
    }

    private fun getSeedAchievements(): List<AchievementItem> {
        return listOf(
            AchievementItem(
                id = "ach_first_win",
                code = "FIRST_WIN",
                titleEn = "First Victory",
                titleFa = "اولین پیروزی",
                descEn = "Win your very first challenge arena match",
                descFa = "در اولین مسابقه چالش آرنا پیروز شو",
                iconSymbol = "🥇",
                isUnlocked = true,
                currentProgress = 1,
                targetProgress = 1,
                rewardXp = 100,
                rewardCoins = 50
            ),
            AchievementItem(
                id = "ach_10_wins",
                code = "10_WINS",
                titleEn = "Arena Contender",
                titleFa = "مدعی میدان",
                descEn = "Win 10 challenge matches",
                descFa = "۱۰ مسابقه چالش را با پیروزی تمام کن",
                iconSymbol = "⚔️",
                isUnlocked = true,
                currentProgress = 10,
                targetProgress = 10,
                rewardXp = 250,
                rewardCoins = 150
            ),
            AchievementItem(
                id = "ach_100_ch",
                code = "100_CHALLENGES",
                titleEn = "Centurion",
                titleFa = "سنتوریون",
                descEn = "Participate in 100 arena challenges",
                descFa = "در ۱۰۰ چالش مختلف آرنا شرکت کن",
                iconSymbol = "🛡️",
                isUnlocked = false,
                currentProgress = 24,
                targetProgress = 100,
                rewardXp = 1000,
                rewardCoins = 750
            ),
            AchievementItem(
                id = "ach_perfect",
                code = "PERFECT_SCORE",
                titleEn = "Flawless Execution",
                titleFa = "اجرای بی‌نقص",
                descEn = "Score 100% accuracy in an Accuracy Challenge",
                descFa = "در چالش دقت، امتیاز کامل ۱۰۰٪ کسب کن",
                iconSymbol = "🎯",
                isUnlocked = false,
                currentProgress = 0,
                targetProgress = 1,
                rewardXp = 400,
                rewardCoins = 300
            ),
            AchievementItem(
                id = "ach_7_streak",
                code = "7_DAY_STREAK",
                titleEn = "Dedicated Warrior",
                titleFa = "جنگجوی متعهد",
                descEn = "Maintain a 7-day login streak",
                descFa = "۷ روز متوالی وارد برنامه شو",
                iconSymbol = "🔥",
                isUnlocked = false,
                currentProgress = 3,
                targetProgress = 7,
                rewardXp = 500,
                rewardCoins = 400
            ),
            AchievementItem(
                id = "ach_top100",
                code = "TOP_100",
                titleEn = "Top 100 Legend",
                titleFa = "صد برتر آرنا",
                descEn = "Climb into the global Top 100 leaderboard",
                descFa = "به جمع ۱۰۰ نفر اول جدول رده‌بندی جهانی برس",
                iconSymbol = "🌟",
                isUnlocked = true,
                currentProgress = 1,
                targetProgress = 1,
                rewardXp = 750,
                rewardCoins = 500
            ),
            AchievementItem(
                id = "ach_top10",
                code = "TOP_10",
                titleEn = "Apex Predator",
                titleFa = "۱۰ نفر برتر",
                descEn = "Reach the Top 10 leaderboard in any category",
                descFa = "در یکی از بخش‌های جدول به جمع ۱۰ نفر برتر برس",
                iconSymbol = "💎",
                isUnlocked = true,
                currentProgress = 1,
                targetProgress = 1,
                rewardXp = 1500,
                rewardCoins = 1000
            ),
            AchievementItem(
                id = "ach_champ",
                code = "CHAMPION",
                titleEn = "Grand Champion",
                titleFa = "قهرمان بزرگ",
                descEn = "Hold Rank #1 on the weekly tournament leaderboard",
                descFa = "رتبه ۱ تورنمنت هفتگی را تصاحب کن",
                iconSymbol = "👑",
                isUnlocked = false,
                currentProgress = 0,
                targetProgress = 1,
                rewardXp = 3000,
                rewardCoins = 2500
            )
        )
    }

    private fun mapStreak(e: DailyStreakEntity) = DailyStreakReward(
        dayNumber = e.dayNumber,
        rewardXp = e.rewardXp,
        rewardCoins = e.rewardCoins,
        rewardTickets = e.rewardTickets,
        isClaimed = e.isClaimed,
        isToday = e.isToday
    )

    private fun mapAchItem(e: AchievementEntity) = AchievementItem(
        id = e.id,
        code = e.code,
        titleEn = e.titleEn,
        titleFa = e.titleFa,
        descEn = e.descEn,
        descFa = e.descFa,
        iconSymbol = e.iconSymbol,
        isUnlocked = e.isUnlocked,
        currentProgress = e.currentProgress,
        targetProgress = e.targetProgress,
        rewardXp = e.rewardXp,
        rewardCoins = e.rewardCoins
    )

    private fun mapAchEntity(i: AchievementItem) = AchievementEntity(
        id = i.id,
        code = i.code,
        titleEn = i.titleEn,
        titleFa = i.titleFa,
        descEn = i.descEn,
        descFa = i.descFa,
        iconSymbol = i.iconSymbol,
        isUnlocked = i.isUnlocked,
        currentProgress = i.currentProgress,
        targetProgress = i.targetProgress,
        rewardXp = i.rewardXp,
        rewardCoins = i.rewardCoins
    )
}
