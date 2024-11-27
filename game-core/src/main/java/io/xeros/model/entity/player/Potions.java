package io.xeros.model.entity.player;

import io.xeros.Server;
import io.xeros.content.SkillcapePerks;
import io.xeros.content.achievement_diary.impl.WildernessDiaryEntry;
import io.xeros.content.combat.Hitmark;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.items.ContainerUpdate;
import io.xeros.model.items.ItemAssistant;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;
import io.xeros.model.multiplayersession.duel.DuelSessionRules;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Sanity
 */

public class Potions {

    private final Player c;

    public Potions(Player c) {
        this.c = c;
    }

    public static long getSanfewTime(Player c) {
        return c.getAttributes().getLong("sanfew_time");
    }

    public void handlePotion(int itemId, int slot) {
        if (Boundary.isIn(c, Boundary.DUEL_ARENA) || Boundary.isIn(c, Boundary.WILDERNESS)) {
            if (itemId >= 11730 && itemId <= 11733) {
                c.sendMessage("You are not allowed to drink overloads right now.");
                return;
            }

            DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
            if (Objects.nonNull(session)) {
                if (session.getRules().contains(DuelSessionRules.Rule.NO_DRINKS)) {
                    c.sendMessage("Drinks have been disabled for this duel.");
                    return;
                }
            }
        }
        if (Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
            c.sendMessage("You are not allowed to drink potions in the outlast hut.");
            return;
        }
        if (c.isDead) {
            return;
        }
        if (c.teleTimer > 0) {
            return;
        }
        if (c.getPotionTimer().elapsed() >= 2) {
            c.getPotionTimer().reset();
            c.getFoodTimer().reset();
            c.getPA().sendSound(2401);
            applyPotion(itemId, slot);
        }
    }

