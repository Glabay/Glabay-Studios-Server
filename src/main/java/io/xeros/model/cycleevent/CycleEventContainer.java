package io.xeros.model.cycleevent;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The wrapper for our event
 * 
 * @author Stuart <RogueX>
 * @author Null++
 * 
 */

public class CycleEventContainer {

	private static final Logger logger = LoggerFactory.getLogger(CycleEventContainer.class);

	/**
	 * Event owner
     * -- GETTER --
     *  Returns the owner of the event
     *

     */
	@Getter
    private final Object owner;

	/**
	 * Is the event running or not
	 */
	private boolean isRunning;

	/**
	 * The amount of cycles per event execution
	 */
	@Getter
    private int cyclesBetweenExecution;

	/**
	 * The actual event
	 */
	@Getter
    private final CycleEvent event;

	/**
	 * The current amount of cycles passed
     * -- SETTER --
     *  Sets the current execution cycle to a new value.
     *

     */
	@Setter
    private int currentExecutionCycle;

	/**
	 * The event ID
	 */
	private final int eventID;

	/**
	 * The total sum of game ticks that have passed during the lifetime of the event
     * -- GETTER --
     *  The number of game ticks that have passed since the creation of the event.
     *

     */
	@Getter
    private int totalTicks;

	/**
	 * The total sum of executions that have happened to this event.
	 */
	@Getter
    private int totalExecutions;

	/**
	 * Determines if this event should be randomized every cycle
     * -- GETTER --
     *  Randomization occurs during the process of the main game loop. Events that are randomized are swapped randomly in execution order until there are none left.
     *

     */
	@Getter
    private boolean randomized;

	/**
	 * Sets the event containers details
	 * 
	 * @param owner , the owner of the event
	 * @param event , the actual event to run
	 * @param cyclesBetweenExecution , the cycles between execution of the event
	 */
	public CycleEventContainer(int id, Object owner, CycleEvent event, int cyclesBetweenExecution) {
		Preconditions.checkState(owner != null, "Owner cannot be null.");
		this.eventID = id;
		this.owner = owner;
		this.event = event;
		this.isRunning = true;
		this.currentExecutionCycle = 0;
		this.cyclesBetweenExecution = cyclesBetweenExecution;
	}

	public CycleEventContainer(int id, Object owner, CycleEvent event, int tick, boolean randomized) {
		this(id, owner, event, tick);
		this.randomized = randomized;
	}

	@Override
	public String toString() {
		return "CycleEventContainer{" +
				"owner=" + owner +
				", isRunning=" + isRunning +
				", cyclesBetweenExecution=" + cyclesBetweenExecution +
				", event=" + event +
				", eventID=" + eventID +
				", totalTicks=" + totalTicks +
				", totalExecutions=" + totalExecutions +
				'}';
	}

	/**
	 * Execute the contents of the event
	 */
	public void execute() {
		event.execute(this);
	}

	public void update() {
		event.update(this);
	}

	/**
	 * Stop the event from running
	 */
	public void stop() {
		isRunning = false;
		try {
			event.onStopped();
		} catch (Exception e) {
			logger.error("Error caught in cycle event.", e);
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Does the event need to be ran?
	 * 
	 * @return true yes false no
	 */
	public boolean needsExecution() {
		if (!this.isRunning()) {
			return false;
		}
		totalTicks++;
		if (++this.currentExecutionCycle >= this.cyclesBetweenExecution) {
			totalExecutions++;
			this.currentExecutionCycle = 0;
			return true;
		}
		return false;
	}

    /**
	 * Is the event running?
	 * 
	 * @return true yes false no
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Returns the event id
	 *
	 * @return id
	 */
	public int getID() {
		return eventID;
	}

    /**
	 * Sets the cycles between each execution and resets the current position of the execution cycle.
	 * @param cyclesBetweenExecution the amount of cycles between executions.
	 */
	public void setCyclesBetweenExecution(int cyclesBetweenExecution) {
		this.currentExecutionCycle = 0;
		this.cyclesBetweenExecution = cyclesBetweenExecution;
	}

}