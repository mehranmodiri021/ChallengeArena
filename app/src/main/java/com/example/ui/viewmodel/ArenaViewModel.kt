package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.TapsellManager
import com.example.billing.BazaarBillingManager
import com.example.config.BazaarConfig
import com.example.config.EconomyConfig
import com.example.data.database.AppDatabase
import com.example.data.preferences.SettingsDataStore
import com.example.data.repository.LocalChallengeRepository
import com.example.data.repository.LocalLeaderboardRepository
import com.example.data.repository.LocalRewardRepository
import com.example.data.repository.LocalUserRepository
import com.example.domain.model.AchievementItem
import com.example.domain.model.ChallengeItem
import com.example.domain.model.ChallengeType
import com.example.domain.model.DailyStreakReward
import com.example.domain.model.LeaderboardEntry
import com.example.domain.model.LeaderboardType
import com.example.domain.model.MatchHistoryItem
import com.example.domain.model.RankTier
import com.example.domain.model.RewardItem
import com.example.domain.model.UserProfile
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.ArenaTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class ArenaGameStatus { IDLE, COUNTDOWN, PLAYING, PAUSED, FINISHED }

data class ActiveMathQuestion(
    val equation: String,
    val options: List<Int>,
    val correctIndex: Int
)

data class ActiveTarget(
    val id: Int,
    val posXFraction: Float,
    val posYFraction: Float,
    val sizeDp: Int,
    val isGolden: Boolean = false,
    val spawnTimeMs: Long = System.currentTimeMillis()
)

data class ArenaGameState(
    val status: ArenaGameStatus = ArenaGameStatus.IDLE,
    val activeChallenge: ChallengeItem? = null,
    val countdownSeconds: Int = 3,
    val score: Int = 0,
    val combo: Int = 1,
    val maxCombo: Int = 1,
    val secondsLeft: Int = 30,
    val mathQuestion: ActiveMathQuestion? = null,
    val currentTarget: ActiveTarget? = null,
    val xpEarned: Int = 0,
    val coinsEarned: Int = 0,
    val isNewHighScore: Boolean = false,
    val isAntiCheatFlagged: Boolean = false
)

class ArenaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val userRepo = LocalUserRepository(db)
    val challengeRepo = LocalChallengeRepository(db)
    val leaderboardRepo = LocalLeaderboardRepository()
    val rewardRepo = LocalRewardRepository(db)

    val billingManager = BazaarBillingManager.getInstance(application)
    val tapsellManager = TapsellManager.getInstance(application)
    private val settingsDataStore = SettingsDataStore(application)

    // UI Preferences (Persisted with DataStore)
    private val _appLanguage = MutableStateFlow(AppLanguage.PERSIAN)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _activeTheme = MutableStateFlow(ArenaTheme.DEFAULT)
    val activeTheme: StateFlow<ArenaTheme> = _activeTheme.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(true)
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    // Navigation & Screen selection
    private val _currentNavDestination = MutableStateFlow("home")
    val currentNavDestination: StateFlow<String> = _currentNavDestination.asStateFlow()

    // Challenges filter
    private val _selectedChallengeType = MutableStateFlow<ChallengeType?>(null)
    val selectedChallengeType: StateFlow<ChallengeType?> = _selectedChallengeType.asStateFlow()

    // Leaderboard active tab
    private val _selectedLeaderboardType = MutableStateFlow(LeaderboardType.GLOBAL)
    val selectedLeaderboardType: StateFlow<LeaderboardType> = _selectedLeaderboardType.asStateFlow()

    // Feedback message (snackbar / banner)
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    // Game state
    private val _gameState = MutableStateFlow(ArenaGameState())
    val gameState: StateFlow<ArenaGameState> = _gameState.asStateFlow()
    private var gameTimerJob: Job? = null
    private var countdownJob: Job? = null

    // Anti-Cheat tracking
    private var matchStartTimeMs: Long = 0L
    private var totalTapEvents: Int = 0

    // Room Flows
    val userProfile: StateFlow<UserProfile> = userRepo.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val challenges: StateFlow<List<ChallengeItem>> = challengeRepo.getChallenges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements: StateFlow<List<AchievementItem>> = rewardRepo.getAchievements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyStreaks: StateFlow<List<DailyStreakReward>> = rewardRepo.getDailyStreakRewards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val storeRewards: StateFlow<List<RewardItem>> = rewardRepo.getStoreRewards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val matchHistory: StateFlow<List<MatchHistoryItem>> = userRepo.getMatchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentLeaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val currentLeaderboard: StateFlow<List<LeaderboardEntry>> = _currentLeaderboard.asStateFlow()

    init {
        // Collect persistent settings from DataStore
        viewModelScope.launch {
            settingsDataStore.settingsFlow.collect { settings ->
                _appLanguage.value = settings.language
                _activeTheme.value = settings.theme
                _soundEnabled.value = settings.soundEnabled
                _hapticEnabled.value = settings.hapticEnabled
                _notificationsEnabled.value = settings.notificationsEnabled
            }
        }

        billingManager.initializeConnection()
        tapsellManager.initializeTapsell()
        loadLeaderboard(LeaderboardType.GLOBAL)
    }

    fun navigateTo(dest: String) {
        _currentNavDestination.value = dest
    }

    fun setLanguage(lang: AppLanguage) {
        _appLanguage.value = lang
        viewModelScope.launch { settingsDataStore.setLanguage(lang) }
    }

    fun setTheme(theme: ArenaTheme) {
        _activeTheme.value = theme
        viewModelScope.launch { settingsDataStore.setTheme(theme) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        _soundEnabled.value = enabled
        viewModelScope.launch { settingsDataStore.setSoundEnabled(enabled) }
    }

    fun setHapticEnabled(enabled: Boolean) {
        _hapticEnabled.value = enabled
        viewModelScope.launch { settingsDataStore.setHapticEnabled(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        viewModelScope.launch { settingsDataStore.setNotificationsEnabled(enabled) }
    }

    fun setChallengeTypeFilter(type: ChallengeType?) {
        _selectedChallengeType.value = type
    }

    fun setLeaderboardType(type: LeaderboardType) {
        _selectedLeaderboardType.value = type
        loadLeaderboard(type)
    }

    private fun loadLeaderboard(type: LeaderboardType) {
        viewModelScope.launch {
            leaderboardRepo.getLeaderboard(type).collect {
                _currentLeaderboard.value = it
            }
        }
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    fun showFeedback(msg: String) {
        _feedbackMessage.value = msg
    }

    // =========================================================================
    // ARENA LIVE GAME ENGINE WITH DEPTH & ANTI-CHEAT
    // =========================================================================
    fun startChallenge(challenge: ChallengeItem) {
        countdownJob?.cancel()
        gameTimerJob?.cancel()
        _currentNavDestination.value = "arena"

        // Step 1: 3-2-1 Countdown
        _gameState.value = ArenaGameState(
            status = ArenaGameStatus.COUNTDOWN,
            activeChallenge = challenge,
            countdownSeconds = 3,
            secondsLeft = challenge.durationSeconds
        )

        triggerHaptic(HapticType.LIGHT)

        countdownJob = viewModelScope.launch {
            for (c in 3 downTo 1) {
                _gameState.value = _gameState.value.copy(countdownSeconds = c)
                triggerHaptic(HapticType.LIGHT)
                delay(800)
            }
            // Transition to PLAYING
            launchGamePlay(challenge)
        }
    }

    private fun launchGamePlay(challenge: ChallengeItem) {
        matchStartTimeMs = System.currentTimeMillis()
        totalTapEvents = 0

        val initialQuestion = if (challenge.type == ChallengeType.DAILY) generateMathQuestion() else null
        val initialTarget = if (challenge.type != ChallengeType.DAILY) generateRandomTarget() else null

        _gameState.value = _gameState.value.copy(
            status = ArenaGameStatus.PLAYING,
            score = 0,
            combo = 1,
            maxCombo = 1,
            secondsLeft = challenge.durationSeconds,
            mathQuestion = initialQuestion,
            currentTarget = initialTarget
        )

        triggerHaptic(HapticType.SUCCESS)

        gameTimerJob = viewModelScope.launch {
            while (_gameState.value.secondsLeft > 0 && _gameState.value.status == ArenaGameStatus.PLAYING) {
                delay(1000)
                // If paused while waiting, loop will respect status
                if (_gameState.value.status != ArenaGameStatus.PLAYING) continue

                val newSeconds = _gameState.value.secondsLeft - 1
                if (newSeconds <= 0) {
                    finishGame()
                } else {
                    _gameState.value = _gameState.value.copy(secondsLeft = newSeconds)
                }
            }
        }
    }

    fun pauseGame() {
        if (_gameState.value.status == ArenaGameStatus.PLAYING) {
            _gameState.value = _gameState.value.copy(status = ArenaGameStatus.PAUSED)
            triggerHaptic(HapticType.LIGHT)
        }
    }

    fun resumeGame() {
        if (_gameState.value.status == ArenaGameStatus.PAUSED) {
            _gameState.value = _gameState.value.copy(status = ArenaGameStatus.PLAYING)
            triggerHaptic(HapticType.LIGHT)
        }
    }

    fun onTargetTapped(targetId: Int) {
        if (_gameState.value.status != ArenaGameStatus.PLAYING) return
        val currentTarget = _gameState.value.currentTarget ?: return

        // Anti-Cheat: Reaction Time sanity check
        val now = System.currentTimeMillis()
        val reactionMs = now - currentTarget.spawnTimeMs
        if (reactionMs < EconomyConfig.MIN_TARGET_REACTION_MS) {
            // Humanly impossible reaction time (<70ms); reject as macro/bot click
            return
        }

        totalTapEvents++

        if (currentTarget.id == targetId) {
            triggerHaptic(HapticType.MEDIUM)
            val basePoints = if (currentTarget.isGolden) EconomyConfig.GOLDEN_TARGET_TAP_POINTS else EconomyConfig.BASE_TARGET_TAP_POINTS
            val points = basePoints * _gameState.value.combo
            val newScore = _gameState.value.score + points
            val newCombo = (_gameState.value.combo + 1).coerceAtMost(EconomyConfig.MAX_COMBO_MULTIPLIER)
            val newMaxCombo = maxOf(_gameState.value.maxCombo, newCombo)

            _gameState.value = _gameState.value.copy(
                score = newScore,
                combo = newCombo,
                maxCombo = newMaxCombo,
                currentTarget = generateRandomTarget()
            )
        }
    }

    fun onMathOptionSelected(selectedIndex: Int) {
        if (_gameState.value.status != ArenaGameStatus.PLAYING) return
        val question = _gameState.value.mathQuestion ?: return

        totalTapEvents++

        if (selectedIndex == question.correctIndex) {
            triggerHaptic(HapticType.MEDIUM)
            val points = EconomyConfig.MATH_CORRECT_POINTS * _gameState.value.combo
            val newScore = _gameState.value.score + points
            val newCombo = (_gameState.value.combo + 1).coerceAtMost(EconomyConfig.MAX_COMBO_MULTIPLIER)
            val newMaxCombo = maxOf(_gameState.value.maxCombo, newCombo)

            _gameState.value = _gameState.value.copy(
                score = newScore,
                combo = newCombo,
                maxCombo = newMaxCombo,
                mathQuestion = generateMathQuestion()
            )
        } else {
            triggerHaptic(HapticType.ERROR)
            _gameState.value = _gameState.value.copy(
                combo = 1,
                mathQuestion = generateMathQuestion()
            )
        }
    }

    private fun finishGame() {
        gameTimerJob?.cancel()
        val current = _gameState.value
        val challenge = current.activeChallenge ?: return
        var finalScore = current.score

        // Anti-Cheat Sanity Check:
        val elapsedDurationMs = maxOf(1000L, System.currentTimeMillis() - matchStartTimeMs)
        val elapsedSeconds = elapsedDurationMs / 1000.0
        val pointsPerSecond = finalScore / elapsedSeconds
        var isAntiCheatFlagged = false

        if (finalScore > EconomyConfig.MAX_POSSIBLE_SCORE_30S || pointsPerSecond > EconomyConfig.MAX_POINTS_PER_SECOND) {
            finalScore = EconomyConfig.MAX_POSSIBLE_SCORE_30S
            isAntiCheatFlagged = true
        }

        val isWin = finalScore >= EconomyConfig.BASE_WIN_SCORE_THRESHOLD
        val isVip = userProfile.value.isVip

        val xpBonus = if (isVip) (challenge.xpReward * EconomyConfig.VIP_XP_MULTIPLIER).toInt() else challenge.xpReward
        val coinsBonus = if (isVip) (challenge.coinReward * EconomyConfig.VIP_COIN_MULTIPLIER).toInt() else challenge.coinReward

        _gameState.value = current.copy(
            status = ArenaGameStatus.FINISHED,
            secondsLeft = 0,
            score = finalScore,
            xpEarned = xpBonus,
            coinsEarned = coinsBonus,
            isNewHighScore = finalScore > userProfile.value.bestScore,
            isAntiCheatFlagged = isAntiCheatFlagged
        )

        triggerHaptic(if (isWin) HapticType.SUCCESS else HapticType.MEDIUM)

        viewModelScope.launch {
            userRepo.addXpAndCoins(xpBonus, coinsBonus)
            val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            userRepo.recordMatchResult(
                MatchHistoryItem(
                    id = "match_${System.currentTimeMillis()}",
                    challengeTitleEn = challenge.titleEn,
                    challengeTitleFa = challenge.titleFa,
                    score = finalScore,
                    isWin = isWin,
                    dateFormatted = formatter.format(Date()),
                    xpEarned = xpBonus,
                    coinsEarned = coinsBonus
                )
            )
            challengeRepo.markChallengeCompleted(challenge.id)
            rewardRepo.updateAchievementProgress("10_WINS", if (isWin) 1 else 0)
            rewardRepo.updateAchievementProgress("100_CHALLENGES", 1)
        }
    }

    fun exitGame() {
        countdownJob?.cancel()
        gameTimerJob?.cancel()
        _gameState.value = ArenaGameState(status = ArenaGameStatus.IDLE)
        _currentNavDestination.value = "home"
    }

    private fun generateRandomTarget(): ActiveTarget {
        return ActiveTarget(
            id = Random.nextInt(10000),
            posXFraction = Random.nextFloat().coerceIn(0.12f, 0.82f),
            posYFraction = Random.nextFloat().coerceIn(0.12f, 0.72f),
            sizeDp = Random.nextInt(60, 84),
            isGolden = Random.nextInt(8) == 0,
            spawnTimeMs = System.currentTimeMillis()
        )
    }

    private fun generateMathQuestion(): ActiveMathQuestion {
        val a = Random.nextInt(5, 40)
        val b = Random.nextInt(3, 25)
        val op = Random.nextInt(2) // 0: +, 1: -
        val correct = if (op == 0) a + b else a - b
        val eq = if (op == 0) "$a + $b = ?" else "$a - $b = ?"

        val wrong1 = correct + Random.nextInt(1, 5)
        val wrong2 = (correct - Random.nextInt(1, 5)).coerceAtLeast(1)
        val options = listOf(correct, wrong1, wrong2).shuffled()
        return ActiveMathQuestion(
            equation = eq,
            options = options,
            correctIndex = options.indexOf(correct)
        )
    }

    // =========================================================================
    // REWARDS & DAILY STREAK
    // =========================================================================
    fun claimDailyStreakReward(dayNumber: Int) {
        viewModelScope.launch {
            val reward = rewardRepo.claimDailyStreak(dayNumber)
            if (reward != null) {
                userRepo.addXpAndCoins(reward.rewardXp, reward.rewardCoins)
                if (reward.rewardTickets > 0) {
                    userRepo.addTickets(reward.rewardTickets)
                }
                triggerHaptic(HapticType.SUCCESS)
                showFeedback(
                    if (_appLanguage.value == AppLanguage.PERSIAN)
                        "پاداش روز $dayNumber دریافت شد! +${reward.rewardCoins} سکه، +${reward.rewardXp} XP"
                    else
                        "Day $dayNumber reward claimed! +${reward.rewardCoins} Coins, +${reward.rewardXp} XP"
                )
            }
        }
    }

    fun purchaseStoreItem(reward: RewardItem) {
        viewModelScope.launch {
            if (userProfile.value.coins < reward.coinCost) {
                triggerHaptic(HapticType.ERROR)
                showFeedback(
                    if (_appLanguage.value == AppLanguage.PERSIAN) "سکه کافی نداری!" else "Not enough coins!"
                )
                return@launch
            }
            val success = userRepo.spendCoins(reward.coinCost)
            if (success) {
                when (reward.type) {
                    com.example.domain.model.RewardType.TICKETS -> userRepo.addTickets(reward.amount)
                    com.example.domain.model.RewardType.COINS -> userRepo.addXpAndCoins(0, reward.amount)
                    com.example.domain.model.RewardType.XP -> userRepo.addXpAndCoins(reward.amount, 0)
                    com.example.domain.model.RewardType.MYSTERY_CHEST -> userRepo.addXpAndCoins(350, 400)
                    com.example.domain.model.RewardType.VIP_BADGE -> {}
                }
                triggerHaptic(HapticType.SUCCESS)
                showFeedback(
                    if (_appLanguage.value == AppLanguage.PERSIAN) "${reward.titleFa} خریداری شد!" else "${reward.titleEn} purchased!"
                )
            }
        }
    }

    // =========================================================================
    // MONETIZATION: CAFE BAZAAR & TAPSELL
    // =========================================================================
    fun subscribeVip(productId: String) {
        viewModelScope.launch {
            billingManager.launchPurchase(productId) { success, _ ->
                if (success) {
                    val days = if (productId == BazaarConfig.PRODUCT_ID_VIP_YEARLY) 365 else 30
                    viewModelScope.launch {
                        userRepo.activateVip(days)
                        userRepo.addTickets(EconomyConfig.VIP_BONUS_INITIAL_TICKETS)
                        triggerHaptic(HapticType.SUCCESS)
                        showFeedback(
                            if (_appLanguage.value == AppLanguage.PERSIAN)
                                "اشتراک VIP با موفقیت فعال شد! از امکانات طلایی لذت ببرید."
                            else
                                "VIP membership successfully activated! Enjoy exclusive perks."
                        )
                    }
                } else {
                    triggerHaptic(HapticType.ERROR)
                    showFeedback("Purchase failed or was canceled.")
                }
            }
        }
    }

    fun watchTapsellRewardedAd() {
        viewModelScope.launch {
            tapsellManager.showRewardedAd(
                onAdStarted = {
                    showFeedback(
                        if (_appLanguage.value == AppLanguage.PERSIAN)
                            "ویدیو تبلیغاتی تپسل شروع شد..."
                        else
                            "Tapsell video ad started..."
                    )
                },
                onRewardGranted = { coins, xp, tickets ->
                    viewModelScope.launch {
                        userRepo.addXpAndCoins(xp, coins)
                        userRepo.addTickets(tickets)
                        triggerHaptic(HapticType.SUCCESS)
                        showFeedback(
                            if (_appLanguage.value == AppLanguage.PERSIAN)
                                "پاداش ویدیوی تپسل دریافت شد: +$coins سکه و +$xp XP!"
                            else
                                "Tapsell ad reward granted: +$coins Coins & +$xp XP!"
                        )
                    }
                },
                onError = { err ->
                    showFeedback(err)
                }
            )
        }
    }

    fun resetLocalProgress() {
        viewModelScope.launch {
            userRepo.resetProgress()
            triggerHaptic(HapticType.MEDIUM)
            showFeedback(
                if (_appLanguage.value == AppLanguage.PERSIAN)
                    "اطلاعات محلی بازنشانی شد."
                else
                    "Local progress has been reset."
            )
        }
    }

    // =========================================================================
    // HAPTIC FEEDBACK
    // =========================================================================
    enum class HapticType { LIGHT, MEDIUM, SUCCESS, ERROR }

    fun triggerHaptic(type: HapticType) {
        if (!_hapticEnabled.value) return
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = getApplication<Application>().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = when (type) {
                        HapticType.LIGHT -> VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE)
                        HapticType.MEDIUM -> VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE)
                        HapticType.SUCCESS -> VibrationEffect.createWaveform(longArrayOf(0, 30, 60, 40), -1)
                        HapticType.ERROR -> VibrationEffect.createWaveform(longArrayOf(0, 50, 40, 50), -1)
                    }
                    v.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(30)
                }
            }
        } catch (_: Exception) {
            // Graceful fallback if device lacks vibrator hardware
        }
    }
}
