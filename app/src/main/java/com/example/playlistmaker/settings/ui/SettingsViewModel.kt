package com.example.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.model.DarkThemeMode
import com.example.playlistmaker.settings.domain.usecase.SaveDarkThemeModeUseCase
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val saveDarkThemeModeUseCase: SaveDarkThemeModeUseCase
) : ViewModel() {

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun saveDarkThemeMode(darkThemeMode: DarkThemeMode) {
        saveDarkThemeModeUseCase(darkThemeMode)
    }
}