package com.rndeveloper.ultimate.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.google.common.base.Stopwatch
import com.rndeveloper.ultimate.utils.Utils
import kotlinx.coroutines.launch

class MyService : LifecycleService() {

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
                    Utils.sendNotification(
                        context = this,
                        contentTitle = "Estas andando",
                        contentText = "¡!",
                        notificationId = 32
                    )
                    Log.d("TAG", "El usuario ha comenzado a andar")
                }

                ActivityTransition.ACTIVITY_TRANSITION_EXIT -> {
                    Utils.sendNotification(
                        context = this,
                        contentTitle = "No estas andando",
                        contentText = "¡!",
                        notificationId = 32
                    )
                    Log.d("TAG", "El usuario ha dejado de andar")
                }
            }
        } else if (event.activityType == DetectedActivity.IN_VEHICLE) {
            when (event.transitionType) {
                ActivityTransition.ACTIVITY_TRANSITION_ENTER -> {
                    Utils.sendNotification(
                        context = this,
                        contentTitle = "Estas conduciendo",
                        contentText = "¡!",
                        notificationId = 32
                    )
                    Log.d("TAG", "El usuario ha comenzado a conducir")
//                    setSpotData()
                }

                ActivityTransition.ACTIVITY_TRANSITION_EXIT -> {
                    Utils.sendNotification(
                        context = this,
                        contentTitle = "No estas conduciendo",
                        contentText = "¡!",
                        notificationId = 32
                    )
                    Log.d("TAG", "El usuario ha dejado de conducir")
                }
            }
        } else {
            Utils.sendNotification(
                context = this,
                contentTitle = "$event",
                contentText = "¡!",
                notificationId = 32
            )
            Log.d("TAG", "Broadscast con otra info")
        }
    }

    private fun setSpotData() =
        lifecycleScope.launch {

            val time = Stopwatch.createUnstarted()
            time.start()

//        val user = async { userRepository.getUserData().firstOrNull()?.getOrNull() }.await()
//        val location = async { locationClient.getLocationsRequest().firstOrNull() }.await()
//
//        geocoderRepository.getAddressList(LatLng(location!!.lat, location.lng)).collectLatest { directions ->
//            Item(
////                timestamp = Utils.currentTime(),
////                type = SpotType.BLUE,
////                directions = directions,
////                position = location,
////                user = user ?: User()
//            ).let { spot ->
//                this.launch {
//                    setSpotsUseCase(Constants.AREA_COLLECTION_REFERENCE to spot).collectLatest {
//                        time.stop()
//                        Log.d("TAG", "Tiempo de ejecución (BUENO): ${time.elapsed(TimeUnit.MILLISECONDS)} ms")
//                        stopSelf()
//                    }
//                }
//            }
//        }
        }
}