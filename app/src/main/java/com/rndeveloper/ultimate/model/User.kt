package com.rndeveloper.ultimate.model

data class User(
    val email: String,
    val pass: String,
    val username: String,
    val uid: String,
    val photo: String,
    val token: String,
    val points: Long,
    val car: Position?,
    val createdAt: String,
) {
    constructor() : this(
        email = "",
        pass = "",
        username = "",
        uid = "",
        photo = "",
        token = "",
        points = 0,
        car = null,
        createdAt = "",
    )
}