package io.xeros.content.events.eventcalendar;

public class ChallengeWinner {

    private final String username;
    private final String displayName;
    private final int day;

    public ChallengeWinner(String username, String displayName, int day) {
        this.username = username.toLowerCase();
        this.displayName = displayName;
        this.day = day;
    }

    @Override
    public String toString() {
        return "ChallengeWinner{" +
                "username='" + username + '\'' +
                ", day=" + day +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDay() {
        return day;
    }
}
