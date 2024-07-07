package com.rndeveloper.ultimate.ui.screens.login.uistates

import androidx.annotation.StringRes
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.User
import com.rndeveloper.ultimate.ui.BaseUiState

data class LoginUiState(
    val loginSignState: LoginSignState,
    val user: User,
    val isLogged: Boolean,
    val isRegistered: Boolean,
    val isEmailSent: Boolean,
    val isEmailVerified: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        loginSignState = LoginSignState.SignIn(),
        user = User(),
        isLogged = false,
        isRegistered = false,
        isEmailSent = false,
        isEmailVerified = false,
        isLoading = false,
        errorMessage = null,
    )
}

sealed class LoginSignState(
    @StringRes val buttonText: Int,
    @StringRes val accountText: Int,
    @StringRes val signText: Int
) {
    data class SignIn(
        @StringRes val button: Int = R.string.login_text_sign_in,
        @StringRes val account: Int = R.string.login_text_no_account,
        @StringRes val sign: Int = R.string.login_text_sign_up
    ) : LoginSignState(button, account, sign)

    data class SignUp(
        @StringRes val button: Int = R.string.login_text_sign_up,
        @StringRes val account: Int = R.string.login_text_yes_account,
        @StringRes val sign: Int = R.string.login_text_sign_in
    ) : LoginSignState(button, account, sign)
}