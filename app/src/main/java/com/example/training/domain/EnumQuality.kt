package com.example.training.domain

enum class EnumQuality(val quality: Quality) {
    FULL_HD(Quality(heightToPx = 1080, widthToPx = 1920)),
    HD(Quality(heightToPx = 720, widthToPx = 1280)),
    SD(Quality(heightToPx = 480, widthToPx = 640)),
}
data class Quality(
    val heightToPx: Int,
    val widthToPx: Int
)