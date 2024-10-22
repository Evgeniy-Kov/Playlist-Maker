package com.example.playlistmaker.common.data.repository

import com.example.playlistmaker.common.data.converters.TrackDbConverter
import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.common.data.db.TrackEntity
import com.example.playlistmaker.common.domain.api.TracksRepository
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : TracksRepository {
    override suspend fun addTrackToFavourite(track: Track) {
        appDatabase.favouriteTrackDao().insertTrack(trackDbConverter.map(track))
    }

    override suspend fun deleteTrackFromFavourite(track: Track) {
        appDatabase.favouriteTrackDao().deleteTrack(trackDbConverter.map(track))
    }

    override fun getTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favouriteTrackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override fun getTracksIds(): Flow<List<Long>> = flow {
        val tracksIds = appDatabase.favouriteTrackDao().getTracksIds()
        emit(tracksIds)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { trackEntity -> trackDbConverter.map(trackEntity) }
    }
}