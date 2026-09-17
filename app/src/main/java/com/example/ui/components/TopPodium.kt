package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.LeaderboardEntry
import com.example.ui.localization.LocalStrings
import com.example.ui.theme.BronzeAccent
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LocalArenaPalette
import com.example.ui.theme.SilverAccent

@Composable
fun TopPodium(
    topThree: List<LeaderboardEntry>,
    modifier: Modifier = Modifier
) {
    if (topThree.size < 3) return
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current

    val first = topThree[0]
    val second = topThree[1]
    val third = topThree[2]

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd Place (Silver)
        PodiumPillar(
            entry = second,
            rankNumber = 2,
            trophy = "🥈",
            crownSymbol = "⭐",
            pillarColor = SilverAccent,
            heightDp = 100,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 1st Place (Gold - Tallest & Center)
        PodiumPillar(
            entry = first,
            rankNumber = 1,
            trophy = "🥇",
            crownSymbol = "👑",
            pillarColor = GoldAccent,
            heightDp = 135,
            modifier = Modifier.weight(1.1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 3rd Place (Bronze)
        PodiumPillar(
            entry = third,
            rankNumber = 3,
            trophy = "🥉",
            crownSymbol = "✨",
            pillarColor = BronzeAccent,
            heightDp = 80,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PodiumPillar(
    entry: LeaderboardEntry,
    rankNumber: Int,
    trophy: String,
    crownSymbol: String,
    pillarColor: Color,
    heightDp: Int,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Crown floating above avatar
        Text(
            text = crownSymbol,
            fontSize = if (rankNumber == 1) 22.sp else 16.sp
        )

        // Avatar circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(if (rankNumber == 1) 56.dp else 46.dp)
                .clip(CircleShape)
                .background(palette.surfaceVariant)
                .border(2.dp, pillarColor, CircleShape)
        ) {
            Text(
                text = entry.avatarEmoji,
                fontSize = if (rankNumber == 1) 26.sp else 20.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Username
        Text(
            text = entry.username,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = palette.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Score
        Text(
            text = "${entry.score}",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = pillarColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Podium Block
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(heightDp.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            pillarColor.copy(alpha = 0.35f),
                            palette.surfaceVariant
                        )
                    )
                )
                .border(
                    1.dp,
                    pillarColor.copy(alpha = 0.5f),
                    RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = trophy,
                    fontSize = 24.sp
                )
                Text(
                    text = "#$rankNumber",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = pillarColor
                )
            }
        }
    }
}
