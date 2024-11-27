package io.xeros.util.logging.player;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.logging.PlayerLog;

import java.util.List;
import java.util.Set;

public class ItemFlowerPokerTradeLog extends PlayerLog {

    private final String tradedWithLoginName;
    private final List<GameItem> received;
    private final List<GameItem> given;

    public ItemFlowerPokerTradeLog(Player player, String tradedWithLoginName, List<GameItem> received, List<GameItem> given) {
        super(player);
        this.tradedWithLoginName = tradedWithLoginName;
        this.received = received;
        this.given = given;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("items_received_fp", "items_received");
    }

    @Override
    public String getLoggedMessage() {
        return String.format("Flower pokered with [%s] (login name) Received [%s] Given [%s]", tradedWithLoginName, received, given);
    }
}
