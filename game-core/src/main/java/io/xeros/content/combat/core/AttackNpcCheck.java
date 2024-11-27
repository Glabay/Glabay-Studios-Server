package io.xeros.content.combat.core;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Preconditions;
import io.xeros.content.bosses.Vorkath;
import io.xeros.content.bosses.hydra.HydraStage;
import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.minigames.raids.Raids;
import io.xeros.content.minigames.warriors_guild.WarriorsGuild;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.slayer.Slayer;
import io.xeros.content.skills.slayer.SlayerMaster;
import io.xeros.content.skills.slayer.Task;
import io.xeros.model.definition.Npcs;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

public class AttackNpcCheck {

    private static void sendCheckMessage(Player c, boolean sendMessages, String message) {
        if (sendMessages) {
            c.sendMessage(message);
        }
    }

    public static boolean check(Player c, Entity targetEntity, boolean sendMessages) {
        NPC npc = targetEntity.asNPC();
        int levelRequired;
        if (npc == null || npc.getHealth().getMaximumHealth() == 0) {
            return false;
        }
        if (!npc.canBeAttacked(c)) {
            return false;
        }

//        if (!PathFinder.getPathFinder().accessable(c, npc.getX(), npc.getY())) {
//            c.sendMessage("You cannot reach that!");
//            return false;
//        }

        // Inferno pillar
        if (npc.getNpcId() == 7710) {
            return false;
        }

        // Unpoked vorkath can't attack
        if (npc.getNpcId() == Vorkath.NPC_IDS[0]) {
            return false;
        }

        if (PetHandler.isPet(npc.getNpcId())) {
            return false;
        }

        if (!npc.getPosition().inMulti()) {
            //!npcs[i].getPosition().inMulti() && ((c.underAttackByPlayer > 0 && c.underAttackByNpc != i)
            //        || (c.underAttackByNpc > 0 && c.underAttackByNpc != i))

            if (!c.getPosition().inMulti() && (c.underAttackByPlayer > 0 && c.underAttackByNpc != npc.getIndex())
                    || (c.underAttackByNpc > 0 && c.underAttackByNpc != npc.getIndex() && npc.getIndex() != 1969 && npc.getIndex() != 7514)) {
                sendCheckMessage(c, sendMessages, "You are already in combat.");
                
                return false;
            }

            if (!Boundary.isIn(c, Boundary.OLM) && !Boundary.isIn(c, Boundary.RAIDS)) {
                if (npc.underAttackBy > 0 && npc.underAttackBy != c.getIndex()) {
                    sendCheckMessage(c, sendMessages, "This monster is already in combat.");
                    return false;
                }
            }
        }

        switch (npc.getNpcId()) {

            //case 5890:
            case 7563:
            case 5916:
            case 2045:
                return true; // Skip "I am already under attack" check
            case 8781:
                if (!Boundary.isIn(c, Boundary.DONATOR_ZONE_BOSS)) {
                    sendCheckMessage(c, sendMessages, "You must be closer to the Queen in order to attack it.");
                    return false;
                }
                break;
            case 4005://dark beast
                levelRequired = 90;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 415://abby demon
                levelRequired = 85;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 7276://bloodveld
                levelRequired = 50;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 437://jelly
            case 7277://jelly
                levelRequired = 52;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 423://dust devil
                levelRequired = 65;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 7279://spectres
                levelRequired = 60;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 7272://spectres
                levelRequired = 15;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 7278://spectres
                levelRequired = 80;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 1543://gargoyles
                levelRequired = 75;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 1047://cave horror
                levelRequired = 58;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 419://cockatrice
                levelRequired = 25;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 421://rockslug
                levelRequired = 20;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 435://pyrefiend
                levelRequired = 30;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 417://basilisk
                levelRequired = 40;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 427://turoth
                levelRequired = 55;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 411://kurask
                levelRequired = 70;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
            case 406://kurask
                levelRequired = 10;
                if (!Slayer.hasRequiredLevel(c, levelRequired)) {
                    return false;
                }
                break;
        }

        if ((c.underAttackByPlayer > 0 || c.underAttackByNpc > 0) && c.underAttackByNpc != npc.getIndex() && !c.getPosition().inMulti()) {
            sendCheckMessage(c, sendMessages, "I am already under attack.");
            return false;
        }

        // Zulrah
        if (npc.getNpcId() >= 2042 && npc.getNpcId() <= 2044 || npc.getNpcId() == 6720) {
            if (c.getZulrahEvent().isTransforming()) {
                return false;
            }
            if (c.getZulrahEvent().getStage() == 0) {
                return false;
            }
        }

        // Hydra
        if (HydraStage.isHydra(npc.getNpcId())) {
            int x = c.absX;
            int y = c.absY;
            if ((x == 1356 || x == 1357) && y >= 10257 && y <= 10278
                    || (y == 10278 || y == 10277) && x >= 1358 && x <= 1377
                    || (x == 1377 || x == 1376) && y >= 10257 && y <= 10278
                    || (y == 10257 || y == 10258) && x >= 1356 && x <= 1377) {
                sendCheckMessage(c, sendMessages, "@red@You can't hit Alchemical Hydra from this position!");
                return false;
            }
        }
        if (c.playerEquipment[Player.playerWeapon] == 22547 || c.playerEquipment[Player.playerWeapon] == 22542 || c.playerEquipment[Player.playerWeapon] == 22552 ) {
            sendCheckMessage(c, sendMessages, "Your weapon needs more then 1000 charges to function properly.");
            return false;
        }

        if (c.playerEquipment[Player.playerWeapon] == 12904 && c.usingSpecial) {
            c.usingSpecial=false;
            c.getItems().updateSpecialBar();
            c.attacking.reset();
            return false;
        }
        if (npc.getNpcId() == FragmentOfSeren.NPC_ID && !FragmentOfSeren.isAttackable) {
            sendCheckMessage(c, sendMessages, "You can't attack her right now.");
            return false;
        }
        Preconditions.checkState(npc != null, "Npc is null.");
        Optional<Task> task = SlayerMaster.get(npc.getName().replaceAll("_", " "));
        if (task.isPresent()) {
            int level = task.get().getLevel();
            if (c.playerLevel[Skill.SLAYER.getId()] < task.get().getLevel()) {
                sendCheckMessage(c, sendMessages, "You need a slayer level of " + level + " to attack this npc.");
                c.attacking.reset();
                return false;
            }
        }
        if (npc.getNpcId() == 7544) {
            if (!Boundary.isIn(c, Boundary.TEKTON_ATTACK_BOUNDARY) && !Boundary.isIn(c, Boundary.XERIC)) {
                sendCheckMessage(c, sendMessages, "You must be within tektons territory to attack him.");
                return false;
            }
        }
        if (npc.getNpcId() == 7573) {
            if (!Boundary.isIn(c, Boundary.SHAMAN_BOUNDARY) && !Boundary.isIn(c, Boundary.XERIC)) {
                sendCheckMessage(c, sendMessages, "You must be within the shaman attack boundries");
                return false;
            }
        }
        if (npc.getNpcId() == 7554) {
            Raids raidInstance = c.getRaidsInstance();
            if(raidInstance != null) {
                if (!raidInstance.rightHand || !raidInstance.leftHand) {
                    sendCheckMessage(c, sendMessages, "@red@Please destroy both hands before attacking The Great Olm.");
                    return false;
                }
            }
        }

        if (npc.getNpcId() == 4922 || npc.getNpcId() == 5129 || npc.getNpcId() == 8918 || npc.getNpcId() == 7860) {
            if (!Boundary.isIn(c, Boundary.WILDERNESS)) {
                sendCheckMessage(c, sendMessages, "You must be within this npc's original spawn location!");
                return false;
            }
        }
        if (npc.getNpcId() == 499) {
            if (!c.getSlayer().onTask("thermonuclear smoke devil") && !c.getSlayer().onTask("smoke devil")) {
                sendCheckMessage(c, sendMessages, "You do not have a "+npc.getName().replace("_", " ")+" task.");
                return false;
            }
        }

        if (npc.getNpcId() == Npcs.CERBERUS) {
            if (!c.getSlayer().onTask("cerberus") && !c.getSlayer().onTask("hellhound")) {
                sendCheckMessage(c, sendMessages, "You need a Cerberus or Hellhound task to fight Cerberus.");
                return false;
            }

            if (c.getLevel(Skill.SLAYER) < 91) {
                sendCheckMessage(c, sendMessages, "You need a Slayer level of 91 to fight Cerberus.");
                return false;
            }
        }

        if (npc.getNpcId() == 492) {
            if (!c.getSlayer().onTask("kraken") ) {
                sendCheckMessage(c, sendMessages, "You do not have a cave kraken task.");
                return false;
            }
            if (!c.getSlayer().getTask().isPresent() || !c.getSlayer().getTask().get().getPrimaryName().contains("kraken")) {
                sendCheckMessage(c, sendMessages, "You do not have a cave kraken task.");
                return false;
            }
        }
        if (npc.getNpcId() == 8609) {
            if(c.playerLevel[18] < 95) {
                sendCheckMessage(c, sendMessages, "You must have a slayer level of at least 95 to wear these boots");
                return false;
            }
        }

        if (npc.getNpcId() >= 5886 && npc.getNpcId() <= 5981 && npc.getNpcId() != 5944) {//abyssal sire
            if (!(c.getSlayer().getTask().isPresent()) || (c.getSlayer().getTask().isPresent() && !c.getSlayer().getTask().get().getPrimaryName().equals("abyssal demon")&& !c.getSlayer().getTask().get().getPrimaryName().equals("abyssal sire"))) {
                sendCheckMessage(c, sendMessages, "You need an abyssal task to attack this monster.");
                return false;
            }
        }

        if (npc.getNpcId() == 9026) {//rat
            if (!(c.getSlayer().getTask().isPresent()) || (c.getSlayer().getTask().isPresent() && !c.getSlayer().getTask().get().getPrimaryName().equals("crystalline rat"))) {
                sendCheckMessage(c, sendMessages, "The creature does not seem interested.");
                return false;
            }
        }
        if (npc.getNpcId() == 9027) {//spider
            if (!(c.getSlayer().getTask().isPresent()) || (c.getSlayer().getTask().isPresent() && !c.getSlayer().getTask().get().getPrimaryName().equals("crystalline spider"))) {
                sendCheckMessage(c, sendMessages, "The creature does not seem interested.");
                return false;
            }
        }
        if (npc.getNpcId() == 9028) {//bat
            if (!(c.getSlayer().getTask().isPresent()) || (c.getSlayer().getTask().isPresent() && !c.getSlayer().getTask().get().getPrimaryName().equals("crystalline bat"))) {
                sendCheckMessage(c, sendMessages, "The creature does not seem interested.");
                return false;
            }
        }
        if (npc.getNpcId() == 9029) {//unicorn
            if (!(c.getSlayer().getTask().isPresent()) || (c.getSlayer().getTask().isPresent() && !c.getSlayer().getTask().get().getPrimaryName().equals("crystalline unicorn"))) {
                sendCheckMessage(c, sendMessages, "The creature does not seem interested.");
                return false;
            }
        }
        if (npc.getNpcId() == 9030) {//scorpion
            if (!(c.getSlayer().getTask().isPresent()) || (c.getSlayer().getTask().isPresent() && !c.getSlayer().getTask().get().getPrimaryName().equals("crystalline scorpion"))) {
                sendCheckMessage(c, sendMessages, "The creature does not seem interested.");
                return false;
            }
        }
        if (npc.getNpcId() == 9031) {//wolf
            if (!(c.getSlayer().getTask().isPresent()) || (c.getSlayer().getTask().isPresent() && !c.getSlayer().getTask().get().getPrimaryName().equals("crystalline wolf"))) {
                sendCheckMessage(c, sendMessages, "The creature does not seem interested.");
                return false;
            }
        }
        if (npc.getNpcId() == 9032) {//bear
            if (!(c.getSlayer().getTask().isPresent()) || (c.getSlayer().getTask().isPresent() && !c.getSlayer().getTask().get().getPrimaryName().equals("crystalline bear"))) {
                sendCheckMessage(c, sendMessages, "The creature does not seem interested.");
                return false;
            }
        }
        if (npc.getNpcId() == 9033) {//dragon
            if (!(c.getSlayer().getTask().isPresent()) || (c.getSlayer().getTask().isPresent() && !c.getSlayer().getTask().get().getPrimaryName().equals("crystalline dragon"))) {
                sendCheckMessage(c, sendMessages, "The creature does not seem interested.");
                return false;
            }

        }
        if (npc.getNpcId() == 9034) {//darkbeast
            if (!(c.getSlayer().getTask().isPresent()) || (c.getSlayer().getTask().isPresent() && !c.getSlayer().getTask().get().getPrimaryName().equals("crystalline dark beast"))) {
                sendCheckMessage(c, sendMessages, "The creature does not seem interested.");
                return false;
            }
        }
        if (npc.getNpcId() == 6611 || npc.getNpcId() == 6612) {
            List<NPC> minion = Arrays.asList(NPCHandler.npcs);
            if (minion.stream().filter(Objects::nonNull).anyMatch(n -> n.getNpcId() == Npcs.SKELETON_HELLHOUND && !n.isDead() && n.getHealth().getCurrentHealth() > 0)) {
                sendCheckMessage(c, sendMessages, "You must kill Vet'ions minions before attacking him.");
                return false;
            }
        }

        if (npc.getNpcId() != 5890 && npc.getNpcId() != 5916) {
            if ((c.underAttackByPlayer > 0 || c.underAttackByNpc > 0) && c.underAttackByNpc != npc.getIndex() && !c.getPosition().inMulti()) {
                sendCheckMessage(c, sendMessages, "I am already under attack.");
                return false;
            }
        }
        if (npc.spawnedBy != c.getIndex() && npc.spawnedBy > 0 && !Boundary.isIn(c, Boundary.XERIC)) {
            sendCheckMessage(c, sendMessages, "This monster was not spawned for you.");
            return false;
        }
        if (c.getX() == npc.getX() && c.getY() == npc.getY()) {
            c.getPA().walkTo(0, 1);
        }

        if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS) && !Boundary.isIn(c, Boundary.GODWARS_BOSSROOMS)) {
            sendCheckMessage(c, sendMessages, "You cannot attack that npc from outside the room.");
            return false;
        }
        int npcType = npc.getNpcId();
        if (npcType == 2463 || npcType == 2464) {
            if (Boundary.isIn(c, WarriorsGuild.CYCLOPS_BOUNDARY)) {
                if (!c.getWarriorsGuild().isActive()) {
                    sendCheckMessage(c, sendMessages, "You cannot attack a cyclops without talking to kamfreena.");
                    return false;
                }
            }
        }

        return true;
    }

}
