package com.rndeveloper.ultimate.receivers

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.maps.model.LatLng
import com.rndeveloper.ultimate.MainActivity
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.model.SpotType
import com.rndeveloper.ultimate.model.User
import com.rndeveloper.ultimate.repositories.GeocoderRepository
import com.rndeveloper.ultimate.repositories.LocationClient
import com.rndeveloper.ultimate.repositories.UserRepository
import com.rndeveloper.ultimate.usecases.spots.SetSpotUseCase
import com.rndeveloper.ultimate.usecases.user.GetUserDataUseCase
import com.rndeveloper.ultimate.utils.Constants.ACT_CHANNEL_ID
import com.rndeveloper.ultimate.utils.Constants.AREA_COLLECTION_REFERENCE
import com.rndeveloper.ultimate.utils.Constants.NOTIFICATION_ID
import com.rndeveloper.ultimate.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

abstract class HiltActivityTransitionReceiver : BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context, intent: Intent) {
    }
}

@AndroidEntryPoint
class ActivityTransitionReceiver : HiltActivityTransitionReceiver() {

    @Inject
    lateinit var setSpotsUseCase: SetSpotUseCase

    @Inject
    lateinit var getUserDataUseCase: GetUserDataUseCase

    @Inject
    lateinit var locationClient: LocationClient

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var geocoderRepository: GeocoderRepository

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val user = intent.getBundleExtra("bundle")?.let { getBundleExtras(it) }

        if (ActivityTransitionResult.hasResult(intent)) {

            val result = ActivityTransitionResult.extractResult(intent)
            result?.transitionEvents?.forEach { event ->

                if (user != null) {
                    sendNotification(
                        context,
                        "Actividad actual de ${user.username}",
                        getInfo(event),
                        NOTIFICATION_ID
                    )
                }

                when {
                    event.activityType == DetectedActivity.IN_VEHICLE &&
                            event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT -> {
                        setMyCarData(context, user)
                    }

                    event.activityType == DetectedActivity.IN_VEHICLE &&
                            event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER -> {
//                        setSpotData(context, user)
                    }
                }
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

        val builder = NotificationCompat.Builder(context, ACT_CHANNEL_ID)
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


    @OptIn(DelicateCoroutinesApi::class)
    private fun setMyCarData(
        context: Context,
        user: User?,
    ) {
        locationClient.getLastLocation { locationData ->
            user?.copy(car = locationData)?.let { user ->
                GlobalScope.launch {
                    userRepository.setUserCar(user).collectLatest {
                        sendNotification(
                            context = context,
                            contentTitle = "¿Has aparcado?",
                            contentText = "¡Toca aquí si quieres corregir tu aparcamiento!",
                            notificationId = 32
                        )
                    }
                }
            }
        }
    }

//    @OptIn(DelicateCoroutinesApi::class)
//    private fun setSpotData(
//        context: Context,
//        user: User?,
//    ) {
//
//        GlobalScope.launch {
//            locationClient.getLocationsRequest().cancellable().collectLatest { location ->
//                geocoderRepository.getAddressList(LatLng(location.lat, location.lng)) { directions ->
//                    user?.let { user ->
//                        Spot().copy(
//                            timestamp = Utils.currentTime(),
//                            type = SpotType.BLUE,
//                            directions = directions,
//                            position = location,
//                            user = user
//                        ).let { spot ->
//                            this@launch.launch {
//                                setSpotsUseCase(AREA_COLLECTION_REFERENCE to spot).collectLatest {
//                                    sendNotification(
//                                        context = context,
//                                        contentTitle = "¡Enhorabuena, ${user.username}!",
//                                        contentText = "Has agregado una plaza libre.",
//                                        notificationId = 32
//                                    )
//                                    this.coroutineContext.job.cancel()
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    private fun getBundleExtras(bundle: Bundle): User? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getSerializable("user", User::class.java)
        } else {
            bundle.getSerializable("user") as User?
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
