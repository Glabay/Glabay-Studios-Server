package io.xeros.content.worldevent;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.worldevent.impl.HesporiWorldEvent;
import io.xeros.content.worldevent.impl.TournamentWorldEvent;
import io.xeros.content.worldevent.impl.WildernessBossWorldEvent;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

public class WorldEventContainer {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(WorldEventContainer.class.getName());
    private static final int CYCLES_BETWEEN_EVENTS = Misc.toCycles(45, TimeUnit.MINUTES);
    private static final Object CYCLE_EVENT_OBJECT = new Object();
    private static final WorldEventContainer SINGLETON = new WorldEventContainer();
    public static final List<WorldEvent> WORLD_EVENT_LIST = Collections.unmodifiableList(Lists.newArrayList(new TournamentWorldEvent(), new HesporiWorldEvent(), new WildernessBossWorldEvent()));

    public static WorldEventContainer getInstance() {
        return SINGLETON;
    }

    private WorldEventState worldEventState;
    private boolean eventRunning;
    private boolean triggerImmediateEvent;
    private int cyclesBetweenEvents;

    private WorldEventContainer() {
    }

    public void initialise() throws IOException {
        if (Server.isDebug() || Server.isTest()) {
            cyclesBetweenEvents = Misc.toCycles(15, TimeUnit.MINUTES);
        } else {
            cyclesBetweenEvents = CYCLES_BETWEEN_EVENTS;
        }
        worldEventState = WorldEventState.load();
        scheduleNext();
    }

    /**
     * Start up the cycle events that handle initializing world events.
     */
    private void scheduleNext() {
        log.fine("Scheduling next world event.");
        CycleEventHandler.getSingleton().stopEvents(CYCLE_EVENT_OBJECT);
        CycleEventHandler.getSingleton().addEvent(CYCLE_EVENT_OBJECT, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                worldEventState.setTicksUntilNextEvent(worldEventState.getTicksUntilNextEvent() - 50);
                if (triggerImmediateEvent || worldEventState.getTicksUntilNextEvent() <= 0) {
                    triggerImmediateEvent = false;
                    next();
                    container.stop();
                }
            }
        }, 50);
    }

    private void next() {
        cancelCurrent();
        int next = getNextIndex();
        worldEventState.setWorldEventIndex(next);
        WorldEvent worldEvent = WORLD_EVENT_LIST.get(next);
        worldEvent.init();
        eventRunning = true;
        //log.info("Starting world event " + worldEvent.getEventName().toLowerCase());
        worldEventState.setTicksUntilNextEvent(cyclesBetweenEvents);
        worldEvent.announce(PlayerHandler.getPlayers());
        PlayerHandler.getPlayers().forEach(p -> p.getQuestTab().updateInformationTab());
        scheduleNext();
    }

    private void cancelCurrent() {
        if (eventRunning) {
            WORLD_EVENT_LIST.get(worldEventState.getWorldEventIndex()).dispose();
        }
    }

    public void startEvent(WorldEvent event) {
        for (int index = 0; index < WORLD_EVENT_LIST.size(); index++) {
            if (WORLD_EVENT_LIST.get(index).getEventName().equals(event.getEventName())) {
                cancelCurrent();
                worldEventState.setWorldEventIndex(index == 0 ? WORLD_EVENT_LIST.size() - 1 : index - 1);
                triggerImmediateEvent = true;
                return;
            }
        }
    }

    private int getNextIndex() {
        return (worldEventState.getWorldEventIndex() + 1) % WORLD_EVENT_LIST.size();
    }

    private WorldEvent getNextEvent() {
        return WORLD_EVENT_LIST.get(getNextIndex());
    }

    private Optional<WorldEvent> getCurrentEvent() {
        if (eventRunning) {
            WorldEvent event = WORLD_EVENT_LIST.get(worldEventState.getWorldEventIndex());
            if (event.isEventCompleted()) {
                return Optional.empty();
            }
            return Optional.of(event);
        } else {
            return Optional.empty();
        }
    }

    public List<String> getWorldEventStatuses() {
        List<String> stringList = Lists.newArrayList();
        Optional<WorldEvent> event = getCurrentEvent();
        if (event.isPresent()) {
            stringList.add(event.get().getCurrentStatus());
            if (!getNextEvent().isOutlast()) {
                stringList.add(getNextStatusString(getNextEvent()));
            }
            if (!event.get().isOutlast()) {
                stringList.add(getNextStatusString(new TournamentWorldEvent()));
            }
        } else {
            stringList.add(getNextStatusString(getNextEvent()));
            if (!getNextEvent().isOutlast()) {
                stringList.add(getNextStatusString(new TournamentWorldEvent()));
            }
        }
        return stringList;
    }

    private String getNextStatusString(WorldEvent worldEvent) {
        return worldEvent.getEventName() + " " + worldEvent.getStartDescription() + " in " + getTimeUntilEvent(worldEvent);
    }

    public String getTimeUntilEvent(WorldEvent worldEvent) {
        return Misc.cyclesToTime(getCyclesUntilEvent(worldEvent));
    }

    public int getCyclesUntilEvent(WorldEvent worldEvent) {
        Optional<WorldEvent> currentEvent = getCurrentEvent();
        if (currentEvent.isPresent() && currentEvent.get().getEventName().equals(worldEvent.getEventName())) {
            return worldEventState.getTicksUntilNextEvent();
        } else {
            int minutes = 0;
            int index = worldEventState.getWorldEventIndex();
            minutes += worldEventState.getTicksUntilNextEvent();
            while (true) {
                index = (index + 1) % WORLD_EVENT_LIST.size();
                if (WORLD_EVENT_LIST.get(index).getEventName().equals(worldEvent.getEventName())) {
                    return minutes;
                } else {
                    minutes += cyclesBetweenEvents;
                }
            }
        }
    }

    public void setTriggerImmediateEvent(boolean triggerImmediateEvent) {
        this.triggerImmediateEvent = triggerImmediateEvent;
    }

    public int getCyclesBetweenEvents() {
        return cyclesBetweenEvents;
    }
}
