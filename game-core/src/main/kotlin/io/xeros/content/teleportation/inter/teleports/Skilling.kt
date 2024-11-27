package io.xeros.content.teleportation.inter.teleports

import io.xeros.model.entity.player.Player
import io.xeros.model.entity.player.Position

enum class Skilling(
    override val position: Position,
    override val isDangerous: Boolean = false,
    override val spriteID: Int = 2281,
    override val description: String = "",
    override val price: Int = -1,
    override val onTeleport: (Player) -> Unit = {}
) : TeleportEntry {

}