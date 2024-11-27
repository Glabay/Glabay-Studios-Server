package io.xeros.content.dailyrewards;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.model.definitions.ShopDef;
import io.xeros.model.items.ImmutableItem;
import io.xeros.model.items.NamedItem;
import io.xeros.util.ItemConstants;
import io.xeros.util.JsonUtil;
import lombok.Data;
import org.apache.commons.io.FileUtils;

public class DailyRewardContainer {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(DailyRewardContainer.class.getName());
    private static final String DAILY_REWARDS_DIRECTORY = Server.getDataDirectory() + "/cfg/daily_rewards/";
    private static DailyRewardContainer dailyRewardContainer = null;

    public static void load() throws IOException {
        File[] files = Objects.requireNonNull(new File(DAILY_REWARDS_DIRECTORY).listFiles());
        DailyRewardContainer latest = null;
        ItemConstants itemConstants = new ItemConstants().load();
        for (File file : files) {
            DailyRewardContainer container = new ObjectMapper(new YAMLFactory()).readValue(file, DailyRewardContainer.class);
            container.rewardsList = container.rewards.stream().map(item -> item.toImmutableItem(itemConstants)).collect(Collectors.toList());
            if (container.getStartDate().isBefore(LocalDate.now()) || container.getStartDate().equals(LocalDate.now())) {
                if (latest == null || latest.getStartDate().isBefore(container.getStartDate())) {
                    latest = container;
                }
            }
        }
        log.info("Selected daily rewards path with start date: " + Objects.requireNonNull(latest).getStartDate());
        dailyRewardContainer = latest;
    }

    public static DailyRewardContainer get() {
        return dailyRewardContainer;
    }

    private final String identifier;
    private final int[] date;
    private final List<NamedItem> rewards;
    private List<ImmutableItem> rewardsList;

    public DailyRewardContainer() {
        identifier = "";
        date = new int[3];
        rewards = Collections.emptyList();
    }

    public int[] getDate() {
        return date;
    }

    public String getIdentifier() {
        return identifier;
    }

    public LocalDate getStartDate() {
        return LocalDate.of(date[0], date[1], date[2]);
    }

    public List<ImmutableItem> getRewards() {
        return rewardsList;
    }
}
