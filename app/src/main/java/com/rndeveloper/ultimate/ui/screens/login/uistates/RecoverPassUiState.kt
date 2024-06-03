package com.rndeveloper.ultimate.ui.screens.login.uistates

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.ui.BaseUiState

data class RecoverPassUiState(
    val isDialogVisible: Boolean,
    val emailErrorMessage: CustomException?,
    val recoveryPassResponse: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        isDialogVisible = false,
        emailErrorMessage = null,
        recoveryPassResponse = false,
        isLoading = false,
        errorMessage = null,
    )

//    override fun copyWithLoading(isLoading: Boolean): BaseUiState = this.copy(
//        isLoading = isLoading,
//    )
}
