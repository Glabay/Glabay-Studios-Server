package io.xeros.content.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.xeros.Server;
import io.xeros.model.definition.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.mode.ModeType;
import io.xeros.model.items.ImmutableItem;

public class Starter {

    private static final String STARTER_ATTRIBUTE_KEY = "starters";

    public static List<String> getStarters(boolean groupIronman) {
        String key = STARTER_ATTRIBUTE_KEY + (groupIronman ? "_gim" : "");
        if (Server.getServerAttributes().getList(key) == null) {
            Server.getServerAttributes().setList(key, new ArrayList());
        }

        return (List<String>) Server.getServerAttributes().getList(key);
    }

    public static void addStarter(Player c) {
        c.setDropWarning(false);
        boolean isGroupIronman = c.getRights().contains(Right.GROUP_IRONMAN);
        List<String> starters = getStarters(isGroupIronman).stream().filter(Objects::nonNull).collect(Collectors.toList());
        long occurances = starters.stream().filter(data -> data.equals(c.getMacAddress())).count();

        if (c.receivedStarter) {
            return;
        }

        c.receivedStarter = true;
        if (occurances >= 5 && !Server.isDebug()) {
            c.sendMessage("@red@You have already received 5 starters.");
            return;
        }

        // We don't give ironman starters to rogue ironmen because we fucked up and gave the first people normals
        if (c.getMode().isIronmanType() && c.getMode().getType() != ModeType.ROGUE_HARDCORE_IRONMAN && c.getMode().getType() != ModeType.ROGUE_IRONMAN)  {
            if (c.getMode().isHardcoreIronman()) {
                c.getItems().equipItem(Items.HARDCORE_IRONMAN_HELM, 1, c.playerHat);
                c.getItems().equipItem(Items.HARDCORE_IRONMAN_PLATEBODY, 1, c.playerChest);
                c.getItems().equipItem(Items.HARDCORE_IRONMAN_PLATELEGS, 1, c.playerLegs);
            } else if (c.getMode().isUltimateIronman()) {
                c.getItems().equipItem(Items.ULTIMATE_IRONMAN_HELM, 1, c.playerHat);
                c.getItems().equipItem(Items.ULTIMATE_IRONMAN_PLATEBODY, 1, c.playerChest);
                c.getItems().equipItem(Items.ULTIMATE_IRONMAN_PLATELEGS, 1, c.playerLegs);
            } else {
                c.getItems().equipItem(Items.IRONMAN_HELM, 1, c.playerHat);
                c.getItems().equipItem(Items.IRONMAN_PLATEBODY, 1, c.playerChest);
                c.getItems().equipItem(Items.IRONMAN_PLATELEGS, 1, c.playerLegs);
            }

            c.getItems().equipItem(1323, 1, c.playerWeapon);
            c.getItems().addItem(1171, 1);
            c.getItems().addItem(380, 100);
            c.getItems().addItem(841, 1);
            c.getItems().addItem(882, 100);
            c.getItems().addItem(556, 100);
            c.getItems().addItem(558, 100);
            c.getItems().addItem(555, 100);
            c.getItems().addItem(557, 100);
            c.getItems().addItem(559, 100);
            c.getItems().addItem(554, 100);
            c.getItems().addItem(563, 100);
            c.getItems().addItem(1381, 1);
        } else {
            addStarterItems(c);
        }

        List<String> startersList = getStarters(isGroupIronman);
        startersList.add(c.getMacAddress());
        Server.getServerAttributes().write();
    }

    public static void addStarterItems(Player c) {
        if (Server.getConfiguration().getServerState().isOpenSpawning()) {
            testingStarter(c);
        } else {
            standardStarter(c);
        }
    }

