package io.xeros.model.definitions;

import io.xeros.model.entity.Bonus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class ItemEquipmentStats {
    public int getBonus(Bonus bonus) {
        return switch (bonus) {
            case ATTACK_STAB -> astab;
            case ATTACK_SLASH -> aslash;
            case ATTACK_CRUSH -> acrush;
            case ATTACK_MAGIC -> amagic;
            case ATTACK_RANGED -> arange;
            case DEFENCE_STAB -> dstab;
            case DEFENCE_SLASH -> dslash;
            case DEFENCE_CRUSH -> dcrush;
            case DEFENCE_MAGIC -> dmagic;
            case DEFENCE_RANGED -> drange;
            case STRENGTH -> str;
            case RANGED_STRENGTH -> rstr;
            case MAGIC_DMG -> mdmg;
            case PRAYER -> prayer;
        };
    }

    public int getAttackSpeed() {
        return aspeed;
    }

    public int getEquipmentSlot() {
        return slot;
    }

    private final int slot;
    private final int astab;
    private final int aslash;
    private final int acrush;
    private final int amagic;
    private final int arange;
    private final int dstab;
    private final int dslash;
    private final int dcrush;
    private final int dmagic;
    private final int drange;
    private final int str;
    private final int rstr;
    private final int mdmg;
    private final int prayer;
    private final int aspeed;

    public static class ItemEquipmentStatsBuilder {

        private int slot;

        private int astab;

        private int aslash;

        private int acrush;

        private int amagic;

        private int arange;

        private int dstab;

        private int dslash;

        private int dcrush;

        private int dmagic;

        private int drange;

        private int str;

        private int rstr;

        private int mdmg;

        private int prayer;

        private int aspeed;

        ItemEquipmentStatsBuilder() {
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder slot(final int slot) {
            this.slot = slot;
            return this;
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder astab(final int astab) {
            this.astab = astab;
            return this;
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder aslash(final int aslash) {
            this.aslash = aslash;
            return this;
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder acrush(final int acrush) {
            this.acrush = acrush;
            return this;
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder amagic(final int amagic) {
            this.amagic = amagic;
            return this;
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder arange(final int arange) {
            this.arange = arange;
            return this;
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder dstab(final int dstab) {
            this.dstab = dstab;
            return this;
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder dslash(final int dslash) {
            this.dslash = dslash;
            return this;
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder dcrush(final int dcrush) {
            this.dcrush = dcrush;
            return this;
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder dmagic(final int dmagic) {
            this.dmagic = dmagic;
            return this;
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder drange(final int drange) {
            this.drange = drange;
            return this;
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder str(final int str) {
            this.str = str;
            return this;
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder rstr(final int rstr) {
            this.rstr = rstr;
            return this;
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder mdmg(final int mdmg) {
            this.mdmg = mdmg;
            return this;
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder prayer(final int prayer) {
            this.prayer = prayer;
            return this;
        }

        public ItemEquipmentStats.ItemEquipmentStatsBuilder aspeed(final int aspeed) {
            this.aspeed = aspeed;
            return this;
        }

        public ItemEquipmentStats build() {
            return new ItemEquipmentStats(this.slot, this.astab, this.aslash, this.acrush, this.amagic, this.arange, this.dstab, this.dslash, this.dcrush, this.dmagic, this.drange, this.str, this.rstr, this.mdmg, this.prayer, this.aspeed);
        }

        @Override
    public String toString() {
            return "ItemEquipmentStats.ItemEquipmentStatsBuilder(slot=" + this.slot + ", astab=" + this.astab + ", aslash=" + this.aslash + ", acrush=" + this.acrush + ", amagic=" + this.amagic + ", arange=" + this.arange + ", dstab=" + this.dstab + ", dslash=" + this.dslash + ", dcrush=" + this.dcrush + ", dmagic=" + this.dmagic + ", drange=" + this.drange + ", str=" + this.str + ", rstr=" + this.rstr + ", mdmg=" + this.mdmg + ", prayer=" + this.prayer + ", aspeed=" + this.aspeed + ")";
        }
    }

    public static ItemEquipmentStats.ItemEquipmentStatsBuilder builder() {
        return new ItemEquipmentStats.ItemEquipmentStatsBuilder();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ItemEquipmentStats other)) return false;
        if (this.getSlot() != other.getSlot()) return false;
        if (this.getAstab() != other.getAstab()) return false;
        if (this.getAslash() != other.getAslash()) return false;
        if (this.getAcrush() != other.getAcrush()) return false;
        if (this.getAmagic() != other.getAmagic()) return false;
        if (this.getArange() != other.getArange()) return false;
        if (this.getDstab() != other.getDstab()) return false;
        if (this.getDslash() != other.getDslash()) return false;
        if (this.getDcrush() != other.getDcrush()) return false;
        if (this.getDmagic() != other.getDmagic()) return false;
        if (this.getDrange() != other.getDrange()) return false;
        if (this.getStr() != other.getStr()) return false;
        if (this.getRstr() != other.getRstr()) return false;
        if (this.getMdmg() != other.getMdmg()) return false;
        if (this.getPrayer() != other.getPrayer()) return false;
        return this.getAspeed() == other.getAspeed();
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getSlot();
        result = result * PRIME + this.getAstab();
        result = result * PRIME + this.getAslash();
        result = result * PRIME + this.getAcrush();
        result = result * PRIME + this.getAmagic();
        result = result * PRIME + this.getArange();
        result = result * PRIME + this.getDstab();
        result = result * PRIME + this.getDslash();
        result = result * PRIME + this.getDcrush();
        result = result * PRIME + this.getDmagic();
        result = result * PRIME + this.getDrange();
        result = result * PRIME + this.getStr();
        result = result * PRIME + this.getRstr();
        result = result * PRIME + this.getMdmg();
        result = result * PRIME + this.getPrayer();
        result = result * PRIME + this.getAspeed();
        return result;
    }

    @Override
    public String toString() {
        return "ItemEquipmentStats(slot=" + this.getSlot() + ", astab=" + this.getAstab() + ", aslash=" + this.getAslash() + ", acrush=" + this.getAcrush() + ", amagic=" + this.getAmagic() + ", arange=" + this.getArange() + ", dstab=" + this.getDstab() + ", dslash=" + this.getDslash() + ", dcrush=" + this.getDcrush() + ", dmagic=" + this.getDmagic() + ", drange=" + this.getDrange() + ", str=" + this.getStr() + ", rstr=" + this.getRstr() + ", mdmg=" + this.getMdmg() + ", prayer=" + this.getPrayer() + ", aspeed=" + this.getAspeed() + ")";
    }
}
