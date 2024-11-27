package io.xeros.content.skills.hunter.trap;

import java.util.ArrayList;
import java.util.List;

import io.xeros.content.skills.hunter.Hunter;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.entity.player.Player;
import io.xeros.util.RandomGen;

/**
 * Represents a single task which will run for each trap.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 *
 */
public final class TrapTask extends CycleEvent {

	/**
	 * The player this task is dependant of.
	 */
	private final Player player;

	/**
	 * The random generator which will generate random values.
	 */
	private final RandomGen gen = new RandomGen();

	/**
	 * The trap this task is running for.
	 */
	public List<Trap> trap = new ArrayList<>();

	/**
	 * Constructs a new {@link TrapTask}.
	 * @param player	{@link #player}.
	 */
	public TrapTask(Player player) {
		this.player = player;
	}

	@Override
	public void update(CycleEventContainer container) {
		if(Hunter.GLOBAL_TRAPS.get(player) == null
				|| !Hunter.GLOBAL_TRAPS.get(player).getTask().isPresent()
				|| Hunter.GLOBAL_TRAPS.get(player).getTraps().isEmpty()) {
			container.stop();
			return;
		}

		for(Trap trap : trap) {
			boolean withinDistance = player.heightLevel == trap.getObject().getHeight() && Math.abs(player.absX - trap.getObject().getX()) <= 15 && Math.abs(player.absY - trap.getObject().getY()) <= 15;
			if(!withinDistance && !trap.isAbandoned()) {
				Hunter.abandon(player, trap, false);
			}
		}

	}
	
	@Override
	public void execute(CycleEventContainer container) {
		if (Hunter.GLOBAL_TRAPS.get(player) == null)
			return;
		trap.clear();
		trap.addAll(Hunter.GLOBAL_TRAPS.get(player).getTraps());
		Trap trap = gen.random(this.trap);

		if(!Hunter.getTrap(player, trap.getObject()).isPresent() || !trap.getState().equals(Trap.TrapState.PENDING)) {
			return;
		}

		trap.onSequence(container);
	}
}
