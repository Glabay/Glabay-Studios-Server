package io.xeros.content.items.item_combinations;

import java.util.List;
import java.util.Optional;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemCombination;

public class TentacleWhip extends ItemCombination {

	public TentacleWhip(GameItem outcome, Optional<List<GameItem>> revertedItems, GameItem[] items) {
		super(outcome, revertedItems, items);
	}

	@Override
	public void combine(Player player) {
		super.items.forEach(item -> player.getItems().deleteItem2(item.id(), item.amount()));
		player.getItems().addItem(super.outcome.id(), super.outcome.amount());
		player.getDH().sendItemStatement("You combined the items and created the Tentacle Whip.", 12006);
		player.setCurrentCombination(Optional.empty());
		player.nextChat = -1;
	}

	@Override
	public void showDialogue(Player player) {
		player.getDH().sendStatement("Once the tentacle is combined with the abyssal whip", "there is no going back. The whip will be lost forever.",
				"You will however receive the tentacle.");
	}

}
