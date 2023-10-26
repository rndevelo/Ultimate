package com.rndeveloper.ultimate.usecases.user

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.repositories.UserRepository
import com.rndeveloper.ultimate.ui.screens.home.HomeUiState
import com.rndeveloper.ultimate.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val repository: UserRepository,
) : BaseUseCase<Unit, Flow<HomeUiState>>() {

    override suspend fun execute(parameters: Unit): Flow<HomeUiState> =
        channelFlow {

            // Loading
            send(HomeUiState().copy(isLoading = true))

            repository.getUserData()
                .catch { exception ->
                    send(
                        HomeUiState().copy(
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
                                HomeUiState().copy(
                                    user = userData,
                                    isLoading = false
                                )
                            )
                        },
                        onFailure = { exception ->
                            send(
                                HomeUiState().copy(
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