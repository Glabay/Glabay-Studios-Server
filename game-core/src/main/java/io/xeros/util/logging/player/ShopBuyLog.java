package io.xeros.util.logging.player;

import java.util.Set;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.logging.PlayerLog;

public class ShopBuyLog extends PlayerLog {

    private final int shopId;
    private final String shopName;
    private final GameItem gameItem;

    public ShopBuyLog(Player player, int shopId, String shopName, GameItem gameItem) {
        super(player);
        this.shopId = shopId;
        this.shopName = shopName;
        this.gameItem = gameItem;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("items_received_shop", "items_received");
    }

    @Override
    public String getLoggedMessage() {
        return "Bought " + gameItem + " from " + shopName + " with id " + shopId;
    }
}
