package com.example.playlistmaker.common.data.db.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.common.data.db.DbConfig

@Entity(tableName = DbConfig.PLAYLIST_TRACK_TABLE_NAME)
data class PlaylistTrackEntity (
    @PrimaryKey
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
)