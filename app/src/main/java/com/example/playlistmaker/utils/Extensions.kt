package com.example.playlistmaker.utils

import android.content.Context
import android.util.TypedValue

fun Context.convertDpToPx(valueInDp: Float) =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        valueInDp,
        resources.displayMetrics
    ).toInt()