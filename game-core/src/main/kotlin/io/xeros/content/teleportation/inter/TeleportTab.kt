package io.xeros.content.teleportation.inter

enum class TeleportTab(val button : Int) {
    SPOTLIGHT(24518),
    CITIES(24520),
    TRAINING(24522),
    MINIGAMES(24524),
    BOSSES(24526),
    DUNGEONS(24528),
    WILDERNESS(24530),
    DONATE(24532);

   companion object {
       private val tabButtons = values().associateBy { it.button }

       fun fromButtonId(buttonId: Int): TeleportTab? = tabButtons[buttonId]
   }
}