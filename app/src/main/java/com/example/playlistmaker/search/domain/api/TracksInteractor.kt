package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.common.domain.model.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: Consumer<List<Track>>)
}