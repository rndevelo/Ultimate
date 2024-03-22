package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Start
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.backend.RouteResponse
import com.rndeveloper.ultimate.extensions.onNavigate
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.screens.home.HomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.rememberHomeUiContainerState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.BitmapHelper
import com.rndeveloper.ultimate.utils.MapStyle

@Composable
fun GoogleMapContent(
    rememberHomeUiContainerState: HomeUiContainerState,
    camPosState: CameraPositionState,
    car: Position?,
    spots: List<Spot>,
    areas: List<Spot>,
    onMapLoaded: () -> Unit,
    isElapsedTime: Boolean,
    mapType: MapType,
    onSelectSpot: (String) -> Unit,
    uiRouteState: List<LatLng>,
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
        onMapClick = { rememberHomeUiContainerState.onExpandedAdmobButton(false) }
    ) {

        if (car != null) {
            Marker(
                state = MarkerState(position = LatLng(car.lat, car.lng)),
                alpha = if (rememberHomeUiContainerState.isSetState || isElapsedTime) 0.4f else 1.0f,
                icon = BitmapHelper.vectorToBitmap(
                    context = context,
                    id = R.drawable.ic_park_my_car_shadow
                ),
                title = stringResource(R.string.home_text_your_parked_car),
                snippet = stringResource(R.string.home_text_navigate_my_car),
                onInfoWindowClick = { marker ->
                    onNavigate(
                        context,
                        LatLng(marker.position.latitude, marker.position.longitude),
                    )
                }
            )
        }

        spots.forEach { spot ->
            Marker(
                state = MarkerState(position = LatLng(spot.position.lat, spot.position.lng)),
                tag = spot.tag,
                icon = spot.icon,
                visible = isElapsedTime,
                onClick = { marker ->
                    onSelectSpot(marker.tag as String)
                    true
                }
            )
        }

        areas.forEach { area ->
            Circle(
                center = LatLng(area.position.lat, area.position.lng),
                fillColor = area.color,
                radius = 50.0,
                strokeColor = Color.Transparent,
                tag = area.tag,
            )
        }

        Polyline(
            points = uiRouteState,
            color = MaterialTheme.colorScheme.primary,
            endCap = RoundCap(),
            startCap = RoundCap(),
            width = 17f
        )
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
            areas = emptyList(),
            onMapLoaded = {},
            isElapsedTime = false,
            mapType = MapType.NORMAL,
            onSelectSpot = {},
            uiRouteState = emptyList(),
        )
    }
}