package io.xeros.content.combat.weapon;

import io.xeros.model.Items;

public class WeaponDataConstants {

    // [[shared, stab], [shared, slash], [shared, crush], [defensive, stab]]
    public static final int[] SPEARS = {
            Items.BRONZE_SPEAR, Items.IRON_SPEAR, Items.STEEL_SPEAR, Items.BLACK_SPEAR, Items.MITHRIL_SPEAR, Items.ADAMANT_SPEAR, Items.RUNE_SPEAR, Items.DRAGON_SPEAR,
            Items.VESTAS_SPEAR, Items.ANGER_SPEAR, Items.BONE_SPEAR, Items.LEAF_BLADED_SPEAR, Items.GUTHANS_WARSPEAR, Items.ZAMORAKIAN_SPEAR, Items.DRAGON_HUNTER_LANCE,

            // Hasta
            Items.BRONZE_HASTA, Items.IRON_HASTA, Items.STEEL_HASTA, Items.MITHRIL_HASTA, Items.ADAMANT_HASTA, Items.RUNE_HASTA, Items.DRAGON_HASTA,
            Items.GILDED_HASTA, Items.ZAMORAKIAN_HASTA,
    };

    // [[accurate, slash], [aggressive, slash], [shared, stab], [defensive, slash]]
    public static final int[] SLASH_SWORDS = {
            // Scimitars
            Items.DRAGON_SCIMITAR, Items.DRAGON_SCIMITAR_OR, Items.BRONZE_SCIMITAR, Items.IRON_SCIMITAR,
            Items.STEEL_SCIMITAR, Items.BLACK_SCIMITAR, Items.MITHRIL_SCIMITAR, Items.ADAMANT_SCIMITAR, Items.RUNE_SCIMITAR,
            Items.STARTER_SWORD, Items.CLEAVER, Items.GILDED_SCIMITAR, Items.BRINE_SABRE, Items.ARCLIGHT,
            Items.MACHETE, // ...
            Items.BLADE_OF_SAELDOR,

            // Longswords
            Items.DRAGON_LONGSWORD, Items.BRONZE_LONGSWORD, Items.IRON_LONGSWORD, Items.STEEL_LONGSWORD, Items.BLACK_LONGSWORD,
            Items.MITHRIL_LONGSWORD, Items.ADAMANT_LONGSWORD, Items.RUNE_LONGSWORD,
            Items.WOODEN_SPOON, Items.BLURITE_SWORD, Items.SILVERLIGHT, Items.DARKLIGHT, Items.EXCALIBUR, Items.FREMENNIK_BLADE, Items.SKEWER,
            Items.KATANA, Items.THIRD_AGE_LONGSWORD, Items.VESTAS_LONGSWORD,
            Items.DECORATIVE_SWORD, Items.ARCLIGHT, Items.EMBERLIGHT // ...
    };

    // [[accurate, slash], [aggressive, slash], [aggressive, crush], [defensive, slash]]
    public static final int[] CRUSH_SWORDS = {
        Items.RUNE_2H_SWORD, Items.BRONZE_2H_SWORD, Items.IRON_2H_SWORD, Items.BLACK_2H_SWORD, Items.MITHRIL_2H_SWORD, Items.ADAMANT_2H_SWORD,
        Items.DRAGON_2H_SWORD, Items.ARMADYL_GODSWORD, Items.SARADOMIN_GODSWORD, Items.ZAMORAK_GODSWORD, Items.BANDOS_GODSWORD,
        Items.ARMADYL_GODSWORD_OR, Items.SARADOMIN_GODSWORD_OR, Items.ZAMORAK_GODSWORD_OR, Items.BANDOS_GODSWORD_OR,
        Items.SARADOMIN_SWORD, Items.SARADOMINS_BLESSED_SWORD, Items.SHADOW_SWORD, Items.SPATULA, Items.GILDED_2H_SWORD,
    };

