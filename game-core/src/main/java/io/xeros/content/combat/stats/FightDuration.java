package io.xeros.content.combat.stats;

public class FightDuration {
    private final String name;
    private final long ticks;

    public FightDuration(final String name, final long ticks) {
        this.name = name;
        this.ticks = ticks;
    }

    public String getName() {
        return this.name;
    }

    public long getTicks() {
        return this.ticks;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof FightDuration)) return false;
        final FightDuration other = (FightDuration) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getTicks() != other.getTicks()) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof FightDuration;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $ticks = this.getTicks();
        result = result * PRIME + (int) ($ticks >>> 32 ^ $ticks);
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "FightDuration(name=" + this.getName() + ", ticks=" + this.getTicks() + ")";
    }
}
