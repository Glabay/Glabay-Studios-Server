package io.xeros.content.items.item_combinations;

import java.util.List;
import java.util.Optional;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemCombination;

public class SaradominsBlessedSword extends ItemCombination {

	public SaradominsBlessedSword(GameItem outcome, Optional<List<GameItem>> revertedItems, GameItem[] items) {
		super(outcome, revertedItems, items);
	}

	@Override
	public void combine(Player player) {
		items.forEach(item -> player.getItems().deleteItem2(item.id(), item.amount()));
		player.getItems().addItem(outcome.id(), outcome.amount());
		player.getDH().sendItemStatement("You combined the items and created Saradomin's blessed sword.", 12809);
		player.setCurrentCombination(Optional.empty());
		player.nextChat = -1;
	}

	@Override
	public void showDialogue(Player player) {
		player.getDH().sendStatement("Saradomon's blessed sword is untradable.", "You can revert this but the saradomin sword will be lost.");
	}

}
