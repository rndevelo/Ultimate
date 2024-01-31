package com.rndeveloper.ultimate.ui.screens.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.screens.home.HomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.CountContent
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.ItemContent
import com.rndeveloper.ultimate.ui.screens.home.rememberHomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.uistates.DirectionsUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.SpotsUiState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme

@Composable
fun SheetContent(
    rememberHomeUiContainerState: HomeUiContainerState,
    uiSpotsState: SpotsUiState,
    uiDirectionsState: DirectionsUiState,
    spotDefault: Spot?,
    onExpand: () -> Unit,
    onSpot: (Spot) -> Unit,
    onRemoveSpot: (Spot) -> Unit,
    modifier: Modifier = Modifier
) {

    Column {
        CountContent(
            screenState = rememberHomeUiContainerState.screenState,
            uiSpotsState = uiSpotsState,
            uiDirectionsState = uiDirectionsState,
            onExpand = onExpand
        )
        Divider()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp),
            state = rememberHomeUiContainerState.scrollState
        ) {
            items(items = uiSpotsState.spots, key = { i -> i.tag }) { spot ->
                ItemContent(
                    spot = spot,
                    selectedSpot = spotDefault,
                    onSpotItem = { onSpot(spot) },
                    onRemoveSpot = { onRemoveSpot(spot) }
                )
            }
        }
        Divider()
//        AnimatedVisibility(homeUiState.elapsedTime > 0 && !isAddPanelState) {
//
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SheetContentPreview() {
    UltimateTheme {
        SheetContent(
            rememberHomeUiContainerState = rememberHomeUiContainerState(),
            uiSpotsState = SpotsUiState(),
            uiDirectionsState = DirectionsUiState(),
            spotDefault = null,
            onExpand = { /*TODO*/ },
            onSpot = {},
            onRemoveSpot = {}
        )
    }
}



