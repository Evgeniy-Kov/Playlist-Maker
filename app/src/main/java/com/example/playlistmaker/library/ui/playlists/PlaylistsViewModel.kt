package com.example.playlistmaker.library.ui.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import com.example.playlistmaker.common.domain.model.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    init {
        getPlaylistsFlow()
    }

    private val _screenStateLiveData = MutableLiveData<PlaylistsFragmentState>()
    val screenStateLiveData: LiveData<PlaylistsFragmentState>
        get() = _screenStateLiveData



    fun getPlaylistsFlow() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(tracks: List<Playlist>) {
        if (tracks.isEmpty()) {
            _screenStateLiveData.postValue(PlaylistsFragmentState.Empty)
        } else {
            _screenStateLiveData.postValue(PlaylistsFragmentState.Content(tracks))
        }
    }
}