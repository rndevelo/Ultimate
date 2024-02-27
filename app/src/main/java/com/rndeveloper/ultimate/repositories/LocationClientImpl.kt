package com.rndeveloper.ultimate.repositories

import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.rndeveloper.ultimate.model.Position
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationClientImpl @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationsRequest(): Flow<Position> = callbackFlow {

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.locations.lastOrNull()?.let { location ->
                    Log.d("LocationUpdates", "lastOrNull")
                    launch { send(Position(location.latitude, location.longitude)) }
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.myLooper()
        )

        awaitClose {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    @SuppressLint("MissingPermission")
    override fun getLastLocation(callback: (Position) -> Unit) {

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            callback(Position(location.latitude, location.longitude))
        }
    }
}

interface LocationClient {
    fun getLocationsRequest(): Flow<Position>
    fun getLastLocation(callback: (Position) -> Unit)
}