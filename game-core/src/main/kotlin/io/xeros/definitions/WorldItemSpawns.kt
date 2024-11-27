package io.xeros.definitions

import com.beust.klaxon.Klaxon
import io.xeros.AssetLoader
import io.xeros.model.entity.grounditem.GroundItemManager
import io.xeros.model.entity.grounditem.GroundItem
import io.xeros.model.entity.grounditem.GroundItemPickupState
import io.xeros.model.entity.player.Position
import io.xeros.model.items.GameItem
import io.xeros.util.ProgressbarUtils
import java.io.File

data class Location(val x : Int, val y : Int, val z : Int)

data class WorldItemData(
    val id : Int = 0,
    val location : Location = Location(0,0,0),
    val amt : Int = 0,
    val respawn : Int = 0
)

object WorldItemSpawns {

    fun init() {

        val itemSpawns = Klaxon().parseArray<WorldItemData>(File(AssetLoader.getFolder("Server/Items/"),"itemSpawns.json"))

        val progress = ProgressbarUtils.progress("World Item Spawns", itemSpawns!!.size)
        itemSpawns.forEach {
            val position = Position(it.location.x,it.location.y,it.location.z)
            addGroundItem(GameItem(it.id, it.amt), position, it.respawn)
            progress.step()
        }
        progress.close()

    }

    private fun addGroundItem(item : GameItem, position: Position, respawn: Int) {
        val item1 = GroundItem(
            item = item,
            pos = position,
            respawnTime = if(respawn == 0) 15 else respawn,
            state = GroundItemPickupState.INSTANT,
            worldSpawn = true
        )
        GroundItemManager.registerGroundItem(item1)
    }

}
