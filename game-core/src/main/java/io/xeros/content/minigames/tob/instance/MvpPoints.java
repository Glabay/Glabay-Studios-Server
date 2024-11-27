package io.xeros.content.minigames.tob.instance;

import com.google.common.collect.Lists;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class MvpPoints {

    private static final Logger logger = LoggerFactory.getLogger(MvpPoints.class);

    private final Map<String, Integer> map = new HashMap<>();
    private List<Map.Entry<String, Integer>> sorted;

    public void award(Player player, int points) {
        String username = player.getLoginName().toLowerCase();
        int total = map.getOrDefault(username, 0);
        map.put(username, total + points);
    }

    /**
     * @param player the Player.
     * @return a Pair with the left being the rank and the right being the total points earned.
     */
    public Pair<Integer, Integer> getRank(Player player) {
        if (sorted == null) {
            sorted = map.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());
            sorted = Lists.reverse(sorted);
        }

        for (int index = 0; index < sorted.size(); index++) {
            Map.Entry<String, Integer> entry = sorted.get(index);
            if (entry.getKey().equals(player.getLoginName().toLowerCase())) {
                return Pair.of(index + 1, entry.getValue());
            }
        }

        logger.error("Could not get rank for player {}, sorted={}", player, sorted);
        return Pair.of(-1, -1);
    }

    public Player chooseRareWinner(List<Player> playerList) {
        return chooseRareWinner(map, playerList);
    }

    public Player chooseRareWinner(Map<String, Integer> map, List<Player> playerList) {
        List<Map.Entry<String, Integer>> players = map.entrySet().stream().filter(it -> playerList.stream()
                .noneMatch(plr -> plr.getLoginName().equals(it.getKey()))).collect(Collectors.toList());

        int total = players.stream().mapToInt(Map.Entry::getValue).sum();
        int roll = Misc.trueRand(total);
        int current = 0;
        for (Map.Entry<String, Integer> entry : players) {
            if (roll >= current && roll <= entry.getValue()) {
                Optional<Player> player = playerList.stream().filter(it -> it.getLoginName().toLowerCase().equals(entry.getKey())).findFirst();
                if (player.isPresent()) {
                    return player.get();
                }

                break;
            }

            current += entry.getValue();
        }

        logger.error("No winner could be selected, total={}, roll={}, current={}", total, roll, current);
        return playerList.get(0);
    }

}
