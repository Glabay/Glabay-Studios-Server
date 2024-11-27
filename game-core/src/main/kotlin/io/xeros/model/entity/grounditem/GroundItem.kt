package io.xeros.model.entity.grounditem

import io.xeros.model.entity.player.Player
import io.xeros.model.entity.player.Position
import io.xeros.model.items.GameItem
import org.joda.time.DateTime
import java.util.*
import java.util.concurrent.ThreadLocalRandom

data class GroundItem(
    val item: GameItem,
    val owner: Optional<Player> = Optional.empty(),
    val pos : Position = if(owner.isPresent) owner.get().position else Position(1, 1),
    val state: GroundItemPickupState = GroundItemPickupState.REGULAR,
    val respawnTime : Int? = null,
    val dropTime : DateTime = DateTime.now(),
    val worldSpawn : Boolean = false,
    val customDelayTime : Int = -1,
) {

    constructor(item : GameItem, owner: Optional<Player>, pos: Position) : this(item = item, owner = owner, pos = pos,
        GroundItemPickupState.REGULAR, null, DateTime.now(),false)

    constructor(item : GameItem, pos: Position) : this(item = item, owner = Optional.empty(), pos = pos,
        GroundItemPickupState.REGULAR, null, DateTime.now(),false)

    constructor(item : GameItem, pos: Position, customDelayTime : Int) : this(item = item, owner = Optional.empty(), pos = pos,
        GroundItemPickupState.REGULAR, null, DateTime.now(),false,customDelayTime)


    /**
     * Random ID to prevent merging of stacks
     */
    val id : Long = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)

    /**
     * If the [GroundItem]] appears on the ground to players other than the owner
     */
    fun canSee() = state == GroundItemPickupState.INSTANT || dropTime.plusSeconds(if (customDelayTime != -1) customDelayTime else state.displayDelay).isBeforeNow

    /**
     * Whether the player can pickup the [GroundItem]]
     */
    fun canPickup(player: Player) = (canSee()) || isOwner(player)

    /**
     * Whether the player owns the [GroundItem]]
     */
    fun isOwner(player: Player) = owner.isPresent && owner.get().displayName == player.displayName

    /**
     * Whether the [GroundItem] has been on the floor for too long and should be deleted
     */

    fun shouldRemove() = dropTime.plusMinutes(GroundItemManager.DELETE_TIME).isBeforeNow


    /**
     * Send a packet to display the [GroundItem] to the player
     */
    fun showTo(player: Player) {
        player.localGroundItems.add(this)
        player.items.createGroundItem(this)
    }

    /**
     * Send a packet to display the [GroundItem] to the player
     */
    fun removeFor(player: Player) {
        player.localGroundItems.remove(this)
        player.items.removeGroundItem(this)
    }

    /**
     * Check that the [GroundItem] is still registered
     */
    fun exists(): Boolean {
        return GroundItemManager.items.containsKey(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GroundItem

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}