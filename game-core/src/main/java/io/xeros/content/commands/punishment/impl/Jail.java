package io.xeros.content.commands.punishment.impl;

import io.xeros.Server;
import io.xeros.content.commands.punishment.OnlinePlayerPCP;
import io.xeros.model.entity.player.Player;
import io.xeros.util.dateandtime.TimeSpan;

public class Jail extends OnlinePlayerPCP {

    @Override
    public String name() {
        return "jail";
    }

    @Override
    public boolean requiresDuration() {
        return true;
    }

    @Override
    public void add(Player staff, Player player, TimeSpan duration) {
        if (Server.getMultiplayerSessionListener().inAnySession(player)) {
            staff.sendMessage("The player is in a trade, or duel. You cannot do this at this time.");
            return;
        }
        if(staff.getLoginName().equals(player.getLoginName())){
            staff.sendMessage("You can not jail yourself.");
            return;
        }
        player.setTeleportToX(2086);
        player.setTeleportToY(4466);
        player.heightLevel = 0;
        player.jailEnd = duration.offsetCurrentTimeMillis();

        player.sendMessage("@red@You have been jailed by {} for {}.", staff.getDisplayNameFormatted(), duration.toString());
        player.sendMessage("@red@Type ::unjail after having served your time to be unjailed.");
        staff.sendMessage("Successfully jailed {} for {}.", player.getDisplayNameFormatted(), duration.toString());
    }

    @Override
    public void remove(Player staff, Player player) {
        player.getPA().movePlayer(3093, 3493, 0);
        player.jailEnd = 0;
        player.isStuck = false;
        player.sendMessage("You have been unjailed by " + staff.getDisplayName() + ". Don't get jailed again!");
        staff.sendMessage("Successfully unjailed " + player.getDisplayName() + ".");
    }
}
