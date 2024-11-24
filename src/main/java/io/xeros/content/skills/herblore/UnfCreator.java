package io.xeros.content.skills.herblore;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.skills.herblore.PotionData.UnfinishedPotions;
import io.xeros.model.definition.Items;
import io.xeros.model.SlottedItem;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public class UnfCreator {

	private static final int CREATE_UNF_PRICE = 250;
	private static final int PRICE_WARN_AMOUNT = 20_000;

	public static int getPriceForInventory(Player player) {
		return player.getItems().getInventoryItems().stream().mapToInt(it -> {
			UnfinishedPotions unf = UnfinishedPotions.forNotedOrUnNotedHerb(it.id());
			return unf != null ? CREATE_UNF_PRICE * it.amount() : 0;
		}).sum();
	}

	public static void makeUnfPotionsFromInventory(Player player) {
		player.getPA().closeAllWindows();
		for (SlottedItem item : player.getItems().getInventoryItems()) {
			UnfinishedPotions unf = UnfinishedPotions.forNotedOrUnNotedHerb(item.id());
			if (unf == null)
				continue;
			if (!makeUnfPotion(player, item, unf, false)) {
				break;
			}
		}
	}

	/**
	 * @return if {@param warn} set to false, return true if potion was made successfully.
	 *         If {@param warn} is true and dialogue sent, return true.
	 */
	public static boolean makeUnfPotion(Player player, SlottedItem gameItem, UnfinishedPotions unf, boolean warn) {
		ItemDef usedItemDef = ItemDef.forId(gameItem.id());

		int price = gameItem.amount() * CREATE_UNF_PRICE;
		if (warn && price >= PRICE_WARN_AMOUNT) {
			String priceString = Misc.formatCoins(price);
			new DialogueBuilder(player).option(
					"Spend " + priceString + "?",
					new DialogueOption("Yes, spend " + priceString + " to make unfinished potions.", plr -> make(player, gameItem, usedItemDef, unf, price)),
					new DialogueOption("No, that's too much!", plr -> plr.getPA().closeAllWindows())
			).send();
			return true;
		}

		return make(player, gameItem, usedItemDef, unf, price);
	}

	/**
	 * @return true if the potion was made successfully.
	 */
	private static boolean make(Player player, SlottedItem gameItem, ItemDef usedItemDef, UnfinishedPotions unf, int price) {
		if (!player.getItems().playerHasItem(gameItem.id(), gameItem.amount()))
			return false;

		if (player.getLevelForXP(player.playerXP[Player.playerHerblore]) < unf.getLevelReq()) {
			player.sendMessage("You need a Herblore level of " + unf.getLevelReq() + " to make this potion.");
			player.getPA().closeAllWindows();
			return false;
		}

		if (!player.getItems().playerHasItem(Items.COINS, price)) {
			player.sendMessage("You don't have enough coins to do that!");
			return false;
		}

		if (player.getItems().playerHasItem(Items.VIAL_OF_WATER, gameItem.amount())) {
			player.getItems().deleteItem(Items.VIAL_OF_WATER, gameItem.amount());
		} else if (player.getItems().playerHasItem(Items.VIAL_OF_WATER_NOTED, gameItem.amount())) {
			player.getItems().deleteItem(Items.VIAL_OF_WATER_NOTED, gameItem.amount());
		} else {
			player.sendMessage("You don't have enough vials of water!");
			return false;
		}

		int unfPotionId = unf.getPotion().id();
		ItemDef unfPotionDef = ItemDef.forId(unfPotionId);

		GameItem unfPotionsItem = new GameItem(usedItemDef.isNoted() ? unfPotionDef.getNotedItemIfAvailable() : unfPotionDef.getId(), gameItem.amount());

		player.getItems().deleteItem(Items.COINS, price);
		player.getItems().deleteItem(gameItem.id(), gameItem.amount());
		player.getItems().addItemUnderAnyCircumstance(unfPotionsItem.id(), unfPotionsItem.amount());
		return true;
	}
}
