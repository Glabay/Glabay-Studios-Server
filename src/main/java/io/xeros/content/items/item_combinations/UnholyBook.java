package io.xeros.content.items.item_combinations;

import java.util.List;
import java.util.Optional;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemCombination;

public class UnholyBook extends ItemCombination {

	public UnholyBook(GameItem outcome, Optional<List<GameItem>> revertedItems, GameItem[] items) {
		super(outcome, revertedItems, items);
	}

	@Override
	public void combine(Player player) {
		super.items.forEach(item -> player.getItems().deleteItem2(item.id(), item.amount()));
		player.getItems().addItem(super.outcome.id(), super.outcome.amount());
		player.getDH().sendItemStatement("You combined the items and created an Unholy book.", 3842);
		player.setCurrentCombination(Optional.empty());
		player.nextChat = -1;
	}

	@Override
	public void showDialogue(Player player) {
		player.getDH().sendStatement("The Unholy book is untradeable.", "You cannot revert this item either.");
	}

}
