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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.SportsEsports
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
import com.example.domain.model.ChallengeItem
import com.example.domain.model.ChallengeType
import com.example.domain.model.LeaderboardEntry
import com.example.domain.model.UserProfile
import com.example.ui.components.ChallengeCard
import com.example.ui.components.RankCard
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.LocalStrings
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LocalArenaPalette

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    challenges: List<ChallengeItem>,
    leaderboardPreview: List<LeaderboardEntry>,
    onPlayQuickMatch: () -> Unit,
    onStartChallenge: (ChallengeItem) -> Unit,
    onViewAllChallenges: () -> Unit,
    onViewLeaderboard: () -> Unit,
    onOpenDailyStreak: () -> Unit,
    onOpenVip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current
    val lang = LocalAppLanguage.current
    val isFa = lang == AppLanguage.PERSIAN

    val featuredChallenge = challenges.firstOrNull { it.type == ChallengeType.TOURNAMENT }
        ?: challenges.firstOrNull()
    val dailyChallenge = challenges.firstOrNull { it.type == ChallengeType.DAILY }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Live Arena Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(palette.gradientBrush)
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Live Status Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.3f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.arenaStatusOnline,
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Daily Streak shortcut
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFF5722).copy(alpha = 0.3f))
                                .clickable(onClick = onOpenDailyStreak)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("home_streak_chip")
                        ) {
                            Text(text = "🔥", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${userProfile.dailyStreak} ${strings.streakDays}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = strings.greetingHello,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Text(
                        text = if (isFa) "رقابت آنلاین و مسابقات نفس‌گیر آرنا" else "Online Arena Duel & Speed Battles",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Play Now CTA
                    Button(
                        onClick = onPlayQuickMatch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("home_play_now_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700),
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.playNow,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // 2. Rank Tier Status Card
        item {
            RankCard(
                currentRank = userProfile.currentRank,
                currentXp = userProfile.xp
            )
        }

        // 3. Featured & Daily Challenges Preview
        if (dailyChallenge != null) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.dailyChallenge,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = palette.onSurface
                        )
                        Text(
                            text = strings.viewAll,
                            fontSize = 12.sp,
                            color = palette.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable(onClick = onViewAllChallenges)
                                .testTag("view_all_challenges_button")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    ChallengeCard(
                        challenge = dailyChallenge,
                        userRank = userProfile.currentRank,
                        onStartClick = { onStartChallenge(dailyChallenge) }
                    )
                }
            }
        }

        if (featuredChallenge != null && featuredChallenge != dailyChallenge) {
            item {
                Column {
                    Text(
                        text = strings.featuredChallenge,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = palette.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ChallengeCard(
                        challenge = featuredChallenge,
                        userRank = userProfile.currentRank,
                        onStartClick = { onStartChallenge(featuredChallenge) }
                    )
                }
            }
        }

        // 4. Leaderboard Quick Sneak Peek
        if (leaderboardPreview.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.surface)
                        .border(1.dp, palette.outline, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Leaderboard,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = strings.leaderboardPreview,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.onSurface
                                )
                            }

                            Text(
                                text = strings.viewAll,
                                fontSize = 12.sp,
                                color = palette.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable(onClick = onViewLeaderboard)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        leaderboardPreview.take(3).forEach { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "#${entry.rank}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (entry.rank == 1) GoldAccent else palette.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = entry.avatarEmoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = entry.username,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.onSurface
                                    )
                                }

                                Text(
                                    text = "${entry.score} pts",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
