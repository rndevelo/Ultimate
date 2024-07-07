package com.rndeveloper.ultimate.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.extensions.customSnackBar
import com.rndeveloper.ultimate.nav.Routes
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.ProfileHeaderContent
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

    var dialogImageVector by remember { mutableStateOf(Icons.Default.Output) }
    var dialogText by remember { mutableStateOf("") }

    if (!uiSettingsState.isLogged) {
        LaunchedEffect(key1 = Unit) {
            navController.navigate(Routes.LoginScreen.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    if (!uiSettingsState.errorMessage?.error.isNullOrBlank()) {
        LaunchedEffect(snackBarHostState) {
            snackBarHostState.customSnackBar(uiSettingsState.errorMessage!!.error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_text_settings),
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
            onShowAlertDialog = { imageVector, text ->
                isShowAlertDialog = true
                dialogImageVector = imageVector
                dialogText = text
            },
            modifier = Modifier.padding(paddingValues),
        )
        AnimatedVisibility(visible = isShowAlertDialog) {

            AlertDialog(
                onDismissRequest = { isShowAlertDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            isShowAlertDialog = false
                            if (dialogImageVector == Icons.Default.Output) {
                                settingsViewModel.logOut()
                            } else {
                                settingsViewModel.deleteUser()
                            }
                        }
                    ) {
                        Text(text = stringResource(R.string.settings_text_dialog_confirm))
                    }
                },
                dismissButton = {
                    Button(onClick = { isShowAlertDialog = false }) {
                        Text(text = stringResource(R.string.settings_text_dialog_cancel))
                    }
                },
                icon = {
                    Icon(
                        imageVector = dialogImageVector,
                        contentDescription = Icons.Default.DeleteForever.toString()
                    )
                },
                text = {
                    Text(text = dialogText)
                }
            )
        }
    }
}

@Composable
fun SettingsContent(
    uiSettingsState: SettingsUiState,
    onShowAlertDialog: (ImageVector, String) -> Unit,
    modifier: Modifier = Modifier
) {

    val textLogout = stringResource(R.string.settings_text_do_you_want_to_log_out)
    val textDeleteUser = stringResource(R.string.settings_text_do_you_want_to_delete_your_account)

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

        SettingButtonContent(imageVector = Icons.Default.Output, text = stringResource(R.string.settings_text_logout), {
            onShowAlertDialog(
                Icons.Default.Output,
                textLogout
            )
        }, modifier = Modifier.padding(top = 25.dp))

        SettingButtonContent(imageVector = Icons.Default.DeleteForever, text = stringResource(R.string.settings_text_delete_user), {
            onShowAlertDialog(
                Icons.Default.DeleteForever,
                textDeleteUser
            )
        }, modifier = Modifier.padding(top = 7.dp))
    }
}

@Composable
private fun SettingButtonContent(
    imageVector: ImageVector,
    text: String,
    onShowAlertDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onShowAlertDialog,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text)
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = imageVector,
                contentDescription = imageVector.toString()
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    UltimateTheme {
        SettingsContent(
            uiSettingsState = SettingsUiState(),
            onShowAlertDialog = { _, _ -> },
        )
    }
}
