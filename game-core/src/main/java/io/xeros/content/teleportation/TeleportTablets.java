package io.xeros.content.teleportation;

import io.xeros.Configuration;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

/**
 * 
 * @author Mack
 *
 */
public class TeleportTablets {

	public enum TabType {
		HOME(8013, Configuration.RESPAWN_X, Configuration.RESPAWN_Y),
		ANNAKARL(12775, Configuration.ANNAKARL_X, Configuration.ANNAKARL_Y),
		CARRALLANGER(12776, Configuration.CARRALLANGAR_X, Configuration.CARRALLANGAR_Y),
		DAREEYAK(12777, Configuration.DAREEYAK_X, Configuration.DAREEYAK_Y),
		GHORROCK(12778, Configuration.GHORROCK_X, Configuration.GHORROCK_Y),
		KHARYRLL(12779, Configuration.KHARYRLL_X, Configuration.KHARYRLL_Y),
		LASSAR(12780, Configuration.LASSAR_X, Configuration.LASSAR_Y),
		PADDEWWA(12781, Configuration.PADDEWWA_X, Configuration.PADDEWWA_Y),
		SENNTISTEN(12782, Configuration.SENNTISTEN_X, Configuration.SENNTISTEN_Y),
		WILDY_RESOURCE(12409, 3184, 3945), 
		PIRATE_HUT(12407, 3045, 3956), 
		MAGE_BANK(12410, 2538, 4716), 
		CALLISTO(12408, 3325, 3849), 
		KBD_LAIR(12411, 2271, 4681),
		
		//City teleports
		VARROCK(8007, Configuration.VARROCK_X, Configuration.VARROCK_Y),
		LUMBRIDGE(8008, Configuration.LUMBY_X, Configuration.LUMBY_Y),
		FALADOR(8009, Configuration.FALADOR_X, Configuration.FALADOR_Y),
		CAMELOT(8010, Configuration.CAMELOT_X, Configuration.CAMELOT_Y),
		ARDOUGNE(8011, Configuration.ARDOUGNE_X, Configuration.ARDOUGNE_Y),
		TROLLHEIM(11747, Configuration.TROLLHEIM_X, Configuration.TROLLHEIM_Y);
		
		
		private final int tab;
		private final int x;
		private final int y;

		public int getTabId() {
			return tab;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		TabType(int tab, int x, int y) {
			this.tab = tab;
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * Operates the teleport tab
	 * 
	 * @param player
	 * @param item
	 */
	public static void operate(final Player player, int item) {
		for (TabType type : TabType.values()) {
			if (type.getTabId() == item) {
				if (System.currentTimeMillis() - player.lastTeleport < 3500)
					return;	
				if (!player.getPA().canTeleport("modern")) {
					return;
				}
				if (Boundary.isIn(player, Boundary.OUTLAST_HUT)) {
					player.sendMessage("Please leave the outlast hut area to teleport.");
					return ;
				}
				if (Boundary.isIn(player, Boundary.RAIDS_LOBBY) || Boundary.isIn(player, Boundary.RAIDS)) {
					player.sendMessage("Please leave the raids to teleport.");
					return ;
				}
				player.teleporting = true;
				player.getItems().deleteItem(type.getTabId(), 1);
				player.lastTeleport = System.currentTimeMillis();
				player.startAnimation(4731);
				player.gfx0(678);
				final int x = type.getX();
				final int y = type.getY();
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

					@Override
					public void execute(CycleEventContainer container) {
						player.setTeleportToX(x);
						player.setTeleportToY(y);
						player.heightLevel = 0;
						player.teleporting = false;
						player.gfx0(-1);
						player.startAnimation(65535);
						container.stop();
					}

				}, 3);
			}
		}
	}

}
