package io.xeros.content.vote_panel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

/**
 * @author Grant_ | www.rune-server.ee/members/grant_ | 3/24/20
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public class VotePanelManager {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(VotePanelManager.class.getName());
    private static final ExecutorService IO_SERVICE = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("vote-panel-%d").build());
    private static final Object CYCLE_EVENT_OBJECT = new Object();
    public static VotePanelWrapper wrapper = new VotePanelWrapper(0, new HashMap<>(), new ArrayList<>());
    public static final int[] REWARD_IDS = {13346, 6828, 6199};

    /**
     * Initializing data and system on server restart
     */
    public static void init() {
        File file = new File(getSaveFile());
        if (file.exists()) {
            try {
                JsonParser parser = new JsonParser();
                if (!file.exists()) {
                    return;
                }
                Object obj = parser.parse(new FileReader(file));
                JsonObject json = (JsonObject) obj;
                Type listType = new TypeToken<VotePanelWrapper>() {
                }.getType();
                wrapper = new Gson().fromJson(json, listType);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        if (wrapper.getFinishTime() == 0) {
            //Initial start of new system
            reset();
            fireCycleEvent();
        } else if (System.currentTimeMillis() >= wrapper.getFinishTime()) {
            //Server turned on and event week was completed
            reward();
            reset();
            fireCycleEvent();
        } else {
            fireCycleEvent();
        }
    }

    /**
     * Saves to a .json file
     */
    public static void saveToJSON() {
        IO_SERVICE.submit(() -> {
            Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
            String prettyJson = prettyGson.toJson(wrapper);
            BufferedWriter bw;
            try {
                bw = new BufferedWriter(new FileWriter(new File(getSaveFile())));
                bw.write(prettyJson);
                bw.flush();
                bw.close();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }

    private static String getSaveFile() {
        return Server.getSaveDirectory() + "vote_panel.json";
    }

    /**
     * Rewards top 3 voters of the week
     */
    private static void reward() {
        if (wrapper.getLastWeeksTopVoters() == null) {
            return;
        }
        wrapper.getLastWeeksTopVoters().clear();
        List<String> topVoters = generateTopThree();
        int i = 0;
        for (String voter : topVoters) {
            wrapper.getLastWeeksTopVoters().add(voter);
            voter = voter.substring(0, (voter.indexOf("[") - 1));
            VoteUser user = wrapper.getVotes().get(voter);
            if (user != null) {
                user.setPrizeSlot(i);
            }
            i++;
        }
    }

    /**
     * Resets the entire system
     */
    private static void reset() {
        wrapper.setStartTime(Instant.now());
        wrapper.setFinishTime(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
        for (Map.Entry<String, VoteUser> users : wrapper.getVotes().entrySet()) {
            users.getValue().resetVoteCount();
            users.getValue().resetFirstVoteTimestamp();
        }
        saveToJSON();
    }

    private static void fireCycleEvent() {
        long remainderInMilliseconds = wrapper.getFinishTime() - System.currentTimeMillis();
        System.out.println("Vote Panel Manager has " + Misc.cyclesToTime(remainderInMilliseconds / 600) + " remaining.");
        CycleEventHandler.getSingleton().stopEvents(CYCLE_EVENT_OBJECT);
        CycleEventHandler.getSingleton().addEvent(CYCLE_EVENT_OBJECT, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                reward();
                reset();
                fireCycleEvent();
                container.stop();
            }
        }, Misc.toCycles(remainderInMilliseconds, TimeUnit.MILLISECONDS));
    }

    /**
     * Adds a vote to a player
     * @param playerName
     */
    public static void addVote(String playerName) {
        if (wrapper.getVotes().containsKey(playerName.toLowerCase())) {
            VoteUser user = wrapper.getVotes().get(playerName.toLowerCase());
            if (user.getFirstVoteTimestamp() == 0) {
                user.setFirstVoteTimestamp(System.currentTimeMillis());
            }
            wrapper.getVotes().get(playerName.toLowerCase()).incrementVoteCount();
        } else {
            wrapper.getVotes().put(playerName.toLowerCase(), new VoteUser(1, System.currentTimeMillis()));
        }
    }

    /**
     * Sorting algorithm to determine top 3 voters of the week
     * @return
     */
    protected static List<String> generateTopThree() {
        List<Map.Entry<String, VoteUser>> entries = new ArrayList<>(wrapper.getVotes().entrySet());
        Collections.sort(entries, Comparator.comparingLong(a -> a.getValue().getFirstVoteTimestamp()));
        Collections.sort(entries, (a, b) -> Integer.compare(b.getValue().getVoteCount(), a.getValue().getVoteCount()));
        List<String> topVoters = new ArrayList<>();
        for (Map.Entry<String, VoteUser> e : entries.subList(0, entries.size() >= 3 ? 3 : entries.size())) {
            topVoters.add(e.getKey() + " [" + e.getValue().getVoteCount() + "]");
        }
        return topVoters;
    }

    /**
     * Gets time remaining in the week
     * @return
     */
    protected static String getTimeRemaining() {
        long seconds = (wrapper.getFinishTime() - System.currentTimeMillis()) / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return days + " Days, " + hours % 24 + " Hours, " + minutes % 60 + " Minutes";// + seconds % 60;
    }

    /**
     * Gets voting user from the map of all users that have voted
     * @param player
     * @return
     */
    public static VoteUser getUser(Player player) {
        return wrapper.getVotes().get(player.getLoginName().toLowerCase());
    }

    /**
     * Checks if a player has an active drop boost
     * @param player
     * @return
     */
    public static boolean hasDropBoost(Player player) {
        if (player.dropBoostStart <= 0) {
            return false;
        }
        if (player.dropBoostStart + TimeUnit.MINUTES.toMillis(60) >= System.currentTimeMillis()) {
            return true;
        } else {
            player.dropBoostStart = -1;
            return false;
        }
    }

    /**
     * Gets the time left on the players xp bonus
     * @param player
     * @return
     */
    public static int getBonusXPTimeInMinutes(Player player) {
        return (int) ((player.bonusXpTime * 600) / 1000) / 60;
    }
}
