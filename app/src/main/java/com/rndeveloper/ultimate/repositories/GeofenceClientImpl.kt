package com.rndeveloper.ultimate.repositories

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.utils.Constants.RADIUS_IS_NEAR_SPOT
import com.rndeveloper.ultimate.utils.Constants.REQUEST_CODE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GeofenceClientImpl @Inject constructor(
    private val appContext: Context,
    private val geofencingClient: GeofencingClient,
    private val geofenceIntent: Intent,
    private val geofencePendingIntent: PendingIntent,
) : GeofenceClient {

    override fun startGeofence(spot: Spot): Flow<Void?> = callbackFlow {

        val bundle = Bundle()
//        bundle.putSerializable("spot", spot)

        geofenceIntent.putExtra("bundle", bundle)

        geofencePendingIntent.send(appContext, REQUEST_CODE, geofenceIntent)

        geofencingClient.removeGeofences(geofencePendingIntent).addOnSuccessListener {

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

//            geofencingClient.addGeofences(getGeofencingRequest(geofence), geofencePendingIntent)
//                .addOnSuccessListener {
//                    launch { send(it) }
//                }
//                .addOnFailureListener {
////                    launch { send(it) }
//
//                }
        }.addOnFailureListener { e ->
            if ((e.message != null)) {

            }
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
    fun startGeofence(spot: Spot): Flow<Void?>
}