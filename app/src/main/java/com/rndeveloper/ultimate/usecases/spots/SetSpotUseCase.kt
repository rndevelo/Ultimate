package com.rndeveloper.ultimate.usecases.spots

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.Item
import com.rndeveloper.ultimate.repositories.ItemsRepository
import com.rndeveloper.ultimate.ui.screens.home.uistates.SpotsUiState
import com.rndeveloper.ultimate.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class SetSpotUseCase @Inject constructor(
    private val repository: ItemsRepository,
) : BaseUseCase<Pair<String, Item>, Flow<SpotsUiState>>() {

    override suspend fun execute(parameters: Pair<String, Item>): Flow<SpotsUiState> =
        channelFlow {

            send(SpotsUiState().copy(isLoading = true))

            repository.setSpot(parameters)
                .catch { exception ->
                    send(
                        SpotsUiState().copy(
                            isLoading = false,
                            errorMessage = CustomException.GenericException(
                                exception.message ?: "Error to get data"
                            ),
                        )
                    )
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = {
                            trySend(SpotsUiState().copy(isLoading = false))
                        },
                        onFailure = { exception ->
                            send(
                                SpotsUiState().copy(
                                    isLoading = false,
                                    errorMessage = CustomException.GenericException(
                                        exception.message ?: "Exception, didn't can get data"
                                    )
                                )
                            )
                        }
                    )
                }
        }
}