    public static final int[] BATTLEAXES = {
            // Battleaxes
            Items.DRAGON_BATTLEAXE, Items.IRON_BATTLEAXE, Items.STEEL_BATTLEAXE, Items.BLACK_BATTLEAXE, Items.MITHRIL_BATTLEAXE,
            Items.ADAMANT_BATTLEAXE, Items.RUNE_BATTLEAXE, Items.LEAF_BLADED_BATTLEAXE, Items.ANGER_BATTLEAXE,
            Items.DHAROKS_GREATAXE,
    };

    public static final int[] SCYTHE = {
            Items.SCYTHE, Items.SCYTHE_OF_VITUR,
    };

    // [[accurate, stab], [aggressive, stab], [aggressive, slash], [defensive, stab]]
    public static final int[] STAB_SWORDS = {
        // daggers
        Items.DRAGON_DAGGER, Items.BRONZE_DAGGER, Items.IRON_DAGGER, Items.STEEL_DAGGER, Items.BLACK_DAGGER, Items.MITHRIL_DAGGER, Items.ADAMANT_DAGGER, Items.RUNE_DAGGER,
        Items.DARK_DAGGER, Items.GLOWING_DAGGER, Items.EGG_WHISK, Items.WOLFBANE, Items.KITCHEN_KNIFE, Items.KERIS, Items.ABYSSAL_DAGGER, Items.TOKTZ_XIL_EK,

        // swords
        Items.DRAGON_SWORD, Items.RUNE_SWORD, Items.BRONZE_SWORD, Items.IRON_SWORD, Items.STEEL_SWORD, Items.BLACK_SWORD, Items.MITHRIL_SWORD, Items.ADAMANT_SWORD,
        Items.ANGER_SWORD, Items.TRAINING_SWORD, Items.WOODEN_SWORD, Items.SPORK, Items.RAPIER, Items.GHRAZI_RAPIER, Items.TOKTZ_XIL_AK, Items.LEAF_BLADED_SWORD,
        Items.WILDERNESS_SWORD_1, Items.WILDERNESS_SWORD_2, Items.WILDERNESS_SWORD_3, Items.WILDERNESS_SWORD_4,
    };

    // [[accurate, crush], [aggressive, crush], [shared, stab], [defensive, crush]]
    public static final int[] MACES = {
        Items.RUNE_MACE, Items.BRONZE_MACE, Items.IRON_MACE, Items.STEEL_MACE, Items.BLACK_MACE,
        Items.MITHRIL_MACE, Items.ADAMANT_MACE, Items.DRAGON_MACE,
        Items.VERACS_FLAIL, Items.BARRELCHEST_ANCHOR, Items.SARACHNIS_CUDGEL, Items.VIGGORAS_CHAINMACE, Items.INQUISITORS_MACE,
        Items.ANGER_MACE, Items.ANCIENT_MACE, Items.ROLLING_PIN, Items.TZHAAR_KET_EM,

    };

    // [[accurate, crush], [aggressive, crush], [defensive, crush]]
    public static final int[] WARHAMMERS = {
            Items.BRONZE_WARHAMMER, Items.IRON_WARHAMMER, Items.STEEL_WARHAMMER, Items.BLACK_WARHAMMER,
            Items.MITHRIL_WARHAMMER, Items.ADAMANT_WARHAMMER, Items.RUNE_WARHAMMER,
            Items.GRANITE_HAMMER, Items.TORAGS_HAMMERS, Items.DRAGON_WARHAMMER, Items.TZHAAR_KET_OM, Items.ELDER_MAUL,
            Items.ABYSSAL_BLUDGEON, Items.HILL_GIANT_CLUB, Items.GADDERHAMMER, Items.CURSED_GOBLIN_HAMMER, Items.FRYING_PAN,
            Items.MEAT_TENDERISER, Items.STATIUSS_WARHAMMER,
            Items.GRANITE_MAUL, Items.GRANITE_MAUL_OR,
    };

    // [[accurate, slash], [shared, slash], [defensive, slash]]
    public static final int[] WHIPS = {
            Items.ABYSSAL_WHIP, Items.FROZEN_ABYSSAL_WHIP, Items.VOLCANIC_ABYSSAL_WHIP, Items.ABYSSAL_TENTACLE,
    };

