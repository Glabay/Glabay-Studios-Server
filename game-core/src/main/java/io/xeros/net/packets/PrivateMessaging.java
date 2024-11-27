package io.xeros.net.packets;

import io.xeros.Server;
import io.xeros.content.privatemessaging.FriendType;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

/**
 * Private messaging, friends etc
 **/
public class PrivateMessaging implements PacketType {

    public final int ADD_FRIEND = 188;
    public final int SEND_PM = 126;
    public final int REMOVE_FRIEND = 215;
    public final int CHANGE_PM_STATUS = 95;
    public final int REMOVE_IGNORE = 74;
    public final int ADD_IGNORE = 133;

    @Override
    public void processPacket(Player player, int packetType, int packetSize) {
        var friendName = Misc.convertLongToFixedName(player.getInStream().readLong());
        switch (packetType) {
            case ADD_FRIEND, ADD_IGNORE -> {
                if (player.hitDatabaseRateLimit(true)) return;
                player.getFriendsList().addNew(friendName, packetType == ADD_FRIEND ? FriendType.FRIEND : FriendType.IGNORE);
            }
            case REMOVE_FRIEND, REMOVE_IGNORE ->
                player.getFriendsList().remove(friendName, packetType == REMOVE_FRIEND ? FriendType.FRIEND : FriendType.IGNORE);
            case CHANGE_PM_STATUS -> {
                player.getInStream().readUnsignedByte();
                player.setPrivateChat(player.getInStream().readUnsignedByte());
                player.getInStream().readUnsignedByte();
                player.getFriendsList().updateOnlineStatusForOthers();
            }
            case SEND_PM -> {
                if (System.currentTimeMillis() < player.muteEnd) {
                    player.sendMessage("You are muted for breaking a rule.");
                    return;
                }
                if (Server.getPunishments().isNetMuted(player)) {
                    player.sendMessage("Your entire network has been muted. Other players cannot see your message.");
                    return;
                }
                player.muteEnd = 0;
                var recipient = player.getInStream().readLong();
                var pmMessageSize = packetSize - 8;
                var pmChatMessage = new byte[pmMessageSize];
                player.getInStream().readBytes(pmChatMessage, pmMessageSize, 0);
                player.getFriendsList().sendPrivateMessage(Misc.convertLongToFixedName(recipient), pmChatMessage);
            }
        }

    }
}
