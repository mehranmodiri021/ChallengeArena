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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.RankTier
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.LocalStrings
import com.example.ui.theme.LocalArenaPalette

@Composable
fun RankCard(
    currentRank: RankTier,
    currentXp: Int,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val lang = LocalAppLanguage.current
    val strings = LocalStrings.current
    val isFa = lang == AppLanguage.PERSIAN

    val nextRank = RankTier.nextTier(currentRank)
    val nextXp = nextRank?.minXp ?: (currentRank.minXp * 2)
    val rankProgress = if (nextRank != null) {
        val diff = nextXp - currentRank.minXp
        val cur = currentXp - currentRank.minXp
        (cur.toFloat() / diff.coerceAtLeast(1)).coerceIn(0f, 1f)
    } else {
        1.0f
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        currentRank.primaryColor.copy(alpha = 0.2f),
                        palette.surfaceVariant
                    )
                )
            )
            .border(1.dp, currentRank.primaryColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(currentRank.primaryColor.copy(alpha = 0.25f))
                            .border(2.dp, currentRank.primaryColor, CircleShape)
                    ) {
                        Text(
                            text = currentRank.iconSymbol,
                            fontSize = 26.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (isFa) currentRank.titleFa else currentRank.titleEn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = currentRank.primaryColor
                        )
                        Text(
                            text = if (isFa) "رتبه سطح ${currentRank.tierLevel} از ۸" else "Tier ${currentRank.tierLevel} of 8",
                            fontSize = 12.sp,
                            color = palette.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(currentRank.primaryColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isFa) "امتیاز: $currentXp XP" else "Score: $currentXp XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentRank.primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Perks Description
            Text(
                text = if (isFa) currentRank.perksFa else currentRank.perksEn,
                fontSize = 12.sp,
                color = palette.onSurface.copy(alpha = 0.85f),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress to Next Rank
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (nextRank != null) {
                        if (isFa) "پیشرفت تا رتبه بعدی (${nextRank.titleFa})" else "Progress to ${nextRank.titleEn}"
                    } else {
                        if (isFa) "بالاترین رتبه کسب شده است!" else "Maximum Rank Achieved!"
                    },
                    fontSize = 11.sp,
                    color = palette.onSurfaceVariant
                )
                Text(
                    text = "${(rankProgress * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = currentRank.primaryColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { rankProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = currentRank.primaryColor,
                trackColor = palette.surface
            )
        }
    }
}
