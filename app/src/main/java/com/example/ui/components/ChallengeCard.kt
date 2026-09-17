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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.domain.model.ChallengeDifficulty
import com.example.domain.model.ChallengeItem
import com.example.domain.model.RankTier
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.LocalStrings
import com.example.ui.theme.LocalArenaPalette

@Composable
fun ChallengeCard(
    challenge: ChallengeItem,
    userRank: RankTier,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val lang = LocalAppLanguage.current
    val strings = LocalStrings.current
    val isFa = lang == AppLanguage.PERSIAN

    val isRankLocked = userRank.tierLevel < challenge.rankRequired.tierLevel
    val difficultyColor = when (challenge.difficulty) {
        ChallengeDifficulty.EASY -> Color(0xFF00E676)
        ChallengeDifficulty.MEDIUM -> Color(0xFFFFB300)
        ChallengeDifficulty.HARD -> Color(0xFFFF5722)
        ChallengeDifficulty.EPIC -> Color(0xFFBD00FF)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surface)
            .border(
                1.dp,
                if (isRankLocked) palette.outline.copy(alpha = 0.4f) else palette.outline,
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            // Header Row: Type Icon + Title + Difficulty Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(palette.surfaceVariant)
                    ) {
                        Text(text = challenge.type.iconSymbol, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (isFa) challenge.titleFa else challenge.titleEn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = palette.onSurface
                        )
                        Text(
                            text = if (isFa) challenge.type.titleFa else challenge.type.titleEn,
                            fontSize = 11.sp,
                            color = palette.onSurfaceVariant
                        )
                    }
                }

                // Difficulty Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(difficultyColor.copy(alpha = 0.2f))
                        .border(1.dp, difficultyColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isFa) challenge.difficulty.titleFa else challenge.difficulty.titleEn,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = difficultyColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = if (isFa) challenge.descFa else challenge.descEn,
                fontSize = 12.sp,
                color = palette.onSurfaceVariant,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Meta Info: Duration, Participants, Rewards
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Info badges
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Time
                    Text(
                        text = "⏱️ ${challenge.durationSeconds}${strings.durationSec}",
                        fontSize = 11.sp,
                        color = palette.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Participants
                    Text(
                        text = "👥 ${challenge.participantsCount}",
                        fontSize = 11.sp,
                        color = palette.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Rewards
                    Text(
                        text = "🪙+${challenge.coinReward}  ⚡+${challenge.xpReward}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFD700)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button / Lock Banner
            if (isRankLocked) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.surfaceVariant)
                ) {
                    Text(
                        text = "🔒 ${strings.locked} - ${challenge.rankRequired.iconSymbol} ${if (isFa) challenge.rankRequired.titleFa else challenge.rankRequired.titleEn}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = challenge.rankRequired.primaryColor
                    )
                }
            } else {
                Button(
                    onClick = onStartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("challenge_start_button_${challenge.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.primary,
                        contentColor = palette.onPrimary
                    )
                ) {
                    Text(
                        text = "${strings.startMatch}  ${if (challenge.ticketCost > 0) "(-${challenge.ticketCost} 🎟️)" else "(رایگان)"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
