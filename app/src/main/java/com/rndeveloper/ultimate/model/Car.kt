package com.rndeveloper.ultimate.model

data class Car(
    val lat: Double,
    val lng: Double
) {
    constructor() : this(lat = 0.0, lng = 0.0)
}
