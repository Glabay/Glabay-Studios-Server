package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.definition.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ImmutableItem;

/**
 * @author Arthur Behesnilian 1:26 PM
 */
public class Cash extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.getInventory().addToInventory(new ImmutableItem(Items.COINS, Integer.MAX_VALUE));
        player.sendMessage("You spawn a stack of cash.");
    }

}
