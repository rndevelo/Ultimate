package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.ui.screens.home.ScreenState
import com.rndeveloper.ultimate.ui.screens.home.uistates.AreasUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.DirectionsUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.SpotsUiState

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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(bottom = 20.dp)
            .clickable(interactionSource = interactionSource, indication = null) { onExpand() },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = when (screenState) {
                ScreenState.MAIN -> stringResource(R.string.home_text_search_parking_spot)
                ScreenState.ADDSPOT -> stringResource(R.string.home_text_add_a_new_spot)
                ScreenState.PARKMYCAR -> stringResource(R.string.home_text_park_your_car)
            },
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        )
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