package com.rndeveloper.ultimate.nav

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rndeveloper.ultimate.ui.screens.home.HomeScreen
import com.rndeveloper.ultimate.ui.screens.login.LoginScreen
import com.rndeveloper.ultimate.ui.screens.permissions.PermissionsScreen
import com.rndeveloper.ultimate.ui.screens.privatepolicy.PrivacyPolicyScreen
import com.rndeveloper.ultimate.ui.screens.settings.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.PrivacyPolicyScreen.route
    ) {
        composable(route = Routes.PrivacyPolicyScreen.route) {
            PrivacyPolicyScreen(navController = navController)
        }
        composable(route = Routes.LoginScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Routes.PermissionsScreen.route) {
            PermissionsScreen(navController = navController)
        }
        composable(route = Routes.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
        composable(route = Routes.SettingsScreen.route) {
            SettingsScreen(navController = navController)
        }
    }
}