package io.xeros.content.combat.weapon;

public enum WeaponInterface {
    SWORD_STAB(2276, 2279, 7574, 7586),
    SWORD_SLASH(2423, 2426, 7599, 7611),
    SWORD_CRUSH(4705, 4708, 7699, 7711),
    STAFF(328, 355, 12323, 12335),
    MAUL(425, 428, 7474, 7486),
    BATTLEAXE(1698, 1701, 7499, 7511),
    BOW(1764, 1767, 7549, 7561),
    MACE(3796, 3799, 7624, 7636),
    THROWN_RANGED(4446, 4449, 7649, 7661),
    SPEAR(4679, 4682, 7674, 7686),
    CLAWS(7762, 7765, 7800, 7812),
    HALBERD(8460, 8463, 8493, 8505),
    WHIP(12290, 12293, 12323, 12335),
    STICKS(6103, 6132, -1, -1),
    UNARMED(5855, 5857, -1, -1),
    SCYTHE(776, 779, -1, -1),
    ;

    private final int interfaceId;
    private final int nameInterfaceId;
    private final int specialBarInterfaceId;
    private final int specialBarAmountInterfaceId;

    WeaponInterface(int interfaceId, int nameInterfaceId, int specialBarInterfaceId, int specialBarAmountInterfaceId) {
        this.interfaceId = interfaceId;
        this.nameInterfaceId = nameInterfaceId;
        this.specialBarInterfaceId = specialBarInterfaceId;
        this.specialBarAmountInterfaceId = specialBarAmountInterfaceId;
    }

    public int getInterfaceId() {
        return interfaceId;
    }

    public int getNameInterfaceId() {
        return nameInterfaceId;
    }

    public int getSpecialBarInterfaceId() {
        return specialBarInterfaceId;
    }

    public int getSpecialBarAmountInterfaceId() {
        return specialBarAmountInterfaceId;
    }
}
