package io.xeros.content;

import java.util.Arrays;
import java.util.Optional;

import io.xeros.model.definitions.ItemDef;

public class Sounds {

    private static final int DEFAULT_EQUIP_SOUND = 2238;

    private static final SoundId[] EQUIP_ITEM_SOUNDS = {
            // Equipment
           // new SoundId(2241, "d'hide"),
            new SoundId(2239, "chainbody", "platebody"),
            //new SoundId(2440, "helm"),
            //new SoundId(2442, "platelegs", "plateskirt"),
            new SoundId(2237, "boots"),

            // Weapons/ammo
            new SoundId(2248, "sword", "scimitar", "mace", "flail", "axe", "pickaxe", "battleaxe", "greataxe"),
            new SoundId(2247, "staff", "spear", "hasta", "lance"),
            new SoundId(2244, "bow", "blowpipe", "arrow"),
            new SoundId(2235, "bolt"),
    };

    public static int getEquipItemSound(int item) {
        String name = ItemDef.forId(item).getName().toLowerCase();
        Optional<SoundId> itemEquipSound = Arrays.stream(EQUIP_ITEM_SOUNDS).filter(id -> Arrays.stream(id.identifier).anyMatch(name::contains)).findFirst();
        return itemEquipSound.map(soundId -> soundId.soundId).orElse(DEFAULT_EQUIP_SOUND);
    }

    private record SoundId(int soundId, String... identifier) {}
}
