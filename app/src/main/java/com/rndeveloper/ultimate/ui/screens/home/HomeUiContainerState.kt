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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.rndeveloper.ultimate.model.Position
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

    private var isTilt: Boolean = false

    fun onCameraTilt() {
//      FIXME:  Handle this correctly
        isTilt = !isTilt
        onCamera(tilt = if (isTilt) 90f else 0f)
    }

    fun onOpenDrawer() {
        scope.launch { drawerState.open() }
    }

    fun onScreenState(newScreenState: ScreenState) {
        screenState = newScreenState
    }

    fun onCamera(
        target: Position? = Position(
            camPosState.position.target.latitude,
            camPosState.position.target.longitude
        ),
        zoom: Float = camPosState.position.zoom,
        tilt: Float = camPosState.position.tilt,
        bearing: Float = camPosState.position.bearing,
    ) {
        target?.let { position ->
            scope.launch {
                camPosState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition(LatLng(position.lat, position.lng), zoom, tilt, bearing)
                    )
                )
            }
        }
    }
}

enum class ScreenState {
    MAIN, ADDSPOT, PARKMYCAR
}