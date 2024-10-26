package io.xeros.content;

import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.inventory.Inventory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Collection box is just a place to put items that the player couldn't claim because they didn't have enough space or other reasons.
 */
public class CollectionBox {

    private final Inventory inventory = new Inventory(128);

    public void add(Player player, GameItem gameItem) {
        Optional<GameItem> add = inventory.add(gameItem.copy());
        player.sendMessage("You have items waiting to be collected, use ::collect.");
        add.ifPresent(item -> player.sendMessage("Your collection box couldn't hold this item: {}", item.getFormattedString()));
    }

    public void collect(Player player) {
        if (player.isBusy()) {
            player.sendMessage("Finish what you're doing before collection your items.");
            return;
        }

        if (!Boundary.EDGEVILLE_PERIMETER.in(player)) {
            player.sendMessage("You must in Edgeville to collect your items.");
            return;
        }

        List<GameItem> gameItems = inventory.buildList();

        if (gameItems.isEmpty()) {
            player.sendMessage("Your collection box is empty.");
            return;
        }

        for (GameItem gameItem : gameItems) {
            if (player.getItems().addItem(gameItem.id(), gameItem.amount(), false)) {
                player.sendMessage("Collected {}.", gameItem.getFormattedString());
                inventory.remove(gameItem);
            } else {
                player.sendMessage("You don't have enough inventory space.");
                break;
            }
        }
    }

    public static class CollectionBoxSave implements PlayerSaveEntry {

        @Override
        public List<String> getKeys(Player player) {
            return List.of("collection_box");
        }

        @Override
        public boolean decode(Player player, String key, String value) {
            if (value == null || value.length() == 0)
                return true;
            String[] data = value.split(";");
            List<GameItem> items = Arrays.stream(data).map(it -> {
                String[] split = it.split(":");
                return new GameItem(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }).collect(Collectors.toList());

            GameItem[] itemArray = new GameItem[items.size()]; // :)
            items.toArray(itemArray);
            player.getCollectionBox().inventory.set(itemArray);
            return true;
        }

        @Override
        public String encode(Player player, String key) {
            return player.getCollectionBox().inventory.buildList().stream().map(it -> it.id() + ":" + it.amount()).collect(Collectors.joining(";"));
        }

        @Override
        public void login(Player player) { }
    }
}