    private static void standardStarter(Player c) {
        c.getInventory().addAnywhere(new ImmutableItem(2841, 1));//double exp scroll
        c.getInventory().addAnywhere(new ImmutableItem(995, 300000));//coins
        c.getInventory().addAnywhere(new ImmutableItem(2528, 1));
        c.getInventory().addAnywhere(new ImmutableItem(11739, 1));//vote mystery box
        c.getInventory().addAnywhere(new ImmutableItem(8013, 50));//teleport to house
        c.getInventory().addAnywhere(new ImmutableItem(380, 100));//noted lobster
        c.getInventory().addAnywhere(new ImmutableItem(386, 100));//noted shark
        c.getInventory().addAnywhere(new ImmutableItem(1323, 1));//iron scimi
        c.getInventory().addAnywhere(new ImmutableItem(1333, 1));//rune scimi
        c.getInventory().addAnywhere(new ImmutableItem(1153, 1));//iron helm
        c.getInventory().addAnywhere(new ImmutableItem(1115, 1));//iron plate
        c.getInventory().addAnywhere(new ImmutableItem(1067, 1));//iron legs
        //c.getInventory().addAnywhere(new ImmutableItem(3842, 1));//unholy book
        c.getInventory().addAnywhere(new ImmutableItem(579, 1));//wizard hat
        c.getInventory().addAnywhere(new ImmutableItem(577, 1));//wizard top
        c.getInventory().addAnywhere(new ImmutableItem(1011, 1));//wiz robe
        c.getInventory().addAnywhere(new ImmutableItem(1381, 1));//staff of air
        c.getInventory().addAnywhere(new ImmutableItem(7458, 1));//mith gloves
        c.getInventory().addAnywhere(new ImmutableItem(1169, 1));//coif
        c.getInventory().addAnywhere(new ImmutableItem(1129, 1));//body
        c.getInventory().addAnywhere(new ImmutableItem(1095, 1));//chaps
        c.getInventory().addAnywhere(new ImmutableItem(863, 100));//iron knife
        c.getInventory().addAnywhere(new ImmutableItem(868, 100));//rune knife
        c.getInventory().addAnywhere(new ImmutableItem(558, 100));//mind runes
        c.getInventory().addAnywhere(new ImmutableItem(562, 100));//chaos runes
    }

