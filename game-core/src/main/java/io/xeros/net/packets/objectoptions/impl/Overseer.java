package io.xeros.net.packets.objectoptions.impl;

import io.xeros.model.entity.player.Player;

public class Overseer {

	private static final int[] bludgeonPieces={13274, 13275, 13276};

	public static void handleBludgeon(Player c) {

		if (!c.getItems().playerHasAllItems(bludgeonPieces)) {
			c.getDH().sendStatement("The Overseer advices you to bring him all three bludgeon pieces!");
		} else {
			for (int item : bludgeonPieces) {
				c.getItems().deleteItem(item, 1);
			}
			c.getDH().sendItemStatement("The Overseer combines the items into a bludgeon!", 13263);
			c.getItems().addItem(13263, 1);
		}
	}


}
