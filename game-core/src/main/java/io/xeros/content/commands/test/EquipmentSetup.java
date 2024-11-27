package io.xeros.content.commands.test;

import java.io.IOException;
import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

public class EquipmentSetup extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            io.xeros.model.EquipmentSetup.equip(player, input);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            player.sendMessage("Could not equip equipment setup.");
        }
    }

    public Optional<String> getDescription() {
        return Optional.of("Spawn a specific equipment setup.");
    }
}
