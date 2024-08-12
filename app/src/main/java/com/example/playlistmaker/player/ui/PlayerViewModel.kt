package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.StatusObserver
import com.example.playlistmaker.player.domain.model.PlayStatus
import com.example.playlistmaker.player.domain.model.PlayerState

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

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

    fun release() {
        playerInteractor.release()
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }

    companion object {
//        fun getViewModelFactory(): ViewModelProvider.Factory {
//            return viewModelFactory {
//                initializer {
//                    PlayerViewModel(Creator.providePlayerInteractor())
//                }
//            }
//        }
    }
}