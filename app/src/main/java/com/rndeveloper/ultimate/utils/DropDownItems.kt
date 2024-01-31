package com.rndeveloper.ultimate.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NaturePeople
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.Color
import com.rndeveloper.ultimate.model.MenuItem
import com.rndeveloper.ultimate.ui.theme.green_place_icon

val timeList = listOf(
    MenuItem(
        title = "Ahora",
        icon = Icons.Outlined.Timer,
        color = green_place_icon
    ),
    MenuItem(
        title = "En 5 minutos",
        icon = Icons.Outlined.Timer,
    ),
    MenuItem(
        title = "En 10 minutos",
        icon = Icons.Outlined.Timer,
    ),
    MenuItem(
        title = "En 15 minutos",
        icon = Icons.Outlined.Timer,
    ),
    MenuItem(
        title = "En 20 minutos",
        icon = Icons.Outlined.Timer,
    ),
    MenuItem(
        title = "En 25 minutos",
        icon = Icons.Outlined.Timer,
    ),
    MenuItem(
        title = "En 30 minutos",
        icon = Icons.Outlined.Timer,
    )
)

val typeList = listOf(
    MenuItem(
        title = "Zona blanca",
        icon = Icons.Default.NaturePeople,
        color = Color.Black
    ),
    MenuItem(
        title = "Minusválido",
        icon = Icons.Default.NaturePeople,
        color = Color.Yellow
    ),
    MenuItem(
        title = "Zona amarilla",
        icon = Icons.Default.NaturePeople,
        color = Color.Yellow
    ),
    MenuItem(
        title = "Zona azul",
        icon = Icons.Default.NaturePeople,
        color = Color.Blue
    ),
    MenuItem(
        title = "Zona roja",
        icon = Icons.Default.NaturePeople,
        color = Color.Red
    ),
    MenuItem(
        title = "Zona verde",
        icon = Icons.Default.NaturePeople,
        color = Color.Green
    )
)