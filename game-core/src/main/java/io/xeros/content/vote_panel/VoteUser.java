package io.xeros.content.vote_panel;

/**
 * @author Grant_ | www.rune-server.ee/members/grant_ | 3/23/20
 * Essentially each player is stored as a vote user once they vote.
 */

public class VoteUser {
    public static int MAX_DAY_STREAK = 5;
    private int voteCount;
    private long firstVoteTimestamp;
    private int dayStreak;
    private int bluePoints;
    private int redPoints;
    private int prizeSlot;

    public VoteUser(int voteCount, long firstVoteTimestamp) {
        this.voteCount = voteCount;
        this.firstVoteTimestamp = firstVoteTimestamp;
        this.dayStreak = 0;
        this.bluePoints = 0;
        this.redPoints = 0;
        this.prizeSlot = -1;
    }

    public void incrementVoteCount() {
        this.voteCount++;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public long getFirstVoteTimestamp() {
        return firstVoteTimestamp;
    }

    public int getPrizeSlot() {
        return prizeSlot;
    }

    public void setPrizeSlot(int prizeSlot) {
        this.prizeSlot = prizeSlot;
    }

    public void setFirstVoteTimestamp(long timestamp) {
        this.firstVoteTimestamp = timestamp;
    }

    public void incrementDayStreak() {
        this.dayStreak++;
        switch(dayStreak) {
            case 1:
                bluePoints += 2;
                break;
            case 2:
                bluePoints += 3;
                break;
            case 3:
                bluePoints += 4;
                break;
            case 4:
                bluePoints += 5;
                break;
            case 5:
                redPoints++;
                break;
        }
    }

    public void resetDayStreak() {
        this.dayStreak = 0;
    }

    public void setDayStreak(int dayStreak) {
        this.dayStreak = dayStreak;
    }

    public void resetVoteCount() {
        this.voteCount = 0;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public void resetFirstVoteTimestamp() {
        this.firstVoteTimestamp = 0;
    }

    public int getDayStreak() {
        return dayStreak;
    }

    public int getBluePoints() {
        return bluePoints;
    }

    public void setBluePoints(int bluePoints) {
        this.bluePoints = bluePoints;
    }

    public int getRedPoints() {
        return redPoints;
    }

    public void setRedPoints(int redPoints) {
        this.redPoints = redPoints;
    }
}
