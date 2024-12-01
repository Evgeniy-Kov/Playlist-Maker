package com.example.playlistmaker.common.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val playlistId: Long,
    val playlistName: String,
    val playlistDescription: String,
    val playlistCoverPath: String,
    val tracksIds: List<Long> = emptyList(),
    val tracksCount: Long
) : Parcelable {
    companion object {
        fun Playlist.getFormattedCount(): String {
            var result = ""
            val beforeLastDigit = tracksCount  % 100 / 10
            if (beforeLastDigit == 1L) {
                result = "треков"
            } else {
                when (tracksCount % 10) {
                    1L -> {
                        result = "трек"
                    }
                    2L -> {
                        result = "трека"
                    }
                    3L -> {
                        result = "трека"
                    }
                    4L -> {
                        result = "трека"
                    }
                    else -> {
                        result = "треков"
                    }
                }
            }
            return "${this.tracksCount} $result"
        }
    }
}