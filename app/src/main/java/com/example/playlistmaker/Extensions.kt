package com.example.playlistmaker

import android.util.DisplayMetrics
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Locale

fun Track.getFormattedTime() =
    SimpleDateFormat("mm:ss", Locale.getDefault()).format(this.trackTimeMillis)


fun Track.getFormattedYear() =
    SimpleDateFormat("yyyy", Locale.getDefault()).format(this.releaseDate)


fun Track.getHighQualityCoverLink() =
    this.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")


fun convertDpToPx(displayMetrics: DisplayMetrics, valueInDp: Float) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, displayMetrics).toInt()