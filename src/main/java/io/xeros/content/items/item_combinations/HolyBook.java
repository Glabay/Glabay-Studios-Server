package io.xeros.content.items.item_combinations;

import java.util.List;
import java.util.Optional;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemCombination;

public class HolyBook extends ItemCombination {

	public HolyBook(GameItem outcome, Optional<List<GameItem>> revertedItems, GameItem[] items) {
		super(outcome, revertedItems, items);
	}

	@Override
	public void combine(Player player) {
		super.items.forEach(item -> player.getItems().deleteItem2(item.id(), item.amount()));
		player.getItems().addItem(super.outcome.id(), super.outcome.amount());
		player.getDH().sendItemStatement("You combined the items and created a Holy book.", 3840);
		player.setCurrentCombination(Optional.empty());
		player.nextChat = -1;
	}

	@Override
	public void showDialogue(Player player) {
		player.getDH().sendStatement("The Holy book is untradeable.", "You cannot revert this item either.");
	}

}
