package io.xeros.content.combat.melee;

import dev.openrune.cache.CacheManager;
import io.xeros.content.combat.WeaponAnimation;
import io.xeros.content.combat.magic.CombatSpellData;
import io.xeros.content.combat.weapon.AttackStyle;
import io.xeros.content.combat.weapon.CombatStyle;
import io.xeros.model.CombatType;
import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;
import it.unimi.dsi.fastutil.ints.*;

import java.util.Objects;

import static io.xeros.model.Items.*;

public class MeleeData { //TODO change this to load from json or txt
    private final Int2ObjectMap<int[]> renderMap = new Int2ObjectOpenHashMap<>();
    public static boolean usingSytheOfVitur(Player player) {
        return player.attacking.getCombatType() == CombatType.MELEE && player.getItems().isWearingItem(Items.SCYTHE_OF_VITUR);
    }

    public static boolean usingHally(Player c) {
        switch (c.playerEquipment[Player.playerWeapon]) {
            case 3190:
            case 3192:
            case 3194:
            case 3196:
            case 3198:
            case 2054:
            case 3202:
            case 3204:
            case 13092:
                return true;

            default:
                return false;
        }
    }

    public static void setWeaponAnimations(Player player) {
        player.playerStandIndex = 0x328;
        player.playerTurnIndex = 0x337;
        player.playerWalkIndex = 0x333;
        player.playerTurn180Index = 0x334;
        player.playerTurn90CWIndex = 0x335;
        player.playerTurn90CCWIndex = 0x336;
        player.playerRunIndex = 0x338;


        int weaponId = player.playerEquipment[Player.playerWeapon];
        if (weaponId == -1) return;
        String name = CacheManager.INSTANCE.getItem(weaponId).getName();
        String weaponName = name.toLowerCase();

        if (weaponName.contains("c'bow")) {
            player.playerStandIndex = 4591;
            player.playerWalkIndex = 4226;
            player.playerRunIndex = 4228;
            return;
        }

        if (weaponName.contains("hunting knife")) {
            player.playerStandIndex = 7329;
            player.playerWalkIndex = 7327;
            player.playerRunIndex = 7327;
            return;
        }

        if (player.isWearingWeapon(27275)) {
            player.playerStandIndex = 1713;
            player.playerWalkIndex = 1703;
            player.playerRunIndex = 1707; //1707
            player.playerTurnIndex = 1713;
            player.playerTurn180Index = 1705;
            player.playerTurn90CWIndex = 1706;  //1702
            player.playerTurn90CCWIndex = 1702;
            return;
        }

        if (player.isWearingWeapon(22324)) {
            player.playerStandIndex = 809;
            player.playerWalkIndex = 823;
            player.playerRunIndex = 824;
            player.playerTurnIndex = 820;
            player.playerTurn180Index = 821;
            player.playerTurn90CWIndex = 822;
            player.playerTurn90CCWIndex = 819;
            return;
        }

        if (weaponName.contains("bulwark")) {
            player.playerStandIndex = 7508;
            player.playerWalkIndex = 7510;
            player.playerRunIndex = 7509;
            return;
        }

        if (weaponName.contains("elder maul")) {
            player.playerStandIndex = 7518;
            player.playerWalkIndex = 7520;
            player.playerRunIndex = 7519;
            return;
        }

        if (weaponName.contains("ballista")) {
            player.playerStandIndex = 7220;
            player.playerWalkIndex = 7223;
            player.playerRunIndex = 7221;
            return;
        }
        if (weaponName.contains("clueless")) {
            player.playerStandIndex = 7271;
            player.playerWalkIndex = 7272;
            player.playerRunIndex = 7273;
            return;
        }
        if (weaponName.contains("casket")) {
            player.playerRunIndex = 7274;
            return;
        }
        if (weaponName.contains("halberd") || weaponName.contains("hasta") || weaponName.contains("spear") || weaponName.contains("guthan") || weaponName.contains("sceptre")) {
            player.playerStandIndex = 809;
            player.playerWalkIndex = 1146;
            player.playerRunIndex = 1210;
            return;
        }

        if (weaponName.contains("scythe")) {
            player.playerStandIndex = 8057;
            player.playerWalkIndex = 1146;
            player.playerRunIndex = 1210;
            return;
        }

        if (weaponName.contains("banner")) {
            player.playerStandIndex = 1421;
            player.playerWalkIndex = 1422;
            player.playerRunIndex = 1427;
            return;
        }
        if (weaponName.startsWith("basket")) {
            player.playerStandIndex = 1836;
            player.playerWalkIndex = 1836;
            player.playerRunIndex = 1836;
            return;
        }
        if (weaponName.contains("sled")) {
            player.playerStandIndex = 1461;
            player.playerWalkIndex = 1468;
            player.playerRunIndex = 1467;
            return;
        }
        if (weaponName.contains("dharok")) {
            player.playerStandIndex = 0x811;
            player.playerWalkIndex = 2064;
            return;
        }
        if (weaponName.contains("ahrim")) {
            player.playerStandIndex = 809;
            player.playerWalkIndex = 1146;
            player.playerRunIndex = 1210;
            return;
        }
        if (weaponName.contains("verac")) {
            player.playerStandIndex = 1832;
            player.playerWalkIndex = 1830;
            player.playerRunIndex = 1831;
            return;
        }
        if (weaponName.contains("wand") || weaponName.contains("staff") || weaponName.contains("trident")) {
            player.playerStandIndex = 809;
            player.playerRunIndex = 1210;
            player.playerWalkIndex = 1146;
            return;
        }
        if (weaponName.contains("karil")) {
            player.playerStandIndex = 2074;
            player.playerWalkIndex = 2076;
            player.playerRunIndex = 2077;
            return;
        }
        if (weaponName.contains("2h sword") || weaponName.contains("godsword") || weaponName.contains("saradomin sw") || weaponName.contains("saradomin's bless") || weaponName.contains("large spade")) {
            if (weaponId != 7158) {
                player.playerStandIndex = 7053;
                player.playerWalkIndex = 7052;
                player.playerRunIndex = 7043;
                player.playerTurnIndex = 7044;
                player.playerTurn180Index = 7047;
                player.playerTurn90CWIndex = 7047;
                player.playerTurn90CCWIndex = 7048;
                return;
            }
        }
        if (weaponName.contains("bow")) {
            player.playerStandIndex = 808;
            player.playerWalkIndex = 819;
            player.playerRunIndex = 824;
            return;
        }

        if (weaponName.contains("zamorakian")) {
            player.playerStandIndex = 1662;
            player.playerWalkIndex = 1663;
            player.playerRunIndex = 1664;
            return;
        }

        switch (weaponId) {
            case DRAGON_HUNTER_LANCE:
                player.playerStandIndex = 813;
                player.playerWalkIndex = 1205;
                player.playerRunIndex = 2563;
                player.playerTurnIndex = 1209;
                player.playerTurn180Index = 1206;
                player.playerTurn90CWIndex = 1207;
                player.playerTurn90CCWIndex = 1208;
                break;
            case 7158:
                player.playerStandIndex = 2065;
                player.playerWalkIndex = 2064;
                break;
            case 22545:
                player.playerStandIndex = 244;
                player.playerWalkIndex = 247;
                player.playerRunIndex = 248;
                break;
            case 4151:
            case 12773:
            case 12774:
            case 12006:
                player.playerWalkIndex = 1660;
                player.playerRunIndex = 1661;
                break;
            case 8004:
            case 7960:
                player.playerStandIndex = 2065;
                player.playerWalkIndex = 2064;
                break;
            case 6528:
            case Items.HILL_GIANT_CLUB:
                player.playerStandIndex = 0x811;
                player.playerWalkIndex = 2064;
                player.playerRunIndex = 1664;
                break;
            case 12848:
            case 4153:
            case 13263:
                player.playerStandIndex = 1662;
                player.playerWalkIndex = 1663;
                player.playerRunIndex = 1664;
                break;
            case 10887:
                player.playerStandIndex = 5869;
                player.playerWalkIndex = 5867;
                player.playerRunIndex = 5868;
                break;
            case 20368:
            case 20370:
            case 20374:
            case 20372:
            case 11802:
            case 11804:
            case 11838:
            case 12809:
            case 11806:
            case 11808:
                player.playerStandIndex = 7053;
                player.playerWalkIndex = 7052;
                player.playerRunIndex = 7043;
                player.playerTurnIndex = 7049;
                player.playerTurn180Index = 7052;
                player.playerTurn90CWIndex = 7052;
                player.playerTurn90CCWIndex = 7052;
                break;
            case 1305:
                player.playerStandIndex = 809;
                break;

            default:
                player.playerStandIndex = 0x328;
                player.playerTurnIndex = 0x337;
                player.playerWalkIndex = 0x333;
                player.playerTurn180Index = 0x334;
                player.playerTurn90CWIndex = 0x335;
                player.playerTurn90CCWIndex = 0x336;
                player.playerRunIndex = 0x338;
                break;
        }
    }

