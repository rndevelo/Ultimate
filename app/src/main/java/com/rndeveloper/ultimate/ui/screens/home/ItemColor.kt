package com.rndeveloper.ultimate.ui.screens.home

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptor
import com.rndeveloper.ultimate.R
import com.rndeveloper.ultimate.ui.theme.blue_place_icon
import com.rndeveloper.ultimate.ui.theme.green_place_icon
import com.rndeveloper.ultimate.ui.theme.red_place_icon
import com.rndeveloper.ultimate.ui.theme.yellow_place_icon
import com.rndeveloper.ultimate.utils.BitmapHelper

sealed class ItemColor(context: Context, color: Color, icon: BitmapDescriptor) {
    data class Blue(
        val context: Context,
        val color: Color = blue_place_icon,
        val icon: BitmapDescriptor = BitmapHelper.vectorToBitmap(
            context = context,
            id = R.drawable.ic_spot_round_blue_marker
        ),
    ) : ItemColor(context, color, icon)

    data class Green(
        val context: Context,
        val color: Color = green_place_icon,
        val icon: BitmapDescriptor = BitmapHelper.vectorToBitmap(
            context = context,
            id = R.drawable.ic_spot_round_marker
        ),
    ) : ItemColor(context, color, icon)

    data class Yellow(
        val context: Context,
        val color: Color = yellow_place_icon,
        val icon: BitmapDescriptor = BitmapHelper.vectorToBitmap(
            context = context,
            id = R.drawable.ic_spot_round_yellow_marker
        ),
    ) : ItemColor(context, color, icon)

    data class Red(
        val context: Context,
        val color: Color = red_place_icon,
        val icon: BitmapDescriptor = BitmapHelper.vectorToBitmap(
            context = context,
            id = R.drawable.ic_spot_round_red_marker
        ),
    ) : ItemColor(context, color, icon)
}