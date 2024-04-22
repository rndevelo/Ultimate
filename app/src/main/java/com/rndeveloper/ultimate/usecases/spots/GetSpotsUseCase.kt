package com.rndeveloper.ultimate.usecases.spots

import android.content.Context
import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.extensions.sortItems
import com.rndeveloper.ultimate.model.Directions
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.repositories.ItemsRepository
import com.rndeveloper.ultimate.ui.screens.home.uistates.SpotsUiState
import com.rndeveloper.ultimate.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class GetSpotsUseCase @Inject constructor(
    private val repository: ItemsRepository,
) : BaseUseCase<Triple<String, Pair<Context, Directions>, Pair<Position, Position>>, Flow<SpotsUiState>>() {

    override suspend fun execute(parameters: Triple<String, Pair<Context, Directions>, Pair<Position, Position>>): Flow<SpotsUiState> =
        channelFlow {

            // Do login if fields are valid
            val (collectionRef, pair, positions) = parameters

            repository.getItems(collectionRef, pair.second)
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
//                        SUCCESS
                        onSuccess = { spots ->
                            trySend(
                                SpotsUiState().copy(
                                    items = spots.sortItems(pair.first, positions),
                                    isLoading = false
                                )
                            )
                        },
//                        FAILURE
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