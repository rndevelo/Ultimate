package com.rndeveloper.ultimate.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rndeveloper.ultimate.nav.Routes
import com.rndeveloper.ultimate.ui.theme.UltimateTheme

@Composable
fun SettingsScreen(navController: NavHostController, settingsViewModel: SettingsViewModel = hiltViewModel()) {

    val loginUiState by settingsViewModel.uiSettingsState.collectAsStateWithLifecycle()

    if (!loginUiState.isLogged) {
        LaunchedEffect(key1 = Unit) {
            navController.navigate(Routes.LoginScreen.route) {
                popUpTo(Routes.SettingsScreen.route) { inclusive = true }
            }
        }
    }

    Scaffold { paddingValues ->
        SettingsContent(
//            profileUiState,
            onClickLogOut = {
                settingsViewModel.logOut()
            },
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
fun SettingsContent(/*profileUiState: ProfileUiState,*/ onClickLogOut: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = { onClickLogOut() }, modifier = Modifier.padding(top = 80.dp)) {
            Text(text = "Logout")
            Spacer(modifier = Modifier.width(10.dp))
            Icon(imageVector = Icons.Filled.Logout, contentDescription = "Logout")
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    UltimateTheme {
        SettingsScreen(navController = rememberNavController())
    }
}
