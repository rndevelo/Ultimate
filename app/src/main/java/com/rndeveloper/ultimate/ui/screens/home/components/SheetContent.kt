package com.rndeveloper.ultimate.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.screens.home.HomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.ScreenState
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.CountContent
import com.rndeveloper.ultimate.ui.screens.home.components.subcomponents.ItemContent
import com.rndeveloper.ultimate.ui.screens.home.rememberHomeUiContainerState
import com.rndeveloper.ultimate.ui.screens.home.uistates.AreasUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.DirectionsUiState
import com.rndeveloper.ultimate.ui.screens.home.uistates.SpotsUiState
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.utils.timeList
import com.rndeveloper.ultimate.utils.typeList

@Composable
fun SheetContent(
    rememberHomeUiContainerState: HomeUiContainerState,
    uiSpotsState: SpotsUiState,
    uiAreasState: AreasUiState,
    uiDirectionsState: DirectionsUiState,
    isElapsedTime: Boolean,
    selectedSpot: Spot?,
    onExpand: () -> Unit,
    onSpot: (String, List<Spot>) -> Unit,
    onRemoveSpot: (Spot) -> Unit,
    modifier: Modifier = Modifier
) {

    Column {
        CountContent(
            screenState = rememberHomeUiContainerState.screenState,
            uiSpotsState = uiSpotsState,
            uiAreasState = uiAreasState,
            uiDirectionsState = uiDirectionsState,
            onExpand = onExpand
        )

//        FIXME : Refactor list content

        when (rememberHomeUiContainerState.screenState) {
            ScreenState.MAIN -> {
                Divider()
                ListsContent(
                    areas = uiAreasState.areas,
                    spots = uiSpotsState.spots,
                    isElapsedTime = isElapsedTime,
                    scrollState = rememberHomeUiContainerState.scrollState,
                    selectedSpot = selectedSpot,
                    onSpot = onSpot,
                    onRemoveSpot = onRemoveSpot
                )
                Divider()
            }

            ScreenState.ADDSPOT -> {
                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DropDownMenuContent(
                        items = timeList,
                        index = rememberHomeUiContainerState.indexSpotTime,
                        onIndex = { index ->
                            rememberHomeUiContainerState.onSpotTime(index)
                        }
                    )
                    DropDownMenuContent(
                        items = typeList,
                        index = rememberHomeUiContainerState.indexSpotType,
                        onIndex = { index ->
                            rememberHomeUiContainerState.onSpotType(index)
                        }
                    )
                }
                Divider()
            }

            ScreenState.PARKMYCAR -> {}
        }
    }
}

@Composable
private fun ListsContent(
    areas: List<Spot>,
    spots: List<Spot>,
    isElapsedTime: Boolean,
    scrollState: LazyListState,
    selectedSpot: Spot?,
    onSpot: (String, List<Spot>) -> Unit,
    onRemoveSpot: (Spot) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 200.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when {
                areas.isEmpty() -> stringResource(R.string.home_text_low_areas_activity)
                else -> stringResource(R.string.home_text_areas_nearby, areas.size)
            },
            modifier = Modifier.padding(3.dp)
        )
        LazyRow {
            items(
                items = areas,
                key = { i -> i.tag }
            ) { area ->
                Icon(
                    imageVector = Icons.Filled.Circle,
                    contentDescription = Icons.Filled.Circle.toString(),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onSpot(area.tag, areas) },
                    tint = area.color
                )
            }
        }
        Text(
            text = if (spots.isNotEmpty()) {
                stringResource(R.string.home_text_spots_nearby, spots.size)
            } else {
                stringResource(R.string.home_text_without_parking_spots)
            },
            modifier = Modifier.padding(3.dp)
        )
        AnimatedVisibility(visible = isElapsedTime) {
            LazyColumn(state = scrollState) {
                items(items = spots, key = { i -> i.tag }) { spot ->
                    ItemContent(
                        spot = spot,
                        selectedSpot = selectedSpot,
                        onSpotItem = { onSpot(spot.tag, spots) },
                        onRemoveSpot = { onRemoveSpot(spot) }
                    )
                }
            }
        }
        AnimatedVisibility(visible = !isElapsedTime) {
            Row {
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = Icons.Filled.Verified.toString()
                )
                Text(
                    text = "Click to show verified spots",
                    modifier = Modifier.padding(5.dp)
                )
            }
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
            uiAreasState = AreasUiState(),
            uiDirectionsState = DirectionsUiState(),
            isElapsedTime = true,
            selectedSpot = null,
            onExpand = {},
            onSpot = { _, _ -> },
            onRemoveSpot = {}
        )
    }
}



