package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Resourse
import com.example.playlistmaker.common.domain.model.Track

interface TracksRepository {
    fun searchTracks(expression: String): Resourse<List<Track>>
}