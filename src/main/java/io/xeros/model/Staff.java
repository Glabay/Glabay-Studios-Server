package io.xeros.model;

import com.google.common.collect.Lists;
import io.xeros.model.definition.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Staff {
    AIR(Lists.newArrayList(
        Items.STAFF_OF_AIR,
        Items.AIR_BATTLESTAFF,
        Items.DUST_BATTLESTAFF,
        Items.SMOKE_BATTLESTAFF,
        Items.MYSTIC_DUST_STAFF,
        Items.MYSTIC_SMOKE_STAFF),
        Lists.newArrayList(Items.AIR_RUNE)),
    WATER(Lists.newArrayList(
        Items.STAFF_OF_WATER,
        Items.WATER_BATTLESTAFF,
        Items.MUD_BATTLESTAFF,
        Items.STEAM_BATTLESTAFF,
        Items.MYSTIC_MUD_STAFF,
        Items.MYSTIC_STEAM_STAFF,
        Items.KODAI_WAND),
        Lists.newArrayList(Items.WATER_RUNE)),
    EARTH(Lists.newArrayList(
        Items.STAFF_OF_EARTH,
        Items.EARTH_BATTLESTAFF,
        Items.MUD_BATTLESTAFF,
        Items.LAVA_BATTLESTAFF,
        Items.DUST_BATTLESTAFF,
        Items.MYSTIC_MUD_STAFF,
        Items.MYSTIC_LAVA_STAFF,
        Items.MYSTIC_DUST_STAFF),
        Lists.newArrayList(Items.EARTH_RUNE)),
    FIRE(Lists.newArrayList(
        Items.STAFF_OF_FIRE,
        Items.FIRE_BATTLESTAFF,
        Items.LAVA_BATTLESTAFF,
        Items.STEAM_BATTLESTAFF,
        Items.SMOKE_BATTLESTAFF,
        Items.MYSTIC_LAVA_STAFF,
        Items.MYSTIC_STEAM_STAFF,
        Items.MYSTIC_STEAM_STAFF,
        Items.MYSTIC_SMOKE_STAFF,
        Items.TOME_OF_FIRE),
        Lists.newArrayList(Items.FIRE_RUNE)),
    ;

    private final List<Integer> staffs;
    private final List<Integer> runesProvided;

    Staff(List<Integer> staffs, List<Integer> runesProvided) {
        this.staffs = Collections.unmodifiableList(staffs);
        this.runesProvided = Collections.unmodifiableList(runesProvided);
    }

    public static List<Integer> getRunesProvidedBy(int weapon) {
        List<Integer> runes = new ArrayList<>();
        Arrays.stream(Staff.values())
            .filter(staff -> staff.staffs.contains(weapon))
            .forEach(staff -> runes.addAll(staff.runesProvided));
        return runes;
    }
}
