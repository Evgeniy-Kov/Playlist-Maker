package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.model.Track

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchHistoryInteractor {
    override fun addTrackToSearchHistory(track: Track) {
        searchHistoryRepository.addTrackToSearchHistory(track)
    }

    override fun getSearchHistory(): MutableList<Track> {
        return searchHistoryRepository.getSearchHistory()
    }

    override fun clearSearchHistory() {
        searchHistoryRepository.clearSearchHistory()
    }
}