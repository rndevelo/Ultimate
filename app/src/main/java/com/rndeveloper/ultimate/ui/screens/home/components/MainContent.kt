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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    onSpot: (String) -> Unit,
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

    Surface(tonalElevation = 2.dp) {

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
                onMapLoaded = onCameraLoc,
                isElapsedTime = uiElapsedTimeState > DEFAULT_ELAPSED_TIME,
                mapType = mapType,
                onSpot = onSpot,
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

//            AnimatedVisibility(
//                visible = uiElapsedTimeState > DEFAULT_ELAPSED_TIME && !rememberHomeUiContainerState.camPosState.isMoving,
//                enter = fadeIn(),
//                exit = fadeOut()
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_spot_marker_stroke),
//                    contentDescription = R.drawable.ic_spot_marker_stroke.toString(),
//                    modifier = Modifier.padding(bottom = 38.dp),
//                    tint = MaterialTheme.colorScheme.inversePrimary
//                )
//            }

            AnimatedVisibility(visible = rememberHomeUiContainerState.isSetState) {

                Image(
                    painter = painterResource(
                        id = when (rememberHomeUiContainerState.screenState) {
                            ScreenState.ADDSPOT -> R.drawable.ic_add_spot
                            ScreenState.PARKMYCAR -> R.drawable.ic_park_my_car
                            ScreenState.MAIN -> R.drawable.ic_nothing
                        }
                    ),
                    contentDescription = when (rememberHomeUiContainerState.screenState) {
                        ScreenState.ADDSPOT -> R.drawable.ic_add_spot.toString()
                        ScreenState.PARKMYCAR -> R.drawable.ic_park_my_car.toString()
                        ScreenState.MAIN -> R.drawable.ic_nothing.toString()
                    },
                    modifier = modifier.padding(bottom = extraPadding),
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
            onSpot = {},
        )
    }
}