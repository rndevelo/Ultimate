package com.rndeveloper.ultimate.nav

import com.rndeveloper.ultimate.utils.Constants.ACCOUNT_SCREEN
import com.rndeveloper.ultimate.utils.Constants.HISTORY_SCREEN
import com.rndeveloper.ultimate.utils.Constants.HOME_SCREEN
import com.rndeveloper.ultimate.utils.Constants.LOGIN_SCREEN
import com.rndeveloper.ultimate.utils.Constants.MARKER_SCREEN
import com.rndeveloper.ultimate.utils.Constants.PERMISSIONS_SCREEN


sealed class Routes(val route: String) {
    object LoginScreen: Routes(LOGIN_SCREEN)
    //    FIXME: Is necessary do it?
    object PermissionsScreen: Routes(PERMISSIONS_SCREEN)
    //
    object HomeScreen: Routes(HOME_SCREEN)
    object HistoryScreen: Routes(HISTORY_SCREEN)
    object AccountScreen: Routes(ACCOUNT_SCREEN)
    object MarkerScreen: Routes(MARKER_SCREEN){
        fun passId(address: String, lat: String, lng: String): String {
            return "marker_screen/$address/$lat/$lng"
        }
    }
}