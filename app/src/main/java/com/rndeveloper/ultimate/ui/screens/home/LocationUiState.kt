package com.rndeveloper.ultimate.ui.screens.home

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.ui.BaseUiState

data class LocationUiState(
    val location: Position?,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        location = null,
        isLoading = false,
        errorMessage = null,
    )
}
