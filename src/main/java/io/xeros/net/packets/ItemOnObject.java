package io.xeros.net.packets;

/**
 * @author Ryan / Lmctruck30
 */

import io.xeros.content.items.UseItem;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.tickable.impl.WalkToTickable;
import io.xeros.util.Misc;

public class ItemOnObject implements PacketType {

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

		int a = c.getInStream().readUnsignedWord();
		int objectId = c.getInStream().readInteger();
		int objectY = c.getInStream().readSignedWordBigEndianA();
		int b = c.getInStream().readUnsignedWord();
		int objectX = c.getInStream().readSignedWordBigEndianA();
		int itemId = c.getInStream().readUnsignedWord();

		c.objectX = objectX;
		c.objectY = objectY;
		c.xInterfaceId = -1;
		c.getPA().stopSkilling();

		WorldObject object = ClickObject.getObject(c, objectId, objectX, objectY);

		if (object == null) {
			return;
		}

		if (!c.getItems().playerHasItem(itemId, 1)) {
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
		if (Misc.isInDuelSession(c)) return;

		Position size = object.getObjectSize();
		c.setTickable(new WalkToTickable(c, object.getPosition(), size.getX(), size.getY(), player1 -> {

			c.getFarming().handleItemOnObject(itemId, objectId, objectX, objectY);
			switch (c.objectId) {

				case 2030: //Allows for items to be used from both sides of the furnace
					c.objectDistance = 4;
					c.objectXOffset = 3;
					c.objectYOffset = 3;
					break;
				case 26782:
					c.objectDistance = 7;
					break;
				case 33320:
					c.objectDistance = 5;
					break;
				case 33311:
					c.objectDistance = 3;//hespori
					break;
				case 18818:
				case 409:
					c.objectDistance = 3;
					break;
				case 884:
					c.objectDistance = 5;
					c.objectXOffset = 3;
					c.objectYOffset = 3;
					break;

				case 28900:
					c.objectDistance = 3;
					break;


				default:
					c.objectDistance = 1;
					c.objectXOffset = 0;
					c.objectYOffset = 0;
					break;

			}

			c.facePosition(objectX, objectY);
			UseItem.ItemonObject(c, objectId, objectX, objectY, itemId);
		}));
	}

}
