/* Distributes loot after completion of Trials of Xeric.
 * Author @Patrity
 */
package io.xeros.content.minigames.xeric;

import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;
/**
 * 
 * @author Patrity
 * 
 */
public class XericRewards {
	
	int qty;

	public static void giveReward(int dmg, Player player) {
		int roll = Misc.random(100);
		player.totalRaidsFinished++;
		player.sendMessage("You have now completed " + player.totalRaidsFinished + " Trials of Xeric!");
		if (dmg > 9999) {
			if (roll >= 93) {
				rareDrop(player);
			} else {
				commonDrop(player);
			}
		}

		if (dmg >= 8000 && dmg <= 9999) {
			if (roll >= 95) {
				rareDrop(player);
			} else {
				commonDrop(player);
			}
		}

		if (dmg >= 5000 && dmg <= 7999) {
			if (roll >= 97) {
				rareDrop(player);
			} else {
				commonDrop(player);
			}
		}

		if (dmg <= 4999 ) {
			if (roll >= 99) {
				rareDrop(player);
			} else {
				commonDrop(player);
			}
		}
	}

	public static final int[] rareDropItem = {
			33021,
			33022,
			33023,
			33024,
			33025,
			33026,
			33027,
			33028,
			33029,
			33030
};
	public static final int[] commonDropItem = {
			200,//guam
			204,//tarromin
			206,//harralander
			208,//ranarr
			210,//irit
			212,//avantoe
			214,//kwuarm
			216,//cadantine
			220,//torstol
			2486,//lantadyme
			3050,//toadflax
			3052,//snapdragon
			454,//coal
			441,//iron ore
			1957//onion
			
			
};

	public static void rareDrop(Player player) {
		int rareitem = Misc.random(rareDropItem.length - 1);
		player.getItems().addItemUnderAnyCircumstance(rareDropItem[rareitem], 1);
		PlayerHandler.executeGlobalMessage(player.getDisplayName() + " has received a " + ItemDef.forId(rareDropItem[rareitem]).getName()  + " from Trials of Xeric!");
	}


	public static void commonDrop(Player player) {
		int qty = (100 + Misc.random(80));
		int commonitem = Misc.random(commonDropItem.length - 1);
		int drop = commonDropItem[commonitem];
		if (drop == 1957) {
			qty = 1;
			PlayerHandler.executeGlobalMessage(player.getDisplayName() +" has received THE ONION as a reward from Trials of Xeric");
		} else {
			player.sendMessage("You have received @red@" + ItemDef.forId(drop).getName()
					+ " x" + qty + "@bla@ as a reward from Trials of Xeric!");
		}
		player.getItems().addItemUnderAnyCircumstance(drop, qty);

	}
}