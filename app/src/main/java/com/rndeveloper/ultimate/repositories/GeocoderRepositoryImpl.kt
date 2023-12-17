//package com.rndeveloper.ultimate.repositories
//
//import android.location.Address
//import android.location.Geocoder
//import android.os.Build
//import android.util.Log
//import com.google.android.gms.maps.model.LatLng
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.callbackFlow
//import javax.inject.Inject
//
//class GeocoderRepositoryImpl @Inject constructor(
//    private val geocoder: Geocoder
//) : GeocoderRepository {
//
//    override fun getAddressList(latLng: LatLng): Flow<Result<List<Address>>> = callbackFlow {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            geocoder.getFromLocation(
//                latLng.latitude,
//                latLng.longitude,
//                1
//            ) { addressList ->
//                if (addressList.isNotEmpty()) {
//                    Log.d("ADDRESSLIST", "repo: ${addressList.first().getAddressLine(0)}")
//                    trySend(Result.success(addressList))
//                } else {
////                    trySend(Result.failure(e.fillInStackTrace()))
//                }
//            }
//        } else {
//            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
//                ?.let { addressList ->
//                    if (addressList.isNotEmpty()) {
//                        trySend(Result.success(addressList))
//                    } else {
////                    trySend(Result.failure(e.fillInStackTrace()))
//                    }
//                }
//        }
//    }
//}
//
//
//interface GeocoderRepository {
//    fun getAddressList(latLng: LatLng): Flow<Result<List<Address>>>
//}