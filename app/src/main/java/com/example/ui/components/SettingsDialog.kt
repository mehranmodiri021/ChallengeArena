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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.LocalAppLanguage
import com.example.ui.localization.LocalStrings
import com.example.ui.theme.ArenaTheme
import com.example.ui.theme.LocalArenaPalette

@Composable
fun SettingsDialog(
    activeTheme: ArenaTheme,
    soundEnabled: Boolean,
    hapticEnabled: Boolean,
    notificationsEnabled: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onThemeChange: (ArenaTheme) -> Unit,
    onSoundChange: (Boolean) -> Unit,
    onHapticChange: (Boolean) -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onResetProgress: () -> Unit,
    onDismiss: () -> Unit
) {
    val palette = LocalArenaPalette.current
    val currentLang = LocalAppLanguage.current
    val strings = LocalStrings.current
    var showPrivacyPolicy by remember { mutableStateOf(false) }

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
                    .verticalScroll(rememberScrollState())
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
                                .background(palette.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = strings.settingsTitle,
                                tint = palette.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = strings.settingsTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = palette.onSurface
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("settings_dialog_close")) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.close,
                            tint = palette.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Language
                Text(
                    text = strings.sectionLanguage,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = palette.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LanguageOption(
                        language = AppLanguage.PERSIAN,
                        isSelected = currentLang == AppLanguage.PERSIAN,
                        onClick = { onLanguageChange(AppLanguage.PERSIAN) },
                        modifier = Modifier.weight(1f)
                    )
                    LanguageOption(
                        language = AppLanguage.ENGLISH,
                        isSelected = currentLang == AppLanguage.ENGLISH,
                        onClick = { onLanguageChange(AppLanguage.ENGLISH) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = palette.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))

                // Section 2: Visual Themes
                Text(
                    text = strings.sectionTheme,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = palette.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ArenaTheme.entries.chunked(2).forEach { rowThemes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowThemes.forEach { themeItem ->
                                ThemeChip(
                                    theme = themeItem,
                                    isSelected = activeTheme == themeItem,
                                    isFa = currentLang == AppLanguage.PERSIAN,
                                    onClick = { onThemeChange(themeItem) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = palette.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))

                // Section 3: Audio & Haptics
                Text(
                    text = strings.sectionAudio,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = palette.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                SettingSwitchRow(
                    title = strings.soundEffects,
                    checked = soundEnabled,
                    onCheckedChange = onSoundChange
                )
                SettingSwitchRow(
                    title = strings.hapticFeedback,
                    checked = hapticEnabled,
                    onCheckedChange = onHapticChange
                )
                SettingSwitchRow(
                    title = strings.notifications,
                    checked = notificationsEnabled,
                    onCheckedChange = onNotificationsChange
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = palette.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))

                // Section 4: Privacy & Reset
                TextButton(
                    onClick = { showPrivacyPolicy = !showPrivacyPolicy },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = strings.privacyPolicyTitle,
                        color = palette.primary,
                        fontSize = 13.sp
                    )
                }

                if (showPrivacyPolicy) {
                    Text(
                        text = strings.privacyPolicyContent,
                        fontSize = 11.sp,
                        color = palette.onSurfaceVariant,
                        lineHeight = 16.sp,
                        modifier = Modifier
                            .background(palette.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                TextButton(
                    onClick = {
                        onResetProgress()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = strings.resetProgressTitle,
                        color = Color(0xFFFF5252),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageOption(
    language: AppLanguage,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) palette.primaryContainer else palette.surfaceVariant)
            .border(
                1.5.dp,
                if (isSelected) palette.primary else palette.outline.copy(alpha = 0.5f),
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Text(
            text = language.displayName,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp,
            color = if (isSelected) palette.onPrimaryContainer else palette.onSurface
        )
    }
}

@Composable
private fun ThemeChip(
    theme: ArenaTheme,
    isSelected: Boolean,
    isFa: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalArenaPalette.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) palette.primaryContainer.copy(alpha = 0.6f) else palette.surfaceVariant)
            .border(
                1.dp,
                if (isSelected) palette.primary else palette.outline.copy(alpha = 0.4f),
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(Color(theme.previewHex))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (isFa) theme.titleFa else theme.titleEn,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = palette.onSurface,
            maxLines = 1
        )
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val palette = LocalArenaPalette.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            color = palette.onSurface
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = palette.primary,
                checkedTrackColor = palette.primaryContainer,
                uncheckedTrackColor = palette.surfaceVariant
            )
        )
    }
}
