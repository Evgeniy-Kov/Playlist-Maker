package com.example.playlistmaker.player.domain.api

interface StatusObserver {

    fun onProgress(progressInMillis: Int)

    fun onPlay()

    fun onPause()
}