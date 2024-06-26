package com.rndeveloper.ultimate.usecases.spots

import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.repositories.ItemsRepository
import com.rndeveloper.ultimate.ui.screens.home.uistates.SpotsUiState
import com.rndeveloper.ultimate.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class RemoveSpotUseCase @Inject constructor(
    private val repository: ItemsRepository,
) : BaseUseCase<Pair<Triple<String, String, String>, Pair<String, String>>,  Flow<SpotsUiState>>() {

    override suspend fun execute(parameters: Pair<Triple<String, String, String>, Pair<String, String>>): Flow<SpotsUiState> =
        channelFlow {

            // TODO: Validate fields: email restriction and empty fields validations

            // Do login if fields are valid

            repository.removeSpot(parameters).catch { exception ->
                send(
                    SpotsUiState().copy(
                        isLoading = false,
                        errorMessage = CustomException.GenericException(
                            exception.message ?: "Error to remove data"
                        ),
                    )
                )
            }.collectLatest { result ->
                result.fold(
                    onSuccess = {
                        trySend(SpotsUiState().copy(isLoading = false))
                    },
                    onFailure = { exception ->
                        send(
                            SpotsUiState().copy(
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