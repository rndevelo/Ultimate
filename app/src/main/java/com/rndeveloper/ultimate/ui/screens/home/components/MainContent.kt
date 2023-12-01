package com.rndeveloper.ultimate.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapType
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.model.Car
import com.rndeveloper.ultimate.ui.screens.home.SpotsUiState
import com.rndeveloper.ultimate.ui.screens.home.UserUiState
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.ButtonsMapContent
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.GoogleMapContent
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.Constants.DEFAULT_ELAPSED_TIME

@Composable
fun MainContent(
    uiUserState: UserUiState,
    uiSpotsState: SpotsUiState,
    uiElapsedTimeState: Long,
    cameraPositionState: CameraPositionState,
    isAddPanelState: Boolean,
    isParkMyCarState: Boolean,
    onMapLoaded: () -> Unit,
    onParkMyCarState: () -> Unit,
    onSetMyCar: (Car) -> Unit,
    onSpotSelected: (String) -> Unit,
    onOpenOrCloseDrawer: () -> Unit,
    onCameraTilt: () -> Unit,
    onCameraLocation: () -> Unit,
    onCameraMyCar: () -> Unit,
    onGetSpots: () -> Unit,
    modifier: Modifier = Modifier
) {

    var mapType by remember { mutableStateOf(MapType.NORMAL) }

    val extraPadding by animateDpAsState(
        if (!cameraPositionState.isMoving) 60.dp else 85.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )

    Surface(tonalElevation = 2.dp) {

        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {

            GoogleMapContent(
                uiUserState = uiUserState,
                uiSpotsState = uiSpotsState,
                isElapsedTime = uiElapsedTimeState > DEFAULT_ELAPSED_TIME,
                cameraPositionState = cameraPositionState,
                isAddOrParkState = isAddPanelState || isParkMyCarState,
                mapLoaded = onMapLoaded,
                mapType = mapType,
                onSpotSelected = onSpotSelected,
                onSetMyCar = onSetMyCar
            )

            ButtonsMapContent(
                car = uiUserState.user.car,
                isShowReloadButton = !cameraPositionState.isMoving && !uiSpotsState.isLoading,
                onOpenOrCloseDrawer = onOpenOrCloseDrawer,
                onMapType = {
                    mapType =
                        if (mapType == MapType.NORMAL) MapType.SATELLITE else MapType.NORMAL
                },
                onParkMyCarState = onParkMyCarState,
                onCameraTilt = onCameraTilt,
                onCameraLocation = onCameraLocation,
                onCameraMyCar = onCameraMyCar,
                onGetSpots = onGetSpots
            )


//            FIXME THIS

            AnimatedVisibility(visible = uiSpotsState.isLoading || uiUserState.isLoading) {
                CircularProgressIndicator()
            }
//

            AnimatedVisibility(
                visible = uiElapsedTimeState > DEFAULT_ELAPSED_TIME && !cameraPositionState.isMoving,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_spot_marker_stroke),
                    contentDescription = R.drawable.ic_spot_marker_stroke.toString(),
                    modifier = Modifier.padding(bottom = 38.dp),
                    tint = MaterialTheme.colorScheme.inversePrimary
                )
            }

            AnimatedVisibility(visible = isAddPanelState || isParkMyCarState) {

                Image(
                    painter = painterResource(id = if (isAddPanelState) R.drawable.ic_add_spot else R.drawable.ic_park_my_car),
                    contentDescription = if (isAddPanelState) R.drawable.ic_add_spot.toString() else R.drawable.ic_park_my_car.toString(),
                    modifier = modifier
                        .padding(bottom = extraPadding),
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_marker_shadow),
                    contentDescription = "Aparcar tu coche",
                    modifier = modifier
                        .align(Alignment.Center)
                        .padding(bottom = 60.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    UltimateTheme {
        MainContent(
            uiUserState = UserUiState(),
            uiSpotsState = SpotsUiState(),
            uiElapsedTimeState = 0L,
            cameraPositionState = CameraPositionState(),
            isAddPanelState = false,
            isParkMyCarState = false,
            onMapLoaded = { /*TODO*/ },
            onParkMyCarState = { },
            onSetMyCar = {},
            onSpotSelected = {},
            onOpenOrCloseDrawer = { /*TODO*/ },
            onCameraTilt = { /*TODO*/ },
            onCameraLocation = { /*TODO*/ },
            onCameraMyCar = {},
            onGetSpots = { /*TODO*/ })
    }
}