package io.xeros.model.cycleevent;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CycleEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(CycleEventHandler.class);
    private static CycleEventHandler instance = new CycleEventHandler();

    public static CycleEventHandler getSingleton() {
        if (instance == null) {
            instance = new CycleEventHandler();
        }
        return instance;
    }

    private final Queue<CycleEventContainer> pending;
    @Getter
    private final List<CycleEventContainer> events;
    private final Set<Object> owners = new HashSet<>();

    public CycleEventHandler() {
        this.pending = new ArrayDeque<>(150);
        this.events = new LinkedList<>();
    }

    public CycleEventContainer addEvent(Object owner, CycleEvent event, int cyclesBetweenExecution) {
        return addEvent(owner, event, cyclesBetweenExecution, false);
    }

    public CycleEventContainer addEvent(Object owner, CycleEvent event, int cyclesBetweenExecution, boolean immediateExecution) {
        return addEvent(-1, owner, event, cyclesBetweenExecution, immediateExecution);
    }

    public CycleEventContainer addEvent(int id, Object owner, CycleEvent event, int cyclesBetweenExecution) {
        return addEvent(id, owner, event, cyclesBetweenExecution, false);
    }

    public CycleEventContainer addEvent(int id, Object owner, CycleEvent event, int cyclesBetweenExecution, boolean immediateExecution) {
        CycleEventContainer cycleEventContainer = new CycleEventContainer(id, owner, event, cyclesBetweenExecution);
        if (immediateExecution) {
            try {
                event.execute(cycleEventContainer);
            } catch (Exception e) {
                logger.error("An error occurred during event immediate execution, task has been stopped.", e);
                e.printStackTrace(System.err);
                cycleEventContainer.stop();
                return null;
            }
        }
        if (cycleEventContainer.isRunning()) {
            this.pending.add(new CycleEventContainer(id, owner, event, cyclesBetweenExecution));
        }
        return cycleEventContainer;
    }

    public void addEvent(CycleEventContainer container) {
        pending.add(container);
    }

    public void process() {
        CycleEventContainer container;
        while ((container = pending.poll()) != null) {
            if (container.isRunning()) {
                events.add(container);
            }
        }

        updateOwnerSet();

        Iterator<CycleEventContainer> it = events.iterator();
        List<CycleEventContainer> randomizedEvents = new ArrayList<>();
        while (it.hasNext()) {
            container = it.next();
            try {
                if (container != null) {
                    if (container.isRunning()) {
                        container.update();
                        if (container.needsExecution()) {
                            if (container.isRandomized()) {
                                randomizedEvents.add(container);
                            } else {
                                container.execute();
                            }
                        }
                    }
                    if (!container.isRunning()) {
                        it.remove();
                    }
                }
            } catch (Exception e) {
                container.stop();
                logger.error("An error occurred while processing tasks, task has been stopped.", e);
                e.printStackTrace(System.err);
            }
        }

        updateOwnerSet();

        if (!randomizedEvents.isEmpty()) {
            Collections.shuffle(randomizedEvents);
            randomizedEvents.forEach(eventContainer -> {
                try {
                    eventContainer.execute();
                } catch (Exception e) {
                    logger.error("An error occurred while processing tasks, task has been stopped.", e);
                    eventContainer.stop();
                    e.printStackTrace(System.err);
                }
            });
        }

        updateOwnerSet();
    }

    private void updateOwnerSet() {
        owners.clear();
        for (CycleEventContainer event : events) {
            if (event.getOwner() == null)
                continue;
            owners.add(event.getOwner());
        }
    }

    public void stopEvents(Object owner) {
        for (CycleEventContainer container : events) {
            if (container.getOwner() != null && container.getOwner().equals(owner)) {
                container.stop();
            }
        }
        for (CycleEventContainer container : pending) {
            if (container.getOwner() != null && container.getOwner().equals(owner)) {
                container.stop();
            }
        }
    }

    public void stopEvents(Object owner, int id) {
        if (id == -1) {
            throw new IllegalStateException("Illegal identification value, -1 is not permitted.");
        }
        for (CycleEventContainer c : events) {
            if (c.getOwner() == owner && id == c.getID()) {
                // '==' equals????
                c.stop();
            }
        }
        for (CycleEventContainer container : pending) {
            if (container.getOwner() != null && container.getOwner().equals(owner) && id == container.getID()) {
                container.stop();
            }
        }
    }

    public void stopEvents(int id) {
        if (id == -1) {
            throw new IllegalStateException("Illegal identification value, -1 is not permitted.");
        }
        for (CycleEventContainer c : events) {
            if (id == c.getID()) {
                c.stop();
            }
        }
        for (CycleEventContainer container : pending) {
            if (container.getID() == id) {
                container.stop();
            }
        }
    }

    /**
     * Determines if an event is active where the object passed is the owner.
     * 
     * @param owner the owner of the event
     * @return true if the event is alive
     */
    public boolean isAlive(Object owner) {
        return owners.contains(owner);
        //Optional<CycleEventContainer> op = events.stream().filter(Objects::nonNull).filter(container -> container.getOwner() != null && container.getOwner().equals(owner)).findFirst();
        //return op.isPresent();
    }

    public interface Event {
        int BONE_ON_ALTAR = 10010;
        int OVERLOAD_BOOST_ID = 10020;
        int OVERLOAD_HITMARK_ID = 10025;
        int DIVINE_COMBAT_POTION = 10040;
        int DIVINE_RANGE_POTION = 10045;
        int DIVINE_MAGIC_POTION = 10050;
        int SKILLING = 10049;
    }
}
