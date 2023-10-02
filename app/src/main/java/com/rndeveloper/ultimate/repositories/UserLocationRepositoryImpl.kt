package com.rndeveloper.ultimate.repositories

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@SuppressLint("MissingPermission")
class UserLocationRepositoryImpl @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
) : UserLocationRepository {

    override suspend fun getLocationsRequest(): LatLng? = suspendCancellableCoroutine { cont ->
        fusedLocationClient.lastLocation.apply {
            if (isComplete) {
                if (isSuccessful) {
                    cont.resume(LatLng(result.latitude, result.longitude)) {}
                } else {
                    cont.resume(null) {}
                }
                return@suspendCancellableCoroutine
            }
            addOnSuccessListener {
                cont.resume(LatLng(it.latitude, it.longitude)) {}
            }
            addOnFailureListener {
                cont.resume(null) {}
            }
            addOnCanceledListener {
                cont.resume(null) {}
            }
        }
    }
}

interface UserLocationRepository {
    suspend fun getLocationsRequest(): LatLng?
}