    // [[accurate, slash], [aggressive, slash], [aggressive, crush], [defensive, slash]]
    public static final int[] AXES = {
            Items.BRONZE_AXE, Items.IRON_AXE, Items.STEEL_AXE, Items.BLACK_AXE, Items.MITHRIL_AXE, Items.ADAMANT_AXE, Items.RUNE_AXE, Items.DRAGON_AXE,
            Items.BLESSED_AXE, Items.GILDED_AXE, Items.THIRD_AGE_AXE, Items.INFERNAL_AXE, Items.CRYSTAL_AXE, Items.DRAGON_HARPOON,
    };

    // [[accurate, stab], [aggressive, stab], [aggressive, crush], [defensive, stab]]c
    public static final int[] PICKAXES = {
            Items.RUNE_PICKAXE, Items.BRONZE_PICKAXE, Items.IRON_PICKAXE, Items.STEEL_PICKAXE, Items.BLACK_PICKAXE, Items.MITHRIL_PICKAXE, Items.ADAMANT_PICKAXE, Items.RUNE_PICKAXE,
            Items.DRAGON_PICKAXE, Items.DRAGON_PICKAXEOR, Items.THIRD_AGE_PICKAXE, Items.INFERNAL_PICKAXE, Items.CRYSTAL_PICKAXE,
    };

   // [[accurate, crush], [aggressive, crush], [defensive, crush]]
   public static final int[] STANDARD_STAFFS = {
            Items.STAFF_OF_AIR, Items.STAFF_OF_WATER, Items.STAFF_OF_EARTH, Items.STAFF_OF_FIRE,
           Items.AIR_BATTLESTAFF, Items.EARTH_BATTLESTAFF, Items.FIRE_BATTLESTAFF, Items.WATER_BATTLESTAFF,
           Items.MUD_BATTLESTAFF, Items.LAVA_BATTLESTAFF, Items.STEAM_BATTLESTAFF, Items.DUST_BATTLESTAFF,
           Items.SMOKE_BATTLESTAFF,
           Items.MYSTIC_AIR_STAFF, Items.MYSTIC_EARTH_STAFF, Items.MYSTIC_FIRE_STAFF, Items.MYSTIC_WATER_STAFF,
           Items.MYSTIC_LAVA_STAFF, Items.MYSTIC_MUD_STAFF, Items.MYSTIC_STEAM_STAFF, Items.MYSTIC_DUST_STAFF, Items.MYSTIC_SMOKE_STAFF,
           Items.BEGINNER_WAND, Items.APPRENTICE_WAND, Items.TEACHER_WAND, Items.MASTER_WAND, Items.THIRD_AGE_WAND, Items.KODAI_WAND,
           Items.TRIDENT_OF_THE_SEAS, Items.TRIDENT_OF_THE_SWAMP, Items.SANGUINESTI_STAFF, Items.DAWNBRINGER, Items.STAFF_OF_LIGHT,
           Items.STAFF_OF_BALANCE, Items.AHRIMS_STAFF, Items.ANCIENT_STAFF, Items.GUTHIX_STAFF, Items.SARADOMIN_STAFF, Items.ZAMORAK_STAFF,
           Items.NIGHTMARE_STAFF, Items.ELDRITCH_NIGHTMARE_STAFF, Items.HARMONISED_NIGHTMARE_STAFF, Items.VOLATILE_NIGHTMARE_STAFF,
           Items.SLAYERS_STAFF, Items.SLAYERS_STAFF_E, Items.THIRD_AGE_DRUIDIC_STAFF, Items.THAMMARONS_SCEPTRE, Items.THAMMARONS_SCEPTRE_U,
           Items.IBANS_STAFF, Items.IBANS_STAFF_U, Items.ZURIELS_STAFF, 27275,
   };

    // [[accurate, stab], [aggressive, slash], [defensive, crush]]
    public static final int[] SOTD = {
            Items.STAFF_OF_THE_DEAD,
            Items.TOXIC_STAFF_OF_THE_DEAD, Items.TOXIC_STAFF_UNCHARGED
    };

