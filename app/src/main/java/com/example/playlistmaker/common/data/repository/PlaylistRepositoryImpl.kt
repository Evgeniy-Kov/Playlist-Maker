package com.example.playlistmaker.common.data.repository

import com.example.playlistmaker.common.data.converters.PlaylistDbConverter
import com.example.playlistmaker.common.data.converters.PlaylistTrackDbConverter
import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.common.data.db.playlist.PlaylistEntity
import com.example.playlistmaker.common.data.db.playlist.PlaylistTrackEntity
import com.example.playlistmaker.common.domain.api.PlaylistRepository
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val playlistTrackDbConverter: PlaylistTrackDbConverter
) : PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().addPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val updatedPlaylist = playlist.copy(
            tracksIds = playlist.tracksIds + listOf(track.trackId),
            tracksCount = playlist.tracksIds.size + 1L
        )
        appDatabase.playlistDao().addPlaylist(playlistDbConverter.map(updatedPlaylist))
        appDatabase.playlistDao().addTrackToDb(playlistTrackDbConverter.map(track))

    }

    override fun getPlaylist(playlistId: Long): Flow<Playlist> = flow {
        appDatabase.playlistDao().getPlaylist(playlistId).collect { playlist ->
            try {
                emit(playlistDbConverter.map(playlist))
            } catch (e: NullPointerException) { }
        }
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        appDatabase.playlistDao().getPlaylists().collect { playlists ->
            emit(convertFromPlaylistEntity(playlists))
        }
    }

    override fun getTracksByIds(trackIds: List<Long>): Flow<List<Track>> = flow {
        val tracks = mutableListOf<PlaylistTrackEntity>()
        for (id in trackIds) {
            tracks.add(appDatabase.playlistDao().getTrackById(id))
        }
        emit(convertFromPlaylistTrackEntity(tracks.reversed()))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val tracksIds = playlist.tracksIds
        appDatabase.playlistDao().deletePlaylistFromDb(playlist.playlistId)
        deleteTracksWithoutRefs(tracksIds)
    }

    private suspend fun deleteTracksWithoutRefs(tracksIds: List<Long>) {
        val playlists = appDatabase.playlistDao().getPlaylists().first()
        for (trackId in tracksIds) {
            var hasRef = false
            for (playlist in playlists) {
                if (playlist.tracksIds.contains(trackId.toString())) {
                    hasRef = true
                    break
                }
            }
            if (!hasRef) {
                appDatabase.playlistDao().deleteTrackFromDb(trackId)
            }
        }
    }

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist, trackId: Long) {
        val ids = playlist.tracksIds.toMutableList()
        ids.remove(trackId)
        val updatedPlaylist = playlist.copy(
            tracksIds = ids,
            tracksCount = playlist.tracksIds.size - 1L
        )
        addPlaylist(updatedPlaylist)
        deleteTracksWithoutRefs(listOf(trackId))
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlistEntity -> playlistDbConverter.map(playlistEntity) }
    }

    private fun convertFromPlaylistTrackEntity(tracks: List<PlaylistTrackEntity>): List<Track> {
        return tracks.map { playlistTrackEntity -> playlistTrackDbConverter.map(playlistTrackEntity) }
    }

}