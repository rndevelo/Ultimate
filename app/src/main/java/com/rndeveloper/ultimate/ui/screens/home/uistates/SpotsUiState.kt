package com.rndeveloper.ultimate.ui.screens.home.uistates

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.Item
import com.rndeveloper.ultimate.ui.BaseUiState

data class SpotsUiState(
    val items: List<Item>,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        items = emptyList(),
        isLoading = false,
        errorMessage = null,
    )
}
