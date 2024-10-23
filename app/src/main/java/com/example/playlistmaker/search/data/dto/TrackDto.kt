package com.example.playlistmaker.search.data.dto

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TrackDto(
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
) {
    companion object {
        fun TrackDto.getFormattedYear() =
            SimpleDateFormat("yyyy", Locale.getDefault()).format(this.releaseDate)
    }
}