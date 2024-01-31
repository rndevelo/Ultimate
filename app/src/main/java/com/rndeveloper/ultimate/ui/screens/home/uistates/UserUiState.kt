package com.rndeveloper.ultimate.ui.screens.home.uistates

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.User
import com.rndeveloper.ultimate.ui.BaseUiState

data class UserUiState(
    val user: User,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        user = User(),
        isLoading = false,
        errorMessage = null,
    )
}