    public static int getWepAnim(Player c) {
        String weaponName = ItemAssistant.getItemName(c.playerEquipment[Player.playerWeapon]).toLowerCase();
        if (c.playerEquipment[Player.playerWeapon] <= 0) {
            if (Objects.requireNonNull(c.getCombatConfigs().getWeaponMode().getAttackStyle()) == AttackStyle.AGGRESSIVE) {
                return 423;
            }
            return 422;
        }
        var weapon = CacheManager.INSTANCE.getItem(c.playerEquipment[Player.playerWeapon]).getId();
        if (weapon != -1) {
            if (WeaponAnimation.animationMap.get(weapon) != null) {
                return WeaponAnimation.animationMap.get(weapon).getAttackAnimation();
            }
        }
        if (weaponName.contains("dart")) {
            return c.getCombatConfigs().getWeaponMode().getAttackStyle() == AttackStyle.AGGRESSIVE ? 806 : 6600;
        }
        if (weaponName.contains("dragon 2h")) {
            return 407;
        }
        if (weaponName.contains("thrownaxe")) {
            return 7617;
        }
        if (weaponName.contains("knife") || weaponName.contains("javelin")) {
            return 806;
        }
        if (weaponName.contains("cross") && !weaponName.contains("karil") || weaponName.contains("c'bow") && !weaponName.contains("karil")) {
            return 4230;
        }
        if (weaponName.startsWith("dragon dagger")) {
            return 402;
        }
        if (weaponName.contains("abyssal dagger")) {
            return c.getCombatConfigs().getWeaponMode().getCombatStyle() == CombatStyle.SLASH ? 3297 : 3294;
        }
        if (weaponName.contains("dagger")) {
            return 412;
        }
        if (weapon == MORRIGANS_JAVELIN || weapon == MORRIGANS_JAVELIN_23619) {
            return 806;
        } else if (weapon == DRAGON_DART || weapon == DRAGON_DARTP || weapon == DRAGON_DARTP_11233 || weapon == DRAGON_DARTP_11234) {
            return 7554;
        } else if (weapon == DRAGON_THROWNAXE || weapon == DRAGON_THROWNAXE_21207) {
            return 7617;
        } else if (weapon == THAMMARONS_SCEPTRE || weapon == THAMMARONS_SCEPTRE_U || weapon == ACCURSED_SCEPTRE_AU || weapon == ACCURSED_SCEPTRE || weapon == ACCURSED_SCEPTRE_A) {
            return 1058;
        }
        if (weaponName.contains("2h sword") || weaponName.contains("aradomin sword")) {
            switch (c.getCombatConfigs().getWeaponMode().getIndex()) {
                case 0:// stab
                case 1:// str
                    return 7045;
                case 2:// str
                    return 7054;
                case 3:// def
                    return 7055;
            }
        }
        if (weaponName.contains("sword") && !weaponName.contains("training")) {
            return 451;
        }
        if (weaponName.contains("karil")) {
            return 2075;
        }
        if (weaponName.contains("bow") && !weaponName.contains("'bow") && !weaponName.contains("karil")) {
            return 426;
        }
        if (weaponName.contains("'bow") && !weaponName.contains("karil")) {
            return 4230;
        }
        return 451;
    }

