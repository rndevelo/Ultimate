package com.rndeveloper.ultimate.usecases.spots

import android.location.Location
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.rndeveloper.ultimate.exceptions.CustomException
import com.rndeveloper.ultimate.repositories.SpotRepository
import com.rndeveloper.ultimate.ui.screens.home.HomeUiState
import com.rndeveloper.ultimate.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class GetSpotsUseCase @Inject constructor(
    private val repository: SpotRepository,
) : BaseUseCase<Pair<LatLng, HomeUiState>, Flow<HomeUiState>>() {

    override suspend fun execute(parameters: Pair<LatLng, HomeUiState>): Flow<HomeUiState> =
        channelFlow {

            // TODO: Validate fields: email restriction and empty fields validations

            // Loading
            send(HomeUiState().copy(isLoading = true))

            // Do login if fields are valid
            val (camLatLng, uiHomeState) = parameters

            repository.getSpots(camLatLng)
                .catch { exception ->
                    send(
                        HomeUiState().copy(
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
                                    GeoLocation(camLatLng.latitude, camLatLng.longitude)
                                ) <= 1000.0
                            }.sortedWith { d1, d2 ->
                                val currentLoc = Location("currentLoc")

                                uiHomeState.loc?.let {
                                    currentLoc.latitude = uiHomeState.loc.latitude
                                    currentLoc.longitude = uiHomeState.loc.longitude
                                }

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
                                if (uiHomeState.loc != null) {
                                    trySend(
                                        uiHomeState.copy(
                                            spots = sortedSpots,
                                            selectedSpot = sortedSpots.firstOrNull(),
                                            isLoading = false
                                        )
                                    )
                                }else{
                                    trySend(
                                        HomeUiState().copy(
                                            spots = sortedSpots,
                                            isLoading = false
                                        )
                                    )
                                }
                            }
                        },
                        onFailure = { exception ->
                            send(
                                HomeUiState().copy(
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