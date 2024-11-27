package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

public class Collect extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.getCollectionBox().collect(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Collect items in your collection box.");
    }
}
