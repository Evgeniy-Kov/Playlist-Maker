package com.example.playlistmaker

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.google.gson.Gson

class SharedPreferencesManager(context: Context) {
    private val preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

    fun saveDarkThemeMode(darkThemeEnabled: Boolean) {
        preferences.edit {
            putInt(
                DARK_THEME_MODE_KEY,
                if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    fun getDarkThemeMode() = preferences.getInt(
        DARK_THEME_MODE_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    )


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
        Log.d("searchHistory size", "${searchHistory.size}")
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