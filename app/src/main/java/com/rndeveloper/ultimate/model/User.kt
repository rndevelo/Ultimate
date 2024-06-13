package com.rndeveloper.ultimate.model

import java.io.Serializable

data class User(
    val isLogged: Boolean,
    val email: String,
    val pass: String,
    val username: String,
    val uid: String,
    val photo: String,
    val token: String,
    val points: Long,
    val car: Position?,
    val createdAt: String,
    val usertoken: String,
    val userpoints: Int,
) {
    constructor() : this(
        isLogged = false,
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
        userpoints = 0,
    )
}