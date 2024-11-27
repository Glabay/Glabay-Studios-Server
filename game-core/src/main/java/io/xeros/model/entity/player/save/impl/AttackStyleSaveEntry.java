package io.xeros.model.entity.player.save.impl;

import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;

public class AttackStyleSaveEntry implements PlayerSaveEntry {
    @Override
    public List<String> getKeys(Player player) {
        return Lists.newArrayList("weapon_mode_3");
    }

    @Override
    public boolean decode(Player player, String key, String value) {
        try {
            player.getAttributes().setInt("weapon_style_load_index", Integer.parseInt(value));
        } catch (Exception e) {
            System.err.println("Error occurred while decoding WeaponStyle: " + value);
            e.printStackTrace(System.err);
        }
        return true;
    }

    @Override
    public String encode(Player player, String key) {
        return "" + player.getCombatConfigs().getAttackStyle();
    }

    @Override
    public void login(Player player) {
        // Determine the attack style
        int attackStyle = player.getAttributes().getInt("weapon_style_load_index", 0);

        // Update the combat configurations
        player.getCombatConfigs().setAttackStyle(attackStyle);
        player.getCombatConfigs().updateWeapon();

        player.getItems().sendWeapon(player.playerEquipment[Player.playerWeapon]);
    }
}
