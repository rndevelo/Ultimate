package com.rndeveloper.ultimate.usecases.spots

import android.content.Context
import android.location.Location
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.extensions.sortItems
import com.rndeveloper.ultimate.model.Directions
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.repositories.SpotRepository
import com.rndeveloper.ultimate.ui.screens.home.uistates.SpotsUiState
import com.rndeveloper.ultimate.ui.theme.blue_place_icon
import com.rndeveloper.ultimate.ui.theme.green_place_icon
import com.rndeveloper.ultimate.ui.theme.red_place_icon
import com.rndeveloper.ultimate.ui.theme.yellow_place_icon
import com.rndeveloper.ultimate.usecases.BaseUseCase
import com.rndeveloper.ultimate.utils.Constants
import com.rndeveloper.ultimate.utils.Constants.SPOTS_RADIUS_TARGET
import com.rndeveloper.ultimate.utils.Utils.currentTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class GetSpotsUseCase @Inject constructor(
    private val repository: SpotRepository,
) : BaseUseCase<Triple<String, Pair<Context, Directions>, Pair<Position, Position>>, Flow<SpotsUiState>>() {

    override suspend fun execute(parameters: Triple<String, Pair<Context, Directions>, Pair<Position, Position>>): Flow<SpotsUiState> =
        channelFlow {

            // Do login if fields are valid
            val (collectionRef, pair, positions) = parameters

            repository.getSpots(collectionRef, pair.second)
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
                                    spots = spots.sortItems(pair.first,positions),
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