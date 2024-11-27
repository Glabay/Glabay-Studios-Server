package io.xeros.content.commands.punishment.impl;

import io.xeros.content.commands.punishment.OnlinePlayerPCP;
import io.xeros.model.entity.player.Player;
import io.xeros.util.dateandtime.TimeSpan;

public class GambleBan extends OnlinePlayerPCP {

    @Override
    public String name() {
        return "gambleban";
    }

    @Override
    public boolean requiresDuration() {
        return false;
    }

    @Override
    public void add(Player staff, Player player, TimeSpan duration) {
        player.setGambleBanned(true);
        player.sendMessage("@red@You've been banned from gambling.");
        staff.sendMessage(player.getDisplayNameFormatted() + " banned from gambling.");
    }

    @Override
    public void remove(Player staff, Player player) {
        player.setGambleBanned(false);
        player.sendMessage("@red@You've been unbanned from gambling.");
        staff.sendMessage(player.getDisplayNameFormatted() + " unbanned from gambling.");
    }
}
