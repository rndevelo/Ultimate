package com.rndeveloper.ultimate.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.extensions.toTime
import com.rndeveloper.ultimate.ui.screens.home.HomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.ScreenState
import com.rndeveloper.ultimate.ui.screens.home.rememberHomeUiContainerState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.Constants

@Composable
fun BottomBarContent(
    rememberHomeUiContainerState: HomeUiContainerState,
    uiElapsedTimeState: Long,
    onStartTimer: () -> Unit,
    onSet: () -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(tonalElevation = 2.dp) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(R.string.home_text_add_spot)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AddLocationAlt,
                        contentDescription = Icons.Default.AddLocationAlt.toString(),
                    )
                },
                onClick = {
                    when (rememberHomeUiContainerState.screenState) {
                        ScreenState.ADDSPOT -> {
                            onSet()
                            rememberHomeUiContainerState.onScreenState(ScreenState.MAIN)
                        }

                        else -> {
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
                    ScreenState.ADDSPOT -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.secondaryContainer
                },
                elevation = FloatingActionButtonDefaults.elevation(1.dp)
            )

            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(R.string.home_text_show_spots)) },
                icon = {
                    if (uiElapsedTimeState <= Constants.DEFAULT_ELAPSED_TIME) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = Icons.Default.Visibility.toString()
                        )
                    } else {
                        Text(text = uiElapsedTimeState.toTime())
                    }
                },
                onClick = {
                    when (rememberHomeUiContainerState.screenState) {
                        ScreenState.MAIN -> {
                            onStartTimer()
                        }

                        else -> {
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
                    ScreenState.MAIN -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.secondaryContainer
                },
                elevation = FloatingActionButtonDefaults.elevation(1.dp)
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
                            onSet()
                            rememberHomeUiContainerState.onScreenState(ScreenState.MAIN)
                        }

                        else -> {
                            rememberHomeUiContainerState.onScreenState(ScreenState.PARKMYCAR)
                        }
                    }
                },
                modifier = if (rememberHomeUiContainerState.screenState == ScreenState.PARKMYCAR) modifier.weight(
                    1f
                ) else modifier,
                expanded = rememberHomeUiContainerState.screenState == ScreenState.PARKMYCAR,
                containerColor = when (rememberHomeUiContainerState.screenState) {
                    ScreenState.PARKMYCAR -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.secondaryContainer
                },
                elevation = FloatingActionButtonDefaults.elevation(1.dp)
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
            onStartTimer = { /*TODO*/ },
            onSet = { /*TODO*/ })
    }
}
