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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rndeveloper.ultimate.model.MenuItem
import com.rndeveloper.ultimate.utils.timeList
import com.rndeveloper.ultimate.utils.typeList

@Composable
fun AddPanelContent(modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Agrega una plaza libre",
            color = Color.DarkGray,
            modifier = modifier.padding(6.dp, top = 12.dp),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ).copy(fontSize = 18.sp)
        )
        Spacer(modifier = modifier.height(15.dp))
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = Icons.Outlined.Place.toString()
            )
            Spacer(modifier = modifier.width(10.dp))
//                if (isLoading) {
//                    CircularProgressIndicator(
//                        modifier = modifier
//                            .size(35.dp)
//                            .padding(8.dp),
//                        strokeWidth = 2.5.dp
//                    )
//                } else {
//                    geoAddress?.let {
//                        Text(
//                            text = it,
//                            style = MaterialTheme.typography.subtitle1
//                        )
//                    }
//                }
        }
        Spacer(modifier = modifier.height(15.dp))
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DropDownMenu(items = timeList)
            Spacer(modifier = modifier.width(35.dp))
            DropDownMenu(items = typeList)
        }
        Spacer(modifier = modifier.height(20.dp))
    }
}

@Composable
fun DropDownMenu(
    items: List<MenuItem>,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val index by remember { mutableIntStateOf(0) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = items.first().icon,
            contentDescription = "Icono",
            tint = animateColorAsState(items[index].color).value
        )
        Spacer(modifier = modifier.width(10.dp))

        Button(
            onClick = { isExpanded = !isExpanded },
            modifier = modifier.height(35.dp),
        ) {
            Text(
                text = items[index].title,
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
//                        onIndex(items.indexOf(label))
                    },
                    modifier = modifier.height(35.dp)
                )
            }
        }
    }
}