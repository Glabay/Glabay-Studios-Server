package io.xeros.content.teleportation.inter.teleports

import io.xeros.model.entity.player.Player
import io.xeros.model.entity.player.Position
import java.util.*

enum class Bosses(
    override val position: Position,
    override val isDangerous: Boolean = false,
    override val spriteID: Int = -1,
    override val description: String = "",
    override val price: Int = -1,
    override val onTeleport: (Player) -> Unit = {}
) : TeleportEntry {

    GODWARS(Position(2882, 5311, 2)),
    LIZARDMAN_SHAMAN(Position(3105, 3249, 0)),
    THERM_SMOKE_DEVILS(Position(2376, 9452, 0)),
    ABYSSAL_SIRE(Position(3039, 4788, 0)),
    KRAKEN(Position(2280, 10016, 0)),
    CERBERUS(Position(3105, 3249, 0)),
    ZULRAH(Position(2202, 3056, 0)),
    THE_NIGHTMARE(Position(3808, 9746, 1)),
    CORP_BEAST(Position(2964, 4382, 2)),
    KALPHITE_QUEEN(Position(3508, 9493, 0)),
    DAGGANNOTH_KINGS(Position(1913, 4367, 0)),
    GROTESQUE_GUARDIANS(Position(3428, 3541, 2)),
    SARACHNIS(Position(1842, 9926, 0)),
    VORKATH(Position(2272, 4050, 0)),
    GIANT_MOLE(Position(2993, 3376, 0)),
    OBOR(Position(3097, 9833, 0)),
    BRYOPHYTA(Position(3174, 3898, 0)),
    NEX(Position(2906, 5203, 0)),
    BARRELCHEST(Position(2903, 3612, 0));

    override fun getName() = name

    override fun getDisplayName(): String {
        return name.lowercase()
            .replace('_', ' ')
            .split(' ')
            .joinToString(" ") { it ->
                it.replaceFirstChar {
                    if (it.isLowerCase())
                        it.titlecase(Locale.getDefault())
                    else
                        it.toString()
                }
            }
    }
}