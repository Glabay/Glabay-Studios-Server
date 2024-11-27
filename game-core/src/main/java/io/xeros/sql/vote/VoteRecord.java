package io.xeros.sql.vote;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class VoteRecord {

    private final int siteId;
    private final Timestamp votedTime;
    private boolean throttled;

    public VoteRecord(int siteId, Timestamp votedTime, boolean throttled) {
        this.siteId = siteId;
        this.votedTime = votedTime;
        this.throttled = throttled;
    }

    @Override
    public String toString() {
        return "VoteRecord{" +
                "siteId=" + siteId +
                ", votedTime=" + votedTime +
                ", throttled=" + throttled +
                '}';
    }

    public int getSiteId() {
        return siteId;
    }

    public Timestamp getVotedTime() {
        return votedTime;
    }

    public LocalDateTime getVotedLocalDateTime() {
        return votedTime.toLocalDateTime();
    }

    public void setThrottled(boolean throttled) {
        this.throttled = throttled;
    }

    public boolean isThrottled() {
        return throttled;
    }
}
