package com.rndeveloper.ultimate.ui.screens.history

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.BaseUiState

data class HistoryUiState(
    val history: List<Spot>,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        history = emptyList(),
        isLoading = false,
        errorMessage = null,
    )
}
