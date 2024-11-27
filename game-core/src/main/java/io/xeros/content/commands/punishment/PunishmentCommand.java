package io.xeros.content.commands.punishment;

import io.xeros.model.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringJoiner;

public class PunishmentCommand {

    public static String getFormat(String commandName) {
        PunishmentCommandParser parser = PunishmentCommandLoader.getByName(commandName, isRemoveCommand(commandName));
        return parser != null ? parser.getFormat(commandName) : null;
    }

    public static String getFormat(String commandName, boolean duration) {
        StringJoiner args = new StringJoiner(SEPARATOR);
        args.add("::" + commandName);
        args.add("display name");
        if (duration && !isRemoveCommand(commandName))
            args.add("5 (minutes) or '5 minutes/hours/days'");
        return args.toString();
    }

    private static boolean isRemoveCommand(String commandName) {
        return commandName.toLowerCase().startsWith("un");
    }

    private static final Logger logger = LoggerFactory.getLogger(PunishmentCommand.class);
    public static final String SEPARATOR = "-";

    private final boolean remove;
    private final String commandName;
    private final String input;
    private final PunishmentCommandParser parser;
    private PunishmentCommandArgs args;

    public PunishmentCommand(String commandName, String input) {
        this.commandName = commandName.toLowerCase();
        this.input = input;
        remove = isRemoveCommand(commandName);
        parser = PunishmentCommandLoader.getByName(commandName, remove);
    }

    public void parse(Player player) {
        try {
            args = new PunishmentCommandArgs(input);
        } catch (Exception e) {
            logger.debug("Error while parsing punishment command arguments", e);
            player.sendMessage("Invalid {} format: {}", commandName, getFormat());
            return;
        }

        PunishmentCommandParser parser = PunishmentCommandLoader.getByName(commandName, remove);
        if (parser == null) {
            player.sendMessage("No punishment command with name '{}'.", commandName);
            return;
        }

        try {
            if (remove) {
                parser.remove(player, args);
            } else {
                parser.add(player, args);
            }
        } catch (Exception e) {
            logger.debug("Error while handling punishment command.", e);
            player.sendMessage("@dre@Error with {}, message: {}", commandName, e.getMessage());
            player.sendMessage(getFormat());
        }
    }

    private String getFormat() {
        return parser.getFormat(commandName);
    }
}
