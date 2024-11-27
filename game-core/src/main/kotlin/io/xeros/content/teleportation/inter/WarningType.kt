package io.xeros.content.teleportation.inter

enum class WarningType(val spriteID: Int, val text: String, val buttonTexts : List<String> = emptyList()) {
    WILDY(
        2237,
        "Are you sure you want to teleport?\\n\\n\\n This location is dangerous. Please confirm your decision.",
        buttonTexts = listOf("I'm sure!","No!")
    ),
    DONATE(
        2239,
        "These teleports are locked\\n\\n\\n You need to be a donor to access this feature.\\nWould you like to donate now?",
        buttonTexts = listOf("Visit Site","Not Today")
    ),
    SPOTLIGHT(
        2238,
        "No active Spotlight Events\\n\\n\\n  Spotlight events include server-wide or world events.\\n\\nsuch as shooting stars."
    ),
    NONE(-1, "")
}