package com.rndeveloper.ultimate.ui.screens.home

import com.google.android.gms.maps.model.LatLng
import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.Car
import com.rndeveloper.ultimate.model.User
import com.rndeveloper.ultimate.ui.BaseUiState

data class HomeUiState(
    val user: User,
    val loc: LatLng?,
    val isLogged: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        user = User(
            email = "",
            pass = "",
            username = "",
            uid = "",
            photo = "",
            token = "",
            points = 0,
            car = Car(0.0, 0.0),
            createdAt = ""
        ),
        loc = null,
        isLogged = false,
        isLoading = false,
        errorMessage = null,
    )
}
