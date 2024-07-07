package com.rndeveloper.ultimate.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.annotation.CallSuper
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.rndeveloper.ultimate.usecases.spots.RemoveSpotUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class HiltGeofenceReceiver : BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context, intent: Intent) {
    }
}

@AndroidEntryPoint
class GeofenceReceiver : HiltGeofenceReceiver() {

    @Inject
    lateinit var removeSpotUseCase: RemoveSpotUseCase

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent) // <-- it's the trick

        val country = intent.extras!!.getString("country")
        val area = intent.extras!!.getString("area")
        val tag = intent.extras!!.getString("tag")
        val uid = intent.extras!!.getString("uid")
        val token = intent.extras!!.getString("token")
        if (country != null && area != null && tag != null && uid != null && token != null) {
            GlobalScope.launch(Dispatchers.IO) {
                removeSpotUseCase(Pair(Triple(country, area, tag), uid to token))
                    .collectLatest { newHomeUiState -> }
            }
        }

    }

    fun errorMessage(context: Context, errorCode: Int): String {
        return when (errorCode) {
            GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "El servicio de geovalla no está disponible ahora"
            GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "Tu aplicación ha registrado demasiadas geocercas"
            GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "Ha proporcionado demasiados PendingIntents a la llamada addGeofences ()"
            else -> "Error desconocido: el servicio Geofence no está disponible ahora"
        }
    }
}
