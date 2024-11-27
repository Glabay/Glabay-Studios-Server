package io.xeros.model.items.bank;

import io.xeros.content.skills.DoubleExpScroll;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;

import java.util.List;

public class SetFirstBankPinReward implements PlayerSaveEntry {

    private static final String ATTR = "set_first_bank_pin";

    public static boolean hasSetFirstBankPin(Player player) {
        return player.getAttributes().getBoolean(ATTR, false);
    }

    public static void giveRewardOnFirstBankPin(Player player) {
        if (!hasSetFirstBankPin(player)) {
            player.getAttributes().setBoolean(ATTR, true);
            DoubleExpScroll.giveBonusScrollXP(player);
            player.sendMessage("@dre@You have received one hour of bonus xp for setting your first account pin!");
        }
    }

    @Override
    public List<String> getKeys(Player player) {
        return List.of("set_first_bank_pin");
    }

    @Override
    public boolean decode(Player player, String key, String value) {
        player.getAttributes().setBoolean(ATTR, Boolean.parseBoolean(value));
        return true;
    }

    @Override
    public String encode(Player player, String key) {
        return player.getAttributes().getBoolean(ATTR) + "";
    }

    @Override
    public void login(Player player) {

    }
}
