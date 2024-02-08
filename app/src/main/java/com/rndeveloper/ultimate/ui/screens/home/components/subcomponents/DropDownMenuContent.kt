package com.rndeveloper.ultimate.ui.screens.home.components.subcomponents

import androidx.compose.animation.animateColorAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rndeveloper.ultimate.model.MenuItem
import com.rndeveloper.ultimate.ui.theme.UltimateTheme


@Composable
fun DropDownMenuContent(
    items: List<MenuItem>,
    index: Int,
    onIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    var isExpanded by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { isExpanded = true },
    ) {
        Icon(
            imageVector = items.first().icon,
            contentDescription = items.first().icon.toString(),
            tint = animateColorAsState(items[index].color, label = "").value
        )
        Text(
            text = items[index].title,
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = Icons.Filled.ArrowDropDown.toString(),
        )
    }
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { isExpanded = false }
    ) {
        items.forEach { label ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = label.title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Light)
                    )
                },
                onClick = {
                    isExpanded = false
                    onIndex(items.indexOf(label))
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPanelContentPreview() {
    UltimateTheme {
        DropDownMenuContent(items = emptyList(), index = 0, onIndex = {})
    }
}