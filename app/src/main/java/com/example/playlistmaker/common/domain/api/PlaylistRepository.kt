package com.example.playlistmaker.common.domain.api

import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun addPlaylist(playlist: Playlist)

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylistWithTracks(): Flow<List<Track>>

    fun getTrackWithPlaylists(trackId: Long): Flow<List<Playlist>>



}