package io.xeros.content.minigames.tob.instance;

import java.util.Map;

import com.google.common.collect.Maps;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ImmutableItem;

public class FoodRewards {

    private final Map<String, Integer> pointsMap = Maps.newHashMap();

    public void award(Player player, int points) {
        String username = player.getLoginName();
        pointsMap.putIfAbsent(username, 0);
        pointsMap.put(username, pointsMap.get(username) + points);
        player.sendMessage("Gained @red@" + points + "@bla@ reward points, you now have @red@" + pointsMap.get(username) + "@bla@.");
    }

    public void openFoodRewards(Player player) {
        player.getPA().showInterface(21_490);
        player.getPA().sendString(21_503, "Points Available: " + pointsMap.getOrDefault(player.getLoginName(), 0));
    }

    public boolean handleBuy(Player player, int interfaceId, int slot) {
        if (interfaceId == 21_493) {
            if (slot < 0 || slot > 7) {
                return true;
            }
            String username = player.getLoginName();
            TobChestSupplies supplies = TobChestSupplies.values()[slot];
            int points = pointsMap.getOrDefault(username, 0);
            if (points < supplies.getCost()) {
                player.sendMessage("You don't have enough points to buy that!");
            } else {
                ImmutableItem reward = new ImmutableItem(supplies.getItemId(), 1);
                if (player.getInventory().hasRoomInInventory(reward)) {
                    player.getInventory().addToInventory(reward);
                    pointsMap.put(username, pointsMap.get(username) - supplies.getCost());
                    openFoodRewards(player);
                } else {
                    player.sendMessage("You don't have enough inventory space.");
                }
            }
            return true;
        }
        return false;
    }
}
