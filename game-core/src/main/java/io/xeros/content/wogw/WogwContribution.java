package io.xeros.content.wogw;

import io.xeros.util.Misc;

public class WogwContribution {

    private final String displayName;
    private final long contribution;

    public WogwContribution(String displayName, long contribution) {
        this.displayName = displayName;
        this.contribution = contribution;
    }

    @Override
    public String toString() {
        return displayName + " (" + Misc.getCoinColour(contribution) + Misc.formatCoins(contribution) + "</col>" + ")";
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getContribution() {
        return contribution;
    }
}
