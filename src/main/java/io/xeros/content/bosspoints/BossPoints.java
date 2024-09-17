package io.xeros.content.bosspoints;

import com.fasterxml.jackson.core.type.TypeReference;
import io.xeros.Server;
import io.xeros.annotate.Init;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.leaderboards.LeaderboardType;
import io.xeros.content.leaderboards.LeaderboardUtils;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.util.JsonUtil;
import lombok.Data;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Log
public class BossPoints {

    private static final Logger logger = LoggerFactory.getLogger(BossPoints.class);

    @Data
    private static final class BossPointEntry {
        private final String name;
        private final int points;

        /**
         * Are the points awarded manually (not on npc death)
         */
        private final boolean manual;

        private BossPointEntry() {
            name = "";
            points = 0;
            manual = false;
        }
    }

    private static final List<BossPointEntry> ENTRIES = new ArrayList<>();

    @Init
    public static void init() throws IOException {
        ENTRIES.clear();
        List<BossPointEntry> list = JsonUtil.fromYaml(Server.getDataDirectory() + "/cfg/npc/boss_points.yaml", new TypeReference<List<BossPointEntry>>() {});
        ENTRIES.addAll(list);
    }

    public static int getPointsOnDeath(NPC npc) {
        return getPoints((entry) -> !entry.isManual() && entry.getName().equalsIgnoreCase(npc.getDefinition().getName()));
    }

    public static int getManualPoints(String name) {
        int points = getPoints((entry) -> entry.isManual() && entry.getName().equalsIgnoreCase(name));
        if (points == 0) {
            logger.warn("No manual points for name: " + name);
        }
        return points;
    }

    public static int getPoints(Predicate<BossPointEntry> predicate) {
        return ENTRIES.stream().filter(predicate).mapToInt(it -> it.points).sum();
    }

    public static void addManualPoints(Player player, String name) {
        addPoints(player, getManualPoints(name), true);
    }

    public static void addPoints(Player player, int points, boolean message) {
        if (points > 0) {
            if (Hespori.activeBuchuSeed) {
                points *= 2;
            }
            player.bossPoints += points;
            player.getQuestTab().updateInformationTab();
            player.getEventCalendar().progress(EventChallenge.GAIN_X_BOSS_POINTS, points);
            LeaderboardUtils.addCount(LeaderboardType.BOSS_POINTS, player, points);
            if (message) {
                player.sendMessage("Gained <col=FF0000>" + points + "</col> boss points.");
            }
        }
    }

    /**
     * Had an issue through june 1-2 2021 where boss points were wiped on logout.
     */
    public static void doRefund(Player player) {
        if (!player.bossPointsRefund) {
            int refund = ENTRIES.stream().mapToInt(it -> player.getNpcDeathTracker().getKc(it.getName().toLowerCase()) * it.getPoints()).sum();
            player.bossPointsRefund = true;
            if (refund > 0) {
                player.bossPoints += refund;
                player.sendMessage("Refunded " + refund + " boss points, sorry for the inconvenience!");
            }
        }
    }
}
