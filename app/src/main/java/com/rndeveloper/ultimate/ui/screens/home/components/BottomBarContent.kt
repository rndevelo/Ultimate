package com.rndeveloper.ultimate.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.ui.theme.UltimateTheme

@Composable
fun BottomBarContent(
    isAddPanelState: Boolean,
    onAddPanelState: (Boolean) -> Unit,
    onSetSpot: () -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(tonalElevation = 2.dp) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(isAddPanelState) {
                FloatingActionButton(
                    onClick = { onAddPanelState(false) },
                    modifier = Modifier.padding(start = 12.dp),
                    elevation = FloatingActionButtonDefaults.elevation(1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = Icons.Default.ArrowBack.toString(),
                    )
                }
            }
            ExtendedFloatingActionButton(
                text = { Text(text = if (isAddPanelState) "Send spot" else "Add spot") },
                icon = {
                    Icon(
                        imageVector = if (isAddPanelState) Icons.Default.Send else Icons.Default.AddLocationAlt,
                        contentDescription = if (isAddPanelState) Icons.Default.Send.toString() else Icons.Default.AddLocationAlt.toString(),
                    )
                },
                onClick = { if (isAddPanelState) onSetSpot() else onAddPanelState(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                elevation = FloatingActionButtonDefaults.elevation(1.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarContentPreview() {
    UltimateTheme {
        BottomBarContent(isAddPanelState = true, onAddPanelState = {}, onSetSpot = { /*TODO*/ })
    }
}
