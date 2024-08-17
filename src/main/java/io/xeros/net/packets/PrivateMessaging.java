package io.xeros.net.packets;

import io.xeros.Server;
import io.xeros.content.privatemessaging.FriendType;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

/**
 * Private messaging, friends etc
 **/
public class PrivateMessaging implements PacketType {

	public final int ADD_FRIEND = 188, SEND_PM = 126, REMOVE_FRIEND = 215, CHANGE_PM_STATUS = 95, REMOVE_IGNORE = 74,
			ADD_IGNORE = 133;

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		switch (packetType) {

		case ADD_FRIEND:
			String name = Misc.convertLongToFixedName(c.getInStream().readLong());

			if (c.hitDatabaseRateLimit(true))
				return;

			c.getFriendsList().addNew(name, FriendType.FRIEND);
			break;

		case SEND_PM:
			if (System.currentTimeMillis() < c.muteEnd) {
				c.sendMessage("You are muted for breaking a rule.");
				return;
			}
			if (Server.getPunishments().isNetMuted(c)) {
				c.sendMessage("Your entire network has been muted. Other players cannot see your message.");
				return;
			}
			c.muteEnd = 0;
			final long recipient = c.getInStream().readLong();
			int pm_message_size = packetSize - 8;
			final byte[] pm_chat_message = new byte[pm_message_size];
			c.getInStream().readBytes(pm_chat_message, pm_message_size, 0);
			c.getFriendsList().sendPrivateMessage(Misc.convertLongToFixedName(recipient), pm_chat_message);
			break;

		case REMOVE_FRIEND:
			name = Misc.convertLongToFixedName(c.getInStream().readLong());
			c.getFriendsList().remove(name, FriendType.FRIEND);
			break;

		case REMOVE_IGNORE:
			name = Misc.convertLongToFixedName(c.getInStream().readLong());
			c.getFriendsList().remove(name, FriendType.IGNORE);
			break;

		case CHANGE_PM_STATUS:
			c.getInStream().readUnsignedByte();
			c.setPrivateChat(c.getInStream().readUnsignedByte());
			c.getInStream().readUnsignedByte();
			c.getFriendsList().updateOnlineStatusForOthers();
			break;

		case ADD_IGNORE:
			name = Misc.convertLongToFixedName(c.getInStream().readLong());

			if (c.hitDatabaseRateLimit(true))
				return;

			c.getFriendsList().addNew(name, FriendType.IGNORE);
			break;

		}

	}
}
