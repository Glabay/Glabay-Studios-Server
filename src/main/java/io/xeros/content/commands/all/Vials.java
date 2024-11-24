package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

/**
 * Show the current position.
 *
 * @author Noah
 */
public class Vials extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.barbarian) {
            if (player.breakVials) {
                player.breakVials = false;
                player.sendMessage("You will no longer break your viles after the last sip.");
            }
            else {
                player.breakVials = true;
                player.sendMessage("You will now break your vials after the last sip.");
            }
            return;
        }
        player.sendMessage("You have not learned how to break vials yet.");

    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Turns on and off break vials.");
    }
}
