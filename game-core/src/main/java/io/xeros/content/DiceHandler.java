package io.xeros.content;

import io.xeros.Server;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.logging.player.RollDiceLog;

public class DiceHandler {

	public static final int ROLL_TIMER = 1000, DICE_BAG = 15094;
	public static final Boundary DICING_AREA = new Boundary(2432, 3077, 2477, 3126);


	enum Dice {
		DIE_6_SIDES(15086, 6, 2072), DICE_6_SIDES(15088, 12, 2074), DIE_8_SIDES(15090, 8, 2071), DIE_10_SIDES(15092, 10,
				2070), DIE_12_SIDES(15094, 12, 2073), DIE_20_SIDES(15096, 20, 2068), DICE_UP_TO_100(15098, 100,
						713), DIE_4_SIDES(15100, 4, 2069);

		private final int id;
		private final int sides;
		private final int gfx;

		Dice(int id, int sides, int gfx) {
			this.id = id;
			this.sides = sides;
			this.gfx = gfx;
		}

		public int diceId() {
			return id;
		}

		public int diceSize() {
			return sides;
		}

		public int diceGfx() {
			return gfx;
		}
	}

	/**
	 * Handles the rolling of the dice to a player.
	 * 
	 * @param c
	 *            The player.
	 * @param roll
	 *            What the player rolled on the dice.
	 * @param item
	 *            The id of the dice.
	 */
	public static void selfRoll(Player c, int roll, int item) {
		c.sendMessage("You rolled @red@" + roll + "@bla@ on the " + ItemAssistant.getItemName(item) + ".");
	}

	/**
	 * Handles selecting the dice
	 * 
	 * @param c
	 *            The player.
	 * @param item
	 *            The dice id.
	 * @return Whether or not a dice were selected.
	 */
	public static boolean selectDice(Player c, int item) {
		for (Dice d : Dice.values()) {
			if (item == d.diceId() || item == DICE_BAG) {
				c.getDH().sendOption5(ItemAssistant.getItemName(Dice.DIE_6_SIDES.diceId()),
						ItemAssistant.getItemName(Dice.DICE_6_SIDES.diceId()),
						ItemAssistant.getItemName(Dice.DIE_8_SIDES.diceId()),
						ItemAssistant.getItemName(Dice.DIE_10_SIDES.diceId()), "Next Page");
				c.diceItem = item;
				return true;
			}
		}
		return false;
	}

	/**
	 * Handles all the clicking for the dice.
	 * 
	 * @param c
	 *            The player.
	 * @param actionButtonId
	 *            Action button id of what is clicked.
	 * @return Whether or not a click was handled.
	 */
	public static boolean handleClick(Player c, int actionButtonId) {
		int[][] dice = { { Dice.DIE_6_SIDES.diceId() }, { Dice.DICE_6_SIDES.diceId() }, { Dice.DIE_8_SIDES.diceId() },
				{ Dice.DIE_10_SIDES.diceId() }, { Dice.DIE_12_SIDES.diceId() }, { Dice.DIE_20_SIDES.diceId() },
				{ Dice.DICE_UP_TO_100.diceId() }, { Dice.DIE_4_SIDES.diceId() } };
		int DICE = 0;
		if (actionButtonId - 9190 >= 0 && actionButtonId - 9190 <= 5) {
			if (c.dicePage == 0) {
				c.getPA().removeAllWindows();
				if (actionButtonId - 9190 <= 3) {
					if (c.getItems().playerHasItem(c.diceItem, 1)) {
						c.getItems().deleteItem(c.diceItem, 1);
						c.getItems().addItem(dice[actionButtonId - 9190][DICE], 1);
					}
				} else {
					c.getDH().sendOption5(ItemAssistant.getItemName(Dice.DIE_12_SIDES.diceId()),
							ItemAssistant.getItemName(Dice.DIE_20_SIDES.diceId()),
							ItemAssistant.getItemName(Dice.DICE_UP_TO_100.diceId()),
							ItemAssistant.getItemName(Dice.DIE_4_SIDES.diceId()), "Return");
					c.dicePage = 1;
				}
			} else if (c.dicePage == 1) {
				c.getPA().removeAllWindows();
				if (actionButtonId - 9190 <= 3) {
					if (c.getItems().playerHasItem(c.diceItem, 1)) {
						c.getItems().deleteItem(c.diceItem, 1);
						c.getItems().addItem(dice[actionButtonId - 9186][DICE], 1);
					}
				} else {
					c.getPA().closeAllWindows();
				}
				c.dicePage = 0;
			}
			return true;
		}
		return false;
	}

	public static boolean inDicingArea(Player c) {
		return Boundary.isIn(c, DICING_AREA) || Boundary.isIn(c, Boundary.FLOWER_POKER_AREA);
	}

	public static void rollDice(Player c) {
		if (c.isGambleBanned()) {
			c.sendMessage("You cannot gamble.");
			return;
		}
		if (!inDicingArea(c)) {
			c.sendMessage("You need to be in the ::dice area to roll dices.");
			return;
		}
		if (System.currentTimeMillis() - c.diceDelay >= 3000) {
			int roll = Misc.random(99) + 1;
			//TODO - ADD GFX + ANIMATION TO THE DICE BAG
			c.startAnimation(412);
			c.forcedChat("[DICE]" + Misc.capitalizeJustFirst(c.getDisplayName()) + " rolled " + roll + " on the percentile dice.");
			Server.getLogging().write(new RollDiceLog(c, roll));

			PlayerHandler.nonNullStream()
					.filter(player -> Boundary.isIn(player, Boundary.FLOWER_POKER_AREA) && player.heightLevel == 0)
					.forEach(player -> {
						player.sendMessage("[@red@DICE@bla@] @blu@" + Misc.capitalizeJustFirst(c.getDisplayName())
						+ " @bla@rolled @red@" + roll + "@bla@ on the percentile dice.");
						
						}
					);
			c.diceDelay = System.currentTimeMillis();
		} else {
			c.sendMessage("You must wait 3 seconds to roll dice again.");
		}
	}

}