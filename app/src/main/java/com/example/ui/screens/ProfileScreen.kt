package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.AchievementItem
import com.example.domain.model.MatchHistoryItem
import com.example.domain.model.UserProfile
import com.example.ui.components.RankCard
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.LocalStrings
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LocalArenaPalette

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    achievements: List<AchievementItem>,
    matchHistory: List<MatchHistoryItem>,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current
    val lang = LocalAppLanguage.current
    val isFa = lang == AppLanguage.PERSIAN

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. User Header & Identity
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(palette.surface)
                    .border(1.dp, palette.outline, RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(palette.surfaceVariant)
                            .border(2.dp, palette.primary, CircleShape)
                    ) {
                        Text(text = "🎮", fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userProfile.username,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = palette.onSurface
                            )
                            if (userProfile.isVip) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(GoldAccent)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "VIP",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        Text(
                            text = "${strings.level} ${userProfile.level} • ${userProfile.xp} XP",
                            fontSize = 12.sp,
                            color = palette.onSurfaceVariant
                        )

                        Text(
                            text = "${strings.rank}: ${userProfile.currentRank.iconSymbol} ${if (isFa) userProfile.currentRank.titleFa else userProfile.currentRank.titleEn}",
                            fontSize = 12.sp,
                            color = userProfile.currentRank.primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. Stats Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = strings.winRate,
                    value = "${userProfile.winRate}%",
                    icon = "🎯",
                    color = Color(0xFF00E676),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = strings.totalMatches,
                    value = "${userProfile.totalMatches}",
                    icon = "⚔️",
                    color = palette.primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = strings.bestScoreLabel,
                    value = "${userProfile.bestScore}",
                    icon = "🏆",
                    color = GoldAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Rank Tier Details Card
        item {
            RankCard(currentRank = userProfile.currentRank, currentXp = userProfile.xp)
        }

        // 4. Achievements Section
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = GoldAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.achievementsTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = palette.onSurface
                )
            }
        }

        items(achievements) { achievement ->
            AchievementRow(achievement = achievement, isFa = isFa)
        }

        // 5. Match History Section
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.History, contentDescription = null, tint = palette.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.matchHistoryTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = palette.onSurface
                )
            }
        }

        if (matchHistory.isEmpty()) {
            item {
                Text(
                    text = strings.noHistory,
                    fontSize = 12.sp,
                    color = palette.onSurfaceVariant
                )
            }
        } else {
            items(matchHistory) { history ->
                MatchHistoryRow(history = history, isFa = isFa)
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(palette.surface)
            .border(1.dp, palette.outline, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                color = palette.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AchievementRow(
    achievement: AchievementItem,
    isFa: Boolean
) {
    val palette = LocalArenaPalette.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(palette.surface)
            .border(
                1.dp,
                if (achievement.isUnlocked) GoldAccent.copy(alpha = 0.6f) else palette.outline.copy(alpha = 0.4f),
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(palette.surfaceVariant)
                ) {
                    Text(text = achievement.iconSymbol, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isFa) achievement.titleFa else achievement.titleEn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = palette.onSurface
                        )
                        if (achievement.isUnlocked) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "✅", fontSize = 10.sp)
                        }
                    }
                    Text(
                        text = if (isFa) achievement.descFa else achievement.descEn,
                        fontSize = 10.sp,
                        color = palette.onSurfaceVariant,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { achievement.progressFraction },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = if (achievement.isUnlocked) GoldAccent else palette.primary,
                        trackColor = palette.surfaceVariant
                    )
                }
            }

            Text(
                text = "+${achievement.rewardXp} XP",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = palette.primary
            )
        }
    }
}

@Composable
private fun MatchHistoryRow(
    history: MatchHistoryItem,
    isFa: Boolean
) {
    val palette = LocalArenaPalette.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(palette.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = if (history.isWin) "🏆" else "💀", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isFa) history.challengeTitleFa else history.challengeTitleEn,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.onSurface
                    )
                    Text(
                        text = history.dateFormatted,
                        fontSize = 10.sp,
                        color = palette.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${history.score} pts",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary
                )
                Text(
                    text = "+${history.xpEarned} XP • +${history.coinsEarned} 🪙",
                    fontSize = 10.sp,
                    color = palette.onSurfaceVariant
                )
            }
        }
    }
}
