package io.xeros.net.packets;

import io.xeros.Server;
import io.xeros.content.Censor;
import io.xeros.model.entity.player.Player;
import io.xeros.punishments.PunishmentType;
import io.xeros.util.Misc;
import io.xeros.util.logging.player.PublicChatLog;

/**
 * Chat
 **/
public class Chat implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		byte[] chatText = new byte[4096];
		c.setChatTextEffects(c.getInStream().readUnsignedByteS());
		c.setChatTextColor(c.getInStream().readUnsignedByteS());
		c.setChatTextSize((byte) (packetSize - 2));

		c.inStream.readBytes_reverseA(chatText, c.getChatTextSize(), 0);

		if (c.getBankPin().requiresUnlock()) {
            c.getBankPin().open(2);
            c.sendMessage("You are muted until you unlock your bank pin.");
            return;
        }
		if (Server.getPunishments().isNetMuted(c)) {
			c.sendMessage("Your entire network has been muted. Other players cannot see your message.");
			return;
		}

		if (Server.getPunishments().contains(PunishmentType.MUTE, c.getLoginName())) {
			c.sendMessage("You are currently muted. Other players cannot see your message.");
			return;
		}

		if (System.currentTimeMillis() < c.muteEnd) {
			c.sendMessage("You are currently muted. Other players cannot see your message.");
			return;
		}

		String message = Misc.decodeMessage(chatText, c.getChatTextSize());

		if (message == null || !Misc.isValidChatMessage(message)) {
			c.sendMessage("Invalid chat message.");
			return;
		}

		for (byte b : chatText) {
			if (b >= Misc.validChars.length) {
				c.sendMessage("Invalid chat message.");
				return;
			}
		}

		if (Misc.isSpam(message)) {
			c.sendMessage("Please don't spam.");
			return;
		}

		Server.getLogging().write(new PublicChatLog(c, message));

		if (Censor.isCensored(c, message)) {
			return;
		}

		c.setChatTextUpdateRequired(true);
		c.setChatText(chatText);
	}

}
