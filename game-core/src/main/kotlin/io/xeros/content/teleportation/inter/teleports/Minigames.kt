package io.xeros.content.teleportation.inter.teleports

import io.xeros.model.entity.player.Player
import io.xeros.model.entity.player.Position
import java.util.*

enum class Minigames (
    override val position: Position,
    override val isDangerous: Boolean = false,
    override val spriteID: Int = -1,
    override val description: String = "",
    override val price: Int = -1,
    override val onTeleport: (Player) -> Unit = {}
) : TeleportEntry {

    CHAMBERS_OF_XERIC(Position(1233, 3570, 0)),
    THEATRE_OF_BLOOD(Position(3671, 3219, 0)),
    OUTLAST(Position(3103, 3511, 0)),
    JAD(Position(2445, 5176, 0)),
    INFERNO(Position(2494, 5107, 0)),
    PEST_CONTROL(Position(2660, 2648, 0)),
    BARROWS(Position(3565, 3316, 0)),
    WARRIORS_GUILD(Position(2874, 3546, 0)),
    MAGE_ARENA(Position(2541, 4716, 0))
    ;

    override fun getName() = name

    override fun getDisplayName(): String {
        return name.lowercase()
            .replace('_', ' ')
            .split(' ')
            .joinToString(" ") { it -> it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
    }
}