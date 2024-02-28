package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.LatLng
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.ui.screens.home.ScreenState
import com.rndeveloper.ultimate.ui.screens.home.uistates.AreasUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.DirectionsUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.SpotsUiState
import com.rndeveloper.ultimate.ui.theme.green_place_icon
import kotlinx.coroutines.delay

@Composable
fun CountContent(
    screenState: ScreenState,
    uiSpotsState: SpotsUiState,
    uiAreasState: AreasUiState,
    uiDirectionsState: DirectionsUiState,
    onCameraArea: (LatLng) -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {

    val interactionSource = remember { MutableInteractionSource() }
    var point by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit, key2 = uiAreasState.areas, block = {
        while (true) {
            delay(
                when {
                    uiAreasState.areas.size >= 6 -> 250
                    uiAreasState.areas.size >= 3 -> 500
                    else -> 1000
                }
            )
            point = !point
        }
    })

    val color by animateColorAsState(
        targetValue = if (point) green_place_icon else Color.Transparent,
        label = ""
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(bottom = 14.dp)
            .clickable(interactionSource = interactionSource, indication = null) { onExpand() },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = when (screenState) {
                    ScreenState.MAIN -> if (uiSpotsState.spots.isNotEmpty()) {
                        stringResource(R.string.home_text_parking_spots, uiSpotsState.spots.size)
                    } else {
                        stringResource(R.string.home_text_without_parking_spots)
                    }

                    ScreenState.ADDSPOT -> stringResource(R.string.home_text_add_a_new_spot)
                    ScreenState.PARKMYCAR -> stringResource(R.string.home_text_park_your_car)
                },
                style = if (uiSpotsState.spots.isNotEmpty()) {
                    MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                } else {
                    MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                },
            )
            Column {

                Row(
                    modifier = Modifier.clickable { isExpanded = !isExpanded },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = Icons.Filled.Circle.toString(),
                        modifier = Modifier.size(8.dp),
                        tint = when {
                            uiAreasState.areas.isEmpty() -> Color.Transparent
                            uiAreasState.areas.size >= 2 -> color
                            else -> color
                        }
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = when {
                            uiAreasState.areas.isEmpty() -> "Low activity"
                            uiAreasState.areas.size >= 4 -> "High activity"
                            else -> "Regular activity"
                        },
                        fontWeight = FontWeight.Light,
                        color = Gray
                    )
                    Spacer(modifier = Modifier.width(3.dp))

                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                        contentDescription = if (isExpanded) Icons.Filled.ArrowDropUp.toString() else Icons.Filled.ArrowDropDown.toString(),
                        tint = Gray
                    )
                }

                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    uiAreasState.areas.forEach { area ->
                        DropdownMenuItem(
                            text = {
                                Row {
                                    Icon(
                                        imageVector = Icons.Filled.Circle,
                                        contentDescription = Icons.Filled.Circle.toString(),
                                        modifier = Modifier.size(40.dp),
                                        tint = area.color
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Column {
                                        Text(
                                            text = area.distance,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Light
                                            )
                                        )
                                        Text(
                                            text = area.time,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }

                                }
                            },
                            onClick = {
                                onCameraArea(
                                    LatLng(
                                        area.position.lat,
                                        area.position.lng
                                    )
                                )
                            },
                        )
                    }
                }
            }

        }

        Spacer(modifier = modifier.height(5.dp))
        Text(
            text = uiDirectionsState.directions.addressLine,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CountContentPreview() {
    CountContent(
        screenState = ScreenState.MAIN,
        uiSpotsState = SpotsUiState(),
        uiAreasState = AreasUiState(),
        uiDirectionsState = DirectionsUiState(),
        onCameraArea = {},
        onExpand = {}
    )
}