    private void applyPotion(int itemId, int slot) {
        if (Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
            c.sendMessage("You are not allowed to drink potions right now.");
            return;
        }
        switch (itemId) {
            case 23685:
                doDivineCombat(23688, slot);
                break;
            case 23688:
                doDivineCombat(23691, slot);
                break;
            case 23691:
                doDivineCombat(23694, slot);
                break;
            case 23694:
                if (c.breakVials) {
                    doDivineCombat(-1, slot);
                    break;
                }
                doDivineCombat(229, slot);
                break;
            case 23733:
                doDivineRange(23736, slot);
                break;
            case 23736:
                doDivineRange(23739, slot);
                break;
            case 23739:
                doDivineRange(23742, slot);
                break;
            case 23742:
                if (c.breakVials) {
                    doDivineRange(-1, slot);
                    break;
                }
                doDivineRange(229, slot);
                break;
            case 23745:
                doDivineMagic(23748, slot);
                break;
            case 23748:
                doDivineMagic(23751, slot);
                break;
            case 23751:
                doDivineMagic(23754, slot);
                break;
            case 23754:
                if (c.breakVials) {
                    doDivineMagic(-1, slot);
                    break;
                }
                doDivineMagic(229, slot);
                break;
            case 3040:
                drinkMagicPotion(3042, slot, 6, false); // Magic pots
                break;
            case 3042:
                drinkMagicPotion(3044, slot, 6, false);
                break;
            case 3044:
                drinkMagicPotion(3046, slot, 6, false);
                break;
            case 3046:
                if (c.breakVials) {
                    drinkMagicPotion(-1, slot, 6, false);
                    break;
                }
                drinkMagicPotion(229, slot, 6, false);
                break;
            case 6685:
                drinkSaradominBrew(6687, slot); // saradomin brew
                break;
            case 6687:
                drinkSaradominBrew(6689, slot);
                break;
            case 6689:
                drinkSaradominBrew(6691, slot);
                break;
            case 6691:
                if (c.breakVials) {
                    drinkSaradominBrew(-1, slot);
                    break;
                }
                drinkSaradominBrew(229, slot);
                break;
            case 2450:
                drinkZamorakBrew(189, slot); // zammorak brew
                break;
            case 189:
                drinkZamorakBrew(191, slot);
                break;
            case 191:
                drinkZamorakBrew(193, slot);
                break;
            case 193:
                if (c.breakVials) {
                    drinkZamorakBrew(-1, slot);
                    break;
                }
                drinkZamorakBrew(229, slot);
                break;
            case 2436:
                drinkStatPotion(145, slot, 0, true); // sup attack
                break;
            case 145:
                drinkStatPotion(147, slot, 0, true);
                break;
            case 147:
                drinkStatPotion(149, slot, 0, true);
                break;
            case 149:
                if (c.breakVials) {
                    drinkStatPotion(-1, slot, 0, true);
                    break;
                }
                drinkStatPotion(229, slot, 0, true);
                break;
            case 2440:
                drinkStatPotion(157, slot, 2, true); // sup str
                break;
            case 157:
                drinkStatPotion(159, slot, 2, true);
                break;
            case 159:
                drinkStatPotion(161, slot, 2, true);
                break;
            case 161:
                if (c.breakVials) {
                    drinkStatPotion(-1, slot, 2, true);
                    break;
                }
                drinkStatPotion(229, slot, 2, true);
                break;
            case 2444:
                drinkStatPotion(169, slot, 4, false); // range pot
                break;
            case 169:
                drinkStatPotion(171, slot, 4, false);
                break;
            case 171:
                drinkStatPotion(173, slot, 4, false);
                break;
            case 173:
                if (c.breakVials) {
                    drinkStatPotion(-1, slot, 4, false);
                    break;
                }
                drinkStatPotion(229, slot, 4, false);
                break;
            case 2432:
                drinkStatPotion(133, slot, 1, false); // def pot
                break;
            case 133:
                drinkStatPotion(135, slot, 1, false);
                break;
            case 135:
                drinkStatPotion(137, slot, 1, false);
                break;
            case 137:
                if (c.breakVials) {
                    drinkStatPotion(-1, slot, 1, false);
                    break;
                }
                drinkStatPotion(229, slot, 1, false);
                break;
            case 113:
                drinkStatPotion(115, slot, 2, false); // str pot
                break;
            case 115:
                drinkStatPotion(117, slot, 2, false);
                break;
            case 117:
                drinkStatPotion(119, slot, 2, false);
                break;
            case 119:
                if (c.breakVials) {
                    drinkStatPotion(-1, slot, 2, false);
                    break;
                }
                drinkStatPotion(229, slot, 2, false);
                break;
            case 2428:
                drinkStatPotion(121, slot, 0, false); // attack pot
                break;
            case 121:
                drinkStatPotion(123, slot, 0, false);
                break;
            case 123:
                drinkStatPotion(125, slot, 0, false);
                break;
            case 125:
                if (c.breakVials) {
                    drinkStatPotion(-1, slot, 0, false);
                    break;
                }
                drinkStatPotion(229, slot, 0, false);
                break;
            case 2442:
                drinkStatPotion(163, slot, 1, true); // super def pot
                break;
            case 163:
                drinkStatPotion(165, slot, 1, true);
                break;
            case 165:
                drinkStatPotion(167, slot, 1, true);
                break;
            case 167:
                if (c.breakVials) {
                    drinkStatPotion(-1, slot, 1, true);
                    break;
                }
                drinkStatPotion(229, slot, 1, true);
                break;
            case 2430:
                drinkRestorePot(127, slot); // restore
                break;
            case 127:
                drinkRestorePot(129, slot);
                break;
            case 129:
                drinkRestorePot(131, slot);
                break;
            case 131:
                if (c.breakVials) {
                    drinkRestorePot(-1, slot);
                    break;
                }
                drinkRestorePot(229, slot);
                break;
            case 3024:
                drinkSuperRestorePot(3026, slot); // sup restore
                break;
            case 3026:
                drinkSuperRestorePot(3028, slot);
                break;
            case 3028:
                drinkSuperRestorePot(3030, slot);
                break;
            case 3030:
                if (c.breakVials) {
                    drinkSuperRestorePot(-1, slot);
                    break;
                }
                drinkSuperRestorePot(229, slot);
                break;
            case 10925:
                drinkSanfewPot(10927, slot); // sanfew serums
                break;
            case 10927:
                drinkSanfewPot(10929, slot);
                break;
            case 10929:
                drinkSanfewPot(10931, slot);
                break;
            case 10931:
                if (c.breakVials) {
                    drinkSanfewPot(-1, slot);
                    break;
                }
                drinkSanfewPot(229, slot);
                break;
            case 2438:
                drinkStatPotion(151, slot, 10, 3); // fishing pot
                break;
            case 151:
                drinkStatPotion(153, slot, 10, 3);
                break;
            case 153:
                drinkStatPotion(155, slot, 10, 3);
                break;
            case 155:
                if (c.breakVials) {
                    drinkStatPotion(-1, slot, 10, 3);
                    break;
                }
                drinkStatPotion(229, slot, 10, 3);

                break;

            case 3032:
                drinkStatPotion(3034, slot, 16, 3); // agility pot
                break;
            case 3034:
                drinkStatPotion(3036, slot, 16, 3);
                break;
            case 3036:
                drinkStatPotion(3038, slot, 16, 3);
                break;
            case 3038:
                if (c.breakVials) {
                    drinkStatPotion(-1, slot, 16, 3);
                    break;
                }
                drinkStatPotion(229, slot, 16, 3);
                break;
            case 2434:
                drinkPrayerPot(139, slot); // pray pot
                break;
            case 139:
                drinkPrayerPot(141, slot);
                break;
            case 141:
                drinkPrayerPot(143, slot);
                break;
            case 143:
                if (c.breakVials) {
                    drinkPrayerPot(-1, slot);
                    break;
                }
                drinkPrayerPot(229, slot);
                break;
            case 2446:
                drinkAntiPoison(175, slot, 200); // anti poisons
                break;
            case 175:
                drinkAntiPoison(177, slot, 200);
                break;
            case 177:
                drinkAntiPoison(179, slot, 200);
                break;
            case 179:
                if (c.breakVials) {
                    drinkAntiPoison(-1, slot, 200);
                    break;
                }
                drinkAntiPoison(229, slot, 200);
                break;
            case 2448:
                drinkAntiPoison(181, slot, 600); // superanti poisons
                break;
            case 181:
                drinkAntiPoison(183, slot, 600);
                break;
            case 183:
                drinkAntiPoison(185, slot, 600);
                break;
            case 185:
                if (c.breakVials) {
                    drinkAntiPoison(-1, slot, 600);
                    break;
                }
                drinkAntiPoison(229, slot, 600);
                break;

            case 5943:
                drinkAntidote(5945, slot, 518); // antidote+
                break;
            case 5945:
                drinkAntidote(5947, slot, 518);
                break;
            case 5947:
                drinkAntidote(5949, slot, 518);
                break;
            case 5949:
                if (c.breakVials) {
                    drinkAntidote(-1, slot, 518);
                    break;
                }
                drinkAntidote(229, slot, 518);
                break;

            case 5952:
                drinkAntidote(5954, slot, 1200); // antidote++
                break;
            case 5954:
                drinkAntidote(5956, slot, 1200);
                break;
            case 5956:
                drinkAntidote(5958, slot, 1200);
                break;
            case 5958:
                if (c.breakVials) {
                    drinkAntidote(-1, slot, 1200);
                    break;
                }
                drinkAntidote(229, slot, 1200);
                break;

            case 9739:
                drinkCombatPotion(9741, slot); // combat pot
                break;
            case 9741:
                drinkCombatPotion(9743, slot);
                break;
            case 9743:
                drinkCombatPotion(9745, slot);
                break;
            case 9745:
                if (c.breakVials) {
                    drinkCombatPotion(-1, slot);
                    break;
                }
                drinkCombatPotion(229, slot);
                break;
            case 12695:
                drinkStatPotion(12697, slot, 0, true); // supercombat pot
                enchanceStat(1, true);
                enchanceStat(2, true);
                break;
            case 12697:
                drinkStatPotion(12699, slot, 0, true);
                enchanceStat(1, true);
                enchanceStat(2, true);
                break;
            case 12699:
                drinkStatPotion(12701, slot, 0, true);
                enchanceStat(1, true);
                enchanceStat(2, true);
                break;
            case 12701:
                if (c.breakVials) {
                    drinkStatPotion(-1, slot, 0, true);
                    enchanceStat(1, true);
                    enchanceStat(2, true);
                    break;
                }
                drinkStatPotion(229, slot, 0, true);
                enchanceStat(1, true);
                enchanceStat(2, true);
                break;
            case 20992:
                if (Boundary.isIn(c, Boundary.RAIDS)) {
                    doOverload(20991, slot);
                    break;
                }
                c.sendMessage("You must be in raids to drink this type of overload.");
                break;
            case 20991:
                if (Boundary.isIn(c, Boundary.RAIDS)) {
                    doOverload(20990, slot);
                    break;
                }
                c.sendMessage("You must be in raids to drink this type of overload.");
                break;
            case 20990:
                if (Boundary.isIn(c, Boundary.RAIDROOMS)) {
                    doOverload(20989, slot);
                    break;
                }
                c.sendMessage("You must be in raids to drink this type of overload.");
                break;
            case 20989:
                if (Boundary.isIn(c, Boundary.RAIDROOMS)) {
                    doOverload(-1, slot);
                    break;
                }
                c.sendMessage("You must be in raids to drink this type of overload.");
                break;
            case 11730:
                doOverload(11731, slot);
                break;
            case 11731:
                doOverload(11732, slot);
                break;
            case 11732:
                doOverload(11733, slot);
                break;
            case 11733:
                if (c.breakVials) {
                    doOverload(-1, slot);
                    break;
                }
                doOverload(229, slot);
                break;
            case 12905:
                drinkAntiVenom(12907, slot, 75, 1200);
                break;
            case 12907:
                drinkAntiVenom(12909, slot, 75, 1200);
                break;
            case 12909:
                drinkAntiVenom(12911, slot, 75, 1200);
                break;
            case 12911:
                if (c.breakVials) {
                    drinkAntiVenom(-1, slot, 75, 1200);
                    break;
                }
                drinkAntiVenom(229, slot, 75, 1200);
                break;
            case 12913:
                drinkAntiVenom(12915, slot, 300, 1200);
                break;
            case 12915:
                drinkAntiVenom(12917, slot, 300, 1200);
                break;
            case 12917:
                drinkAntiVenom(12919, slot, 300, 1200);
                break;
            case 12919:
                if (c.breakVials) {
                    drinkAntiVenom(-1, slot, 300, 1200);
                    break;
                }
                drinkAntiVenom(229, slot, 300, 1200);
                break;
            case 12625:
                drinkStamina(12627, slot, TimeUnit.MINUTES.toMillis(2));
                break;
            case 12627:
                drinkStamina(12629, slot, TimeUnit.MINUTES.toMillis(2));
                break;
            case 12629:
                drinkStamina(12631, slot, TimeUnit.MINUTES.toMillis(2));
                break;
            case 12631:
                if (c.breakVials) {
                    drinkStamina(-1, slot, TimeUnit.MINUTES.toMillis(2));
                    break;
                }
                drinkStamina(229, slot, TimeUnit.MINUTES.toMillis(2));
                break;
            case 21978:
                drinkSuperAntifire(21981, slot, TimeUnit.MINUTES.toMillis(3));
                break;
            case 21981:
                drinkSuperAntifire(21984, slot, TimeUnit.MINUTES.toMillis(3));
                break;
            case 21984:
                drinkSuperAntifire(21987, slot, TimeUnit.MINUTES.toMillis(3));
                break;
            case 21987:
                if (c.breakVials) {
                    drinkSuperAntifire(-1, slot, TimeUnit.MINUTES.toMillis(3));
                    break;
                }
                drinkSuperAntifire(229, slot, TimeUnit.MINUTES.toMillis(3));
                break;
            case 2452:
                drinkAntifire(2454, slot, TimeUnit.MINUTES.toMillis(6));
                break;
            case 2454:
                drinkAntifire(2456, slot, TimeUnit.MINUTES.toMillis(6));
                break;
            case 2456:
                drinkAntifire(2458, slot, TimeUnit.MINUTES.toMillis(6));
                break;
            case 2458:
                if (c.breakVials) {
                    drinkAntifire(-1, slot, TimeUnit.MINUTES.toMillis(6));
                    break;
                }
                drinkAntifire(229, slot, TimeUnit.MINUTES.toMillis(6));
                break;
            case 11951:
                drinkAntifire(11953, slot, TimeUnit.MINUTES.toMillis(12));
                break;
            case 11953:
                drinkAntifire(11955, slot, TimeUnit.MINUTES.toMillis(12));
                break;
            case 11955:
                drinkAntifire(11957, slot, TimeUnit.MINUTES.toMillis(12));
                break;
            case 11957:
                if (c.breakVials) {
                    drinkAntifire(-1, slot, TimeUnit.MINUTES.toMillis(12));
                    break;
                }
                drinkAntifire(229, slot, TimeUnit.MINUTES.toMillis(12));
                break;
            case 22209:
                drinkAntifire(22212, slot, TimeUnit.MINUTES.toMillis(6));
                break;
            case 22212:
                drinkAntifire(22215, slot, TimeUnit.MINUTES.toMillis(6));
                break;
            case 22215:
                drinkAntifire(22218, slot, TimeUnit.MINUTES.toMillis(6));
                break;
            case 22218:
                if (c.breakVials) {
                    drinkAntifire(-1, slot, TimeUnit.MINUTES.toMillis(6));
                    break;
                }
                drinkAntifire(229, slot, TimeUnit.MINUTES.toMillis(6));
                break;
            /*
             * Run energy
             */
            case 3008:
            case 3010:
            case 3012:
                drinkEnergyPots(itemId + 2, slot);
                break;

            case 3014:
                if (c.breakVials) {
                    drinkEnergyPots(-1, slot);
                    break;
                }

                drinkEnergyPots(229, slot);
                break;

            case 3016:
            case 3018:
            case 3020:
                drinkSuperEnergyPots(itemId + 2, slot);
                break;

            case 3022:
                if (c.breakVials) {
                    drinkEnergyPots(-1, slot);
                    break;
                }
                drinkSuperEnergyPots(229, slot);
                break;

            /*
             * case 3144: drinkStatPotion2(itemId, -1, slot, 1, true); c.sendMessage("Eating it"); break;
             */
        }
    }

