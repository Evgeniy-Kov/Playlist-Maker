package com.example.playlistmaker.common.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "track_table")
data class TrackEntity (
    @PrimaryKey
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: Date,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
)