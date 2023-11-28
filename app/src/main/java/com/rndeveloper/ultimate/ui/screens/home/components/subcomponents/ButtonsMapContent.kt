package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PanoramaPhotosphere
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.model.Car
import com.rndeveloper.ultimate.ui.screens.home.HomeUiState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme

@Composable
fun ButtonsMapContent(
    car: Car?,
    isShowReloadButton: Boolean,
    onOpenOrCloseDrawer: () -> Unit,
    onMapType: () -> Unit,
    onCameraTilt: () -> Unit,
    onCameraLocation: () -> Unit,
    onGetSpots: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val surfaceColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(17.dp)
    ) {

        FloatingActionButton(
            modifier = modifier.align(Alignment.TopStart),
            onClick = onOpenOrCloseDrawer,
            containerColor = surfaceColor
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = Icons.Default.Menu.toString(),
            )
        }

        Column(modifier = modifier.align(Alignment.TopEnd)) {
            FloatingActionButton(
                onClick = onMapType,
                containerColor = surfaceColor
            ) {
                Icon(
                    imageVector = Icons.Default.Terrain,
                    contentDescription = Icons.Default.Terrain.toString(),
                )
            }
            Spacer(modifier = modifier.height(12.dp))
            FloatingActionButton(
                onClick = onCameraTilt,
                containerColor = surfaceColor
            ) {
                Icon(
                    imageVector = Icons.Default.PanoramaPhotosphere,
                    contentDescription = Icons.Default.PanoramaPhotosphere.toString(),
                )
            }
        }

        FloatingActionButton(
            onClick = {},
            modifier = Modifier.align(Alignment.BottomStart),

//            FIXME this -----------------
            containerColor = MaterialTheme.colorScheme.tertiary

        ) {
            Icon(
                imageVector = Icons.Default.Slideshow,
                contentDescription = Icons.Default.Slideshow.toString(),
            )
        }

        AnimatedVisibility(
            visible = isShowReloadButton,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {

            FloatingActionButton(
                modifier = modifier.size(40.dp),
                onClick = onGetSpots
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = Icons.Default.Refresh.toString(),
                )
            }
        }


        //        FIXME: CarLoc FloatingButtons
        Column(
            modifier = modifier.align(Alignment.BottomEnd),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FloatingActionButton(
                modifier = modifier.size(40.dp),
                onClick = { },
                containerColor = surfaceColor

            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_carloc_userloc),
                    contentDescription = "Ir a Ubicación y coche"
                )
            }
            Spacer(modifier = modifier.height(4.dp))
            Row {
                FloatingCar(
                    car = car,
                    onShowCarMarker = {},
                    onCameraCar = {},
                    deleteMyCar = {}
                )
                Spacer(modifier = modifier.width(8.dp))
                FloatingActionButton(
                    modifier = modifier.size(40.dp),
                    onClick = onCameraLocation,
                    containerColor = surfaceColor

                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = Icons.Default.GpsFixed.toString(),
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingCar(
    car: Car?,
    onShowCarMarker: () -> Unit,
    onCameraCar: () -> Unit,
    deleteMyCar: () -> Unit,
    modifier: Modifier = Modifier
) {

    val surfaceColor = MaterialTheme.colorScheme.surface


    AnimatedVisibility(visible = car != null) {
        FloatingActionButton(
            modifier = modifier.height(40.dp),
            onClick = onShowCarMarker,
            containerColor = surfaceColor

        ) {
            Text(
                text = "Cambiar mi coche",
                modifier = modifier
                    .padding(8.dp)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal
                )
            )
        }
        Spacer(modifier = modifier.width(8.dp))

    }
    FloatingActionButton(
        modifier = if (car != null /*|| !isCarLatLng*/) modifier.height(40.dp) else modifier.size(40.dp),
        onClick = if (car == null) onCameraCar else onShowCarMarker,
        containerColor = surfaceColor
    ) {
        Row(
            modifier = modifier
                .padding(8.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(visible = car != null) {
                Row {
                    Text(
                        text = if (car != null) "¡Me voy!"  else "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal,
                        )
                    )
                    Spacer(modifier = modifier.width(2.dp))
                }
            }

            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = "Coche",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonsMapContentPreview() {
    UltimateTheme {
        ButtonsMapContent(
            car = HomeUiState().user?.car,
            isShowReloadButton = true,
            onOpenOrCloseDrawer = { /*TODO*/ },
            onMapType = { /*TODO*/ },
            onCameraTilt = { /*TODO*/ },
            onCameraLocation = { /*TODO*/ },
            onGetSpots = { /*TODO*/ })
    }
}