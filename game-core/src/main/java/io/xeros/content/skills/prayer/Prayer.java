package io.xeros.content.skills.prayer;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import io.xeros.Server;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.EquipmentSet;
import io.xeros.util.Misc;

/**
 * A class that manages a single player training the {@code Skill.PRAYER} skill.
 * 
 * @author Jason MacKeigan
 * @date Mar 10, 2015, 2015, 2:48:38 AM
 */
public class Prayer {

	public static final int RESTORE_PRAYER_BONES = 1;
	public static final int RESTORE_PRAYER_BIG_BONES = 2;
	public static final int RESTORE_PRAYER_BABY_DRAG_WYRM = 3;
	public static final int RESTORE_PRAYER_DRAGON_WYVERN_HYDRA_DRAKE_DAGGANOTH = 4;
	public static final int RESTORE_PRAYER_SUPER_DRAGON = 5;

	/**
	 * The current bone being used on the altar
	 */
	private Optional<Bone> altarBone = Optional.empty();

	/**
	 * The time that must pass before two bones can be buried consecutively.
	 */
	private static final int BURY_DELAY = 1_200;

	/**
	 * A set of all bones that cannot be modified at any time to ensure consistency
	 */
	private static final Set<Bone> BONES = Collections.unmodifiableSet(EnumSet.allOf(Bone.class));

	/**
	 * The player that will be training the {@code Skill.PRAYER} skill.
	 */
	private final Player player;

	/**
	 * Tracks the time in milliseconds of the last bury or use of bone on an altar
	 */
	private final Stopwatch lastAction = Stopwatch.createStarted();

	/**
	 * Creates a new class that will manage training the prayer skill for an individual player.
	 * 
	 * @param player the player training the skill
	 */
	public Prayer(Player player) {
		this.player = player;
	}

	/**
	 * Attempts to bury a single bone into the dirt
	 * 
	 * @param bone the bone being burried
	 */
	public void bury(Bone bone) {
		player.getPA().stopSkilling();
		if (!player.getItems().playerHasItem(bone.getItemId())) {
			return;
		}
		if (lastAction.elapsed(TimeUnit.MILLISECONDS) < BURY_DELAY) {
			return;
		}
		if (bone.getItemId() == 536) {
			player.getEventCalendar().progress(EventChallenge.BURY_X_DRAGON_BONES);
		}
		ItemDef definition = ItemDef.forId(bone.getItemId());
		player.sendMessage("You bury the " + (definition == null ? "bone" : definition.getName()) + ".");
		player.getPA().addSkillXPMultiplied(bone.getExperience() * (Boundary.isIn(player, Boundary.LAVA_DRAGON_ISLE) && bone.getItemId() == 11943 ? 4 : 1), Skill.PRAYER.getId(), true);
		player.getItems().deleteItem2(bone.getItemId(), 1);
		player.startAnimation(827);
		player.getPA().sendSound(2738);
		lastAction.reset();
		lastAction.start();
		onBonesBuriedOrCrushed(bone, false);
	}

	public void alter(final int amount, int objectX, int objectY) {
		if (!altarBone.isPresent()) {
			return;
		}
		Bone bone = altarBone.get();
		player.boneOnAltar = false;
		player.getPA().stopSkilling();
		if (!player.getItems().playerHasItem(bone.getItemId())) {
			return;
		}
		if (lastAction.elapsed(TimeUnit.MILLISECONDS) < BURY_DELAY) {
			return;
		}
		ItemDef definition = ItemDef.forId(bone.getItemId());
		player.getPA().stillGfx(624, objectX, objectY, player.heightLevel, 1);
		player.getPA().addSkillXPMultiplied(bone.getExperience() * 3, Skill.PRAYER.getId(), true);
		player.getItems().deleteItem2(bone.getItemId(), 1);
		player.startAnimation(3705);
		lastAction.reset();
		lastAction.start();
		Server.getEventHandler().submit(new Event<Player>("skilling", player, 3) {
			int remaining = amount - 1;

			@Override
			public void execute() {
				int chance = Misc.random(2) + 1;
				if (player == null || player.isDisconnected() || player.getSession() == null) {
					super.stop();
					return;
				}
				if (!player.getItems().playerHasItem(bone.getItemId())) {
					super.stop();
					player.sendMessage("You have run out of " + (definition == null ? "bones" : definition.getName()) + ".");
					return;
				}
				if (remaining <= 0) {
					super.stop();
					return;
				}
				remaining--;
				player.facePosition(player.objectX, player.objectY);
				player.getPA().stillGfx(624, objectX, objectY, player.heightLevel, 1);
				player.getPA().addSkillXPMultiplied(bone.getExperience() * 3, Skill.PRAYER.getId(), true);
				if (player.getPosition().inWild() && chance == 1) {
					player.getItems().addItem(bone.getItemId(), 1);
					player.sendMessage("@red@The god of chaos smiles on you and returns your sacrifice.");
				}
				player.getItems().deleteItem2(bone.getItemId(), 1);
				player.startAnimation(3705);
				lastAction.reset();
				lastAction.start();
			}

		});
	}

	public void onBonesBuriedOrCrushed(Bone bone, boolean crushed) {
		if (crushed && Boundary.CATACOMBS.in(player) || EquipmentSet.isWearing(player, EquipmentSet.DRAGONBONE_NECKLACE)) {
			player.restore(Skill.PRAYER, bone.getPrayerRestore());
		}
	}

	/**
	 * The bone last used on the altar
	 * 
	 * @return the bone on the altar
	 */
	public Optional<Bone> getAltarBone() {
		return altarBone;
	}

	/**
	 * Modifies the last bone used on the altar to the parameter
	 * 
	 * @param altarBone the bone on the altar
	 */
	public void setAltarBone(Optional<Bone> altarBone) {
		this.altarBone = altarBone;
	}

	/**
	 * Determines if the {@code itemId} matches any of the {@link Bone} itemId values.
	 * 
	 * @param itemId the item id we're comparing
	 * @return {@code true} if a bone exists with a matching itemId.
	 */
	public static Optional<Bone> isOperableBone(int itemId) {
		return BONES.stream().filter(bone -> bone.getItemId() == itemId).findFirst();
	}
}
