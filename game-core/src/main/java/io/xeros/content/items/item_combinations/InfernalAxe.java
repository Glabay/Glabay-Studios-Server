package io.xeros.content.items.item_combinations;

import java.util.List;
import java.util.Optional;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemCombination;

public class InfernalAxe extends ItemCombination {

	public InfernalAxe(GameItem outcome, Optional<List<GameItem>> revertedItems, GameItem[] items) {
		super(outcome, revertedItems, items);
	}

	@Override
	public void combine(Player player) {
		if (player.playerLevel[11] < 85) {
			player.sendMessage("You must have a firemaking level of at least 85 to do this.");
			player.getPA().closeAllWindows();
			return;
		}
		items.forEach(item -> player.getItems().deleteItem2(item.id(), item.amount()));
		player.getItems().addItem(outcome.id(), outcome.amount());
		//emote 4512
		player.startAnimation(4512);
		player.getDH().sendItemStatement("You combined the items and created an infernal pickaxe.", outcome.id());
		player.setCurrentCombination(Optional.empty());
		player.nextChat = -1;
	}

	@Override
	public void showDialogue(Player player) {
		player.getDH().sendStatement("Combining these are final.", "You cannot revert this.");
	}

}
