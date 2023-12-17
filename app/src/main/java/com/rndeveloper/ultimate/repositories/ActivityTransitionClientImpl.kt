package com.rndeveloper.ultimate.repositories

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import com.rndeveloper.ultimate.extensions.fixApi31
import com.rndeveloper.ultimate.model.User
import com.rndeveloper.ultimate.receivers.ActivityTransitionReceiver
import javax.inject.Inject

class ActivityTransitionClientImpl @Inject constructor(private val appContext: Context) :
    ActivityTransitionClient {
    @SuppressLint("MissingPermission")
    override fun startActivityTransition(user: User) {

        val transitions = mutableListOf<ActivityTransition>()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        val request = ActivityTransitionRequest(transitions)

        val task = ActivityRecognition.getClient(appContext)
            .requestActivityTransitionUpdates(request, activityTransitionPendingIntent(user))

        task.addOnSuccessListener {
            // Handle success
            Toast.makeText(appContext, "success, $it", Toast.LENGTH_SHORT).show()


        }

        task.addOnFailureListener { e: Exception ->
            // Handle error
            Toast.makeText(appContext, "failure, ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun activityTransitionPendingIntent(user: User): PendingIntent {
        val intent = Intent(appContext, ActivityTransitionReceiver::class.java)
        val bundle = Bundle()
        bundle.putSerializable("user", user)
        intent.putExtra("bundle", bundle)
        return PendingIntent.getBroadcast(
            appContext,
            3,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT.fixApi31()
        )
    }
}

interface ActivityTransitionClient {
    fun startActivityTransition(user: User)
}