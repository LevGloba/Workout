package com.example.training.domain

import com.example.training.R

//Перечисление типов тренировок
enum class EnumWorkoutType (val typeId: Int) {
    ALL(0),
    WORKOUT(R.string.string_workout),
    ETHER(R.string.string_ethir),
    COMPLEX(R.string.string_complex);
}