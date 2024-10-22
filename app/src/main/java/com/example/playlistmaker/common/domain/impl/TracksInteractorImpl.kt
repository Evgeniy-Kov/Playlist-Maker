package com.example.playlistmaker.common.domain.impl

import com.example.playlistmaker.common.domain.api.TracksInteractor
import com.example.playlistmaker.common.domain.api.TracksRepository
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override suspend fun addTrackToFavourite(track: Track) {
        repository.addTrackToFavourite(track)
    }

    override suspend fun deleteTrackFromFavourite(track: Track) {
        repository.deleteTrackFromFavourite(track)
    }

    override fun getTracks(): Flow<List<Track>> {
        return repository.getTracks()
    }

    override fun getTracksIds(): Flow<List<Long>> {
        return repository.getTracksIds()
    }
}