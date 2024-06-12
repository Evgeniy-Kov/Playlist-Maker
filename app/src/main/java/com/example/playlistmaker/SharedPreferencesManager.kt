package com.example.playlistmaker

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson

class SharedPreferencesManager(context: Context) {
    private val preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

    fun saveDarkThemeMode(darkThemeMode: DarkThemeMode) {
        preferences.edit {
            putString(
                DARK_THEME_MODE_KEY, darkThemeMode.name
            )
        }
    }

    fun getDarkThemeMode(): DarkThemeMode {
        val darkThemeModeString = preferences.getString(
            DARK_THEME_MODE_KEY, null
        ) ?: DarkThemeMode.FOLLOW_SYSTEM.name
        return DarkThemeMode.valueOf(darkThemeModeString)
    }

    fun saveSearchHistory(trackList: List<Track>) {
        val searchHistoryJson = Gson().toJson(trackList)
        preferences.edit {
            putString(SEARCH_HISTORY_KEY, searchHistoryJson)
        }
    }

    fun getSearchHistory(): MutableList<Track> {
        val searchHistoryJson = preferences.getString(SEARCH_HISTORY_KEY, "")
        val searchHistory =
            Gson().fromJson(searchHistoryJson, Array<Track>::class.java) ?: emptyArray()
        return searchHistory.toMutableList()
    }

    fun clearSearchHistory() {
        preferences.edit {
            remove(SEARCH_HISTORY_KEY)
        }
    }

    companion object {
        private const val APP_PREFERENCES = "playlist_maker_preferences"
        private const val DARK_THEME_MODE_KEY = "dark_theme_mode_key"
        private const val SEARCH_HISTORY_KEY = "search_history_key"
    }

}