package io.xeros.content.commands.punishment;

import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.punishments.PunishmentType;
import io.xeros.util.dateandtime.TimeSpan;

public abstract class OnlinePlayerPunishmentPCP implements PunishmentCommandParser {

    public abstract PunishmentType getPunishmentType();

    public abstract void onPunishment(Player staff, Player player, TimeSpan duration);

    public abstract void onRemovePunishment(Player staff, Player player);

    public abstract String extract(Player player);

    @Override
    public void add(Player staff, PunishmentCommandArgs args) {
        Player player = args.getPlayerForDisplayName();
        TimeSpan duration = args.getDuration();
        Server.getPunishments().add(getPunishmentType(), duration, extract(player));
        onPunishment(staff, player, duration);
        staff.sendMessage("{} '{}' for {}.", getPunishmentType().getFormattedName(), player.getDisplayName(), duration);
    }

    @Override
    public void remove(Player staff, PunishmentCommandArgs args) {
        String value = args.index(0);
        if (!Server.getPunishments().remove(getPunishmentType(), value)) {
            staff.sendMessage("No {} for '{}'.", getPunishmentType(), value);
            return;
        }

        staff.sendMessage("{} {}.", "Un" + getPunishmentType().getFormattedName().toLowerCase(), value);

        // If they're online tell them about it
        Player player = PlayerHandler.getPlayerByDisplayName(value);
        if (player != null) {
            onRemovePunishment(staff, player);
            player.sendMessage("@red@You've been {}.", "Un" + getPunishmentType().getFormattedName().toLowerCase());
        }
    }

    @Override
    public String getFormat(String commandName) {
        return PunishmentCommand.getFormat(commandName, true);
    }
}
