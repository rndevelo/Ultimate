package com.rndeveloper.ultimate.ui.screens.login

import androidx.annotation.StringRes
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.Car
import com.rndeveloper.ultimate.model.User
import com.rndeveloper.ultimate.ui.BaseUiState

data class LoginUiState(
    val screenState: LoginState,
    val user: User,
    val emailErrorMessage: CustomException?,
    val passErrorMessage: CustomException?,
    val isLogged: Boolean,
    val isSendEmailRecovered: Boolean,
    override val isLoading: Boolean,
    override val errorMessage: CustomException?,
) : BaseUiState(isLoading, errorMessage) {

    constructor() : this(
        screenState = LoginState.Login(),
        user = User(
            email = "",
            pass = "",
            username = "",
            uid = "",
            photo = "",
            token = "",
            points = 0,
            car = Car(0.0, 0.0),
            createdAt = ""
        ),
        emailErrorMessage = null,
        passErrorMessage = null,
        isLogged = false,
        isSendEmailRecovered = false,
        isLoading = false,
        errorMessage = null,
    )
}

sealed class LoginState(
    @StringRes val buttonText: Int,
    @StringRes val accountText: Int,
    @StringRes val signText: Int
) {
    data class Login(
        @StringRes val button: Int = R.string.login_text_button,
        @StringRes val account: Int = R.string.login_text_no_account,
        @StringRes val sign: Int = R.string.login_text_sign_up
    ) : LoginState(button, account, sign)

    data class Register(
        @StringRes val button: Int = R.string.register_text_button,
        @StringRes val account: Int = R.string.login_text_yes_account,
        @StringRes val sign: Int = R.string.login_text_sign_in
    ) : LoginState(button, account, sign)
}