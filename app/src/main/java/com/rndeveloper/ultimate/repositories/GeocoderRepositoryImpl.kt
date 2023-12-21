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
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                ?.let { addressList ->
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
        }
    }
}


interface GeocoderRepository {
    fun getAddressList(latLng: LatLng, callback: (Directions) -> Unit)
}