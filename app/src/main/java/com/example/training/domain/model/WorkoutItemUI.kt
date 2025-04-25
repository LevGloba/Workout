package com.example.training.domain.model

import android.os.Parcelable
import com.example.training.domain.EnumWorkoutType
import kotlinx.parcelize.Parcelize

//Представление элемента для ui
@Parcelize
data class WorkoutItemUI(
    val id: Int,
    val title: String,
    val description: String,
    val typeUI: EnumWorkoutType,
    val time: String
): Parcelable
