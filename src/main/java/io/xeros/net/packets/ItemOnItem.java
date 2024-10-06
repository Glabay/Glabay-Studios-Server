package io.xeros.net.packets;

/**
 * @author Ryan / Lmctruck30
 */

import io.xeros.content.items.UseItem;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class ItemOnItem implements PacketType {

	public static boolean is(int item1, int item2, int itemUsed, int itemUsedOn) {
		return item1 == itemUsed && item2 == itemUsedOn || item2 == itemUsed && item1 == itemUsedOn;
	}

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		if (c.getMovementState().isLocked() || c.getLock().cannotInteract(c))
			return;
		if (c.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		c.interruptActions();
		int usedWithSlot = c.getInStream().readUnsignedWord();
		int itemUsedSlot = c.getInStream().readUnsignedWordA();
		if (usedWithSlot > c.playerItems.length - 1 || usedWithSlot < 0 || itemUsedSlot > c.playerItems.length - 1 || itemUsedSlot < 0) {
			return;
		}
		int useWith = c.playerItems[usedWithSlot] - 1;
		int itemUsed = c.playerItems[itemUsedSlot] - 1;
		if (useWith == -1 || itemUsed == -1) {
			return;
		}
		if (!c.getItems().playerHasItem(useWith, 1) || !c.getItems().playerHasItem(itemUsed, 1)) {
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		c.getPA().stopSkilling();
		if (Misc.isInDuelSession(c)) return;

		UseItem.ItemonItem(c, itemUsed, useWith, itemUsedSlot, usedWithSlot);
	}

}
