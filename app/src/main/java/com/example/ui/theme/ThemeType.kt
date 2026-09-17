package com.example.ui.theme

enum class ArenaTheme(
    val code: String,
    val titleEn: String,
    val titleFa: String,
    val previewHex: Long
) {
    DEFAULT("default", "Cyber Arena", "سایبر آرنا", 0xFF00E5FF),
    MIDNIGHT("midnight", "Midnight Navy", "سورمه‌ای نیمه‌شب", 0xFF3D5AFE),
    OCEAN("ocean", "Oceanic Depths", "اقیانوس عمیق", 0xFF00B4D8),
    PURPLE("purple", "Neon Purple", "بنفش نئونی", 0xFFBD00FF),
    SUNSET("sunset", "Solar Sunset", "غروب خورشیدی", 0xFFFF6D00),
    EMERALD("emerald", "Emerald Jade", "زمرد یشمی", 0xFF00E676);

    companion object {
        fun fromCode(code: String): ArenaTheme {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: DEFAULT
        }
    }
}
