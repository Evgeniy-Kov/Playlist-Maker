package com.example.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
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

//    companion object {
//        fun getViewModelFactory(): ViewModelProvider.Factory {
//            return viewModelFactory {
//                initializer {
//                    SettingsViewModel(
//                        Creator.provideSharingInteractor(),
//                        Creator.provideSaveDarkThemeModeUseCase()
//                    )
//                }
//            }
//        }
//    }
}