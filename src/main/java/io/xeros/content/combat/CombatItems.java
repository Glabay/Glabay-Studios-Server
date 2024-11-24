package io.xeros.content.combat;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import io.xeros.content.combat.melee.MeleeExtras;
import io.xeros.content.combat.weapon.CombatStyle;
import io.xeros.content.items.OrnamentedItem;
import io.xeros.model.CombatType;
import io.xeros.model.Graphic;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatItems {

	private static final Logger logger = LoggerFactory.getLogger(CombatItems.class);

	public static final Set<Integer> SLAYER_HELMETS_REGULAR = Set.of(
			Items.BLACK_MASK,
			Items.SLAYER_HELMET,
			OrnamentedItem.TWISTED_SLAYER_HELMET.getOrnamentedItem(),
			OrnamentedItem.HYDRA_SLAYER_HELMET.getOrnamentedItem(),
			OrnamentedItem.KBD_SLAYER_HELMET.getOrnamentedItem(),
			OrnamentedItem.KQ_SLAYER_HELMET.getOrnamentedItem(),
			OrnamentedItem.ABYSSAL_SLAYER_HELMET.getOrnamentedItem(),
			OrnamentedItem.DARK_CLAW_SLAYER_HELMET.getOrnamentedItem(),
			OrnamentedItem.VORKATH_SLAYER_HELMET.getOrnamentedItem()
	);

	public static final Set<Integer> SLAYER_HELMETS_IMBUED = Set.of(
			Items.BLACK_MASK_I,
			Items.SLAYER_HELMET_I,
			OrnamentedItem.TWISTED_SLAYER_HELMET_I.getOrnamentedItem(),
			OrnamentedItem.HYDRA_SLAYER_HELMET_I.getOrnamentedItem(),
			OrnamentedItem.KBD_SLAYER_HELMET_I.getOrnamentedItem(),
			OrnamentedItem.KQ_SLAYER_HELMET_I.getOrnamentedItem(),
			OrnamentedItem.ABYSSAL_SLAYER_HELMET_I.getOrnamentedItem(),
			OrnamentedItem.DARK_CLAW_SLAYER_HELMET_I.getOrnamentedItem(),
			OrnamentedItem.VORKATH_SLAYER_HELMET_I.getOrnamentedItem()
	);

	/**
	 * Charged is [x][0], uncharged is [x][1].
	 */
	public static final int[][] MUTAGEN_HELMETS = { { 12931, 12929 }, { 13199, 13198 }, { 13197, 13196 } };

	private final Player player;

	public CombatItems(Player player) {
		this.player = player;
	}

	public boolean hasArcLight() {
		return this.player.getArcLightCharge() > 0 && player.getItems().isWearingItem(Items.ARCLIGHT, Player.playerWeapon);
	}

	public boolean hasTomeOfFire() {
		return player.getItems().isWearingItem(Items.TOME_OF_FIRE, Player.playerShield) && player.getTomeOfFire().hasCharges();
	}

	public static int getUnchargedSerpentineHelmet(int chargedHelmetId) {
        for (int[] mutagenHelmet : MUTAGEN_HELMETS) {
            if (mutagenHelmet[0] == chargedHelmetId)
                return mutagenHelmet[1];
        }

		logger.error("No uncharged serpentine helmet for id {}", chargedHelmetId);
		return Items.SERPENTINE_HELM_UNCHARGED;
	}

	/**
	 * The armour has a set effect in which each piece of the set boosts damage and accuracy by 0.5% when using the crush attack style.
	 * If all three pieces are worn, an additional 1.0% bonus is added for a total of 2.5% accuracy and damage bonus.
	 */
	public double getInquisitorBonus() {
		if (player.attacking.getCombatType() == CombatType.MELEE && player.getCombatConfigs().getWeaponMode().getCombatStyle() == CombatStyle.CRUSH) {
			double bonus = 100;
			int[] items = {Items.INQUISITORS_GREAT_HELM, Items.INQUISITORS_HAUBERK, Items.INQUISITORS_PLATESKIRT};
			long wearingCount = Arrays.stream(items).filter(item -> player.getItems().isWearingItem(item)).count();
			bonus += wearingCount * 0.5;
			if (wearingCount >= 3)
				bonus += 1;
			return bonus / 100d;
		}
		else return 1.0;
	}

	public boolean elyProc() {
		if (player.playerEquipment[Player.playerShield] == 12817 && Misc.trueRand(10) <= 6) {
			player.startGraphic(new Graphic(321));
			return true;
		}
		return false;
	}

	public boolean doQueuedGraniteMaulSpecials() {
		if (player.graniteMaulSpecialCharges > 0) {
			if (player.npcAttackingIndex != 0) {
				NPC npc = NPCHandler.npcs[player.npcAttackingIndex];
				if (!player.goodDistance(player.getX(), player.getY(), npc.getX(), npc.getY(), player.attacking.getRequiredDistance())) {
					return false;
				}
			}
			else if (player.playerAttackingIndex != 0) {
				Player target = PlayerHandler.players[player.playerAttackingIndex];
				if (target != null && !player.goodDistance(player.getX(), player.getY(), target.getX(), target.getY(), player.attacking.getRequiredDistance())) {
					return false;
				}
			}

			boolean spec = player.graniteMaulSpecialCharges > 0;
			while (player.graniteMaulSpecialCharges != 0) {
				MeleeExtras.graniteMaulSpecial(player, false);
				player.graniteMaulSpecialCharges--;
			}

			return spec;
		}

		return false;
	}

	public void absorbDragonfireDamage() {
		int shieldId = player.playerEquipment[Player.playerShield];
		var shieldName = ItemAssistant.getItemName(shieldId).toLowerCase();
		if (shieldName.contains("dragonfire")) {
			int charges = player.getDragonfireShieldCharge();
			if (charges < 50) {
				player.setDragonfireShieldCharge(charges++);
				if (charges == 50)
					player.sendMessage("<col=255>Your dragonfire shield has completely finished charging.");
				player.startAnimation(6695);
				player.gfx0(1164);
				player.setDragonfireShieldCharge(charges);
            }
		}
	}

	/**
	 * Determines if the player is wearing a crawsbow
	 * @return True if the player is wearing a crawsbow
	 */
	public boolean wearingCrawsBow() {
		return player.getItems().getWeapon() == Items.CRAWS_BOW;
	}

	/**
	 * Determines if the player is wearing a chainmace
	 * @return True if the player is wearing a chainmace
	 */
	public boolean wearingViggorasChainmace() {
		return player.getItems().getWeapon() == Items.VIGGORAS_CHAINMACE;
	}

	/**
	 * Determines if the player is wearing a sceptre
	 * @return True if the player is wearing a sceptre
	 */
	public boolean wearingThammaronsSceptre() {
		return player.getItems().getWeapon() == Items.THAMMARONS_SCEPTRE;
	}

	public boolean usingNightmareStaffSpecial() {
		int weapon = player.playerEquipment[3];
		return weapon == 24424 && player.usingSpecial && !player.usingClickCast;
	}

	public boolean usingEldritchStaffSpecial() {
		int weapon = player.playerEquipment[3];
		return weapon == 24425 && player.usingSpecial && !player.usingClickCast;
	}

	public boolean usingCrystalBow() {
		return player.playerEquipment[Player.playerWeapon] >= 4212 && player.playerEquipment[Player.playerWeapon] <= 4223;
	}

	public boolean usingCrawsBow() {
		return player.playerEquipment[Player.playerWeapon] == Items.CRAWS_BOW;
	}

	public boolean usingBlowPipe() {
		return player.playerEquipment[Player.playerWeapon] == Items.TOXIC_BLOWPIPE;
	}

	public boolean usingTwistedBow() {
		return player.playerEquipment[Player.playerWeapon] == Items.TWISTED_BOW;
	}

	public boolean usingDbow() {
		return player.playerEquipment[Player.playerWeapon] == 11235 || player.playerEquipment[Player.playerWeapon] == 12765 || player.playerEquipment[Player.playerWeapon] == 12766
				|| player.playerEquipment[Player.playerWeapon] == 12767 || player.playerEquipment[Player.playerWeapon] == 12768;
	}

	public boolean properBolts() {
		int i = player.playerEquipment[Player.playerArrows];
		return (i >= 9139 && i <= 9145) || (i >= 9236 && i <= 9245) || (i >= 21924 && i <= 21974)
				|| (i >= 9335 && i <= 9341) || i == 11875 || i == 21905 || i == 21906 ||i == 21316;
	}

	public boolean usingJavelins(int i) {
		return (i >= 825 && i <= 830) || i == 19484 || i == 21318;
	}
	
	public void checkDemonItems() {
		if (player.getItems().isWearingItem(19675, Player.playerWeapon)) {
			player.setArcLightCharge(player.getArcLightCharge() - 1);
			if (player.getArcLightCharge() <= 0) {
				player.setArcLightCharge(0);
				player.sendMessage("Your arclight has lost all charge.");
				player.getItems().equipItem(-1, 0, Player.playerWeapon);
				player.getItems().addItemUnderAnyCircumstance(19675, 1);
			}
		}
	}

	public boolean usingAssembler() {
		return player.getItems().isWearingItem(Items.ASSEMBLER_MAX_CAPE)
				|| player.getItems().isWearingItem(Items.AVAS_ASSEMBLER);
	}

	public boolean usingAccumulator() {
		return player.getItems().isWearingItem(Items.AVAS_ACCUMULATOR)
				|| player.getItems().isWearingItem(Items.ACCUMULATOR_MAX_CAPE)
				|| player.getItems().isWearingItem(Items.RANGING_CAPE)
				|| player.getItems().isWearingItem(Items.RANGE_MASTER_CAPE);
	}

	public boolean consumeDart() {
		int chance = usingAccumulator() ? 72 : usingAssembler() ? 80 : 0;
		if (chance == 0)
			return true;
		return Misc.isLucky(100 - chance);
	}

	public boolean consumeScale() {
		return Misc.random(3) == 1;
	}

	public void checkBlowpipeShotsRemaining() {
		if (player.getToxicBlowpipeAmmo() != 0) {
			player.sendMessage("The blowpipe has %d %s and %d scales remaining.".formatted(player.getToxicBlowpipeAmmoAmount(), ItemDef.forId(player.getToxicBlowpipeAmmo()).getName(), player.getToxicBlowpipeCharge()));
		} else {
			player.sendMessage("The blowpipe has no ammo and %d scales remaining.".formatted(player.getToxicBlowpipeCharge()));
		}
	}

	public void checkBlowpipe() {
		if (player.getItems().isWearingItem(Items.TOXIC_BLOWPIPE, Player.playerWeapon)) {
			if (consumeDart())
				player.setToxicBlowpipeAmmoAmount(player.getToxicBlowpipeAmmoAmount() - 1);

			if (consumeScale())
				player.setToxicBlowpipeCharge(player.getToxicBlowpipeCharge() - 1);

			if (player.getToxicBlowpipeAmmoAmount() % 500 == 0 && player.getToxicBlowpipeAmmoAmount() > 0)
				player.sendMessage("<col=255>You have %d ammo in your blow pipe remaining.</col>".formatted(player.getToxicBlowpipeAmmoAmount()));

			if (player.getToxicBlowpipeAmmoAmount() <= 0 && player.getToxicBlowpipeCharge() <= 0) {
				player.sendMessage("Your toxic blowpipe has lost all charge.");
				player.getItems().equipItem(-1, 0, 3);
				player.getItems().addItemUnderAnyCircumstance(12924, 1);
				player.setToxicBlowpipeAmmo(0);
				player.setToxicBlowpipeAmmoAmount(0);
				player.setToxicBlowpipeCharge(0);
			}
		}
	}

	public void checkCombatTickBasedItems() {
		if (player.serpHelmCombatTicks > 0 && (player.serpHelmCombatTicks % 8 == 0)) {
			player.serpHelmCombatTicks = 0;
			for (int[] helmets : MUTAGEN_HELMETS) {
				int charged = helmets[0];
				int uncharged = helmets[1];
				if (player.getItems().isWearingItem(charged) && player.getItems().getWornItemSlot(charged) == Player.playerHat) {
					player.setSerpentineHelmCharge(player.getSerpentineHelmCharge() - 1);
					if (player.getSerpentineHelmCharge() % 500 == 0 && player.getSerpentineHelmCharge() != 0) {
						player.sendMessage("<col=255>You have %d charges remaining in your serpentine helm.</col>".formatted(player.getSerpentineHelmCharge()));
					}
					if (player.getSerpentineHelmCharge() <= 0) {
						player.sendMessage("Your serpentine helm has lost all of it's charge.");
						player.getItems().equipItem(-1, 0, Player.playerHat);
						player.getItems().addItemUnderAnyCircumstance(uncharged, 1);
						player.setSerpentineHelmCharge(0);
					}
				}
			}
		}
	}

	public void checkVenomousItems(Entity target) {
		if (player.getItems().isWearingItem(12904, Player.playerWeapon)) {
			player.setToxicStaffOfTheDeadCharge(player.getToxicStaffOfTheDeadCharge() - 1);
			if (player.getToxicStaffOfTheDeadCharge() <= 0) {
				player.setToxicStaffOfTheDeadCharge(0);
				player.sendMessage("Your toxic staff of the dead has lost all charge.");
				player.getItems().equipItem(-1, 0, Player.playerWeapon);
				player.getItems().addItemUnderAnyCircumstance(12902, 1);
			}
		}
		if (target.isNPC() && Misc.random(6) == 1) {
			for (int[] helmets : MUTAGEN_HELMETS) {
				int charged = helmets[0];
				if (player.getItems().isWearingItem(charged) && player.getItems().getWornItemSlot(charged) == Player.playerHat)
					if (player.getSerpentineHelmCharge() > 0)
						target.getHealth().proposeStatus(HealthStatus.VENOM, 6, Optional.of(player));
			}
		}
	}
}
