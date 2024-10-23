package com.example.playlistmaker.common.domain.api

import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {

    suspend fun addTrackToFavourite(track: Track)

    suspend fun deleteTrackFromFavourite(trackId: Long)

    fun getTracks(): Flow<List<Track>>

    fun getTracksIds(): Flow<List<Long>>
}