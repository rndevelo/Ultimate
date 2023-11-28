package com.rndeveloper.ultimate.ui.screens.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.screens.home.HomeUiState
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.CountContent
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.ItemContent
import com.rndeveloper.ultimate.ui.theme.UltimateTheme

@Composable
fun SheetContent(
    isAddPanelState: Boolean,
    homeUiState: HomeUiState,
    scrollState: LazyListState,
    onExpand: () -> Unit,
    onStartTimer: () -> Unit,
    onSpotSelected: (String) -> Unit,
    onRemoveSpot: (Spot) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {
        CountContent(
            homeUiState = homeUiState,
            onStartTimer = onStartTimer,
            onExpand = onExpand
        )
        Divider()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
                .padding(vertical = 4.dp),
            state = scrollState
        ) {
            items(items = homeUiState.spots, key = { i -> i.tag }) { spot ->
                ItemContent(
                    spot = spot,
                    selectedSpot = homeUiState.selectedSpot,
                    onSpotSelected = { onSpotSelected(spot.tag) },
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

@Preview(showBackground = true)
@Composable
fun SheetContentPreview() {
    UltimateTheme {
        SheetContent(
            isAddPanelState = true,
            homeUiState = HomeUiState(),
            scrollState = LazyListState(),
            onExpand = { /*TODO*/ },
            onStartTimer = { /*TODO*/ },
            onSpotSelected = {},
            onRemoveSpot = {}
        )
    }
}



