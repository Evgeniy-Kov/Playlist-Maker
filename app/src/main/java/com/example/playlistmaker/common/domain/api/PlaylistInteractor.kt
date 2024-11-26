package com.example.playlistmaker.common.domain.api

import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun addPlaylist(playlist: Playlist)

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    fun getPlaylist(playlistId: Long): Flow<Playlist>

    fun getPlaylists(): Flow<List<Playlist>>

    fun getTracksByIds(trackIds: List<Long>): Flow<List<Track>>

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun deleteTrackFromPlaylist(playlist: Playlist, trackId: Long)

}