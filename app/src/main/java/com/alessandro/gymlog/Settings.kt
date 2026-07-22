package com.alessandro.gymlog

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.alessandro.gymlog.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

object Settings {
    private val THEME_KEY = stringPreferencesKey("theme_mode")

    fun themeMode(context: Context): Flow<ThemeMode> =
        context.dataStore.data.map { prefs ->
            runCatching { ThemeMode.valueOf(prefs[THEME_KEY] ?: "SYSTEM") }.getOrDefault(ThemeMode.SYSTEM)
        }

    suspend fun setThemeMode(context: Context, mode: ThemeMode) {
        context.dataStore.edit { it[THEME_KEY] = mode.name }
    }
}
