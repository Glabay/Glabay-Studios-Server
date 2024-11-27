package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.tob.TobConstants;
import io.xeros.content.minigames.tob.instance.TobInstance;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

/**
 * @author Arthur Behesnilian 12:29 AM
 */
public class Uncage extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        InstancedArea instance = player.getInstance();
        if (!(instance instanceof TobInstance)) {
            player.sendMessage("You must be within a Theater of Blood raid to use this command.");
            return;
        }
        TobInstance tob = (TobInstance) instance;
        if (tob.isFinalBossComplete()) {
            Boundary treasureRoom = TobConstants.LOOT_ROOM_BOUNDARY;
            if (player.getX() >= treasureRoom.getMinimumX() && player.getX() <= treasureRoom.getMaximumX()
                    && player.getY() >= treasureRoom.getMinimumY() && player.getY() <= treasureRoom.getMaximumY()) {
                player.sendMessage("You have no reason to use this command right now.");
                return;
            }
            player.moveTo(new Position(3168, 4320, instance.getHeight()));
        } else {
            player.sendMessage("Verzik Vitur must die before you can use this command.");
        }
    }
}
