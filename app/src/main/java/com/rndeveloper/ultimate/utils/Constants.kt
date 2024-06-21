package com.rndeveloper.ultimate.utils

import android.Manifest
import android.os.Build

object Constants {

    //    Arguments
    const val ARGUMENT_ADDRESS = "item_address"
    const val ARGUMENT_lAT = "item_lat"
    const val ARGUMENT_LNG = "item_lng"

    //    Screens
    const val LOGIN_SCREEN = "login_screen"
    const val PERMISSIONS_SCREEN = "permissions_screen"
    const val HOME_SCREEN = "profile_screen"
    const val HISTORY_SCREEN = "history_screen"
    const val MARKER_SCREEN = "marker_screen/{$ARGUMENT_ADDRESS}/{$ARGUMENT_lAT}/{$ARGUMENT_LNG}"
    const val SETTINGS_SCREEN = "settings_screen"

    //    User
    const val USER_REFERENCE = "USERS"

    //    Spots
    const val SPOTS_RADIUS_TARGET = 1000.0
    const val SPOT_COLLECTION_REFERENCE = "SPOTS"
    const val AREA_COLLECTION_REFERENCE = "AREAS"

    //    Timer
    const val REQUEST_CODE = 0
    const val SPOTS_TIMER = "SPOTS_TIMER"
    const val INTERVAL: Long = 1_000L
    const val TIMER: Long = 30_000L
    const val DEFAULT_ELAPSED_TIME: Long = 0L
    //Time
    const val MINUTE: Long = 60_000L

    //    Geofence
    const val RADIUS_IS_NEAR_SPOT = 500.0f

    //    Notifications
    const val ACT_CHANNEL_ID = "ActivityRecognitionChannel"
    const val NOTIFICATION_ID = 33

    val permissionsToRequest = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> arrayOf(
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        else -> {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }
    }
}
