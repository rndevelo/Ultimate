package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.ui.screens.home.DirectionsUiState
import com.rndeveloper.ultimate.ui.screens.home.ScreenState
import com.rndeveloper.ultimate.ui.screens.home.SpotsUiState

@Composable
fun CountContent(
    screenState: ScreenState,
    uiSpotsState: SpotsUiState,
    uiElapsedTimeState: Long,
    uiDirectionsState: DirectionsUiState,
    onStartTimer: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(bottom = 20.dp)
            .clickable(interactionSource = interactionSource, indication = null) { onExpand() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            AnimatedVisibility(uiSpotsState.spots.isNotEmpty()) {
                Text(
                    text = if (screenState == ScreenState.MAIN) "${uiSpotsState.spots.size} spots in this zone" else if (screenState == ScreenState.ADDSPOT) "Add a new spot" else "Park your car",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
            }

            Spacer(modifier = modifier.height(2.dp))
            Text(
                text = if (screenState == ScreenState.MAIN) uiDirectionsState.directions.locality + ", " + uiDirectionsState.directions.area else uiDirectionsState.directions.addressLine,

//                text = if (uiSpotsState.spots.isNotEmpty())
//                    uiDirectionsState.ifEmpty { stringResource(R.string.home_text_unknow_location) }
//                else stringResource(R.string.home_text_without_spots),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Light),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CountContentPreview() {
    CountContent(
        screenState = ScreenState.MAIN,
        uiSpotsState = SpotsUiState(),
        uiElapsedTimeState = 0L,
        uiDirectionsState = DirectionsUiState(),
        onStartTimer = {},
        onExpand = {}
    )
}