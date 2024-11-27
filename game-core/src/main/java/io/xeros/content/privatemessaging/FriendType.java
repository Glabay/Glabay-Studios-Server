package io.xeros.content.privatemessaging;

public enum FriendType {
    FRIEND("friends"),
    IGNORE("ignore")
    ;

    private final String name;

    FriendType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
