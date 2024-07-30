package com.example.playlistmaker.domain.usecase

import com.example.playlistmaker.domain.api.DarkThemeModeRepository
import com.example.playlistmaker.domain.model.DarkThemeMode

class GetDarkThemeModeUseCase(private val darkThemeModeRepository: DarkThemeModeRepository) {
    operator fun invoke(): DarkThemeMode {
        return darkThemeModeRepository.getDarkThemeMode()
    }
}