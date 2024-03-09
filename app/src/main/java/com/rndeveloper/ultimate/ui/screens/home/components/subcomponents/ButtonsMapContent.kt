package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.model.Position
import com.rndeveloper.ultimate.ui.screens.home.HomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.ScreenState
import com.rndeveloper.ultimate.ui.screens.home.rememberHomeUiContainerState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.Constants

@Composable
fun ButtonsMapContent(
    rememberHomeUiContainerState: HomeUiContainerState,
    uiElapsedTimeState: Long,
    car: Position?,
    isShowLoading: Boolean,
    onOpenOrCloseDrawer: () -> Unit,
    onMapType: () -> Unit,
    onCameraTilt: () -> Unit,
    onCameraLocation: () -> Unit,
    onCameraMyCar: () -> Unit,
    onCameraCarLoc: () -> Unit,
    showRewardedAdmob: () -> Unit,
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

//        AnimatedVisibility(
//            visible = isShowLoading,
//            modifier = modifier.align(Alignment.TopCenter),
//        ) {
//            LoadingAnimation()
//        }

        AnimatedVisibility(
            visible = true,
            modifier = modifier.align(Alignment.TopCenter),
        ) {
            Card(modifier = Modifier.padding(3.dp)) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = Icons.Default.Visibility.toString()
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = DateUtils.formatElapsedTime(uiElapsedTimeState.div(1000)))
                }

            }
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

        //        Admob button
        ExtendedFloatingActionButton(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Show add")
                    Spacer(modifier = modifier.width(5.dp))
                    Text(
                        text = "(+1cred.)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light),
                        modifier = Modifier.height(16.dp)
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Slideshow,
                    contentDescription = Icons.Default.Slideshow.toString()
                )
            },
            onClick = {
                if (rememberHomeUiContainerState.isExpandedAdmobButton) {
                    rememberHomeUiContainerState.onExpandedAdmobButton(false)
                    showRewardedAdmob()
                } else {
                    rememberHomeUiContainerState.onExpandedAdmobButton(true)
                }
            },
            modifier = Modifier.align(Alignment.BottomStart),
            expanded = rememberHomeUiContainerState.isExpandedAdmobButton,
            containerColor = MaterialTheme.colorScheme.tertiary,
        )

//        FloatingActionButton(
//            onClick = {
//                if (isExpandedAdmobButton)
//                    showRewardedAdmob()
//            },
//            modifier = Modifier.align(Alignment.BottomStart),
//            containerColor = MaterialTheme.colorScheme.tertiary
//
//        ) {
//            Row(modifier = modifier.padding(7.dp)) {
//
//                Icon(
//                    imageVector = Icons.Default.Slideshow,
//                    contentDescription = Icons.Default.Slideshow.toString(),
//                )
//                AnimatedVisibility(visible = isExpandedAdmobButton) {
//                    Spacer(modifier = modifier.width(5.dp))
//                    Text(
//                        text = "(+1cred.)",
//                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light)
//                    )
//                }
//
//            }
//
//        }

        Column(
            modifier = modifier.align(Alignment.BottomEnd),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FloatingActionButton(
                modifier = modifier.size(40.dp),
                onClick = onCameraCarLoc,
                containerColor = surfaceColor

            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_carloc_userloc),
                    contentDescription = "Ir a Ubicación y coche"
                )
            }
            Spacer(modifier = modifier.height(4.dp))
            Row {
//                FloatingCar(
//                    car = car,
//                    onShowCarMarker = {},
//                    onCameraCar = {},
//                    deleteMyCar = {}
//                )
                FloatingActionButton(
                    modifier = modifier.size(40.dp),
                    onClick = onCameraMyCar,
                    containerColor = surfaceColor
                ) {
                    Row {
                        AnimatedVisibility(visible = car == null) {
                            Text(
                                text = stringResource(R.string.home_text_not_park_car),
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = Icons.Default.DirectionsCar.toString(),
                        )
                    }
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ButtonsMapContentPreview() {
    UltimateTheme {
        ButtonsMapContent(
            rememberHomeUiContainerState = rememberHomeUiContainerState(),
            uiElapsedTimeState = 0L,
            car = Position(),
            isShowLoading = true,
            onOpenOrCloseDrawer = { /*TODO*/ },
            onMapType = { /*TODO*/ },
            onCameraTilt = { /*TODO*/ },
            onCameraLocation = { /*TODO*/ },
            onCameraMyCar = {},
            onCameraCarLoc = { },
            showRewardedAdmob = {},
        )
    }
}