package com.rndeveloper.ultimate.model

enum class SpotType {
    GREEN,
    BLUE,
    YELLOW,
    RED,
    FREE
}

data class Position(
    val lat: Double,
    val lng: Double,
) {
    constructor() : this(lat = 0.0, lng = 0.0)
}


data class Directions(
    val addressLine: String,
    val locality: String,
    val area: String,
    val country: String,
) {
    constructor() : this(
        addressLine = "",
        locality = "",
        area = "",
        country = "",
    )
}