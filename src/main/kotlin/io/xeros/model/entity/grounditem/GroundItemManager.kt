package io.xeros.model.entity.grounditem

import io.xeros.model.cycleevent.CycleEvent
import io.xeros.model.cycleevent.CycleEventContainer
import io.xeros.model.cycleevent.CycleEventHandler
import io.xeros.model.entity.player.Player
import io.xeros.model.entity.player.PlayerHandler
import io.xeros.model.entity.player.Position
import io.xeros.model.items.GameItem
import io.xeros.model.items.ImmutableItem
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object GroundItemManager {

    /**
     * Time before a [GroundItem] is deleted in minutes.
     */
    val DELETE_TIME = 3

    /**
     * All spawned [GroundItem]s.
     */
    val items = ConcurrentHashMap<GroundItem, Boolean>()

    /**
     * Merge stackable items on the same position that are visible into one
     */
    internal fun mergeStacks(groundItem: GroundItem, owner: Player? = null) {

        // Only stackable items will merge
        if (!groundItem.item.def.isStackable) return

        // Do not stack items with attributes
        //if(groundItem.item.attributes.isNotEmpty()) return

        // Find all items with the same id and position that are visible
        val stack = getItemsAtPositions(groundItem.pos)
            .filter { if (owner != null) it.isOwner(owner) || it.canSee() else it.canSee() }
            .filter { it.item.id() == groundItem.item.id() }

        // If there is only 1 item, there is nothing to merge
        if (stack.size == 1) return

        // Calculate the total amount of items
        val newSize = stack.sumOf { it.item.amount() }

        // Remove all old ground items at the position
        stack.forEach {
            items.remove(it)
            removeForNearbyPlayers(it)
        }

        // Create a single ground item with an amount equal to all those removed
        val newItem = GroundItem(
            owner = if (owner == null) Optional.empty() else Optional.of(owner),
            item = GameItem(groundItem.item.id(), newSize),
            pos = groundItem.pos,
            state = if (owner == null) GroundItemPickupState.INSTANT else groundItem.state
        )

        registerGroundItem(newItem)
    }

    /**
     * Send a packet to display the [GroundItem] for all nearby players
     */
    internal fun showForNearbyPlayers(item: GroundItem) {
        PlayerHandler.getPlayers()
            .filterNotNull()
            .filter { it.position.isWithinDistance(item.pos, 64) }
            .filter { !it.localGroundItems.contains(item) }
            .forEach { item.showTo(it) }
    }

    /**
     * Send a packet to remove the [GroundItem] for all nearby players
     */
    internal fun removeForNearbyPlayers(item: GroundItem) {
        PlayerHandler.getPlayers().filterNotNull()
            .filter { it.position.isWithinDistance(item.pos, 64) }
            .forEach { item.removeFor(it) }
    }

    /**
     * Register a [GroundItem] and display it for nearby players if required
     */
    fun registerGroundItem(groundItem: GroundItem) {
        registerGroundItem(groundItem, true)
    }


    /**
     * Register a [GroundItem] and display it for nearby players if required
     */
    fun registerGroundItem(groundItem: GroundItem, show : Boolean = true) {
        items[groundItem] = groundItem.canSee()

        if (show && groundItem.canSee()) {
            showForNearbyPlayers(groundItem)
            return
        } else if (show && groundItem.owner.isPresent && groundItem.canPickup(groundItem.owner.get())) {
            groundItem.showTo(groundItem.owner.get())
        }
    }

    /**
     * Deregister a [GroundItem] and remove it for nearby players
     */
    fun deregisterGroundItem(groundItem: GroundItem) {
        if (groundItem.state == GroundItemPickupState.NO_PICKUP) return

        if(items.containsKey(groundItem)) {
            items.remove(groundItem)
        }
        removeForNearbyPlayers(groundItem)

        if (groundItem.respawnTime != null) {
            CycleEventHandler.getSingleton().addEvent(Any(), object : CycleEvent() {
                override fun execute(container: CycleEventContainer) {
                    registerGroundItem(groundItem.copy())
                    container.stop()
                }
            }, groundItem.respawnTime)
        }
    }

    fun deregisterPlayerOwned(player: Player): MutableSet<GroundItem> {
        val iter = items.iterator()
        val removed = mutableSetOf<GroundItem>()

        while(iter.hasNext()) {
            val entry = iter.next()
            val item = entry.key

            if(item.owner.isPresent && item.owner.get() == player) {
                removed.add(item)
                iter.remove()
                deregisterGroundItem(item)
            }
        }

        return removed
    }

    fun onRegionChange(player: Player) {

        // Clear the player's local items
        player.localGroundItems.clear()

        // Find nearby items that are visible and should be displayed
        val nearbyVisible = items
            .filter { it.key.pos.isWithinDistance(player.position, 64) }
            .filter { it.value || it.key.isOwner(player) }

        // Remove existing ground items that should no longer be visible
        player.localGroundItems
            .filter { !nearbyVisible.containsKey(it) }
            .forEach { it.removeFor(player) }

        // Add ground items that should be visible and are not already sent to the player
        nearbyVisible
            .filter { !player.localGroundItems.contains(it.key) }
            .forEach { it.key.showTo(player) }
    }

    /**
     * Finds all registered [GroundItem] at the specified position
     */
    private fun getItemsAtPositions(position: Position) = items.filter { it.key.pos == position }.map { it.key }

    /**
     * Finds all registered [GroundItem] at the specified position with the specified id
     */
    fun getItemWithId(position: Position, id: Int) =
        items.filterKeys { it.pos == position && it.item.id() == id }.map { it.key }.firstOrNull()

    /**
     * Finds all registered [GroundItem] at the specified position with the specified id
     */
    fun getVisibleItemWithId(position: Position, id: Int, player: Player) =
        items.filterKeys { it.pos == position && it.item.id() == id && it.canPickup(player) }.map { it.key }
            .firstOrNull()

    /**
     * Finds all registered [GroundItem] at the specified position owned by the player
     */
    fun getItemsAtPositions(position: Position, owner: Player) =
        items.keys.filter { it.pos == position && it.isOwner(owner) }

    /**
     * Finds all registered [GroundItem] at the specified position owned by the player
     */
    fun getItemsWithinDistance(position: Position, radius: Int = 64) =
        items.keys.filter { it.pos == position && it.pos.isWithinDistance(position, radius) }

    /**
     * Finds all registered [GroundItem] at the specified position that can be seen
     */
    fun getVisibleItemsAtPosition(position: Position) = items.keys.filter { it.pos == position && it.canSee() }

    /**
     * Finds all registered [GroundItem] at the specified position that can be picked up by the player
     */
    fun getVisibleItemsAtPosition(position: Position, player: Player) =
        items.keys.filter { it.pos == position && it.canPickup(player) }

    /**
     * Check if an item with the same id is spawned at the current position
     */
    fun itemExists(position: Position, id: Int): Boolean {
        return items.keys.any { it.pos == position && it.item.id() == id }
    }

    /**
     * Check if an item with the same id is spawned at the current position
     */
    fun itemExists(groundItem: GroundItem): Boolean {
        return items.containsKey(groundItem)
    }

    /**
     * Register a [GroundItem] owned by the specified player at their current position by default
     */
    fun dropItem(player: Player, item: GameItem, position: Position = player.position): GroundItem {
        val groundItem = GroundItem(item, Optional.of(player), position)
        groundItem.showTo(player)
        items[groundItem] = groundItem.canSee()
        mergeStacks(groundItem, player)
        return groundItem
    }

    fun pickupItem(player: Player, groundItem: GroundItem) {
        if(itemExists(groundItem.pos, groundItem.item.id())) {
            val item = ImmutableItem(groundItem.item.id(),groundItem.item.amount());
            if (player.inventory.hasRoomInInventory(item)) {
                player.inventory.addToInventory(item)
                deregisterGroundItem(groundItem)
            } else {
                player.sendMessage("Your inventory is too full.")
            }
        }
    }

}