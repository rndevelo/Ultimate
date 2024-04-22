package com.rndeveloper.ultimate.repositories

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import com.rndeveloper.ultimate.extensions.fixApi31
import com.rndeveloper.ultimate.model.User
import com.rndeveloper.ultimate.receivers.ActivityTransitionReceiver
import javax.inject.Inject

class ActivityTransitionRepoImpl @Inject constructor(
    private val activityRecognitionClient: ActivityRecognitionClient,
    private val appContext: Context
) : ActivityTransitionRepo {
    @SuppressLint("MissingPermission")
    override fun startActivityTransition() {

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

        val task = activityRecognitionClient
            .requestActivityTransitionUpdates(request, activityTransitionPendingIntent())

        task.addOnSuccessListener {}

        task.addOnFailureListener { e: Exception ->
            // Handle error
            Toast.makeText(appContext, "failure, ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun activityTransitionPendingIntent(): PendingIntent {
        val intent = Intent(appContext, ActivityTransitionReceiver::class.java)
        val bundle = Bundle()
//        bundle.putSerializable("user", user)
//        intent.putExtra("bundle", bundle)
        return PendingIntent.getBroadcast(
            appContext,
            3,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT.fixApi31()
        )
    }
}

interface ActivityTransitionRepo {
    fun startActivityTransition()
}