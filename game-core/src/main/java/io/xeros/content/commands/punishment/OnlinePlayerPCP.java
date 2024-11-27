package io.xeros.content.commands.punishment;

import io.xeros.model.entity.player.Player;
import io.xeros.util.dateandtime.TimeSpan;

public abstract class OnlinePlayerPCP implements PunishmentCommandParser {

    public abstract boolean requiresDuration();

    public abstract void add(Player staff, Player player, TimeSpan duration);

    public abstract void remove(Player staff, Player player);

    @Override
    public void add(Player staff, PunishmentCommandArgs args) {
        TimeSpan duration = null;
        if (requiresDuration())
            duration = args.getDuration();
        add(staff, args.getPlayerForDisplayName(), duration);
    }

    @Override
    public void remove(Player staff, PunishmentCommandArgs args) {
        remove(staff, args.getPlayerForDisplayName());
    }

    @Override
    public String getFormat(String commandName) {
        return PunishmentCommand.getFormat(commandName, requiresDuration());
    }

}
