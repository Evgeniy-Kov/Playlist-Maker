package com.example.playlistmaker.common

import android.app.Application
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.model.DarkThemeMode

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
        context = this
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

    companion object {
        private lateinit var context: Application

        fun getStringFromResources(resourceId: Int): String {
            return context.getString(resourceId)
        }
    }
}