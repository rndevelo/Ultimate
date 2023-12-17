package com.rndeveloper.ultimate.usecases

//class GetDirectionsUseCase @Inject constructor(
//    private val repository: GeocoderRepository,
//) : BaseUseCase<LatLng, DirectionsUiState>() {
//
//    override suspend fun execute(parameters: LatLng): DirectionsUiState {
//
//        repository.getAddressList(parameters)
//            .catch { exception ->
//                Log.d("ADDRESSLIST", "Error to get data ${exception.message}")
//
//                DirectionsUiState().copy(
//                    isLoading = false,
//                    errorMessage = CustomException.GenericException(
//                        exception.message ?: "Error to get data"
//                    ),
//                )
//            }
//            .collectLatest { result ->
//                result.fold(
//                    onSuccess = { address ->
//                        Log.d("ADDRESSLIST", "address: ${address.first().getAddressLine(0)}")
//                        Directions(
//                            addressLine = address.first().getAddressLine(0),
//                            locality = address.first().locality,
//                            area = address.first().subAdminArea,
//                            country = address.first().countryName
//                        ).let { directions ->
//                            Log.d("ADDRESSLIST", "directions: $directions")
//
//                            DirectionsUiState().copy(
//                                directions = directions,
//                                isLoading = false
//                            )
//                        }
//                    },
//                    onFailure = { exception ->
//                        Log.d("ADDRESSLIST", "exception  Error to get data")
//                        DirectionsUiState().copy(
//                            isLoading = false,
//                            errorMessage = CustomException.GenericException(
//                                exception.message ?: "Exception, didn't can get data"
//                            )
//                        )
//                    }
//                )
//            }
//
//    }
//}