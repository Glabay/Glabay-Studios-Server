package io.xeros.content.referral;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.util.JsonUtil;
import io.xeros.util.discord.Discord;

public class ReferralRegister {

    private static final ExecutorService SERVICE = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("referral-worker-%d").build());
    private static final String REFERRAL_ATTRIBUTE_KEY = "referalls";
    private static final String REFERRAL_MAP_ATTRIBUTE_KEY = "referalls_map";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd HH_mm_ss");
    public static final String COLOR = "<col=05695d>";

    static void register(Player player, ReferralSource source, String qualifier) {
        setUsedReferral(player);
        player.referallFlag += 5;
        player.gfx100(199);
        Discord.writeServerSyncMessage("[Referral]: " + player.getDisplayName()
                + " came from " + (qualifier == null ? source.toString() : qualifier) + ".");
        Discord.writeReferralMessage("[Referral]: " + player.getDisplayName()
                + " came from " + (qualifier == null ? source.toString() : qualifier) + ".");

        SERVICE.submit(() -> {
            LocalDateTime time = LocalDateTime.now();
            JsonUtil.toJson(new Referral(player.getLoginName(), time), getReferallFolder(source, qualifier) + "/" + TIME_FORMATTER.format(time) + ".json");
        });
    }

    static boolean canGetReward(Player player) {
        if (getUsedReferrals().stream().anyMatch(data -> data.equals(player.getMacAddress()))) {
            return false;
        }

        HashSet<String> used = getUsedMapReferrals();
        return !used.contains(player.getIpAddress()) && !used.contains(player.getMacAddress());
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    private static List<String> getUsedReferrals() {
        if (Server.getServerAttributes().getList(REFERRAL_ATTRIBUTE_KEY) == null) {
            Server.getServerAttributes().setList(REFERRAL_ATTRIBUTE_KEY, new ArrayList());
        }

        return (List<String>) Server.getServerAttributes().getList(REFERRAL_ATTRIBUTE_KEY);
    }

    @SuppressWarnings("unchecked")
    private static HashSet<String> getUsedMapReferrals() {
        if (Server.getServerAttributes().getHashSet(REFERRAL_MAP_ATTRIBUTE_KEY) == null) {
            Server.getServerAttributes().setHashSet(REFERRAL_MAP_ATTRIBUTE_KEY, new HashSet<String>());
        }

        return (HashSet<String>) Server.getServerAttributes().getHashSet(REFERRAL_MAP_ATTRIBUTE_KEY);
    }


    private static String getReferallFolder(ReferralSource source, String qualifier) {
        String directory = Server.getSaveDirectory() + "/refers/" + source.name().toLowerCase()
                + "/" + (qualifier == null ? "" : qualifier.toLowerCase() + "/");
        if (!new File(directory).exists()) {
            Preconditions.checkState(new File(directory).mkdirs());
        }
        return directory;
    }

    private static void setUsedReferral(Player c) {
        getUsedMapReferrals().add(c.getIpAddress());
        getUsedMapReferrals().add(c.getMacAddress());
        Server.getServerAttributes().write();
    }
}
