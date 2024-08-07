package com.rndeveloper.ultimate.utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.rndeveloper.ultimate.MainActivity
import com.rndeveloper.ultimate.R
import java.util.Calendar

object Utils {

    fun currentTime() = Calendar.getInstance().time.time

    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()

    @SuppressLint("MissingPermission")
    fun sendNotification(
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

        val builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_ultimate)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(notifyPendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define.
            notify(notificationId, builder.build())
        }
    }
}