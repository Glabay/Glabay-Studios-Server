package io.xeros.punishments;

import lombok.Getter;

public enum PunishmentType {
	MUTE(0, "Muted", "mute.dat"),
	BAN(1, "Banned", "ban.dat"),
	NET_MUTE(2, "Net Muted", "network_mute.dat"),
	NET_BAN(3, "Net Banned", "network_ban.dat"),
	MAC_BAN(4, "Mac Banned", "mac_ban.dat"),

	;

	/**
	 * The identification value associated with this type of punishment
	 */
	private final int id;

	@Getter
    private final String formattedName;

	/**
	 * The name of the file associated with the type of punishment
	 */
	private final String fileName;

	/**
	 * Creates a new type of punishment associated with a file to log data
	 *
	 * @param file the name of the file
	 */
    PunishmentType(int id, String formattedName, String file) {
		this.id = id;
		this.formattedName = formattedName;
		this.fileName = file;
	}

	/**
	 * The name of the file
	 * 
	 * @return the name
	 */
	public final String getFileName() {
		return fileName;
	}

    /**
	 * The id associated with this type
	 * 
	 * @return the id
	 */
	public final int getId() {
		return id;
	}
}
