package io.xeros.model.entity.player.save.impl;

import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.ModeRevertType;
import io.xeros.model.entity.player.save.PlayerSaveEntry;

public class IronmanRevertTypeSaveEntry implements PlayerSaveEntry {

    private static final String KEY = "ironman_revert_type";

    @Override
    public List<String> getKeys(Player player) {
        return Lists.newArrayList(KEY);
    }

    @Override
    public boolean decode(Player player, String key, String value) {
        player.setModeRevertType(ModeRevertType.valueOf(value));
        return true;
    }

    @Override
    public String encode(Player player, String key) {
        return player.getModeRevertType().name();
    }

    @Override
    public void login(Player player) { }
}
