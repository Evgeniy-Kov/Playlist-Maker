package com.example.playlistmaker.settings.domain.usecase

import com.example.playlistmaker.settings.domain.api.DarkThemeModeRepository
import com.example.playlistmaker.settings.domain.model.DarkThemeMode

class SaveDarkThemeModeUseCase(private val darkThemeModeRepository: DarkThemeModeRepository) {
    operator fun invoke(darkThemeMode: DarkThemeMode) {
        darkThemeModeRepository.saveDarkThemeMode(darkThemeMode)
    }
}