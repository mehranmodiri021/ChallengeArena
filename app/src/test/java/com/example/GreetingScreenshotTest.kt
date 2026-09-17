package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.domain.model.RankTier
import com.example.domain.model.UserProfile
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.ChallengeArenaTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun greeting_screenshot() {
        composeTestRule.setContent {
            ChallengeArenaTheme {
                HomeScreen(
                    userProfile = UserProfile(
                        username = "SeyedHamid",
                        level = 5,
                        xp = 1250,
                        currentRank = RankTier.GOLD,
                        coins = 1500,
                        tickets = 10
                    ),
                    challenges = emptyList(),
                    leaderboardPreview = emptyList(),
                    onPlayQuickMatch = {},
                    onStartChallenge = {},
                    onViewAllChallenges = {},
                    onViewLeaderboard = {},
                    onOpenDailyStreak = {},
                    onOpenVip = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
    }
}
