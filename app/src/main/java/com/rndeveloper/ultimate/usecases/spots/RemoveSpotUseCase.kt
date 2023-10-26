package com.rndeveloper.ultimate.usecases.spots

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.repositories.SpotRepository
import com.rndeveloper.ultimate.ui.screens.home.HomeUiState
import com.rndeveloper.ultimate.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class RemoveSpotUseCase @Inject constructor(
    private val repository: SpotRepository,
) : BaseUseCase<Spot, Flow<HomeUiState>>() {

    override suspend fun execute(parameters: Spot): Flow<HomeUiState> =
        channelFlow {

            // TODO: Validate fields: email restriction and empty fields validations

            // Loading
            send(HomeUiState().copy(isLoading = true))

            // Do login if fields are valid

            repository.removeSpot(parameters)
                .catch { exception ->
                    send(
                        HomeUiState().copy(
                            isLoading = false,
                            errorMessage = CustomException.GenericException(
                                exception.message ?: "Error to remove data"
                            ),
                        )
                    )
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = {
                            trySend(HomeUiState().copy(isLoading = false))
                        },
                        onFailure = { exception ->
                            send(
                                HomeUiState().copy(
                                    isLoading = false,
                                    errorMessage = CustomException.GenericException(
                                        exception.message ?: "Exception, didn't can remove data"
                                    )
                                )
                            )
                        }
                    )
                }
        }
}