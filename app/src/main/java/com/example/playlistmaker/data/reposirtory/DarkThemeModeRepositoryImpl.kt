package com.example.playlistmaker.data.reposirtory

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.domain.api.DarkThemeModeRepository
import com.example.playlistmaker.domain.model.DarkThemeMode

class DarkThemeModeRepositoryImpl(private val preferences: SharedPreferences) :
    DarkThemeModeRepository {

    override fun getDarkThemeMode(): DarkThemeMode {
        val darkThemeModeString = preferences.getString(
            DARK_THEME_MODE_KEY, null
        ) ?: DarkThemeMode.FOLLOW_SYSTEM.name
        return DarkThemeMode.valueOf(darkThemeModeString)
    }

    override fun saveDarkThemeMode(darkThemeMode: DarkThemeMode) {
        preferences.edit {
            putString(
                DARK_THEME_MODE_KEY, darkThemeMode.name
            )
        }
    }

    private companion object {
        const val DARK_THEME_MODE_KEY = "dark_theme_mode_key"
    }
}