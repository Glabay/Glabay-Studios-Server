package io.xeros.content.skills.fletching;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import io.xeros.Server;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.slayer.SlayerUnlock;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

public class Fletching {

	public static final String BROAD_ARROW_MESSAGE = "You need to unlock the ability to craft broad ammo through Slayer Rewards.";
	
	private static final Set<FletchableGem> GEMS = Collections.unmodifiableSet(EnumSet.allOf(FletchableGem.class));

	private static final Set<FletchableUnfinishedBolt> UNFINISHED_BOLTS = Collections.unmodifiableSet(EnumSet.allOf(FletchableUnfinishedBolt.class));

	private static final Set<FletchableBow> BOWS = Collections.unmodifiableSet(EnumSet.allOf(FletchableBow.class));

	private static final Set<FletchableCrossbow> CROSSBOWS = Collections.unmodifiableSet(EnumSet.allOf(FletchableCrossbow.class));

	public static final Set<FletchableArrow> ARROWS = Collections.unmodifiableSet(EnumSet.allOf(FletchableArrow.class));

	private static final Set<FletchableJavelin> JAVELINS = Collections.unmodifiableSet(EnumSet.allOf(FletchableJavelin.class));

	private static final Set<FletchableDart> DARTS = Collections.unmodifiableSet(EnumSet.allOf(FletchableDart.class));

	private static final Set<FletchableBolt> BOLTS = Collections.unmodifiableSet(EnumSet.allOf(FletchableBolt.class));

	/**
	 * An unmodifiable {@link Set} of {@link FletchableLogGroup} elements
	 */
	private static final Set<FletchableLogGroup> FLETCHABLE_LOG_GROUP = Collections.unmodifiableSet(EnumSet.allOf(FletchableLogGroup.class));
	/**
	 * An array of values that represent the amount of some selection
	 */
	private static final int[] FLETCHABLE_AMOUNTS = { 1, 5, 10, -1 };

	/**
	 * The {@link Player} that will be fletching
	 */
	private final Player player;

	/**
	 * An Optional of type {@link FletchableLogGroup} that will keep track of what group the player has selected for fletching.
	 */
	private Optional<FletchableLogGroup> selectedGroup = Optional.empty();

	/**
	 * The {@link FletchableLog} that the player has selected to fletch
	 */
	private Optional<FletchableLog> selectedFletchable = Optional.empty();

	/**
	 * Creates a new single class to manage fletching operations related to the {@code fletching} skill.
	 * 
	 * @param player the player that will be fletching
	 */
	public Fletching(final Player player) {
		this.player = player;
	}

	public boolean combine(int use, int used) {
		selectedGroup = FLETCHABLE_LOG_GROUP.stream().filter(g -> Arrays.stream(g.getFletchables()).anyMatch(f -> f.getItemId() == use || f.getItemId() == used)).findFirst();
		selectedGroup.ifPresent(group -> {
			FletchableLog[] fletchables = group.getFletchables();
			player.getPA().stopSkilling();
			player.getPA().sendChatboxInterface(8880);
			player.getPA().sendFrame126("What would you like to make?", 8879);
			player.getPA().sendFrame246(8884, 190, fletchables[1].getProduct());
			player.getPA().sendFrame246(8883, 190, fletchables[0].getProduct());
			player.getPA().sendFrame246(8885, 190, fletchables[2].getProduct());
			player.getPA().sendFrame126(ItemAssistant.getItemName(fletchables[0].getProduct()), 8889);
			player.getPA().sendFrame126(ItemAssistant.getItemName(fletchables[1].getProduct()), 8893);
			player.getPA().sendFrame126(ItemAssistant.getItemName(fletchables[2].getProduct()), 8897);
		});
		return selectedGroup.isPresent();
	}

	public void select(int buttonId) {
		selectedGroup.ifPresent(group -> {
			for (FletchableLog fletchable : group.getFletchables()) {
				int index = Misc.linearSearch(fletchable.getButtonIds(), buttonId);
				if (index != -1) {
					int amount = FLETCHABLE_AMOUNTS[index];
					selectedFletchable = Optional.of(fletchable);
					if (amount == -1) {
						player.getPA().sendEnterAmount(0);
						break;
					}
					fletchLog(fletchable, amount);
					break;
				}
			}
		});
	}

