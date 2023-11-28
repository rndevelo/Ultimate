package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.ui.screens.home.HomeUiState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.BitmapHelper
import com.rndeveloper.ultimate.utils.MapStyle

@Composable
fun GoogleMapContent(
    homeUiState: HomeUiState,
    cameraPositionState: CameraPositionState,
    mapLoaded: () -> Unit,
    mapType: MapType,
    onSpotSelected: (tag: String) -> Unit,
    onSetMyCar: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    val mapStyle = if (isSystemInDarkTheme()) {
        MapStyleOptions(MapStyle.jsonMapDarkMode)
    } else {
        MapStyleOptions(MapStyle.jsonWithoutPoi)
    }

    GoogleMap(
        modifier = modifier.clip(RoundedCornerShape(bottomStartPercent = 5, bottomEndPercent = 5)),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true,
            mapType = mapType,
            mapStyleOptions = mapStyle
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false,
            compassEnabled = false
        ),
        onMapLoaded = mapLoaded,
        onMapLongClick = { onSetMyCar(it) }
    ) {

        homeUiState.user?.car?.let {
            Marker(
                state = MarkerState(position = LatLng(it.lat, it.lng)),
                anchor = Offset(0.5f, 0.5f),
                icon = BitmapHelper.vectorToBitmap(
                    context = context,
                    id = R.drawable.ic_add_my_car
                ),
                title = "Your parked car"
            )
        }

        homeUiState.spots.forEach { spot ->
            Marker(
                state = MarkerState(position = LatLng(spot.position.lat, spot.position.lng)),
                anchor = Offset(0.5f, 0.5f),
                tag = spot.tag,
                icon = BitmapHelper.vectorToBitmap(
                    context = context,
                    id = R.drawable.ic_marker
                ),
                visible = homeUiState.elapsedTime > 0L,
                onClick = { marker ->
                    onSpotSelected(marker.tag as String)
                    true
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoogleMapContentPreview() {
    UltimateTheme {
        GoogleMapContent(
            homeUiState = HomeUiState(),
            cameraPositionState = CameraPositionState(),
            mapLoaded = { /*TODO*/ },
            mapType = MapType.HYBRID,
            onSpotSelected = {},
            onSetMyCar = {}
        )
    }
}