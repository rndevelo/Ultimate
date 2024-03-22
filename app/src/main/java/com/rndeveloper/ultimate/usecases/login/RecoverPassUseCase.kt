package com.rndeveloper.ultimate.usecases.login

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.exceptions.LoginException
import com.rndeveloper.ultimate.extensions.isEmailValid
import com.rndeveloper.ultimate.repositories.LoginRepository
import com.rndeveloper.ultimate.ui.screens.login.LoginUiState
import com.rndeveloper.ultimate.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class RecoverPassUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) : BaseUseCase<String, Flow<LoginUiState>>() {

    override suspend fun execute(parameters: String): Flow<LoginUiState> = channelFlow {
        send(
            LoginUiState().copy(isLoading = false,)
        )

        if (!parameters.isEmailValid()) {
            send(
                LoginUiState().copy(
                    isLoading = false,
                    errorMessage = LoginException.EmailInvalidFormat()
                )
            )
        } else {
            send(LoginUiState().copy(isLoading = true))
            loginRepository.recoverPassword(parameters).catch { exception ->
                send(
                    LoginUiState().copy(
                        isLoading = false,
                        errorMessage = CustomException.GenericException(
                            exception.message ?: "Recover Pass Email Error"
                        )
                    )
                )
            }.collectLatest { result ->
                result.fold(
                    onSuccess = { firebaseResponse ->
                        send(
                            LoginUiState().copy(isLoading = false,)
                        )
                    },
                    onFailure = {
                        send(
                            LoginUiState().copy(
                                isLoading = false,
                                errorMessage = CustomException.GenericException(
                                    it.message ?: "Recover Pass Email Error"
                                )
                            )
                        )
                    }
                )
            }
        }
    }
}
