package com.example.playlistmaker.common.domain.impl

import com.example.playlistmaker.common.domain.api.PlaylistInteractor
import com.example.playlistmaker.common.domain.api.PlaylistRepository
import com.example.playlistmaker.common.domain.model.Playlist
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

    override fun getTracksByIds(trackIds: List<Long>): Flow<List<Track>> {
        return repository.getTracksByIds(trackIds)
    }

    override fun getPlaylist(playlistId: Long): Flow<Playlist> {
        return repository.getPlaylist(playlistId)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist, trackId: Long) {
        repository.deleteTrackFromPlaylist(playlist, trackId)
    }
}