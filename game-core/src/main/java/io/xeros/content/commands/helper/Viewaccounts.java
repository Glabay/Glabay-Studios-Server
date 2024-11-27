package io.xeros.content.commands.helper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;

public class Viewaccounts extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        boolean hideAddresses = !player.getRights().contains(Right.OWNER) && !player.getRights().contains(Right.ADMINISTRATOR);
        Map<String, List<Player>> playersForAddress = Maps.newHashMap();
        PlayerHandler.getPlayers().stream().filter(Objects::nonNull).forEach(it -> {
            add(playersForAddress, it, "IP: " + it.getIpAddress() + ", Mac: " + it.getMacAddress());
        });

        List<String> lines = Lists.newArrayList();
        playersForAddress.forEach((key, value) -> {
            lines.add(hideAddresses ? "Redacted address" : key);
            lines.add(value.stream().map(p -> p.getDisplayName() + ", ").collect(Collectors.joining()));
        });

        player.getPA().openQuestInterface("Accounts by address", lines.stream().limit(149).collect(Collectors.toList()));
    }

    private static void add(Map<String, List<Player>> stringListMap, Player player, String string) {
        stringListMap.putIfAbsent(string, Lists.newArrayList());
        stringListMap.get(string).add(player);
    }

    public Optional<String> getDescription() {
        return Optional.of("Shows accounts and their ips");
    }
}
