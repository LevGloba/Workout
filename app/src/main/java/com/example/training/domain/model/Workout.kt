package com.example.training.domain.model

//Представление элемента тренировок
data class Workout(
    val id: Int,
    val title: String,
    val description: String?,
    val type: Int,
    val duration: String
)
