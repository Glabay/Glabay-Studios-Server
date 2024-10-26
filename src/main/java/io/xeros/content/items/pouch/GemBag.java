package io.xeros.content.items.pouch;

import java.util.stream.IntStream;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;

public class GemBag extends Pouch {
	/**
	 * Checks whether a player is allowed to configure the gem bag or not
	 * @return
	 */

	/**
	 * The gem bag id and boolean to set if we want to check if a player has one
	 */
	public static final int GEM_BAG_ID = 12020;

	/**
	 * The gem bag class
	 * @param player
	 */
	public GemBag(Player player) {
		this.player = player;
	}
	
	/**
	 * Attempts to withdraw all gems from the gem bag
	 */
	public void withdrawAll() {
		withdrawItems();
	}

	/**
	 * The id's of the gems you are allowed to store in the gem bag
	 */
	private final int[] uncutGems = { 1617, 1619, 1621, 1623, 1625, 1627, 1629, 1631, 6571, 19496 };
	
	/**
	 * Attempts to fill the bag with the gems a player has in their inventory
	 */
	public void fillBag() {

		for (int uncutGem : uncutGems) {
			if (player.getItems().playerHasItem(uncutGem, 1)) {
				addItemToGemBag(uncutGem, player.getItems().getItemAmount(uncutGem));
			}
		}
	}

	/**
	 * Attempts  to add the gems chosen to the gem bag
	 * @param id
	 * @param amount
	 */
	public void addItemToGemBag(int id, int amount) {
		boolean isUncut = IntStream.of(uncutGems).anyMatch(identification -> identification == id);
		boolean haveUncut = IntStream.of(uncutGems).anyMatch(identification -> player.getItems().playerHasItem(identification));
		if (!haveUncut) {
			player.sendMessage("You currently do not have any uncut gems to store.");
			return;
		}
		if (!isUncut) {
			player.sendMessage("You can only store uncut gems in the gem bag.");
			return;
		}
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		if (player.getItems().isStackable(id)) {
			return;
		}
		if (amount >= 28) {
			amount = player.getItems().getItemAmount(id);
		}
		if (id == GEM_BAG_ID) {
			player.sendMessage("Don't be silly.");
			return;
		}
		if (items.size() >= 61 && !(sackContainsItem(id) && player.getItems().isStackable(id))) {
			return;
		}
		if (id <= 0 || amount <= 0) {
			return;
		}
		if (countItems(id) + amount >= 61 || countItems(id) + amount <= 0) {
			player.sendMessage("You cannot store this many of this gem.");
			return;
		}
		player.sendMessage("Filled the gem bag with x" + amount + " " + ItemAssistant.getItemName(id));
		for (int amt = 0; amt < amount; amount--) {
			player.getItems().deleteItem(id, 1);
			addItemToList(id + 1, 1);
		}
	}
	
	/**
	 * Checks the amount and of what gem you have stored in the sack
	 */
	public void check() {
		int frame = 8149;
		int totalAmount = 0;
		player.getPA().resetQuestInterface();
		player.getPA().sendFrame126("@dre@                   Gem Bag", 8144);
		player.getPA().sendFrame126("", 8145);
		player.getPA().sendFrame126("", 8148);
		if (totalAmount == 0) {
			player.getPA().sendFrame126("@red@EMPTY", 8147);
		}
		for (int i = 0; i < 14; i++) {
			int id = 0;
			int amt = 0;

			if (i < items.size()) {
				GameItem item = items.get(i);
				if (item != null) {
					id = item.id();
					amt = item.amount();
				}
				totalAmount += amt;
				player.getPA().sendFrame126("@red@Total Amount: "+totalAmount+"/600", 8147);
				player.getPA().sendFrame126("@blu@x" + amt + " " + ItemAssistant.getItemName(id) + "", frame);
				frame++;
			}
			player.getPA().openQuestInterface();
		}
	}

}
