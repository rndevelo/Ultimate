package com.rndeveloper.ultimate.ui.screens.home

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.nav.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberHomeUiContainerState(
    camPosState: CameraPositionState = rememberCameraPositionState(),
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    bsScaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    scrollState: LazyListState = rememberLazyListState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): HomeUiContainerState = remember {
    HomeUiContainerState(
        camPosState,
        drawerState,
        bsScaffoldState,
        scrollState,
        scope,
        navController
    )
}

@Stable
class HomeUiContainerState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val camPosState: CameraPositionState,
    val drawerState: DrawerState,
    val bsScaffoldState: BottomSheetScaffoldState,
    val scrollState: LazyListState,
    private val scope: CoroutineScope,
    val navController: NavHostController,
) {

    var screenState by mutableStateOf(ScreenState.MAIN)
        private set


    // UI logic
    val isSetState: Boolean
        get() = screenState == ScreenState.ADDSPOT || screenState == ScreenState.PARKMYCAR

    var isMapLoaded by mutableStateOf(false)

    var isExpandedAdmobButton by mutableStateOf(false)

    var isAlertDialogVisible by mutableStateOf(false)

    var indexSpotTime by mutableIntStateOf(0)

    var indexSpotType by mutableIntStateOf(0)

    private var isTilt = false

    fun onExpandedAdmobButton(isVisible: Boolean) {
        isExpandedAdmobButton = isVisible
    }

    fun onVisibleAlertDialog(isVisible: Boolean) {
        isAlertDialogVisible = isVisible
    }

    fun onSpotTime(spotTime: Int) {
        indexSpotTime = spotTime
    }

    fun onSpotType(spotType: Int) {
        indexSpotType = spotType
    }

    fun onNavigate() {
        navController.navigate(Routes.PermissionsScreen.route) {
            popUpTo(Routes.HomeScreen.route) { inclusive = true }
        }
    }

    fun onMapLoaded() {
        isMapLoaded = true
    }

    fun onOpenDrawer() {
        scope.launch { drawerState.open() }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun onOpenBottomSheet() {
        scope.launch {
            bsScaffoldState.bottomSheetState.expand()
        }
    }

    fun onAnimateCamera(
        latLng: LatLng = camPosState.position.target,
        zoom: Float = 15f,
        tilt: Float = camPosState.position.tilt
    ) {
        scope.launch {
            camPosState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition(latLng, zoom, tilt, 0f)
                )
            )
        }
    }

    fun onAnimateCameraTilt() {
        isTilt = !isTilt
        onAnimateCamera(zoom = camPosState.position.zoom, tilt = if (isTilt) 90f else 0f)
    }

    fun onAnimateCameraBounds(from: Position?, to: Position?) {
        scope.launch {
            val latLngBounds = LatLngBounds.Builder()
            from?.let { loc ->
                latLngBounds.include(LatLng(loc.lat, loc.lng))
            }
            to?.let {
                latLngBounds.include(LatLng(to.lat, to.lng))
            }
            camPosState.animate(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 420))
        }
    }

    fun onScreenState(newScreenState: ScreenState) {
        screenState = newScreenState
    }
}

enum class ScreenState {
    MAIN, ADDSPOT, PARKMYCAR
}