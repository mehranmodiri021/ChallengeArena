package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.localization.AppLanguage
import com.example.ui.theme.ArenaTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "arena_settings")

data class UserSettings(
    val language: AppLanguage = AppLanguage.PERSIAN,
    val theme: ArenaTheme = ArenaTheme.DEFAULT,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true
)

class SettingsDataStore(private val context: Context) {

    private object PreferencesKeys {
        val LANGUAGE = stringPreferencesKey("arena_language")
        val THEME = stringPreferencesKey("arena_theme")
        val SOUND = booleanPreferencesKey("arena_sound")
        val HAPTIC = booleanPreferencesKey("arena_haptic")
        val NOTIFICATIONS = booleanPreferencesKey("arena_notifications")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        val langStr = preferences[PreferencesKeys.LANGUAGE] ?: AppLanguage.PERSIAN.name
        val themeStr = preferences[PreferencesKeys.THEME] ?: ArenaTheme.DEFAULT.name

        val language = try {
            AppLanguage.valueOf(langStr)
        } catch (_: Exception) {
            AppLanguage.PERSIAN
        }

        val theme = try {
            ArenaTheme.valueOf(themeStr)
        } catch (_: Exception) {
            ArenaTheme.DEFAULT
        }

        val sound = preferences[PreferencesKeys.SOUND] ?: true
        val haptic = preferences[PreferencesKeys.HAPTIC] ?: true
        val notifications = preferences[PreferencesKeys.NOTIFICATIONS] ?: true

        UserSettings(
            language = language,
            theme = theme,
            soundEnabled = sound,
            hapticEnabled = haptic,
            notificationsEnabled = notifications
        )
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language.name
        }
    }

    suspend fun setTheme(theme: ArenaTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND] = enabled
        }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTIC] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS] = enabled
        }
    }
}
