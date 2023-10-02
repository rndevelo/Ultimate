package com.rndeveloper.ultimate.exceptions

sealed class LoginException(message: String) : CustomException(message) {

    data class EmailInvalidFormat(val value: String = "Email format not valid") : LoginException(value)

    data class PasswordInvalidFormat(
        val value: String = """
            Password must contain special chars, numbers, lower and upper case,
            and more than 8 characters
        """.trimIndent()
    ) : LoginException(value)
}
