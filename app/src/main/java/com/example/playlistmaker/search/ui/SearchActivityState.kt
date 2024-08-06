package com.example.playlistmaker.search.ui

import com.example.playlistmaker.common.domain.model.Track

sealed interface SearchActivityState {

    object Loading : SearchActivityState

    data class Content(val tracks: List<Track>) : SearchActivityState

    data class History(val tracks: List<Track>) : SearchActivityState

    object Empty : SearchActivityState

    object Error : SearchActivityState
}