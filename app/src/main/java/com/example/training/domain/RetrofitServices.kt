package com.example.training.domain

import com.example.training.domain.model.Workout
import com.example.training.domain.model.VideoWorkout
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitServices {
    @GET("/get_workouts")
    fun getWorkoutsList(): Call<MutableList<Workout>>

    @GET("/get_video")
    fun getWorkout(
        @Query("id") id: Int
    ): Call<VideoWorkout>
}