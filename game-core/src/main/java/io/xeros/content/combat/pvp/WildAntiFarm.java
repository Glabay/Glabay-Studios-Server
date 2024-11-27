package io.xeros.content.combat.pvp;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WildAntiFarm {

    private static Logger logger = LoggerFactory.getLogger(WildAntiFarm.class);

    private static final String RECENT_KILLED_LIST_ATTRIBUTE = "recent_killed_players";
    private static final long MINUTES = 30;

    @SuppressWarnings("unchecked")
    private static List<RecentKilled> getRecentKilled(Player player) {
        List<RecentKilled> list = (List<RecentKilled>) player.getAttributes().getList(RECENT_KILLED_LIST_ATTRIBUTE, Lists.newArrayList());
        list.sort(Comparator.comparingLong(RecentKilled::getMinutesElapsed));
        list.removeIf(it -> it.getMinutesElapsed() >= MINUTES);
        updateList(player, list);
        return list;
    }

    private static void updateList(Player player, List<RecentKilled> list) {
        player.getAttributes().setList(RECENT_KILLED_LIST_ATTRIBUTE, list);
    }

    /**
     * Checks if the killer has killed the victim with the last {@link WildAntiFarm#MINUTES} minutes
     * (or anyone with the same mac address or ip address as the victim). Also verifies they don't
     * share the same ip address or mac address.
     */
    public static boolean canReceiveRewards(Player killer, Player victim) {
        if (killer.isSameComputer(victim)) {
            if (Server.isDebug()) {
                logger.info("Skipping same computer check because server is in debug mode.");
            } else {
                return false;
            }
        }

        return getRecentKilled(killer).stream().filter(it -> victim.isSameComputer(it.username, it.ipAddress, it.macAddress))
                .noneMatch(it -> it.getMinutesElapsed() < MINUTES);
    }

    public static void addReceivedRewards(Player killer, Player victim) {
        List<RecentKilled> recentKilled = getRecentKilled(killer);
        recentKilled.add(new RecentKilled(victim.getNameAsLong(), victim.getMacAddress(), victim.getIpAddress()));
        updateList(killer, recentKilled);
    }

    public static class RecentKilled {
        private final long username;
        private final long time;
        private final String macAddress;
        private final String ipAddress;

        public RecentKilled(long username, String macAddress, String ipAddress) {
            this(username, System.currentTimeMillis(), macAddress, ipAddress);
        }

        public RecentKilled(long username, long time, String macAddress, String ipAddress) {
            this.username = username;
            this.time = time;
            this.macAddress = macAddress;
            this.ipAddress = ipAddress;
        }

        private RecentKilled(String[] data) {
            this(Long.parseLong(data[0]), Long.parseLong(data[1]), data[2], data[3]);
        }

        public long getMinutesElapsed() {
            return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - time);
        }
    }

    public static class WildAntiFarmPlayerSaveEntry implements PlayerSaveEntry {

        @Override
        public List<String> getKeys(Player player) {
            return List.of("recent_killed");
        }

        @Override
        public boolean decode(Player player, String key, String value) {
            String[] indexesSplit = value.split(":");
            if (indexesSplit.length == 1 && indexesSplit[0].length() == 0)
                return true;
            List<String[]> valuesSplit = Arrays.stream(indexesSplit).map(it -> it.split(",")).collect(Collectors.toList());
            List<RecentKilled> recentKilledList = valuesSplit.stream().map(RecentKilled::new).collect(Collectors.toList());
            player.getAttributes().setList(RECENT_KILLED_LIST_ATTRIBUTE, recentKilledList);
            return true;
        }

        @Override
        public String encode(Player player, String key) {
            return getRecentKilled(player).stream().map(it -> it.username
                    + "," + it.time + ","
                    + it.macAddress + ","
                    + it.ipAddress)
                    .collect(Collectors.joining(":"));
        }

        @Override
        public void login(Player player) {

        }
    }
}
