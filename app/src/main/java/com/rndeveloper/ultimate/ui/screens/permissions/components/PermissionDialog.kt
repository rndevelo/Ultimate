package com.rndeveloper.ultimate.ui.screens.permissions.components

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.R

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
) {

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider()
                Text(
                    text = if (isPermanentlyDeclined) {
                        stringResource(R.string.permission_text_grant_permission)
                    } else {
                        stringResource(R.string.permission_text_ok)
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isPermanentlyDeclined) {
                                onGoToAppSettingsClick()
                            } else {
                                onOkClick()
                            }
                        }
                        .padding(16.dp)
                )
            }
        },
        title = {
            Text(text = stringResource(R.string.permission_text_permission_required))
        },
        text = {
            Text(text = permissionTextProvider.getDescription(isPermanentlyDeclined = isPermanentlyDeclined, context = context))
        },
        modifier = modifier
    )

}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean, context: Context): String
}

class ActivityRecognitionPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean, context: Context): String {
        return if (isPermanentlyDeclined) {
            context.getString(R.string.permission_text_permanently_declined_activity_recognition) +
                    context.getString(R.string.permission_text_app_settings)
        } else {
            context.getString(R.string.permission_text_app_requires_functionality)
        }
    }
}

class PostNotificationsPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean, context: Context): String {
        return if (isPermanentlyDeclined) {
            context.getString(R.string.permission_text_permanently_declined_post_notifications) +
                    context.getString(R.string.permission_text_app_settings)
        } else {
            context.getString(R.string.permission_text_app_requires_functionality)
        }
    }
}

class FineLocationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean, context: Context): String {
        return if (isPermanentlyDeclined) {
            context.getString(R.string.permission_text_permanently_declined_fine) +
                    context.getString(R.string.permission_text_app_settings)
        } else {
            context.getString(R.string.permission_text_app_requires_functionality)
        }
    }
}

class BackGroundLocationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean, context: Context): String {
        return if (isPermanentlyDeclined) {
            context.getString(R.string.permission_text_permanently_declined_background) +
                    context.getString(R.string.permission_text_app_settings)
        } else {
            context.getString(R.string.permission_text_app_requires_functionality)
        }
    }
}