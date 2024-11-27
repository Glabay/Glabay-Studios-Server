package io.xeros.model.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.xeros.content.skills.hunter.impling.ItemRarity;
import io.xeros.model.definitions.ItemDef;
import io.xeros.util.Misc;
import org.apache.commons.lang3.RandomUtils;

/**
 * Represents an item that exists within the game. Items hold identification and value, and have the potential to contain other information as well.
 * 
 * @author Jason MacKeigan
 * @date Oct 20, 2014, 2:55:17 PM
 */
public class GameItem implements ItemInterface {

	/**
	 * All stackables (or all items with same id if {@param stackEverything} with the same
	 * id are combined into a single stack.
	 * @param items The item list.
	 * @param stackEverything True to stack every item with the same id, regardless of if they are normally stackable.
	 * @return The normalized list.
	 */
	public static List<GameItem> normalize(List<GameItem> items, boolean stackEverything) {
		List<GameItem> normalizedItems = new ArrayList<>();
		itemLoop: for (GameItem item : items) {
			for (GameItem normalizedItem : normalizedItems) {
				if (normalizedItem.id() == item.id() && (stackEverything || item.getDef().isStackable())) {
					normalizedItem.setAmount(normalizedItem.amount() + item.amount());
					continue itemLoop;
				}
			}

			normalizedItems.add(item);
		}

		return normalizedItems;
	}

	/**
	 * Compare two items based on {@link ItemDef#getShopValue()}, weighted towards the higher price.
	 * Does not take consider item amount.
	 */
	public static int comparePrice(GameItem a, GameItem b) {
		return Integer.compare(b.getDef().getShopValue(), a.getDef().getShopValue());
	}

	public static boolean isTradeable(int id) {
		return ItemDef.forId(id).isTradable();
	}

	private int id, amount;

	@JsonIgnore
	private int slot;

	@JsonIgnore
	private boolean stackable; // Jackson serialized isStackable method and can't deserialize without a field to write to.. TODO fix

	// For Jackson
	private GameItem() { }

	/**
	 * Constructs a new game item with an id and amount of 0
	 * 
	 * @param id the id of the item
	 */
	public GameItem(int id) {
		this.id = id;
		this.amount = 1;
	}

	public GameItem copy() {
		return new GameItem(id, amount);
	}

	public ItemDef getDef() {
		return ItemDef.forId(id());
	}
	
	/**
	 * Constructs a new game item with an id and amount
	 * 
	 * @param id the id of the item
	 * @param amount the amount of the item
	 */
	public GameItem(int id, int amount) {
		this(id);
		this.amount = amount;
	}

	public GameItem(int id, int amount, int slot) {
		this(id, amount);
		this.amount = amount;
		this.slot = slot;
	}

	/**
	 * Attempts to return the same item with a randomized amount.
	 * 
	 * @return the same item with a randomized amount
	 */
	public GameItem randomizedAmount() {
		if (amount == 1) {
			return this;
		}

		return new GameItem(id, RandomUtils.nextInt(1, amount));
	}

	public String getFormattedName(boolean coinColor) {
		String coins = Misc.formatCoins(amount());
		if (coinColor) {
			coins = Misc.colorWrap(Misc.getCoinColour(amount()), coins);
		}

		return getDef().getName() + " x " + coins;
	}

	/**
	 * Returns a new GameItem object with a new id and amount
	 * 
	 * @param id the item id
	 * @param amount the item amount
	 * @return a new game item
	 */
	public GameItem set(int id, int amount) {
		return new GameItem(id, amount);
	}

	/**
	 * Retries the item id for the game item
	 * 
	 * @return the item id
	 */
	@Override
	public int id() {
		return id;
	}

	/**
	 * Sets the item id
	 * @param id the item id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param id the id to set
	 */
	public void changeDrop(int id, int amount) {
		this.id = id;
		this.amount = amount;
	}

	/**
	 * Retrieves the amount of the game item
	 * 
	 * @return the amount
	 */
	@Override
	public int amount() {
		return amount;
	}

	/**
	 * Sets the amount of the game item
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * The slot the game item exists in the container
	 * 
	 * @return the slot
	 */
	@JsonIgnore
	public int getSlot() {
		return slot;
	}

	/**
	 * Determines if the item is stackable
	 * 
	 * @return true if the item is stackable, false if it is not.
	 */
	@JsonIgnore
	public boolean isStackable() {
		return getDef().isStackable();
	}

	@Override
	public String toString() {
		ItemDef definition = ItemDef.forId(id);
		String name = definition == null ? "null" : definition.getName();
		return "GameItem{" +
				"name=" + name +
				", id=" + id +
				", amount=" + Misc.insertCommas(String.valueOf(amount)) +
				'}';
	}

	public String getFormattedString() {
		ItemDef definition = ItemDef.forId(id);
		String name = definition == null ? "null" : definition.getName();
		return name + " x " + Misc.insertCommas(amount);
	}

	/** ITEM RARITY **/
	@JsonIgnore
	public ItemRarity rarity;

	public GameItem setRarity(ItemRarity rarity) {
		this.rarity = rarity;
		return this;
	}

	@JsonIgnore
	public ItemRarity getRarity() {
		return this.rarity;
	}

	public void incrementAmount(int amount) {
		this.amount += amount;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GameItem gameItem = (GameItem) o;
		return id == gameItem.id &&
				amount == gameItem.amount;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, amount);
	}
}