    public void drinkSuperAntifire(int replaceItem, int slot, long duration) {
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        c.sendMessage("You now have resistance against dragon fire.");
        c.lastAntifirePotion = System.currentTimeMillis();
        c.antifireDelay = duration;
        c.getPA().sendGameTimer(ClientGameTimer.ANTIFIRE, TimeUnit.MILLISECONDS, (int) (duration));
    }

    public void drinkAntifire(int replaceItem, int slot, long duration) {
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        c.sendMessage("You now have resistance against dragon fire.");
        c.lastAntifirePotion = System.currentTimeMillis();
        c.antifireDelay = duration;
        c.getPA().sendGameTimer(ClientGameTimer.ANTIFIRE, TimeUnit.MILLISECONDS, (int) (duration));
    }

    public void drinkStamina(int replaceItem, int slot, long duration) {
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        c.staminaDelay = duration;
        int total = c.getRunEnergy() + 20;
        if (total > 100) {
            c.setRunEnergy(100, true);
            c.getPA().sendFrame126(Integer.toString(100), 149);
        }
        else {
            c.getPA().sendFrame126(Integer.toString(c.getRunEnergy()), 149);
            c.setRunEnergy(c.getRunEnergy() + 20, true);
        }
        c.getPA().sendGameTimer(ClientGameTimer.STAMINA, TimeUnit.MILLISECONDS, (int) (duration));
    }

