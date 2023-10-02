package com.rndeveloper.ultimate.exceptions

sealed class CustomException(val error: String) {
    data class GenericException(val value: String = "Generic Exception") : CustomException(value.trim())
}