	/**
	 * Attempts to cut a log with a knife in hopes to make some secondary item
	 *
	 */
	public void fletchLog(FletchableLog fletchable, int amount) {
		selectedGroup = Optional.empty();
		selectedFletchable = Optional.empty();
		if (!player.getItems().playerHasItem(fletchable.getItemId())) {
			player.sendMessage("You do not have the items required for this.");
			player.getPA().removeAllWindows();
			return;
		}
		if (player.playerLevel[Skill.FLETCHING.getId()] < fletchable.getLevel()) {
			player.sendMessage("You need a fletching level of " + fletchable.getLevel() + " to do this.");
			player.getPA().removeAllWindows();
			return;
		}
		player.startAnimation(1248);
		player.getPA().removeAllWindows();
		Server.getEventHandler().stop(player, "skilling");
		Server.getEventHandler().submit(new FletchLogEvent(player, 3, fletchable, amount));
	}

	public void fletchGem(int use, int used) {
		selectedGroup = Optional.empty();
		selectedFletchable = Optional.empty();
		Optional<FletchableGem> gem = GEMS.stream().filter(g -> g.getGem() == use || g.getGem() == used).findFirst();
		gem.ifPresent(g -> {
			Consumer<Player> make = plr -> {
				if (!player.getItems().playerHasItem(g.getGem())) {
					player.sendMessage("You do not have the items required for this.");
					player.getPA().removeAllWindows();
					return;
				}
				if (player.playerLevel[Skill.FLETCHING.getId()] < g.getLevel()) {
					player.sendMessage("You need a fletching level of " + g.getLevel() + " to do this.");
					player.getPA().removeAllWindows();
					return;
				}

				player.startAnimation(886);
				player.getItems().deleteItem2(g.getGem(), 1);
				player.getItems().addItem(g.getTips(), g.getAmount());
				player.getPA().addSkillXPMultiplied(g.getExperience(), Skill.FLETCHING.getId(), true);
				player.getPA().removeAllWindows();
			};

			if (g == FletchableGem.ONYX) {
				player.start(new DialogueBuilder(player).option("Fletch onyx bolt tips?",
						new DialogueOption("Yes, fletch my Onyx into bolt tips.", make),
						new DialogueOption("No, I want to keep my Onyx.", plr -> plr.getPA().closeAllWindows())
				));
				return;
			}

			make.accept(player);
		});
	}

	public void fletchUnfinishedBolt(int boltId) {
		Optional<FletchableUnfinishedBolt> bolt = UNFINISHED_BOLTS.stream().filter(b -> b.getUnfinished() == boltId).findFirst();
		bolt.ifPresent(b -> {
			if (bolt.get() == FletchableUnfinishedBolt.BROAD && !player.getSlayer().getUnlocks().contains(SlayerUnlock.BROADER_FLETCHING)) {
				player.sendMessage(BROAD_ARROW_MESSAGE);
				player.getPA().removeAllWindows();
				return;
			}
			if (player.getItems().freeSlots() < 1) {
				player.sendMessage("You need at least 1 free slot to do this.");
				player.getPA().removeAllWindows();
				return;
			}
			if (!player.getItems().playerHasItem(314, 10)) {
				player.sendMessage("You need at least 10 feathers to do this.");
				player.getPA().removeAllWindows();
				return;
			}
			if (!player.getItems().playerHasItem(b.getUnfinished(), 10)) {
				player.sendMessage("You need at least 10 of this bolt type to do this.");
				player.getPA().removeAllWindows();
				return;
			}
			if (player.playerLevel[Skill.FLETCHING.getId()] < b.getLevel()) {
				player.sendMessage("You need a fletching level of " + b.getLevel() + " to fletch this bolt.");
				player.getPA().removeAllWindows();
				return;
			}
			player.getPA().stopSkilling();
			player.getItems().deleteItem2(314, 10);
			player.getItems().deleteItem2(b.getUnfinished(), 10);
			player.getItems().addItem(b.getBolt(), 10);
			player.getPA().addSkillXPMultiplied(b.getExperience() * 10, Skill.FLETCHING.getId(), true);
		});
	}

	public void fletchHeadlessArrows() {
		if (player.getItems().freeSlots() < 1) {
			player.sendMessage("You need at least 1 free slot.");
			player.getPA().removeAllWindows();
			return;
		}
		if (!player.getItems().playerHasItem(52, 15)) {
			player.sendMessage("You need at least 15 arrow shafts to do this.");
			player.getPA().removeAllWindows();
			return;
		}
		if (!player.getItems().playerHasItem(314, 15)) {
			player.sendMessage("You need at least 15 feathers to do this.");
			player.getPA().removeAllWindows();
			return;
		}
		player.getItems().deleteItem2(314, 15);
		player.getItems().deleteItem2(52, 15);
		player.getItems().addItem(53, 15);
		player.getPA().addSkillXPMultiplied(15, Skill.FLETCHING.getId(), true);
	}

