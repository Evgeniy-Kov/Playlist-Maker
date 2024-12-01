package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import com.example.playlistmaker.common.domain.api.TracksInteractor
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.StatusObserver
import com.example.playlistmaker.player.domain.model.PlayStatus
import com.example.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val tracksInteractor: TracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playStatusLiveData = MutableLiveData<PlayStatus>(PlayStatus())
    val playStatusLiveData: LiveData<PlayStatus>
        get() = _playStatusLiveData

    private val _isFavouriteTrackLiveData = MutableLiveData<Boolean>()
    val isFavouriteTrackLiveData: LiveData<Boolean>
        get() = _isFavouriteTrackLiveData

    private val _playlistsLiveData = MutableLiveData<List<Playlist>>()
    val playlistsLiveData: LiveData<List<Playlist>>
        get() = _playlistsLiveData

    private val _messageOfAddingTrackToPlaylistLiveData = MutableLiveData<String>()
    val messageOfAddingTrackToPlaylistLiveData: LiveData<String>
        get() = _messageOfAddingTrackToPlaylistLiveData

    private val _resultOfAddingTrackToPlaylistLiveData = MutableLiveData<Boolean>()
    val resultOfAddingTrackToPlaylistLiveData: LiveData<Boolean>
        get() = _resultOfAddingTrackToPlaylistLiveData

    private val listOfPlaylists = mutableListOf<Playlist>()

    init {
        getPlaylists()
    }

    fun play() {
        playerInteractor.play()
    }

    fun pause() {
        playerInteractor.pause()
    }

    fun playbackControl() {
        when (playerInteractor.getPlayerState()) {
            PlayerState.PLAYER_STATE_DEFAULT -> {}
            PlayerState.PLAYER_STATE_PLAYING -> pause()
            PlayerState.PLAYER_STATE_PREPARED, PlayerState.PLAYER_STATE_PAUSED -> play()
        }
    }

    fun prepare(dataSource: String) {
        playerInteractor.prepare(
            dataSource,
            object : StatusObserver {
                override fun onProgress(progressInMillis: Int) {
                    _playStatusLiveData.value =
                        playStatusLiveData.value?.copy(progress = progressInMillis)
                }

                override fun onPlay() {
                    _playStatusLiveData.value = playStatusLiveData.value?.copy(isPlaying = true)
                }

                override fun onPause() {
                    _playStatusLiveData.value = playStatusLiveData.value?.copy(isPlaying = false)
                }
            })
    }

    fun reset() {
        playerInteractor.reset()
    }

    fun onFavouriteButtonPressed(track: Track) {
        if (track.isFavourite) {
            deleteTrackFromFavourite(track)
        } else {
            addTrackToFavourite(track)
        }
        setIsFavouriteFlag(!track.isFavourite)
    }

    fun setIsFavouriteFlag(isFavourite: Boolean) {
        _isFavouriteTrackLiveData.value = isFavourite
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {

        if (!isPlaylistContainsTrack(playlist, track)) {
            _resultOfAddingTrackToPlaylistLiveData.value = true
            _messageOfAddingTrackToPlaylistLiveData.value =
                "Добавлено в плейлист ${playlist.playlistName}"
            viewModelScope.launch {
                playlistInteractor.addTrackToPlaylist(track, playlist)
            }
        } else {
            _resultOfAddingTrackToPlaylistLiveData.value = false
            _messageOfAddingTrackToPlaylistLiveData.value =
                "Трек уже добавлен в плейлист ${playlist.playlistName}"
        }
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .collect { playlists ->
                    _playlistsLiveData.postValue(playlists)
                }
        }
    }

    fun getPlaylistsContainTrack(trackId: Long) {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .collect { playlists ->
                    listOfPlaylists.addAll(playlists)
                }
        }
    }

    fun isPlaylistContainsTrack(playlist: Playlist, track: Track): Boolean {
        return playlist.tracksIds.contains(track.trackId)
    }


    private fun addTrackToFavourite(track: Track) {
        viewModelScope.launch {
            tracksInteractor.addTrackToFavourite(track)
        }
    }

    private fun deleteTrackFromFavourite(track: Track) {
        viewModelScope.launch {
            tracksInteractor.deleteTrackFromFavourite(track.trackId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        reset()
    }
}