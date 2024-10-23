package com.example.playlistmaker.library.ui

import com.example.playlistmaker.common.domain.model.Track

sealed interface FavouriteTracksFragmentState {

    data class Content(val tracks: List<Track>) : FavouriteTracksFragmentState

    data object Empty : FavouriteTracksFragmentState
}