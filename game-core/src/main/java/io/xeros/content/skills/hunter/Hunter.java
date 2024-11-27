package io.xeros.content.skills.hunter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.xeros.Server;
import io.xeros.content.skills.hunter.trap.Trap;
import io.xeros.content.skills.hunter.trap.TrapProcessor;
import io.xeros.content.skills.hunter.trap.TrapTask;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

/**
 * The class which holds static functionality for the hunter skill.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class Hunter {
	/**
	 * The mappings which contain each trap by player on the world.
	 */
	public static final Map<Player, TrapProcessor> GLOBAL_TRAPS = new HashMap<>();

	/**
	 * Retrieves the maximum amount of traps a player can lay.
	 * @param player	the player to lay a trap down for.
	 * @return a numerical value determining the amount a player can lay.
	 */
	private static int getMaximumTraps(Player player) {
		int level = player.playerLevel[21];
		return player.getPosition().inWild() ? level / 20 + 2 : level / 20 + 1;

	}

	/**
	 * Attempts to abandon the specified {@code trap} for the player.
	 * @param trap		the trap that was abandoned.
	 * @param logout	if the abandon was due to the player logging out.
	 */
	public static void abandon(Player player, Trap trap, boolean logout) {
		if(GLOBAL_TRAPS.get(player) == null) {
			return;
		}
		
		if(logout) {
			GLOBAL_TRAPS.get(player).getTraps().forEach(t -> {
				t.setAbandoned(true);
				Server.getGlobalObjects().remove(t.getObject());
				Server.getGlobalObjects().remove(t.getObject().getObjectId(), t.getObject().getX(), t.getObject().getY(), t.getObject().getHeight());
				Server.itemHandler.createGroundItem(player, t.getType().getItemId(), t.getObject().getX(), t.getObject().getY(), t.getObject().getHeight(), 1, player.getIndex());
			});
			GLOBAL_TRAPS.get(player).getTraps().clear();
		} else {
			GLOBAL_TRAPS.get(player).getTraps().remove(trap);
			trap.setAbandoned(true);
			Server.getGlobalObjects().remove(trap.getObject());
			Server.getGlobalObjects().remove(trap.getObject().getObjectId(), trap.getObject().getX(), trap.getObject().getY(), trap.getObject().getHeight());
			Server.itemHandler.createGroundItem(player, trap.getType().getItemId(), trap.getObject().getX(), trap.getObject().getY(), trap.getObject().getHeight(), 1, player.getIndex());
			player.sendMessage("You have abandoned your trap...");
		}

		if(GLOBAL_TRAPS.get(player).getTraps().isEmpty()) {
			GLOBAL_TRAPS.get(player).setTask(Optional.empty());
			GLOBAL_TRAPS.remove(player);
		}
	}

	/**
	 * Attempts to lay down the specified {@code trap} for the specified {@code player}.
	 * @param player	the player to lay the trap for.
	 * @param trap		the trap to lay down for the player.
	 * @return {@code true} if the trap was laid, {@code false} otherwise.
	 */
	public static boolean lay(Player player, Trap trap) {
		if(!player.last_trap_layed.elapsed(1200)) {
			return false;
		}

		player.last_trap_layed.reset();
		
		if (!Boundary.isIn(player, Boundary.HUNTER_BOUNDARIES) && !Boundary.isIn(player, Boundary.REGULAR_DZ_HUNTER)
				&& !Boundary.isIn(player, Boundary.HUNTER_AREA)) {
			player.sendMessage("This is not a suitable spot to place a trap.");
			return false;
		}

		GLOBAL_TRAPS.putIfAbsent(player, new TrapProcessor());

		if(!GLOBAL_TRAPS.get(player).getTask().isPresent()) {
			GLOBAL_TRAPS.get(player).setTask(new TrapTask(player));
			CycleEventHandler.getSingleton().addEvent(player, GLOBAL_TRAPS.get(player).getTask().get(), 10);
		}

		if(GLOBAL_TRAPS.get(player).getTraps().size() >= getMaximumTraps(player)) {
			player.sendMessage("You cannot lay more then " + getMaximumTraps(player) + " traps with your hunter level.");
			return false;
		}

		if(Server.getGlobalObjects().anyExists(player.absX, player.absY, player.heightLevel)) {
			player.sendMessage("You can't lay down your trap here.");
			return false;
		}

		GLOBAL_TRAPS.get(player).getTraps().add(trap);

		trap.submit();
		player.startAnimation(827);
		player.getItems().deleteItem(trap.getType().getItemId(), 1);
		Server.getGlobalObjects().add(trap.getObject());
		//Server.getGlobalObjects().add(trap.getObject());
		if (player.getRegionProvider().getClipping(player.getX() - 1, player.getY(), player.heightLevel, -1, 0)) {
			player.getPA().walkTo2(-1, 0);
		} else if (player.getRegionProvider().getClipping(player.getX() + 1, player.getY(), player.heightLevel, 1, 0)) {
			player.getPA().walkTo2(1, 0);
		} else if (player.getRegionProvider().getClipping(player.getX(), player.getY() - 1, player.heightLevel, 0, -1)) {
			player.getPA().walkTo2(0, -1);
		} else if (player.getRegionProvider().getClipping(player.getX(), player.getY() + 1, player.heightLevel, 0, 1)) {
			player.getPA().walkTo2(0, 1);
		}
		return true;
	}

	public static boolean resetTrap(Player player, GlobalObject object) {
		Trap trap = getTrap(player, object).orElse(null);
		if(trap == null) {
			return false;
		}
		if(trap.getPlayer() == null) {
			player.sendMessage("You can't reset someone elses trap...");
			return false;
		}
		if (!trap.getState().equals(Trap.TrapState.FALLEN)) {
			return false;
		}
		trap.setState(Trap.TrapState.PENDING);
		trap.submit();
		Server.getGlobalObjects().add(trap.getObject());
		player.sendMessage("You've reset your box trap.");
		return false;
	}
	/**
	 * Attempts to pick up the trap for the specified {@code player}.
	 * @param player	the player to pick this trap up for.
	 * @param object		the object id that was clicked.
	 * @return {@code true} if the trap was picked up, {@code false} otherwise.
	 */
	public static boolean pickup(Player player, GlobalObject object) {
		Optional<Trap.TrapType> type = Trap.TrapType.getTrapByObjectId(object.getObjectId());
		
		if (System.currentTimeMillis() - player.lastPickup < 2500)
			return false;		

		if(!type.isPresent()) {
			return false;
		}

		Trap trap = getTrap(player, object).orElse(null);

		if(trap == null) {
			return false;
		}

		if(trap.getPlayer() == null) {
			player.sendMessage("You can't pickup someone elses trap...");
			return false;
		}

		if (trap.getState().equals(Trap.TrapState.TRIGGERED)) {
			player.sendMessage("You notice an animal moving closer to your trap and don't pick it up.");
			return false;
		}

		if(trap.getState().equals(Trap.TrapState.CAUGHT)) {
			return false;
		}

		GLOBAL_TRAPS.get(player).getTraps().remove(trap);

		if(GLOBAL_TRAPS.get(player).getTraps().isEmpty()) {
			GLOBAL_TRAPS.get(player).setTask(Optional.empty());
			GLOBAL_TRAPS.remove(player);
		}

		trap.onPickUp();
		Server.getGlobalObjects().remove(trap.getObject());
		Server.getGlobalObjects().remove(trap.getObject().getObjectId(), trap.getObject().getX(), trap.getObject().getY(), trap.getObject().getHeight());
		player.getItems().addItem(trap.getType().getItemId(), 1);
		player.startAnimation(827);
		player.lastPickup = System.currentTimeMillis();
		return true;
	}


	/**
	 * Attempts to claim the rewards of this trap.
	 * @param player		the player attempting to claim the items.
	 * @param object		the object being interacted with.
	 * @return {@code true} if the trap was claimed, {@code false} otherwise.
	 */
	public static boolean claim(Player player, GlobalObject object) {
		Trap trap = getTrap(player, object).orElse(null);
		
		if (System.currentTimeMillis() - player.lastPickup < 2500)
			return false;		

		if(trap == null) {
			player.sendMessage("You can't pickup someone elses trap...");
			return false;
		}

		if(!trap.canClaim(object)) {
			return false;
		}

		if(trap.getPlayer() == null) {
			player.sendMessage("You can't claim the rewards of someone elses trap...");
			return false;
		}

		if(!trap.getState().equals(Trap.TrapState.CAUGHT)) {
			return false;
		}

		double percentOfXp = (trap.experience() * 2.5);
		Arrays.stream(trap.reward()).forEach(reward -> player.getItems().addItem(reward.id(), reward.amount()));
		player.getPA().addSkillXPMultiplied((int) ((int) trap.experience() + (player.getItems().isWearingItem(10071) ? percentOfXp : 0)), 21, true);
		GLOBAL_TRAPS.get(player).getTraps().remove(trap);
		
		if(GLOBAL_TRAPS.get(player).getTraps().isEmpty()) {
			GLOBAL_TRAPS.get(player).setTask(Optional.empty());
			GLOBAL_TRAPS.remove(player);
		}
		
		Server.getGlobalObjects().remove(trap.getObject());
		Server.getGlobalObjects().remove(trap.getObject().getObjectId(), trap.getObject().getX(), trap.getObject().getY(), trap.getObject().getHeight());
		player.getItems().addItem(trap.getType().getItemId(), 1);
		player.startAnimation(827);
		player.lastPickup = System.currentTimeMillis();
		
		int randomGray = player.skillingPetRateScroll ? Misc.random(1230) : Misc.random(1500);
		int randomRed = player.skillingPetRateScroll ? Misc.random(1875) : Misc.random(2500);
		int randomBlack = player.skillingPetRateScroll ? Misc.random(2700) : Misc.random(3500);
		int randomGold = player.skillingPetRateScroll ? Misc.random(11300) : Misc.random(15000);
		
		if (randomGold == 2 && player.getItems().getItemCount(13326, false) == 0 && player.petSummonId != 13326) {
			PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr18@ <col=255>" + player.getDisplayName() + "</col> caught a <col=CC0000>Golden Chinchompa</col> pet lucky enough!");
			player.getItems().addItemUnderAnyCircumstance(13326, 1);
			player.getCollectionLog().handleDrop(player, 5, 13326, 1);
		}
		switch (trap.getType().getItemId()) {
		case 10033:
			if (randomGray == 25 && player.getItems().getItemCount(13324, false) == 0 && player.petSummonId != 13324) {
				PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr18@ <col=255>" + player.getDisplayName() + "</col> caught a <col=CC0000>Gray Chinchompa</col> pet!");
				player.getItems().addItemUnderAnyCircumstance(13324, 1);
				player.getCollectionLog().handleDrop(player, 5, 13324, 1);
			}
			break;
			
		case 10008:
			if (randomRed == 15 && player.getItems().getItemCount(13323, false) == 0 && player.petSummonId != 13323) {
				PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr18@ <col=255>" + player.getDisplayName() + "</col> caught a <col=CC0000>Red Chinchompa</col> pet!");
				player.getItems().addItemUnderAnyCircumstance(13323, 1);
				player.getCollectionLog().handleDrop(player, 5, 13323, 1);
			}
			break;
			
		case 11959:
			if (randomBlack == 8 && player.getItems().getItemCount(13325, false) == 0 && player.petSummonId != 13325) {
				PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] @cr18@ <col=255>" + player.getDisplayName() + "</col> caught a <col=CC0000>Black Chinchompa</col> pet!");
				player.getItems().addItemUnderAnyCircumstance(13325, 1);
				player.getCollectionLog().handleDrop(player, 5, 13325, 1);
			}
			break;
		}
		return true;
	}


	/**
	 * Gets a trap for the specified global object given.
	 * @param player	the player to return a trap for.
	 * @param object	the object to compare.
	 * @return a trap wrapped in an optional, {@link Optional#empty()} otherwise.
	 */
	public static Optional<Trap> getTrap(Player player, GlobalObject object) {
		return !GLOBAL_TRAPS.containsKey(player) ? Optional.empty() : GLOBAL_TRAPS.get(player).getTraps().stream().filter(trap -> trap.getObject().getObjectId() == object.getObjectId() && trap.getObject().getX() == object.getX() && trap.getObject().getY() == object.getY() && trap.getObject().getHeight() == object.getHeight()).findAny();
	}
}
