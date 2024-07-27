package com.rndeveloper.ultimate.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.CallSuper
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.google.firebase.auth.FirebaseAuth
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.repositories.GeocoderRepository
import com.rndeveloper.ultimate.repositories.LocationClient
import com.rndeveloper.ultimate.repositories.UserRepository
import com.rndeveloper.ultimate.usecases.user.GetUserDataUseCase
import com.rndeveloper.ultimate.utils.Utils.sendNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
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
    lateinit var getUserDataUseCase: GetUserDataUseCase

    @Inject
    lateinit var locationClient: LocationClient

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var geocoderRepository: GeocoderRepository

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (ActivityTransitionResult.hasResult(intent)) {

            val result = ActivityTransitionResult.extractResult(intent)
            result?.transitionEvents?.forEach { event ->

                when {
                    event.activityType == DetectedActivity.IN_VEHICLE &&
                            event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT -> {
                        setMyCarData(context)
                    }

                    event.activityType == DetectedActivity.IN_VEHICLE &&
                            event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER -> {
                        sendNotification(
                            context = context,
                            contentTitle = context.getString(R.string.home_text_notification_free_spot),
                            contentText = context.getString(R.string.home_text_notification_add_spot),
                            35
                        )
                    }
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun setMyCarData(context: Context) {

        GlobalScope.launch {

            val userData = async { firebaseAuth.currentUser?.let { userRepository.getUserData(it.uid).firstOrNull()?.getOrNull() } }.await()
            val locationData = async { locationClient.getLocationsRequest().firstOrNull() }.await()

            userData?.copy(car = locationData)?.let { user ->
                firebaseAuth.currentUser?.let {
                    userRepository.setUserData(user).collectLatest {
                        sendNotification(
                            context = context,
                            contentTitle = context.getString(R.string.home_text_notification_park_car),
                            contentText = context.getString(R.string.home_text_notification_correct_park),
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
//        sendNotification(
//            context = context,
//            contentTitle = "¿Has dejado una plaza libre?",
//            contentText = "Envíala para que otro usuario la obtenga.",
//            notificationId = 32
//        )
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
