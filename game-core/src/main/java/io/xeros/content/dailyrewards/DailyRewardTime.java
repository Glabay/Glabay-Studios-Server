package io.xeros.content.dailyrewards;

import java.time.Month;

public class DailyRewardTime {
    private final int year;
    private final Month month;
    private final int day;

    public DailyRewardTime(final int year, final Month month, final int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return this.year;
    }

    public Month getMonth() {
        return this.month;
    }

    public int getDay() {
        return this.day;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof DailyRewardTime)) return false;
        final DailyRewardTime other = (DailyRewardTime) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getYear() != other.getYear()) return false;
        if (this.getDay() != other.getDay()) return false;
        final Object this$month = this.getMonth();
        final Object other$month = other.getMonth();
        if (this$month == null ? other$month != null : !this$month.equals(other$month)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof DailyRewardTime;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getYear();
        result = result * PRIME + this.getDay();
        final Object $month = this.getMonth();
        result = result * PRIME + ($month == null ? 43 : $month.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "DailyRewardTime(year=" + this.getYear() + ", month=" + this.getMonth() + ", day=" + this.getDay() + ")";
    }
}
