package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.model.Spot

@Composable
fun ListsContent(
    spots: List<Spot>,
    scrollState: LazyListState,
    selectedSpot: Spot?,
    onSpot: (String) -> Unit,
    modifier: Modifier = Modifier
) {

        Column(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(max = 160.dp),
        ) {
            HorizontalDivider()
            LazyColumn(state = scrollState) {
                items(items = spots, key = { i -> i.tag }) { spot ->
                    ItemContent(
                        spot = spot,
                        selectedSpot = selectedSpot,
                        onSpotItem = { onSpot(spot.tag) },
                    )
                }
            }
            HorizontalDivider()
        }

}