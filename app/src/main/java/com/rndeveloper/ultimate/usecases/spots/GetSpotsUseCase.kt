package com.rndeveloper.ultimate.usecases.spots

import android.location.Location
import android.util.Log
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.model.Directions
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.repositories.SpotRepository
import com.rndeveloper.ultimate.ui.screens.home.SpotsUiState
import com.rndeveloper.ultimate.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class GetSpotsUseCase @Inject constructor(
    private val repository: SpotRepository,
) : BaseUseCase<Triple<Position, Position, Directions>, Flow<SpotsUiState>>() {

    override suspend fun execute(parameters: Triple<Position, Position, Directions>): Flow<SpotsUiState> =
        channelFlow {

            // TODO: Validate fields: email restriction and empty fields validations

            // Loading
            send(SpotsUiState().copy(isLoading = true))

            // Do login if fields are valid
            val (camLatLng, locLatLng, directions) = parameters

            repository.getSpots(directions)
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
                            Log.d("BUCLEMIO", "GetSpotsUseCase: spots ${spots.size}")

//                            TODO : METER TODA ESTA FUNCIÓN EN UNA LAMBDA
                            spots.filter { spot ->
                                GeoFireUtils.getDistanceBetween(
                                    GeoLocation(spot.position.lat, spot.position.lng),
                                    GeoLocation(camLatLng.lat, camLatLng.lng)
                                ) <= 1000.0
                            }.sortedWith { d1, d2 ->
                                val currentLoc = Location("currentLoc")
                                currentLoc.latitude = locLatLng.lat
                                currentLoc.longitude = locLatLng.lng
//                                Log.d("BUCLEMIO", "GetSpotsUseCase: sortedWith ")

                                val targets1 = Location("target")
                                targets1.latitude = d1.position.lat
                                targets1.longitude = d1.position.lng

                                val target2 = Location("location")
                                target2.latitude = d2.position.lat
                                target2.longitude = d2.position.lng

                                val distanceOne = currentLoc.distanceTo(targets1)
                                val distanceTwo = currentLoc.distanceTo(target2)

                                distanceOne.compareTo(distanceTwo)
                            }.take(12).let { sortedSpots ->
                                Log.d("BUCLEMIO", "GetSpotsUseCase: sortedSpots ${sortedSpots.size}")
                                trySend(
                                    SpotsUiState().copy(
                                        spots = sortedSpots,
                                        selectedSpot = sortedSpots.firstOrNull(),
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