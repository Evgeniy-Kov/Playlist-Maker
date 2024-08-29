package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.model.PlayerState

interface PlayerInteractor {

    fun play()

    fun pause()

    fun prepare(dataSource: String, observer: StatusObserver)

    fun reset()

    fun getPlayerState(): PlayerState
}