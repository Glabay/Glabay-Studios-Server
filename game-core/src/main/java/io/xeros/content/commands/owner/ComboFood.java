package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * @author Arthur Behesnilian 2:52 PM
 */
public class ComboFood extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.getItems().deleteAllItems();
        for (int i = 0; i < 28; i += 4) {
            player.playerItems[i] = 392;
            player.playerItemsN[i] = 1;
        }
        for (int i = 1; i < 28; i += 4) {
            player.playerItems[i] = 6686;
            player.playerItemsN[i] = 1;
        }
        for (int i = 2; i < 28; i += 4) {
            player.playerItems[i] = 3145;
            player.playerItemsN[i] = 1;
        }
    }
}
