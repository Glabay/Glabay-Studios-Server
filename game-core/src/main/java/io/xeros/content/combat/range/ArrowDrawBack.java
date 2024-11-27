package io.xeros.content.combat.range;

public enum ArrowDrawBack {
    BRONZE_ARROW(882, -1, 19, 10, 41, 30, 41,5),
    IRON_ARROW(884, -1, 18, 9, 41, 30, 41,5),
    STEEL_ARROW(886, -1, 20, 11, 41, 30, 41,5),
    MITHRIL_ARROW(888, -1, 21, 12, 41, 30, 41,5),
    ADAMANT_ARROW(890, -1, 22, 13, 41, 30, 41,5),
    RUNITE_ARROW(892, -1, 24, 15, 41, 30, 41,5),
    AMETHYST_ARROW(21326, -1, 1385, 1384, 41, 30, 41,5),
    DRAGON_ARROW(11212, -1, 1116, 1120, 45, 30, 41,5),
    DRAGON_ARROW_20389(20389, -1, 1116, 1120, 45, 30, 41,5),
    DRAGON_ARROW_P(11227, -1, 1116, 1120, 41, 30, 41,5),
    DRAGON_ARROW_P_PLUS(11228, -1, 1116, 1120, 41, 30, 41,5),
    DRAGON_ARROW_P_PLUS_PLUS(11229, -1, 1116, 1120, 41, 30, 41,5),
    FIRE_ARROW(4160, -1, 20, 11, 41, 30, 41,5),
    ICE_ARROW(78, -1, 1116, 1120, 41, 30, 41,5),
    BRONZE_JAV(825, -1, 344, 200, 40, 36, 41,5),
    IRON_JAV(826, -1, 344, 201, 40, 36, 41,5),
    STEEL_JAV(827, -1, 344, 202, 40, 36, 41,5),
    MITHRIL_JAV(828, -1, 344, 203, 40, 36, 41,5),
    ADDY_JAV(829, -1, 344, 204, 40, 36, 41,5),
    RUNE_JAV(830, -1, 344, 205, 40, 36, 41,5),
    AMETHYST_JAV(21318, -1, 344, 1386, 40, 36, 41,5),
    DRAGON_JAV(19484, -1, 344, 1301, 40, 36, 41,5),
    BOW_OF_FAERDHENIN(-1, 25865, 1888, 1887, 40, 36, 41,5),
    BOW_OF_FAERDHENIN_HEFIN(-1, 25867, 1923, 1922, 40, 36, 41,5),
    BOW_OF_FAERDHENIN_ITHELL(-1, 25884, 1925, 1924, 40, 36, 41,5),
    BOW_OF_FAERDHENIN_IOWERTH(-1, 25886, 1927, 1926, 40, 36, 41,5),
    BOW_OF_FAERDHENIN_TRAHEAERN(-1, 25888, 1929, 1928, 40, 36, 41,5),
    BOW_OF_FAERDHENIN_CADARN(-1, 25890, 1931, 1930, 40, 36, 41,5),
    BOW_OF_FAERDHENIN_CRWYS(-1, 25892, 1933, 1932, 40, 36, 41,5),
    BOW_OF_FAERDHENIN_AMLODD(-1, 25896, 1935, 1934, 40, 36, 41,5),
    CRAWS_BOW(-1, 22550, 1611, 1574, 41, 30, 41,5),
    WEB_WEAVER_BOW(-1, 27655, 2283, 2282, 41, 30, 41,5),
    VENATOR_BOW(-1, 27610, 2289, 2291, 30, 30, 40,5),
    STARTER_BOW(-1, 22333, 1385, 1384, 41, 30, 41,5);

    public final int weaponID;
    public final int gfx;
    public final int projectile;
    public final int startHeight;
    public final int endHeight;
    public final int startSpeed;

    public final int arrowID;

    public final int stepMultiplier;

    ArrowDrawBack(int arrowID, int weaponID, int gfx, int projectile, int startHeight, int endHeight, int startspeed, int stepMultiplier) {
        this.arrowID = arrowID;
        this.weaponID = weaponID;
        this.gfx = gfx;
        this.projectile = projectile;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.startSpeed = startspeed;
        this.stepMultiplier = stepMultiplier;
    }

    public static ArrowDrawBack find(int weaponID, int arrowID) {
        if (weaponID != -1) {
            for (ArrowDrawBack arrowDrawBack : ArrowDrawBack.values()) {
                if (arrowDrawBack.weaponID == weaponID) {
                    return arrowDrawBack;
                }
            }
            if (arrowID != -1) {
                for (ArrowDrawBack arrowDrawback : ArrowDrawBack.values()) {
                    if (arrowDrawback.arrowID == arrowID) {
                        return arrowDrawback;
                    }
                }
            }
        }
        return null;
    }
}