package com.example.data

data class BusRoute(
    val id: String,
    val routeName: String,
    val busNumber: String,
    val status: RouteStatus,
    val eta: String
)

enum class RouteStatus {
    IN_TRANSIT, SCHEDULED, DELAYED, COMPLETED
}
