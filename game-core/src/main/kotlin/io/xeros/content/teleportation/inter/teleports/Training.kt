package io.xeros.content.teleportation.inter.teleports

import io.xeros.model.entity.player.Player
import io.xeros.model.entity.player.Position
import java.util.*

enum class Training (
    override val position: Position,
    override val isDangerous: Boolean = false,
    override val spriteID: Int = 2275,
    override val description: String = "",
    override val price: Int = -1,
    override val onTeleport: (Player) -> Unit = {}
) : TeleportEntry {

    ROCK_CRABS(Position(2673, 3710, 0)),
    CHICKENS(Position(3234, 3293, 0)),
    KALPHITES(Position(3503, 9522, 2)),
    BANDITS(Position(3176, 2987, 0)),
    ELF_WARRIORS(Position(2897, 2725, 0)),
    DAGANNOTHS(Position(2442, 10147, 0)),
    SMOKE_DEVILS(Position(2404, 9415, 0)),
    MITHRIL_DRAGONS(Position(1746, 5323, 0)),
    DEMONIC_GORILLAS(Position(2130, 5647, 0)),
    FOSSIL_ISLAND_WYVERNS(Position(3607, 10290)),
    CAVE_KRAKENS(Position(2277, 10001, 0)),
    RUNE_DRAGONS(Position(1568, 5075, 0)),
    ADAMANT_DRAGONS(Position(1568, 5075, 0));

    override fun getName() = name

    override fun getDisplayName(): String {
        return name.lowercase()
            .replace('_', ' ')
            .split(' ')
            .joinToString(" ") { it -> it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
    }
}