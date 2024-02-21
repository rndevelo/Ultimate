package com.rndeveloper.ultimate.ui.screens.permissions

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.rndeveloper.ultimate.services.ActivityTransitionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionsViewModel @Inject constructor(
    private val activityTransition: ActivityTransitionManager
) : ViewModel() {

    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    fun startActivityTransition() =
        activityTransition.startActivityTransition()

}
