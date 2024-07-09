package com.rndeveloper.ultimate.model

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptor
import com.rndeveloper.ultimate.ui.theme.green_area
import com.rndeveloper.ultimate.ui.theme.green_place_icon

data class Item(
    val tag: String,
    val position: Position,
    val distance: String,
    val spotColor: Color,
    val areaColor: Color,
    val icon: BitmapDescriptor?,
    val directions: Directions,
    val type: SpotType,
    val time: String,
    val timestamp: Long,
    val user: User
) {
    constructor() : this(
        tag = "",
        position = Position(),
        distance = "",
        spotColor = green_place_icon,
        areaColor = green_area,
        icon = null,
        directions = Directions(),
        type = SpotType.FREE,
        time = "",
        timestamp = 0L,
        user = User()
    )
}
