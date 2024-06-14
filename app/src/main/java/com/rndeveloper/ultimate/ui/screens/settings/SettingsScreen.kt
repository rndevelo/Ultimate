package com.rndeveloper.ultimate.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Output
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.nav.Routes
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.ProfileHeaderContent
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.SetAlertDialog
import com.rndeveloper.ultimate.ui.theme.UltimateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {

    val uiSettingsState by settingsViewModel.uiSettingsState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val interactionSource = remember { MutableInteractionSource() }
    var isShowAlertDialog by remember { mutableStateOf(false) }

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
                title = {
                    Text(
                        text = stringResource(R.string.settings_text_app_bar_settings),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = Icons.Filled.ArrowBackIosNew.toString(),
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { navController.navigateUp() }
                    )
                },
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        SettingsContent(
            uiSettingsState = uiSettingsState,
            onClickLogOut = settingsViewModel::logOut,
            onClickDeleteUser = settingsViewModel::deleteUser,
            onShowAlertDialog = { isShowAlertDialog = true },
            modifier = Modifier.padding(paddingValues),
        )
        AnimatedVisibility(visible = isShowAlertDialog) {

            AlertDialog(
                onDismissRequest = { isShowAlertDialog = false },
                confirmButton = {
                    Button(onClick = { isShowAlertDialog = false }) {
                        Text(text = "Confirm")
                    }
                },
                dismissButton = {
                    Button(onClick = { isShowAlertDialog = false }) {
                        Text(text = "Cancel")
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = Icons.Default.DeleteForever.toString()
                    )
                },
                text = {
                    Text(text = "Desea eliminar su cuenta de forma permanente?")
                }
            )
        }
    }
}

@Composable
fun SettingsContent(
    uiSettingsState: SettingsUiState,
    onClickLogOut: () -> Unit,
    onClickDeleteUser: () -> Unit,
    onShowAlertDialog: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 10.dp, horizontal = 15.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Card {
            ProfileHeaderContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 15.dp),
                user = uiSettingsState.user
            )
        }

        Card(modifier = Modifier.padding(top = 25.dp)) {
            TextButton(
                onClick = onShowAlertDialog,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Logout")
                Spacer(modifier = Modifier.width(10.dp))
                Icon(imageVector = Icons.Default.Output, contentDescription = "Logout")
            }
        }

        Card(modifier = Modifier.padding(top = 7.dp)) {
            TextButton(
                onClick = onShowAlertDialog,
                modifier = Modifier.fillMaxWidth(),
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
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    UltimateTheme {
        SettingsContent(
            uiSettingsState = SettingsUiState(),
            onClickLogOut = { /*TODO*/ },
            onClickDeleteUser = { /*TODO*/ },
            onShowAlertDialog = { }
        )
    }
}
