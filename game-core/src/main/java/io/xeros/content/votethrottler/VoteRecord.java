package io.xeros.content.votethrottler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class VoteRecord {
    private final String address;
    private LocalDateTime localDateTime;
    private int voteCount;

    public VoteRecord(String address) {
        this.address = address;
        localDateTime = LocalDateTime.now();
    }

    public boolean is12HoursElapsed() {
        return ChronoUnit.HOURS.between(getLocalDateTime(), LocalDateTime.now()) >= 12;
    }

    public void reset() {
        localDateTime = LocalDateTime.now();
        voteCount = 0;
    }

    public String getAddress() {
        return this.address;
    }

    public LocalDateTime getLocalDateTime() {
        return this.localDateTime;
    }

    public int getVoteCount() {
        return this.voteCount;
    }

    public void setLocalDateTime(final LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public void setVoteCount(final int voteCount) {
        this.voteCount = voteCount;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof VoteRecord)) return false;
        final VoteRecord other = (VoteRecord) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getVoteCount() != other.getVoteCount()) return false;
        final Object this$address = this.getAddress();
        final Object other$address = other.getAddress();
        if (this$address == null ? other$address != null : !this$address.equals(other$address)) return false;
        final Object this$localDateTime = this.getLocalDateTime();
        final Object other$localDateTime = other.getLocalDateTime();
        if (this$localDateTime == null ? other$localDateTime != null : !this$localDateTime.equals(other$localDateTime)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof VoteRecord;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getVoteCount();
        final Object $address = this.getAddress();
        result = result * PRIME + ($address == null ? 43 : $address.hashCode());
        final Object $localDateTime = this.getLocalDateTime();
        result = result * PRIME + ($localDateTime == null ? 43 : $localDateTime.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "VoteRecord(address=" + this.getAddress() + ", localDateTime=" + this.getLocalDateTime() + ", voteCount=" + this.getVoteCount() + ")";
    }
}