	public void fletchUnstrung(int bowId) {
		Optional<FletchableBow> bow = BOWS.stream().filter(b -> b.getItem() == bowId).findFirst();
		bow.ifPresent(b -> {
			player.getPA().stopSkilling();
			if (player.playerLevel[Skill.FLETCHING.getId()] < b.getLevelRequired()) {
				player.sendMessage("You need a fletching level of " + b.getLevelRequired() + " to do this.");
				player.getPA().removeAllWindows();
				return;
			}
			Server.getEventHandler().submit(new StringBowEvent(b, player, 3));
		});
	}

	public void fletchUnstrungCross(int crossbowId) {
		Optional<FletchableCrossbow> crossbow = CROSSBOWS.stream().filter(b -> b.getItem() == crossbowId).findFirst();
		crossbow.ifPresent(b -> {
			player.getPA().stopSkilling();
			if (player.playerLevel[Skill.FLETCHING.getId()] < b.getLevelRequired()) {
				player.sendMessage("You need a fletching level of " + b.getLevelRequired() + " to do this.");
				player.getPA().removeAllWindows();
				return;
			}
			Server.getEventHandler().submit(new StringCrossbowEvent(b, player, 3));
		});
	}

	public void fletchArrow(int arrowId) {
		Optional<FletchableArrow> arrow = ARROWS.stream().filter(a -> a.getId() == arrowId).findFirst();
		arrow.ifPresent(a -> {
			player.getPA().stopSkilling();
			if (arrow.get() == FletchableArrow.BROAD && !player.getSlayer().getUnlocks().contains(SlayerUnlock.BROADER_FLETCHING)) {
				player.sendMessage(Fletching.BROAD_ARROW_MESSAGE);
				player.getPA().removeAllWindows();
				return;
			}
			if (player.playerLevel[Skill.FLETCHING.getId()] < a.getLevelRequired()) {
				player.sendMessage("You need a fletching level of " + a.getLevelRequired() + " to do this.");
				player.getPA().removeAllWindows();
				return;
			}
			Server.getEventHandler().submit(new MakeArrowEvent(player, a));
		});
	}
	
	public void fletchJavelin(int arrowId) {
		Optional<FletchableJavelin> arrow = JAVELINS.stream().filter(a -> a.getId() == arrowId).findFirst();
		arrow.ifPresent(a -> {
			player.getPA().stopSkilling();
			if (player.playerLevel[Skill.FLETCHING.getId()] < a.getLevelRequired()) {
				player.sendMessage("You need a fletching level of " + a.getLevelRequired() + " to do this.");
				player.getPA().removeAllWindows();
				return;
			}
			Server.getEventHandler().submit(new MakeJavelinEvent(player, a));
		});
	}

	public void fletchDart(int dartId) {
		Optional<FletchableDart> dart = DARTS.stream().filter(a -> a.getId() == dartId).findFirst();
		dart.ifPresent(d -> {
			player.getPA().stopSkilling();
			if (player.playerLevel[Skill.FLETCHING.getId()] < d.getLevelRequired()) {
				player.sendMessage("You need a fletching level of " + d.getLevelRequired() + " to do this.");
				player.getPA().removeAllWindows();
				return;
			}
			Server.getEventHandler().submit(new MakeDartEvent(player, d));
		});
	}

	public boolean fletchBolt(int boltId, int tipId) {
		Optional<FletchableBolt> bolt = BOLTS.stream().filter(b -> b.getUnfinished() == boltId && b.getTip() == tipId).findFirst();
		bolt.ifPresent(b -> {
			player.getPA().stopSkilling();
			if (bolt.get() == FletchableBolt.BROAD_AMETHYST && !player.getSlayer().getUnlocks().contains(SlayerUnlock.BROADER_FLETCHING)) {
				player.sendMessage(BROAD_ARROW_MESSAGE);
				player.getPA().removeAllWindows();
				return;
			}
			if (player.playerLevel[Skill.FLETCHING.getId()] < b.getLevel()) {
				player.sendMessage("You need a fletching level of " + b.getLevel() + " to do this.");
				player.getPA().removeAllWindows();
				return;
			}

			Server.getEventHandler().submit(new MakeBoltEvent(player, b, boltId, tipId));
		});
		return bolt.isPresent();
	}

	public Optional<FletchableLog> getSelectedFletchable() {
		return selectedFletchable;
	}

}
