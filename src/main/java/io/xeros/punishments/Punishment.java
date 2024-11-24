package io.xeros.punishments;

import java.util.Arrays;

public record Punishment(PunishmentType type, long duration, String... data) {
	public boolean contains(String information) {
		return Arrays.stream(data).anyMatch(s -> s.equalsIgnoreCase(information));
	}
	public String toString() {
		return Arrays.toString(data);
	}
}
