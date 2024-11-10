package com.example.playlistmaker.common.data.db.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.common.data.db.DbConfig

@Entity(tableName = DbConfig.PLAYLISTS_TABLE_NAME)
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    val playlistName: String,
    val playlistDescription: String,
    val playlistCoverPath: String,
    val tracksCount: Long
)