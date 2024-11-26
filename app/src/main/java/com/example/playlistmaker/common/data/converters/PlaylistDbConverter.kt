package com.example.playlistmaker.common.data.converters

import com.example.playlistmaker.common.data.db.playlist.PlaylistEntity
import com.example.playlistmaker.common.domain.model.Playlist
import com.google.gson.Gson

class PlaylistDbConverter(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            playlistCoverPath = playlist.playlistCoverPath,
            tracksIds = gson.toJson(playlist.tracksIds),
            tracksCount = playlist.tracksCount
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = entity.playlistId,
            playlistName = entity.playlistName,
            playlistDescription = entity.playlistDescription,
            playlistCoverPath = entity.playlistCoverPath,
            tracksIds = gson.fromJson(entity.tracksIds, Array<Long>::class.java).toList(),
            tracksCount = entity.tracksCount
        )
    }


}