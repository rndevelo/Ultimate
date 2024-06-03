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
import com.rndeveloper.ultimate.model.Item

@Composable
fun ListsContent(
    spots: List<Item>,
    scrollState: LazyListState,
    selectedItem: Item?,
    onSpot: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 160.dp)
            .padding(bottom = 5.dp),
    ) {
        HorizontalDivider()
        LazyColumn(state = scrollState) {
            items(items = spots, key = { i -> i.tag }) { spot ->
                ItemContent(
                    spot = spot,
                    selectedItem = selectedItem,
                    onSpotItem = { onSpot(spot.tag) },
                )
            }
        }
        HorizontalDivider()
    }
}