package io.xeros.model.entity.player.mode.group;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.annotate.Init;
import io.xeros.content.collection_log.CollectionLog;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.group.contest.GroupIronmanContest;
import io.xeros.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class GroupIronmanRepository {

    private static final Logger logger = LoggerFactory.getLogger(GroupIronmanRepository.class);

    /**
     * Group data for all created ironman groups.
     */
    private static final List<GroupIronmanGroup> groups = new ArrayList<>();
    private static final Map<String, GroupIronmanGroup> groupsByLoginName = new HashMap<>();
    private static final Map<String, GroupIronmanGroup> groupsByGroupName = new HashMap<>();

    public static GroupIronmanGroup addFormingGroup(Player player, String name) {
        Optional<GroupIronmanGroup> current = getGroupForOnline(player);
        if (current.isPresent()) {
            player.sendStatement("You already have a group, you must leave your current group.");
            return null;
        }

        if (player.isJoinedIronmanGroup()) {
            player.sendStatement("You've already joined and left a group, you can't join/create another.");
            return null;
        }

        name = name.trim();
        Preconditions.checkState(!groupExistsWithName(name), "Group with name already exists: " + name);

        GroupIronmanGroup group = new GroupIronmanGroup(name, Lists.newArrayList(player.getLoginNameLower()));
        group.setOnline(player);

        CollectionLog collectionLog = new CollectionLog();
        collectionLog.setGroupIronman(true);
        collectionLog.setSaveName(group.getName().toLowerCase());
        group.setCollectionLog(collectionLog);

        addGroup(group);
        return group;
    }

    private static void addGroup(GroupIronmanGroup group) {
        Preconditions.checkState(!groupExistsWithName(group.getName()), "Group with name already exists: " + group.getName());
        addToGroupLists(group);
        logger.debug("Added a new group {}", group);
    }

    private static void addToGroupLists(GroupIronmanGroup group) {
        groups.add(group);
        groupsByGroupName.put(group.getName().toLowerCase(), group);
        group.getMembers().forEach(it -> groupsByLoginName.put(it.toLowerCase(), group));
    }

    private static void removeGroup(GroupIronmanGroup group) {
        groupsByGroupName.remove(group.getName().toLowerCase());
        groups.remove(group);
        logger.debug("Removed group: {}", group);
    }

    public static void addToGroup(Player player, GroupIronmanGroup group) {
        if (groupsByLoginName.containsKey(player.getLoginNameLower())) {
            player.sendStatement("You are already in a group!");
            return;
        }

        if (group.size() >= 5) {
            player.sendStatement("That group has the maximum amount of players already.");
            return;
        }

        if (group.getJoined() >= 5) {
            player.sendStatement("That group has already had the maximum amount of joins, which is 5.");
            return;
        }

        if (player.isJoinedIronmanGroup()) {
            player.sendStatement("You've already joined and left a group, you can't join/create another.");
            return;
        }

        if (group.isFinalized()) {
            player.setJoinedIronmanGroup(true);
            group.setJoined(group.getJoined() + 1);
        }

        group.sendGroupNotice(player.getDisplayNameFormatted() + " has joined your group.");
        group.addToGroup(player);
        groupsByLoginName.put(player.getLoginNameLower(), group);
        player.getCollectionLog().setLinked(group.getCollectionLog());
        CollectionLog.combineForGroupIronman(player, group);

        logger.debug("Added player to group: {}", group);
    }

    public static void removeFromGroup(Player player, GroupIronmanGroup group) {
        player.getCollectionLog().setLinked(null); // Remove link to group collection log
        groupsByLoginName.remove(player.getLoginNameLower());
        group.removeFromGroup(player);
        group.sendGroupNotice(player.getDisplayNameFormatted() + " has left your group.");
        logger.debug("Removed {} from group.", player);
        if (group.size() == 0) {
            removeGroup(group);
        }
    }

    public static void removeFromGroup(String loginName, GroupIronmanGroup group) {
        groupsByLoginName.remove(loginName);
        group.removeFromGroup(loginName);
        group.sendGroupNotice(loginName + " has left your group.");
        logger.debug("Removed player with login name {} from group.", loginName);
        if (group.size() == 0) {
            removeGroup(group);
        }
    }

    public static void finalize(GroupIronmanGroup group) {
        group.getOnline().forEach(it -> it.setJoinedIronmanGroup(true));
        group.setJoined(group.getMembers().size());
        group.setFinalized(true);
        serializeAll();
    }

    public static void onLogin(Player player) {
        getFromGroupList(player).ifPresent(group -> {
            GroupIronmanContest.insertContestEntry(player, group);
            group.sendGroupNotice(player.getDisplayNameFormatted() + " has logged in.");
            group.setOnline(player);
            groupsByLoginName.put(player.getLoginNameLower(), group);
            logger.debug("Set player online for group: {}", group);
        });
    }

    public static void onLogout(Player player) {
        getGroupForOnline(player).ifPresent(group -> {
            if (!group.isFinalized()) {
                group.removeFromGroup(player);
                logger.debug("Removed player from group because it was still forming on logout player={}, group={}", player, group);
                if (group.getOnline().isEmpty()) {
                    removeGroup(group);
                }
            } else {
                group.setOffline(player);
                group.sendGroupNotice(player.getDisplayNameFormatted() + " has logged out.");
                logger.debug("Removed player from online group on logout player={}, group={}", player, group);
                if (group.getOnline().isEmpty()) {
                    group.setCollectionLog(null);
                    logger.debug("Disposing group collection log because all players are offine in group {}", group);
                }
            }
        });
    }

    public static Optional<GroupIronmanGroup> getGroupForOnline(Player player) {
        return Optional.ofNullable(groupsByLoginName.get(player.getLoginNameLower()));
    }

    public static Optional<GroupIronmanGroup> getGroupForOffline(String loginName) {
        return Optional.ofNullable(groupsByLoginName.get(loginName.toLowerCase()));
    }

    public static Optional<GroupIronmanGroup> getFromGroupList(Player player) {
        return groups.stream().filter(it -> it.getMembers().contains(player.getLoginNameLower())).findFirst();
    }

    public static GroupIronmanGroup get(String groupName) {
        return groupsByGroupName.get(groupName.toLowerCase());
    }

    public static boolean groupExistsWithName(String name) {
        return groupsByGroupName.containsKey(name.trim().toLowerCase());
    }

    private static String getGroupsSaveDirectory() {
        return Server.getSaveDirectory() + "/gim/";
    }

    public static void serializeAll() {
        Server.getIoExecutorService().submit(GroupIronmanRepository::serializeAllInstant);
    }

    public static void serializeAllInstant() {
        try {
            Files.createDirectories(new File(getGroupsSaveDirectory()).toPath());
            for (GroupIronmanGroup group : groups) {
                serializeInstant(group);
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public static void serializeInstant(GroupIronmanGroup group) throws IOException {
        if (group.isFinalized()) {
            GroupIronmanGroupSave save = GroupIronmanGroupSave.toSave(group);
            JsonUtil.toJacksonJson(save, getGroupsSaveDirectory() + save.getName() + ".json");
            logger.debug("Saved group ironman group {}", group);
        }
    }

    @Init
    public static void load() throws IOException {
        if (!new File(getGroupsSaveDirectory()).exists())
            return;
        groups.clear();

        File[] files = new File(getGroupsSaveDirectory()).listFiles();

        for (File file : files) {
            if (file.getName().endsWith(".json")) {
                try {
                    GroupIronmanGroupSave save = JsonUtil.fromJacksonJson(file, new TypeReference<>() {
                    });
                    addToGroupLists(save.toGroup());
                } catch (Exception e) {
                    System.err.println("Error loading: " + file);
                    throw e;
                }
            }
        }

        logger.info("Loaded {} group ironman groups.", GroupIronmanRepository.groups.size());
    }
}
