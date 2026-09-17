package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.model.DailyStreakReward
import com.example.ui.localization.LocalStrings
import com.example.ui.theme.LocalArenaPalette

@Composable
fun DailyStreakDialog(
    streaks: List<DailyStreakReward>,
    onClaim: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current

    val todayStreak = streaks.firstOrNull { it.isToday }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = palette.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF5722).copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = "Streak",
                                tint = Color(0xFFFF5722),
                                modifier = Modifier.size(24.dp)
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

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("streak_dialog_close")) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.close,
                            tint = palette.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = strings.dailyStreakDesc,
                    fontSize = 12.sp,
                    color = palette.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 7-Day Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(streaks) { streak ->
                        StreakDayCard(streak = streak)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Claim Button
                val canClaim = todayStreak != null && !todayStreak.isClaimed
                Button(
                    onClick = {
                        if (todayStreak != null) {
                            onClaim(todayStreak.dayNumber)
                            onDismiss()
                        }
                    },
                    enabled = canClaim,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6D00),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("claim_daily_streak_button")
                ) {
                    Text(
                        text = if (canClaim) strings.claim else strings.claimed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StreakDayCard(streak: DailyStreakReward) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current

    val borderColor = when {
        streak.isToday -> palette.primary
        streak.isClaimed -> Color(0xFF00E676)
        else -> palette.outline.copy(alpha = 0.5f)
    }

    val bgColor = when {
        streak.isToday -> palette.primaryContainer.copy(alpha = 0.5f)
        streak.isClaimed -> Color(0xFF00E676).copy(alpha = 0.1f)
        else -> palette.surfaceVariant
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(68.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "روز ${streak.dayNumber}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = palette.onSurface
            )

            Text(
                text = if (streak.dayNumber == 7) "🎁" else if (streak.isClaimed) "✅" else "🪙",
                fontSize = 22.sp
            )

            Text(
                text = "+${streak.rewardCoins}",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFFD700)
            )
        }
    }
}
