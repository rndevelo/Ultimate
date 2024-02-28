package com.rndeveloper.ultimate.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapType
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.ui.screens.home.HomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.ScreenState
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.ButtonsMapContent
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.GoogleMapContent
import com.rndeveloper.ultimate.ui.screens.home.rememberHomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.uistates.AreasUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.SpotsUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.UserUiState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.Constants.DEFAULT_ELAPSED_TIME
import kotlinx.coroutines.launch

@Composable
fun MainContent(
    rememberHomeUiContainerState: HomeUiContainerState,
    camPosState: CameraPositionState,
    uiUserState: UserUiState,
    uiSpotsState: SpotsUiState,
    uiAreasState: AreasUiState,
    uiElapsedTimeState: Long,
    onCameraLoc: () -> Unit,
    onCameraCar: () -> Unit,
    onCameraCarLoc: () -> Unit,
    onCameraTilt: () -> Unit,
    onSelectSpot: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var mapType by rememberSaveable { mutableStateOf(MapType.NORMAL) }

    val extraPadding by animateDpAsState(
        if (rememberHomeUiContainerState.screenState == ScreenState.ADDSPOT && !camPosState.isMoving
            || rememberHomeUiContainerState.screenState == ScreenState.PARKMYCAR && !camPosState.isMoving
        ) 60.dp else 85.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )

    Surface(tonalElevation = 3.dp) {

        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {

            GoogleMapContent(
                rememberHomeUiContainerState = rememberHomeUiContainerState,
                camPosState = camPosState,
                car = uiUserState.user.car,
                spots = uiSpotsState.spots,
                areas = uiAreasState.areas,
                onMapLoaded = {},
                isElapsedTime = uiElapsedTimeState > DEFAULT_ELAPSED_TIME,
                mapType = mapType,
                onSelectSpot = onSelectSpot,
            )

            ButtonsMapContent(
                car = uiUserState.user.car,
                isShowLoading = uiSpotsState.isLoading || uiUserState.isLoading,
                onOpenOrCloseDrawer = { rememberHomeUiContainerState.onOpenDrawer() },
                onMapType = {
                    mapType =
                        if (mapType == MapType.NORMAL) MapType.SATELLITE else MapType.NORMAL
                },
                onCameraTilt = onCameraTilt,
                onCameraLocation = onCameraLoc,
                onCameraMyCar = onCameraCar,
                onCameraCarLoc = onCameraCarLoc,
            )


            Image(
                painter = painterResource(
                    id = when (rememberHomeUiContainerState.screenState) {
                        ScreenState.ADDSPOT -> R.drawable.ic_add_spot
                        ScreenState.PARKMYCAR -> R.drawable.ic_park_my_car
                        ScreenState.MAIN -> R.drawable.ic_circle
                    }
                ),
                contentDescription = when (rememberHomeUiContainerState.screenState) {
                    ScreenState.ADDSPOT -> R.drawable.ic_add_spot.toString()
                    ScreenState.PARKMYCAR -> R.drawable.ic_park_my_car.toString()
                    ScreenState.MAIN -> R.drawable.ic_circle.toString()
                },
                modifier = if (rememberHomeUiContainerState.isSetState) modifier.padding(bottom = extraPadding) else modifier,
            )
            AnimatedVisibility(visible = rememberHomeUiContainerState.isSetState) {

                Image(
                    painter = painterResource(id = R.drawable.ic_marker_shadow),
                    contentDescription = R.drawable.ic_marker_shadow.toString(),
                    modifier = modifier
                        .align(Alignment.Center)
                        .padding(bottom = 60.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    UltimateTheme {
        MainContent(
            rememberHomeUiContainerState = rememberHomeUiContainerState(),
            camPosState = CameraPositionState(),
            uiUserState = UserUiState(),
            uiSpotsState = SpotsUiState(),
            uiAreasState = AreasUiState(),
            uiElapsedTimeState = 0L,
            onCameraLoc = {},
            onCameraCar = {},
            onCameraCarLoc = {},
            onCameraTilt = {},
            onSelectSpot = {},
        )
    }
}