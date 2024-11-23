package com.example.playlistmaker.common.domain.api

import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.PlaylistWithTracks
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun addPlaylist(playlist: Playlist)

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun deletePlaylist(playlistId: Long)

    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long)

    fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks>

    fun getTrackWithPlaylists(trackId: Long): Flow<List<Playlist>>


}