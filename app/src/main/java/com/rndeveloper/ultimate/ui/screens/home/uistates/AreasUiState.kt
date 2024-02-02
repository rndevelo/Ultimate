package com.rndeveloper.ultimate.ui.screens.home.uistates

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.BaseUiState

data class AreasUiState(
    val areas: List<Spot>,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        areas = emptyList(),
        isLoading = false,
        errorMessage = null,
    )
}