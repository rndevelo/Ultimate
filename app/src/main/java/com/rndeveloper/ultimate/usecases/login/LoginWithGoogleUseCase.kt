package com.rndeveloper.ultimate.usecases.login

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.repositories.LoginRepository
import com.rndeveloper.ultimate.ui.screens.login.LoginState
import com.rndeveloper.ultimate.ui.screens.login.LoginUiState
import com.rndeveloper.ultimate.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val repository: LoginRepository,
) : BaseUseCase<String, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: String): Flow<LoginUiState> = channelFlow {
        send(LoginUiState().copy(isLoading = true))

        repository.loginWithGoogle(parameters)
            .catch { exception ->
                send(
                    LoginUiState().copy(
                        isLoading = false,
                        isLogged = false,
                        errorMessage = CustomException.GenericException(
                            exception.message ?: "Google Login Error"
                        ),
                    )
                )
            }
            .collect { result ->
                result.fold(
                    onSuccess = { authResult ->
                        send(
                            LoginUiState().copy(
                                isLogged = true,
                                isLoading = false,
                            )
                        )
                    },
                    onFailure = { exception ->
                        send(
                            LoginUiState().copy(
                                screenState = LoginState.Login(),
                                isLogged = false,
                                isLoading = false,
                                errorMessage = CustomException.GenericException(
                                    exception.message ?: "Google Login Failure"
                                )
                            )
                        )
                    }
                )
            }
    }
}
