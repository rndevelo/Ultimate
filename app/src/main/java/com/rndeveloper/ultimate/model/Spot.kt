package com.rndeveloper.ultimate.model

data class Spot(
    val tag: String,
    val position: Position,
    val directions: Directions,
    val type: SpotType,
    val timestamp: Long,
    val newpoints: Long,
    val user: User
) {
    constructor() : this(
        tag = "",
        position = Position(),
        directions = Directions(),
        type = SpotType.FREE,
        timestamp = 0L,
        newpoints = 0L,
        user = User()
    )
}
