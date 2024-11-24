package io.xeros.content.combat.formula;

import com.google.common.base.Preconditions;
import io.xeros.Configuration;
import io.xeros.model.entity.Bonus;
import io.xeros.model.entity.CombatType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class MagicSuccessFormula {

    public static int getHitChance(Player attacker, Entity defender) {
        Player defenderPlayer = defender.isPlayer() ? defender.asPlayer() : null;
        NPC defenderNpc = defender.isNPC() ? defender.asNPC() : null;
        Preconditions.checkState(defenderPlayer != null || defenderNpc != null, "Defender is neither npc or player");

        // Attacker
        double attackerPrayerBonus = CombatFormula.getPrayerMagicAccuracyBonus(attacker);
        double attackerEquipmentBonus = attacker.getItems().getBonus(Bonus.ATTACK_MAGIC);
        double attackerMagicLevel = attacker.playerLevel[6];
        double attackerEffectiveLevel = Math.floor(attackerMagicLevel * attackerPrayerBonus) + CombatFormula.EFFECTIVE_LEVEL_BOOST;

        // Void mage
        if (attacker.fullVoidMage()) {
            attackerEffectiveLevel *= 1.3;
        }

        // Slayer helmet and salve ammy
        if (attacker.npcAttackingIndex > 0 && attacker.getSlayer().getTask().isPresent()) {
            NPC npc = NPCHandler.npcs[attacker.npcAttackingIndex];
            if (npc != null) {
                // Salve amulet
                if (attacker.getItems().isWearingItem(12018, Player.playerAmulet) && Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) != -1) {
                    attackerEffectiveLevel *= 1.20;
                    // Slayer helmet
                } else if (attacker.getSlayer().hasSlayerHelmBoost(npc, CombatType.MAGE)) {
                    attackerEffectiveLevel *= 1.15;
                }
            }
        }


        double attackRoll = Math.floor(attackerEffectiveLevel * (1 + attackerEquipmentBonus / 64.0));


        // Defender equipment bonus
        double defenderEquipmentBonus;
        if (defenderPlayer != null)
            defenderEquipmentBonus = defenderPlayer.getItems().getBonus(Bonus.DEFENCE_MAGIC);
        else
            defenderEquipmentBonus = defenderNpc.getDefenceBonus(CombatType.MAGE, attacker);

        // Defender defence level
        double defenderDefenceLevel;
        if (defenderPlayer != null)
            defenderDefenceLevel = CombatFormula.getPrayerBoostedDefenceLevel(defenderPlayer); // Formula doesn't mention defender prayer bonus at all
        else
            defenderDefenceLevel = defenderNpc.getDefenceLevel();

        // Defender magic level
        double defenderMagicLevel;
        if (defenderPlayer != null)
            defenderMagicLevel = defenderPlayer.playerLevel[6];
        else
            defenderMagicLevel = defenderNpc.getNpcStats().getMagicLevel();

        double defenderEffectiveLevel = Math.floor(defenderMagicLevel * 0.7) + Math.floor(defenderDefenceLevel * 0.3);
        double defenceRoll = Math.floor((defenderEffectiveLevel + 8) * (1 + defenderEquipmentBonus / 64.0));

        double roll;
        if (attackRoll < defenceRoll)
            roll = (attackRoll - 1) / (2 * defenceRoll);
        else
            roll = 1 - (defenceRoll + 1) / (2 * attackRoll);

        int hitPercentage = (int) Math.ceil(roll * 100);
        if (attacker.isPrintAttackStats()) {
            String name = defenderPlayer != null ? defenderPlayer.getDisplayName() : defenderNpc.getName();
            attacker.sendMessage(Misc.replaceBracketsWithArguments("Magic {}, {}% Chance, aLevel {}, dLevel {}, aRoll {}, dRoll {}",
                    name, hitPercentage, attackerEffectiveLevel, defenderEffectiveLevel, attackRoll, defenceRoll));
        }
        return hitPercentage;
    }

    /**
     * Is magic attack a successful hit?
     *
     * Source is <a href="https://oldschool.runescape.wiki/w/Magic#Magic_accuracy">Wiki</a>
     *
     * @param attacker the attacker
     * @param defender the defender
     * @return <code>true</code> for successful hit
     */
    public static boolean isHit(Player attacker, Entity defender) {
        int hitChance = getHitChance(attacker, defender);

        if (defender.isPlayer()) {
            Player defenderPlayer = defender.asPlayer();
            if (defenderPlayer.isPrintDefenceStats())
                defenderPlayer.sendMessage("Magic block chance: " + (100 - hitChance) + "%");
        }

        return Misc.random(0, 100) <= hitChance;
    }

}
