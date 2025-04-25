package com.example.training.data

import com.example.training.domain.Requests
import com.example.training.domain.RetrofitServices
import com.example.training.domain.model.Workout
import com.example.training.domain.model.VideoWorkout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

//Выполняет Get запросы на сервер для получения данных
class RequestsImpl @Inject constructor() : Requests {

    private val retrofitServices: RetrofitServices = RetrofitClientImpl().getRetrofitService()

    override suspend fun getWorkoutList(): List<Workout> =
        withContext(Dispatchers.IO) {
            retrofitServices.getWorkoutsList().execute().body()?: emptyList()
        }

    override suspend fun getWorkoutVideo(id: Int): VideoWorkout =
        withContext(Dispatchers.IO){
            retrofitServices.getWorkout(id).execute().body()?: VideoWorkout(
                id = -1,
                duration = "",
                link = ""
            )
    }
}