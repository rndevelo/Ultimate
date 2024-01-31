package com.rndeveloper.ultimate.model

import androidx.compose.ui.graphics.Color
import com.rndeveloper.ultimate.ui.theme.green_place_icon

data class Spot(
    val tag: String,
    val position: Position,
    val distance: String,
    val color: Color,
    val directions: Directions,
    val type: SpotType,
    val timestamp: Long,
    val newpoints: Long,
    val user: User
) {
    constructor() : this(
        tag = "",
        position = Position(),
        distance = "",
        color = green_place_icon,
        directions = Directions(),
        type = SpotType.FREE,
        timestamp = 0L,
        newpoints = 0L,
        user = User()
    )
}
