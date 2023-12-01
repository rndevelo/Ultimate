package com.rndeveloper.ultimate.usecases.user

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.repositories.UserRepository
import com.rndeveloper.ultimate.ui.screens.home.UserUiState
import com.rndeveloper.ultimate.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val repository: UserRepository,
) : BaseUseCase<Unit, Flow<UserUiState>>() {

    override suspend fun execute(parameters: Unit): Flow<UserUiState> =
        channelFlow {

            // Loading
            send(UserUiState().copy(isLoading = true))

            repository.getUserData()
                .catch { exception ->
                    send(
                        UserUiState().copy(
                            isLoading = false,
                            errorMessage = CustomException.GenericException(
                                exception.message ?: "Register Error"
                            ),
                        )
                    )
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { userData ->
                            send(
                                UserUiState().copy(
                                    user = userData,
                                    isLoading = false
                                )
                            )
                        },
                        onFailure = { exception ->
                            send(
                                UserUiState().copy(
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