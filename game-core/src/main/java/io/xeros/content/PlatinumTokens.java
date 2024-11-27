package io.xeros.content;

import com.google.common.base.Preconditions;
import io.xeros.model.definition.Items;
import io.xeros.model.definition.Npcs;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.Optional;

public class PlatinumTokens {

    private static final int TOKEN_VALUE = 1_000;
    private static final int MAX_COINS_FROM_TOKENS = 2_147_483_000;
    private static final int MAX_TOKENS = MAX_COINS_FROM_TOKENS / TOKEN_VALUE;

    public static boolean itemOnNpc(Player player, NPC npc, int itemId, int itemSlot) {
        if (npc.getNpcId() != Npcs.BARMAN)
            return false;
        convert(player, itemId, itemSlot);
        return true;
    }

    public static void convert(Player player, int itemId, int itemSlot) {
        if (!player.getItems().isItemInInventorySlot(itemId, itemSlot))
            return;

        int tokenAmount = player.getItems().getInventoryCount(Items.PLATINUM_TOKEN);
        int coinAmount = player.getItems().getInventoryCount(Items.COINS);

        switch (itemId) {
            case Items.COINS:
                if (coinAmount < TOKEN_VALUE)
                    return;

                int tokensToAdd = coinAmount / TOKEN_VALUE;
                Optional<GameItem> addedTokens = player.getItems().addItemUntilFullReverse(new GameItem(Items.PLATINUM_TOKEN, tokensToAdd));
                if (addedTokens.isEmpty())
                    return;

                player.getItems().deleteItem(Items.COINS, addedTokens.get().amount() * TOKEN_VALUE);
                break;
            case Items.PLATINUM_TOKEN:
                long coinsToAdd = (long) tokenAmount * (long) TOKEN_VALUE;

                if (coinsToAdd > MAX_COINS_FROM_TOKENS)
                    coinsToAdd = MAX_COINS_FROM_TOKENS;

                if (coinsToAdd + (long) coinAmount > MAX_COINS_FROM_TOKENS) {
                    coinsToAdd = MAX_COINS_FROM_TOKENS - coinAmount;
                    if (coinsToAdd == 0) {
                        player.sendMessage("Not enough space in your inventory.");
                        return;
                    }
                    Preconditions.checkState(coinsToAdd > 0 && coinsToAdd < Integer.MAX_VALUE);
                }

                Optional<GameItem> addedCoins = player.getItems().addItemUntilFullReverse(new GameItem(Items.COINS, (int) coinsToAdd));
                if (addedCoins.isEmpty())
                    return;

                player.getItems().deleteItem(Items.PLATINUM_TOKEN, addedCoins.get().amount() / TOKEN_VALUE);
                break;
        }
    }

}
