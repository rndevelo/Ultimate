package com.rndeveloper.ultimate.ui

import com.rndeveloper.ultimate.exceptions.CustomException

open class BaseUiState(
    open val isLoading: Boolean,
    open val errorMessage: CustomException?,
)
