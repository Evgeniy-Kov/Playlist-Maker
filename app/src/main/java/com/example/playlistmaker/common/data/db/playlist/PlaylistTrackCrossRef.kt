package com.example.playlistmaker.common.data.db.playlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.playlistmaker.common.data.db.DbConfig

@Entity(tableName = DbConfig.PLAYLIST_TRACK_CROSS_REF_TABLE_NAME, primaryKeys = ["playlistId", "trackId"])
data class PlaylistTrackCrossRef(
    val playlistId: Long,
    @ColumnInfo(index = true)
    val trackId: Long
)