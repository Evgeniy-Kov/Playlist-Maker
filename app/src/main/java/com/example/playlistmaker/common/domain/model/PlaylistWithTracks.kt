package com.example.playlistmaker.common.domain.model

import java.util.concurrent.TimeUnit

data class PlaylistWithTracks(

    val playlist: Playlist,
    val tracks: List<Track>
) {
    companion object {
        fun PlaylistWithTracks.getFormattedDuration(): String {
            var result = ""
            var duration = 0L
            for (track in tracks) {
                duration += track.trackTimeMillis.toLong()
            }
            duration = TimeUnit.MILLISECONDS.toMinutes(duration)

            val beforeLastDigit = duration % 100 / 10
            if (beforeLastDigit == 1L) {
                result = "минут"
            } else {
                when (duration % 10) {
                    1L -> {
                        result = "минута"
                    }

                    2L, 3L, 4L -> {
                        result = "минуты"
                    }

                    else -> {
                        result = "минут"
                    }
                }
            }
            return "${duration} $result"
        }
    }
}