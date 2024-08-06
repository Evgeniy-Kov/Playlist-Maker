package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.api.StatusObserver
import com.example.playlistmaker.player.domain.model.PlayerState

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {
    private var playerState = PlayerState.PLAYER_STATE_DEFAULT

    private lateinit var statusObserver: StatusObserver

    private val handler = Handler(Looper.getMainLooper())

    private val updatePlaybackTimeRunnable by lazy {
        createUpdatePlaybackTimeTask()
    }

    override fun play() {
        mediaPlayer.start()
        statusObserver.onPlay()
        playerState = PlayerState.PLAYER_STATE_PLAYING
        handler.post(updatePlaybackTimeRunnable)
    }

    override fun pause() {
        mediaPlayer.pause()
        statusObserver.onPause()
        playerState = PlayerState.PLAYER_STATE_PAUSED
        handler.removeCallbacks(updatePlaybackTimeRunnable)
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
            handler.removeCallbacks(updatePlaybackTimeRunnable)
        }
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getPlayerState(): PlayerState {
        return playerState
    }

    private fun createUpdatePlaybackTimeTask(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == PlayerState.PLAYER_STATE_PLAYING) {
                    statusObserver.onProgress(mediaPlayer.currentPosition)
                    handler.postDelayed(this, PLAYER_UPDATE_PLAYBACK_TIME_DELAY_MILLIS)
                }
            }
        }
    }

    companion object {
        private const val PLAYER_DEFAULT_PLAYBACK_TIME = 0
        private const val PLAYER_UPDATE_PLAYBACK_TIME_DELAY_MILLIS = 100L
    }
}