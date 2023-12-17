package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.google.maps.android.compose.rememberCameraPositionState
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.BitmapHelper
import com.rndeveloper.ultimate.utils.MapStyle

@Composable
fun GoogleMapContent(
    cameraPos: CameraPositionState,
    isSetState: Boolean,
    car: Position?,
    spots: List<Spot>,
    isElapsedTime: Boolean,
    mapType: MapType,
    onSpotSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Log.d("MY CAR", "GoogleMapContent: ")

    val context = LocalContext.current

    val mapStyle = if (isSystemInDarkTheme()) {
        MapStyleOptions(MapStyle.jsonMapDarkMode)
    } else {
        MapStyleOptions(MapStyle.jsonWithoutPoi)
    }

    Log.d("BUCLEMIO", "car: $car")


    GoogleMap(
        modifier = modifier.clip(RoundedCornerShape(bottomStartPercent = 5, bottomEndPercent = 5)),
        cameraPositionState = cameraPos,
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
        onMapLoaded = {},
    ) {


        if (car != null) {
            Marker(
                state = MarkerState(position = LatLng(car.lat, car.lng)),
                alpha = if (isSetState) 0.4f else 1.0f,
                icon = BitmapHelper.vectorToBitmap(
                    context = context,
                    id = R.drawable.ic_park_my_car_shadow
                ),
                title = stringResource(R.string.home_text_your_parked_car)
            )
        }

        Log.d("BUCLEMIO", "GoogleMapContent -------------------: ")

        spots.forEach { spot ->
            Marker(
                state = MarkerState(position = LatLng(spot.position.lat, spot.position.lng)),
                tag = spot.tag,
                icon = BitmapHelper.vectorToBitmap(
                    context = context,
                    id = R.drawable.ic_spot_marker
                ),
//                visible = isElapsedTime,
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
            cameraPos = rememberCameraPositionState(),
            isSetState = false,
            car = Position(),
            spots = emptyList(),
            isElapsedTime = false,
            mapType = MapType.NORMAL,
            onSpotSelected = {},
        )
    }
}