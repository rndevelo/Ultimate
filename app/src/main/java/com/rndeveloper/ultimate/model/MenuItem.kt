package com.rndeveloper.ultimate.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.rndeveloper.ultimate.ui.theme.blue_time_spot

data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val color: Color = blue_time_spot,
    val unit: () -> Unit = {}
)
