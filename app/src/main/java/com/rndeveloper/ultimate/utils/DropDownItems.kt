package com.rndeveloper.ultimate.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NaturePeople
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.Color
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.model.MenuItem
import com.rndeveloper.ultimate.ui.theme.green_place_icon

val timeList = listOf(
    MenuItem(
        title = R.string.home_text_now,
        icon = Icons.Outlined.Timer,
        color = green_place_icon
    ),
    MenuItem(
        title = R.string.home_text_in_5_minutes,
        icon = Icons.Outlined.Timer,
    ),
    MenuItem(
        title = R.string.home_text_in_10_minutes,
        icon = Icons.Outlined.Timer,
    ),
    MenuItem(
        title = R.string.home_text_in_15_minutes,
        icon = Icons.Outlined.Timer,
    ),
    MenuItem(
        title = R.string.home_text_in_20_minutes,
        icon = Icons.Outlined.Timer,
    ),
    MenuItem(
        title = R.string.home_text_in_25_minutes,
        icon = Icons.Outlined.Timer,
    ),
    MenuItem(
        title = R.string.home_text_in_30_minutes,
        icon = Icons.Outlined.Timer,
    )
)
