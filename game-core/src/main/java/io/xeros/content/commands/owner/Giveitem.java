package io.xeros.content.commands.owner;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

/**
 * Give a certain amount of an item to a player.
 * 
 * @author Emiel
 */
public class Giveitem extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		try {
			String[] args = input.split("-");
			if (args.length != 3) {
				throw new IllegalArgumentException();
			}
			String playerName = args[0];
			int itemID = Integer.parseInt(args[1]);
			int amount = Misc.stringToInt(args[2]);

			Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

			if (optionalPlayer.isPresent()) {
				Player c2 = optionalPlayer.get();

				if (c2.getMode().isIronmanType()) {
					if (!c.getRights().isOrInherits(Right.OWNER)) {
						c.sendMessage("You cannot give items to these players because of their respective game modes.");
						return;
					}
				}

				if (c2.getItems().hasRoomInInventory(itemID, amount)) {
					c2.getItems().addItem(itemID, amount);
					c2.sendMessage("You have just been given " + amount + " of item: " + ItemAssistant.getItemName(itemID) + " by: " + Misc.optimizeText(c.getDisplayName()));
					c.sendMessage("You have just given " + amount + " of item: " + ItemAssistant.getItemName(itemID) + " to inventory.");
				} else if (c2.getBank().hasRoomFor(itemID, amount)) {
					c2.getItems().addItemToBankOrDrop(itemID, amount);
					c2.sendMessage("You have just been given " + amount + " of item: " + ItemAssistant.getItemName(itemID) + " by: " + Misc.optimizeText(c.getDisplayName()));
					c2.sendMessage("It is in your bank because you didn't have enough space in your inventory.");
					c.sendMessage("You have just given " + amount + " of item: " + ItemAssistant.getItemName(itemID) + " to bank or dropped.");
				} else {
					c.sendMessage("No space in player's bank or inventory for item.");
				}
			} else {
				c.sendMessage(playerName + " is not online.");
			}
		} catch (Exception e) {
			c.sendMessage("Error. Correct syntax: ::giveitem-player-itemid-amount");
		}
	}
}
