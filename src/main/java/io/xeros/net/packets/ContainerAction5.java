package io.xeros.net.packets;

import io.xeros.model.ContainerAction;
import io.xeros.model.ContainerActionType;
import io.xeros.model.entity.player.Player;

/**
 * Bank X Items
 **/
public class ContainerAction5 implements PacketType {

	public static final int PART1 = 135;

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
		int xRemoveSlot = c.getInStream().readSignedWordBigEndian();
		int xInterfaceId = c.getInStream().readUnsignedWordA();
		int xRemoveId = c.getInStream().readSignedWordBigEndian();

		ContainerAction action = new ContainerAction(ContainerActionType.ACTION_5, xInterfaceId, xRemoveId, xRemoveSlot);
		
		if (packetType == PART1) {
			c.xRemoveSlot = xRemoveSlot;
			c.xInterfaceId = xInterfaceId;
			c.xRemoveId = xRemoveId;
		}

		if (c.getLootingBag().handleClickItem(xRemoveId, -1)) {
			return;
		}

		if (c.viewingRunePouch) {
			c.getRunePouch().setEnterAmountVariables(c.xRemoveId, c.xInterfaceId);
		}
		if (c.debugMessage)
			c.sendMessage("ContainerAction5: interfaceid: "+c.xInterfaceId+", removeSlot: "+c.xRemoveSlot+", removeID: " + c.xRemoveId);

/* 		if (c.xInterfaceId == 3823) {
			c.getShops().sellItem(c.xRemoveId, c.xRemoveSlot, 100);// buy 100
			c.xRemoveSlot = 0;
			c.xInterfaceId = 0;
			c.xRemoveId = 0;
			return;
		} */
		/**
		 * Buy 500
		 */
		 if (c.xInterfaceId == 64016) {
			c.buyingX = true;
			 c.getPA().sendEnterAmount(0);
        }
		
		 if (c.xInterfaceId == 3823) {
				c.getShops().sellItem(c.xRemoveId, c.xRemoveSlot, 2000000000);//sell all
				c.xRemoveSlot = 0;
				c.xInterfaceId = 0;
				c.xRemoveId = 0;
				return;
			}


		if (packetType == PART1) {
			c.getPA().sendEnterAmount(0);
		}

	}
}
