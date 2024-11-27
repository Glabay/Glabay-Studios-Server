package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.worldevent.WorldEventContainer;
import io.xeros.content.worldevent.impl.HesporiWorldEvent;
import io.xeros.model.entity.player.Player;

public class Starthespori extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        WorldEventContainer.getInstance().startEvent(new HesporiWorldEvent());
        player.sendMessage("Hespori will start soon.");
    }
}
