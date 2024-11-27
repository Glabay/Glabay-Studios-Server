package io.xeros.model.entity.grounditem

enum class GroundItemPickupState(val displayDelay : Int) {
    REGULAR(60),
    INSTANT(0),
    NO_PICKUP(0),
}