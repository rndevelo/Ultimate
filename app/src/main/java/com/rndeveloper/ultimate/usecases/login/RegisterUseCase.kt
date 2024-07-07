package com.rndeveloper.ultimate.usecases.login

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.exceptions.LoginException
import com.rndeveloper.ultimate.extensions.isEmailValid
import com.rndeveloper.ultimate.extensions.isPasswordValid
import com.rndeveloper.ultimate.repositories.LoginRepository
import com.rndeveloper.ultimate.ui.screens.login.uistates.LoginSignState
import com.rndeveloper.ultimate.ui.screens.login.uistates.LoginUiState
import com.rndeveloper.ultimate.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: LoginRepository,
) : BaseUseCase<Pair<String, String>, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: Pair<String, String>): Flow<LoginUiState> =
        channelFlow {
            val (email, pass) = parameters

            // Loading
            send(
                LoginUiState().copy(
                    loginSignState = LoginSignState.SignUp(),
                    isLoading = true
                )
            )

            when {

                !email.isEmailValid() -> {
                    send(
                        LoginUiState().copy(
                            loginSignState = LoginSignState.SignIn(),
                            errorMessage = LoginException.EmailInvalidFormat(),
                            isLoading = false,
                            isRegistered = false,
                        )
                    )
                }
                !pass.isPasswordValid() -> {
                    send(
                        LoginUiState().copy(
                            loginSignState = LoginSignState.SignIn(),
                            errorMessage = LoginException.PasswordInvalidFormat(),
                            isLoading = false,
                            isRegistered = false,
                        )
                    )
                }

                else -> {
                    repository.register(email, pass)
                        .catch { exception ->
                            send(
                                LoginUiState().copy(
                                    loginSignState = LoginSignState.SignUp(),
                                    isRegistered = false,
                                    isLoading = false,
                                    errorMessage = CustomException.GenericException(
                                        exception.message ?: "Register Error"
                                    ),
                                )
                            )
                        }
                        .collectLatest { result ->
                            result.fold(
                                onSuccess = { authResult ->
                                    send(
                                        LoginUiState().copy(
                                            loginSignState = LoginSignState.SignUp(),
                                            isRegistered = true,
                                            isLoading = false
                                        )
                                    )
                                },
                                onFailure = { exception ->
                                    send(
                                        LoginUiState().copy(
                                            loginSignState = LoginSignState.SignUp(),
                                            isRegistered = false,
                                            isLoading = false,
                                            errorMessage = CustomException.GenericException(
                                                exception.message ?: "Register Failure"
                                            )
                                        )
                                    )
                                }
                            )
                        }
                }
            }
        }
}
