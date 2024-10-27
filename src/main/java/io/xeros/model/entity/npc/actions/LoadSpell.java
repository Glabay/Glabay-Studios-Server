package io.xeros.model.entity.npc.actions;

import io.xeros.Server;
import io.xeros.content.bosses.Skotizo;
import io.xeros.content.bosses.Tekton;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.bosses.wildypursuit.TheUnbearable;
import io.xeros.content.minigames.inferno.InfernoWaveData;
import io.xeros.model.CombatType;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Location3D;
import io.xeros.util.Misc;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static io.xeros.model.entity.npc.NPCHandler.*;

public class LoadSpell {

    private static NPCHandler handler() {
        return Server.npcHandler;
    }

    public static void loadSpell(Player player, NPC npc) {
        int chance;
        switch (npc.getNpcId()) {
            case 7931:
            case 7932:
            case 7933:
            case 7934:
            case 7935:
            case 7936:
            case 7937:
            case 7938:
            case 7939:
            case 7940:
                if (npc.getHealth().getCurrentHealth() < npc.getHealth().getMaximumHealth() / 2) {
                    int healchance = Misc.random(100);
                    if (healchance > 75) {
                        npc.gfx0(1196);
                        npc.getHealth().setCurrentHealth(npc.getHealth().getCurrentHealth() + (npc.getHealth().getMaximumHealth() / 4));
                        player.sendMessage("The revenant drains power from within and heals.");
                        return;
                    }
                    int randomHit = Misc.random(15);
                    boolean distance = !player.goodDistance(npc.absX, npc.absY, player.getX(), player.getY(), 5);
                    if (randomHit <= 5 && !distance) {
                        npc.setAttackType(CombatType.MELEE);
                        npc.projectileId = -1;
                        npc.endGfx = -1;
                    }
                    else if (randomHit > 5 && randomHit <= 10 || distance) {
                        npc.setAttackType(CombatType.MAGE);
                        npc.projectileId = 1415;
                        npc.endGfx = -1;
                    }
                    else if (randomHit <= 15 || distance) {
                        npc.setAttackType(CombatType.RANGE);
                        npc.projectileId = 1415;
                        npc.endGfx = -1;
                    }
                }
                break;
            case 1605:
                npc.setAttackType(CombatType.MAGE);
                npc.endGfx = 78;
                break;
            case 5890:
                if (npc.getAttackType() != null) {
                    switch (npc.getAttackType()) {
                        case MAGE:
                            npc.endGfx = -1;
                            npc.projectileId = 1274;
                            break;
                        case MELEE:
                            npc.setAttackType(CombatType.MELEE);
                            npc.endGfx = -1;
                            npc.projectileId = -1;
                            break;
                        case SPECIAL:
                            npc.setAttackType(CombatType.SPECIAL);
                            handler().groundAttack(npc, player, -1, 1284, -1, 5);
                            npc.attackTimer = 8;
                            npc.hitDelayTimer = 5;
                            npc.endGfx = -1;
                            npc.projectileId = -1;
                            break;
                        default:
                            npc.setAttackType(CombatType.MELEE);
                            npc.endGfx = -1;
                            npc.projectileId = -1;
                            break;
                    }
                }
                break;
            case 8028:
                if (npc.getAttackType() != null) {
                    switch (npc.getAttackType()) {
                        case MAGE:
                            npc.setAttackType(CombatType.MAGE);
                            npc.hitDelayTimer = 5;
                            npc.endGfx = -1;
                            npc.projectileId = 393;
                            break;
                        case MELEE:
                            npc.setAttackType(CombatType.MELEE);
                            npc.hitDelayTimer = 5;
                            npc.endGfx = -1;
                            npc.projectileId = -1;
                            break;
                        case SPECIAL:
                            npc.setAttackType(CombatType.SPECIAL);
                            handler().groundAttack(npc, player, -1, 1284, -1, 5);
                            npc.hitDelayTimer = 5;
                            npc.endGfx = -1;
                            npc.projectileId = -1;
                            break;
                        default:
                            npc.setAttackType(CombatType.MAGE);
                            npc.hitDelayTimer = 5;
                            npc.endGfx = -1;
                            npc.projectileId = 393;
                            break;
                    }
                }
                break;
            case 7706:
                npc.setAttackType(CombatType.RANGE);
                npc.hitDelayTimer = 5;
                npc.projectileId = 1375;
                npc.endGfx = -1;
                break;
            case 9021:
            case 9022:
            case 9023:
                //gfxstuff
                if (npc.getAttackType() != null) switch (npc.getAttackType()) {
                    case MAGE:
                        npc.setAttackType(CombatType.MAGE);
                        handler().groundSpell(npc, player, 280, 281, "vetion", 4);
                        break;
                    case MELEE:
                        npc.setAttackType(CombatType.MELEE);
                        npc.endGfx = -1;
                        npc.projectileId = -1;
                        break;
                    default:
                        break;
                }
                break;
            case 7144:
            case 7145:
            case 7146:
                if (npc.getAttackType() != null) switch (npc.getAttackType()) {
                    case MAGE:
                        npc.setAttackType(CombatType.MAGE);
                        npc.endGfx = 1304;
                        npc.projectileId = 1305;
                        break;
                    case MELEE:
                        npc.setAttackType(CombatType.MELEE);
                        npc.endGfx = -1;
                        npc.projectileId = -1;
                        break;
                    case RANGE:
                        npc.setAttackType(CombatType.RANGE);
                        npc.endGfx = 1303;
                        npc.projectileId = 1302;
                        break;
                    case SPECIAL:
                        npc.setAttackType(CombatType.SPECIAL);
                        handler().groundAttack(npc, player, 304, 303, 305, 5);
                        npc.attackTimer = 8;
                        npc.hitDelayTimer = 5;
                        npc.endGfx = -1;
                        npc.projectileId = -1;
                        break;
                    default:
                        break;
                }
                break;
            case 9293:
                int random122 = Misc.random(6);
                if (random122 <= 2) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = 1735;
                    npc.endGfx = 1736;
                    npc.maxHit = 20;
                    npc.hitDelayTimer = 4;
                    npc.attackTimer = 8;
                    break;
                }
                else if (random122 <= 5) {
                    npc.setAttackType(CombatType.MELEE);
                    npc.maxHit = 20;
                    npc.projectileId = -1;
                    npc.endGfx = -1;
                    break;
                }
                else {
                    npc.setAttackType(CombatType.SPECIAL);
                    handler().groundAttack(npc, player, 1744, 1736, 1743, 5);
                    npc.maxHit = 25;
                    npc.attackTimer = 8;
                    npc.hitDelayTimer = 5;
                    npc.endGfx = -1;
                    npc.projectileId = -1;
                    break;
                }
            case 320:
                if (npc.getHealth().getStatus() == HealthStatus.POISON) {
                    npc.attackTimer *= 2;
                }
                break;
            case 498:
            case 499:
                npc.projectileId = 642;
                npc.setAttackType(CombatType.RANGE);
                npc.endGfx = -1;
                break;
            // Zilyana
            case 2205:
                if (Misc.random(4) == 0) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.endGfx = -1;
                    npc.projectileId = -1;
                }
                else {
                    npc.setAttackType(CombatType.MELEE);
                    npc.endGfx = -1;
                    npc.projectileId = -1;
                }
                break;
            case 6607:
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 156;
                npc.endGfx = 160;
                break;
            // Growler
            case 2207:
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 1203;
                break;
            // Bree
            case 2208:
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 9;
                break;
            // Saradomin priest
            case 2209:
                npc.setAttackType(CombatType.MAGE);
                npc.endGfx = 76;
                break;
            // Saradomin ranger
            case 2211:
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 20;
                npc.endGfx = -1;
                break;
            // Saradomin mage
            case 2212:
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 162;
                npc.endGfx = 163;
                npc.setProjectileDelay(2);
                break;
            // Graardor
            case 2215:
                if (Misc.random(4) == 1) {
                    npc.setAttackType(CombatType.RANGE);
                    npc.projectileId = 174;
                    npc.endGfx = -1;
                }
                else {
                    npc.setAttackType(CombatType.MELEE);
                    npc.projectileId = -1;
                    npc.endGfx = -1;
                }
                break;
            case 3428:
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 249;
                npc.endGfx = -1;
                break;
            // Steelwill
            case 2217:
                npc.projectileId = 1217;
                npc.endGfx = 1218;
                npc.setAttackType(CombatType.MAGE);
                break;
            // Grimspike
            case 2218:
                npc.projectileId = 1193;
                npc.endGfx = -1;
                npc.setAttackType(CombatType.RANGE);
                break;
            // Bandos ranger
            case 2242:
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 1197;
                npc.endGfx = -1;
                break;
            // Bandos mage
            case 2244:
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 165;
                npc.endGfx = 166;
                break;
            // Saradomin ranger
            case 3160:
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 20;
                npc.endGfx = -1;
                break;
            // Zammorak mage
            case 3161:
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 156;
                npc.endGfx = 157;
                npc.setProjectileDelay(2);
                break;
            // Armadyl boss
            case 3162:
                if (Misc.trueRand(2) == 0) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = 1200;
                }
                else {
                    npc.setAttackType(CombatType.RANGE);
                    npc.projectileId = 1199;
                }
                npc.setProjectileDelay(1);
                break;
            // Skree
            case 3163:
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 1192;
                npc.endGfx = -1;
                break;
            // Geerin
            case 3164:
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 1192;
                npc.endGfx = -1;
                break;
            // Armadyl ranger
            case 3167:
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 1192;
                npc.endGfx = -1;
                break;
            // Armadyl mage
            case 3168:
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 159;
                npc.endGfx = 160;
                break;
            // Aviansie
            case 3174:
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 1193;
                npc.endGfx = -1;
                break;
            case 1672:
                // Ahrim
                npc.setAttackType(CombatType.MAGE);
                switch (npc.getAttackType()) {
                    case SPECIAL:
                        npc.projectileId = 156;
                        npc.endGfx = 400;
                        break;
                    case MAGE:
                        npc.projectileId = 156;
                        npc.endGfx = 157;
                        break;
                    default:
                        npc.projectileId = 156;
                        npc.endGfx = 157;
                        break;
                }
                break;
            case 1675:
                // Karil
                npc.projectileId = 27;
                npc.setAttackType(CombatType.RANGE);
                break;
            case 2042:
                chance = 1;
                if (player != null) {
                    if (player.getZulrahEvent().getStage() == 9) {
                        chance = 2;
                    }
                }
                chance = Misc.random(chance);
                npc.setFacePlayer(true);
                if (chance < 2) {
                    npc.setAttackType(CombatType.RANGE);
                    npc.projectileId = 97;
                    npc.endGfx = -1;
                    npc.hitDelayTimer = 3;
                    npc.attackTimer = 4;
                }
                else {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = 156;
                    npc.endGfx = -1;
                    npc.hitDelayTimer = 3;
                    npc.attackTimer = 4;
                }
                break;
            case 1610:
                npc.setAttackType(CombatType.MAGE);
                npc.endGfx = 78;
                break;
            case 1611:
                npc.setAttackType(CombatType.MAGE);
                npc.endGfx = 76;
                break;
            case 1612:
                npc.setAttackType(CombatType.MAGE);
                npc.endGfx = 77;
                break;
            case 2044:
                npc.setFacePlayer(true);
                if (Misc.random(3) > 0) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = 1046;
                    npc.endGfx = -1;
                    npc.hitDelayTimer = 3;
                    npc.attackTimer = 4;
                }
                else {
                    npc.setAttackType(CombatType.RANGE);
                    npc.projectileId = 1044;
                    npc.endGfx = -1;
                    npc.hitDelayTimer = 3;
                    npc.attackTimer = 4;
                }
                break;
            case 1443:
                npc.setFacePlayer(true);
                if (Misc.random(2) > 0) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = 1046;
                    npc.endGfx = -1;
                    npc.hitDelayTimer = 3;
                }
                else {
                    npc.setAttackType(CombatType.MELEE);
                }
                break;
            case 983:
                // Dagannoth mother Air
            case 6373:
                npc.setAttackType(CombatType.MAGE);
                npc.attackTimer = 4;
                npc.projectileId = 159;
                npc.endGfx = 160;
                break;
            case 984:
                // Dagannoth mother Water
            case 6375:
                npc.setAttackType(CombatType.MAGE);
                npc.attackTimer = 4;
                npc.projectileId = 162;
                npc.endGfx = 163;
                break;
            case 985:
                // Dagannoth mother Fire
            case 6376:
                npc.setAttackType(CombatType.MAGE);
                npc.attackTimer = 4;
                npc.projectileId = 156;
                npc.endGfx = 157;
                break;
            case 6378:
                // Mother Earth
                npc.setAttackType(CombatType.MAGE);
                npc.attackTimer = 4;
                npc.projectileId = 165;
                npc.endGfx = 166;
                break;
            case 987:
                // Dagannoth mother range
            case 6377:
                npc.setAttackType(CombatType.RANGE);
                npc.attackTimer = 4;
                npc.projectileId = 996;
                npc.endGfx = -1;
                break;
            case 6371:
                // Karamel
                if (Misc.random(10) > 6) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.attackTimer = 6;
                    npc.endGfx = 369;
                    npc.forceChat("Semolina-Go!");
                }
                else {
                    npc.setAttackType(CombatType.RANGE);
                    npc.attackTimer = 3;
                    npc.endGfx = -1;
                }
                break;
            case 6372:
                // Dessourt
                npc.setAttackType(CombatType.MAGE);
                npc.attackTimer = 4;
                npc.projectileId = 866;
                npc.endGfx = 865;
                if (Misc.random(10) > 6) {
                    npc.forceChat("Hssssssssss");
                }
                break;
            case 6368:
                // Culinaromancer
                npc.setAttackType(CombatType.MAGE);
                npc.attackTimer = 4;
                break;
            case 2043:
                npc.setFacePlayer(false);
                npc.facePosition(player.getX(), player.getY());
                npc.targetedLocation = new Location3D(player.getX(), player.getY(), player.heightLevel);
                npc.setAttackType(CombatType.MELEE);
                npc.attackTimer = 9;
                npc.hitDelayTimer = 6;
                npc.projectileId = -1;
                npc.endGfx = -1;
                break;
            case 6611:
            case 6612:
                chance = Misc.random(100);
                int distanceToVet = player.distanceToPoint(npc.absX, npc.absY);
                if (distanceToVet < 3) {
                    if (chance < 25) {
                        npc.setAttackType(CombatType.MAGE);
                        npc.attackTimer = 7;
                        npc.hitDelayTimer = 4;
                        handler().groundSpell(npc, player, 280, 281, "vetion", 4);
                    }
                    else if (chance > 90 && System.currentTimeMillis() - npc.lastSpecialAttack > 15000) {
                        npc.setAttackType(CombatType.SPECIAL);
                        npc.attackTimer = 5;
                        npc.hitDelayTimer = 2;
                        npc.lastSpecialAttack = System.currentTimeMillis();
                    }
                    else {
                        npc.setAttackType(CombatType.MELEE);
                        npc.attackTimer = 5;
                        npc.hitDelayTimer = 2;
                    }
                }
                else {
                    if (chance < 71) {
                        npc.setAttackType(CombatType.MAGE);
                        npc.attackTimer = 7;
                        npc.hitDelayTimer = 4;
                        handler().groundSpell(npc, player, 280, 281, "vetion", 4);
                    }
                    else if (System.currentTimeMillis() - npc.lastSpecialAttack > 15000) {
                        npc.setAttackType(CombatType.SPECIAL);
                        npc.attackTimer = 5;
                        npc.hitDelayTimer = 2;
                        npc.lastSpecialAttack = System.currentTimeMillis();
                    }
                    else {
                        npc.setAttackType(CombatType.MAGE);
                        npc.attackTimer = 7;
                        npc.hitDelayTimer = 4;
                        handler().groundSpell(npc, player, 280, 281, "vetion", 4);
                    }
                }
                break;
            case 6914:
                // Lizardman, Lizardman brute
                /*
                  Demonic Gorillas attack
                 */
            case 6915:
            case 6916:
            case 6917:
            case 6918:
            case 6919:
                int randomAtt = Misc.random(1);
                if (randomAtt == 1) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = 1291;
                    npc.endGfx = -1;
                    if (Misc.random(10) == 5) {
                        player.getHealth().proposeStatus(HealthStatus.POISON, 3, Optional.of(npc));
                    }
                }
                else {
                    npc.setAttackType(CombatType.MELEE);
                    npc.projectileId = -1;
                    npc.endGfx = -1;
                }
                break;
            /*
              Lizardman Shaman<
             */
            case 6766:
            case 6768:
                int randomAttack3 = Misc.random(100);
                if (randomAttack3 > 9 && randomAttack3 < 90) {
                    npc.setAttackType(CombatType.MELEE);
                    npc.attackTimer = 5;
                    npc.hitDelayTimer = 2;
                    npc.projectileId = -1;
                    npc.endGfx = -1;
                }
                else if (randomAttack3 > 89) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.hitDelayTimer = 3;
                    npc.projectileId = 1293;
                    npc.endGfx = 1294;
                    if (Misc.random(5) == 5) {
                        player.getHealth().proposeStatus(HealthStatus.POISON, 10, Optional.of(npc));
                    }
                }
                else {
                    npc.setAttackType(CombatType.SPECIAL);
                    npc.attackTimer = 10;
                    npc.projectileId = -1;
                    npc.endGfx = -1;
                    npc.hitDelayTimer = 8;
                    handler().groundSpell(npc, player, -1, 1295, "spawns", 10);
                }
                break;
            /*
              Crazy Archaeologist
             */
            case 6618:
                int randomAttack = Misc.random(10);
                String[] shout = {"I'm Bellock - respect me!", "Get off my site!", "No-one messes with Bellock's dig!", "These ruins are mine!", "Taste my knowledge!", "You belong in a museum!"};
                String randomShout = (shout[new Random().nextInt(shout.length)]);
                if (player.distanceToPoint(npc.absX, npc.absY) < 2) {
                    npc.forceChat(randomShout);
                    if (randomAttack > 2 && randomAttack < 7) {
                        npc.setAttackType(CombatType.MELEE);
                        npc.attackTimer = 5;
                        npc.hitDelayTimer = 2;
                        npc.projectileId = -1;
                        npc.endGfx = -1;
                    }
                    else if (randomAttack > 6) {
                        npc.setAttackType(CombatType.RANGE);
                        npc.hitDelayTimer = 3;
                        npc.projectileId = 1259;
                        npc.endGfx = 140;
                    }
                    else {
                        npc.forceChat("Rain of knowledge!");
                        npc.setAttackType(CombatType.SPECIAL);
                        npc.projectileId = -1;
                        npc.endGfx = -1;
                        npc.hitDelayTimer = 3;
                        handler().groundSpell(npc, player, 1260, 131, "archaeologist", 4);
                    }
                }
                else {
                    if (randomAttack > 3) {
                        npc.forceChat(randomShout);
                        npc.setAttackType(CombatType.RANGE);
                        npc.projectileId = 1259;
                        npc.endGfx = 140;
                    }
                    else {
                        npc.forceChat("Rain of knowledge!");
                        npc.setAttackType(CombatType.SPECIAL);
                        npc.projectileId = -1;
                        npc.endGfx = -1;
                        npc.hitDelayTimer = 3;
                        handler().groundSpell(npc, player, 1260, 131, "archaeologist", 4);
                    }
                }
                break;
            case 6619:
                int randomAttack2 = Misc.random(10);
                String[] shout_chaos = {"Burn!", "WEUGH!", "Develish Oxen Roll!", "All your wilderness are belong to them!", "AhehHeheuhHhahueHuUEehEahAH", "I shall call him squidgy and he shall be my squidgy!"};
                String randomShoutChaos = (shout_chaos[new Random().nextInt(shout_chaos.length)]);
                npc.forceChat(randomShoutChaos);
                if (player.distanceToPoint(npc.absX, npc.absY) < 2) {
                    if (randomAttack2 > 2) {
                        npc.setAttackType(CombatType.MAGE);
                        npc.hitDelayTimer = 3;
                        npc.projectileId = 1044;
                        npc.endGfx = 140;
                    }
                    else {
                        npc.setAttackType(CombatType.SPECIAL);
                        npc.projectileId = -1;
                        npc.endGfx = -1;
                        npc.hitDelayTimer = 3;
                        handler().groundSpell(npc, player, 1045, 131, "fanatic", 4);
                    }
                }
                else {
                    if (randomAttack2 > 3) {
                        npc.setAttackType(CombatType.MAGE);
                        npc.projectileId = 1044;
                        npc.endGfx = 140;
                    }
                    else {
                        npc.setAttackType(CombatType.SPECIAL);
                        npc.projectileId = -1;
                        npc.endGfx = -1;
                        npc.hitDelayTimer = 3;
                        handler().groundSpell(npc, player, 1045, 131, "fanatic", 4);
                    }
                }
                break;
            case 465:
                boolean distanceToWyvern = player.goodDistance(npc.absX, npc.absY, player.getX(), player.getY(), 3);
                int newRandom = Misc.random(10);
                if (newRandom >= 2) {
                    npc.setAttackType(CombatType.RANGE);
                    npc.projectileId = 258;
                    npc.endGfx = -1;
                }
                else if (distanceToWyvern && newRandom == 1) {
                    npc.setAttackType(CombatType.MELEE);
                    npc.projectileId = -1;
                    npc.endGfx = -1;
                }
                else {
                    npc.setAttackType(CombatType.DRAGON_FIRE);
                    npc.projectileId = 162;
                    npc.endGfx = 163;
                }
                break;
            /*
             * Hydra combat
             */
            case 8609:
                if (player.hydraAttackCount == 12) {
                    player.hydraAttackCount = 0;
                }
                //player.sendMessage("Hydra attack count reset to 0");
                if (player.hydraAttackCount <= 6 && player.countUntilPoison != 20) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = 1663;
                    npc.endGfx = -1;
                    npc.hitDelayTimer = 4;
                    player.hydraAttackCount++;
                    player.countUntilPoison++;
                }
                //player.sendMessage("count until poison hits: " + player.countUntilPoison + " [MAGE]");
                //player.sendMessage("hydra attack counter: " + player.hydraAttackCount + "[MAGE]");
                if (player.hydraAttackCount > 6 && player.hydraAttackCount <= 12 && player.countUntilPoison != 20) {
                    npc.setAttackType(CombatType.RANGE);
                    npc.projectileId = 1662;
                    npc.endGfx = -1;
                    npc.maxHit = 22;
                    player.hydraAttackCount++;
                    player.countUntilPoison++;
                }
                //player.sendMessage("count until poison hits: " + player.countUntilPoison + " [RANGE]");
                //player.sendMessage("hydra attack counter: " + player.hydraAttackCount + " [RANGE]");
                if (player.countUntilPoison == 20) {
                    npc.setAttackType(CombatType.SPECIAL);
                    npc.projectileId = -1;
                    npc.endGfx = -1;
                    player.countUntilPoison = 0;
                    handler().groundSpell(npc, player, 1660, 1655, "hydra", 4);
                    player.getHealth().proposeStatus(HealthStatus.POISON, Misc.random(3, 10), Optional.of(npc));
                    player.sendMessage("You have been poisoned.");
                }
                break;
            case 8611:
                //Wyrm
                if (npc.getAttackType() == CombatType.MAGE) {
                    npc.projectileId = 1634;
                    npc.hitDelayTimer = 6;
                    npc.maxHit = 13;
                }
                else {
                    npc.projectileId = -1;
                    npc.maxHit = 13;
                }
                if (npc.getDistance(player.getX(), player.getY()) <= 3) {
                    npc.setAttackType(CombatType.getRandom(CombatType.MAGE, CombatType.MELEE));
                    npc.maxHit = 13;
                }
                else {
                    npc.setAttackType(CombatType.MAGE);
                    npc.maxHit = 13;
                }
                break;
            case 8612:
                //Drake
                if (npc.getAttackType() == CombatType.RANGE) {
                    npc.projectileId = 1636;
                    npc.hitDelayTimer = 6;
                    npc.maxHit = 15;
                }
                else {
                    npc.projectileId = -1;
                    npc.maxHit = 15;
                }
                if (npc.totalAttacks == 7) {
                    npc.setAttackType(CombatType.SPECIAL);
                    npc.maxHit = 15;
                }
                else if (npc.getDistance(player.getX(), player.getY()) <= 3 && npc.getAttackType() != CombatType.SPECIAL) {
                    npc.setAttackType(CombatType.getRandom(CombatType.RANGE, CombatType.MELEE));
                    npc.maxHit = 15;
                }
                else {
                    npc.setAttackType(CombatType.RANGE);
                    npc.maxHit = 15;
                }
                if (npc.getAttackType() == CombatType.SPECIAL) {
                    handler().groundAttack(npc, player, 1637, -1, 1638, 4);
                    npc.totalAttacks = 0;
                    npc.maxHit = 15;
                }
                break;
            /*
             * Gets the npc id for the Rune Dragon & addy dragon
             */
            case 8031:
            case 8030:
                boolean distanceToDragon = player.goodDistance(npc.getX(), npc.getY(), player.getX(), player.getY(), 3);
                int randomAttack1 = Misc.random(12);
                int damage = 0;
                damage = Misc.random(handler().getMaxHit(player, npc));
                int hit = damage;
                int hp = npc.getHealth().getCurrentHealth();
                int maxHp = npc.getHealth().getMaximumHealth();
                /*
                 * Handles the main/range attack if any number 4 and above is rolled
                 * for randomAttack
                 */
                if (randomAttack1 >= 5 && randomAttack1 <= 7) {
                    npc.setAttackType(CombatType.RANGE);
                    npc.projectileId = 258;
                    npc.endGfx = -1;
                    npc.maxHit = 24;
                }
                else
                    /*
                     * Handles the melee attack if the player is within 1 tile and 1
                     * is rolled for randomAttack
                     */
                    if (distanceToDragon && randomAttack1 == 1) {
                        npc.setAttackType(CombatType.MELEE);
                        npc.projectileId = -1;
                        npc.endGfx = -1;
                        npc.maxHit = 30;
                    }
                    else
                        /*
                         * Handles the magic attack if 2 is rolled for randomAttack
                         */
                        if (randomAttack1 >= 8 && randomAttack1 <= 10) {
                            npc.setAttackType(CombatType.MAGE);
                            npc.projectileId = -1;
                            npc.endGfx = -1;
                            npc.maxHit = 38;
                        }
                        else
                            /*
                             * Handles the first special attack if a 2 is rolled for
                             * randomAttack + adds 100% dmg dealt to the Rune Dragons hp
                             */
                            if (randomAttack1 == 3) {
                                npc.setAttackType(CombatType.SPECIAL);
                                npc.projectileId = 1183;
                                npc.endGfx = 1363;
                                npc.maxHit = 30;
                                // TODO REAL Special gfx / special 1 / healing isnt working
                                if (hp >= maxHp) {
                                    return;
                                }
                                else if (npc != null && hp < maxHp) {
                                    npc.getHealth().increase(hit / 2);
                                    player.sendMessage("You feel the dragon leeching your life force.");
                                }
                            }
                            else
                                /*
                                 * Handles the second special attack if a 3 is rolled for the
                                 * randomAttack + does dmg per tick if hit
                                 * Projectile hits at the players coordinates TODO
                                 * 5x5 square radius for tick dmg TODO
                                 */
                                if (randomAttack1 == 4) {
                                    npc.setAttackType(CombatType.SPECIAL);
                                    npc.projectileId = 1198;
                                    npc.endGfx = 1196;
                                    CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                                        int ticks;

                                        @Override
                                        public void execute(CycleEventContainer container) {
                                            if (player.isDisconnected()) {
                                                onStopped();
                                                return;
                                            }
                                            switch (ticks++) {
                                                case 0:
                                                    npc.setAttackType(CombatType.SPECIAL);
                                                    npc.projectileId = -1;
                                                    npc.endGfx = -1;
                                                    npc.maxHit = 7;
                                                    //handler().groundSpell(npc, player, 1198, 1196, "Dragon", 4);
                                                    //player.sendMessage("@red@Lightening attack 1/5");
                                                    break;
                                                case 1:
                                                    npc.setAttackType(CombatType.SPECIAL);
                                                    npc.projectileId = -1;
                                                    npc.endGfx = -1;
                                                    npc.maxHit = 7;
                                                    //player.sendMessage("@red@Lightening attack 2/5");
                                                    break;
                                                case 2:
                                                    npc.setAttackType(CombatType.SPECIAL);
                                                    npc.projectileId = -1;
                                                    npc.endGfx = -1;
                                                    npc.maxHit = 7;
                                                    //player.sendMessage("@red@Lightening attack 3/5");
                                                    container.stop();
                                                    break;
                                            }
                                        }
                                    }, 1); //handles delay between ticks
                                }
                                else
                                    /*
                                     * handles the dragonfire breathe
                                     */ {
                                    npc.setAttackType(CombatType.DRAGON_FIRE);
                                    npc.projectileId = 162;
                                    npc.endGfx = 163;
                                }
                break;
            case 1046:
            case 1049:
                if (Misc.random(10) > 0) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.gfx100(194);
                    npc.projectileId = 195;
                    npc.endGfx = 196;
                }
                else {
                    npc.setAttackType(CombatType.SPECIAL);
                    npc.gfx100(194);
                    npc.projectileId = 195;
                    npc.endGfx = 576;
                }
                break;
            case 6610:
                if (Misc.random(15) > 0) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.gfx100(164);
                    npc.projectileId = 165;
                    npc.endGfx = 166;
                    player.getHealth().proposeStatus(HealthStatus.POISON, 8, Optional.of(npc));
                    if (!player.getHealth().getStatus().isPoisoned()) {
                        player.sendMessage("You have been poisoned!");
                    }
                }
                else {
                    npc.setAttackType(CombatType.SPECIAL);
                    npc.gfx0(164);
                    npc.projectileId = 165;
                    npc.endGfx = 166;
                }
                break;
            case 3132:
                npc.setAttackType(CombatType.MAGE);
                npc.gfx100(164);
                npc.projectileId = 165;
                npc.endGfx = 166;
                break;
            case 3131:
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 258;
                npc.endGfx = -1;
                break;
            case 6609:
                if (player != null) {
                    int randomHit = Misc.random(20);
                    boolean distance = !player.goodDistance(npc.absX, npc.absY, player.getX(), player.getY(), 5);
                    if (randomHit < 15 && !distance) {
                        npc.setAttackType(CombatType.MELEE);
                        npc.projectileId = -1;
                        npc.endGfx = -1;
                    }
                    else if (randomHit >= 15 && randomHit < 20 || distance) {
                        npc.setAttackType(CombatType.MAGE);
                        npc.projectileId = 395;
                        npc.endGfx = 431;
                    }
                    else {
                        npc.setAttackType(CombatType.SPECIAL);
                        npc.projectileId = -1;
                        npc.endGfx = -1;
                    }
                }
                break;
            case 5535:
            case 494:
            case 492:
                npc.setAttackType(CombatType.MAGE);
                if (Misc.random(5) > 0 && npc.getNpcId() == 494 || npc.getNpcId() == 5535) {
                    npc.gfx0(161);
                    npc.projectileId = 162;
                    npc.endGfx = 163;
                }
                else {
                    npc.gfx0(155);
                    npc.projectileId = 156;
                    npc.endGfx = 157;
                }
                break;
            case 2892:
                npc.projectileId = 94;
                npc.setAttackType(CombatType.MAGE);
                npc.endGfx = 95;
                break;
            case 2894:
                npc.projectileId = 298;
                npc.setAttackType(CombatType.RANGE);
                break;
            case 264:
            case 259:
            case 247:
            case 268:
            case 270:
            case 274:
            case 6593:
            case 273:
                int random3 = Misc.random(2);
                if (random3 == 0) {
                    npc.projectileId = 393;
                    npc.endGfx = 430;
                    npc.setAttackType(CombatType.DRAGON_FIRE);
                }
                else {
                    npc.setAttackType(CombatType.MELEE);
                    npc.projectileId = -1;
                    npc.endGfx = -1;
                }
                break;
            case 2919:
            case 2918:
            case 9033:
                //dragon
                int random2 = Misc.random(2);
                if (random2 == 0) {
                    npc.projectileId = 393;
                    npc.endGfx = 430;
                    npc.setAttackType(CombatType.DRAGON_FIRE);
                }
                else {
                    npc.setAttackType(CombatType.MELEE);
                    npc.projectileId = -1;
                    npc.endGfx = -1;
                }
                break;
            /*
             * brutal black dragons
             */
            case 7275:
                int bbdrandom2 = Misc.random(5);
                int distanceToBrutal = player.distanceToPoint(npc.getX(), npc.getY());
                if (bbdrandom2 <= 4) {
                    npc.projectileId = 396;
                    npc.endGfx = 428;
                    npc.setAttackType(CombatType.MAGE);
                }
                else if (distanceToBrutal <= 4) {
                    npc.setAttackType(CombatType.MELEE);
                    npc.projectileId = -1;
                    npc.endGfx = -1;
                }
                else {
                    npc.projectileId = 393;
                    npc.endGfx = 430;
                    npc.setAttackType(CombatType.DRAGON_FIRE);
                }
                break;
            /*
             * brutal black dragons
             */
            case 7274:
                int bReddrandom2 = Misc.random(5);
                int distanceToBrutalR = player.distanceToPoint(npc.getX(), npc.getY());
                if (bReddrandom2 <= 4) {
                    npc.projectileId = 396;
                    npc.endGfx = 428;
                    npc.setAttackType(CombatType.MAGE);
                }
                else if (distanceToBrutalR <= 4) {
                    npc.setAttackType(CombatType.MELEE);
                    npc.projectileId = -1;
                    npc.endGfx = -1;
                }
                else {
                    npc.projectileId = 393;
                    npc.endGfx = 430;
                    npc.setAttackType(CombatType.DRAGON_FIRE);
                }
                break;
            /*
             * brutal blue dragons
             */
            case 7273:
                int bblueDrandom2 = Misc.random(5);
                int distanceToBrutalB = player.distanceToPoint(npc.getX(), npc.getY());
                if (bblueDrandom2 <= 4) {
                    npc.projectileId = 396;
                    npc.endGfx = 428;
                    npc.setAttackType(CombatType.MAGE);
                }
                else if (distanceToBrutalB <= 4) {
                    npc.setAttackType(CombatType.MELEE);
                    npc.projectileId = -1;
                    npc.endGfx = -1;
                }
                else {
                    npc.projectileId = 393;
                    npc.endGfx = 430;
                    npc.setAttackType(CombatType.DRAGON_FIRE);
                }
                break;
            case 239:
                int random = Misc.random(100);
                int distance = player.distanceToPoint(npc.absX, npc.absY);
                if (random >= 60 && random < 65) {
                    npc.projectileId = 394; // green
                    npc.endGfx = 429;
                    npc.setAttackType(CombatType.DRAGON_FIRE);
                }
                else if (random >= 65 && random < 75) {
                    npc.projectileId = 395; // white
                    npc.endGfx = 431;
                    npc.setAttackType(CombatType.DRAGON_FIRE);
                }
                else if (random >= 75 && random < 80) {
                    npc.projectileId = 396; // blue
                    npc.endGfx = 428;
                    npc.setAttackType(CombatType.DRAGON_FIRE);
                }
                else if (random >= 80 && distance <= 4) {
                    npc.projectileId = -1; // melee
                    npc.endGfx = -1;
                    npc.setAttackType(CombatType.MELEE);
                }
                else {
                    npc.projectileId = 393; // red
                    npc.endGfx = 430;
                    npc.setAttackType(CombatType.DRAGON_FIRE);
                }
                break;
            // arma npcs
            case 2561:
                npc.setAttackType(CombatType.MELEE);
                break;
            case 2560:
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 1190;
                break;
            case 2559:
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 1203;
                break;
            case 2558:
                npc.setAttackType(CombatType.getRandom(CombatType.RANGE, CombatType.MAGE));
                if (npc.getAttackType() == CombatType.RANGE) {
                    npc.projectileId = 1197;
                }
                else {
                    npc.projectileId = 1198;
                }
                break;
            // sara npcs
            case 2562:
                // sara
                random = Misc.random(1);
                if (random == 0) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.endGfx = 1224;
                    npc.projectileId = -1;
                }
                else if (random == 1) npc.setAttackType(CombatType.MELEE);
                break;
            case 2563:
                // star
                npc.setAttackType(CombatType.MELEE);
                break;
            case 2564:
                // growler
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 1203;
                break;
            case 2565:
                // bree
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 9;
                break;
            case 2551:
                npc.setAttackType(CombatType.MELEE);
                break;
            case 2552:
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 1203;
                break;
            case 2553:
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 1206;
                break;
            case 2025:
                npc.setAttackType(CombatType.MAGE);
                int r = Misc.random(3);
                if (r == 0) {
                    npc.gfx100(158);
                    npc.projectileId = 159;
                    npc.endGfx = 160;
                }
                if (r == 1) {
                    npc.gfx100(161);
                    npc.projectileId = 162;
                    npc.endGfx = 163;
                }
                if (r == 2) {
                    npc.gfx100(164);
                    npc.projectileId = 165;
                    npc.endGfx = 166;
                }
                if (r == 3) {
                    npc.gfx100(155);
                    npc.projectileId = 156;
                }
                break;
            case 6707:
                npc.setAttackType(CombatType.MAGE);
                npc.gfx100(158);
                npc.projectileId = 159;
                npc.endGfx = 160;
                if (Misc.random(8) == 0) {
                    player.getPA().movePlayer(3239, 3618, 0);
                }
                break;
            case 2265:
                // supreme
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 298;
                break;
            case 2266:
                // prime
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 162;
                npc.endGfx = 477;
                break;
            case 2028:
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 27;
                break;
            case 8781:
                int r10 = Misc.random(10);
                if (r10 <= 9) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.startAnimation(-1);
                    npc.projectileId = 1380;
                    npc.endGfx = 431;
                }
                else {
                    npc.setAttackType(CombatType.SPECIAL);
                    npc.projectileId = 395;
                    npc.startAnimation(-1);
                    npc.endGfx = 555;
                    player.gfx0(437);
                    player.lastSpear = System.currentTimeMillis();
                    player.getPA().getSpeared(npc.absX, npc.absY, 3);
                    player.freezeTimer = 3;
                    player.sendMessage("The Queen's magic pushes you out of her way.");
                }
                break;
            case 2054:
                int r2 = Misc.random(1);
                if (r2 == 0) {
                    npc.setAttackType(CombatType.RANGE);
                    npc.gfx100(550);
                    npc.projectileId = 551;
                    npc.endGfx = 552;
                }
                else {
                    npc.setAttackType(CombatType.MAGE);
                    npc.gfx100(553);
                    npc.projectileId = 554;
                    npc.endGfx = 555;
                }
                break;
            case 6257:
                // saradomin strike
                npc.setAttackType(CombatType.MAGE);
                npc.endGfx = 76;
                break;
            case 6221:
                // zamorak strike
                npc.setAttackType(CombatType.MAGE);
                npc.endGfx = 78;
                break;
            case 6231:
                // arma
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 1199;
                break;
            case 7708:
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 660;
                break;
            // sara npcs
            case 3129:
                random = Misc.trueRand(15);
                if (random == 0) {
                    npc.setAttackType(CombatType.SPECIAL);
                    npc.projectileId = -1;
                }
                else if (random > 0 && random < 7) {
                    npc.setAttackType(CombatType.MELEE);
                    npc.projectileId = -1;
                }
                else {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = 1211;
                }
                break;
            case 3209:
                // cave horror
                /*
                  Chaos fanatic
                 */
                random = Misc.random(3);
                if (random == 0 || random == 1) {
                    npc.setAttackType(CombatType.MELEE);
                }
                else {
                    npc.setAttackType(CombatType.MAGE);
                }
                break;
            case 3127:
                int r3 = 0;
                if (handler().goodDistance(npc.absX, npc.absY, PlayerHandler.players[npc.spawnedBy].absX, PlayerHandler.players[npc.spawnedBy].absY, 1)) {
                    r3 = Misc.random(2);
                }
                else {
                    r3 = Misc.random(1);
                }
                if (r3 == 0) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = 448;
                    npc.endGfx = 157;
                }
                else if (r3 == 1) {
                    npc.setAttackType(CombatType.RANGE);
                    npc.endGfx = 451;
                    npc.projectileId = -1;
                    npc.hitDelayTimer = 6;
                    npc.attackTimer = 9;
                }
                else if (r3 == 2) {
                    npc.setAttackType(CombatType.MELEE);
                    npc.projectileId = -1;
                    npc.endGfx = -1;
                }
                break;
            case InfernoWaveData.JALTOK_JAD:
                int r4 = 0;
                if (handler().goodDistance(npc.absX, npc.absY, PlayerHandler.players[npc.spawnedBy].absX, PlayerHandler.players[npc.spawnedBy].absY, 1)) {
                    r4 = Misc.random(2);
                }
                else {
                    r4 = Misc.random(1);
                }
                if (r4 == 0) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = 448;
                    npc.endGfx = 157;
                }
                else if (r4 == 1) {
                    npc.setAttackType(CombatType.RANGE);
                    npc.projectileId = -1;
                    npc.endGfx = 451;
                }
                else if (r4 == 2) {
                    npc.setAttackType(CombatType.MELEE);
                    npc.projectileId = -1;
                    npc.endGfx = -1;
                }
                break;
            case 3125:
                if (player.distanceToPoint(npc.getX(), npc.getY()) > 2) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = 445;
                    npc.endGfx = 446;
                }
                else {
                    npc.setAttackType(CombatType.MELEE);
                    npc.projectileId = -1;
                    npc.endGfx = -1;
                }
                break;
            case 446:
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 100;
                npc.hitDelayTimer = 3;
                npc.endGfx = 101;
                break;
            case 3121:
            case 2167:
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 443;
                break;
            case 1678:
            case 1679:
            case 1680:
            case 1683:
            case 1684:
            case 1685:
                npc.setAttackType(CombatType.MELEE);
                npc.attackTimer = 4;
                break;
            case 319:
                int corpRandom = Misc.random(15);
                if (corpRandom >= 12) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.hitDelayTimer = 3;
                    npc.projectileId = Misc.random(1) == 0 ? 316 : 314;
                    npc.endGfx = -1;
                }
                if (corpRandom >= 3 && corpRandom <= 11) {
                    npc.setAttackType(CombatType.MELEE);
                    npc.projectileId = -1;
                    npc.hitDelayTimer = 2;
                    npc.endGfx = -1;
                }
                if (corpRandom <= 2) {
                    npc.setAttackType(CombatType.SPECIAL);
                    npc.hitDelayTimer = 3;
                    handler().groundSpell(npc, player, 315, 317, "corp", 4);
                }
                break;
            case InfernoWaveData.JAL_XIL:
                if (npc.getDistance(player.getX(), player.getY()) <= 1) {
                    int x = Misc.random(0, 1);
                    if (x == 0) {
                        npc.projectileId = -1;
                        npc.setAttackType(CombatType.MELEE);
                    }
                    else {
                        npc.projectileId = 1376;
                        npc.setAttackType(CombatType.RANGE);
                    }
                }
                else {
                    npc.projectileId = 1376;
                    npc.setAttackType(CombatType.RANGE);
                }
                break;
            case InfernoWaveData.JAL_ZEK:
                if (npc.getDistance(player.getX(), player.getY()) <= 1) {
                    int x = Misc.random(0, 1);
                    if (x == 0) {
                        npc.projectileId = -1;
                        npc.setAttackType(CombatType.MELEE);
                    }
                    else {
                        npc.projectileId = 1378;
                        npc.setAttackType(CombatType.MAGE);
                    }
                }
                else {
                    npc.projectileId = 1378;
                    npc.setAttackType(CombatType.MAGE);
                }
                break;
            case InfernoWaveData.JAL_IMKOT:
                npc.setAttackType(CombatType.MELEE);
                break;
            case InfernoWaveData.JAL_AKREK_KET:
                npc.setAttackType(CombatType.MELEE);
                break;
            case InfernoWaveData.JAL_AKREK_MEJ:
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 1381;
                break;
            case InfernoWaveData.JAL_AKREK_XIL:
                npc.setAttackType(CombatType.RANGE);
                npc.projectileId = 1379;
                break;
            case InfernoWaveData.JAL_AK:
                if (npc.getDistance(player.getX(), player.getY()) <= 1) {
                    int x = Misc.random(0, 1);
                    if (x == 0) {
                        npc.setAttackType(CombatType.MELEE);
                        npc.projectileId = -1;
                    }
                    else if (x == 1) {
                        if (player.protectingMagic()) {
                            npc.setAttackType(CombatType.RANGE);
                            npc.hitDelayTimer = 3;
                            npc.projectileId = 1378;
                        }
                        else if (player.protectingRange()) {
                            npc.setAttackType(CombatType.MAGE);
                            npc.hitDelayTimer = 3;
                            npc.projectileId = 1380;
                        }
                        else {
                            int y = Misc.random(0, 1);
                            if (y == 0) {
                                npc.setAttackType(CombatType.RANGE);
                                npc.hitDelayTimer = 3;
                                npc.projectileId = 1378;
                            }
                            else if (y == 1) {
                                npc.setAttackType(CombatType.MAGE);
                                npc.hitDelayTimer = 3;
                                npc.projectileId = 1380;
                            }
                        }
                    }
                }
                else {
                    if (player.protectingMagic()) {
                        npc.setAttackType(CombatType.RANGE);
                        npc.hitDelayTimer = 3;
                        npc.projectileId = 1378;
                    }
                    else if (player.protectingRange()) {
                        npc.setAttackType(CombatType.MAGE);
                        npc.hitDelayTimer = 3;
                        npc.projectileId = 1380;
                    }
                    else {
                        int y = Misc.random(0, 1);
                        if (y == 0) {
                            npc.setAttackType(CombatType.RANGE);
                            npc.hitDelayTimer = 3;
                            npc.projectileId = 1378;
                        }
                        else if (y == 1) {
                            npc.setAttackType(CombatType.MAGE);
                            npc.hitDelayTimer = 3;
                            npc.projectileId = 1380;
                        }
                    }
                }
                break;
            case InfernoWaveData.JAL_MEJRAH:
                npc.setAttackType(CombatType.RANGE);
                npc.hitDelayTimer = 3;
                npc.projectileId = 1382;
                int[] toDecrease = {0, 2, 4, 6};
                for (int tD : toDecrease) {
                    player.playerLevel[tD] -= 1;
                    if (player.playerLevel[tD] < 0) {
                        player.playerLevel[tD] = 1;
                    }
                    player.getPA().refreshSkill(tD);
                    player.getPA().setSkillLevel(tD, player.playerLevel[tD], player.playerXP[tD]);
                }
                break;
            /*
              Kalphite Queen Stage One
             */
            case 963:
            case 965:
                int kqRandom = Misc.random(2);
                switch (kqRandom) {
                    case 0:
                        npc.setAttackType(CombatType.MAGE);
                        npc.hitDelayTimer = 3;
                        npc.projectileId = 280;
                        npc.endGfx = 281;
                        break;
                    case 1:
                        npc.setAttackType(CombatType.RANGE);
                        npc.hitDelayTimer = 3;
                        npc.projectileId = 473;
                        npc.endGfx = 281;
                        break;
                    case 2:
                        npc.setAttackType(CombatType.MELEE);
                        npc.projectileId = -1;
                        npc.endGfx = -1;
                        break;
                }
                break;
            /*
              Tekton
             */
            case 7544:
                if (Objects.equals(tektonAttack, "MELEE")) {
                    npc.setAttackType(CombatType.MELEE);
                }
                else if (Objects.equals(tektonAttack, "SPECIAL")) {
                    npc.setAttackType(CombatType.SPECIAL);
                    Tekton.tektonSpecial(player);
                    tektonAttack = "MELEE";
                    npc.hitDelayTimer = 4;
                    npc.attackTimer = 8;
                }
                break;
            /*
              theunbearable
             */
            case TheUnbearable.NPC_ID:
                if (npc.getAttackType() != null) {
                    int randomNum = Misc.random(100);
                    if (randomNum >= 20) {
                        npc.setAttackType(CombatType.MELEE);
                    }
                    else {
                        npc.setAttackType(CombatType.MAGE);
                    }
                    if (Objects.requireNonNull(npc.getAttackType()) == CombatType.MAGE) {
                        npc.endGfx = 379;//157
                        npc.projectileId = 448;
                        npc.hitDelayTimer = 4;
                    }
                    else {
                        npc.setAttackType(CombatType.MELEE);
                        npc.endGfx = -1;
                        npc.projectileId = -1;
                    }
                }
                break;
            /*
              Fragment Of Seren
             */
            case FragmentOfSeren.NPC_ID:
                if (Objects.equals(queenAttack, "MAGIC")) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = 195;
                    npc.endGfx = 196;
                    npc.hitDelayTimer = 4;

                }
                else if (Objects.equals(queenAttack, "SPECIAL")) {
                    npc.setAttackType(CombatType.SPECIAL);
                    FragmentOfSeren.handleSpecialAttack(player);
                    queenAttack = "MAGIC";
                    npc.hitDelayTimer = 4;
                    npc.attackTimer = 8;
                }
                break;
            case 7617:
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 1348;
                npc.endGfx = 1345;
                npc.hitDelayTimer = 5;
                npc.attackTimer = 15;
                break;
            case 7529:
                if (Misc.random(10) == 5) {
                    npc.setAttackType(CombatType.SPECIAL);
                    npc.projectileId = 1348;
                    npc.endGfx = 1345;
                    npc.hitDelayTimer = 4;
                }
                else {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = 1348;
                    npc.endGfx = 1345;
                    npc.hitDelayTimer = 4;
                }
                break;
            case 7566:
                if (Misc.random(10) <= 8) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = 1327;
                    npc.endGfx = 1328;
                    npc.hitDelayTimer = 3;
                    npc.maxHit = 35;
                }
                else {
                    npc.setAttackType(CombatType.SPECIAL);
                    npc.projectileId = 1329;
                    npc.endGfx = 1330;
                    npc.maxHit = 48;
                    npc.hitDelayTimer = 3;
                    break;
                }
                break;
            case Hespori.NPC_ID:
                int hesporiRandom = Misc.random(100);
                if (hesporiRandom <= 5) {
                    npc.setAttackType(CombatType.SPECIAL);
                    //npc.projectileId = Hespori.SPECIAL_PROJECTILE;
                    npc.hitDelayTimer = 2;
                    npc.attackTimer = 10;
                    npc.maxHit = 3;
                    player.freezeTimer = 5;
                    player.gfx100(Hespori.SPECIAL_HIT_GFX);
                    break;
                }
                else if (hesporiRandom <= 55) {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = Hespori.MAGIC_PROJECTILE;
                    npc.endGfx = 196;
                    npc.hitDelayTimer = 4;
                    npc.attackTimer = 10;
                    npc.maxHit = 3;
                    break;
                }
                else {
                    npc.setAttackType(CombatType.RANGE);
                    npc.projectileId = Hespori.RANGE_PROJECTILE;
                    //npc.endGfx = 196;
                    npc.hitDelayTimer = 4;
                    npc.attackTimer = 10;
                    npc.maxHit = 3;
                    break;
                }
            case 7554:
                //great olm
                int randomStyle1 = Misc.random(12);
                switch (randomStyle1) {
                    case 0:
                        //mage
                    case 1:
                        npc.setAttackType(CombatType.MAGE);
                        npc.projectileId = 1339;
                        npc.endGfx = 1353;
                        npc.maxHit = 33;
                        npc.hitDelayTimer = 2;
                        break;
                    case 2:
                        //range
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        npc.setAttackType(CombatType.RANGE);
                        npc.projectileId = 1340;
                        npc.endGfx = 1353;
                        npc.maxHit = 33;
                        npc.hitDelayTimer = 2;
                        break;
                    case 7:
                        //acid
                        npc.setAttackType(CombatType.SPECIAL);
                        npc.projectileId = 1354;
                        npc.endGfx = 1358;
                        npc.maxHit = 45;
                        npc.hitDelayTimer = 2;
                        player.getHealth().proposeStatus(HealthStatus.POISON, Misc.random(3, 10), Optional.of(npc));
                        player.sendMessage("You have been poisoned by Olm's acid attack!");
                        break;
                    case 8:
                        //dragon fire
                    case 9:
                        npc.setAttackType(CombatType.DRAGON_FIRE);
                        npc.projectileId = 393;
                        npc.endGfx = 430;
                        npc.maxHit = 62;
                        npc.hitDelayTimer = 3;
                        break;
                    case 10:
                        //burn
                        /*
                          Tekton magers
                         */
                    case 11:
                        npc.setAttackType(CombatType.SPECIAL);
                        npc.projectileId = 1349;
                        npc.endGfx = -1;
                        npc.maxHit = 45;
                        npc.hitDelayTimer = 2;
                        break;
                }
                break;
            case 7604:
            case 7605:
            case 7606:
                if (Misc.random(10) == 5) {
                    npc.setAttackType(CombatType.SPECIAL);
                    npc.forceChat("RAA!");
                    npc.projectileId = 1348;
                    npc.endGfx = 1345;
                    npc.hitDelayTimer = 3;
                }
                else {
                    npc.setAttackType(CombatType.MAGE);
                    npc.projectileId = 1348;
                    npc.endGfx = 1345;
                    npc.hitDelayTimer = 3;
                }
                break;
            case 5862:
                if (Objects.equals(player.CERBERUS_ATTACK_TYPE, "GROUND_ATTACK")) {
                    startAnimation(4492, npc);
                    npc.forceChat("Grrrrrrrrrrrrrr");
                    npc.setAttackType(CombatType.SPECIAL);
                    npc.hitDelayTimer = 4;
                    handler().groundSpell(npc, player, -1, 1246, "cerberus", 4);
                    player.CERBERUS_ATTACK_TYPE = "MELEE";
                }
                if (Objects.equals(player.CERBERUS_ATTACK_TYPE, "GHOST_ATTACK")) {
                    startAnimation(4494, npc);
                    player.CERBERUS_ATTACK_TYPE = "MELEE";
                }
                if (Objects.equals(player.CERBERUS_ATTACK_TYPE, "FIRST_ATTACK")) {
                    startAnimation(4493, npc);
                    npc.attackTimer = 5;
                    player.CERBERUS_ATTACK_TYPE = "MELEE";
                    CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                        int ticks;

                        @Override
                        public void execute(CycleEventContainer container) {
                            if (player.isDisconnected() || npc.isDeadOrDying()) {
                                container.stop();
                                return;
                            }
                            switch (ticks++) {
                                case 0:
                                    npc.setAttackType(CombatType.MELEE);
                                    npc.projectileId = -1;
                                    npc.endGfx = -1;
                                    break;
                                case 2:
                                    npc.setAttackType(CombatType.RANGE);
                                    npc.projectileId = 1245;
                                    npc.endGfx = 1244;
                                    break;
                                case 4:
                                    npc.setAttackType(CombatType.MAGE);
                                    npc.projectileId = 1242;
                                    npc.endGfx = 1243;
                                    container.stop();
                                    break;
                            }
                        }

                    }, 2);
                }
                else {
                    int randomStyle = Misc.random(2);
                    switch (randomStyle) {
                        case 0:
                            npc.setAttackType(CombatType.MELEE);
                            npc.projectileId = -1;
                            npc.endGfx = -1;
                            break;
                        case 1:
                            npc.setAttackType(CombatType.RANGE);
                            npc.projectileId = 1245;
                            npc.endGfx = 1244;
                            break;
                        case 2:
                            npc.setAttackType(CombatType.MAGE);
                            npc.projectileId = 1242;
                            npc.endGfx = 1243;
                            break;
                    }
                }
                break;
            case Skotizo.AWAKENED_ALTAR_NORTH:
            case Skotizo.AWAKENED_ALTAR_SOUTH:
            case Skotizo.AWAKENED_ALTAR_WEST:
            case Skotizo.AWAKENED_ALTAR_EAST:
                npc.setAttackType(CombatType.MAGE);
                npc.projectileId = 1242;
                npc.endGfx = 1243;
                break;
            case Skotizo.SKOTIZO_ID:
                int randomStyle;
                if (player.getSkotizo().firstHit) {
                    randomStyle = 1;
                    player.getSkotizo().firstHit = false;
                }
                else {
                    randomStyle = Misc.random(1);
                }
                switch (randomStyle) {
                    case 0:
                        npc.setAttackType(CombatType.MELEE);
                        npc.projectileId = -1;
                        npc.endGfx = -1;
                        break;
                    case 1:
                        npc.setAttackType(CombatType.MAGE);
                        npc.projectileId = 1242;
                        npc.endGfx = 1243;
                        break;
                }
                break;
            case 5867:
                npc.setAttackType(CombatType.RANGE);
                npc.hitDelayTimer = 3;
                npc.projectileId = 1230;
                npc.attackTimer = 15;
                break;
            case 5868:
                npc.setAttackType(CombatType.MAGE);
                npc.hitDelayTimer = 3;
                npc.projectileId = 127;
                npc.attackTimer = 15;
                break;
            case 5869:
                npc.setAttackType(CombatType.MELEE);
                npc.hitDelayTimer = 3;
                npc.projectileId = 1248;
                npc.attackTimer = 15;
                break;
        }
    }

}