    public void drinkAntiVenom(int replaceItem, int slot, int venomImmunityTicks, int poisonImmunityTicks) {
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        c.getHealth().resolveStatus(HealthStatus.VENOM, venomImmunityTicks);
        c.getHealth().resolveStatus(HealthStatus.POISON, poisonImmunityTicks);
        c.getPA().requestUpdates();
        if (venomImmunityTicks > 0) {
            c.getPA().sendGameTimer(ClientGameTimer.ANTIVENOM, TimeUnit.SECONDS, (int) (venomImmunityTicks * .6));
            c.getPA().sendGameTimer(ClientGameTimer.ANTIPOISON, TimeUnit.SECONDS, (int) (poisonImmunityTicks * .6));
        }
    }

    public void drinkAntidote(int replaceItem, int slot, int duration) {
        boolean venom = c.getHealth().getStatus().isVenomed();
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        c.getPA().requestUpdates();

        if (venom) {
            c.getHealth().resolveStatus(HealthStatus.VENOM, duration);
            c.getPA().sendGameTimer(ClientGameTimer.ANTIVENOM, TimeUnit.SECONDS, (int) (duration * .6));
            c.sendMessage("Venom");
        }
        else {
            c.getHealth().resolveStatus(HealthStatus.POISON, duration);
            c.getPA().sendGameTimer(ClientGameTimer.ANTIPOISON, TimeUnit.SECONDS, (int) (duration * .6));
        }
    }

