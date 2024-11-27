package io.xeros.model.cycleevent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DelayEvent extends Event<Object> {

	private static final Logger logger = LoggerFactory.getLogger(DelayEvent.class);

	public DelayEvent(int ticks) {
		super(new Object(), ticks);
	}

	public abstract void onExecute();

	@Override
	public void execute() {
		try {
			onExecute();
			stop();
		} catch (Exception e) {
			logger.error("Error during delay event.", e);
			e.printStackTrace(System.err);
			stop();
		}
	}
}
