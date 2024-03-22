package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

        AnimatedVisibility(
            visible = isShowLoading,
            modifier = modifier.align(Alignment.TopCenter),
        ) {
            LoadingAnimation()
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
                    Text(text = stringResource(R.string.home_text_show_add))
                    Spacer(modifier = modifier.width(5.dp))
                    Text(
                        text = stringResource(R.string.home_text_more_1creds),
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


        Card(
            modifier = Modifier.align(Alignment.BottomCenter),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(5.dp),
            ) {
                Icon(
                    imageVector = if (uiElapsedTimeState > Constants.DEFAULT_ELAPSED_TIME) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = Icons.Default.Visibility.toString()
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = if (uiElapsedTimeState > Constants.DEFAULT_ELAPSED_TIME) DateUtils.formatElapsedTime(
                        uiElapsedTimeState.div(1000)
                    ) else "05:00"
                )
            }
        }

        LocCarButtonsContent(
            rememberHomeUiContainerState = rememberHomeUiContainerState,
            surfaceColor = surfaceColor,
            car = car,
            onCameraCarLoc = onCameraCarLoc,
            onCameraMyCar = onCameraMyCar,
            onCameraLocation = onCameraLocation,
            modifier = modifier.align(Alignment.BottomEnd),
        )
    }
}

@Composable
private fun LocCarButtonsContent(
    rememberHomeUiContainerState: HomeUiContainerState,
    surfaceColor: Color,
    car: Position?,
    onCameraCarLoc: () -> Unit,
    onCameraMyCar: () -> Unit,
    onCameraLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FloatingActionButton(
            modifier = modifier.size(40.dp),
            onClick = onCameraCarLoc,
            containerColor = surfaceColor

        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_carloc_userloc),
                contentDescription = R.drawable.ic_carloc_userloc.toString()
            )
        }
        Spacer(modifier = modifier.height(4.dp))
        Row {
            FloatingActionButton(
                modifier = modifier.size(40.dp),
                onClick = {
                    if (car != null) onCameraMyCar() else rememberHomeUiContainerState.onScreenState(
                        ScreenState.PARKMYCAR
                    )
                },
                containerColor = surfaceColor
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = Icons.Default.DirectionsCar.toString(),
                    tint = if (car != null) MaterialTheme.colorScheme.onSurface else Color.LightGray
                )
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