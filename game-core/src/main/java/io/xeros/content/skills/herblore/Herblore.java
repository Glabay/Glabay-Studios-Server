package io.xeros.content.skills.herblore;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import io.xeros.content.SkillcapePerks;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.impl.ArdougneDiaryEntry;
import io.xeros.content.achievement_diary.impl.DesertDiaryEntry;
import io.xeros.content.achievement_diary.impl.FremennikDiaryEntry;
import io.xeros.content.achievement_diary.impl.KandarinDiaryEntry;
import io.xeros.content.achievement_diary.impl.KaramjaDiaryEntry;
import io.xeros.content.achievement_diary.impl.VarrockDiaryEntry;
import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public class Herblore {

	/**
	 * A {@link Set} of all {@link Herb} elements from it's respective enumeration.
	 */
	public static final Set<Herb> HERBS = Collections.unmodifiableSet(EnumSet.allOf(Herb.class));

	/**
	 * A {@link Set} of all {@link Potion} elements from it's respective enumeration.
	 */
	private static final Set<PotionData.FinishedPotions> FINISHED = Collections.unmodifiableSet(EnumSet.allOf(PotionData.FinishedPotions.class));

	/**
	 * The player that will be operating this skill
	 */
	private final Player player;

	/**
	 * A class for managing herblore operation
	 * 
	 * @param player the player
	 */
	public Herblore(Player player) {
		this.player = player;
	}

	/**
	 * Cleans a single her
	 * 
	 * @param itemId the herb attempting to be cleaned
	 */
	public void clean(int itemId) {
		Optional<Herb> herb = HERBS.stream().filter(h -> h.getGrimy() == itemId).findFirst();
		herb.ifPresent(h -> {
			player.getPA().stopSkilling();
			if (!player.getItems().playerHasItem(h.getGrimy())) {
				player.sendMessage("You need the grimy herb to do this.");
				return;
			}
			if (player.playerLevel[Skill.HERBLORE.getId()] < h.getLevel()) {
				player.sendMessage("You need a herblore level of " + h.getLevel() + " to clean this grimy herb.");
				return;
			}
			ItemDef definition = ItemDef.forId(h.getClean());
			player.getPA().addSkillXPMultiplied(h.getExperience(), Skill.HERBLORE.getId(), true);
			player.getItems().deleteItem2(h.getGrimy(), 1);
			player.getItems().addItem(h.getClean(), 1);
			player.sendMessage("You identify the herb as " + definition.getName() + ".");
		});
	}

	public void mix(int primary) {
		Optional<PotionData.FinishedPotions> potion = FINISHED.stream().filter(p -> p.getPrimary().id() == primary && containsSecondaries(p)).findFirst();
		potion.ifPresent(p -> {
			
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (player == null || player.isDisconnected() || player.getSession() == null) {
						onStopped();
						return;
					}
					if (player.getItems().playerHasItem(p.getPrimary().id(), p.getPrimary().amount()) && containsSecondaries(p)) {
					player.getPA().stopSkilling();
						ItemDef definition = ItemDef.forId(p.getResult().id());
					String name = definition != null && definition.getName() != null ? definition.getName() : "unknown";
					if (player.playerLevel[Skill.HERBLORE.getId()] < p.getLevel()) {
						player.sendMessage("You need a herblore level of " + p.getLevel() + " to make " + (definition != null ? definition.getName() : "potion") + ".");
						container.stop();
						return;
					}
					player.startAnimation(363);
					Arrays.asList(p.getIngredients()).stream().forEach(ing -> player.getItems().deleteItem2(ing.id(), ing.amount()));
					
					/**
					 * Chance of saving a herb while wearing herblore or max cape
					 */
					if (SkillcapePerks.HERBLORE.isWearing(player) || SkillcapePerks.isWearingMaxCape(player)) {
						if (Misc.random(4) == 2) {
							player.sendMessage("You manage to save an ingredient.");
						} else {
							player.getItems().deleteItem2(p.getPrimary().id(), p.getPrimary().amount());
						}
					} else {
						player.getItems().deleteItem2(p.getPrimary().id(), p.getPrimary().amount());
					}
					
					player.getItems().addItem(p.getResult().id(), p.getResult().amount());
					player.getPA().addSkillXPMultiplied(p.getExperience(), Skill.HERBLORE.getId(), true);
					player.sendMessage("You combine all of the ingredients and make a " + name + ".");
					Achievements.increase(player, AchievementType.HERB, 1);
					switch (p) {
					case SUPER_DEFENCE:
						if (Boundary.isIn(player, Boundary.RELLEKKA_BOUNDARY)) {
							player.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.MIX_SUPER_DEFENCE);
						}
						break;
					case SUPER_COMBAT_3:
					case SUPER_COMBAT_4:
						if (Boundary.isIn(player, Boundary.ARDOUGNE_ZOO_BRIDGE_BOUNDARY)) {
							player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.SUPER_COMBAT_ARD);
						}
						if (Boundary.isIn(player, Boundary.VARROCK_BOUNDARY)) {
							player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.SUPER_COMBAT);
						}
						break;
					case ANTI_VENOM_4:
					case ANTI_VENOM_3:
					case ANTI_VENOM_2:
					case ANTI_VENOM_1:
						if (Boundary.isIn(player, Boundary.BRIMHAVEN_BOUNDARY)) {
							player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.ANTI_VENOM);
						}
						break;
					case WEAPON_POISON_PLUS_PLUS:
						if (Boundary.isIn(player, Boundary.CATHERBY_BOUNDARY)) {
							player.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.WEAPON_POISON_PLUS_PLUS);
						}
						break;
					case COMBAT:
						if (Boundary.isIn(player, Boundary.DESERT_BOUNDARY)) {
							player.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.COMBAT_POTION);
						}
						break;
					default:
						break;
					}
					} else {
						player.sendMessage("You have run out of supplies to do this.");
						container.stop();
					}
				}
				@Override
				public void onStopped() {
				}
			}, 2);
		});
	}
	
	public boolean makeUnfinishedPotion(final Player player, final GameItem itemUsed) {
		final PotionData.UnfinishedPotions unf = PotionData.UnfinishedPotions.forHerb(itemUsed.id());
		if (unf == null) {
			return false;
		}
		if (player.getLevelForXP(player.playerXP[Player.playerHerblore]) < unf.getLevelReq()) {
			player.sendMessage("You need a Herblore level of " + unf.getLevelReq() + " to make this potion.");
			return false;
		}
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (player == null || player.isDisconnected() || player.getSession() == null) {
					onStopped();
					return;
				}
				if (player.getItems().playerHasItem(227) && player.getItems().playerHasItem(unf.getHerb().id())) {
					player.getItems().deleteItem(227, player.getItems().getInventoryItemSlot(227), 1);
					player.getItems().deleteItem2(unf.getHerb().id(), 1);
					player.getItems().addItem(unf.getPotion().id(), 1);
					player.sendMessage("You put the " + ItemDef.forId(unf.getHerb().id()).getName() + " into the vial of water and create a "
							+ ItemDef.forId(unf.getPotion().id()).getName() + ".");
				} else {
					player.sendMessage("You have run out of supplies to do this.");
					container.stop();
				}
			}
			@Override
			public void onStopped() {
			}
		}, 1);
		return false;
	}

	/**
	 * Determines if the player has all of the ingredients required for the potion.
	 * 
	 * @param p the potion we're determining this for
	 * @return {@code true} if we have all of the ingredients, otherwise {@code false}
	 */
	private boolean containsSecondaries(PotionData.FinishedPotions p) {
		int required = p.getIngredients().length;

		for (GameItem ingredient : p.getIngredients()) {
			if (player.getItems().playerHasItem(ingredient.id(), ingredient.amount())) {
				required--;
			}
		}
		return required == 0;
	}

	public void crushItem(int itemid) {

	}
}
