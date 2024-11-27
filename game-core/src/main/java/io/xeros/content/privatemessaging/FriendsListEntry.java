package io.xeros.content.privatemessaging;

public class FriendsListEntry {

    public static FriendsListEntry withDisplayName(FriendsListEntry friendsListEntry, String displayName) {
        return new FriendsListEntry(friendsListEntry.getType(), friendsListEntry.getLoginName(), displayName);
    }

    private final FriendType type;
    private final String loginName;
    private final String displayName;

    public FriendsListEntry(FriendType type, String loginName, String displayName) {
        this.type = type;
        this.loginName = loginName;
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "FriendOrIgnore{" +
                "type=" + type +
                ", loginName='" + loginName + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }

    public FriendType getType() {
        return type;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
