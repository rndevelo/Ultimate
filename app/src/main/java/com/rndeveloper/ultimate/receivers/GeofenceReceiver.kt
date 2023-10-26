package com.rndeveloper.ultimate.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.CallSuper
import com.google.android.gms.location.GeofenceStatusCodes
import dagger.hilt.android.AndroidEntryPoint


abstract class HiltGeofenceReceiver : BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context, intent: Intent) {
    }
}

@AndroidEntryPoint
class GeofenceReceiver : HiltGeofenceReceiver() {

//    @Inject
//    lateinit var spotUseCases: SpotUseCases
//
//    @OptIn(DelicateCoroutinesApi::class)
//    @Suppress("DEPRECATION")
//    override fun onReceive(context: Context, intent: Intent) {
//        super.onReceive(context, intent) // <-- it's the trick
//
//        val points = intent.extras!!.getInt("points")
//        val bundle = intent.getBundleExtra("bundle")
//        if (bundle != null) {
//            val newItem: NewSpot? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                bundle.getSerializable("newItem", NewSpot::class.java)
//            } else {
//                bundle.getSerializable("newItem") as NewSpot?
//            }
//
//            val iUser: User? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                bundle.getSerializable("iUser", User::class.java)
//            } else {
//                bundle.getSerializable("iUser") as User?
//            }
//
//            Log.d(TAG, "onReceive: points$points, newItem$newItem, iUser$iUser")
//
//            GlobalScope.launch(Dispatchers.IO) {
//                if (newItem != null) {
//                    if (iUser != null) {
//                        spotUseCases.deleteSpot(
//                            points,
//                            newItem,
//                            iUser
//                        ).collect()
//                    }
//                }
//            }
//
//            Log.d(TAG, "receiver")
//        }
//
//        val geofencingEvent = GeofencingEvent.fromIntent(intent)
//        if (geofencingEvent != null) {
//            if (geofencingEvent.hasError()) {
//                val errorMessage = GeofenceStatusCodes
//                    .getStatusCodeString(geofencingEvent.errorCode)
//                Log.e("New Receiver", errorMessage)
//                return
//            }
//        }
//        // Get the transition type.
//        val geofenceTransition = geofencingEvent?.geofenceTransition
//        // Get the geofences that were triggered. A single event can trigger
//        // multiple geofences.
//        val triggeringGeofences = geofencingEvent?.triggeringGeofences
//
//        // Test that the reported transition was of interest.
//        when (geofenceTransition) {
//            Geofence.GEOFENCE_TRANSITION_ENTER -> Toast.makeText(
//                context,
//                "ENTER",
//                Toast.LENGTH_SHORT
//            ).show()
//
//            Geofence.GEOFENCE_TRANSITION_DWELL -> Toast.makeText(
//                context,
//                "DWELL",
//                Toast.LENGTH_SHORT
//            ).show()
//
//            Geofence.GEOFENCE_TRANSITION_EXIT -> Toast.makeText(context, "EXIT", Toast.LENGTH_SHORT)
//                .show()
//        }
//    }

    fun errorMessage(context: Context, errorCode: Int): String {
        return when (errorCode) {
            GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "El servicio de geovalla no está disponible ahora"
            GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "Tu aplicación ha registrado demasiadas geocercas"
            GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "Ha proporcionado demasiados PendingIntents a la llamada addGeofences ()"
            else -> "Error desconocido: el servicio Geofence no está disponible ahora"
        }
    }
}
