package com.example.training

import android.view.View
import androidx.core.view.ViewCompat
import androidx.media3.exoplayer.ExoPlayer

object Constants {
    const val BASE_URL = "https://ref.test.kolsa.ru/"
    var player: ExoPlayer? = null
}

fun setTransitionName(view: View, name: String) {
    ViewCompat.setTransitionName(view, name)
}