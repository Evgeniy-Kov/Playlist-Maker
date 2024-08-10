package com.example.playlistmaker.player.domain.model

data class PlayStatus(
    val isPlaying: Boolean = false,
    val progress: Int = 0
)