package com.rndeveloper.ultimate.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rndeveloper.ultimate.model.Item
import com.rndeveloper.ultimate.ui.screens.home.HomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.ScreenState
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.CountContent
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.ListsContent
import com.rndeveloper.ultimate.ui.screens.home.rememberHomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.uistates.DirectionsUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.SpotsUiState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.Constants

@Composable
fun SheetContent(
    rememberHomeUiContainerState: HomeUiContainerState,
    uiSpotsState: SpotsUiState,
    uiDirectionsState: DirectionsUiState,
    uiElapsedTimeState: Long,
    selectedItem: Item?,
    onSelectSpot: (String) -> Unit
) {

    Column {
        CountContent(
            screenState = rememberHomeUiContainerState.screenState,
            uiSpotsState = uiSpotsState,
            uiDirectionsState = uiDirectionsState,
            uiElapsedTimeState = uiElapsedTimeState,
            onExpand = rememberHomeUiContainerState::onOpenBottomSheet,
        )

        AnimatedVisibility(
            visible = uiElapsedTimeState > Constants.DEFAULT_ELAPSED_TIME
                    && ScreenState.MAIN == rememberHomeUiContainerState.screenState
        ) {
            ListsContent(
                spots = uiSpotsState.items,
                scrollState = rememberHomeUiContainerState.scrollState,
                selectedItem = selectedItem,
                onSpot = onSelectSpot
            )
        }
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
            uiElapsedTimeState = 0L,
            selectedItem = Item(),
            onSelectSpot = {}
        )
    }
}



