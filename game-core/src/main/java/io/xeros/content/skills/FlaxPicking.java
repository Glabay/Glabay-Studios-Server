package io.xeros.content.skills;

import io.xeros.Server;
import io.xeros.content.achievement_diary.impl.FaladorDiaryEntry;
import io.xeros.content.achievement_diary.impl.KandarinDiaryEntry;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Location3D;
import org.apache.commons.lang3.RandomUtils;

public final class FlaxPicking {

	/**
	 * The single instance of this class
	 */
	private static final FlaxPicking INSTANCE = new FlaxPicking();

	/**
	 * Attempts to pick a new flax on the map by creating an event that continues until there is no flax or the player has no more room.
	 * 
	 * @param player the player picking the flax
	 * @param location the location of the flax
	 */
	public final void pick(Player player, Location3D location) {
		player.getPA().stopSkilling();
		if (player.getItems().freeSlots() == 0) {
			player.sendMessage("You have run out of free slots.");
			return;
		}
		Server.getEventHandler().submit(new FlaxPickingEvent(player, 4, location));
	}

	/**
	 * The single {@link FlaxPicking} object that exists
	 * 
	 * @return the single instance
	 */
	public static final FlaxPicking getInstance() {
		return INSTANCE;
	}

	/**
	 * A class that is created to handle the flax picking event for a single player
	 * 
	 * @author Jason MacKeigan
	 * @date May 15, 2015, 2015, 10:54:44 AM
	 */
	private final class FlaxPickingEvent extends Event<Player> {

		/**
		 * The location of the flax
		 */
		private final Location3D location;

		/**
		 * Creates a new {@link FlaxPickingEvent} for a single player
		 * 
		 * @param attachment the player
		 * @param ticks the time the event is alive for
		 * @param location the location
		 */
		public FlaxPickingEvent(Player attachment, int ticks, Location3D location) {
			super("skilling", attachment, ticks);
			this.location = location;
		}

		@Override
		public void execute() {
			if (attachment == null || attachment.isDisconnected()) {
				stop();
				return;
			}

			if (attachment.getItems().freeSlots() == 0) {
				attachment.sendMessage("You have run out of free slots.");
				stop();
				return;
			}

			boolean originalObject = attachment.getRegionProvider().get(location.getX(), location.getY()).isWorldObject(14896, location.getX(), location.getY(), location.getZ());
			boolean spawnedObject = Server.getGlobalObjects().exists(14896, location.getX(), location.getY());

			if (!originalObject && !spawnedObject || Server.getGlobalObjects().exists(-1, location.getX(), location.getY())) {
				stop();
				return;
			}
			

			if (Boundary.isIn(attachment, Boundary.FALADOR_BOUNDARY)) {
				attachment.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.PICK_FLAX);
			}
			if (Boundary.isIn(attachment, Boundary.SEERS_BOUNDARY)) {
				attachment.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.PICK_FLAX_SEERS);
			}
			attachment.startAnimation(827);
			attachment.getItems().addItem(1779, 1);

			if (RandomUtils.nextInt(0, 3) == 1) {
				Server.getGlobalObjects().add(new GlobalObject(-1, location.getX(), location.getY(), location.getZ(), 0, 10, 50, 14896));
				stop();
			}
		}

		@Override
		public void stop() {
			super.stop();
			if (attachment != null && !attachment.isDisconnected()) {
				attachment.stopAnimation();
			}
		}

	}

}
