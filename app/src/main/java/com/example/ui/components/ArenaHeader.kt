package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.domain.model.UserProfile
import com.example.ui.localization.LocalStrings
import com.example.ui.theme.LocalArenaPalette

@Composable
fun ArenaHeader(
    userProfile: UserProfile,
    onVipClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current
    val strings = LocalStrings.current

    Surface(
        color = palette.surface.copy(alpha = 0.95f),
        tonalElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Top Row: User Level / Avatar / VIP + Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // User & Level Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(palette.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(palette.primary)
                    ) {
                        Text(
                            text = "${userProfile.level}",
                            fontWeight = FontWeight.Bold,
                            color = palette.onPrimary,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userProfile.username,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = palette.onSurface,
                                maxLines = 1
                            )
                            if (userProfile.isVip) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFFFD700))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "VIP",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        Text(
                            text = "${strings.rank}: ${userProfile.currentRank.iconSymbol} ${userProfile.currentRank.titleFa}",
                            fontSize = 11.sp,
                            color = userProfile.currentRank.primaryColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Currency Badges & Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Coins Pill
                    CurrencyPill(
                        icon = "🪙",
                        amount = "${userProfile.coins}",
                        bgColor = palette.surfaceVariant,
                        textColor = Color(0xFFFFD700)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Tickets Pill
                    CurrencyPill(
                        icon = "🎟️",
                        amount = "${userProfile.tickets}",
                        bgColor = palette.surfaceVariant,
                        textColor = Color(0xFF00E5FF)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // VIP Button
                    IconButton(
                        onClick = onVipClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (userProfile.isVip) Color(0xFFFFD700) else palette.surfaceVariant)
                            .testTag("vip_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "VIP",
                            tint = if (userProfile.isVip) Color.Black else Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Settings Button
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(palette.surfaceVariant)
                            .testTag("settings_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = palette.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // About Button
                    IconButton(
                        onClick = onAboutClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(palette.surfaceVariant)
                            .testTag("about_header_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About",
                            tint = palette.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // XP Progress Bar to next level
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { userProfile.currentLevelProgress },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = palette.primary,
                    trackColor = palette.surfaceVariant
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${userProfile.xp} / ${userProfile.xpForCurrentLevel} ${strings.xp}",
                    fontSize = 10.sp,
                    color = palette.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CurrencyPill(
    icon: String,
    amount: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .padding(horizontal = 7.dp, vertical = 4.dp)
    ) {
        Text(text = icon, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = amount,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
