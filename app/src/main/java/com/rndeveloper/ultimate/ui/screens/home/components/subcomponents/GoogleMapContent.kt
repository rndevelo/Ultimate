package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.screens.home.HomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.rememberHomeUiContainerState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.ui.theme.circle_green
import com.rndeveloper.ultimate.utils.BitmapHelper
import com.rndeveloper.ultimate.utils.MapStyle

@Composable
fun GoogleMapContent(
    rememberHomeUiContainerState: HomeUiContainerState,
    camPosState: CameraPositionState,
    car: Position?,
    spots: List<Spot>,
    onMapLoaded: () -> Unit,
    isElapsedTime: Boolean,
    mapType: MapType,
    onSpot: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    val mapStyle = if (isSystemInDarkTheme()) {
        MapStyleOptions(MapStyle.jsonMapDarkMode)
    } else {
        MapStyleOptions(MapStyle.jsonWithoutPoi)
    }

////    val spotsList by remember { mutableStateOf(spots) }
//
//    val aa = cameraPos.projection?.toScreenLocation(cameraPos.position.target)
//
//    Log.d(
//        "DEBUG MAP",
//        "GoogleMapContent: ${cameraPos.projection?.toScreenLocation(cameraPos.position.target)}"
//    )

    GoogleMap(
        modifier = modifier.clip(RoundedCornerShape(bottomStartPercent = 5, bottomEndPercent = 5)),
        cameraPositionState = camPosState,
        properties = MapProperties(
            isMyLocationEnabled = true,
            mapType = mapType,
            mapStyleOptions = mapStyle
        ),
        uiSettings = MapUiSettings(
            compassEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false,
        ),
        onMapLoaded = onMapLoaded,
    ) {

        if (car != null) {
            Marker(
                state = MarkerState(position = LatLng(car.lat, car.lng)),
                alpha = if (rememberHomeUiContainerState.isSetState) 0.4f else 1.0f,
                icon = BitmapHelper.vectorToBitmap(
                    context = context,
                    id = R.drawable.ic_park_my_car_shadow
                ),
                title = stringResource(R.string.home_text_your_parked_car)
            )
        }

        spots.forEach { spot ->

            Circle(
                center = LatLng(spot.position.lat, spot.position.lng),
                fillColor = circle_green,
                radius = 50.0,
                strokeColor = Color.Transparent,
                tag = spot.tag,
                onClick = { circle ->
                    onSpot(circle.tag as String)
                }
            )

            Marker(
                state = MarkerState(position = LatLng(spot.position.lat, spot.position.lng)),
                tag = spot.tag,
                icon = BitmapHelper.vectorToBitmap(
                    context = context,
                    id = R.drawable.ic_spot_marker
                ),
//                visible = isElapsedTime,
                onClick = { marker ->
//                    onSpotSelected(marker.tag as String)
                    true
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GoogleMapContentPreview() {
    UltimateTheme {
        GoogleMapContent(
            rememberHomeUiContainerState = rememberHomeUiContainerState(),
            camPosState = rememberCameraPositionState(),
            car = Position(),
            spots = emptyList(),
            onMapLoaded = {},
            isElapsedTime = false,
            mapType = MapType.NORMAL,
            onSpot = {},
        )
    }
}