    public void drinkAntiPoison(int replaceItem, int slot, int duration) {
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        c.getHealth().resolveStatus(HealthStatus.POISON, duration);
        c.getPA().sendGameTimer(ClientGameTimer.ANTIPOISON, TimeUnit.SECONDS, (int) (duration * .6));
        c.getPA().requestUpdates();
    }

    public void eatChoc(int slot) {
        if (c.getPotionTimer().elapsed() > 2) {
            c.getPotionTimer().reset();
            c.startAnimation(829);
            c.getItems().deleteItem(9553, slot, 1);
            c.getHealth().increase(10);
            c.sendMessage("The choc. bomb heals you.");
        }
    }

    public void drinkStatPotion(int replaceItem, int slot, int stat, boolean sup) {
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        enchanceStat(stat, sup);
    }

    public void drinkCombatPotion(int replaceItem, int slot) {
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        enchanceStat(0, false);
        enchanceStat(2, false);
    }

    public void drinkStatPotion(int replaceItem, int slot, int stat, int amount) {
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        c.playerLevel[stat] += amount;
        if (c.playerLevel[stat] > c.getLevelForXP(c.playerXP[stat]) + amount) {
            c.playerLevel[stat] = c.getLevelForXP(c.playerXP[stat]) + amount;
        }
        c.getPA().refreshSkill(stat);
    }

