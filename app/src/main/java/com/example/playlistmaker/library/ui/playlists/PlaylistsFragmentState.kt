package com.example.playlistmaker.library.ui.playlists

import com.example.playlistmaker.common.domain.model.Playlist

sealed interface PlaylistsFragmentState {

    data class Content(val playlists: List<Playlist>) : PlaylistsFragmentState

    data object Empty : PlaylistsFragmentState
}