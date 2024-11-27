package io.xeros.content.skills.crafting;

enum Gem {
    TOXIC_GEM(23778, 23783, 50, 5),
    DIAMOND(1617, 1601, 43, 107),
    RUBY(1619, 1603, 34, 85),
    EMERALD(1621, 1605, 27, 68),
    SAPPHIRE(1623, 1607, 20, 50),
    OPAL(1625, 1609, 1, 15),
    JADE(1627, 1611, 13, 20),
    TOPAZ(1629, 1613, 16, 25),
    DRAGONSTONE(1631, 1615, 55, 137),
    ONYX(6571, 6573, 67, 168),
    ZENYTE(19496, 19493, 89, 200);

    private final int uncut, cut, level, experience;

    Gem(int uncut, int cut, int level, int experience) {
        this.uncut = uncut;
        this.cut = cut;
        this.level = level;
        this.experience = experience;
    }

    public int getUncut() {
        return uncut;
    }

    public int getCut() {
        return cut;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }
}
