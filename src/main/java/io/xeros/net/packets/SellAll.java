package io.xeros.net.packets;

import io.xeros.model.entity.player.Player;

/**
 * Bank All Items
 **/
public class SellAll implements PacketType {

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
		int removeSlot = c.getInStream().readUnsignedWordA();
		int interfaceId = c.getInStream().readUnsignedWord();
		int removeId = c.getInStream().readUnsignedWordA();
		int amount = c.getInStream().readUnsignedWordA();
		if (c.debugMessage)
			c.sendMessage("Sell All: interfaceid: "+interfaceId+", removeSlot: "+removeSlot+", removeID: " + removeId);
		
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (c.getLootingBag().isWithdrawInterfaceOpen() || c.getLootingBag().isDepositInterfaceOpen()) {
			if (c.getLootingBag().handleClickItem(removeId, c.getItems().getItemAmount(removeId))) {
				return;
			}
		}
		if (c.viewingRunePouch) {
			if (c.getRunePouch().handleClickItem(removeId, c.getItems().getItemAmount(removeId), interfaceId)) {
				return;
			}
		}
		switch (interfaceId) {
		
		case 3823:
			c.getShops().sellItem(removeId, removeSlot, amount);
			break;

		}
	}

}