    public static int getBlockEmote(Player c) {
        String shield = ItemAssistant.getItemName(c.playerEquipment[Player.playerShield]).toLowerCase();
        String weapon = ItemAssistant.getItemName(c.playerEquipment[Player.playerWeapon]).toLowerCase();
        if (shield.contains("defender"))
            return 4177;
        if (shield.contains("2h") && c.playerEquipment[Player.playerWeapon] != 7158)
            return 7050;
        if (shield.contains("book") || (weapon.contains("wand") || (weapon.contains("staff") || weapon.contains("trident"))))
            return 420;
        if (shield.contains("shield"))
            return 1156;
        if (shield.contains("warhammer"))
            return 397;
        if (shield.contains("bulwark"))
            return 7512;
        if (shield.contains("elder maul"))
            return 7517;
        switch (c.playerEquipment[Player.playerWeapon]) {
            case Items.SCYTHE_OF_VITUR:
                return 435;
            case DRAGON_HUNTER_LANCE:
                return 420;
            case 1734:
            case 411:
                return 3895;
            case 1724:
                return 3921;
            case 1709:
                return 3909;
            case 1704:
                return 3916;
            case 1699:
                return 3902;
            case 1689:
                return 3890;
            case 4755:
                return 2063;
            case 14484:
                return 397;
            case 12848:
            case 4153:
            case 13263:
                return 1666;
            case 13265:
            case 13267:
            case 13269:
            case 13271:
                return 3295;
            case 7158:
                return 410;
            case 4151:
            case 12773:
            case 12774:
            case 12006:
                return 1659;
            case 20727:
                return 2614;
            case 20368:
            case 20370:
            case 20374:
            case 20372:
            case 11802:
            case 11806:
            case 11808:
            case 11804:
            case 11838:
            case 12809:
            case 11730:
                return 7056;
            case -1:
                return 424;
            default:
                return 424;
        }
    }

    public static int getHitDelay(Player c) {
        String weaponName = ItemAssistant.getItemName(c.playerEquipment[Player.playerWeapon]).toLowerCase();
        if (c.usingMagic) {
            switch (CombatSpellData.getSpellId(c.getSpellId())) {
                case 12891:
                    return 4;
                case 12871:
                    return 6;
                default:
                    return 4;
            }
        }
        if (weaponName.contains("dart")) {
            return 3;
        }
        if (weaponName.contains("knife") || weaponName.contains("javelin") || weaponName.contains("thrownaxe") || weaponName.contains("throwing axe")) {
            return 3;
        }
        if (weaponName.contains("cross") || weaponName.contains("c'bow")) {
            return 4;
        }
        if (weaponName.contains("ballista")) {
            return 5;
        }
        if (weaponName.contains("bow") && !c.dbowSpec) {
            return 4;
        } else if (c.dbowSpec) {
            return 4;
        }

        switch (c.playerEquipment[Player.playerWeapon]) {// Toktz-xil-ul
            case 6522:
                return 3;
            case 10887:
                return 3;
            case 10034:
            case 10033:
                return 3;
            default:
                return 2;
        }
    }
}