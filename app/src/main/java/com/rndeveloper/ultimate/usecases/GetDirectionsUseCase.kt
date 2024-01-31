package com.rndeveloper.ultimate.usecases

import com.google.android.gms.maps.model.LatLng
import com.rndeveloper.ultimate.repositories.GeocoderRepository
import com.rndeveloper.ultimate.ui.screens.home.uistates.DirectionsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetDirectionsUseCase @Inject constructor(
    private val repository: GeocoderRepository,
) : BaseUseCase<LatLng, Flow<DirectionsUiState>>() {

    override suspend fun execute(parameters: LatLng): Flow<DirectionsUiState> =
        channelFlow {

            send(DirectionsUiState().copy(isLoading = true))


//            repository.getAddressList(parameters)
//                .catch { exception ->
//                    Log.d("ADDRESSLIST", "Error to get data ${exception.message}")
//
//                    send(
//                        DirectionsUiState().copy(
//                            isLoading = false,
//                            errorMessage = CustomException.GenericException(
//                                exception.message ?: "Error to get data"
//                            ),
//                        )
//                    )
//                }
//                .collectLatest { result ->
//                    result.fold(
//                        onSuccess = { address ->
//                            Log.d("ADDRESSLIST", "address: ${address.first().getAddressLine(0)}")
//
//                            Directions(
//                                addressLine = address.first().getAddressLine(0),
//                                locality = address.first().locality,
//                                area = address.first().subAdminArea,
//                                country = address.first().countryName
//                            ).let { directions ->
//                                Log.d("ADDRESSLIST", "directions: $directions")
//
//                                send(
//                                    DirectionsUiState().copy(
//                                        directions = directions,
//                                        isLoading = false
//                                    )
//                                )
//                            }
//                        },
//                        onFailure = { exception ->
//                            Log.d("ADDRESSLIST", "exception  Error to get data")
//                            send(
//                                DirectionsUiState().copy(
//                                    isLoading = false,
//                                    errorMessage = CustomException.GenericException(
//                                        exception.message ?: "Exception, didn't can get data"
//                                    )
//                                )
//                            )
//                        }
//                    )
//                }
        }
}