package io.xeros.model.entity.grounditem

import io.xeros.model.cycleevent.Event

class GameItemUpdateTask : Event<Any?>("", Any(), 10) {

    override fun execute() {
        val iter = GroundItemManager.items.iterator()

        while (iter.hasNext()) {
            val item = iter.next()

            // Display the ground item to nearby players if it can be seen
            if (!item.value && item.key.canSee()) {
                GroundItemManager.showForNearbyPlayers(item.key)
                item.setValue(true)
                GroundItemManager.mergeStacks(item.key)
            }

            // Remove the ground item to nearby players if it has expired
            if (!item.key.worldSpawn && item.key.shouldRemove()) {
                GroundItemManager.removeForNearbyPlayers(item.key)
                iter.remove()
            }
        }
    }

}