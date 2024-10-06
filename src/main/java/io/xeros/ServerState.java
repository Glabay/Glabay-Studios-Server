package io.xeros;

import lombok.Getter;

@Getter
public enum ServerState {
	PUBLIC(Configuration.PORT_DEFAULT, false, true),
	TEST_PUBLIC(Configuration.PORT_DEFAULT, false, true),

	/**
	 * Same as public but no sql and allows item spawning.
	 */
	PUBLIC_BETA(Configuration.PORT_DEFAULT, true, false,
        "Thank you for testing the %s beta.".formatted(Configuration.SERVER_NAME),
			"Please report any bugs in the discord."),

	/**
	 * Uses {@link Configuration#PORT_TEST} as the port.
	 * Many in-game events are sped up to allow for easier testing.
	 */
	TEST(Configuration.PORT_DEFAULT, true, false),

	/**
	 * Debug the server with auto-admin, spawning, no tutorial, no sql, multi-log enabled.
	 * Many other conditions to enable rapid testing.
	 * Also counts as {@link ServerState#TEST} mode.
	 */
	DEBUG(Configuration.PORT_DEFAULT, true, false),
	DEBUG_SQL(Configuration.PORT_DEFAULT, true, true),
	;

	private final int port;
	private final boolean openSpawning;
	private final boolean sqlEnabled;
	private final String[] loginMessages;

	ServerState(int port, boolean openSpawning, boolean sqlEnabled, String... loginMessages) {
		this.port = port;
		this.loginMessages = loginMessages;
		this.openSpawning = openSpawning;
		this.sqlEnabled = sqlEnabled;
	}
}
