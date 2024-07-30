package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: Consumer<List<Track>>)
}