package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ads.TapsellManager
import com.example.billing.BazaarBillingManager
import com.example.config.AppConfig
import com.example.config.BazaarConfig
import com.example.config.EconomyConfig
import com.example.data.preferences.SettingsDataStore
import com.example.domain.model.RankTier
import com.example.domain.model.UserProfile
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.ArenaTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun testAppNameAndDeveloperIdentity() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Challenge Arena", appName)
        assertEquals("سیدحمید موسوی زاده", AppConfig.DEVELOPER_NAME)
    }

    @Test
    fun testRankTierCalculation() {
        assertEquals(RankTier.BRONZE, RankTier.fromXp(50))
        assertEquals(RankTier.SILVER, RankTier.fromXp(550))
        assertEquals(RankTier.GOLD, RankTier.fromXp(1600))
        assertEquals(RankTier.PLATINUM, RankTier.fromXp(4000))
        assertEquals(RankTier.DIAMOND, RankTier.fromXp(8000))
        assertEquals(RankTier.MASTER, RankTier.fromXp(13000))
        assertEquals(RankTier.GRANDMASTER, RankTier.fromXp(21000))
        assertEquals(RankTier.LEGEND, RankTier.fromXp(40000))
    }

    @Test
    fun testUserProfileCalculatedProperties() {
        val profile = UserProfile(
            wins = 15,
            losses = 5,
            level = 2,
            xp = 350
        )
        assertEquals(20, profile.totalMatches)
        assertEquals(75, profile.winRate)
    }

    @Test
    fun testEconomyConfigFormulas() {
        val xpLvl1 = EconomyConfig.getRequiredXpForNextLevel(1)
        val xpLvl5 = EconomyConfig.getRequiredXpForNextLevel(5)
        assertTrue(xpLvl5 > xpLvl1)
        assertEquals(2.0f, EconomyConfig.VIP_XP_MULTIPLIER, 0.01f)
        assertEquals(1.5f, EconomyConfig.VIP_COIN_MULTIPLIER, 0.01f)
    }

    @Test
    fun testSettingsDataStorePersistence() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataStore = SettingsDataStore(context)
        dataStore.setLanguage(AppLanguage.ENGLISH)
        dataStore.setTheme(ArenaTheme.EMERALD)
        dataStore.setSoundEnabled(false)

        val settings = dataStore.settingsFlow.first()
        assertEquals(AppLanguage.ENGLISH, settings.language)
        assertEquals(ArenaTheme.EMERALD, settings.theme)
        assertEquals(false, settings.soundEnabled)
    }

    @Test
    fun testBazaarBillingManagerInitialization() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val billingManager = BazaarBillingManager.getInstance(context)
        billingManager.initializeConnection()
        assertTrue(billingManager.productPrices.containsKey(BazaarConfig.PRODUCT_ID_VIP_MONTHLY))
        assertTrue(billingManager.productPrices.containsKey(BazaarConfig.PRODUCT_ID_VIP_YEARLY))
    }

    @Test
    fun testTapsellRewardExecutionCycle() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tapsellManager = TapsellManager.getInstance(context)
        var rewardGranted = false
        var earnedCoins = 0
        var earnedXp = 0

        tapsellManager.showRewardedAd(
            onRewardGranted = { coins, xp, _ ->
                rewardGranted = true
                earnedCoins = coins
                earnedXp = xp
            }
        )

        assertTrue("Tapsell reward must be verified and granted", rewardGranted)
        assertTrue(earnedCoins > 0)
        assertTrue(earnedXp > 0)
    }
}
