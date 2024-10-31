package com.example.playlistmaker.search.ui

import com.example.playlistmaker.common.domain.model.Track

sealed interface SearchFragmentState {

    object Loading : SearchFragmentState

    data class Content(val tracks: List<Track>) : SearchFragmentState

    data class History(val tracks: List<Track>) : SearchFragmentState

    object Empty : SearchFragmentState

    object NothingFound : SearchFragmentState

    object Error : SearchFragmentState
}