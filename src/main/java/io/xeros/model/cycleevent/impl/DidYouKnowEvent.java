package io.xeros.model.cycleevent.impl;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.xeros.Configuration;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;
import org.apache.commons.lang3.text.WordUtils;

public class DidYouKnowEvent extends Event<Object> {
	/**
	 * The amount of time in game cycles (600ms) that the event pulses at
	 */
	private static final int INTERVAL = Misc.toCycles(20, TimeUnit.MINUTES);

	/**
	 * A {@link Collection} of messages that are to be displayed
	 */
	private final List<String> MESSAGES = Misc.jsonArrayToList(Paths.get(Configuration.DATA_FOLDER, "cfg", "did_you_know.json"), String[].class);

	/**
	 * The index or position in the list that we're currently at
	 */
	private int position;

	/**
	 * Creates a new event to cycle through messages for the entirety of the runtime
	 */
	public DidYouKnowEvent() {
		super("", new Object(), INTERVAL);
	}

	@Override
	public void execute() {
		position++;
		if (position >= MESSAGES.size()) {
			position = 0;
		}
		List<String> messages = Arrays.asList(WordUtils.wrap(MESSAGES.get(position), 90).split("\\n"));
		messages.set(0, "[<col=255>News</col>] @red@" + messages.getFirst());
		PlayerHandler.nonNullStream().forEach(player -> {
			if (player.didYouKnow && PlayerHandler.getPlayerCount() > 5)
				messages.forEach(player::sendMessage);
		});
	}

}