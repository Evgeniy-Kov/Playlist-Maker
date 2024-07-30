package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Resourse
import com.example.playlistmaker.domain.model.Track

interface TracksRepository {
    fun searchTracks(expression: String): Resourse<List<Track>>
}