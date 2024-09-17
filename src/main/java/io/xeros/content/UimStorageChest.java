package io.xeros.content;

import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;

public class UimStorageChest {



    public static boolean isStorageItem(Player c, int itemId) {
        switch (itemId) {
            //graceful
            case Items.GRACEFUL_BOOTS:
            case Items.GRACEFUL_CAPE:
            case Items.GRACEFUL_GLOVES:
            case Items.GRACEFUL_HOOD:
            case Items.GRACEFUL_LEGS:
            case Items.GRACEFUL_TOP:
                //99 capes
            case Items.AGILITY_CAPET:
            case Items.CONSTRUCT_CAPET:
            case Items.COOKING_CAPET:
            case Items.ATTACK_CAPET:
            case Items.CRAFTING_CAPET:
            case Items.DEFENCE_CAPET:
            case Items.FARMING_CAPET:
            case Items.FIREMAKING_CAPET:
            case Items.FISHING_CAPET:
            case Items.FLETCHING_CAPET:
            case Items.HERBLORE_CAPET:
            case Items.HITPOINTS_CAPET:
            case Items.HUNTER_CAPET:
            case Items.MAGIC_CAPET:
            case Items.MINING_CAPET:
            case Items.MUSIC_CAPET:
            case Items.PRAYER_CAPET:
            case Items.RANGING_CAPET:
            case Items.RUNECRAFT_CAPET:
            case Items.SLAYER_CAPET:
            case Items.SMITHING_CAPET:
            case Items.STRENGTH_CAPET:
            case Items.WOODCUT_CAPET:
            case Items.THIEVING_CAPET:
            case Items.COMPLETIONIST_CAPE:
            case Items.MAX_CAPE:
            case Items.MAX_HOOD:
            case Items.ACCUMULATOR_MAX_CAPE:
            case Items.ACCUMULATOR_MAX_HOOD:
            case Items.ARDOUGNE_MAX_CAPE:
            case Items.ARDOUGNE_MAX_HOOD:
            case Items.FIRE_MAX_CAPE:
            case Items.FIRE_MAX_HOOD:
            case Items.INFERNAL_MAX_CAPE:
            case Items.INFERNAL_MAX_HOOD:
            case Items.GUTHIX_MAX_CAPE:
            case Items.GUTHIX_MAX_HOOD:
            case Items.SARADOMIN_MAX_CAPE:
            case Items.SARADOMIN_MAX_HOOD:
            case Items.ZAMORAK_MAX_CAPE:
            case Items.ZAMORAK_MAX_HOOD:
            case Items.IMBUED_GUTHIX_MAX_HOOD:
            case Items.IMBUED_GUTHIX_MAX_CAPE:
            case Items.IMBUED_SARADOMIN_MAX_CAPE:
            case Items.IMBUED_SARADOMIN_MAX_HOOD:
            case Items.IMBUED_ZAMORAK_MAX_CAPE:
            case Items.IMBUED_ZAMORAK_MAX_HOOD:
            case Items.ASSEMBLER_MAX_CAPE:
            case Items.ASSEMBLER_MAX_HOOD:
                //slayer helms
            case Items.SLAYER_HELMET:
            case Items.SLAYER_HELMET_I:
            case Items.BLACK_SLAYER_HELMET_I:
            case Items.BLACK_SLAYER_HELMET:
            case Items.GREEN_SLAYER_HELMET:
            case Items.GREEN_SLAYER_HELMET_I:
            case Items.RED_SLAYER_HELMET_I:
            case Items.RED_SLAYER_HELMET:
            case Items.PURPLE_SLAYER_HELMET:
            case Items.PURPLE_SLAYER_HELMET_I:
            case Items.TURQUOISE_SLAYER_HELMET:
            case Items.TURQUOISE_SLAYER_HELMET_I:
            case Items.HYDRA_SLAYER_HELMET:
            case Items.HYDRA_SLAYER_HELMET_I:
            case Items.TWISTED_SLAYER_HELMET:
            case Items.TWISTED_SLAYER_HELMET_I:
                //farming items
            case Items.MAGIC_SECATEURS:
            case Items.MAGIC_SECATEURS_NZ:
            //cannon
            case Items.CANNON_BASE:
            case Items.CANNON_BARRELS:
            case Items.CANNON_FURNACE:
            case Items.CANNON_STAND:
                //herblore
            case Items.HERB_SACK:
                //rfd gloves
            case Items.BARROWS_GLOVES:
            case Items.RUNE_GLOVES:
            case Items.MITHRIL_GLOVES:









                //VOID
            case Items.VOID_KNIGHT_GLOVES:
            case Items.VOID_KNIGHT_MACE:
            case Items.VOID_KNIGHT_ROBE:
            case Items.VOID_KNIGHT_TOP:
            case Items.VOID_MAGE_HELM:
            case Items.VOID_MELEE_HELM:
            case Items.VOID_RANGER_HELM:
            case Items.ELITE_VOID_ROBE:
            case Items.ELITE_VOID_TOP:
            //ROGUE EQUIPMENT
            case Items.ROGUE_BOOTS:
            case Items.ROGUE_GLOVES:
            case Items.ROGUE_MASK:
            case Items.ROGUE_TOP:
            case Items.ROGUE_TROUSERS:
            //PROSELYTE ARMOUR
            case Items.PROSELYTE_CUISSE:
            case Items.PROSELYTE_HAUBERK:
            case Items.PROSELYTE_TASSET:
            case Items.PROSELYTE_SALLET:
            //MOURNER GEAR
            case Items.MOURNER_BOOTS:
            case Items.MOURNER_CLOAK:
            case Items.MOURNER_GLOVES:
            case Items.MOURNER_TOP:
            case Items.MOURNER_TROUSERS:
            case Items.GAS_MASK:
            //LUMBERJACK
            case Items.LUMBERJACK_BOOTS:
            case Items.LUMBERJACK_HAT:
            case Items.LUMBERJACK_LEGS:
            case Items.LUMBERJACK_TOP:
            //PROSPECTOR
            case Items.PROSPECTOR_BOOTS:
            case Items.PROSPECTOR_HELMET:
            case Items.PROSPECTOR_JACKET:
            case Items.PROSPECTOR_LEGS:
            //ANGLER
            case Items.ANGLER_BOOTS:
            case Items.ANGLER_HAT:
            case Items.ANGLER_TOP:
            case Items.ANGLER_WADERS:
            //SHAYZIEN
            case Items.SHAYZIEN_GLOVES_5:
            case Items.SHAYZIEN_BOOTS_5:
            case Items.SHAYZIEN_GREAVES_5:
            case Items.SHAYZIEN_HELM_5:
            case Items.SHAYZIEN_PLATEBODY_5:
            //FARMERS OUTFIT
            case Items.FARMERS_BOOTS:
            case Items.FARMERS_BORO_TROUSERS:
            case Items.FARMERS_FORK:
            case Items.FARMERS_JACKET:
            case Items.FARMERS_SHIRT:
            case Items.FARMERS_STRAWHAT:
            case Items.FARMERS_STRAWHAT_2:
            case Items.FARMERS_BORO_TROUSERS_2:
            //OBSIDIAN_ARMOUR
            case Items.OBSIDIAN_PLATEBODY:
            case Items.OBSIDIAN_PLATELEGS:
            case Items.OBSIDIAN_HELMET:
            //FIGHTER TORSO
            case Items.FIGHTER_TORSO:
            //JUSTICIAR_ARMOUR
            case Items.JUSTICIAR_CHESTGUARD:
            case Items.JUSTICIAR_FACEGUARD:
            case Items.JUSTICIAR_LEGGUARDS:
            //INQUISITORS ARMOUR
            case Items.INQUISITORS_GREAT_HELM:
            case Items.INQUISITORS_PLATESKIRT:
            case Items.INQUISITORS_HAUBERK:
            //PYROMANCER
            case Items.PYROMANCER_BOOTS:
            case Items.PYROMANCER_GARB:
            case Items.PYROMANCER_HOOD:
            case Items.PYROMANCER_ROBE:
            //MYSTIC ROBES
            case Items.MYSTIC_BOOTS:
            case Items.MYSTIC_BOOTS_DARK:
            case Items.MYSTIC_BOOTS_DUSK:
            case Items.MYSTIC_BOOTS_LIGHT:
            case Items.MYSTIC_GLOVES:
            case Items.MYSTIC_GLOVES_DARK:
            case Items.MYSTIC_GLOVES_DUSK:
            case Items.MYSTIC_GLOVES_LIGHT:
            case Items.MYSTIC_HAT:
            case Items.MYSTIC_HAT_DARK:
            case Items.MYSTIC_HAT_DUSK:
            case Items.MYSTIC_HAT_LIGHT:
            case Items.MYSTIC_ROBE_BOTTOM:
            case Items.MYSTIC_ROBE_BOTTOM_DARK:
            case Items.MYSTIC_ROBE_BOTTOM_DUSK:
            case Items.MYSTIC_ROBE_BOTTOM_LIGHT:
            //INFINITY ROBES
            case Items.INFINITY_BOOTS:
            case Items.INFINITY_BOTTOMS:
            case Items.INFINITY_GLOVES:
            case Items.INFINITY_HAT:
            case Items.INFINITY_TOP:
            case Items.DARK_INFINITY_BOTTOMS:
            case Items.DARK_INFINITY_HAT:
            case Items.DARK_INFINITY_TOP:
            //ANCESTRAL ROBES
            case Items.ANCESTRAL_HAT:
            case Items.ANCESTRAL_ROBE_TOP:
            case Items.ANCESTRAL_ROBE_BOTTOM:
            //FOE PETS
            case 30010://postie pete
            case 30012://toucan
            case 30011://imp
            case 30013://penguin king
            case 30014://klik
            case 30015://melee pet
            case 30016://range pet
            case 30017://magic pet
            case 30018://healer
            case 30019://prayer
            case 30020://corrupt beast
            case 30021://roc pet
            case 30022://yama pet
            case 23939://seren
                //dark versions
            case 30110://postie pete
            case 30112://toucan
            case 30111://imp
            case 30113://penguin king
            case 30114://klik
            case 30115://melee pet
            case 30116://range pet
            case 30117://magic pet
            case 30118://healer
            case 30119://prayer
            case 30120://corrupt beast
            case 30121://roc pet
            case 30122://yama pet
            case 30123://seren
            //skilling pets
            case 13320:
            case 13321:
            case 21187:
            case 21188:
            case 21189:
            case 21192:
            case 21193:
            case 21194:
            case 21196:
            case 21197:
            case 13322:
            case 13323:
            case 13324:
            case 13325:
            case 13326:
            case 20659:
            case 20661:
            case 20663:
            case 20665:
            case 20667:
            case 20669:
            case 20671:
            case 20673:
            case 20675:
            case 20677:
            case 20679:
            case 20681:
            case 20683:
            case 20685:
            case 20687:
            case 20689:
            case 20691:
            case 20693:
            case 19557:
            //boss pets
            case 12650:
            case 12649:
            case 12651:
            case 12652:
            case 12644:
            case 12645:
            case 12643:
            case 11995:
            case 12653:
            case 12655:
            case 13178:
            case 12646:
            case 13179:
            case 13180:
            case 13177:
            case 12648:
            case 13225:
            case 13247:
            case 21273:
            case 12921:
            case 12939:
            case 12940:
            case 21992:
            case 13181:
            case 12816:
            case 12654:
            case 22318:
            case 12647:
            case 13262:
            case 19730:
            case 22376:
            case 22378:
            case 22380:
            case 22382:
            case 22384:
            case 20851:
            case 22473:
            case 21291:
            case 22319:
            case 22746:
            case 22748:
            case 22750:
            case 22752:
            case 23760:
            case 23757:
            case 23759:
            case 24491:
                return true;
            default:
                c.sendMessage("@red@Your game mode cannot store: @blu@" + ItemAssistant.getItemName(itemId));
                return false;
        }
    }
}
