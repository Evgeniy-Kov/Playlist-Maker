package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.SharedPreferencesManager
import com.example.playlistmaker.domain.model.DarkThemeMode

class App : Application() {

    val sharedPreferencesManager by lazy {
        SharedPreferencesManager(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        switchTheme(
            sharedPreferencesManager.getDarkThemeMode()
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