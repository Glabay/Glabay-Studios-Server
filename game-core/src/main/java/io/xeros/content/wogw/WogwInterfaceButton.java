package io.xeros.content.wogw;

public enum WogwInterfaceButton {
    EXPERIENCE_BOOST(22942, 22945, Wogw.EXPERIENCE_COINS_REQUIRED),
    PEST_CONTROL_BOOST(22946, 22949, Wogw.PC_COINS_REQUIRED),
    DROP_RATE_BOOST(22950, 22953, Wogw.DROP_RATE_COINS_REQUIRED);

    private final int buttonId;
    private final int coinsTextId;
    private final int coinsRequired;

    WogwInterfaceButton(int buttonId, int coinsTextId, int coinsRequired) {
        this.buttonId = buttonId;
        this.coinsTextId = coinsTextId;
        this.coinsRequired = coinsRequired;
    }

    public long getCurrentCoins() {
        switch (this) {
            case EXPERIENCE_BOOST:
                return Wogw.MONEY_TOWARDS_EXPERIENCE;
            case PEST_CONTROL_BOOST:
                return Wogw.MONEY_TOWARDS_PC_POINTS;
            case DROP_RATE_BOOST:
                return Wogw.MONEY_TOWARDS_DROP_RATE_BOOST;
            default:
                throw new IllegalArgumentException(toString());
        }
    }

    public int getButtonId() {
        return buttonId;
    }

    public int getCoinsRequired() {
        return coinsRequired;
    }

    public int getCoinsTextId() {
        return coinsTextId;
    }

    @Override
    public String toString() {
        return name().toLowerCase().replaceAll("_", " ");
    }
}
