package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DailyStreakReward
import com.example.domain.model.RewardItem
import com.example.domain.model.UserProfile
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.LocalStrings
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LocalArenaPalette

@Composable
fun RewardsScreen(
    userProfile: UserProfile,
    dailyStreaks: List<DailyStreakReward>,
    storeRewards: List<RewardItem>,
    onClaimStreak: (Int) -> Unit,
    onWatchTapsellAd: () -> Unit,
    onPurchaseStoreItem: (RewardItem) -> Unit,
    onOpenVip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current
    val lang = LocalAppLanguage.current
    val isFa = lang == AppLanguage.PERSIAN

    val todayStreak = dailyStreaks.firstOrNull { it.isToday }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header
        item {
            Column {
                Text(
                    text = strings.rewardsTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = palette.onSurface
                )
                Text(
                    text = if (isFa) "جوایز ورود روزانه، تبلیغات تپسل و صندوق‌های ویژه" else "Daily login streaks, Tapsell ad rewards and mystery chests",
                    fontSize = 12.sp,
                    color = palette.onSurfaceVariant
                )
            }
        }

        // 2. Daily Streak Box
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(palette.surface)
                    .border(1.dp, palette.outline, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF5722).copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = null,
                                    tint = Color(0xFFFF5722),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = strings.dailyStreakTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = palette.onSurface
                            )
                        }

                        if (todayStreak != null && !todayStreak.isClaimed) {
                            Button(
                                onClick = { onClaimStreak(todayStreak.dayNumber) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF5722),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("claim_streak_today_button")
                            ) {
                                Text(text = strings.claim, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 7-day strip
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(dailyStreaks) { streak ->
                            StreakMiniBadge(streak = streak, isFa = isFa)
                        }
                    }
                }
            }
        }

        // 3. Tapsell Rewarded Video Ad Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF00B4D8), Color(0xFF0077B6))
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = strings.watchAdForRewards,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isFa) "تبلیغات ویدیویی تپسل (Tapsell Plus)" else "Tapsell Rewarded Video Ad",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Text(text = "📺", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onWatchTapsellAd,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("watch_tapsell_ad_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF0077B6)
                        )
                    ) {
                        Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.tapsellAdButton,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 4. Store Rewards / Mystery Chests
        item {
            Text(
                text = strings.mysteryChests,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = palette.onSurface
            )
        }

        items(storeRewards) { reward ->
            StoreRewardCard(
                reward = reward,
                userCoins = userProfile.coins,
                onPurchase = { onPurchaseStoreItem(reward) },
                onVipClick = onOpenVip,
                isFa = isFa
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun StreakMiniBadge(streak: DailyStreakReward, isFa: Boolean) {
    val palette = LocalArenaPalette.current
    val borderColor = if (streak.isToday) palette.primary else if (streak.isClaimed) Color(0xFF00E676) else palette.outline.copy(alpha = 0.4f)
    val bgColor = if (streak.isToday) palette.primaryContainer.copy(alpha = 0.4f) else palette.surfaceVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(52.dp)
            .height(68.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "D${streak.dayNumber}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = palette.onSurface
            )
            Text(
                text = if (streak.isClaimed) "✅" else "🪙",
                fontSize = 16.sp
            )
            Text(
                text = "+${streak.rewardCoins}",
                fontSize = 9.sp,
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StoreRewardCard(
    reward: RewardItem,
    userCoins: Int,
    onPurchase: () -> Unit,
    onVipClick: () -> Unit,
    isFa: Boolean
) {
    val palette = LocalArenaPalette.current
    val canAfford = userCoins >= reward.coinCost

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(palette.surface)
            .border(1.dp, palette.outline, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surfaceVariant)
                ) {
                    Text(text = reward.iconSymbol, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isFa) reward.titleFa else reward.titleEn,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = palette.onSurface
                    )
                    Text(
                        text = if (isFa) reward.descFa else reward.descEn,
                        fontSize = 11.sp,
                        color = palette.onSurfaceVariant
                    )
                }
            }

            if (reward.isVipOnly) {
                Button(
                    onClick = onVipClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = "VIP", fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            } else {
                Button(
                    onClick = onPurchase,
                    enabled = canAfford,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.primary,
                        contentColor = palette.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("buy_reward_button_${reward.id}")
                ) {
                    Text(
                        text = "${reward.coinCost} 🪙",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
