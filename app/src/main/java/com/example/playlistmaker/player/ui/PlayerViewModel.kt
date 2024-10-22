package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.api.TracksInteractor
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.StatusObserver
import com.example.playlistmaker.player.domain.model.PlayStatus
import com.example.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    private val _playStatusLiveData = MutableLiveData<PlayStatus>(PlayStatus())
    val playStatusLiveData: LiveData<PlayStatus>
        get() = _playStatusLiveData

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
    }

    private fun addTrackToFavourite(track: Track) {
        viewModelScope.launch {
            tracksInteractor.addTrackToFavourite(track)
        }
    }

    private fun deleteTrackFromFavourite(track: Track) {
        viewModelScope.launch {
            tracksInteractor.deleteTrackFromFavourite(track)
        }
    }

    override fun onCleared() {
        super.onCleared()
        reset()
    }
}