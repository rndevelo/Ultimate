package com.rndeveloper.ultimate.repositories

import android.location.Geocoder
import android.os.Build
import com.google.android.gms.maps.model.LatLng
import com.rndeveloper.ultimate.model.Directions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GeocoderRepositoryImpl @Inject constructor(
    private val geocoder: Geocoder
) : GeocoderRepository {

    override fun getAddressList(latLng: LatLng): Flow<Directions> = callbackFlow {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    1
                ) { addressList ->
                    if (addressList.isNotEmpty()) {
                        Directions(
                            addressLine = addressList.first().getAddressLine(0) ?: "",
                            locality = addressList.first().locality ?: "",
                            area = addressList.first().subAdminArea ?: "",
                            country = addressList.first().countryName ?: ""
                        ).let { directions ->
                            trySend(directions)
                        }
                    } else {
//                    trySend(Result.failure(e.fillInStackTrace()))
                    }
                }
            } else {
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)?.let { addressList ->
                    if (addressList.isNotEmpty()) {
                        Directions(
                            addressLine = addressList.firstOrNull()?.getAddressLine(0) ?: "",
                            locality = addressList.firstOrNull()?.locality ?: "",
                            area = addressList.firstOrNull()?.subAdminArea ?: "",
                            country = addressList.firstOrNull()?.countryName ?: ""
                        ).let { directions ->
                            trySend(directions)
                        }
                    } else {
//                    trySend(Result.failure(e.fillInStackTrace()))
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        awaitClose {
            channel.close()
        }
    }
}


interface GeocoderRepository {
    fun getAddressList(latLng: LatLng): Flow<Directions>
}