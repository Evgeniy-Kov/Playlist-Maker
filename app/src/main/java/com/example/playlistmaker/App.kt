package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.model.DarkThemeMode

class App : Application() {

    private val getDarkThemeModeUseCase by lazy {
        Creator.provideGetDarkThemeModeUseCase()
    }

    override fun onCreate() {
        super.onCreate()

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