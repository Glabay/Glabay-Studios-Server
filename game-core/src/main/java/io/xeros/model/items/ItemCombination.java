package io.xeros.model.items;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import io.xeros.model.entity.player.Player;
import lombok.Getter;

/**
 * 
 * @author Jason MacKeigan
 * @since Nov 21, 2014, 12:38:16 PM
 */
public abstract class ItemCombination {
	/**
	 * List of items that are required in the combination
     * -- GETTER --
     *  List of the required items
     *

     */
	@Getter
    protected List<GameItem> items;

	/**
	 * The item received when the items are combined
     * -- GETTER --
     *  The item that we receive for combining the items
     *

     */
	@Getter
    protected GameItem outcome;

	/**
	 * The item that can be reverted from the combination, if possible.
	 */
	protected Optional<List<GameItem>> revertedItems;

	/**
	 * Creates a new item combination
	 * 
	 * @param items the game items required
	 */
	public ItemCombination(GameItem outcome, Optional<List<GameItem>> revertedItems, GameItem... items) {
		this.items = Arrays.asList(items);
		this.outcome = outcome;
		this.revertedItems = revertedItems;
	}

	/**
	 * Combines all the items to create the outcome
	 * 
	 * @param player the player combining the items
	 */
	public abstract void combine(Player player);

	/**
	 * Shows the initial dialogue with basic information about the combination
	 * 
	 * @param player the player
	 */
	public abstract void showDialogue(Player player);

	/**
	 * Reverts the outcome, if possible, back to the items used to combine it
	 * 
	 * @param player the player requesting reversion
	 */
	public void revert(Player player) {
		if (revertedItems.isEmpty()) {
			return;
		}
		if (player.getItems().freeSlots() < revertedItems.get().size()) {
			player.getDH().sendStatement("You need at least " + revertedItems.get().size() + " inventory slots to do this.");
			player.nextChat = -1;
			return;
		}
		player.getItems().deleteItem2(outcome.id(), outcome.amount());
		revertedItems.get().forEach(item -> player.getItems().addItem(item.id(), item.amount()));
		player.getDH().sendStatement("The " + ItemAssistant.getItemName(outcome.id()) + " has been split up.", "You have received some of the item(s) used in the making of",
				"this item.");
		player.nextChat = -1;
	}

	/**
	 * Sends the confirmation page to the player so they can choose to cancel it
	 * 
	 * @param player the player
	 */
	public void sendCombineConfirmation(Player player) {
		player.getDH().sendOption2("Combine items?", "Yes", "Cancel");
	}

	/**
	 * Sends the confirmation page to the player so thay revert the item, or cancel the decision.
	 * 
	 * @param player the player
	 */
	public void sendRevertConfirmation(Player player) {
		player.getDH().sendOption2("Revert item?", "Yes", "Cancel");
	}

	/**
	 * Determines if the player has all of the items required for the combination.
	 * 
	 * @param player the player making the combination
	 * @return true if they have all of the items, otherwise false
	 */
	public boolean isCombinable(Player player) {
		Optional<GameItem> unavailableItem = items.stream().filter(i -> !player.getItems().playerHasItem(i.id(), i.amount())).findFirst();
		return unavailableItem.isEmpty();
	}

	/**
	 * Determines if the items used match that required in the combination
	 * 
	 * @param item1 the first item
	 * @param item2 the second item
	 * @return true if all items match, otherwise false
	 */
	public boolean itemsMatch(GameItem item1, GameItem item2) {
		return items.stream().filter(itemValuesMatch(item1).or(itemValuesMatch(item2))).count() == 2;
	}

	private static Predicate<GameItem> itemValuesMatch(GameItem item) {
		return i -> i.id() == item.id() && i.amount() <= item.amount();
	}

    /**
	 * Determines if the item is revertable or not
	 *
     */
	public boolean isRevertable() {
		return revertedItems.isPresent();
	}

	/**
	 * Attempts to retrieve the revertable item, if it exists
	 * 
	 * @return the revertable item
	 */
	public Optional<List<GameItem>> getRevertItems() {
		return revertedItems;
	}

}
