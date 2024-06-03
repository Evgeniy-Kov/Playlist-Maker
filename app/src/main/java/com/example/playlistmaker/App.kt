package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private var darkThemeMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    val sharedPreferencesManager by lazy {
        SharedPreferencesManager(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        switchTheme(
            sharedPreferencesManager.getDarkThemeMode()
        )
    }

    fun switchTheme(darkThemeMode: Int) {
        this.darkThemeMode = darkThemeMode
        AppCompatDelegate.setDefaultNightMode(darkThemeMode)
    }
}