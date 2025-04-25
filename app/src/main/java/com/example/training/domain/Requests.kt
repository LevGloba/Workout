package com.example.training.domain

import com.example.training.domain.model.Workout
import com.example.training.domain.model.VideoWorkout

interface Requests {

    suspend fun getWorkoutList(): List<Workout>

    suspend fun getWorkoutVideo(id: Int): VideoWorkout
}