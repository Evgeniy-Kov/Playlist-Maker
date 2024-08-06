package com.example.playlistmaker.settings.domain.usecase

import com.example.playlistmaker.settings.domain.api.DarkThemeModeRepository
import com.example.playlistmaker.settings.domain.model.DarkThemeMode

class GetDarkThemeModeUseCase(private val darkThemeModeRepository: DarkThemeModeRepository) {
    operator fun invoke(): DarkThemeMode {
        return darkThemeModeRepository.getDarkThemeMode()
    }
}