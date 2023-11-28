package com.rndeveloper.ultimate.utils

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
    const val ACCOUNT_SCREEN = "account_screen"

    //    Timer
    const val REQUEST_CODE = 0
    const val SPOTS_TIMER = "SPOTS_TIMER"
    const val INTERVAL: Long = 1_000L
    const val TIMER: Long = 10_000L

    //    Geofence
    const val RADIUS_IS_NEAR_SPOT = 500.0f

}