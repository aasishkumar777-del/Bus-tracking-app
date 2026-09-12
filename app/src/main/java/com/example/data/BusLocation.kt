package com.example.data

data class BusLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0L,
    val speed: Float = 0f,
    val isMoving: Boolean = false
)
