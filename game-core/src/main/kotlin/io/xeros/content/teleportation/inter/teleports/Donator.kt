package io.xeros.content.teleportation.inter.teleports

import io.xeros.model.entity.player.Player
import io.xeros.model.entity.player.Position
import java.util.*

enum class Donator (
    override val position: Position,
    override val isDangerous: Boolean = false,
    override val spriteID: Int = -1,
    override val description: String = "",
    override val price: Int = -1,
    override val onTeleport: (Player) -> Unit = {}
) : TeleportEntry {

    DONATOR_ZONE(Position(3105, 3249, 0)),
    EXTREME_DONATOR_ZONE(Position(1310, 3618, 0)),
    LEGENDARY_DONATOR_ZONE(Position(1310, 3618, 0))

    ;

    override fun getName() = name

    override fun getDisplayName(): String {
        return name.lowercase()
            .replace('_', ' ')
            .split(' ')
            .joinToString(" ") { it -> it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
    }

}