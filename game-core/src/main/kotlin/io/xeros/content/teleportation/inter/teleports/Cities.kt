package io.xeros.content.teleportation.inter.teleports

import io.xeros.content.achievement_diary.impl.ArdougneDiaryEntry
import io.xeros.content.achievement_diary.impl.FaladorDiaryEntry
import io.xeros.content.achievement_diary.impl.KandarinDiaryEntry
import io.xeros.content.achievement_diary.impl.LumbridgeDraynorDiaryEntry
import io.xeros.model.entity.player.Player
import io.xeros.model.entity.player.Position
import java.util.*

enum class Cities(
    override val position: Position,
    override val isDangerous: Boolean = false,
    override val spriteID: Int = -1,
    override val description: String = "",
    override val price: Int = -1,
    override val onTeleport: (Player) -> Unit = {}
) : TeleportEntry {
    VARROCK(Position(3210, 3424, 0), description = "Location: Varrock"),
    YANILLE(Position(2606, 3093, 0), description = "Location: Yanille"),
    EDGEVILLE(Position(3093, 3493, 0), description = "Location: Edgeville"),
    LUMBRIDGE(Position(3222, 3218, 0), description = "Location: Lumbridge", onTeleport = { player ->
        player.diaryManager.lumbridgeDraynorDiary.progress(LumbridgeDraynorDiaryEntry.LUMBRIDGE_TELEPORT)
    }),
    ARDOUGNE(Position(2662, 3305, 0), description = "Location: Ardougne", onTeleport = { player ->
        player.diaryManager.ardougneDiary.progress(ArdougneDiaryEntry.TELEPORT_ARDOUGNE)
    }),
    NEITIZNOT(Position(2321, 3804, 0), description = "Location: Neitiznot"),
    KARAMJA(Position(2948, 3147, 0), description = "Location: Karmaja"),
    FALADOR(Position(2964, 3378, 0), description = "Location: Falador", onTeleport = { player ->
        player.diaryManager.faladorDiary.progress(FaladorDiaryEntry.TELEPORT_TO_FALADOR)
    }),
    TAVERLEY(Position(2928, 3451, 0), description = "Location: Taverly"),
    CAMELOT(Position(2757, 3478, 0), description = "Location: Camelot", onTeleport = { player ->
        player.diaryManager.kandarinDiary.progress(KandarinDiaryEntry.CAMELOT_TELEPORT)
    }),
    CATHERBY(Position(2804, 3432, 0), description = "Location: Catherby"),
    AL_KHARID(Position(3293, 3179, 0), description = "Location: Al Kharid"),
    DRAYNOR(Position(3105, 3249, 0), description = "Location: Draynor"),
    KEBOS_LOWLANDS(Position(1310, 3618, 0), description = "Location: Kebos Lowlands");

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