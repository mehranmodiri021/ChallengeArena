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
import com.example.domain.model.LeaderboardEntry
import com.example.domain.model.LeaderboardType
import com.example.domain.model.UserProfile
import com.example.ui.components.TopPodium
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.LocalStrings
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LocalArenaPalette

@Composable
fun LeaderboardScreen(
    leaderboardEntries: List<LeaderboardEntry>,
    selectedType: LeaderboardType,
    userProfile: UserProfile,
    onTypeSelected: (LeaderboardType) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current
    val lang = LocalAppLanguage.current
    val isFa = lang == AppLanguage.PERSIAN

    val topThree = leaderboardEntries.take(3)
    val remainingEntries = leaderboardEntries.drop(3)
    val userEntry = leaderboardEntries.firstOrNull { it.isCurrentUser }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
    ) {
        // Title
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = strings.leaderboardTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = palette.onSurface
            )
            Text(
                text = strings.demoDataNotice,
                fontSize = 11.sp,
                color = palette.onSurfaceVariant
            )
        }

        // Leaderboard Category Tabs
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(LeaderboardType.entries) { type ->
                val isSelected = selectedType == type
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) palette.primaryContainer else palette.surfaceVariant)
                        .clickable { onTypeSelected(type) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("leaderboard_tab_${type.name.lowercase()}")
                ) {
                    Text(
                        text = if (isFa) type.titleFa else type.titleEn,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) palette.onPrimaryContainer else palette.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sticky / Featured User Rank Card
        if (userEntry != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(palette.primary.copy(alpha = 0.15f))
                    .border(1.5.dp, palette.primary, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "#${userEntry.rank}",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = palette.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = userEntry.avatarEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "${userEntry.username} (${strings.yourRank})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = palette.onSurface
                            )
                            Text(
                                text = "${strings.winsLabel}: ${userProfile.wins}",
                                fontSize = 11.sp,
                                color = palette.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = "${userEntry.score} pts",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = palette.primary
                    )
                }
            }
        }

        // Leaderboard Scrollable List
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // TOP 3 PODIUM
            if (topThree.size >= 3) {
                item {
                    TopPodium(topThree = topThree)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Remaining Competitors
            items(remainingEntries) { entry ->
                LeaderboardRow(entry = entry, isFa = isFa)
            }
        }
    }
}

@Composable
private fun LeaderboardRow(
    entry: LeaderboardEntry,
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
                if (entry.isCurrentUser) palette.primary else palette.outline.copy(alpha = 0.4f),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#${entry.rank}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = palette.onSurfaceVariant,
                    modifier = Modifier.width(32.dp)
                )

                Text(text = entry.avatarEmoji, fontSize = 20.sp)

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = palette.onSurface
                        )
                        if (entry.badge != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(palette.primary.copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = entry.badge,
                                    fontSize = 9.sp,
                                    color = palette.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = "Lv.${entry.level} • ${if (isFa) entry.rankTier.titleFa else entry.rankTier.titleEn}",
                        fontSize = 11.sp,
                        color = entry.rankTier.primaryColor
                    )
                }
            }

            Text(
                text = "${entry.score}",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = palette.onSurface
            )
        }
    }
}
