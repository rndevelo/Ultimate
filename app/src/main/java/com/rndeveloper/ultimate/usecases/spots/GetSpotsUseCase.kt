package com.rndeveloper.ultimate.usecases.spots

import android.location.Location
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.Directions
import com.rndeveloper.ultimate.model.Position
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
) : BaseUseCase<Triple<String, Directions, Pair<Position, Position>>, Flow<SpotsUiState>>() {

    override suspend fun execute(parameters: Triple<String, Directions, Pair<Position, Position>>): Flow<SpotsUiState> =
        channelFlow {

            // Do login if fields are valid
            val (collectionRef, directions, positions) = parameters

            repository.getSpots(collectionRef, directions)
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
                        onSuccess = { spots ->
//                            TODO : METER TODA ESTA FUNCIÓN EN UNA LAMBDA

                            spots.filter { spot ->
                                GeoFireUtils.getDistanceBetween(
                                    GeoLocation(spot.position.lat, spot.position.lng),
                                    GeoLocation(positions.first.lat, positions.first.lng)
                                ) <= SPOTS_RADIUS_TARGET
                            }.sortedWith { d1, d2 ->
                                val currentLoc = Location("currentLoc")
                                currentLoc.latitude = positions.second.lat
                                currentLoc.longitude = positions.second.lng

                                val targets1 = Location("target")
                                targets1.latitude = d1.position.lat
                                targets1.longitude = d1.position.lng

                                val targets2 = Location("location")
                                targets2.latitude = d2.position.lat
                                targets2.longitude = d2.position.lng

                                val distanceOne = currentLoc.distanceTo(targets1)
                                val distanceTwo = currentLoc.distanceTo(targets2)

                                distanceOne.compareTo(distanceTwo)
                            }.map { spot ->
                                val distance = FloatArray(2)
                                Location.distanceBetween(
                                    spot.position.lat, spot.position.lng,
                                    positions.second.lat, positions.second.lng, distance
                                )
//                                val dec = DecimalFormat("#.##").format(distance[0])

                                val timeResult = currentTime() - spot.timestamp

                                val color = when {
                                    timeResult < 0 -> blue_place_icon
                                    timeResult < Constants.MINUTE * 10 -> green_place_icon
                                    timeResult < Constants.MINUTE * 20 -> yellow_place_icon
                                    else -> red_place_icon
                                }

                                spot.copy(distance = "${distance[0].toInt()}m", color = color)
                            }.take(6).let { sortedSpots ->
                                trySend(
                                    SpotsUiState().copy(
                                        spots = sortedSpots,
                                        isLoading = false
                                    )
                                )
                            }
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