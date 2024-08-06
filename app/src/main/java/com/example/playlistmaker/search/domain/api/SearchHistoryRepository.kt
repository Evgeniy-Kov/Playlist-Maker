package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.common.domain.model.Track

interface SearchHistoryRepository {
    fun addTrackToSearchHistory(track: Track)

    fun getSearchHistory(): MutableList<Track>

    fun clearSearchHistory()
}