package io.xeros.model.entity.player.broadcasts;

import io.xeros.model.entity.player.PlayerHandler;

import java.util.Objects;

public class BroadcastManager {

    public static Broadcast[] broadcasts = new Broadcast[1000];

    public static void removeBroadcast(int index) {
        if (broadcasts[index] != null) {
            broadcasts[index] = null;
        }
    }

    public static void addIndex(Broadcast broadcast) {
        int index = getFreeIndex();

        if (index == -1) {
            System.err.println("Error adding index.. broadcast list is full!");
            return;
        }

        broadcast.setIndex(index);

        broadcasts[index] = broadcast;

        PlayerHandler.getPlayers().stream().filter(Objects::nonNull).forEach(p -> {
            if (broadcast.sendChatMessage)
                p.sendMessage(broadcast.getMessage());
            p.getPA().sendBroadCast(broadcasts[index]);
        });
    }

    public static int getFreeIndex() {
        for (int i = 0; i < broadcasts.length; i++) {
            Broadcast broadcast = broadcasts[i];
            if (broadcast == null)
                return i;
        }
        return -1;
    }
}
