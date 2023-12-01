package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.extensions.toTime
import com.rndeveloper.ultimate.ui.screens.home.SpotsUiState
import com.rndeveloper.ultimate.utils.Constants.DEFAULT_ELAPSED_TIME

@Composable
fun CountContent(
    uiSpotsState: SpotsUiState,
    uiElapsedTimeState: Long,
    uiAddressLineState: String,
    onStartTimer: () -> Unit,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(bottom = 20.dp)
            .clickable(interactionSource = interactionSource, indication = null) { onExpand() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            AnimatedVisibility(uiSpotsState.spots.isNotEmpty()) {
                Text(
                    text = "${uiSpotsState.spots.size} spots in this zone",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
            }
            Spacer(modifier = modifier.height(1.dp))
            Text(
                text = if (uiSpotsState.spots.isNotEmpty())
                    uiAddressLineState.ifEmpty { stringResource(R.string.home_text_unknow_location) }
                else stringResource(R.string.home_text_without_spots),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Light),
            )
        }

        FloatingActionButton(
            onClick = onStartTimer,
            elevation = FloatingActionButtonDefaults.elevation(1.dp)
        ) {
            if (uiElapsedTimeState <= DEFAULT_ELAPSED_TIME) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = Icons.Default.Visibility.toString()
                )
            } else {
                Text(text = uiElapsedTimeState.toTime())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CountContentPreview() {
    CountContent(
        uiSpotsState = SpotsUiState(),
        uiElapsedTimeState = 0L,
        uiAddressLineState = "Address",
        onStartTimer = {},
        onExpand = {}
    )
}