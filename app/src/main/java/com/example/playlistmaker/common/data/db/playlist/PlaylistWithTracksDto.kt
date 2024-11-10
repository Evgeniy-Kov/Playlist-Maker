package com.example.playlistmaker.common.data.db.playlist

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithTracksDto(
    @Embedded
    val playlist: PlaylistEntity,
    @Relation (
        parentColumn = "playlistId",
        entityColumn = "trackId",
        associateBy = Junction(PlaylistTrackCrossRef::class)
    )
    val tracks: List<PlaylistTrackEntity>
)