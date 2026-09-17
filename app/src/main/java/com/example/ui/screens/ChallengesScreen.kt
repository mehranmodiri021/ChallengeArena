package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ChallengeItem
import com.example.domain.model.ChallengeType
import com.example.domain.model.UserProfile
import com.example.ui.components.ChallengeCard
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.LocalStrings
import com.example.ui.theme.LocalArenaPalette

@Composable
fun ChallengesScreen(
    challenges: List<ChallengeItem>,
    userProfile: UserProfile,
    selectedType: ChallengeType?,
    onTypeSelected: (ChallengeType?) -> Unit,
    onStartChallenge: (ChallengeItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current
    val lang = LocalAppLanguage.current
    val isFa = lang == AppLanguage.PERSIAN

    val filteredChallenges = if (selectedType != null) {
        challenges.filter { it.type == selectedType }
    } else {
        challenges
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = strings.challengesTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = palette.onSurface
            )
            Text(
                text = "${filteredChallenges.size} ${if (isFa) "چالش آماده رقابت" else "challenges available"}",
                fontSize = 12.sp,
                color = palette.onSurfaceVariant
            )
        }

        // Filter Chips Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // "All" chip
            item {
                ChallengeFilterChip(
                    title = strings.tabAll,
                    icon = "⚡",
                    isSelected = selectedType == null,
                    onClick = { onTypeSelected(null) },
                    testTag = "filter_chip_all"
                )
            }

            items(ChallengeType.entries) { type ->
                ChallengeFilterChip(
                    title = if (isFa) type.titleFa else type.titleEn,
                    icon = type.iconSymbol,
                    isSelected = selectedType == type,
                    onClick = { onTypeSelected(type) },
                    testTag = "filter_chip_${type.name.lowercase()}"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Challenges List
        if (filteredChallenges.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Text(
                    text = strings.emptyChallenges,
                    fontSize = 14.sp,
                    color = palette.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredChallenges, key = { it.id }) { challenge ->
                    ChallengeCard(
                        challenge = challenge,
                        userRank = userProfile.currentRank,
                        onStartClick = { onStartChallenge(challenge) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChallengeFilterChip(
    title: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val palette = LocalArenaPalette.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) palette.primaryContainer else palette.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag(testTag)
    ) {
        Text(text = icon, fontSize = 12.sp)
        Spacer(modifier = Modifier.padding(horizontal = 3.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) palette.onPrimaryContainer else palette.onSurfaceVariant
        )
    }
}
