package com.rndeveloper.ultimate.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Properties

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val uiHomeState by homeViewModel.uiHomeState.collectAsStateWithLifecycle()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 0f)
    }

    uiHomeState.loc?.let { initLatLng ->
        LaunchedEffect(key1 = initLatLng, block = {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(initLatLng, 15f)
                )
            )
        })
    }
    HomeContent(cameraPositionState)

}

@Composable
private fun HomeContent(cameraPositionState: CameraPositionState) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        /*.clip(
            RoundedCornerShape(bottomStartPercent = 6, bottomEndPercent = 6)
        )*/
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true),
//        onMapLoaded = mapLoaded,
//        onMapClick = { if (!cameraPositionState.isMoving) hideList() },
//        onMapLongClick = { if (!cameraPositionState.isMoving) onShowPanel() },
    ) {
//        carPosition?.let {
//            Marker(
//                state = MarkerState(position = it),
//                icon = BitmapHelper.vectorToBitmap(
//                    context = LocalContext.current,
//                    id = R.drawable.ic_marcador_mi_coche
//                ),
//                alpha = if (isCarMarkerShow || isPanelShow || isListShow) 0.2f else 1f,
//                snippet = "Pulsa aquí para llegar a tu vehículo",
//                title = "Tú vehículo",
//                onInfoWindowClick = { marker ->
//                    onNavigateMyCar(
//                        LatLng(marker.position.latitude, marker.position.longitude),
//                        context
//                    )
//                }
//            )
//        }
//        spotsList.forEach { spot ->
//            ShowItems(
//                spot = spot,
//                onSpotSelected = onSpotSelected,
//                onChangeLatLngCamera = onChangeLatLngCamera
//            )
//        }

//        selectedSpot?.let {
//            Circle(circleLatLng = LatLng(it.lat, it.lng))
//        }
    }
}