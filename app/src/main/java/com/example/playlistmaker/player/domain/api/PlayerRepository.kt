package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.model.PlayerState

interface PlayerRepository {

    fun play()

    fun pause()

    fun prepare(dataSource: String, observer: StatusObserver)

    fun release()

    fun getPlayerState(): PlayerState
}