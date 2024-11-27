package io.xeros.content.items.pouch;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;

public class HerbSack extends Pouch {

	/**
	 * The herb sack id and boolean to set if we want to check if a player has a herb sack
	 */
	public static final int HERB_SACK_ID = 13226;

	/**
	 * The herb sack class
	 * @param player
	 */
	public HerbSack(Player player) {
		this.player = player;
	}
	
	/**
	 * Attempts to withdraw all herbs from the herb sack
	 */
	public void withdrawAll() {
		withdrawItems();
	}

	/**
	 * The id's of the herbs you are allowed to store in the herb sack
	 */
	private final int[] cleanHerbs = { 249, 251, 253, 255, 257, 2998, 259, 261, 263, 3000, 265, 2481, 267, 269 };
	
	/**
	 * Attempts to fill the sack with the herbs a player has in their inventory
	 */
	public void fillSack() {

		for (int cleanHerb : cleanHerbs) {
			if (player.getItems().playerHasItem(cleanHerb, 1)) {
				addItemToHerbSack(cleanHerb, player.getItems().getItemAmount(cleanHerb));
			}
		}
	}

	/**
	 * Attempts  to add the herbs chosen to the herb sack
	 * @param id
	 * @param amount
	 */
	public void addItemToHerbSack(int id, int amount) {
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		if (player.getItems().isStackable(id)) {
			return;
		}
		if (amount >= 28) {
			amount = player.getItems().getItemCount(id, false);
		}
		if (id == HERB_SACK_ID) {
			player.sendMessage("Don't be silly.");
			return;
		}
		if (!(id >= 249 && id <= 269) && id != 2481 && id != 3000 && id != 2998) {
			player.sendMessage("You can only store clean herbs in the herb sack.");
			return;
		}
		if (items.size() >= 14 && !(sackContainsItem(id) && player.getItems().isStackable(id))) {
			player.sendMessage("Herb sack cannot hold more than 14 different herbs.");
			return;
		}
		if (id <= 0 || amount <= 0) {
			return;
		}
		if (countItems(id) + amount >= 51 || countItems(id) + amount <= 0) {
			return;
		}
		player.sendMessage("Filled the sack with x" + amount + " " + ItemAssistant.getItemName(id));
		for (int amt = 0; amt < amount; amount--) {
			player.getItems().deleteItem(id, 1);
			addItemToList(id + 1, 1);
		}
	}
	
	/**
	 * Checks the amount and of what herb you have stored in the sack
	 */
	public void check() {
		int frame = 8149;
		int totalAmount = 0;
		player.getPA().resetQuestInterface();
		player.getPA().sendFrame126("@dre@                   Herb Sack", 8144);
		player.getPA().sendFrame126("", 8145);
		player.getPA().sendFrame126("", 8148);
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
				player.getPA().sendFrame126("@red@Total Amount: "+totalAmount+"/700", 8147);
				player.getPA().sendFrame126("@blu@x" + amt + " " + ItemAssistant.getItemName(id) + "", frame);
				frame++;
			}
			player.getPA().openQuestInterface();
		}
	}

}
