package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.combat.stats.MonsterKillLog;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

public class KillLog extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        MonsterKillLog.openInterface(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Opens the kill log.");
    }

}
