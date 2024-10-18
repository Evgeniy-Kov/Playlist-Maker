package com.example.playlistmaker.common.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
data class Track(
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
) : Parcelable {
    companion object {
        fun Track.getFormattedTime() =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(this.trackTimeMillis)

        fun Track.getHighQualityCoverLink() =
            this.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }
}