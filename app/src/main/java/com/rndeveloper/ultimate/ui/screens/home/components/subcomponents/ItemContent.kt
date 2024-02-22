package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.rndeveloper.ultimate.extensions.getFormattedPrettyTime
import com.rndeveloper.ultimate.model.Spot
import com.rndeveloper.ultimate.ui.theme.UltimateTheme

@Composable
fun ItemContent(
    spot: Spot,
    selectedSpot: Spot?,
    onSpotItem: () -> Unit,
    onRemoveSpot: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val surfaceColor by animateColorAsState(
        targetValue = if (spot.tag == (selectedSpot?.tag ?: spot.tag))
            MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface,
        label = "",
    )

    val context = LocalContext.current

    var expandedItem by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSpotItem() },
        color = surfaceColor,
    ) {
        Column(
            modifier = modifier
                .padding(vertical = 4.dp, horizontal = 14.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = spot.distance, style = TextStyle().copy(fontWeight = FontWeight.Light))

                Text(
                    text = spot.timestamp.getFormattedPrettyTime(),
                    fontWeight = FontWeight.Bold,
                    color = spot.color
                )

                Row(verticalAlignment = Alignment.CenterVertically) {

                    IconButton(onClick = { expandedItem = !expandedItem }) {
                        Icon(
                            imageVector = if (expandedItem) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expandedItem) Icons.Filled.KeyboardArrowUp.toString() else Icons.Filled.KeyboardArrowDown.toString(),
                        )
                    }

                    FilledIconButton(onClick = onRemoveSpot) {
                        Icon(
                            imageVector = Icons.Filled.Navigation,
                            contentDescription = Icons.Filled.Navigation.toString(),
                        )
                    }
                }
            }

            if (expandedItem) {
                Text(
                    text = spot.directions.addressLine,
                    maxLines = 3,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemContentPreview() {
    UltimateTheme {
        ItemContent(
            spot = Spot(),
            selectedSpot = Spot(),
            onSpotItem = { /*TODO*/ },
            onRemoveSpot = { /*TODO*/ }
        )
    }
}