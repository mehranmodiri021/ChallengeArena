package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalArenaPalette = staticCompositionLocalOf { DefaultPalette }

@Composable
fun ChallengeArenaTheme(
    activeTheme: ArenaTheme = ArenaTheme.DEFAULT,
    content: @Composable () -> Unit
) {
    val palette = getPaletteForTheme(activeTheme)

    val colorScheme: ColorScheme = darkColorScheme(
        primary = palette.primary,
        onPrimary = palette.onPrimary,
        primaryContainer = palette.primaryContainer,
        onPrimaryContainer = palette.onPrimaryContainer,
        secondary = palette.secondary,
        onSecondary = palette.onSecondary,
        tertiary = palette.tertiary,
        background = palette.background,
        onBackground = palette.onBackground,
        surface = palette.surface,
        onSurface = palette.onSurface,
        surfaceVariant = palette.surfaceVariant,
        onSurfaceVariant = palette.onSurfaceVariant,
        outline = palette.outline
    )

    CompositionLocalProvider(
        LocalArenaPalette provides palette
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
