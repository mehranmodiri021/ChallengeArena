package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Universal Gaming Accents
val GoldAccent = Color(0xFFFFD700)
val GoldGradientEnd = Color(0xFFFFA000)
val SilverAccent = Color(0xFFC0C0C0)
val BronzeAccent = Color(0xFFCD7F32)
val WinGreen = Color(0xFF00E676)
val LossRed = Color(0xFFFF1744)
val VipCrownGold = Color(0xFFFFD700)

// Theme Palette Data Class
data class ArenaColorPalette(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val tertiary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val gradientBrush: Brush
)

// 1. DEFAULT (Cyber Arena)
val DefaultPalette = ArenaColorPalette(
    primary = Color(0xFF00E5FF),
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF80F5FF),
    secondary = Color(0xFF3D5AFE),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFFFD700),
    background = Color(0xFF0C1322),
    onBackground = Color(0xFFF0F4FC),
    surface = Color(0xFF141F36),
    onSurface = Color(0xFFF0F4FC),
    surfaceVariant = Color(0xFF1C2B4B),
    onSurfaceVariant = Color(0xFFBAC7DF),
    outline = Color(0xFF2E4370),
    gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF00E5FF), Color(0xFF3D5AFE))
    )
)

// 2. MIDNIGHT (Deep Navy & Indigo)
val MidnightPalette = ArenaColorPalette(
    primary = Color(0xFF536DFE),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF1A237E),
    onPrimaryContainer = Color(0xFFC5CAE9),
    secondary = Color(0xFF00B0FF),
    onSecondary = Color(0xFF002244),
    tertiary = Color(0xFFFFAB00),
    background = Color(0xFF080C16),
    onBackground = Color(0xFFECEFF1),
    surface = Color(0xFF101726),
    onSurface = Color(0xFFECEFF1),
    surfaceVariant = Color(0xFF192238),
    onSurfaceVariant = Color(0xFF90A4AE),
    outline = Color(0xFF263554),
    gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF536DFE), Color(0xFF1A237E))
    )
)

// 3. OCEAN (Deep Marine & Cyan)
val OceanPalette = ArenaColorPalette(
    primary = Color(0xFF00B4D8),
    onPrimary = Color(0xFF002A35),
    primaryContainer = Color(0xFF00566A),
    onPrimaryContainer = Color(0xFF90E0EF),
    secondary = Color(0xFF06D6A0),
    onSecondary = Color(0xFF003829),
    tertiary = Color(0xFFFFD166),
    background = Color(0xFF061826),
    onBackground = Color(0xFFE8F1F5),
    surface = Color(0xFF0B253A),
    onSurface = Color(0xFFE8F1F5),
    surfaceVariant = Color(0xFF133652),
    onSurfaceVariant = Color(0xFFA2C4D9),
    outline = Color(0xFF214E73),
    gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF00B4D8), Color(0xFF06D6A0))
    )
)

// 4. PURPLE (Neon Violet & Magenta)
val PurplePalette = ArenaColorPalette(
    primary = Color(0xFFBD00FF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF4A0072),
    onPrimaryContainer = Color(0xFFE1BEE7),
    secondary = Color(0xFFFF007F),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF00E5FF),
    background = Color(0xFF120824),
    onBackground = Color(0xFFF5EEFD),
    surface = Color(0xFF1F1138),
    onSurface = Color(0xFFF5EEFD),
    surfaceVariant = Color(0xFF2F1A52),
    onSurfaceVariant = Color(0xFFD1BBE8),
    outline = Color(0xFF4C2F7C),
    gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFBD00FF), Color(0xFFFF007F))
    )
)

// 5. SUNSET (Solar Orange & Crimson)
val SunsetPalette = ArenaColorPalette(
    primary = Color(0xFFFF6D00),
    onPrimary = Color(0xFF3E1500),
    primaryContainer = Color(0xFF6B2600),
    onPrimaryContainer = Color(0xFFFFD180),
    secondary = Color(0xFFFF1744),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFFFFD700),
    background = Color(0xFF1E100D),
    onBackground = Color(0xFFFFF2ED),
    surface = Color(0xFF2D1914),
    onSurface = Color(0xFFFFF2ED),
    surfaceVariant = Color(0xFF3F241E),
    onSurfaceVariant = Color(0xFFE0B8B0),
    outline = Color(0xFF633A32),
    gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFFF6D00), Color(0xFFFF1744))
    )
)

// 6. EMERALD (Vibrant Jade & Lime)
val EmeraldPalette = ArenaColorPalette(
    primary = Color(0xFF00E676),
    onPrimary = Color(0xFF003816),
    primaryContainer = Color(0xFF005824),
    onPrimaryContainer = Color(0xFFB9F6CA),
    secondary = Color(0xFF00B0FF),
    onSecondary = Color(0xFF00263D),
    tertiary = Color(0xFFFFD700),
    background = Color(0xFF081C15),
    onBackground = Color(0xFFEEFAF4),
    surface = Color(0xFF112E23),
    onSurface = Color(0xFFEEFAF4),
    surfaceVariant = Color(0xFF184233),
    onSurfaceVariant = Color(0xFFA5D6BE),
    outline = Color(0xFF27614B),
    gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF00E676), Color(0xFF00B0FF))
    )
)

fun getPaletteForTheme(theme: ArenaTheme): ArenaColorPalette {
    return when (theme) {
        ArenaTheme.DEFAULT -> DefaultPalette
        ArenaTheme.MIDNIGHT -> MidnightPalette
        ArenaTheme.OCEAN -> OceanPalette
        ArenaTheme.PURPLE -> PurplePalette
        ArenaTheme.SUNSET -> SunsetPalette
        ArenaTheme.EMERALD -> EmeraldPalette
    }
}
