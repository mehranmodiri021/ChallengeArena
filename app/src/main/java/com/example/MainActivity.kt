package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.model.ChallengeType
import com.example.ui.components.AboutDialog
import com.example.ui.components.ArenaBottomNavigation
import com.example.ui.components.ArenaHeader
import com.example.ui.components.DailyStreakDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.VipDialog
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.EnglishStrings
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.LocalStrings
import com.example.ui.localization.PersianStrings
import com.example.ui.screens.ArenaScreen
import com.example.ui.screens.ChallengesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.theme.ChallengeArenaTheme
import com.example.ui.theme.LocalArenaPalette
import com.example.ui.viewmodel.ArenaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChallengeArenaApp()
        }
    }
}

@Composable
fun ChallengeArenaApp(
    viewModel: ArenaViewModel = viewModel()
) {
    val language by viewModel.appLanguage.collectAsState()
    val activeTheme by viewModel.activeTheme.collectAsState()
    val strings = if (language == AppLanguage.PERSIAN) PersianStrings else EnglishStrings

    val currentDestination by viewModel.currentNavDestination.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val challenges by viewModel.challenges.collectAsState()
    val selectedChallengeType by viewModel.selectedChallengeType.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val dailyStreaks by viewModel.dailyStreaks.collectAsState()
    val storeRewards by viewModel.storeRewards.collectAsState()
    val matchHistory by viewModel.matchHistory.collectAsState()
    val currentLeaderboard by viewModel.currentLeaderboard.collectAsState()
    val selectedLeaderboardType by viewModel.selectedLeaderboardType.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    val feedbackMessage by viewModel.feedbackMessage.collectAsState()

    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()

    var showVipDialog by remember { mutableStateOf(false) }
    var showStreakDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalAppLanguage provides language,
        LocalStrings provides strings,
        LocalLayoutDirection provides language.layoutDirection
    ) {
        ChallengeArenaTheme(activeTheme = activeTheme) {
            val palette = LocalArenaPalette.current

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("arena_root_scaffold"),
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    ArenaHeader(
                        userProfile = userProfile,
                        onVipClick = { showVipDialog = true },
                        onSettingsClick = { showSettingsDialog = true },
                        onAboutClick = { showAboutDialog = true }
                    )
                },
                bottomBar = {
                    ArenaBottomNavigation(
                        currentDestination = currentDestination,
                        onNavigate = { dest -> viewModel.navigateTo(dest) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentDestination) {
                        "home" -> {
                            HomeScreen(
                                userProfile = userProfile,
                                challenges = challenges,
                                leaderboardPreview = currentLeaderboard,
                                onPlayQuickMatch = {
                                    val quickCh = challenges.firstOrNull { it.type == ChallengeType.QUICK }
                                        ?: challenges.firstOrNull()
                                    if (quickCh != null) viewModel.startChallenge(quickCh)
                                },
                                onStartChallenge = { challenge ->
                                    viewModel.startChallenge(challenge)
                                },
                                onViewAllChallenges = { viewModel.navigateTo("challenges") },
                                onViewLeaderboard = { viewModel.navigateTo("leaderboard") },
                                onOpenDailyStreak = { showStreakDialog = true },
                                onOpenVip = { showVipDialog = true }
                            )
                        }

                        "challenges" -> {
                            ChallengesScreen(
                                challenges = challenges,
                                userProfile = userProfile,
                                selectedType = selectedChallengeType,
                                onTypeSelected = { type -> viewModel.setChallengeTypeFilter(type) },
                                onStartChallenge = { challenge ->
                                    viewModel.startChallenge(challenge)
                                }
                            )
                        }

                        "arena" -> {
                            ArenaScreen(
                                gameState = gameState,
                                onTargetTapped = { id -> viewModel.onTargetTapped(id) },
                                onMathOptionSelected = { idx -> viewModel.onMathOptionSelected(idx) },
                                onPlayQuickMatch = {
                                    val ch = challenges.firstOrNull()
                                    if (ch != null) viewModel.startChallenge(ch)
                                },
                                onExitGame = { viewModel.exitGame() },
                                onPauseGame = { viewModel.pauseGame() },
                                onResumeGame = { viewModel.resumeGame() }
                            )
                        }

                        "leaderboard" -> {
                            LeaderboardScreen(
                                leaderboardEntries = currentLeaderboard,
                                selectedType = selectedLeaderboardType,
                                userProfile = userProfile,
                                onTypeSelected = { type -> viewModel.setLeaderboardType(type) }
                            )
                        }

                        "rewards" -> {
                            RewardsScreen(
                                userProfile = userProfile,
                                dailyStreaks = dailyStreaks,
                                storeRewards = storeRewards,
                                onClaimStreak = { day -> viewModel.claimDailyStreakReward(day) },
                                onWatchTapsellAd = { viewModel.watchTapsellRewardedAd() },
                                onPurchaseStoreItem = { reward -> viewModel.purchaseStoreItem(reward) },
                                onOpenVip = { showVipDialog = true }
                            )
                        }

                        "profile" -> {
                            ProfileScreen(
                                userProfile = userProfile,
                                achievements = achievements,
                                matchHistory = matchHistory
                            )
                        }
                    }

                    // Floating Feedback Message Banner
                    AnimatedVisibility(
                        visible = feedbackMessage != null,
                        enter = slideInVertically(initialOffsetY = { -it }),
                        exit = slideOutVertically(targetOffsetY = { -it }),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                    ) {
                        feedbackMessage?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(palette.primary)
                                    .clickable { viewModel.clearFeedback() }
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                                    .testTag("floating_feedback_banner")
                            ) {
                                Text(
                                    text = msg,
                                    color = palette.onPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // MODALS
            if (showVipDialog) {
                VipDialog(
                    isVipActive = userProfile.isVip,
                    onSubscribe = { sku -> viewModel.subscribeVip(sku) },
                    onRestore = {
                        viewModel.showFeedback(
                            if (language == AppLanguage.PERSIAN) "خریدهای پیشین بررسی و بازیابی شدند." else "Previous purchases verified."
                        )
                    },
                    onDismiss = { showVipDialog = false }
                )
            }

            if (showStreakDialog) {
                DailyStreakDialog(
                    streaks = dailyStreaks,
                    onClaim = { day -> viewModel.claimDailyStreakReward(day) },
                    onDismiss = { showStreakDialog = false }
                )
            }

            if (showSettingsDialog) {
                SettingsDialog(
                    activeTheme = activeTheme,
                    soundEnabled = soundEnabled,
                    hapticEnabled = hapticEnabled,
                    notificationsEnabled = notificationsEnabled,
                    onLanguageChange = { lang -> viewModel.setLanguage(lang) },
                    onThemeChange = { th -> viewModel.setTheme(th) },
                    onSoundChange = { snd -> viewModel.setSoundEnabled(snd) },
                    onHapticChange = { hpt -> viewModel.setHapticEnabled(hpt) },
                    onNotificationsChange = { notif -> viewModel.setNotificationsEnabled(notif) },
                    onResetProgress = { viewModel.resetLocalProgress() },
                    onDismiss = { showSettingsDialog = false }
                )
            }

            if (showAboutDialog) {
                AboutDialog(onDismiss = { showAboutDialog = false })
            }
        }
    }
}
