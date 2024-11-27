package io.xeros.content.combat.formula;

import io.xeros.Configuration;
import io.xeros.content.combat.magic.CombatSpellData;
import io.xeros.content.skills.Skill;
import io.xeros.model.entity.Bonus;
import io.xeros.model.entity.CombatType;
import io.xeros.model.definition.Items;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class MagicMaxHit {

	public static int mageAttack(Player c) {
		int magicLevel = CombatFormula.getPrayerBoostedLevel(c.getLevel(Skill.MAGIC), CombatFormula.getPrayerMagicAccuracyBonus(c));
		double modifier = 1.00;
		if (c.fullVoidMage()) {
			modifier += .45;
		}

		if (c.npcAttackingIndex > 0 && c.getSlayer().getTask().isPresent()) {
			NPC npc = NPCHandler.npcs[c.npcAttackingIndex];
			if (npc != null) {
				// Salve amulet
				if (c.getItems().isWearingItem(12018, Player.playerAmulet) && Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) != -1) {
					modifier += .20;
				// Slayer helmet
				} else if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MAGE)) {
					modifier += .15;
				}
			}
		}

		magicLevel *= modifier * 1.7;
		magicLevel += CombatFormula.EFFECTIVE_LEVEL_BOOST;
		return CombatFormula.getEffectLevel(magicLevel, c.getItems().getBonus(Bonus.ATTACK_MAGIC));
	}

	public static int mageDefence(Player c) {
		double prayerDefence = CombatFormula.getPrayerDefenceBonus(c);
		double defence = Math.floor(c.playerLevel[1] * .3);
		double magicDefence = Math.floor(c.playerLevel[6] * .7);
		//defence += magicDefence + c.getItems().getBonus(Bonus.DEFENCE_MAGIC);
		//return (int) defence;
		return CombatFormula.getEffectLevel((int) ((magicDefence + defence) * prayerDefence), c.getItems().getBonus(Bonus.DEFENCE_MAGIC));
	}

	public static int getNightmareSpecialMaxHit(int magicLevel, int base) {
		double modifier = (double) magicLevel / 75d;
		return (int) Math.floor(base * modifier);
	}

	public static int magiMaxHit(Player c) {
		if (c.oldSpellId <= -1) {
			return 0;
		}
		double damage = CombatSpellData.MAGIC_SPELLS[c.oldSpellId][6];
		double damageMultiplier = 1.0 + ((double) c.getItems().getBonus(Bonus.MAGIC_DMG) / 100d);


		switch (c.playerEquipment[Player.playerWeapon]) {
			case 24424://volatile
				if (c.getCombatItems().usingNightmareStaffSpecial()) {
					damage = getNightmareSpecialMaxHit(c.playerLevel[Skill.MAGIC.getId()], 44); // lowered to 44 from 50
				}
				break;
			case 24425://eldritch
				if (c.getCombatItems().usingNightmareStaffSpecial()) {
					damage = getNightmareSpecialMaxHit(c.playerLevel[Skill.MAGIC.getId()], 39);
				}
				break;
		}

		if (c.npcAttackingIndex > 0) {
			NPC npc = NPCHandler.npcs[c.npcAttackingIndex];
			if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MAGE)) {
				damageMultiplier += .15;
			}
		}
		NPC npc = NPCHandler.npcs[c.npcAttackingIndex];
		if (c.getItems().isWearingItem(12018, Player.playerAmulet) && Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) != -1) {
			damageMultiplier += .20;
		}
		boolean hasDarkVersion = (c.petSummonId == 30117 || c.petSummonId == 30120 || c.petSummonId == 30122);

		if (c.hasFollower
				&& ((c.petSummonId == 30017 || c.petSummonId == 30020 || c.petSummonId == 30022))
				|| (hasDarkVersion)){
			if (hasDarkVersion) {
				damageMultiplier += .10;
			} else if (Misc.random(1) == 1) {
				damageMultiplier += .10;
			}
		}
		if (c.oldSpellId > -1) {
			switch (CombatSpellData.MAGIC_SPELLS[c.oldSpellId][0]) {
				case 12037:
					if (c.getItems().isWearingAnyItem(Items.SLAYERS_STAFF_E) && c.getSlayer().getTask().isPresent()) {

						//NPC npc = NPCHandler.npcs[c.npcAttackingIndex];
						if (npc != null && c.getSlayer().getTask().get().matches(npc.getDefinition().getName())) {
							damage += (c.playerLevel[6] / 10) + 1;
						}
					} else {
						damage += c.playerLevel[6] / 14;
					}
					break;
			}

			damage *= damageMultiplier;
			return (int) damage;
		}

		return 0;
	}
}
