package io.xeros.content.tournaments;

/**
 * 
 * @author Grant_ | www.rune-server.ee/members/grant_ | 10/6/19
 * 
 */
public class TourneyItem {
	private final int itemID;
	private final int itemAmount;
	
	public TourneyItem(int itemID, int itemAmount) {
		this.itemID = itemID;
		this.itemAmount = itemAmount;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	public int getItemAmount() {
		return itemAmount;
	}
}
