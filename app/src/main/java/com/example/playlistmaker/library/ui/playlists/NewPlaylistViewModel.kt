package com.example.playlistmaker.library.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import com.example.playlistmaker.common.domain.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            interactor.addPlaylist(playlist)
        }
    }
}