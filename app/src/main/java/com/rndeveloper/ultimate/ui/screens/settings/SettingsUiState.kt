package com.rndeveloper.ultimate.ui.screens.settings

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.User
import com.rndeveloper.ultimate.ui.BaseUiState

data class SettingsUiState(
    val user: User,
    val isLogged: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        user = User(),
        isLogged = true,
        isLoading = false,
        errorMessage = null,
    )
}