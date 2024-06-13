package com.rndeveloper.ultimate.ui.screens.settings

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rndeveloper.ultimate.nav.Routes
import com.rndeveloper.ultimate.ui.theme.UltimateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {

    val snackBarHostState = remember { SnackbarHostState() }
    val uiSettingsState by settingsViewModel.uiSettingsState.collectAsStateWithLifecycle()

    if (!uiSettingsState.isLogged) {
        LaunchedEffect(key1 = Unit) {
            navController.navigate(Routes.LoginScreen.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    if (!uiSettingsState.errorMessage?.error.isNullOrBlank()) {
        LaunchedEffect(snackBarHostState) {
            snackBarHostState.showSnackbar(
                uiSettingsState.errorMessage!!.error,
                "Close",
                false,
                SnackbarDuration.Long
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = Icons.Filled.ArrowBackIosNew.toString()
                    )
                })
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        SettingsContent(
//            profileUiState,
            onClickLogOut = settingsViewModel::logOut,
            onClickDeleteUser = settingsViewModel::deleteUser,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
fun SettingsContent(
    onClickLogOut: () -> Unit,
    onClickDeleteUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            onClick = onClickLogOut,
            modifier = Modifier.padding(top = 80.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(Color.Gray)
        ) {
            Text(text = "Logout")
            Spacer(modifier = Modifier.width(10.dp))
            Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout")
        }

        Button(
            onClick = onClickDeleteUser,
            modifier = Modifier.padding(top = 80.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(Color.Red)
        ) {
            Text(text = "Delete user")
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = Icons.Default.DeleteForever.toString()
            )
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
