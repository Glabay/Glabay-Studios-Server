package io.xeros.content.commands.test;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.EquipmentSetup;
import io.xeros.model.entity.player.Player;

public class CreateEquipmentSetup extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        if (input.length() == 0) {
            player.sendMessage("You must enter a name for your preset, please don't replace others unless intended.");
        } else if (input.contains(" ")) {
            player.sendMessage("Your equipment setup contains spaces, use underscores instead!");
        } else {
            try {
                if (!EquipmentSetup.from(player, input).serialize()) {
                    player.sendMessage("You can't overwrite this setup as you didn't create it.");
                    player.sendMessage("It was created by: " + Objects.requireNonNull(EquipmentSetup.get(input)).getCreator());
                } else {
                    player.sendMessage("Serialized '" + input + "' equipment setup.");
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
                player.sendMessage("Error occurred.");
            }
        }
    }

    public Optional<String> getDescription() {
        return Optional.of("Create a new setup, needs a name too!");
    }
}
