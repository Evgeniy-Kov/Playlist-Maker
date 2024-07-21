package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track

interface SearchHistoryInteractor {
    fun addTrackToSearchHistory(track: Track)

    fun getSearchHistory(): MutableList<Track>

    fun clearSearchHistory()
}