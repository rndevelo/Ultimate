package com.rndeveloper.ultimate.repositories

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.rndeveloper.ultimate.extensions.fixApi31
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.receivers.GeofenceReceiver
import com.rndeveloper.ultimate.utils.Constants.RADIUS_IS_NEAR_SPOT
import com.rndeveloper.ultimate.utils.Constants.REQUEST_CODE
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GeofenceClientImpl @Inject constructor(
    private val appContext: Context,
    private val geofencingClient: GeofencingClient,
) : GeofenceClient {

    @SuppressLint("MissingPermission")
    override fun startGeofence(spot: Spot): Flow<Result<Boolean>> = callbackFlow {

        val geofenceIntent = Intent(appContext, GeofenceReceiver::class.java)
        geofenceIntent.putExtra("country", spot.directions.country)
        geofenceIntent.putExtra("area", spot.directions.area)
        geofenceIntent.putExtra("tag", spot.tag)
        geofenceIntent.putExtra("uid", spot.user.uid)
        geofenceIntent.putExtra("token", spot.user.token)

        val geofencePendingIntent = PendingIntent.getBroadcast(
            appContext,
            REQUEST_CODE,
            Intent(appContext, GeofenceReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT.fixApi31()
        )

        val geofence = Geofence.Builder()
            .setRequestId(spot.tag)
            .setCircularRegion(
                spot.position.lat,
                spot.position.lng,
                RADIUS_IS_NEAR_SPOT
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        geofencePendingIntent.send(appContext, REQUEST_CODE, geofenceIntent)

        geofencingClient.removeGeofences(geofencePendingIntent).addOnSuccessListener {

            geofencingClient.addGeofences(getGeofencingRequest(geofence), geofencePendingIntent)
                .addOnSuccessListener {
                    trySend(Result.success(true))
                }
                .addOnFailureListener {
                    trySend(Result.failure(it.fillInStackTrace()))
                }
        }.addOnFailureListener { e ->
            if ((e.message != null)) {
                trySend(Result.failure(e.fillInStackTrace()))
            }
        }
        awaitClose {
            geofencingClient.removeGeofences(geofencePendingIntent)
        }
    }

    private fun getGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(geofence)
        }.build()
    }
}

interface GeofenceClient {
    fun startGeofence(spot: Spot): Flow<Result<Boolean>>
}