    public static void testingStarter(Player player) {
        player.sendMessage("Your bank has been stocked with high-level equipment.");
        int consumables = 500_000;
        int equipment = 15;

        int mainTab = 0;
        int meleeTab = 1;
        int rangedTab = 2;
        int magicTab = 3;
        int skillingTab = 4;

        int tab = mainTab;

        // Coins
        player.getItems().sendToTab(tab, Items.COINS, 600_000_000);

        // Food consumables
        player.getItems().sendToTab(tab, Items.ANGLERFISH, consumables);
        player.getItems().sendToTab(tab, Items.COOKED_KARAMBWAN, consumables);
        player.getItems().sendToTab(tab, Items.DIVINE_SUPER_COMBAT_POTION4, consumables);
        player.getItems().sendToTab(tab, Items.SUPER_COMBAT_POTION4, equipment);
        player.getItems().sendToTab(tab, Items.DIVINE_RANGING_POTION4, consumables);
        player.getItems().sendToTab(tab, Items.RANGING_POTION4, equipment);
        player.getItems().sendToTab(tab, Items.DIVINE_MAGIC_POTION4, consumables);
        player.getItems().sendToTab(tab, Items.MAGIC_POTION4, equipment);
        player.getItems().sendToTab(tab, Items.SUPER_RESTORE4, consumables);
        player.getItems().sendToTab(tab, Items.ANTI_VENOMPLUS4, consumables);
        player.getItems().sendToTab(tab, Items.ANTIDOTEPLUSPLUS4, consumables);
        player.getItems().sendToTab(tab, Items.SARADOMIN_BREW4, consumables);
        
        // Combat consumables
        player.getItems().sendToTab(tab, Items.BRACELET_OF_ETHEREUM_UNCHARGED, consumables);
        player.getItems().sendToTab(tab, Items.REVENANT_ETHER, consumables);
        player.getItems().sendToTab(tab, Items.ECUMENICAL_KEY, consumables);
        player.getItems().sendToTab(tab, Items.ZULRAHS_SCALES, consumables);
        player.getItems().sendToTab(tab, Items.RING_OF_RECOIL, consumables);

        // Jewellry
        player.getItems().sendToTab(tab, Items.AMULET_OF_GLORY4, equipment);
        player.getItems().sendToTab(tab, Items.AMULET_OF_FURY, equipment);
        player.getItems().sendToTab(tab, Items.REGEN_BRACELET, equipment);
        player.getItems().sendToTab(tab, Items.RING_OF_SUFFERING, equipment);
        player.getItems().sendToTab(tab, Items.RING_OF_SUFFERING_I, equipment);
        player.getItems().sendToTab(tab, Items.AMULET_OF_THE_DAMNED, equipment);
        
        // Misc equipment
        player.getItems().sendToTab(tab, Items.MYTHICAL_CAPE, equipment);
        player.getItems().sendToTab(tab, Items.ARDOUGNE_CLOAK_4, equipment);
        player.getItems().sendToTab(tab, Items.FIRE_CAPE, equipment);
        player.getItems().sendToTab(tab, Items.INFERNAL_CAPE, equipment);
        player.getItems().sendToTab(tab, Items.OBSIDIAN_CAPE, equipment);
        player.getItems().sendToTab(tab, Items.MITHRIL_GLOVES, equipment);
        player.getItems().sendToTab(tab, Items.BARROWS_GLOVES, equipment);

        // Spirit shields
        player.getItems().sendToTab(tab, Items.SPIRIT_SHIELD, equipment);
        player.getItems().sendToTab(tab, Items.BLESSED_SPIRIT_SHIELD, equipment);
        player.getItems().sendToTab(tab, Items.ARCANE_SPIRIT_SHIELD, equipment);
        player.getItems().sendToTab(tab, Items.ELYSIAN_SPIRIT_SHIELD, equipment);
        player.getItems().sendToTab(tab, Items.SPECTRAL_SPIRIT_SHIELD, equipment);

        // Void
        player.getItems().sendToTab(tab, Items.VOID_MELEE_HELM, equipment);
        player.getItems().sendToTab(tab, Items.VOID_MAGE_HELM, equipment);
        player.getItems().sendToTab(tab, Items.VOID_RANGER_HELM, equipment);
        player.getItems().sendToTab(tab, Items.VOID_KNIGHT_TOP, equipment);
        player.getItems().sendToTab(tab, Items.VOID_KNIGHT_ROBE, equipment);
        player.getItems().sendToTab(tab, Items.VOID_KNIGHT_GLOVES, equipment);



        tab = rangedTab;

        // Ranged equipment
        player.getItems().sendToTab(tab, Items.PEGASIAN_BOOTS, equipment);
        player.getItems().sendToTab(tab, Items.NECKLACE_OF_ANGUISH, equipment);
        player.getItems().sendToTab(tab, Items.ARCHERS_RING_I, equipment);
        player.getItems().sendToTab(tab, Items.BLACK_DHIDE_BODY, equipment);
        player.getItems().sendToTab(tab, Items.BLACK_DHIDE_CHAPS, equipment);
        player.getItems().sendToTab(tab, Items.BLACK_DHIDE_VAMB, equipment);
        player.getItems().sendToTab(tab, Items.AVAS_ACCUMULATOR, equipment);
        player.getItems().sendToTab(tab, Items.AVAS_ASSEMBLER, equipment);
        player.getItems().sendToTab(tab, Items.ODIUM_WARD, equipment);
        player.getItems().sendToTab(tab, Items.DRAGONFIRE_WARD, equipment);
        player.getItems().sendToTab(tab, Items.TWISTED_BUCKLER, equipment);
        player.getItems().sendToTab(tab, Items.BLACK_DHIDE_SHIELD, equipment);
        player.getItems().sendToTab(tab, Items.ARMADYL_HELMET, equipment);
        player.getItems().sendToTab(tab, Items.ARMADYL_CHESTPLATE, equipment);
        player.getItems().sendToTab(tab, Items.ARMADYL_CHAINSKIRT, equipment);
        player.getItems().sendToTab(tab, Items.KARILS_LEATHERTOP, equipment);
        player.getItems().sendToTab(tab, Items.KARILS_LEATHERSKIRT, equipment);
        player.getItems().sendToTab(tab, Items.KARILS_ARMOUR_SET, equipment);


        // Ranged weapons
        player.getItems().sendToTab(tab, Items.ADAMANT_JAVELIN, consumables);
       player.getItems().sendToTab(tab, Items.RUNE_JAVELIN, consumables);
       player.getItems().sendToTab(tab, Items.TOKTZ_XIL_UL, equipment);
       player.getItems().sendToTab(tab, Items.RUNE_DART, consumables);
       player.getItems().sendToTab(tab, Items.ADAMANT_DART, consumables);
        player.getItems().sendToTab(tab, Items.DRAGON_ARROW, consumables);
       player.getItems().sendToTab(tab, Items.AMETHYST_ARROW, consumables);
       player.getItems().sendToTab(tab, Items.RUNE_ARROW, consumables);
       player.getItems().sendToTab(tab, Items.BRONZE_ARROW, consumables);
       player.getItems().sendToTab(tab, Items.DRAGONSTONE_BOLTS_E, consumables);
       player.getItems().sendToTab(tab, Items.RUBY_BOLTS_E, consumables);
       player.getItems().sendToTab(tab, Items.ONYX_BOLTS_E, consumables);
       player.getItems().sendToTab(tab, Items.BOLT_RACK, consumables);
       player.getItems().sendToTab(tab, Items.DRAGON_JAVELIN, consumables);
       player.getItems().sendToTab(tab, Items.DRAGON_KNIFE, consumables);
       player.getItems().sendToTab(tab, Items.DRAGON_DART, consumables);
       player.getItems().sendToTab(tab, Items.DRAGON_THROWNAXE, consumables);
       player.getItems().sendToTab(tab, Items.MORRIGANS_THROWING_AXE, consumables);
       player.getItems().sendToTab(tab, Items.CHINCHOMPA, consumables);
       player.getItems().sendToTab(tab, Items.RED_CHINCHOMPA, consumables);
       player.getItems().sendToTab(tab, Items.BLACK_CHINCHOMPA, consumables);
        
       player.getItems().sendToTab(tab, Items.CRAWS_BOW, equipment);
       player.getItems().sendToTab(tab, Items.SHORTBOW, equipment);
       player.getItems().sendToTab(tab, Items.MAGIC_SHORTBOW, equipment);
       player.getItems().sendToTab(tab, Items.MAGIC_SHORTBOW_I, equipment);
       player.getItems().sendToTab(tab, Items.DARK_BOW, equipment);
       player.getItems().sendToTab(tab, Items.CRYSTAL_BOW, equipment);
       player.getItems().sendToTab(tab, Items.TWISTED_BOW, equipment);
       player.getItems().sendToTab(tab, Items.TOXIC_BLOWPIPE, equipment);
       player.getItems().sendToTab(tab, Items.ARMADYL_CROSSBOW, equipment);
       player.getItems().sendToTab(tab, Items.DRAGON_HUNTER_CROSSBOW, equipment);
       player.getItems().sendToTab(tab, Items.DRAGON_CROSSBOW, equipment);
       player.getItems().sendToTab(tab, Items.LIGHT_BALLISTA, equipment);
       player.getItems().sendToTab(tab, Items.HEAVY_BALLISTA, equipment);

        tab = meleeTab;

        // Melee equipment
        player.getItems().sendToTab(tab, Items.BERSERKER_RING_I, equipment);
        player.getItems().sendToTab(tab, Items.TREASONOUS_RING_I, equipment);
        player.getItems().sendToTab(tab, Items.TYRANNICAL_RING_I, equipment);
        player.getItems().sendToTab(tab, Items.CLIMBING_BOOTS, equipment);
        player.getItems().sendToTab(tab, Items.GUARDIAN_BOOTS, equipment);
        player.getItems().sendToTab(tab, Items.PRIMORDIAL_BOOTS, equipment);
        player.getItems().sendToTab(tab, Items.FEROCIOUS_GLOVES, equipment);
        player.getItems().sendToTab(tab, Items.BERSERKER_NECKLACE, equipment);
        player.getItems().sendToTab(tab, Items.AMULET_OF_TORTURE, equipment);
        player.getItems().sendToTab(tab, Items.DINHS_BULWARK, equipment);
        player.getItems().sendToTab(tab, Items.BOOTS_OF_BRIMSTONE, equipment);
        player.getItems().sendToTab(tab, Items.RUNE_DEFENDER, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_DEFENDER, equipment);
        player.getItems().sendToTab(tab, Items.AVERNIC_DEFENDER, equipment);
        player.getItems().sendToTab(tab, Items.RUNE_BOOTS, equipment);
        player.getItems().sendToTab(tab, Items.RUNE_FULL_HELM, equipment);
        player.getItems().sendToTab(tab, Items.RUNE_PLATEBODY, equipment);
        player.getItems().sendToTab(tab, Items.RUNE_PLATELEGS, equipment);
        player.getItems().sendToTab(tab, Items.WARRIOR_HELM, equipment);
        player.getItems().sendToTab(tab, Items.BERSERKER_HELM, equipment);
        player.getItems().sendToTab(tab, Items.HELM_OF_NEITIZNOT, equipment);
        player.getItems().sendToTab(tab, Items.NEITIZNOT_FACEGUARD, equipment);
        player.getItems().sendToTab(tab, Items.FIGHTER_HAT, equipment);
        player.getItems().sendToTab(tab, Items.FIGHTER_TORSO, equipment);
        player.getItems().sendToTab(tab, Items.SERPENTINE_HELM, equipment);
        player.getItems().sendToTab(tab, Items.OBSIDIAN_HELMET, equipment);
        player.getItems().sendToTab(tab, Items.OBSIDIAN_PLATEBODY, equipment);
        player.getItems().sendToTab(tab, Items.OBSIDIAN_PLATELEGS, equipment);
        player.getItems().sendToTab(tab, Items.TOKTZ_KET_XIL, equipment);
        player.getItems().sendToTab(tab, Items.CRYSTAL_SHIELD, equipment);
        player.getItems().sendToTab(tab, Items.DRAGONFIRE_SHIELD, equipment);

        player.getItems().sendToTab(tab, Items.DRAGON_FULL_HELM, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_MED_HELM, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_CHAINBODY, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_PLATEBODY, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_PLATELEGS, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_BOOTS, equipment);

        player.getItems().sendToTab(tab, Items.BANDOS_CHESTPLATE, equipment);
        player.getItems().sendToTab(tab, Items.BANDOS_TASSETS, equipment);
        player.getItems().sendToTab(tab, Items.BANDOS_BOOTS, equipment);

        player.getItems().sendToTab(tab, Items.DHAROKS_HELM, equipment);
        player.getItems().sendToTab(tab, Items.DHAROKS_PLATEBODY, equipment);
        player.getItems().sendToTab(tab, Items.DHAROKS_PLATELEGS, equipment);
        player.getItems().sendToTab(tab, Items.DHAROKS_GREATAXE, equipment);

        player.getItems().sendToTab(tab, Items.JUSTICIAR_FACEGUARD, equipment);
        player.getItems().sendToTab(tab, Items.JUSTICIAR_CHESTGUARD, equipment);
        player.getItems().sendToTab(tab, Items.JUSTICIAR_LEGGUARDS, equipment);

        player.getItems().sendToTab(tab, Items.INQUISITORS_GREAT_HELM, equipment);
        player.getItems().sendToTab(tab, Items.INQUISITORS_HAUBERK, equipment);
        player.getItems().sendToTab(tab, Items.INQUISITORS_PLATESKIRT, equipment);

        player.getItems().sendToTab(tab, Items.TORAGS_ARMOUR_SET, equipment);
        player.getItems().sendToTab(tab, Items.DHAROKS_ARMOUR_SET, equipment);
        player.getItems().sendToTab(tab, Items.GUTHANS_ARMOUR_SET, equipment);


        // Melee weapons
        player.getItems().sendToTab(tab, Items.VIGGORAS_CHAINMACE, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_SPEAR, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_SCIMITAR, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_LONGSWORD, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_DAGGERPPLUSPLUS, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_BATTLEAXE, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_2H_SWORD, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_SWORD, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_MACE, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_WARHAMMER, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_CLAWS, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_HALBERD, equipment);
        player.getItems().sendToTab(tab, Items.CRYSTAL_HALBERD, equipment);

        player.getItems().sendToTab(tab, Items.GRANITE_MAUL, equipment);
        player.getItems().sendToTab(tab, Items.GRANITE_MAUL_OR, equipment);
        player.getItems().sendToTab(tab, Items.ABYSSAL_BLUDGEON, equipment);
        player.getItems().sendToTab(tab, Items.ABYSSAL_WHIP, equipment);
        player.getItems().sendToTab(tab, Items.ABYSSAL_TENTACLE, equipment);
        player.getItems().sendToTab(tab, Items.VESTAS_SPEAR, equipment);
        player.getItems().sendToTab(tab, Items.VESTAS_LONGSWORD, equipment);
        player.getItems().sendToTab(tab, Items.STATIUSS_WARHAMMER, equipment);
        player.getItems().sendToTab(tab, Items.ZAMORAKIAN_SPEAR, equipment);
        player.getItems().sendToTab(tab, Items.ZAMORAKIAN_HASTA, equipment);
        player.getItems().sendToTab(tab, Items.DRAGON_HUNTER_LANCE, equipment);

        player.getItems().sendToTab(tab, Items.ELDER_MAUL, equipment);
        player.getItems().sendToTab(tab, Items.INQUISITORS_MACE, equipment);
        player.getItems().sendToTab(tab, Items.GHRAZI_RAPIER, equipment);

        player.getItems().sendToTab(tab, Items.SCYTHE_OF_VITUR, equipment);
        player.getItems().sendToTab(tab, Items.ARMADYL_GODSWORD, equipment);
        player.getItems().sendToTab(tab, Items.SARADOMIN_GODSWORD, equipment);
        player.getItems().sendToTab(tab, Items.ZAMORAK_GODSWORD, equipment);
        player.getItems().sendToTab(tab, Items.BANDOS_GODSWORD, equipment);
        player.getItems().sendToTab(tab, Items.SARADOMIN_SWORD, equipment);
        player.getItems().sendToTab(tab, Items.SARADOMINS_BLESSED_SWORD, equipment);
        player.getItems().sendToTab(tab, Items.BLADE_OF_SAELDOR, equipment);

        player.getItems().sendToTab(tab, Items.ARCLIGHT, equipment);
        player.getItems().sendToTab(tab, Items.DARKLIGHT, equipment);
        player.getItems().sendToTab(tab, Items.EXCALIBUR, equipment);

        player.getItems().sendToTab(tab, Items.TOKTZ_XIL_EK, equipment);
        player.getItems().sendToTab(tab, Items.TOKTZ_XIL_AK, equipment);
        player.getItems().sendToTab(tab, Items.TZHAAR_KET_OM, equipment);
        player.getItems().sendToTab(tab, Items.TZHAAR_KET_EM, equipment);
        player.getItems().sendToTab(tab, Items.TOKTZ_MEJ_TAL, equipment);

        tab = magicTab;

        // Magic equipment
        player.getItems().sendToTab(tab, Items.RUNE_POUCH, equipment);
        player.getItems().sendToTab(tab, Items.IMBUED_HEART, equipment);
        player.getItems().sendToTab(tab, Items.ETERNAL_BOOTS, equipment);
        player.getItems().sendToTab(tab, Items.TORMENTED_BRACELET, equipment);
        player.getItems().sendToTab(tab, Items.SEERS_RING_I, equipment);
        player.getItems().sendToTab(tab, Items.FARSEER_HELM, equipment);
        player.getItems().sendToTab(tab, Items.TOME_OF_FIRE, equipment);
        player.getItems().sendToTab(tab, Items.BURNT_PAGE, consumables);
        player.getItems().sendToTab(tab, Items.SARADOMIN_CAPE, equipment);
        player.getItems().sendToTab(tab, Items.IMBUED_SARADOMIN_CAPE, equipment);
        player.getItems().sendToTab(tab, Items.MALEDICTION_WARD, equipment);
        player.getItems().sendToTab(tab, Items.OCCULT_NECKLACE, equipment);
        player.getItems().sendToTab(tab, Items.THIRD_AGE_AMULET, equipment);
        player.getItems().sendToTab(tab, Items.ANCIENT_WYVERN_SHIELD, equipment);
        player.getItems().sendToTab(tab, Items.ETERNAL_BOOTS, equipment);
        player.getItems().sendToTab(tab, Items.MYSTIC_HAT, equipment);
        player.getItems().sendToTab(tab, Items.MYSTIC_ROBE_TOP, equipment);
        player.getItems().sendToTab(tab, Items.MYSTIC_ROBE_BOTTOM, equipment);
        player.getItems().sendToTab(tab, Items.MYSTIC_GLOVES, equipment);
        player.getItems().sendToTab(tab, Items.MYSTIC_BOOTS, equipment);
        player.getItems().sendToTab(tab, Items.AHRIMS_HOOD, equipment);
        player.getItems().sendToTab(tab, Items.AHRIMS_ROBETOP, equipment);
        player.getItems().sendToTab(tab, Items.AHRIMS_ROBESKIRT, equipment);
        player.getItems().sendToTab(tab, Items.ANCESTRAL_HAT, equipment);
        player.getItems().sendToTab(tab, Items.ANCESTRAL_ROBE_TOP, equipment);
        player.getItems().sendToTab(tab, Items.ANCESTRAL_ROBE_BOTTOM, equipment);
        player.getItems().sendToTab(tab, Items.AHRIMS_ARMOUR_SET, equipment);

        // Magic weapons
        for (int i = 554; i <= 566; i++) {
            player.getItems().sendToTab(tab, i, consumables);
        }
        player.getItems().sendToTab(tab, 9075, consumables);
        player.getItems().sendToTab(tab, Items.WRATH_RUNE, consumables);

        player.getItems().sendToTab(tab, Items.THAMMARONS_SCEPTRE, equipment);
        player.getItems().sendToTab(tab, Items.SANGUINESTI_STAFF, equipment);
        player.getItems().sendToTab(tab, Items.MYSTIC_AIR_STAFF, equipment);
        player.getItems().sendToTab(tab, Items.MYSTIC_WATER_STAFF, equipment);
        player.getItems().sendToTab(tab, Items.MYSTIC_EARTH_STAFF, equipment);
        player.getItems().sendToTab(tab, Items.MYSTIC_FIRE_STAFF, equipment);
        player.getItems().sendToTab(tab, Items.ZAMORAK_STAFF, equipment);
        player.getItems().sendToTab(tab, Items.SARADOMIN_STAFF, equipment);
        player.getItems().sendToTab(tab, Items.GUTHIX_STAFF, equipment);
        player.getItems().sendToTab(tab, Items.IBANS_STAFF, equipment);
        player.getItems().sendToTab(tab, Items.ANCIENT_STAFF, equipment);
        player.getItems().sendToTab(tab, Items.MASTER_WAND, equipment);
        player.getItems().sendToTab(tab, Items.KODAI_WAND, equipment);
        player.getItems().sendToTab(tab, Items.TRIDENT_OF_THE_SEAS, equipment);
        player.getItems().sendToTab(tab, Items.TRIDENT_OF_THE_SWAMP, equipment);
        player.getItems().sendToTab(tab, Items.STAFF_OF_LIGHT, equipment);
        player.getItems().sendToTab(tab, Items.NIGHTMARE_STAFF, equipment);
        player.getItems().sendToTab(tab, Items.ELDRITCH_NIGHTMARE_STAFF, equipment);
        player.getItems().sendToTab(tab, Items.HARMONISED_NIGHTMARE_STAFF, equipment);
        player.getItems().sendToTab(tab, Items.VOLATILE_NIGHTMARE_STAFF, equipment);
        player.getItems().sendToTab(tab, Items.SLAYERS_STAFF, equipment);
        player.getItems().sendToTab(tab, Items.SLAYERS_STAFF_E, equipment);
        player.getItems().sendToTab(tab, Items.STAFF_OF_THE_DEAD, equipment);
        player.getItems().sendToTab(tab, Items.TOXIC_STAFF_OF_THE_DEAD, equipment);

        // Skilling tab
        tab = skillingTab;
        player.getItems().sendToTab(tab, Items.PURE_ESSENCE, consumables);

        // Other settings
        player.setSangStaffCharge(20_000);
        player.setToxicBlowpipeAmmoAmount(20_000);
        player.setToxicBlowpipeAmmo(Items.DRAGON_DART);
        player.setToxicTridentCharge(20_000);
        player.setTridentCharge(20_000);

        PvpWeapons.giveStartingCharges(player);
    }

}
