package io.xeros.content.combat.stats;

import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;

public class BossTimersPlayerSaveEntry implements PlayerSaveEntry {
    private final String KEY = "boss_kill_times";

    @Override
    public List<String> getKeys(Player player) {
        return Lists.newArrayList(KEY);
    }

    @Override
    public boolean decode(Player player, String key, String value) {
        String[] data = value.split(":");
        if (data.length > 1) {
            List<FightDuration> fightDurationList = Lists.newArrayList();
            for (int index = 0; index < data.length; index += 2) {
                fightDurationList.add(new FightDuration(data[index], Long.parseLong(data[index + 1])));
            }
            player.getBossTimers().mapAll(fightDurationList);
        }
        return true;
    }

    @Override
    public String encode(Player player, String key) {
        return player.getBossTimers().encode();
    }

    @Override
    public void login(Player player) {

    }
}
