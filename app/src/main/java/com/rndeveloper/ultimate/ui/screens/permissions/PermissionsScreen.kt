package com.rndeveloper.ultimate.ui.screens.permissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.extensions.findActivity
import com.rndeveloper.ultimate.nav.Routes
import com.rndeveloper.ultimate.ui.screens.permissions.components.ActivityRecognitionPermissionTextProvider
import com.rndeveloper.ultimate.ui.screens.permissions.components.BackGroundLocationPermissionTextProvider
import com.rndeveloper.ultimate.ui.screens.permissions.components.FineLocationPermissionTextProvider
import com.rndeveloper.ultimate.ui.screens.permissions.components.PermissionDialog
import com.rndeveloper.ultimate.ui.screens.permissions.components.PermissionsMainContent
import com.rndeveloper.ultimate.ui.screens.permissions.components.PostNotificationsPermissionTextProvider
import com.rndeveloper.ultimate.utils.Constants.permissionsToRequest

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(
    navController: NavController,
    permissionsViewModel: PermissionsViewModel = viewModel()
) {

    val context = LocalContext.current
    val dialogQueue = permissionsViewModel.visiblePermissionDialogQueue

    val multiplePermissionsState =
        rememberMultiplePermissionsState(permissionsToRequest.toList())

    val result = multiplePermissionsState.permissions.all {
        it.status == PermissionStatus.Granted
    }

    val backgroundPermissionsState =
        rememberPermissionState(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

    val backgroundPermissionResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        backgroundPermissionsState.status == PermissionStatus.Granted
    } else {
        true
    }

    LaunchedEffect(key1 = result, key2 = backgroundPermissionResult) {
        if (result && !backgroundPermissionResult) {
            permissionsViewModel.onPermissionResult(
                permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                isGranted = false
            )
        } else if (result && backgroundPermissionResult) {
            navController.navigate(Routes.HomeScreen.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            permissionsToRequest.forEach { permission ->
                permissionsViewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )
            }
        }
    )

    val backgroundLocationPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
//            if (perms[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == true) {
//                navController.navigate(Routes.HomeScreen.route) {
//                    popUpTo(navController.graph.id) { inclusive = true }
//                }
//            }
        }
    )

    PermissionsMainContent(
        description = stringResource(
            id = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                R.string.permissions_text_background_description
            } else {
                R.string.permissions_text_main_description
            }
        ),
        onEvent = {
            if (!result) {
                multiplePermissionResultLauncher.launch(permissionsToRequest)
            } else {
                permissionsViewModel.onPermissionResult(
                    permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    isGranted = false
                )
            }
        }
    )

    dialogQueue
        .reversed()
        .forEach { permission ->
            PermissionDialog(
                permissionTextProvider = when (permission) {
                    Manifest.permission.ACTIVITY_RECOGNITION -> ActivityRecognitionPermissionTextProvider()
                    Manifest.permission.POST_NOTIFICATIONS -> PostNotificationsPermissionTextProvider()
                    Manifest.permission.ACCESS_FINE_LOCATION -> FineLocationPermissionTextProvider()
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION -> BackGroundLocationPermissionTextProvider()
                    else -> return@forEach
                },
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    context.findActivity()!!,
                    permission
                ),
                onOkClick = {
                    if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION) {
                        backgroundLocationPermissionResultLauncher.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                        permissionsViewModel.dismissDialog()
                    } else {
                        multiplePermissionResultLauncher.launch(permissionsToRequest)
                        permissionsViewModel.dismissDialog()
                    }
                },
                onGoToAppSettingsClick = {
                    context.findActivity()?.openAppSettings()
                    permissionsViewModel.dismissDialog()
                },
                onDismiss = permissionsViewModel::dismissDialog,
            )
        }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}