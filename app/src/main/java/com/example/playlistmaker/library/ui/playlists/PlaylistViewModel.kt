package com.example.playlistmaker.library.ui.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val _playlistLiveData = MutableLiveData<Playlist>()
    val playlistLiveData: LiveData<Playlist>
        get() = _playlistLiveData

    private val _tracksLiveData = MutableLiveData<List<Track>>()
    val tracksLiveData: LiveData<List<Track>>
        get() = _tracksLiveData


    fun getPlaylist(playlistId: Long) {
        viewModelScope.launch {
            interactor.getPlaylist(playlistId).collect { playlist ->
                _playlistLiveData.value = playlist
            }
        }
    }

    fun getTracksByIds(tracksIds: List<Long>) {
        viewModelScope.launch {
            interactor.getTracksByIds(tracksIds).collect { tracks ->
                _tracksLiveData.postValue(tracks)
            }
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        CoroutineScope(SupervisorJob()).launch(Dispatchers.IO) {
                interactor.deletePlaylist(playlist)
        }
    }

    fun deleteTrackFromPlaylist(playlist: Playlist, trackId: Long) {
        viewModelScope.launch {
            interactor.deleteTrackFromPlaylist(playlist, trackId)
        }
    }
}