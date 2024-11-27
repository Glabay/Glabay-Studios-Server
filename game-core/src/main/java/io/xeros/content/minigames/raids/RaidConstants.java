package io.xeros.content.minigames.raids;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

public class RaidConstants {
	
	public static List<Raids> raidGames = Lists.newArrayList();

	public static int currentRaidHeight = 4;
	
	public static void checkInstances() {
		Lists.newArrayList(raidGames).stream().filter(Objects::nonNull).forEach(raid -> {
			if(raid.getPlayers().size() == 0 && System.currentTimeMillis() - raid.lastActivity > 60000) {
				raid.killAllSpawns();
				System.out.println("raid destroyed");
				raidGames.remove(raid);
			}
		});
	}
	
	public static void checkLogin(Player player) {
		checkInstances();
		if (Boundary.isIn(player, Boundary.RAIDS) || Boundary.isIn(player, Boundary.OLM)) {
			boolean[] addedToGame = {false};
			Lists.newArrayList(raidGames)
			.stream()
			.filter(Objects::nonNull)
			.filter(raid -> raid.hadPlayer(player))
			.findFirst()
			.ifPresent(raid -> {
				boolean added = raid.login(player);
				if(added) {
					addedToGame[0] = true;
				}
			});
			if(!addedToGame[0]) {
				player.sendMessage("You logged out and were removed from the raid instance!");
				player.getPA().movePlayerUnconditionally(3034, 6067, 0);
				player.setRaidsInstance(null);
				player.specRestore = 120;
				player.specAmount = 10.0;
				player.setRunEnergy(100, true);
				player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
				player.getPA().refreshSkill(Player.playerPrayer);
				player.getHealth().removeAllStatuses();
				player.getHealth().reset();
				player.getPA().refreshSkill(5);
			}
		}
		
	}

}
