package com.rndeveloper.ultimate.usecases.login

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.repositories.LoginRepository
import com.rndeveloper.ultimate.ui.screens.login.LoginState
import com.rndeveloper.ultimate.ui.screens.login.LoginUiState
import com.rndeveloper.ultimate.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class CheckUserLoggedInUseCase @Inject constructor(
    private val repository: LoginRepository,
) : BaseUseCase<Unit, Flow<LoginUiState>>() {
    override suspend fun execute(parameters: Unit): Flow<LoginUiState> = channelFlow {
        repository.getUserAuthentication().catch { exception ->
            send(
                LoginUiState().copy(
                    isLogged = false,
                    errorMessage = CustomException.GenericException(
                        exception.message ?: "Google Login Error"
                    ),
                )
            )
        }.collectLatest { result ->
            result.fold(
                onSuccess = { logged ->
                    send(
                        LoginUiState().copy(
                            screenState = LoginState.Login(),
                            isLogged = logged
                        )
                    )
                },
                onFailure = {
                    send(
                        LoginUiState().copy(
                            screenState = LoginState.Login(),
                            isLogged = false,
                            errorMessage = CustomException.GenericException(
                                it.message ?: "Google Login Error"
                            ),
                        )
                    )
                }
            )
        }
    }
}
