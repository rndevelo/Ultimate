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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    onSpot: (String) -> Unit,
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
//                Divider()
                ListsContent(
                    areas = uiAreasState.areas,
                    spots = uiSpotsState.spots,
                    isElapsedTime = isElapsedTime,
                    scrollState = rememberHomeUiContainerState.scrollState,
                    selectedSpot = selectedSpot,
                    onSpot = onSpot,
                    onRemoveSpot = onRemoveSpot
                )
//                Divider()
            }

            ScreenState.ADDSPOT -> {
//                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp),
                ) {
                    DropDownMenuContent(
                        items = timeList,
                        index = rememberHomeUiContainerState.indexSpotTime,
                        onIndex = { index ->
                            rememberHomeUiContainerState.onSpotTime(index)
                        }
                    )
//                Divider()
                }
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
    onSpot: (String) -> Unit,
    onRemoveSpot: (Spot) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 210.dp),
    ) {
        AnimatedVisibility(visible = areas.isNotEmpty()) {
            Text(
                text = stringResource(R.string.home_text_areas_nearby, areas.size),
                modifier = Modifier.padding(3.dp),
                fontWeight = FontWeight.Bold
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
                            .clickable { onSpot(area.tag) },
                        tint = area.color
                    )
                }
            }
        }
        AnimatedVisibility(visible = isElapsedTime) {
            Text(
                text = stringResource(R.string.home_text_spots_nearby, spots.size),
                modifier = Modifier.padding(3.dp),
                fontWeight = FontWeight.Bold
            )

            LazyColumn(state = scrollState) {
                items(items = spots, key = { i -> i.tag }) { spot ->
                    ItemContent(
                        spot = spot,
                        selectedSpot = selectedSpot,
                        onSpotItem = { onSpot(spot.tag) },
                        onRemoveSpot = { onRemoveSpot(spot) }
                    )
                }
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
            selectedSpot = Spot(),
            onExpand = {},
            onSpot = { },
            onRemoveSpot = {}
        )
    }
}



