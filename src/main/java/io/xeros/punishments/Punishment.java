package io.xeros.punishments;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class Punishment {

	/**
	 * The type of punishment
     * -- GETTER --
     *  The type of punishment
     *
     * @return the type

     */
	private final PunishmentType type;

	/**
	 * The duration of the punishment
     * -- GETTER --
     *  The duration of the punishment
     *
     * @return the duration

     */
	private final long duration;

	/**
	 * An array of information that pertains to this punishment
     * -- GETTER --
     *  The data or information about this punishment
     *
     * @return the data

     */
	private final String[] data;

	/**
	 * A new punishment with some information
	 * 
	 * @param type the type of punishment
	 * @param data the group of data
	 */
	public Punishment(PunishmentType type, long duration, String... data) {
		this.type = type;
		this.duration = duration;
		this.data = data;
	}

	/**
	 * Determines if any information from the punishment matches the information given
	 * 
	 * @param information the information given
	 * @return {@code true} if any of the information matches.
	 */
	public boolean contains(String information) {
		return Arrays.stream(data).anyMatch(s -> s.equalsIgnoreCase(information));
	}

    public String toString() {
		return Arrays.toString(data);
	}

}
