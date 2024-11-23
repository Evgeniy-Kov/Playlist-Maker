package com.example.playlistmaker.common.domain.impl

import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import com.example.playlistmaker.common.domain.api.PlaylistRepository
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.PlaylistWithTracks
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist) {
        repository.addPlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToPlaylist(track, playlist)
    }

    override fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks> {
        return repository.getPlaylistWithTracks(playlistId)
    }

    override fun getTrackWithPlaylists(trackId: Long): Flow<List<Playlist>> {
        return repository.getTrackWithPlaylists(trackId)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        repository.deletePlaylist(playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long) {
        repository.deleteTrackFromPlaylist(playlistId, trackId)
    }
}