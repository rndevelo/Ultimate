package com.rndeveloper.ultimate.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.ui.graphics.vector.ImageVector

data class HelpExample(
    @StringRes val title: Int? = null,
    @StringRes val description: Int? = null,
    @DrawableRes val image: Int,
    val imageVector: ImageVector = Icons.Default.Circle
)