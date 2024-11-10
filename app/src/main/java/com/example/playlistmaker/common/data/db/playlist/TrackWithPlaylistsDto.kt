package com.example.playlistmaker.common.data.db.playlist

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TrackWithPlaylistsDto(
    @Embedded
    val track: PlaylistTrackEntity,
    @Relation (
        parentColumn = "trackId",
        entityColumn = "playlistId",
        associateBy = Junction(PlaylistTrackCrossRef::class)
    )
    val playlists: List<PlaylistEntity>
)