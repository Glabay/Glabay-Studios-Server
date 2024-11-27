package io.xeros.model.entity.player.save.impl;

import io.xeros.content.skills.Skill;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Skill200mPlayerSaveEntry implements PlayerSaveEntry {
    @Override
    public List<String> getKeys(Player player) {
        return List.of("200mtime");
    }

    @Override
    public boolean decode(Player player, String key, String value) {
        Long[] array = Arrays.stream(value.split(",")).map(Long::parseLong).toList().toArray(new Long[25]);
        player.gained200mTime = ArrayUtils.toPrimitive(array);
        return true;
    }

    @Override
    public String encode(Player player, String key) {
        return Arrays.stream(player.gained200mTime).mapToObj(String::valueOf).collect(Collectors.joining(","));
    }

    @Override
    public void login(Player player) {
        for (int i = 0; i < player.playerXP.length; i++) {
            if (player.playerXP[i] >= Skill.MAX_EXP && player.gained200mTime[i] == 0) {
                player.gained200mTime[i] = System.currentTimeMillis();
            }
        }
    }
}
