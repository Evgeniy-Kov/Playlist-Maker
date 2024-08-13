package com.example.playlistmaker.di

import com.example.playlistmaker.settings.data.repository.DarkThemeModeRepositoryImpl
import com.example.playlistmaker.settings.domain.api.DarkThemeModeRepository
import com.example.playlistmaker.settings.domain.usecase.GetDarkThemeModeUseCase
import com.example.playlistmaker.settings.domain.usecase.SaveDarkThemeModeUseCase
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule  = module {

    single<DarkThemeModeRepository> {
        DarkThemeModeRepositoryImpl(get())
    }

    factory {
        GetDarkThemeModeUseCase(get())
    }

    factory {
        SaveDarkThemeModeUseCase(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }
}