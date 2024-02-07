package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Circle
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {

    val interactionSource = remember { MutableInteractionSource() }

    var point by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit, block = {
        while (true) {
            delay(500)
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
            .padding(bottom = 20.dp)
            .clickable(interactionSource = interactionSource, indication = null) { onExpand() },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
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
                    MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                } else {
                    MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                },
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Circle,
                    contentDescription = Icons.Filled.Circle.toString(),
                    modifier = Modifier.size(8.dp),
                    tint = if (uiAreasState.areas.isEmpty()) {
                        color
                    } else {
                        color
                    }
                )
                Spacer(modifier = modifier.width(3.dp))
                Text(
                    text = if (uiAreasState.areas.isEmpty()) {
                        "Low activity"
                    } else {
                        "High activity"
                    },
                    fontWeight = FontWeight.Light,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = modifier.height(3.dp))
        Text(
            text = uiDirectionsState.directions.addressLine,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Light),
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
        onExpand = {}
    )
}