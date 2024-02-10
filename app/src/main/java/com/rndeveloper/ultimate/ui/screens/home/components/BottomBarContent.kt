package com.rndeveloper.ultimate.ui.screens.home.components

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.ui.screens.home.HomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.ScreenState
import com.rndeveloper.ultimate.ui.screens.home.rememberHomeUiContainerState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.Constants
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarContent(
    rememberHomeUiContainerState: HomeUiContainerState,
    uiElapsedTimeState: Long,
    onStartTimer: () -> Unit,
    onCameraZoom: (Float) -> Unit,
    onSet: (onMain: () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    Surface(tonalElevation = 3.dp) {
        Row(
            modifier = modifier
                .height(85.dp)
                .fillMaxWidth()
                .padding(7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExtendedFloatingActionButton(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(R.string.home_text_add_spot))
                        Spacer(modifier = modifier.width(5.dp))
                        Text(
                            text = stringResource(R.string.home_text_4cred),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light)
                        )
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AddLocationAlt,
                        contentDescription = Icons.Default.AddLocationAlt.toString(),
                    )
                },
                onClick = {
                    when (rememberHomeUiContainerState.screenState) {
                        ScreenState.ADDSPOT -> {
                            onSet {
                                onCameraZoom(16f)
                                rememberHomeUiContainerState.onScreenState(ScreenState.MAIN)
                            }
                        }

                        else -> {
                            scope.launch {
                                rememberHomeUiContainerState.bsScaffoldState.bottomSheetState.expand()
                            }
                            onCameraZoom(17f)
                            rememberHomeUiContainerState.onScreenState(ScreenState.ADDSPOT)
                        }
                    }
                },
                modifier = when (rememberHomeUiContainerState.screenState) {
                    ScreenState.ADDSPOT -> modifier.weight(1f)
                    else -> modifier
                },
                expanded = rememberHomeUiContainerState.screenState == ScreenState.ADDSPOT,
                containerColor = when (rememberHomeUiContainerState.screenState) {
                    ScreenState.ADDSPOT -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.secondaryContainer
                },
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            )

            ExtendedFloatingActionButton(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(R.string.home_text_show_spots))
                        Spacer(modifier = modifier.width(5.dp))
                        Text(
                            text = stringResource(R.string.home_text_2creds),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light)
                        )
                    }
                },
                icon = {
                    if (uiElapsedTimeState <= Constants.DEFAULT_ELAPSED_TIME) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = Icons.Default.Visibility.toString()
                        )
                    } else {
                        Text(text = DateUtils.formatElapsedTime(uiElapsedTimeState.div(1000)))
                    }
                },
                onClick = {
                    when (rememberHomeUiContainerState.screenState) {
                        ScreenState.MAIN -> {
                            onStartTimer()
                        }

                        else -> {
                            onCameraZoom(16f)
                            rememberHomeUiContainerState.onScreenState(ScreenState.MAIN)
                        }
                    }
                },
                modifier = when {
                    rememberHomeUiContainerState.screenState == ScreenState.MAIN && Constants.DEFAULT_ELAPSED_TIME <= uiElapsedTimeState -> modifier
                        .padding(7.dp)
                        .weight(1f)

                    else -> modifier.padding(7.dp)
                },
                expanded = rememberHomeUiContainerState.screenState == ScreenState.MAIN && uiElapsedTimeState <= Constants.DEFAULT_ELAPSED_TIME,
                containerColor = when (rememberHomeUiContainerState.screenState) {
                    ScreenState.MAIN -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.secondaryContainer
                },
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            )

            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(R.string.home_text_park_your_car)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = Icons.Default.DirectionsCar.toString(),
                    )
                },
                onClick = {
                    when (rememberHomeUiContainerState.screenState) {
                        ScreenState.PARKMYCAR -> {
                            onSet {
                                onCameraZoom(16f)
                                rememberHomeUiContainerState.onScreenState(ScreenState.MAIN)
                            }
                        }

                        else -> {
                            onCameraZoom(17f)
                            rememberHomeUiContainerState.onScreenState(ScreenState.PARKMYCAR)
                        }
                    }
                },
                modifier = if (rememberHomeUiContainerState.screenState == ScreenState.PARKMYCAR) modifier.weight(
                    1f
                ) else modifier,
                expanded = rememberHomeUiContainerState.screenState == ScreenState.PARKMYCAR,
                containerColor = when (rememberHomeUiContainerState.screenState) {
                    ScreenState.PARKMYCAR -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.secondaryContainer
                },
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun BottomBarContentPreview() {
    UltimateTheme {
        BottomBarContent(
            rememberHomeUiContainerState = rememberHomeUiContainerState(),
            uiElapsedTimeState = 0L,
            onStartTimer = {},
            onCameraZoom = {},
            onSet = {}
        )
    }
}
