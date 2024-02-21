package com.rndeveloper.ultimate.ui.screens.permissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.extensions.findActivity
import com.rndeveloper.ultimate.nav.Routes
import com.rndeveloper.ultimate.ui.screens.permissions.components.ActivityRecognitionPermissionTextProvider
import com.rndeveloper.ultimate.ui.screens.permissions.components.BackGroundLocationPermissionTextProvider
import com.rndeveloper.ultimate.ui.screens.permissions.components.PermissionDialog
import com.rndeveloper.ultimate.ui.screens.permissions.components.PermissionsMainContent
import com.rndeveloper.ultimate.ui.screens.permissions.components.PostNotificationsPermissionTextProvider
import com.rndeveloper.ultimate.utils.Constants.permissionsToRequest

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PermissionsScreen(
    navController: NavController,
    permissionsViewModel: PermissionsViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val dialogQueue = permissionsViewModel.visiblePermissionDialogQueue

    val multiplePermissionsState =
        rememberMultiplePermissionsState(permissionsToRequest.toList())

    val result = multiplePermissionsState.permissions.all {
        it.status == PermissionStatus.Granted
    }

    LaunchedEffect(key1 = result) {
        if (result) {
            permissionsViewModel.startActivityTransition()
            navController.navigate(Routes.HomeScreen.route) {
                popUpTo(Routes.PermissionsScreen.route) { inclusive = true }
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

    PermissionsMainContent(
        description = stringResource(
            id = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                R.string.permissions_text_background_description
            } else {
                R.string.permissions_text_main_description
            }
        ),
        onEvent = { multiplePermissionResultLauncher.launch(permissionsToRequest) }
    )

    dialogQueue
        .reversed()
        .forEach { permission ->
            PermissionDialog(
                permissionTextProvider = when (permission) {
                    Manifest.permission.ACTIVITY_RECOGNITION -> ActivityRecognitionPermissionTextProvider()
                    Manifest.permission.POST_NOTIFICATIONS -> PostNotificationsPermissionTextProvider()
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION -> BackGroundLocationPermissionTextProvider()
                    else -> return@forEach
                },
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    context.findActivity()!!,
                    permission
                ),
                onOkClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION) {
                        permissionsViewModel.dismissDialog()
                        context.findActivity()?.openAppSettings()
                    } else {
                        permissionsViewModel.dismissDialog()
                        multiplePermissionResultLauncher.launch(permissionsToRequest)
                    }
                },
                onGoToAppSettingsClick = { context.findActivity()?.openAppSettings() },
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