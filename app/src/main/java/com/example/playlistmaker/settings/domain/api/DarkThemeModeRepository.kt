package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.settings.domain.model.DarkThemeMode

interface DarkThemeModeRepository {
    fun getDarkThemeMode(): DarkThemeMode

    fun saveDarkThemeMode(darkThemeMode: DarkThemeMode)
}