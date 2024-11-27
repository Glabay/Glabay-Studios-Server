package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

public class Pin extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.getBankPin().requiresUnlock()) {
            player.getBankPin().open(2);
            return;
        }
        player.getPA().closeAllWindows();
        player.getBankPin().openInterface();
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Create or manage your account pin.");
    }
}
