package io.xeros.content.teleportation.inter.teleports

import io.xeros.model.entity.player.Player
import io.xeros.model.entity.player.Position
import java.util.*

enum class Dungeons(
    override val position: Position,
    override val isDangerous: Boolean = false,
    override val spriteID: Int = -1,
    override val description: String = "",
    override val price: Int = -1,
    override val onTeleport: (Player) -> Unit = {}
    ) : TeleportEntry {

        CATACOMBS(Position(1661, 10049, 0)),
        SLAYER_TOWER(Position(3428, 3538, 0)),
        FORTHOS_DUNGEON(Position(1801, 9938)),
        FREMENNIK_SLAYER_DUNGEON(Position(2805, 10001, 0)),
        TAVERLY_DUNGEON(Position(2884, 9796, 0)),
        STRONGHOLD_CAVE(Position(2431, 3425, 0)),
        ASGARNIAN_ICE_DUNGEON(Position(3029, 9582, 0)),
        LITHKREN_DUNGEON(Position(3105, 3249, 0)),
        CRYSTAL_CAVERN(Position(3105, 3249, 0)),
        BRIMHAVEN_DUNGEON(Position(2710, 9564, 0)),
        BASILISK_DUNGEON(Position(2454, 10409)),
        JORMUNGANDS_DUNGEON(Position(1310, 3618, 0)),
        IORWERTH_DUNGEON(Position(3272, 6052, 0))
        ;

        override fun getName() = name

        override fun getDisplayName(): String {
            return name.lowercase()
                .replace('_', ' ')
                .split(' ')
                .joinToString(" ") { it -> it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
        }
}