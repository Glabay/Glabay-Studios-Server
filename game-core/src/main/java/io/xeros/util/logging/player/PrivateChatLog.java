package io.xeros.util.logging.player;

import java.util.Set;

import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;
import io.xeros.util.logging.PlayerLog;

public class PrivateChatLog extends PlayerLog {

    private final String message;
    private final String recipient;

    public PrivateChatLog(Player player, String message, String recipient) {
        super(player);
        this.message = message;
        this.recipient = recipient;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("chat_private", "chat_all");
    }

    @Override
    public String getLoggedMessage() {
        return "Recipient = " + recipient + ", message = " + message;
    }
}
