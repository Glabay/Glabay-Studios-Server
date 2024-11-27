package io.xeros.content.combat.weapon;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.xeros.content.skills.herblore.PoisonedWeapon;

public enum WeaponData {
    SPEAR(WeaponDataConstants.SPEARS, WeaponInterface.SPEAR,
            new WeaponMode(0, AttackStyle.CONTROLLED, CombatStyle.STAB),
            new WeaponMode(1, AttackStyle.CONTROLLED, CombatStyle.SLASH),
            new WeaponMode(2, AttackStyle.CONTROLLED, CombatStyle.CRUSH),
            new WeaponMode(3, AttackStyle.DEFENSIVE, CombatStyle.STAB)),

    SLASH_SWORD(WeaponDataConstants.SLASH_SWORDS, WeaponInterface.SWORD_SLASH,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.SLASH),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.SLASH),
            new WeaponMode(2, AttackStyle.CONTROLLED, CombatStyle.STAB),
            new WeaponMode(3, AttackStyle.DEFENSIVE, CombatStyle.SLASH)),

    CRUSH_SWORD(WeaponDataConstants.CRUSH_SWORDS, WeaponInterface.SWORD_CRUSH,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.SLASH),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.SLASH),
            new WeaponMode(2, AttackStyle.AGGRESSIVE, CombatStyle.CRUSH),
            new WeaponMode(3, AttackStyle.DEFENSIVE, CombatStyle.SLASH)),

    STAB_SWORD(WeaponDataConstants.STAB_SWORDS, WeaponInterface.SWORD_STAB,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.STAB),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.STAB),
            new WeaponMode(2, AttackStyle.AGGRESSIVE, CombatStyle.SLASH),
            new WeaponMode(3, AttackStyle.DEFENSIVE, CombatStyle.STAB)),

    SCYTHE(WeaponDataConstants.SCYTHE, WeaponInterface.SCYTHE,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.SLASH),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.STAB),
            new WeaponMode(2, AttackStyle.AGGRESSIVE, CombatStyle.CRUSH),
            new WeaponMode(3, AttackStyle.DEFENSIVE, CombatStyle.SLASH)),

    BATTLEAXE(WeaponDataConstants.BATTLEAXES, WeaponInterface.BATTLEAXE,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.SLASH),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.SLASH),
            new WeaponMode(2, AttackStyle.AGGRESSIVE, CombatStyle.CRUSH),
            new WeaponMode(3, AttackStyle.DEFENSIVE, CombatStyle.SLASH)),

    PICKAXE(WeaponDataConstants.PICKAXES, WeaponInterface.BATTLEAXE,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.SLASH),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.SLASH),
            new WeaponMode(2, AttackStyle.AGGRESSIVE, CombatStyle.CRUSH),
            new WeaponMode(3, AttackStyle.DEFENSIVE, CombatStyle.SLASH)),

    AXE(WeaponDataConstants.AXES, WeaponInterface.BATTLEAXE,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.SLASH),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.SLASH),
            new WeaponMode(2, AttackStyle.AGGRESSIVE, CombatStyle.CRUSH),
            new WeaponMode(3, AttackStyle.DEFENSIVE, CombatStyle.SLASH)),

    WHIP(WeaponDataConstants.WHIPS, WeaponInterface.WHIP,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.SLASH),
            new WeaponMode(1, AttackStyle.CONTROLLED, CombatStyle.SLASH),
            new WeaponMode(2, AttackStyle.DEFENSIVE, CombatStyle.SLASH)),

    STAFF(WeaponDataConstants.STANDARD_STAFFS, WeaponInterface.STAFF,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.CRUSH),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.CRUSH),
            new WeaponMode(2, AttackStyle.DEFENSIVE, CombatStyle.CRUSH)),

    SOTD(WeaponDataConstants.SOTD, WeaponInterface.STAFF,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.STAB),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.SLASH),
            new WeaponMode(2, AttackStyle.DEFENSIVE, CombatStyle.CRUSH)),

    CLAWS(WeaponDataConstants.CLAWS, WeaponInterface.CLAWS,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.SLASH),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.SLASH),
            new WeaponMode(2, AttackStyle.CONTROLLED, CombatStyle.STAB),
            new WeaponMode(3, AttackStyle.DEFENSIVE, CombatStyle.SLASH)),

    HALBERD(WeaponDataConstants.HALBERDS, WeaponInterface.HALBERD,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.STAB),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.SLASH),
            new WeaponMode(2, AttackStyle.DEFENSIVE, CombatStyle.STAB)),

    MACE(WeaponDataConstants.MACES, WeaponInterface.MACE,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.CRUSH),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.CRUSH),
            new WeaponMode(2, AttackStyle.CONTROLLED, CombatStyle.STAB),
            new WeaponMode(3, AttackStyle.DEFENSIVE, CombatStyle.CRUSH)),

    WARHAMMER(WeaponDataConstants.WARHAMMERS, WeaponInterface.MAUL,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.CRUSH),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.CRUSH),
            new WeaponMode(2, AttackStyle.DEFENSIVE, CombatStyle.CRUSH)),

    BOW(WeaponDataConstants.BOWS, WeaponInterface.BOW,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.RANGE),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.RANGE),
            new WeaponMode(2, AttackStyle.DEFENSIVE, CombatStyle.RANGE)),

    THROWN(WeaponDataConstants.THROWN, WeaponInterface.THROWN_RANGED,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.RANGE),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.RANGE),
            new WeaponMode(2, AttackStyle.DEFENSIVE, CombatStyle.RANGE)),

    UNARMED(new int[] {0}, WeaponInterface.UNARMED,
            new WeaponMode(0, AttackStyle.ACCURATE, CombatStyle.RANGE),
            new WeaponMode(1, AttackStyle.AGGRESSIVE, CombatStyle.RANGE),
            new WeaponMode(2, AttackStyle.DEFENSIVE, CombatStyle.RANGE)),

    ;

    private final int[] items;
    private final WeaponInterface weaponInterface;
    private final WeaponMode[] weaponModes;

    WeaponData(int[] items, WeaponInterface weaponInterface, WeaponMode...weaponModes) {
        this.items = items.clone();
        this.weaponInterface = weaponInterface;
        this.weaponModes = weaponModes;
    }

    public int[] getItems() {
        return items;
    }

    public WeaponMode[] getWeaponModes() {
        return weaponModes;
    }

    public WeaponInterface getWeaponInterface() {
        return weaponInterface;
    }

    private static final Map<Integer, WeaponData> weaponModeMap = new HashMap<>();

    static {
        Arrays.stream(values()).forEach(data -> Arrays.stream(data.items).forEach(item -> weaponModeMap.put(item, data)));
    }

    public static WeaponData forItemId(int itemId) {
        Optional<Integer> poison = PoisonedWeapon.getOriginal(itemId);
        return weaponModeMap.getOrDefault(poison.orElse(itemId), UNARMED);
    }

    /**
     * Generate comments for the item constants.
     */
    public static void main(String...args) {
        for (WeaponData data : values()) {
            System.out.println(data.toString() + ":" + Arrays.toString(data.weaponModes));
        }
    }
}
