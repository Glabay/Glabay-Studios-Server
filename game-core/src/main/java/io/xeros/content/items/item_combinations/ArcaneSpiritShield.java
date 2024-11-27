package io.xeros.content.items.item_combinations;

import java.util.List;
import java.util.Optional;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemCombination;

public class ArcaneSpiritShield extends ItemCombination {

	public ArcaneSpiritShield(GameItem outcome, Optional<List<GameItem>> revertedItems, GameItem[] items) {
		super(outcome, revertedItems, items);
	}

	@Override
	public void combine(Player player) {
		super.items.forEach(item -> player.getItems().deleteItem2(item.id(), item.amount()));
		player.getItems().addItem(super.outcome.id(), super.outcome.amount());
		player.getDH().sendItemStatement("You combined the items and created the Arcane Spirit Shield.", 12825);
		player.setCurrentCombination(Optional.empty());
		player.nextChat = -1;
	}

	@Override
	public void showDialogue(Player player) {
		player.getDH().sendStatement("Once the sigil is combined with the blessed spirit shield", "there is no going back. The items cannot be reverted.");
	}

}
