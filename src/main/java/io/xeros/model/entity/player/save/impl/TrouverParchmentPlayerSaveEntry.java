package io.xeros.model.entity.player.save.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;
import io.xeros.util.Misc;

public class TrouverParchmentPlayerSaveEntry implements PlayerSaveEntry {

    private static final String KEY = "trouver_items";

    @Override
    public List<String> getKeys(Player player) {
        return Lists.newArrayList(KEY);
    }

    @Override
    public boolean decode(Player player, String key, String value) {
        if (!value.isEmpty()) {
            List<Integer> items = new ArrayList<>();
            String[] split = value.split("-");
            for (String s : split) {
                items.add(Integer.parseInt(s));
            }

            player.getAttributes().set(KEY, items);
        }

        return true;
    }

    @Override
    public String encode(Player player, String key) {
        return "";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void login(Player player) {
        Object items = player.getAttributes().get(KEY);
        if (items != null) {
            int price = FireOfExchangeBurnPrice.getBurnPrice(player, Items.TROUVER_PARCHMENT, false);
            int sum = ((List<Integer>) items).stream().mapToInt(it -> price).sum();
            player.getAttributes().remove(KEY);
            player.exchangePoints += sum;
            player.sendMessage("<img=1> @dre@Trouver parchments have been removed, you've received {} FoE Points.", Misc.insertCommas(sum));
            player.sendMessage("<img=1> @dre@If you have more, offer them to the FoE. Check the update log for more info.");
        }
    }
}
