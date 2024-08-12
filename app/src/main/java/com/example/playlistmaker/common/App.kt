package com.example.playlistmaker.common

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.di.playerModule
import com.example.playlistmaker.di.settingsModule
import com.example.playlistmaker.di.sharingModule
import com.example.playlistmaker.settings.domain.model.DarkThemeMode
import com.example.playlistmaker.settings.domain.usecase.GetDarkThemeModeUseCase
import org.koin.android.ext.android.inject
import org.koin.core.context.startKoin

class App : Application() {

    private val getDarkThemeModeUseCase: GetDarkThemeModeUseCase by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(playerModule, settingsModule, sharingModule)
        }

        Creator.initApplication(this)

        switchTheme(
            getDarkThemeModeUseCase()
        )
    }

    fun switchTheme(darkThemeMode: DarkThemeMode) {
        when (darkThemeMode) {
            DarkThemeMode.FOLLOW_SYSTEM -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }

            DarkThemeMode.ON -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            DarkThemeMode.OFF -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}