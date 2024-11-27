package io.xeros.content.referral;

import java.time.LocalDateTime;

public class Referral {
    private final String username;
    private final LocalDateTime date;

    public Referral(final String username, final LocalDateTime date) {
        this.username = username;
        this.date = date;
    }

    public String getUsername() {
        return this.username;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Referral)) return false;
        final Referral other = (Referral) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$username = this.getUsername();
        final Object other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) return false;
        final Object this$date = this.getDate();
        final Object other$date = other.getDate();
        if (this$date == null ? other$date != null : !this$date.equals(other$date)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Referral;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $username = this.getUsername();
        result = result * PRIME + ($username == null ? 43 : $username.hashCode());
        final Object $date = this.getDate();
        result = result * PRIME + ($date == null ? 43 : $date.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Referral(username=" + this.getUsername() + ", date=" + this.getDate() + ")";
    }
}
