package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository

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