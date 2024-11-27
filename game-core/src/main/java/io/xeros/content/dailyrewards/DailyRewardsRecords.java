package io.xeros.content.dailyrewards;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.util.JsonUtil;
import org.apache.commons.io.FileUtils;

public class DailyRewardsRecords {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(DailyRewardsRecords.class.getName());
    private static Map<String, Map<String, DailyRewardsRecord>> records;
    private static final ExecutorService SERVICE = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("daily-rewards-%d").build());
    private static final String FILE_NAME = "daily_rewards_record.json";
    private static String file;

    public static void load() throws IOException {
        file = Server.getSaveDirectory() + FILE_NAME;
        if (new File(file).exists()) {
            records = new Gson().fromJson(FileUtils.readFileToString(new File(file)), new TypeToken<Map<String, Map<String, DailyRewardsRecord>>>() {
            }.getType());
            log.info("Loaded data on " + records.values().stream().mapToInt(map -> map.values().size()).count() + " claimed daily rewards.");
        } else {
            records = new HashMap<>();
            log.warning("No daily reward claims found.");
        }
    }

    private static void save() {
        SERVICE.submit(() -> JsonUtil.toJson(records, file));
    }

    static boolean canClaim(Player player) {
        String id = DailyRewardContainer.get().getIdentifier();
        if (records.containsKey(id)) {
            List<DailyRewardsRecord> playerRecords = build(player);
            for (DailyRewardsRecord record : playerRecords) {
                DailyRewardsRecord playerRecord = records.get(id).get(record.getAddress());
                if (playerRecord != null) {
                    if (ChronoUnit.HOURS.between(playerRecord.getDate(), LocalDateTime.now()) < 24) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static List<DailyRewardsRecord> build(Player player) {
        List<DailyRewardsRecord> records = Lists.newArrayList();
        records.add(new DailyRewardsRecord(player.getIpAddress(), LocalDateTime.now()));
        if (player.getMacAddress() != null && player.getMacAddress().length() > 0 && !player.getMacAddress().equals("0")) {
            records.add(new DailyRewardsRecord(player.getMacAddress(), LocalDateTime.now()));
        }
        return records;
    }

    static void add(Player player, int day) {
        String id = DailyRewardContainer.get().getIdentifier();
        List<DailyRewardsRecord> playerRecords = build(player);
        if (!records.containsKey(id)) {
            records.clear(); // Clear everything else because this is a new reward collection
            records.put(id, Maps.newHashMap());
        }
        for (DailyRewardsRecord record : playerRecords) {
            records.get(id).put(record.getAddress(), record);
        }
        save();
    }
}
