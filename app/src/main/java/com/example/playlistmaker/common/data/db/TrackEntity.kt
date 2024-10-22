package com.example.playlistmaker.common.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = DbConfig.FAVOURITE_TRACKS_TABLE_NAME)
data class TrackEntity (
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