    public void drinkPrayerPot(int replaceItem, int slot) {
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        c.playerLevel[5] += (c.getLevelForXP(c.playerXP[5]) * .33);
        if (Boundary.isIn(c, Boundary.DEMONIC_RUINS_BOUNDARY)) {
            c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.DEMONIC_RUINS);
        }
        if (SkillcapePerks.PRAYER.isWearing(c) || SkillcapePerks.isWearingMaxCape(c))
            c.playerLevel[5] += 5;
        if (c.playerLevel[5] > c.getLevelForXP(c.playerXP[5]))
            c.playerLevel[5] = c.getLevelForXP(c.playerXP[5]);
        c.getPA().refreshSkill(5);
    }

    public void drinkSanfewPot(int replaceItem, int slot) {
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        for (int skill = 0; skill < c.playerLevel.length; skill++) {
            if (skill == 3)
                continue;
            if (c.playerLevel[skill] < c.getLevelForXP(c.playerXP[skill])) {
                c.playerLevel[skill] += 8 + (c.getLevelForXP(c.playerXP[skill]) * .25);
                if (SkillcapePerks.PRAYER.isWearing(c) || SkillcapePerks.isWearingMaxCape(c))
                    c.playerLevel[skill] += 5;
                if (c.playerLevel[skill] > c.getLevelForXP(c.playerXP[skill])) {
                    c.playerLevel[skill] = c.getLevelForXP(c.playerXP[skill]);
                }
                if (Boundary.isIn(c, Boundary.DEMONIC_RUINS_BOUNDARY)) {
                    c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.DEMONIC_RUINS);
                }
                c.getPA().refreshSkill(skill);
                c.getPA().setSkillLevel(skill, c.playerLevel[skill], c.playerXP[skill]);
            }
        }
        c.getHealth().resolveStatus(HealthStatus.VENOM, 100);
        c.getHealth().resolveStatus(HealthStatus.POISON, 100);
        c.getAttributes().setLong("sanfew_time", System.currentTimeMillis());
    }

    public void drinkEnergyPots(int replaceItem, int slot) {
        c.startAnimation(829);
        int total = c.getRunEnergy() + 10;
        if (total > 100) {
            c.setRunEnergy(100, true);
            c.getPA().sendFrame126(Integer.toString(100), 149);
        }
        else {
            c.getPA().sendFrame126(Integer.toString(c.getRunEnergy()), 149);
            c.setRunEnergy(c.getRunEnergy() + 10, true);
        }
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
    }

    public void drinkSuperEnergyPots(int replaceItem, int slot) {
        c.startAnimation(829);
        int total = c.getRunEnergy() + 20;
        if (total > 100) {
            c.setRunEnergy(100, true);
            c.getPA().sendFrame126(Integer.toString(100), 149);
        }
        else {
            c.getPA().sendFrame126(Integer.toString(c.getRunEnergy()), 149);
            c.setRunEnergy(c.getRunEnergy() + 20, true);
        }
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
    }

    public void drinkRestorePot(int replaceItem, int slot) {
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        for (int skill = 0; skill <= 6; skill++) {
            if (skill == 5 || skill == 3)
                continue;
            if (c.playerLevel[skill] < c.getLevelForXP(c.playerXP[skill])) {
                c.playerLevel[skill] += 10 + (c.getLevelForXP(c.playerXP[skill]) * .30);
                if (c.playerLevel[skill] > c.getLevelForXP(c.playerXP[skill])) {
                    c.playerLevel[skill] = c.getLevelForXP(c.playerXP[skill]);
                }
                if (Boundary.isIn(c, Boundary.DEMONIC_RUINS_BOUNDARY)) {
                    c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.DEMONIC_RUINS);
                }
                c.getPA().refreshSkill(skill);
                c.getPA().setSkillLevel(skill, c.playerLevel[skill], c.playerXP[skill]);
            }
        }
    }

    public void drinkSuperRestorePot(int replaceItem, int slot) {
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        for (int skill = 0; skill < c.playerLevel.length; skill++) {
            if (skill == 3)
                continue;
            if (c.playerLevel[skill] < c.getLevelForXP(c.playerXP[skill])) {
                c.playerLevel[skill] += 8 + (c.getLevelForXP(c.playerXP[skill]) * .25);
                if (SkillcapePerks.PRAYER.isWearing(c) || SkillcapePerks.isWearingMaxCape(c))
                    c.playerLevel[skill] += 5;
                if (c.playerLevel[skill] > c.getLevelForXP(c.playerXP[skill])) {
                    c.playerLevel[skill] = c.getLevelForXP(c.playerXP[skill]);
                }
                if (Boundary.isIn(c, Boundary.DEMONIC_RUINS_BOUNDARY)) {
                    c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.DEMONIC_RUINS);
                }
                c.getPA().refreshSkill(skill);
                c.getPA().setSkillLevel(skill, c.playerLevel[skill], c.playerXP[skill]);
            }
        }
    }

    public void drinkMagicPotion(int replaceItem, int slot, int stat, boolean sup) {
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        enchanceMagic(stat, sup);

    }

    public void enchanceMagic(int skillID, boolean sup) {
        c.playerLevel[skillID] += getBoostedMagic(skillID, sup);
        c.getPA().refreshSkill(skillID);
    }

    public int getBoostedMagic(int skill, boolean sup) {
        int increaseBy = 0;
        if (sup)
            increaseBy = (int) (c.getLevelForXP(c.playerXP[skill]) * .06);
        else
            increaseBy = (int) (c.getLevelForXP(c.playerXP[skill]) * .06);
        if (c.playerLevel[skill] + increaseBy > c.getLevelForXP(c.playerXP[skill]) + increaseBy + 1) {
            return c.getLevelForXP(c.playerXP[skill]) + increaseBy - c.playerLevel[skill];
        }
        return increaseBy;
    }

    public void drinkSaradominBrew(int replaceItem, int slot) {
        if (c.getHealth().getCurrentHealth() <= 0 || c.isDead) {
            return;
        }
        DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
        if (Objects.nonNull(session)) {
            if (session.getRules().contains(DuelSessionRules.Rule.NO_FOOD)) {
                c.sendMessage("The saradomin brew has been disabled because of its healing effect.");
                return;
            }
        }
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        int[] toDecrease = {0, 2, 4, 6};

        for (int tD : toDecrease) {
            c.playerLevel[tD] -= getBrewStat(tD, .10);
            if (c.playerLevel[tD] < 0)
                c.playerLevel[tD] = 1;
            c.getPA().refreshSkill(tD);
            c.getPA().setSkillLevel(tD, c.playerLevel[tD], c.playerXP[tD]);
        }
        c.playerLevel[1] += getBrewStat(1, .20) + 2;
        if (c.playerLevel[1] > (c.getLevelForXP(c.playerXP[1]) * 1.2 + 2)) {
            c.playerLevel[1] = (int) (c.getLevelForXP(c.playerXP[1]) * 1.2 + 2);
        }
        c.getPA().refreshSkill(1);

        int offset = getBrewStat(3, .15) + 2;
        int maximum = c.getHealth().getMaximumHealth() + offset;
        c.getHealth().setCurrentHealth(Math.min(c.getHealth().getCurrentHealth() + offset, maximum));
    }

    public void drinkZamorakBrew(int replaceItem, int slot) {
        if (c.getHealth().getCurrentHealth() <= 0 || c.isDead) {
            return;
        }
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);

        c.playerLevel[1] -= getBrewStat(1, .10) + 2;
        if (c.playerLevel[1] < 0)
            c.playerLevel[1] = 1;
        c.getPA().refreshSkill(1);
        c.getPA().setSkillLevel(1, c.playerLevel[1], c.playerXP[1]);

        int damage = (getBrewStat(1, .10) + 2);
        c.appendDamage(damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);

        c.playerLevel[0] += getBrewStat(0, .20) + 2;
        if (c.playerLevel[0] > (c.getLevelForXP(c.playerXP[0]) * 1.2 + 2)) {
            c.playerLevel[0] = (int) (c.getLevelForXP(c.playerXP[0]) * 1.2 + 2);
        }
        c.getPA().refreshSkill(0);

        c.playerLevel[2] += getBrewStat(2, .12) + 2;
        if (c.playerLevel[2] > (c.getLevelForXP(c.playerXP[2]) * 1.12 + 2)) {
            c.playerLevel[2] = (int) (c.getLevelForXP(c.playerXP[2]) * 1.12 + 2);
        }
        c.getPA().refreshSkill(2);
    }

    public void enchanceStat(int skillID, boolean sup) {
        c.playerLevel[skillID] += getBoostedStat(skillID, sup);
        c.getPA().refreshSkill(skillID);
    }

    public int getBrewStat(int skill, double amount) {
        return (int) (c.getLevelForXP(c.playerXP[skill]) * amount);
    }

    public int getBoostedStat(int skill, boolean sup) {
        int increaseBy = 0;
        if (sup)
            increaseBy = (int) (c.getLevelForXP(c.playerXP[skill]) * .20);
        else
            increaseBy = (int) (c.getLevelForXP(c.playerXP[skill]) * .13) + 1;
        if (c.playerLevel[skill] + increaseBy > c.getLevelForXP(c.playerXP[skill]) + increaseBy + 1) {
            return c.getLevelForXP(c.playerXP[skill]) + increaseBy - c.playerLevel[skill];
        }
        return increaseBy;
    }

    public void doAllDivine() {
        doDivineCombatBoost();
        doDivineRangeBoost();
        doDivineMagicBoost();
    }

    public void doOverload(int replaceItem, int slot) {
        int health = c.getHealth().getCurrentHealth();
        if (health <= 50) {
            c.sendMessage("I should get some more lifepoints before using this!");
            return;
        }
        c.getPA().sendGameTimer(ClientGameTimer.OVERLOAD, TimeUnit.MINUTES, 5);
        c.hasOverloadBoost = false;
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        c.hasOverloadBoost = true;
        createOverloadDamageEvent();
        doOverloadBoost();
        handleOverloadTimers();
        c.getPA().refreshSkill(0);
        c.getPA().refreshSkill(1);
        c.getPA().refreshSkill(2);
        c.getPA().refreshSkill(3);

    }

    public void doDivineCombat(int replaceItem, int slot) {
        int health = c.getHealth().getCurrentHealth();
        if (health <= 10) {
            c.sendMessage("I should get some more lifepoints before using this!");
            return;
        }
        c.hasDivineCombatBoost = false;
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        c.hasDivineCombatBoost = true;
        doDivineCombatBoost();
        handleDivineCombatTimers();
        c.startAnimation(3170);
        c.appendDamage(10, Hitmark.HIT);
        c.getPA().refreshSkill(0);
        c.getPA().refreshSkill(1);
        c.getPA().refreshSkill(2);
        c.getPA().refreshSkill(3);
        c.getPA().sendGameTimer(ClientGameTimer.DIVINE_SUPER_COMBAT, TimeUnit.SECONDS, 300);
    }

    public void doDivineRange(int replaceItem, int slot) {
        int health = c.getHealth().getCurrentHealth();
        if (health <= 10) {
            c.sendMessage("I should get some more lifepoints before using this!");
            return;
        }
        c.hasDivineRangeBoost = false;
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        c.hasDivineRangeBoost = true;
        doDivineRangeBoost();
        handleDivineRangeTimers();
        c.startAnimation(3170);
        c.appendDamage(10, Hitmark.HIT);
        c.getPA().refreshSkill(4);
        c.getPA().refreshSkill(3);
        c.getPA().sendGameTimer(ClientGameTimer.DIVINE_RANGE, TimeUnit.SECONDS, 300);
    }

    public void doDivineMagic(int replaceItem, int slot) {
        int health = c.getHealth().getCurrentHealth();
        if (health <= 10) {
            c.sendMessage("I should get some more lifepoints before using this!");
            return;
        }
        c.hasDivineMagicBoost = false;
        c.startAnimation(829);
        c.playerItems[slot] = replaceItem + 1;
        c.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        c.hasDivineMagicBoost = true;
        doDivineMagicBoost();
        handleDivineMagicTimers();
        c.startAnimation(3170);
        c.appendDamage(10, Hitmark.HIT);
        c.getPA().refreshSkill(6);
        c.getPA().refreshSkill(3);
        c.getPA().sendGameTimer(ClientGameTimer.DIVINE_MAGIC, TimeUnit.SECONDS, 300);
    }

    private void createOverloadDamageEvent() {
        CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.OVERLOAD_HITMARK_ID);
        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.OVERLOAD_HITMARK_ID, c, new CycleEvent() {
            int time = 5;

            @Override
            public void execute(CycleEventContainer b) {
                if (c == null) {
                    b.stop();
                    return;
                }
                if (c.teleTimer > 0) {
                    return;
                }
                if (time <= 0) {
                    b.stop();
                    return;
                }
                if (c.getHealth().getCurrentHealth() <= 10) {
                    b.stop();
                    return;
                }
                time--;
                c.startAnimation(3170);
                c.appendDamage(10, Hitmark.HIT);
                c.isOverloading = true;
            }

            @Override
            public void onStopped() {
                c.isOverloading = false;
            }
        }, 1);
    }

    public void resetOverload() {
        if (!c.hasOverloadBoost)
            return;
        c.hasOverloadBoost = false;
        int[] toNormalise = {0, 1, 2, 4, 6};
        for (int j : toNormalise) {
            c.playerLevel[j] = c.getLevelForXP(c.playerXP[j]);
            c.getPA().refreshSkill(j);
        }
        if (c.getHealth().getCurrentHealth() < c.getHealth().getMaximumHealth()) {
            c.getHealth().reset();
        }
        c.sendMessage("The effects of the potion have worn off...");
    }

    public void resetCombatDivine() {
        if (!c.hasDivineCombatBoost)
            return;
        c.hasDivineCombatBoost = false;
        int[] toNormalise = {0, 1, 2, 4, 6};
        for (int j : toNormalise) {
            c.playerLevel[j] = c.getLevelForXP(c.playerXP[j]);
            c.getPA().refreshSkill(j);
        }
        c.sendMessage("The effects of the divine combat potion have worn off...");
    }

    public void resetRangeDivine() {
        if (!c.hasDivineRangeBoost)
            return;
        c.hasDivineRangeBoost = false;
        int[] toNormalise = {4};
        for (int j : toNormalise) {
            c.playerLevel[j] = c.getLevelForXP(c.playerXP[j]);
            c.getPA().refreshSkill(j);
        }
        c.sendMessage("The effects of the divine range potion have worn off...");
    }

    public void resetMageDivine() {
        if (!c.hasDivineMagicBoost)
            return;
        c.hasDivineMagicBoost = false;
        int[] toNormalise = {6};
        for (int j : toNormalise) {
            c.playerLevel[j] = c.getLevelForXP(c.playerXP[j]);
            c.getPA().refreshSkill(j);
        }
        c.sendMessage("The effects of the divine mage potion have worn off...");
    }

    public void resetPotionBoost() {
        if (c.hasPotionBoost) {
            resetCombatDivine();
            resetMageDivine();
            resetRangeDivine();
            resetOverload();
            handleOverloadTimers();
            handleDivineRangeTimers();
            handleDivineMagicTimers();
            handleDivineCombatTimers();
        }
    }

    public boolean hasPotionBoost() {
        return c.hasDivineCombatBoost || c.hasDivineMagicBoost || c.hasDivineRangeBoost || c.hasOverloadBoost;
    }

    public void handleOverloadTimers() {
        CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.OVERLOAD_BOOST_ID);
        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.OVERLOAD_BOOST_ID, c, new CycleEvent() {

            @Override
            public void execute(CycleEventContainer b) {
                if (c == null) {
                    b.stop();
                    return;
                }
                resetOverload();
            }

        }, 500); // 5 minutes
    }

    public void handleDivineCombatTimers() {
        CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.DIVINE_COMBAT_POTION);
        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.DIVINE_COMBAT_POTION, c, new CycleEvent() {

            @Override
            public void execute(CycleEventContainer b) {
                if (c == null) {
                    b.stop();
                    return;
                }
                resetCombatDivine();
            }

        }, 500); // 5 minutes
    }

    public void handleDivineRangeTimers() {
        CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.DIVINE_RANGE_POTION);
        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.DIVINE_RANGE_POTION, c, new CycleEvent() {

            @Override
            public void execute(CycleEventContainer b) {
                if (c == null) {
                    b.stop();
                    return;
                }
                resetRangeDivine();
            }

        }, 500); // 5 minutes
    }

    public void handleDivineMagicTimers() {
        CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.DIVINE_MAGIC_POTION);
        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.DIVINE_MAGIC_POTION, c, new CycleEvent() {

            @Override
            public void execute(CycleEventContainer b) {
                if (c == null) {
                    b.stop();
                    return;
                }
                resetMageDivine();
            }

        }, 500); // 5 minutes
    }

    public void doOverloadBoost() {
        int[] toIncrease = {0, 1, 2, 4, 6};
        int boost;
        for (int j : toIncrease) {
            boost = (int) (getOverloadBoost(j));
            c.playerLevel[j] += boost;
            if (c.playerLevel[j] > (c.getLevelForXP(c.playerXP[j]) + boost))
                c.playerLevel[j] = (c.getLevelForXP(c.playerXP[j]) + boost);
            c.getPA().refreshSkill(j);
        }
    }

    public void doDivineCombatBoost() {
        doDivineBoost(0, 1, 2);

    }

    public void doDivineRangeBoost() {
        doDivineBoost(4);
    }

    public void doDivineMagicBoost() {
        doDivineBoost(6);
    }

    public void doDivineBoost(int... toIncrease) {
        int boost;
        for (int j : toIncrease) {
            boost = (int) (getDivineBoost(j));
            c.playerLevel[j] += boost;
            if (c.playerLevel[j] > (c.getLevelForXP(c.playerXP[j]) + boost))
                c.playerLevel[j] = (c.getLevelForXP(c.playerXP[j]) + boost);
            c.getPA().refreshSkill(j);
        }
    }

    public double getOverloadBoost(int skill) {
        return switch (skill) {
            case 0, 1, 2 -> 5 + (c.getLevelForXP(c.playerXP[skill]) * .22);
            case 4 -> 3 + (c.getLevelForXP(c.playerXP[skill]) * .22);
            case 6 -> 7;
            default -> 1;
        };
    }

    public double getDivineBoost(int skill) {
        return switch (skill) {
            case 0, 1, 2 -> (c.getLevelForXP(c.playerXP[skill]) * .20);
            case 4 -> 1 + ((c.getLevelForXP(c.playerXP[skill]) * .13));
            case 6 -> 4;
            default -> 1;
        };
    }

    public boolean isPotion(int itemId) {
        if (c.getItems().isNoted(itemId)) {
            return false;
        }
        String name = ItemAssistant.getItemName(itemId);
        return name.contains("(4)") || name.contains("(3)") || name.contains("(2)") || name.contains("(1)") || name.contains("Antidote");
    }

}