package com.rndeveloper.ultimate.repositories

import android.location.Geocoder
import android.os.Build
import com.google.android.gms.maps.model.LatLng
import com.rndeveloper.ultimate.model.Directions
import javax.inject.Inject

class GeocoderRepositoryImpl @Inject constructor(
    private val geocoder: Geocoder
) : GeocoderRepository {

    override fun getAddressList(latLng: LatLng, callback: (Directions) -> Unit) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    1
                ) { addressList ->
                    if (addressList.isNotEmpty()) {
                        Directions(
                            addressLine = addressList.first().getAddressLine(0),
                            locality = addressList.first().locality,
                            area = addressList.first().subAdminArea,
                            country = addressList.first().countryName
                        ).let { directions ->
                            callback(directions)
                        }
                    } else {
//                    trySend(Result.failure(e.fillInStackTrace()))
                    }
                }
            } else {
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)?.let { addressList ->
                    if (addressList.isNotEmpty()) {
                        Directions(
                            addressLine = addressList.firstOrNull()?.getAddressLine(0)?: "",
                            locality = addressList.firstOrNull()?.locality ?: "" ,
                            area = addressList.firstOrNull()?.subAdminArea?: "",
                            country = addressList.firstOrNull()?.countryName?: ""
                        ).let { directions ->
                            callback(directions)
                        }
                    } else {
//                    trySend(Result.failure(e.fillInStackTrace()))
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}


interface GeocoderRepository {
    fun getAddressList(latLng: LatLng, callback: (Directions) -> Unit)
}


/*

class GeocoderRepositoryImpl @Inject constructor(
    private val geocoder: Geocoder
) : GeocoderRepository {

    override fun getAddressList(latLng: LatLng): Flow<Directions> = callbackFlow {

        var geocoderListener = Geocoder.GeocodeListener { addressList ->
            if (addressList.isNotEmpty()) {
                Directions(
                    addressLine = addressList.first().getAddressLine(0),
                    locality = addressList.first().locality,
                    area = addressList.first().subAdminArea,
                    country = addressList.first().countryName
                ).let { directions ->
                    launch { trySend(directions) }
                }
            } else {
                launch { trySend(Directions()) }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1,
                geocoderListener
            )
        } else {
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)?.let { addressList ->
                try {

                    if (addressList.isNotEmpty()) {
                        Directions(
                            addressLine = addressList.first().getAddressLine(0),
                            locality = addressList.first().locality,
                            area = addressList.first().subAdminArea,
                            country = addressList.first().countryName
                        ).let { directions ->
                            launch { trySend(directions) }
                        }
                    } else {
                        launch { trySend(Directions()) }
                    }
                } catch (ex: Exception) {
                    Timber.e(ex)
                    launch { trySend(Directions()) }
                }
            }

            awaitClose {
                channel.close()
            }
        }
    }
}


interface GeocoderRepository {
    fun getAddressList(latLng: LatLng): Flow<Directions>
}

 */