package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.model.Resourse

interface TracksRepository {
    fun searchTracks(expression: String): Resourse<List<Track>>
}