    // [[accurate, slash], [aggressive, slash], [shared, stab], [defensive, slash]]
    public static final int[] CLAWS = {
            Items.BRONZE_CLAWS, Items.IRON_CLAWS, Items.STEEL_CLAWS, Items.WHITE_CLAWS, Items.MITHRIL_CLAWS, Items.ADAMANT_CLAWS, Items.RUNE_CLAWS,
            Items.DRAGON_CLAWS, Items.BEACH_BOXING_GLOVES, Items.BOXING_GLOVES
    };

    // [[accurate, stab], [aggressive, slash], [defensive, stab]]
    public static final int[] HALBERDS = {
            Items.BRONZE_HALBERD, Items.IRON_HALBERD, Items.STEEL_HALBERD, Items.BLACK_HALBERD, Items.WHITE_HALBERD, Items.MITHRIL_HALBERD, Items.ADAMANT_HALBERD,
            Items.RUNE_HALBERD, Items.DRAGON_HALBERD, Items.CRYSTAL_HALBERD,
    };

    public static final int[] BOWS = {
            //Crossbows
            Items.CROSSBOW, Items.BRONZE_CROSSBOW, Items.IRON_CROSSBOW, Items.STEEL_CROSSBOW,  Items.MITH_CROSSBOW, Items.ADAMANT_CROSSBOW,
            Items.RUNE_CROSSBOW, Items.DRAGON_CROSSBOW, Items.ARMADYL_CROSSBOW,  Items.KARILS_CROSSBOW,
            Items.BLURITE_CROSSBOW,
            //Special Crossbows
            Items.PHOENIX_CROSSBOW, Items.LIGHT_BALLISTA, Items.HEAVY_BALLISTA, Items.DRAGON_HUNTER_CROSSBOW, Items.DORGESHUUN_CROSSBOW, Items.HUNTERS_CROSSBOW,
            //Shortbows
            Items.SHORTBOW, Items.OAK_SHORTBOW, Items.WILLOW_SHORTBOW, Items.MAPLE_SHORTBOW, Items.YEW_SHORTBOW, Items.MAGIC_SHORTBOW, Items.MAGIC_SHORTBOW_I,
            //Longbows
            Items.LONGBOW, Items.OAK_LONGBOW, Items.WILLOW_LONGBOW, Items.MAPLE_LONGBOW, Items.YEW_LONGBOW, Items.MAGIC_LONGBOW,
            //Special Bows
            Items.CURSED_GOBLIN_BOW, Items.TRAINING_BOW, Items.OGRE_BOW, Items.DARK_BOW, Items.CRYSTAL_BOW, Items.TWISTED_BOW, Items.CRAWS_BOW,
            Items.DARK_BOW_BLUE, Items.DARK_BOW_GREEN,Items.DARK_BOW_YELLOW,Items.DARK_BOW_WHITE, Items.THIRD_AGE_BOW,

            // Crystal bows
            412, 4213, 4214, 4215, 4216, 4217, 4218, 4219, 4220, 4221, 4222, 4223,

    };

    public static final int[] THROWN = {
            //Knifes
            Items.BRONZE_KNIFE, Items.IRON_KNIFE, Items.STEEL_KNIFE, Items.BLACK_KNIFE, Items.MITHRIL_KNIFE, Items.ADAMANT_KNIFE, Items.RUNE_KNIFE, Items.DRAGON_KNIFE,
            //Darts
            Items.BRONZE_DART, Items.IRON_DART, Items.STEEL_DART, Items.BLACK_DART, Items.MITHRIL_DART, Items.ADAMANT_DART, Items.RUNE_DART, Items.DRAGON_DART,
            //Thrownaxe
            Items.BRONZE_THROWNAXE, Items.IRON_THROWNAXE, Items.STEEL_THROWNAXE, Items.MITHRIL_THROWNAXE, Items.ADAMANT_THROWNAXE, Items.RUNE_THROWNAXE, Items.DRAGON_THROWNAXE, Items.MORRIGANS_THROWING_AXE,
            //Special Thrown
            Items.TOKTZ_XIL_UL, Items.CHINCHOMPA_2, Items.RED_CHINCHOMPA_2, Items.BLACK_CHINCHOMPA, Items.TOXIC_BLOWPIPE

    };
}

