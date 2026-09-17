package com.example.ui.localization

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(val code: String, val displayName: String, val layoutDirection: LayoutDirection) {
    PERSIAN("fa", "فارسی", LayoutDirection.Rtl),
    ENGLISH("en", "English", LayoutDirection.Ltr);

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: PERSIAN
        }
    }
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.PERSIAN }
val LocalStrings = compositionLocalOf { PersianStrings as AppStrings }
