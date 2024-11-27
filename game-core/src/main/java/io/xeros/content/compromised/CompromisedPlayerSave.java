package io.xeros.content.compromised;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;

import java.util.List;

public class CompromisedPlayerSave implements PlayerSaveEntry {

    private static final String ATTR = "compromised_account_type";
    private static final String CHANGED_PASSWORD = "changed_pass_compromised";

    public static void setCompromised(Player player, CompromisedAccountType type) {
        if (type == null) {
            player.getAttributes().remove(ATTR);
            player.getAttributes().setBoolean(CHANGED_PASSWORD, true);
            return;
        }

        player.getAttributes().set(ATTR, type);
    }

    public static CompromisedAccountType getCompromisedType(Player player) {
        if (player.getAttributes().get(ATTR) != null) {
            return (CompromisedAccountType) player.getAttributes().get(ATTR);
        }

        return null;
    }

    public static boolean hasChangedPassword(Player player) {
        return player.getAttributes().getBoolean(CHANGED_PASSWORD, false);
    }

    @Override
    public List<String> getKeys(Player player) {
        return List.of("compromised_account_type");
    }

    @Override
    public boolean decode(Player player, String key, String value) {
        if (value == null || value.length() == 0)
            return true;
        String[] data = value.split(";");
        CompromisedAccountType type = data[0].length() == 0 ? null : CompromisedAccountType.valueOf(data[0]);
        boolean changedPassword = Boolean.parseBoolean(data[1]);

        if (type != null)
            player.getAttributes().set(ATTR, type);
        if (changedPassword)
            player.getAttributes().setBoolean(CHANGED_PASSWORD, true);
        return true;
    }

    @Override
    public String encode(Player player, String key) {
        CompromisedAccountType type = getCompromisedType(player);
        boolean changedPassword = player.getAttributes().getBoolean(CHANGED_PASSWORD, false);
        return (type == null ? "" : type.name()) + ";" + changedPassword;
    }

    @Override
    public void login(Player player) {

    }
}
