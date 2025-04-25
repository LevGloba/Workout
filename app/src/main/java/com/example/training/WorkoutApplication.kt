package com.example.training

import android.app.Application
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WorkoutApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Constants.player = ExoPlayer.Builder(applicationContext).build()
    }
}