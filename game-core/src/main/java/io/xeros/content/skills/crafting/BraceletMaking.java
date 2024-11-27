package io.xeros.content.skills.crafting;

import io.xeros.model.definition.Items;
import io.xeros.model.entity.player.Player;

public class BraceletMaking extends CraftingData {
	
	
	public static void craftBraceletDialogue(final Player c) {
		String[] name = { "Dragonstone bracelet", "Onyx bracelet", "Zenyte bracelet" };
		c.getPA().sendChatboxInterface(8880);
		c.getPA().itemOnInterface(8883, 180, 11126);
		c.getPA().itemOnInterface(8884, 180, 11133);
		c.getPA().itemOnInterface(8885, 180, 19492);
		for (int i = 0; i < name.length; i++) {
			c.getPA().sendFrame126(name[i], 8889 + (i * 4));
		}
		c.braceletDialogue = true;
	}

	public static void craftBracelet(final Player c, final int buttonId) {
		c.getPA().removeAllWindows();
		
		if (c.playerIsCrafting == true) {
			c.sendMessage("You are already crafting.");
			return;
		}

		if (!c.getItems().playerHasItem(Items.GOLD_BAR)) {
			c.sendMessage("You do not have enough gold bars.");
			return;
		}

		// Regen
		if (buttonId == 34189) {
			if (c.getItems().playerHasItem(Items.ONYX)) {
				c.getItems().deleteItem2(Items.ONYX, 1);
				c.getItems().deleteItem2(Items.GOLD_BAR, 1);
				c.getItems().addItem(Items.ONYX_BRACELET, 1);
				c.getPA().addSkillXPMultiplied(125, 12, true);
				c.startAnimation(899);
			}
		}

		// Dragonstone
		if (buttonId == 34185) {
			if (c.getItems().playerHasItem(Items.DRAGONSTONE)) {
				c.getItems().deleteItem2(Items.DRAGONSTONE, 1);
				c.getItems().deleteItem2(Items.GOLD_BAR, 1);
				c.getItems().addItem(Items.DRAGONSTONE_BRACELET, 1);
				c.getPA().addSkillXPMultiplied(110, 12, true);
				c.startAnimation(899);
			}
		}

		// Zenyte
		if (buttonId == 34193) {
			if (c.getItems().playerHasItem(Items.ZENYTE)) {
				c.getItems().deleteItem2(Items.ZENYTE, 1);
				c.getItems().deleteItem2(Items.GOLD_BAR, 1);
				c.getItems().addItem(19532, 1); // ZENYTE_BRACELET_2
				c.getPA().addSkillXPMultiplied(180, 12, true);
				c.startAnimation(899);
			}
		}

		c.braceletDialogue = false;
		c.playerIsCrafting = false;
	}
}
