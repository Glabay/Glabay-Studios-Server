package io.xeros.content.teleportation.inter.teleports

import io.xeros.model.entity.player.Player
import io.xeros.model.entity.player.Position

interface TeleportEntry {
    val position: Position
    val isDangerous: Boolean
    val spriteID: Int
    val description: String
    val price: Int
    val onTeleport: (Player) -> Unit

    fun getDisplayName() : String

    fun getName() : String

}