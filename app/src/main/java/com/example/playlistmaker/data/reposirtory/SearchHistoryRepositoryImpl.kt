package com.example.playlistmaker.data.reposirtory

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson

class SearchHistoryRepositoryImpl(private val preferences: SharedPreferences) :
    SearchHistoryRepository {
    override fun saveSearchHistory(trackList: List<Track>) {
        val searchHistoryJson = Gson().toJson(trackList)
        preferences.edit {
            putString(SEARCH_HISTORY_KEY, searchHistoryJson)
        }
    }

    override fun getSearchHistory(): MutableList<Track> {
        val searchHistoryJson = preferences.getString(SEARCH_HISTORY_KEY, "")
        val searchHistory =
            Gson().fromJson(searchHistoryJson, Array<Track>::class.java) ?: emptyArray()
        return searchHistory.toMutableList()
    }

    override fun clearSearchHistory() {
        preferences.edit {
            remove(SEARCH_HISTORY_KEY)
        }
    }

    private companion object {
        const val SEARCH_HISTORY_KEY = "search_history_key"
    }
}