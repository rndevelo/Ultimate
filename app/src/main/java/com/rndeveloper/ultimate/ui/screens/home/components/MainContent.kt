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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.google.maps.android.compose.MapType
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.ui.screens.home.HomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.LocationUiState
import com.rndeveloper.ultimate.ui.screens.home.ScreenState
import com.rndeveloper.ultimate.ui.screens.home.SpotsUiState
import com.rndeveloper.ultimate.ui.screens.home.UserUiState
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.ButtonsMapContent
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.GoogleMapContent
import com.rndeveloper.ultimate.ui.screens.home.rememberHomeUiContainerState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.Constants.DEFAULT_ELAPSED_TIME

@Composable
fun MainContent(
    rememberHomeUiContainerState: HomeUiContainerState,
    uiLocState: LocationUiState,
    uiUserState: UserUiState,
    uiSpotsState: SpotsUiState,
    uiElapsedTimeState: Long,
    onSpotSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var mapType by rememberSaveable { mutableStateOf(MapType.NORMAL) }

    val extraPadding by animateDpAsState(
        if (!rememberHomeUiContainerState.camPosState.isMoving) 60.dp else 85.dp,
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
                cameraPos = rememberHomeUiContainerState.camPosState,
                isSetState = rememberHomeUiContainerState.isSetState,
                car = uiUserState.user.car,
                spots = uiSpotsState.spots,
                isElapsedTime = uiElapsedTimeState > DEFAULT_ELAPSED_TIME,
                mapType = mapType,
                onSpotSelected = onSpotSelected,
            )

            ButtonsMapContent(
                car = uiUserState.user.car,
                isShowReloadButton = !rememberHomeUiContainerState.camPosState.isMoving && !uiSpotsState.isLoading && uiElapsedTimeState > DEFAULT_ELAPSED_TIME,
                onOpenOrCloseDrawer = { rememberHomeUiContainerState.onOpenDrawer() },
                onMapType = {
                    mapType =
                        if (mapType == MapType.NORMAL) MapType.SATELLITE else MapType.NORMAL
                },
                onParkMyCarState = { rememberHomeUiContainerState.onScreenState(ScreenState.PARKMYCAR) },
                onCameraTilt = { rememberHomeUiContainerState.onCameraTilt() },
                onCameraLocation = {
                    rememberHomeUiContainerState.onCamera(
                        target = uiLocState.location,
                        zoom = 16f,
                        tilt = 0f,
                        bearing = 0f
                    )
                },
                onCameraMyCar = {
                    rememberHomeUiContainerState.onCamera(
                        target = uiUserState.user.car,
                        zoom = 16f,
                        tilt = 0f,
                        bearing = 0f
                    )
                },
            )


//            FIXME THIS

            AnimatedVisibility(visible = uiSpotsState.isLoading || uiUserState.isLoading) {
                CircularProgressIndicator()
            }
//
            AnimatedVisibility(
                visible = uiElapsedTimeState > DEFAULT_ELAPSED_TIME && !rememberHomeUiContainerState.camPosState.isMoving,
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

            AnimatedVisibility(visible = rememberHomeUiContainerState.isSetState) {

                Image(
                    painter = painterResource(id = if (rememberHomeUiContainerState.screenState == ScreenState.ADDSPOT) R.drawable.ic_add_spot else R.drawable.ic_park_my_car),
                    contentDescription = if (rememberHomeUiContainerState.screenState == ScreenState.ADDSPOT) R.drawable.ic_add_spot.toString() else R.drawable.ic_park_my_car.toString(),
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    UltimateTheme {
        MainContent(
            rememberHomeUiContainerState = rememberHomeUiContainerState(),
            uiLocState = LocationUiState(),
            uiUserState = UserUiState(),
            uiSpotsState = SpotsUiState(),
            uiElapsedTimeState = 0L,
            onSpotSelected = {},
        )
    }
}