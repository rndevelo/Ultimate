package com.rndeveloper.ultimate.nav

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.rndeveloper.ultimate.ui.screens.home.HomeScreen
import com.rndeveloper.ultimate.ui.screens.login.LoginScreen
import com.rndeveloper.ultimate.ui.screens.permissions.PermissionsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LoginScreen.route
    ) {
        composable(route = Routes.LoginScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Routes.PermissionsScreen.route) {
            PermissionsScreen(navController = navController)
        }
        composable(route = Routes.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
    }
}