package io.xeros.content.teleportation.inter.teleports

import io.xeros.model.entity.player.Player
import io.xeros.model.entity.player.Position
import java.util.*

enum class Wilderness (
    override val position: Position,
    override val isDangerous: Boolean = false,
    override val spriteID: Int = -1,
    override val description: String = "",
    override val price: Int = -1,
    override val onTeleport: (Player) -> Unit = {}
    ) : TeleportEntry {

        WEST_DRAGONS(Position(2976, 3591, 0)),
        EAST_DRAGONS(Position(3351, 3659, 0)),
        MAGE_BANK(Position(2539, 4716, 0)),
        FOUNTAIN_OF_RUNE(Position(3347, 3872, 0)),
        CHAOS_ALTAR(Position(3236, 3628, 0)),
        RUINS(Position(3287, 3899, 0)),
        WILDERNESS_AGILITY(Position(3003, 3934, 0)),
        LAVA_DRAGONS(Position(3204, 3812, 0)),
        REVENANTS(Position(3128, 3833, 0)),
        VENENATIS(Position(3345, 3754, 0)),
        CALLISTO(Position(3325, 3845, 0)),
        CRAZY_ARCH(Position(2984, 3713, 0)),
        CHAOS_FANATIC(Position(2981, 3836, 0)),
        VETION(Position(3176, 3782, 0)),
        CHAOS_ELEMENTAL(Position(3281, 3910, 0)),
        KING_BLACK_DRAGON(Position(2271, 4680, 0))
    ;

        override fun getName() = name

        override fun getDisplayName(): String {
            return name.lowercase()
                .replace('_', ' ')
                .split(' ')
                .joinToString(" ") { it -> it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
        }
    }