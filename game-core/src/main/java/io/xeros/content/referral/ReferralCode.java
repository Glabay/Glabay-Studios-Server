package io.xeros.content.referral;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.content.dailyrewards.DailyRewardContainer;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.NamedItem;
import io.xeros.util.ItemConstants;
import lombok.Data;
import org.apache.commons.io.FileUtils;

public class ReferralCode {

    private static final List<ReferralCode> REFERRAL_CODES = Lists.newArrayList();

    public static void load() throws IOException {
        ItemConstants itemConstants = new ItemConstants().load();
        List<ReferralCode> list = new ObjectMapper(new YAMLFactory()).readValue(new File(Server.getDataDirectory() + "/cfg/referral_codes.yaml"), new TypeReference<List<ReferralCode>>() {});
        list.forEach(it -> {
            it.rewardsList = it.rewards.stream().map(item -> item.toGameItem(itemConstants)).collect(Collectors.toList());
        });
        REFERRAL_CODES.clear();
        REFERRAL_CODES.addAll(list);
    }

    public static List<ReferralCode> getReferralCodes() {
        return Collections.unmodifiableList(REFERRAL_CODES);
    }

    private String code;
    private List<NamedItem> rewards;
    private List<GameItem> rewardsList;

    public ReferralCode() {
        code = "";
        rewards = Collections.emptyList();
    }

    public String getCode() {
        return code;
    }

    public List<GameItem> getRewards() {
        return rewardsList;
    }
}
