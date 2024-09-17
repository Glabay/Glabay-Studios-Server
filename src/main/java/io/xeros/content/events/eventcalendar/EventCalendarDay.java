package io.xeros.content.events.eventcalendar;

public enum EventCalendarDay {

    DAY_1(EventChallenge.KILL_X_NIGHTMARE),
    DAY_2(EventChallenge.WIN_AN_OUTLAST_TOURNAMENT),
    DAY_3(EventChallenge.THIEVE_X_STALLS),
    DAY_4(EventChallenge.PARTICIPATE_IN_X_OUTLAST_TOURNIES),
    DAY_5(EventChallenge.COMPLETE_X_HARD_SLAYER_ASSIGNMENTS),
    DAY_6(EventChallenge.KILL_X_GODWARS_BOSSES_OF_ANY_TYPE),
    DAY_7(EventChallenge.KILL_X_BARROWS_BROTHERS),
    DAY_8(EventChallenge.COMPLETE_X_RAIDS),
    DAY_9(EventChallenge.WIN_AN_OUTLAST_TOURNAMENT),
    DAY_10(EventChallenge.COMPLETE_X_RUNS_AT_THE_ZMI_ALTAR),
    DAY_11(EventChallenge.HAVE_126_COMBAT),
    DAY_12(EventChallenge.OBTAIN_X_WILDY_EVENT_KEYS),
    DAY_13(EventChallenge.KILL_X_REVS_IN_WILDY),
    DAY_14(EventChallenge.WIELD_A_DRAGON_DEFENDER),
    DAY_15(EventChallenge.KILL_HUNLLEF_X_TIMES),
    DAY_16(EventChallenge.WIN_AN_OUTLAST_TOURNAMENT),
    DAY_17(EventChallenge.KILL_ZULRAH_X_TIMES),
    DAY_18(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS),
    DAY_19(EventChallenge.OBTAIN_X_LARRENS_KEYS),
    DAY_20(EventChallenge.BURY_X_DRAGON_BONES),
    DAY_21(EventChallenge.CUT_DOWN_X_MAGIC_LOGS),
    DAY_22(EventChallenge.COMPLETE_TOB),
    DAY_23(EventChallenge.WIN_AN_OUTLAST_TOURNAMENT),
    DAY_24(EventChallenge.KILL_X_DEMONIC_GORILLAS),
    DAY_25(EventChallenge.GAIN_X_EXCHANGE_POINTS),
    DAY_26(EventChallenge.OBTAIN_X_WILDY_EVENT_KEYS),
    DAY_27(EventChallenge.KILL_X_WILDY_BOSSES),
    DAY_28(EventChallenge.OPEN_X_HESPORI_CHESTS),
    DAY_29(EventChallenge.HAVE_2000_TOTAL_LEVEL),
    DAY_30(EventChallenge.WIN_AN_OUTLAST_TOURNAMENT),
    DAY_31(EventChallenge.CONTRIBUTE_10M_TO_THE_WOGW),

    ;

    public static EventCalendarDay forDayOfTheMonth(long dayOfTheMonth) {
        long dayIndex = dayOfTheMonth - 1;
        if (dayIndex < 0 || dayIndex >= values().length) {
            System.err.println("Calendar day " + dayOfTheMonth + " is out of bounds, returning null.");
            return null;
        } else {
            return values()[(int)dayIndex];
        }
    }

    private final EventChallenge challenge;

    EventCalendarDay(EventChallenge challenge) {
        this.challenge = challenge;
    }

    public int[] getReward() {
        return isLastDay() ? new int[] {100, 125} : new int[] {50, 65};
    }

    public boolean isLastDay() {
        return ordinal() == values().length - 1;
    }

    public int getDay() {
        return ordinal() + 1;
    }

    public EventChallenge getChallenge() {
        return challenge;
    }
}
