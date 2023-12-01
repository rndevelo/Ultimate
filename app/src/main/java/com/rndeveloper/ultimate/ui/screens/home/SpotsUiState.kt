package com.rndeveloper.ultimate.ui.screens.home

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.BaseUiState

data class SpotsUiState(
    val spots: List<Spot>,
    val selectedSpot: Spot?,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        spots = emptyList(),
        selectedSpot = null,
        isLoading = false,
        errorMessage = null,
    )
}
