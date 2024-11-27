package io.xeros.content.events.eventcalendar;

import io.xeros.model.entity.player.Player;

public class ChallengeParticipant {

    private final String username;
    private final String displayName;
    private final String ipAddress;
    private final String macAddress;
    private final int entryDay;

    public ChallengeParticipant(Player player, DateProvider dateProvider) {
        this(player.getLoginName(), player.getDisplayName(), player.getIpAddress(), player.getMacAddress(), dateProvider.getDay());
    }

    public ChallengeParticipant(String username, String ipAddress, String macAddress, int entryDay) {
        this(username, "N/A", ipAddress, macAddress, entryDay);
    }

    public ChallengeParticipant(String username, String displayName, String ipAddress, String macAddress, int entryDay) {
        this.username = username.toLowerCase();
        this.displayName = displayName;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.entryDay = entryDay;
    }

    @Override
    public String toString() {
        return "ChallengeParticipant{" +
                "username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", entryDay=" + entryDay +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getEntryDay() {
        return entryDay;
    }
}
