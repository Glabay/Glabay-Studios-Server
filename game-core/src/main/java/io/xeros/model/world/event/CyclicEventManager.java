package io.xeros.model.world.event;

import java.util.List;

import com.google.common.collect.Lists;

public class CyclicEventManager {
	
	private final List<CyclicEvent> events = Lists.newArrayList();
	
	public void wakeCycles() {
		events.parallelStream().forEach(CyclicEvent::wake);
	}
	
	public void register(CyclicEvent event) {
		events.add(event);
	}

	public void unregister(CyclicEvent cyclicEvent) {
		events.remove(cyclicEvent);
	}

}
