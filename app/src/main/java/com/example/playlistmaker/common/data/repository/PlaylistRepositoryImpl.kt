package com.example.playlistmaker.common.data.repository

import com.example.playlistmaker.common.data.converters.PlaylistDbConverter
import com.example.playlistmaker.common.data.converters.PlaylistTrackDbConverter
import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.common.data.db.playlist.PlaylistEntity
import com.example.playlistmaker.common.data.db.playlist.PlaylistTrackCrossRef
import com.example.playlistmaker.common.data.db.playlist.PlaylistTrackEntity
import com.example.playlistmaker.common.data.db.playlist.PlaylistWithTracksDto
import com.example.playlistmaker.common.domain.api.PlaylistRepository
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.PlaylistWithTracks
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow
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
        appDatabase.playlistDao().addTrackToDb(playlistTrackDbConverter.map(track))
        appDatabase.playlistDao().addPlaylistTrackCrossRef(
            PlaylistTrackCrossRef(playlist.playlistId, track.trackId)
        )
        addPlaylist(playlist.copy(tracksCount = playlist.tracksCount + 1))
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        appDatabase.playlistDao().getPlaylists().collect { playlists ->
            emit(convertFromPlaylistEntity(playlists))
        }
    }

    override fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks> = flow {
        appDatabase.playlistDao().getPlaylistWithTracks(playlistId)
            .collect { playlistWithTracks ->
                emit(convertFromPlaylistWithTracksDto(playlistWithTracks))
            }
    }


    override fun getTrackWithPlaylists(trackId: Long): Flow<List<Playlist>> = flow {
        appDatabase.playlistDao().getTrackWithPlaylists(trackId)
            .collect { trackWithPlaylists ->
                val playlists: List<Playlist> = if (trackWithPlaylists == null) {
                    emptyList()
                } else {
                    convertFromPlaylistEntity(trackWithPlaylists.playlists)
                }
                emit(playlists)
            }
    }


    private fun convertFromPlaylistWithTracksDto(playlistDto: PlaylistWithTracksDto): PlaylistWithTracks {
        return PlaylistWithTracks(
            playlist = playlistDbConverter.map(playlistDto.playlist),
            tracks = convertFromPlaylistTrackEntity(playlistDto.tracks)
        )
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlistEntity -> playlistDbConverter.map(playlistEntity) }
    }

    private fun convertFromPlaylistTrackEntity(tracks: List<PlaylistTrackEntity>): List<Track> {
        return tracks.map { playlistTrackEntity -> playlistTrackDbConverter.map(playlistTrackEntity) }
    }

}