package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.extensions.toTime
import com.rndeveloper.ultimate.ui.screens.home.HomeUiState

@Composable
fun CountContent(
    homeUiState: HomeUiState,
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
            AnimatedVisibility(homeUiState.spots.isNotEmpty()) {
                Text(
                    text = "${homeUiState.spots.size} spots in this zone",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
            }
            Text(
                text = if (homeUiState.spots.isNotEmpty())
                    homeUiState.addressLine
                else "Without spots in this zone",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Light),
            )
        }

        FloatingActionButton(
            onClick = onStartTimer,
            elevation = FloatingActionButtonDefaults.elevation(1.dp)
        ) {
            if (homeUiState.elapsedTime <= 0L) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = Icons.Default.Visibility.toString()
                )
            } else {
                Text(text = homeUiState.elapsedTime.toTime())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CountContentPreview() {
    CountContent(
        homeUiState = HomeUiState(),
        onStartTimer = {},
        onExpand = {}
    )
}