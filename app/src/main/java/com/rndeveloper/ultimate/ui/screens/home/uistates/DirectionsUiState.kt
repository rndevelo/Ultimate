package com.rndeveloper.ultimate.ui.screens.home.uistates

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.Directions
import com.rndeveloper.ultimate.ui.BaseUiState

data class DirectionsUiState(
    val directions: Directions,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        directions = Directions(addressLine = "", locality = "", area = "", country = ""),
        isLoading = false,
        errorMessage = null,
    )
}