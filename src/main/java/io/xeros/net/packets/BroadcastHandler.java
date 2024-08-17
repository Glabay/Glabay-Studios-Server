package io.xeros.net.packets;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import io.xeros.model.entity.player.broadcasts.BroadcastManager;
import io.xeros.model.entity.player.broadcasts.BroadcastType;

public class BroadcastHandler implements PacketType {

    @Override
    public void processPacket(Player player, int opcode, int size) {

        int index = player.getInStream().readUnsignedByte();

        if (index < 0 || index > BroadcastManager.broadcasts.length)
            return;

        Broadcast broadcast = BroadcastManager.broadcasts[index];

        if (broadcast == null) {
            System.err.println("Nulled broadcast for ID "+index);
            return;
        }

        BroadcastType type = broadcast.getType();

        switch (type) {

            case LINK:
                String URL = broadcast.getUrl();
                player.getPA().sendURL(URL);
                player.sendMessage("Opening link: "+URL);
                return;
            case MESSAGE:
                /**
                 * Not clickable
                 */
                return;
            case TELEPORT:
                Position pos = broadcast.getTeleport();
                if (Boundary.isIn(broadcast.getTeleport(), Boundary.WILDERNESS_PARAMETERS)) {
                    player.setDialogueBuilder(new DialogueBuilder(player).option("Teleport to Wild Event at "+Boundary.getWildernessLevel(pos.getX(), pos.getY())+" Wilderness?",
                            new DialogueOption("Yes", player1 ->
                        player.getPA().startTeleport(pos.getX(), pos.getY(), pos.getHeight(), "modern", false)),
                            new DialogueOption("No", player1 ->
                        player1.getPA().closeAllWindows())).send());
                    return;
                }
                player.getPA().startTeleport(pos.getX(), pos.getY(), pos.getHeight(), "modern", false);
                return;

        }



    }
}
