package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.api.StatusObserver
import com.example.playlistmaker.player.domain.model.PlayerState

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override fun play() {
        repository.play()
    }

    override fun pause() {
        repository.pause()
    }

    override fun prepare(dataSource: String, observer: StatusObserver) {
        repository.prepare(dataSource, observer)
    }

    override fun reset() {
        repository.reset()
    }

    override fun getPlayerState(): PlayerState {
        return repository.getPlayerState()
    }
}