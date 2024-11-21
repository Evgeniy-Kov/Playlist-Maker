package com.example.playlistmaker.library.ui.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import com.example.playlistmaker.common.domain.model.PlaylistWithTracks
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val _playlistWithTracksLiveData = MutableLiveData<PlaylistWithTracks>()
    val playlistWithTracksLiveData: LiveData<PlaylistWithTracks>
        get() = _playlistWithTracksLiveData

    fun getPlaylistWithTracks(playlistId: Long) {
        viewModelScope.launch {
            interactor.getPlaylistWithTracks(playlistId).collect { playlistWithTracks ->
                _playlistWithTracksLiveData.postValue(playlistWithTracks)
            }
        }
    }
}