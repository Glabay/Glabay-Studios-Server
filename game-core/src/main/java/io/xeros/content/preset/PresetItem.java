package io.xeros.content.preset;

/**
 * 
 * @author Grant_ | www.rune-server.ee/members/grant_ | 9/30/19
 *
 */
public class PresetItem {

	private final int itemId;
	private final int amount;
	
	public PresetItem(int itemId, int amount) {
		this.itemId = itemId;
		this.amount = amount;
	}

	public int getItemId() {
		return itemId;
	}

	public int getAmount() {
		return amount;
	}
}
