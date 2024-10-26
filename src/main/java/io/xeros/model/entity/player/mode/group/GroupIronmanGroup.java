package io.xeros.model.entity.player.mode.group;

import com.google.common.collect.Lists;
import io.xeros.content.collection_log.CollectionLog;
import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.mode.group.log.GimDropItemLog;
import io.xeros.model.entity.player.mode.group.log.GimWithdrawItemLog;
import io.xeros.model.items.GameItem;
import org.apache.commons.collections4.ListUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GroupIronmanGroup {

    private static final int LOG_MAX_SIZE = 200;

    private final String name;
    private final List<String> members;
    private boolean finalized;
    private final List<Player> online = Lists.newArrayList();
    private final GroupIronmanGroupStats stats = new GroupIronmanGroupStats(this);
    private final GroupIronmanBank bank = new GroupIronmanBank(this);
    private final List<String> mergedCollectionLogs = Lists.newArrayList();
    private CollectionLog collectionLog;
    private final Deque<GimWithdrawItemLog> withdrawItemLog = new ArrayDeque<>();
    private final Deque<GimDropItemLog> dropItemLog = new ArrayDeque<>();

    /**
     * Total amount of joined members after you leave tutorial islands,
     * includes all people present at finalization and all subsequent invites.
     */
    private int joined = 0;

    public GroupIronmanGroup(String name, List<String> members) {
        this.name = name;
        this.members = members;
    }

    public boolean canJoin(Player checking, Player joining) {
        if (joined >= 5) {
            checking.sendMessage("That group has already had the maximum number of joins, which is 5.");
            return false;
        }

        if (members.size() == 0) {
            checking.sendStatement("Group is no longer forming.");
            return false;
        }

        if (!joining.getRights().contains(Right.GROUP_IRONMAN)) {
            checking.sendStatement("Not a Group Ironman player.");
            return false;
        }

        if (GroupIronmanRepository.getGroupForOnline(joining).isPresent()) {
            checking.sendStatement("Already in a group.");
            return false;
        }

        if (members.size() >= 5) {
            checking.sendStatement("The group is full.");
            return false;
        }

        return true;
    }

    public void addToGroup(Player player) {
        members.add(player.getLoginNameLower());
        online.add(player);
        player.sendMessage("Joined Ironman Group '" + name + "'.");
    }

    /**
     * Remove player from group list and from online players.
     */
    public void removeFromGroup(Player player) {
        members.remove(player.getLoginNameLower());
        online.remove(player);
    }

    public void removeFromGroup(String loginName) {
        members.remove(loginName);
    }

    public void setOnline(Player player) {
        if (!online.contains(player)) {
            online.add(player);
        }
    }

    public void setOffline(Player player) {
        online.remove(player);
    }

    public List<Player> getOnline() {
        return Lists.newArrayList(online);
    }

    public List<String> getOfflineMembers() {
        var onlineUsername = online.stream().map(Player::getLoginNameLower).collect(Collectors.toList());
        var offlineUsernames = ListUtils.subtract(onlineUsername, members);
        return Collections.unmodifiableList(offlineUsernames);
    }
    public boolean isLeader(Player player) {
        return !members.isEmpty() && members.get(0).equals(player.getLoginNameLower());
    }

    public void sendGroupChat(Player player, String message) {
        getOnline().forEach(s -> s.sendMessage("[<col=ff0000>" + name + "</col>] " + player.getDisplayNameFormatted() + ": @dre@" + message));
    }

    public void sendGroupNotice(String msg) {
        getOnline().forEach(s -> s.sendMessage("[<col=ff0000>" + name + "</col>] @dre@" + msg));
    }

    public boolean isGroupMember(Player player) {
        return members.stream().anyMatch(member -> player.getLoginNameLower().equalsIgnoreCase(member));
    }

    public boolean isGroupMember(String name) {
        return members.stream().anyMatch(member -> name.toLowerCase().equalsIgnoreCase(member));
    }

    private boolean log(GameItem gameItem) {
        return FireOfExchangeBurnPrice.hasValue(gameItem.id()) || gameItem.getDef().getShopValue() > 50_000;
    }

    public void addWithdrawItemLog(Player player, GameItem gameItem) {
        if (!log(gameItem))
            return;
        withdrawItemLog.offerFirst(new GimWithdrawItemLog(player.getDisplayName(), gameItem));
        while (withdrawItemLog.size() > LOG_MAX_SIZE)
            withdrawItemLog.removeLast();
    }

    public void addDropItemLog(Player player, GameItem gameItem) {
        if (!log(gameItem))
            return;
        dropItemLog.offerFirst(new GimDropItemLog(player.getDisplayName(), gameItem, player.getPosition()));
        while (dropItemLog.size() > LOG_MAX_SIZE)
            dropItemLog.removeLast();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, members, finalized);
    }

    public int size() {
        return members.size();
    }

    public String getName() {
        return name;
    }

    public List<String> getMembers() {
        return members;
    }

    public boolean isFinalized() {
        return finalized;
    }

    public void setFinalized(boolean finalized) {
        this.finalized = finalized;
    }

    public GroupIronmanGroupStats getStatistics() {
        return stats;
    }

    public GroupIronmanBank getBank() {
        return bank;
    }

    public int getJoined() {
        return joined;
    }

    public void setJoined(int joined) {
        this.joined = joined;
    }

    public CollectionLog getCollectionLog() {
        return collectionLog;
    }

    public void setCollectionLog(CollectionLog collectionLog) {
        this.collectionLog = collectionLog;
    }

    public Deque<GimWithdrawItemLog> getWithdrawItemLog() {
        return withdrawItemLog;
    }

    public Deque<GimDropItemLog> getDropItemLog() {
        return dropItemLog;
    }

    @Override
    public String toString() {
        return "GroupIronmanGroup{" +
                "name='" + name + '\'' +
                ", members=" + members +
                ", finalized=" + finalized +
                ", online=" + online +
                ", joined=" + joined +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GroupIronmanGroup that = (GroupIronmanGroup) o;
        return finalized == that.finalized &&
                Objects.equals(name, that.name) &&
                Objects.equals(members, that.members);
    }

    public List<String> getMergedCollectionLogs() {
        return mergedCollectionLogs;
    }
}
