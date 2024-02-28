package com.rndeveloper.ultimate.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.maps.model.LatLng
import com.google.common.base.Stopwatch
import com.rndeveloper.ultimate.MainActivity
import com.rndeveloper.ultimate.R
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
                sendNotification(
                    this,
                    "Your actually activity",
                    getInfo(event),
                    Constants.NOTIFICATION_ID
                )
            }
        }
        return START_NOT_STICKY
    }

    private fun handleActivityTransitionEvent(event: ActivityTransitionEvent) {

        if (event.activityType == DetectedActivity.STILL) {
            when (event.transitionType) {
                ActivityTransition.ACTIVITY_TRANSITION_ENTER -> {
                    setSpotData()
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
        val directions = async { geocoderRepository.getAddressList(LatLng(location!!.lat, location.lng)).firstOrNull() }.await()

        Spot(
            timestamp = Utils.currentTime(),
            type = SpotType.BLUE,
            directions = directions!!,
            position = location!!,
            user = user ?: User()
        ).let { spot ->
            setSpotsUseCase(Constants.AREA_COLLECTION_REFERENCE to spot).collectLatest {
                time.stop()
                sendNotification(
                    context = this@UploadFirebaseService,
                    contentTitle = "¡Enhorabuena, ${user!!.username}!",
                    contentText = "Has agregado una plaza libre.",
                    notificationId = 32
                )
                Log.d("TAG", "Tiempo de ejecución (BUENO): ${time.elapsed(TimeUnit.MILLISECONDS)} ms")
                stopSelf()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(
        context: Context,
        contentTitle: String,
        contentText: String,
        notificationId: Int
    ) {

        val notifyIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notifyPendingIntent = PendingIntent.getActivity(
            context, 0, notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, Constants.ACT_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_ultimate)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
//            .setOngoing(true)
            .setContentIntent(notifyPendingIntent)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define.
            notify(notificationId, builder.build())
        }
    }

    private fun getInfo(event: ActivityTransitionEvent): String {
        return "Transition: " + toActivityString(event.activityType) +
                " (" + toTransitionType(event.transitionType) + ")" + "   " +
                SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
    }
    private fun toActivityString(activity: Int): String {
        return when (activity) {
            DetectedActivity.STILL -> "STILL"
            DetectedActivity.WALKING -> "WALKING"
            DetectedActivity.IN_VEHICLE -> "IN VEHICLE"
            DetectedActivity.RUNNING -> "RUNNING"
            else -> "UNKNOWN"
        }
    }

    private fun toTransitionType(transitionType: Int): String {
        return when (transitionType) {
            ActivityTransition.ACTIVITY_TRANSITION_ENTER -> "ENTER"
            ActivityTransition.ACTIVITY_TRANSITION_EXIT -> "EXIT"
            else -> "UNKNOWN"
        }
    }
}
