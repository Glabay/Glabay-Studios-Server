package io.xeros.content.privatemessaging;

import io.xeros.model.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class FriendsListRepository {

    /**
     * Map {@link FriendsListEntry} to {@link Player#getLoginNameLower()}.
     */
    private final Map<String, FriendsListEntry> friends = new HashMap<>();

    public void add(FriendsListEntry friendsListEntry) {
        friends.put(friendsListEntry.getLoginName().toLowerCase(), friendsListEntry);
    }

    public boolean remove(String displayName, FriendType type) {
        for (Map.Entry<String, FriendsListEntry> entry : friends.entrySet()) {
            if (type != entry.getValue().getType())
                continue;
            if (entry.getValue().getDisplayName().equalsIgnoreCase(displayName)) {
                friends.remove(entry.getKey());
                return true;
            }
        }

        return false;
    }

    public FriendsListEntry get(Player player) {
        return friends.get(player.getLoginNameLower());
    }

    public int getSize(FriendType type) {
        return (int) friends.values().stream().filter(it -> it.getType() == type).count();
    }

    public boolean isFriend(Player player) {
        return isFriend(player.getLoginNameLower());
    }

    public boolean isIgnore(Player player) {
        if (player.getRights().hasStaffPosition()) {
            return false;
        }

        return isIgnore(player.getLoginNameLower());
    }

    public boolean isFriend(String loginName) {
        return is(FriendType.FRIEND, loginName.toLowerCase());
    }

    public boolean isIgnore(String loginName) { // TODO don't let staff members be ignored
        return is(FriendType.IGNORE, loginName.toLowerCase());
    }

    public boolean isFriend(FriendsListEntry friendsListEntry) {
        return is(FriendType.FRIEND, friendsListEntry.getLoginName().toLowerCase());
    }

    public boolean isIgnore(FriendsListEntry friendsListEntry) {
        return is(FriendType.IGNORE, friendsListEntry.getLoginName().toLowerCase());
    }

    private boolean is(FriendType type, String loginNameLower) {
        FriendsListEntry listed = friends.get(loginNameLower);
        return listed != null && listed.getType() == type;
    }

    public Map<String, FriendsListEntry> getAll() {
        return friends;
    }
}
