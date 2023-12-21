package com.rndeveloper.ultimate

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.google.firebase.BuildConfig
import com.rndeveloper.ultimate.utils.Constants.ACT_CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant

@HiltAndroidApp
class UltimateApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.home_text_not_park_car)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(ACT_CHANNEL_ID, name, importance).apply {
                setShowBadge(true)
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
                description = descriptionText
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

