package com.rndeveloper.ultimate.model

data class User(
    val isLogged: Boolean,
    val loc: Position?,
    val email: String,
    val pass: String,
    val username: String,
    val uid: String,
    val photo: String,
    val token: String,
    val points: Int,
    val car: Car?,
    val createdAt: String,
    val usertoken: String,
    val userpoints: Int,
) {
    constructor() : this(
        isLogged = false,
        loc = null,
        email = "",
        pass = "",
        username = "",
        uid = "",
        photo = "",
        token = "",
        points = 0,
        car = null,
        createdAt = "",
        usertoken = "",
        userpoints = 0
    )
}