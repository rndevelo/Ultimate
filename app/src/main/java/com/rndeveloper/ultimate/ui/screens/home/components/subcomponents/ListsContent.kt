package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.model.Spot

@Composable
fun ListsContent(
    areas: List<Spot>,
    spots: List<Spot>,
    isElapsedTime: Boolean,
    scrollState: LazyListState,
    selectedSpot: Spot?,
    onSpot: (String) -> Unit,
    onRemoveSpot: (Spot) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 210.dp),
    ) {
        AnimatedVisibility(visible = areas.isNotEmpty()) {
            Column {
                Divider()
                Text(
                    text = stringResource(R.string.home_text_areas_nearby, areas.size),
                    modifier = modifier.padding(3.dp),
//                    fontWeight = FontWeight.Bold
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
        }
        Spacer(modifier = Modifier.height(5.dp))
        AnimatedVisibility(visible = isElapsedTime) {
            Column {
                Divider()
                Text(
                    text = stringResource(R.string.home_text_spots_nearby, spots.size),
                    modifier = Modifier.padding(3.dp),
//                    fontWeight = FontWeight.Bold
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
        AnimatedVisibility(visible = isElapsedTime || areas.isNotEmpty()) {
            Divider()
        }
    }
}