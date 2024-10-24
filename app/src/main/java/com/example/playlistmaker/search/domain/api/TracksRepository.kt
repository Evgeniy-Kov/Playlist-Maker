package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.model.Resourse
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<Resourse<List<Track>>>
}