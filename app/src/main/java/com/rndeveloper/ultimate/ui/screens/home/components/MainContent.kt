package com.rndeveloper.ultimate.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapType
import com.rndeveloper.ultimate.ui.screens.home.HomeUiState
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.ButtonsMapContent
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.GoogleMapContent

@Composable
fun MainContent(
    homeUiState: HomeUiState,
    cameraPositionState: CameraPositionState,
    onMapLoaded: () -> Unit,
    setSpot: (LatLng) -> Unit,
    onSetMyCar: (LatLng) -> Unit,
    isAddPanelState: Boolean,
    onSpotSelected: (String) -> Unit,
    onOpenOrCloseDrawer: () -> Unit,
    onCameraTilt: () -> Unit,
    onScreenState: () -> Unit,
    onCameraLocation: () -> Unit,
    onGetSpots: () -> Unit,
    modifier: Modifier = Modifier
) {

    var mapType by remember { mutableStateOf(MapType.NORMAL) }

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
                setSpot = setSpot,
                onSetMyCar = onSetMyCar
            )
            AnimatedVisibility(visible = !isAddPanelState) {
                ButtonsMapContent(
                    isShowReloadButton = !cameraPositionState.isMoving && !homeUiState.isLoading,
                    onOpenOrCloseDrawer = onOpenOrCloseDrawer,
                    onMapType = {
                        mapType =
                            if (mapType == MapType.NORMAL) MapType.SATELLITE else MapType.NORMAL
                    },
                    onCameraTilt = onCameraTilt,
                    onCameraLocation = onCameraLocation,
                    onGetSpots  = onGetSpots
                )
            }

            Icon(
                imageVector = Icons.Default.BlurCircular,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            AnimatedVisibility(visible = homeUiState.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}