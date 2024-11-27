package io.xeros.model.cycleevent.impl;

import java.util.concurrent.TimeUnit;

import io.xeros.Server;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

public class WheatPortalEvent extends Event<Object> {
	
	/**
	 * The identifications of the portal and the wheat
	 */
	static final int PORTAL = 25014, WHEAT = 24989;

	/**
	 * The amount of time in game cycles (600ms) that the event pulses at
	 */
	private static final int INTERVAL = Misc.toCycles(5, TimeUnit.MINUTES);

	/**
	 * The x and y location of the container to be searched
	 */
	public static int xLocation, yLocation;

	/**
	 * The portal objects being used
	 */
	GlobalObject portal_one, portal_two, portal_three, portal_four, wheat_one, wheat_two, wheat_three, wheat_four;
	
	/**
	 * The portal locations
	 */
	private static final int[][] PORTAL_LOCATIONS = {
		{ 2526, 2933 }, { 2498, 2937 }, { 2559, 2941 }, { 2577, 2943 }, { 2594, 2928}, 
		{ 2612, 2939 }, { 2602, 2916 }, { 2584, 2905 }, { 2589, 2884 }, { 2559, 2887 }
	};

	/**
	 * Creates a new event to cycle through messages for the entirety of the runtime
	 */
	public WheatPortalEvent() {
		super("", new Object(), INTERVAL);
	}

	@Override
	public void execute() {
		Server.getGlobalObjects().remove(portal_one);
		Server.getGlobalObjects().remove(portal_two);
		Server.getGlobalObjects().remove(portal_three);
		Server.getGlobalObjects().remove(portal_four);
		Server.getGlobalObjects().remove(wheat_one);
		Server.getGlobalObjects().remove(wheat_two);
		Server.getGlobalObjects().remove(wheat_three);
		Server.getGlobalObjects().remove(wheat_four);
		generateLocation();
		Server.getGlobalObjects().add(portal_one = new GlobalObject(PORTAL, xLocation, yLocation, 0, 2, 22, -1, Integer.MAX_VALUE));
		Server.getGlobalObjects().add(portal_two = new GlobalObject(PORTAL, xLocation -1, yLocation, 0, 2, 22, -1, Integer.MAX_VALUE));
		Server.getGlobalObjects().add(portal_three = new GlobalObject(PORTAL, xLocation, yLocation +1, 0, 2, 22, -1, Integer.MAX_VALUE));
		Server.getGlobalObjects().add(portal_four = new GlobalObject(PORTAL, xLocation -1, yLocation +1, 0, 2, 22, -1, Integer.MAX_VALUE));
		Server.getGlobalObjects().add(wheat_one = new GlobalObject(WHEAT, xLocation, yLocation, 0, 2, 10, -1, Integer.MAX_VALUE));
		Server.getGlobalObjects().add(wheat_two = new GlobalObject(WHEAT, xLocation -1, yLocation, 0, 2, 10, -1, Integer.MAX_VALUE));
		Server.getGlobalObjects().add(wheat_three = new GlobalObject(WHEAT, xLocation, yLocation +1, 0, 2, 10, -1, Integer.MAX_VALUE));
		Server.getGlobalObjects().add(wheat_four = new GlobalObject(WHEAT, xLocation -1, yLocation +1, 0, 2, 10, -1, Integer.MAX_VALUE));
		
		PlayerHandler.nonNullStream().forEach(player -> {
			if (Boundary.isIn(player, Boundary.HUNTER_JUNGLE)) {
				player.sendMessage("@red@[Hunter] @bla@A new puro-puro teleport has spawned!");
			}
		});
		
//		int index = 0;
//		for (int i = 0; i < PORTAL_LOCATIONS.length; i++) {
//			if (xLocation == PORTAL_LOCATIONS[i][0] && yLocation == PORTAL_LOCATIONS[i][1]) {
//				index = i + 1;
//			}
//		}
	}
	
	private void generateLocation() {
		int oldX = xLocation;
		int oldY = yLocation;
		int attempts = 0;
		while (oldX == xLocation && oldY == yLocation && attempts++ < 50) {
			int index = Misc.random(PORTAL_LOCATIONS.length - 1);
			int locX = PORTAL_LOCATIONS[index][0];
			int locY = PORTAL_LOCATIONS[index][1];
			if (locX != oldX && locY != oldY) {
				xLocation = locX;
				yLocation = locY;
				break;
			}
		}
	}

}
