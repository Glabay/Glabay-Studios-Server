package io.xeros.content.dailyrewards;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class DailyRewardsRecord {
    private final String address;
    private final LocalDateTime date;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DailyRewardsRecord that = (DailyRewardsRecord) o;
        return Objects.equals(address, that.address) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, date);
    }

    public DailyRewardsRecord(final String address, final LocalDateTime date) {
        this.address = address;
        this.date = date;
    }

    public String getAddress() {
        return this.address;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    @Override
    public String toString() {
        return "DailyRewardsRecord(address=" + this.getAddress() + ", date=" + this.getDate() + ")";
    }
}
