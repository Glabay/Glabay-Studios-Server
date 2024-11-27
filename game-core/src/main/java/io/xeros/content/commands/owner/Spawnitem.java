package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

/**
 * Puts a given amount of the item in the player's inventory.
 * 
 * @author Emiel
 */
public class Spawnitem extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		String[] args = input.split(" ");
		int itemId = Integer.parseInt(args[0]);
		int amount;
		int id_one, id_two;
		if (!ItemDef.getDefinitions().containsKey(itemId)) {
			player.sendMessage("Item out of bounds, can't spawn this item.");
			return;
		}
		switch (args.length) {
		case 1:
			player.getItems().addItem(itemId, 1);
			player.sendMessage("@cr18@@blu@Spawning 1 x " + ItemAssistant.getItemName(itemId) + ". [" + itemId + "]");
			break;

		case 2:
			amount = Misc.stringToInt(args[1]);
			player.getItems().addItem(itemId, amount);
			player.sendMessage("@cr18@@blu@Spawning " + amount + " x " + ItemAssistant.getItemName(itemId) + ". [" + itemId + "]");
			break;

		case 3:
			id_one = Misc.stringToInt(args[0]);
			id_two = Misc.stringToInt(args[1]);
			amount = Misc.stringToInt(args[2]);
			for (int i = id_one; i <= id_two; i++) {
				ItemDef itemList = ItemDef.forId(i);
				if (itemList == null) {
					continue;
				}
				if (ItemAssistant.getItemName(i).isEmpty() || 
					ItemAssistant.getItemName(i).equals("Unarmed") || 
					ItemAssistant.getItemName(i).equals("null")) {
					continue;
				}
				if (player.getItems().isNoted(i)) {
					continue;
				}
				if (player.getItems().freeSlots() == 0) {
					break;
				}
				player.getItems().addItem(i, amount);
				player.sendMessage("@cr18@@blu@Spawning " + amount + " x " + ItemAssistant.getItemName(i) + ". [" + itemId + "]");
			}
			break;
		}
	}
}
