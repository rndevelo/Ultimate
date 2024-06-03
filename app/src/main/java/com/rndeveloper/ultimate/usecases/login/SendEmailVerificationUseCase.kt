package com.rndeveloper.ultimate.usecases.login

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.exceptions.LoginException
import com.rndeveloper.ultimate.extensions.isEmailValid
import com.rndeveloper.ultimate.extensions.isPasswordValid
import com.rndeveloper.ultimate.repositories.LoginRepository
import com.rndeveloper.ultimate.ui.screens.login.uistates.LoginState
import com.rndeveloper.ultimate.ui.screens.login.uistates.LoginUiState
import com.rndeveloper.ultimate.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class SendEmailVerificationUseCase @Inject constructor(
    private val repository: LoginRepository,
) : BaseUseCase<Unit, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: Unit): Flow<LoginUiState> =
        channelFlow {

            // Loading
            send(
                LoginUiState().copy(
                    screenState = LoginState.Login(),
                    isLoading = true,
                )
            )

            repository.sendVerificationEmail()
                .catch { exception ->
                    send(
                        LoginUiState().copy(
                            screenState = LoginState.Login(),
                            isLoading = false,
                            isEmailSent = false,
                            errorMessage = CustomException.GenericException(
                                exception.message ?: "Login Error"
                            ),
                        )
                    )
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { _ ->
                            send(
                                LoginUiState().copy(
                                    screenState = LoginState.Login(),
                                    isLoading = false,
                                    isRegistered = false,
                                    isEmailSent = true,
                                )
                            )
                        },
                        onFailure = { exception ->
                            send(
                                LoginUiState().copy(
                                    screenState = LoginState.Login(),
                                    isLoading = false,
                                    isEmailSent = false,
                                    errorMessage = CustomException.GenericException(
                                        exception.message ?: "Login Failure"
                                    )
                                )
                            )
                        },
                    )
                }
        }
}
