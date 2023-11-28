package com.rndeveloper.ultimate.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BlurCircular
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
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapType
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.ui.screens.home.HomeUiState
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.ButtonsMapContent
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.GoogleMapContent
import com.rndeveloper.ultimate.ui.theme.UltimateTheme

@Composable
fun MainContent(
    homeUiState: HomeUiState,
    cameraPositionState: CameraPositionState,
    onMapLoaded: () -> Unit,
    onSetMyCar: (LatLng) -> Unit,
    isAddPanelState: Boolean,
    onSpotSelected: (String) -> Unit,
    onOpenOrCloseDrawer: () -> Unit,
    onCameraTilt: () -> Unit,
    onCameraLocation: () -> Unit,
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
                homeUiState = homeUiState,
                cameraPositionState = cameraPositionState,
                mapLoaded = onMapLoaded,
                mapType = mapType,
                onSpotSelected = onSpotSelected,
                onSetMyCar = onSetMyCar
            )

            ButtonsMapContent(
                car = homeUiState.user?.car,
                isShowReloadButton = !cameraPositionState.isMoving && !homeUiState.isLoading,
                onOpenOrCloseDrawer = onOpenOrCloseDrawer,
                onMapType = {
                    mapType =
                        if (mapType == MapType.NORMAL) MapType.SATELLITE else MapType.NORMAL
                },
                onCameraTilt = onCameraTilt,
                onCameraLocation = onCameraLocation,
                onGetSpots = onGetSpots
            )

            AnimatedVisibility(visible = homeUiState.isLoading) {
                CircularProgressIndicator()
            }

            AnimatedVisibility(visible = homeUiState.elapsedTime > 0L) {
                Icon(
                    imageVector = Icons.Default.BlurCircular,
                    contentDescription = Icons.Default.BlurCircular.toString(),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = isAddPanelState) {

                Image(
                    painter = painterResource(id = R.drawable.ic_add_spot),
                    contentDescription = "Aparcar tu coche",
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
            homeUiState = HomeUiState(),
            cameraPositionState = CameraPositionState(),
            onMapLoaded = { /*TODO*/ },
            onSetMyCar = {},
            isAddPanelState = false,
            onSpotSelected = {},
            onOpenOrCloseDrawer = { /*TODO*/ },
            onCameraTilt = { /*TODO*/ },
            onCameraLocation = { /*TODO*/ },
            onGetSpots = { /*TODO*/ })
    }
}