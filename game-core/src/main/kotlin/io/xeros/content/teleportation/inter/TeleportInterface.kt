package io.xeros.content.teleportation.inter

import io.xeros.content.teleportation.inter.teleports.*
import io.xeros.content.wildwarning.WildWarning
import io.xeros.model.entity.player.Player
import io.xeros.model.entity.player.Right
import io.xeros.model.items.ImmutableItem

class TeleportInterface(private val player: Player) {

    private var currentTab = TeleportTab.CITIES

    private var warningType = WarningType.NONE

    private var currentlyClicked : TeleportEntry? = null

    private var cofferAmount = 0

    private var teleports : List<TeleportEntry> = emptyList()

    private var favourites : MutableMap<String,String> = emptyMap<String, String>().toMutableMap()

    fun onLogin() = updateObjectActions()

    fun openInterface() {
        if (!player.controller.canMagicTeleport(player)) {
            player.sendMessage("You can't teleport right now.")
            return
        }
        if (player.position.inWild() && player.wildLevel > 20) {
            player.sendMessage("You can't teleport above level 20 in the wilderness.")
            return
        }
        openTab()
        player.pa.showInterface(24511)
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun openTab(tab: TeleportTab? = null) {
        val displayTab = tab ?: currentTab

        TeleportTab.entries.forEach {
            player.pa.sendSprite(it.button, 2230,2231)
        }
        player.pa.sendSprite(displayTab.button, 2232)

        teleports = getTeleportsForTab(displayTab)

        populateTeleports()
        if (tab != null) {
            currentTab = tab
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun getTeleportsForTab(teleportTab: TeleportTab) : List<TeleportEntry> = when (teleportTab) {
        TeleportTab.CITIES -> Cities.entries
        TeleportTab.TRAINING -> Training.entries
        TeleportTab.DUNGEONS -> Dungeons.entries
        TeleportTab.BOSSES -> Bosses.entries
        TeleportTab.MINIGAMES -> Minigames.entries
        TeleportTab.WILDERNESS -> Wilderness.entries
        else -> Cities.entries

    }

    private fun updatePanelInfo() {
        player.pa.sendString(24536,"${favourites.size} / 3")
        player.pa.sendString(24537,"$cofferAmount")
        updateObjectActions()
    }

    //Disabled since this object is custom
    private fun updateObjectActions() {
        //val options = listOf("Teleport") + favourites.keys.toList()
       // player.pa.sendSceneOptions(PlayerAssistant.SceneType.OBJECT, 60005, options.toTypedArray())
    }


    private fun populateTeleports() {
        teleports.forEachIndexed { index, teleportEntry ->
            updateTeleportDisplay(index, teleportEntry)
        }
        val scrollSize = calculateScrollSize()
        player.pa.setScrollableMaxHeight(28230, scrollSize)
        handleContainerWarning(warningType)
    }

    private fun updateTeleportDisplay(index: Int, teleportEntry: TeleportEntry) {
        val rasterStartID = 28231
        val spriteStartID = 28291
        val nameStartID = 28351
        val descStartID = 28411
        val favButtonStartID = 28471
        val coinsSpriteStartID = 28531
        val priceStringStartID = 28591

        player.pa.sendString(nameStartID + index, teleportEntry.getDisplayName())
        player.pa.sendString(descStartID + index, teleportEntry.description.ifEmpty { teleportEntry.getDisplayName() })
        player.pa.sendSprite(favButtonStartID + index, if (favourites.containsKey(teleportEntry.getDisplayName())) 2235 else 2234)
        player.pa.sendSprite(spriteStartID + index, if (teleportEntry.spriteID == -1) 2236 else teleportEntry.spriteID)

        val isFree = teleportEntry.price == -1
        player.pa.sendInterfaceHidden(coinsSpriteStartID + index, isFree)
        player.pa.sendInterfaceHidden(priceStringStartID + index, isFree)
        if (!isFree) {
            player.pa.sendString(priceStringStartID + index, "${teleportEntry.price}")
        }

        val shouldWarn = warningType != WarningType.NONE
        player.pa.sendInterfaceAction(rasterStartID + index, shouldWarn)
        player.pa.sendInterfaceAction(favButtonStartID + index, shouldWarn)
    }

    private fun calculateScrollSize() = 284.coerceAtLeast(teleports.size * 40)

    private fun handleContainerWarning(type: WarningType) {
        with(player.pa) {
            sendInterfaceHidden(580, type == WarningType.NONE)
            if (type != WarningType.NONE) {
                sendSprite(582, type.spriteID)
                sendString(583, type.text)
                if (type.buttonTexts.isNotEmpty()) {
                    sendInterfaceHidden(59511, false)
                    sendString(59513, type.buttonTexts[0])
                    sendString(59515, type.buttonTexts[1])
                } else {
                    sendInterfaceHidden(59511, true)
                }
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun onButton(buttonID: Int) {
        when (buttonID) {
            24515 -> player.pa.closeAllWindows()
            in TeleportTab.entries.map { it.button } -> handleTabSwitch(TeleportTab.fromButtonId(buttonID))
            in 28231..28291 -> teleport(teleports[buttonID - 28231])
            in 28471..28531 -> toggleFavorite(buttonID - 28471)
            59514 -> resetWarning()
            59512 -> handleWarningButton()
        }
    }

    private fun handleTabSwitch(tab: TeleportTab?) {
        tab?.let {
            warningType = when {
                tab == TeleportTab.DONATE && !player.rights.isOrInherits(Right.REGULAR_DONATOR) -> WarningType.DONATE
                tab == TeleportTab.SPOTLIGHT -> WarningType.SPOTLIGHT
                else -> WarningType.NONE
            }

            openTab(it)
        }
    }

    private fun toggleFavorite(teleportIndex: Int) {
        val teleportEntry = teleports[teleportIndex]
        val displayName = teleportEntry.getDisplayName()
        if (favourites.containsKey(displayName)) {
            favourites.remove(displayName)
        } else if (favourites.size < 3) {
            favourites[displayName] = currentTab.name
        }
        player.pa.sendSprite(28471 + teleportIndex, if (favourites.containsKey(teleportEntry.getDisplayName())) 2235 else 2234)
        updatePanelInfo()
        updateObjectActions()
    }

    private fun resetWarning() {
        warningType = WarningType.NONE
        handleContainerWarning(warningType)
    }

    private fun handleWarningButton() {
        if (warningType == WarningType.WILDY) {
            currentlyClicked?.let {
                teleport(it, true)
                currentlyClicked = null
            }
        } else {
            println("Soon")
        }

        resetWarning()
    }

    fun teleport(entry: TeleportEntry, force : Boolean = false) {
        if ((WildWarning.isWarnable(player, entry.position.x, entry.position.y, entry.position.height) || entry.isDangerous) && !force) {
            currentlyClicked = entry
            warningType = WarningType.WILDY
            handleContainerWarning(WarningType.WILDY)
            return
        }
        if (entry.price != -1) {
            if (!player.inventory.containsAll(ImmutableItem(995,entry.price))) {
                player.sendMessage("Not enough coins: ${entry.price}")
                return
            }
            player.items.deleteItem(995,entry.price)
        }
        currentlyClicked = null
        player.pa.startTeleport(entry.position.x, entry.position.y, entry.position.height, "modern");
    }

    fun onObjectClick(index: Int) {
        when(index) {
            1 -> openInterface()
            2 -> quickFavouriteTeleport(0)
            3 -> quickFavouriteTeleport(1)
            4 -> quickFavouriteTeleport(2)
        }
    }

    private fun quickFavouriteTeleport(index : Int) {
        val displayName = favourites.keys.toList()[index]
        val tab = favourites.values.toList()[index]
        val teleport = getTeleportsForTab(TeleportTab.valueOf(tab)).find { it.getDisplayName() == displayName }
        if (teleport != null) {
            teleport(teleport,true)
        } else {
            player.sendMessage("Oh Sorry: $displayName has been removed removing from favourites")
        }

    }

}
