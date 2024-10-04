package io.xeros.net.packets.itemoptions;

import io.xeros.Server;
import io.xeros.content.combat.magic.SanguinestiStaff;
import io.xeros.content.items.Degrade.DegradableItem;
import io.xeros.content.items.pouch.RunePouch;
import io.xeros.content.teleportation.TeleportTablets;
import io.xeros.net.packets.PacketType;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import static io.xeros.content.items.Degrade.checkPercentage;

/**
 * Item Click 3 Or Alternative Item Option 1
 * 
 * @author Ryan / Lmctruck30
 * 
 *         Proper Streams
 */

public class ItemOptionFour implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		if (c.getMovementState().isLocked()) return;
		c.interruptActions();
		int itemId11 = c.getInStream().readSignedWordBigEndianA();
		int itemId1 = c.getInStream().readSignedWordA();
		int itemId = c.getInStream().readSignedWordA();

		if (c.debugMessage) c.sendMessage(String.format("ItemClick[item=%d, option=%d, interface=%d, slot=%d]", itemId, 4, -1, -1));

		if (c.getLock().cannotClickItem(c, itemId)) return;
		if (!c.getItems().playerHasItem(itemId, 1)) return;
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if (RunePouch.isRunePouch(itemId)) {
			c.getRunePouch().emptyBagToInventory();
			return;
		}
		TeleportTablets.operate(c, itemId);
		SanguinestiStaff.clickItem(c, itemId, 4);
		DegradableItem.forId(itemId).ifPresent(degradableItem -> checkPercentage(c, itemId));
		if (Misc.isInDuelSession(c)) return;

		var itemActionManager = Server.getItemOptionActionManager();
		if (itemActionManager.findHandlerById(itemId).isPresent()) {
			var npcAction = itemActionManager.findHandlerById(itemId).get();
			if (npcAction.performedAction(c, itemId, itemId1, itemId11, 4))
				return;
			logger.error("Unhandled Item Action 4: {} ", npcAction.getClass().getSimpleName());
		}
	}
}
