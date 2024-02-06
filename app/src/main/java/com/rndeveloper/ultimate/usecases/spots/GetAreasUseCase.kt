package com.rndeveloper.ultimate.usecases.spots

import android.content.Context
import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.extensions.sortItems
import com.rndeveloper.ultimate.model.Directions
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.repositories.SpotRepository
import com.rndeveloper.ultimate.ui.screens.home.uistates.AreasUiState
import com.rndeveloper.ultimate.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class GetAreasUseCase @Inject constructor(
    private val repository: SpotRepository,
) : BaseUseCase<Triple<String, Pair<Context, Directions>, Pair<Position, Position>>, Flow<AreasUiState>>() {

    override suspend fun execute(parameters: Triple<String, Pair<Context, Directions>, Pair<Position, Position>>): Flow<AreasUiState> =
        channelFlow {

            // Do login if fields are valid
            val (collectionRef, pair, positions) = parameters

            repository.getSpots(collectionRef, pair.second)
                .catch { exception ->
                    send(
                        AreasUiState().copy(
                            isLoading = false,
                            errorMessage = CustomException.GenericException(
                                exception.message ?: "Error to get data"
                            ),
                        )
                    )
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { spots ->
//                            TODO : METER TODA ESTA FUNCIÓN EN UNA LAMBDA
                            trySend(
                                AreasUiState().copy(
                                    areas = spots.sortItems(pair.first, positions),
                                    isLoading = false
                                )
                            )
                        },
                        onFailure = { exception ->
                            send(
                                AreasUiState().copy(
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