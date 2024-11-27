package io.xeros.content.skills.smithing;

import java.util.Objects;

import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

public class Smelting {

	public enum Bars {
		BRONZE(436, 438, 2349, 1, 6, 2405, true, "bronze"), 
		IRON(440, -1, 2351, 15, 13, 2406, false, "iron"), 
		SILVER(442, -1, 2355, 20, 14, 2407, false, "silver"), 
		STEEL(440, 453, 2353, 30, 18, 2409, true, "steel"), 
		GOLD(444, -1, 2357, 40, 23, 2410, false, "gold"), 
		MITHRIL(447, 453, 2359, 50, 30, 2411, true, "mithril"), 
		ADAMANT(449, 453, 2361, 70, 38, 2412, true, "adamant"), 
		RUNE(451, 453, 2363, 85, 50, 2413, true, "rune");

		private final int ore1;
		private final int ore2;
		private final int bar;
		private final int req;
		private final int exp;
		private final int frame;
		private final boolean twoOres;
		private final String type;

		int getOre1() {
			return ore1;
		}

		int getOre2() {
			return ore2;
		}

		public int getBar() {
			return bar;
		}

		int getReq() {
			return req;
		}

		int getExp() {
			return exp;
		}

		int getFrame() {
			return frame;
		}

		boolean twoOres() {
			return twoOres;
		}

		String getType() {
			return type;
		}

		Bars(int ore, int ore2, int bar, int req, int xp, int frame, boolean two, String type) {
			this.ore1 = ore;
			this.ore2 = ore2;
			this.bar = bar;
			this.req = req;
			this.exp = xp;
			this.frame = frame;
			this.twoOres = two;
			this.type = type;
		}

		static Bars forType(String type) {
			for (Bars bar : values())
				if (bar.getType().equals(type))
					return bar;
			return null;
		}
	}

	/**
	 * Starts Smelting
	 */
	public static void startSmelting(Player c, String type, String amount, String usage) {
		c.barType = type;
		c.smeltAmount = getAmount(amount);
		c.bar = Bars.forType(c.barType);
		if(c.bar == null) //Making sure when the "bar" is null it returns
			return;
		boolean hasItems;
		if (c.bar.twoOres()) {
			hasItems = hasItems(c, c.bar.getOre1(), c.bar.getOre2(), c.bar.getBar());
		} else {
			hasItems = hasItems(c, c.bar.getOre1(), -1, c.bar.getBar());
		}
		if (hasItems) {
			if (hasReqLvl(c, c.bar.getReq(), c.bar.getBar())) {
				startCycle(c, usage);
			}
		}
	}

	private static void startCycle(final Player c, String usage) {
		c.getPA().stopSkilling();
		Server.getEventHandler().submit(new Event<Player>("skilling", c, 1) {

			public void execute() {
				if (attachment == null || attachment.isDisconnected()) {
					this.stop();
					return;
				}
				if (c.lastSmelt <= 0 || System.currentTimeMillis() - c.lastSmelt >= 1000) {
					if (c.smeltAmount <= 0) {
						stop();
						return;
					}
					appendDelay(c, usage);
				}
			}

			@Override
			public void stop() {
				super.stop();
				if (attachment == null || attachment.isDisconnected()) {
					return;
				}
				resetSmelting(c);
			}
		});
	}

	/**
	 * Applies Smelting delay
	 */
	private static void appendDelay(Player c, String usage) {
		boolean goldSmithGaunts = c.getItems().isWearingItem(776) && c.bar.getBar() == 2357;
		double percentOfXp = c.bar.getExp() / 2;
		if (c.smeltAmount > 0) {
			boolean hasItems;
			if (c.bar.twoOres())
				hasItems = hasItems(c, c.bar.getOre1(), c.bar.getOre2(), c.bar.getBar());
			else
				hasItems = hasItems(c, c.bar.getOre1(), -1, c.bar.getBar());
			if (hasItems) {
				Achievements.increase(c, AchievementType.SMITH, 1);
				c.getPA().sendSound(2725);
				if (c.bar.twoOres()) {
					if (!Objects.equals(usage, "INFERNAL")) {
						c.startAnimation(899);
					}
					c.getItems().deleteItem(c.bar.getOre1(), 1);
					c.getItems().deleteItem(c.bar.getOre2(), 1);
					c.getItems().addItem(c.bar.getBar(), 1);
					c.getPA().addSkillXPMultiplied((int) (Objects.equals(usage, "INFERNAL") ? c.bar.getExp() / 2 : c.bar.getExp() + (goldSmithGaunts ? percentOfXp : 0)), Player.playerSmithing, true);
				} else {
					if (!Objects.equals(usage, "INFERNAL")) {
						c.startAnimation(899);
					}
					c.getItems().deleteItem(c.bar.getOre1(), 1);
					if ((c.bar.getBar() == 2351 && Misc.random(100) >= 50) && c.playerEquipment[Player.playerRing] != 2568)
						c.sendMessage("The ore is too impure and you fail to refine it.");
					else {
						c.getItems().addItem(c.bar.getBar(), 1);
						c.getPA().addSkillXPMultiplied((int) (usage == "INFERNAL" ? c.bar.getExp() / 2 : c.bar.getExp() + (goldSmithGaunts ? percentOfXp : 0)), Player.playerSmithing, true);
					}
				}
			}
		} else {
			resetSmelting(c);
			c.getPA().removeAllWindows();
			return;
		}
		c.lastSmelt = System.currentTimeMillis();
		c.smeltAmount--;
		c.getPA().removeAllWindows();
	}

	/**
	 * Resets Smelting variables
	 */
	private static void resetSmelting(Player c) {
		c.smeltAmount = 0;
		c.barType = "";
		c.bar = null;
		c.isSmelting = false;
		c.lastSmelt = 0;
		CycleEventHandler.getSingleton().stopEvents(c, c.smeltEventId);
	}

	/**
	 * Checks if the player has the required level
	 */
	private static boolean hasReqLvl(Player c, int req, int bar) {
		int level = c.getPA().getLevelForXP(c.playerXP[Player.playerSmithing]);
		if (level >= req)
			return true;
		else
			c.sendMessage("You need a Smithing level of " + req + " to smelt a " + ItemAssistant.getItemName(bar));
		resetSmelting(c);
		return false;
	}

	/**
	 * Checks if the player has the required items
	 */
	private static boolean hasItems(Player c, int firstOre, int secondOre, int createdBar) {
		String oreOne = ItemAssistant.getItemName(firstOre);
		String oreTwo = ItemAssistant.getItemName(secondOre);
		String barOutcome = ItemAssistant.getItemName(createdBar);
		if (secondOre > 0) {
			if (c.getItems().playerHasItem(firstOre) && c.getItems().playerHasItem(secondOre))
				return true;
		} else {
			if (c.getItems().playerHasItem(firstOre))
				return true;
		}
		if (secondOre > 0)
			c.sendMessage("You need " + oreOne + " and " + oreTwo + " to smelt a " + barOutcome);
		else
			c.sendMessage("You need " + oreOne + " to smelt a " + barOutcome);
			resetSmelting(c);
		return false;
	}

	/**
	 * Gets the smelting amount
	 */
	private static int getAmount(String amount) {
		if (Objects.equals(amount, "ONE"))
			return 1;
		if (Objects.equals(amount, "FIVE"))
			return 5;
		if (Objects.equals(amount, "TEN"))
			return 10;
		if (Objects.equals(amount, "ALL"))
			return 28;
		
		return -1;
	}

}
