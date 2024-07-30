package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.DarkThemeMode

interface DarkThemeModeRepository {
    fun getDarkThemeMode(): DarkThemeMode

    fun saveDarkThemeMode(darkThemeMode: DarkThemeMode)
}