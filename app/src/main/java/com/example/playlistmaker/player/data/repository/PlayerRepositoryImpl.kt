package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.api.StatusObserver
import com.example.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {
    private var playerState = PlayerState.PLAYER_STATE_DEFAULT

    private lateinit var statusObserver: StatusObserver

    private var timerJob: Job? = null

    private var  scope: CoroutineScope? = null


    override fun play() {
        mediaPlayer.start()
        statusObserver.onPlay()
        playerState = PlayerState.PLAYER_STATE_PLAYING
        startTimer()
    }

    override fun pause() {
        mediaPlayer.pause()
        statusObserver.onPause()
        playerState = PlayerState.PLAYER_STATE_PAUSED
        stopTimer()
    }

    override fun prepare(dataSource: String, observer: StatusObserver) {
        statusObserver = observer

        mediaPlayer.setDataSource(dataSource)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.PLAYER_STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            statusObserver.onProgress(PLAYER_DEFAULT_PLAYBACK_TIME)
            statusObserver.onPause()
            playerState = PlayerState.PLAYER_STATE_PREPARED
            stopTimer()
        }
    }

    override fun reset() {
        mediaPlayer.reset()
    }

    override fun getPlayerState(): PlayerState {
        return playerState
    }

    private fun startTimer() {
            scope = MainScope()
            timerJob = scope?.launch {
                while (mediaPlayer.isPlaying) {
                    delay(PLAYER_UPDATE_PLAYBACK_TIME_DELAY_MILLIS)
                    statusObserver.onProgress(mediaPlayer.currentPosition)
                }
            }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        scope?.cancel()
    }

    companion object {
        private const val PLAYER_DEFAULT_PLAYBACK_TIME = 0
        private const val PLAYER_UPDATE_PLAYBACK_TIME_DELAY_MILLIS = 300L
    }
}