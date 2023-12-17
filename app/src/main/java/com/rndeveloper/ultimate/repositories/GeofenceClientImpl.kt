package com.rndeveloper.ultimate.repositories

import android.content.Context
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.rndeveloper.ultimate.model.Spot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GeofenceClientImpl @Inject constructor(
    private val appContext: Context,
    private val geofencingClient: GeofencingClient,
) : GeofenceClient {

    override fun startGeofence(spot: Spot): Flow<Void?> = callbackFlow {

//        val bundle = Bundle()
////        bundle.putSerializable("spot", spot)
//
//        //    Intent
//        val geofenceIntent = Intent(appContext, GeofenceReceiver::class.java)
//        geofenceIntent.putExtra("bundle", bundle)
//
//        val geofencePendingIntent = PendingIntent.getBroadcast(
//            appContext,
//            REQUEST_CODE,
//            Intent(appContext, GeofenceReceiver::class.java),
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        geofencePendingIntent.send(appContext, REQUEST_CODE, geofenceIntent)
//
//        geofencingClient.removeGeofences(geofencePendingIntent).addOnSuccessListener {
//
//            val geofence = Geofence.Builder()
//                .setRequestId(spot.tag)
////                .setCircularRegion(
////                    spot.position.lat,
////                    spot.position.lng,
////                    RADIUS_IS_NEAR_SPOT
////                )
//                .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
//                .build()
//
////            geofencingClient.addGeofences(getGeofencingRequest(geofence), geofencePendingIntent)
////                .addOnSuccessListener {
////                    launch { send(it) }
////                }
////                .addOnFailureListener {
//////                    launch { send(it) }
////
////                }
//        }.addOnFailureListener { e ->
//            if ((e.message != null)) {
//
//            }
//        }
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