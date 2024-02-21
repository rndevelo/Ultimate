package com.rndeveloper.ultimate.services

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.maps.model.LatLng
import com.google.common.base.Stopwatch
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.model.SpotType
import com.rndeveloper.ultimate.model.User
import com.rndeveloper.ultimate.repositories.GeocoderRepository
import com.rndeveloper.ultimate.repositories.LocationClient
import com.rndeveloper.ultimate.repositories.UserRepository
import com.rndeveloper.ultimate.usecases.spots.SetSpotUseCase
import com.rndeveloper.ultimate.utils.Constants
import com.rndeveloper.ultimate.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class UploadFirebaseService : LifecycleService() {

    @Inject
    lateinit var setSpotsUseCase: SetSpotUseCase

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var locationClient: LocationClient

    @Inject
    lateinit var geocoderRepository: GeocoderRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("TAG", "onStartCommand")
        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent!!)
            for (event in result?.transitionEvents ?: emptyList()) {
                handleActivityTransitionEvent(event)
            }
        }
        return START_NOT_STICKY
    }

    private fun handleActivityTransitionEvent(event: ActivityTransitionEvent) {

        if (event.activityType == DetectedActivity.WALKING) {
            when (event.transitionType) {
                ActivityTransition.ACTIVITY_TRANSITION_ENTER -> {
                    Log.d("TAG", "El usuario ha comenzado a andar")
                }

                ActivityTransition.ACTIVITY_TRANSITION_EXIT -> {
                    Log.d("TAG", "El usuario ha dejado de andar")
                }
            }
        } else if (event.activityType == DetectedActivity.IN_VEHICLE) {
            when (event.transitionType) {
                ActivityTransition.ACTIVITY_TRANSITION_ENTER -> {
                    Log.d("TAG", "El usuario ha comenzado a conducir")
                    setSpotData()
                }

                ActivityTransition.ACTIVITY_TRANSITION_EXIT -> {
                    Log.d("TAG", "El usuario ha dejado de conducir")
                }
            }
        } else {
            Log.d("TAG", "Broadscast con otra info")
        }
    }

    private fun setSpotData() = lifecycleScope.launch {

        val time = Stopwatch.createUnstarted()
        time.start()

        val user = async { userRepository.getUserData().firstOrNull()?.getOrNull() }.await()
        val location = async { locationClient.getLocationsRequest().firstOrNull() }.await()

        geocoderRepository.getAddressList(LatLng(location!!.lat, location.lng)) { directions ->
            Spot(
                timestamp = Utils.currentTime(),
                type = SpotType.BLUE,
                directions = directions,
                position = location,
                user = user ?: User()
            ).let { spot ->
                this.launch {
                    setSpotsUseCase(Constants.AREA_COLLECTION_REFERENCE to spot).collectLatest {
                        time.stop()
                        Log.d("TAG", "Tiempo de ejecución (BUENO): ${time.elapsed(TimeUnit.MILLISECONDS)} ms")
                        stopSelf()
                    }
                }
            }
        }
    }
}
