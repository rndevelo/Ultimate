package com.rndeveloper.ultimate.nav

import com.rndeveloper.ultimate.utils.Constants.HOME_SCREEN
import com.rndeveloper.ultimate.utils.Constants.LOGIN_SCREEN
import com.rndeveloper.ultimate.utils.Constants.PERMISSIONS_SCREEN
import com.rndeveloper.ultimate.utils.Constants.SETTINGS_SCREEN


sealed class Routes(val route: String) {
    object LoginScreen: Routes(LOGIN_SCREEN)
    object PermissionsScreen: Routes(PERMISSIONS_SCREEN)
    object HomeScreen: Routes(HOME_SCREEN)
    object SettingsScreen: Routes(SETTINGS_SCREEN)
}