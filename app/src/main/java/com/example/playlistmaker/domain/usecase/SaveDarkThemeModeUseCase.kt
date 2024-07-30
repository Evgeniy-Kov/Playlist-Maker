package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.DarkThemeModeRepository
import com.example.playlistmaker.domain.model.DarkThemeMode

class SaveDarkThemeModeUseCase(private val darkThemeModeRepository: DarkThemeModeRepository) {
    operator fun invoke(darkThemeMode: DarkThemeMode) {
        darkThemeModeRepository.saveDarkThemeMode(darkThemeMode)
    }
}