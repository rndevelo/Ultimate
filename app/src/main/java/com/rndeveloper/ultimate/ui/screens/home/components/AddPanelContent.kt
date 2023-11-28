package com.rndeveloper.ultimate.ui.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rndeveloper.ultimate.model.MenuItem
import com.rndeveloper.ultimate.ui.theme.UltimateTheme
import com.rndeveloper.ultimate.ui.theme.red_place_icon
import com.rndeveloper.ultimate.utils.timeList
import com.rndeveloper.ultimate.utils.typeList

@Composable
fun AddPanelContent(
    addressLine: String,
    modifier: Modifier = Modifier
) {

    Surface(tonalElevation = 2.dp) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Agrega una plaza libre",
                modifier = modifier.padding(12.dp),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = Icons.Outlined.Place.toString(),
                    tint = red_place_icon
                )
                Spacer(modifier = modifier.width(10.dp))
                Text(
                    text = addressLine,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = modifier.height(15.dp))
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DropDownMenu(items = timeList)
                DropDownMenu(items = typeList)
            }
        }
    }
}

@Composable
fun DropDownMenu(
    items: List<MenuItem>,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var index by remember { mutableIntStateOf(0) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = items.first().icon,
            contentDescription = "Icono",
            tint = animateColorAsState(items[index].color, label = "").value
        )
        Spacer(modifier = modifier.width(10.dp))

        Button(onClick = { isExpanded = true }) {
            Text(
                text = items[index].title,
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Desplegar",
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
                        index = items.indexOf(label)
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPanelContentPreview() {
    UltimateTheme {
        AddPanelContent(addressLine = "")
    }
}