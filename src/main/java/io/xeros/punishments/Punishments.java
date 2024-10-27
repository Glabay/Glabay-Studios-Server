package io.xeros.punishments;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerAddresses;
import io.xeros.util.Stream;
import io.xeros.util.dateandtime.TimeSpan;
import lombok.Getter;

public class Punishments {

	/**
	 * A mapping of all punishments
     * -- GETTER --
     *  The list of punishments
     *
     * @return the punishments

     */
	@Getter
    private final Map<PunishmentType, List<Punishment>> punishments = new HashMap<>();

	/**
	 * A Queue of Punishments that will be added to the list of punishments
	 */
	private final Queue<Punishment> toAdd = new LinkedList<>();

	/**
	 * A Queue of punishments that will be removed from
	 */
	private final Queue<Punishment> toRemove = new LinkedList<>();

	/**
	 * A method that reads all information regarding punishments
	 * 
	 * @throws FileNotFoundException thrown if one of the punishment files doesn't exist
	 * @throws IOException thrown if any input/output exception occurs
	 */
	public final void initialize() throws FileNotFoundException, IOException {
		for (PunishmentType type : PunishmentType.values()) {
			List<Punishment> list = new ArrayList<>();
			read(type, list);
			punishments.put(type, list);
		}

		System.out.println("Finished loading all punishments.");
	}

	/**
	 * Reads a particular file and stores information into the given list
	 * 
	 * @param list the list the information will be stored into
	 * @throws FileNotFoundException thrown if the file does not exist
	 * @throws IOException thrown if any IO occurs
	 */
	public final void read(PunishmentType type, List<Punishment> list) throws FileNotFoundException, IOException {
		Path path = Paths.get(Server.getSaveDirectory(), "punishments");
		if(Files.notExists(path)) Files.createDirectories(path);

		Path file = path.resolve(type.getFileName());
		if(Files.notExists(file)) Files.createFile(file);

		byte[] data = Files.readAllBytes(file);

		if (data.length == 0) {
			return;
		}

		Stream stream = new Stream(data);

		long length = stream.readLong();

		while (stream.currentOffset < length) {
			long duration = stream.readLong();
			if (type.getId() > -1 && type.getId() < 5) {
				String information = stream.readString();
				list.add(new Punishment(type, duration, information));
			}
		}
	}

	/**
	 * Writes information to a particular file.
	 *
     */
	public final void write(PunishmentType punishmentType) {
		Path path = Paths.get(Server.getSaveDirectory(), "punishments", punishmentType.getFileName());
		Stream stream = new Stream();
		List<Punishment> punishments = this.punishments.get(punishmentType);

		for (Punishment punishment : punishments) {
			stream.writeQWord(punishment.getDuration());
			for (String information : punishment.getData()) {
				stream.writeString(information);
			}
		}
		Stream payload = new Stream();

		payload.ensureNecessaryCapacity(stream.buffer.length + 8);
		payload.writeQWord(stream.currentOffset + 8);
		payload.writeBytes(stream.buffer, stream.buffer.length, 0);

		try {
			Files.write(path, payload.getBuffer(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.out.println("Punishments - Check for error");
		}
	}

	/**
	 * Determines if certain information exists in a certain group of punishments
	 * 
	 * @param type the type of punishment
	 * @param information the information the punishment may contain
	 * @return {@code true} if the information matches any of the information in a punishment.
	 */
	public boolean contains(PunishmentType type, String information) {
		List<Punishment> list = punishments.get(type);

		if (list == null) {
			return false;
		}

		return list.stream().anyMatch(punishment -> punishment.contains(information));
	}

	/**
	 * Determines how many times some piece of information occurs in a particular list of data
	 * 
	 * @param type the relative type of punishment
	 * @param information the information we're counting the occurrences of
	 * @return a non-negative amount of occurrences, or zero.
	 */
	public int occurrences(PunishmentType type, String... information) {
		int occurances = 0;

		for (Punishment punishment : punishments.get(type)) {
			outer: for (String data : punishment.getData()) {
				for (String info : information) {
					if (data.equalsIgnoreCase(info)) {
						occurances++;
						continue outer;
					}
				}
			}
		}
		return occurances;
	}

	/**
	 * Adds a new punishment to the queue to be added in the future
	 * 
	 * @param punishment the new punishment to be added
	 */
	public boolean add(Punishment punishment) {
		return toAdd.add(punishment);
	}

	public void add(PunishmentType type, TimeSpan duration, String value) {
		add(new Punishment(type, duration.offsetCurrentTimeMillis(), value));
	}

	/**
	 * Adds a new punishment to the queue that will be removed in the future
	 * 
	 * @param punishment the punishment to be removed
	 */
	public boolean remove(Punishment punishment) {
		List<Punishment> punishments = this.punishments.get(punishment.getType());
		if (punishments == null) return false;
		var matches = punishments.stream().filter(p -> Arrays.stream(p.getData()).anyMatch(punishment::contains)).toList();
		if (matches.isEmpty()) return false;
        toRemove.addAll(matches);
		return true;
	}

	public void removeWithMessage(Player toMessage, PunishmentType type, String value) {
		if (remove(type, value)) {
			toMessage.sendMessage("Removed {} from '{}'.", type.name().toLowerCase(), value);
			return;
		}

		toMessage.sendMessage("No {} for '{}'.", type.name().toLowerCase(), value);
	}

	public boolean remove(PunishmentType type, String value) {
		Punishment punishment = getPunishment(type, value);
		if (punishment == null)
			return false;
		return remove(punishment);
	}

	public void forceQueueUpdate() {
		toAdd.forEach(punishment -> {
			List<Punishment> punishments = this.punishments.getOrDefault(punishment.getType(), new ArrayList<>());
			punishments.add(punishment);
			this.punishments.put(punishment.getType(), punishments);
		});
	}

	/**
	 * Attempts to retrieve a particular punishment for a player
	 * 
	 * @param type the punishment
	 * @param information the information in the punishment
	 * @return the punishment itself
	 */
	public Punishment getPunishment(PunishmentType type, String... information) {
		List<Punishment> list = punishments.get(type);

		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.stream().filter(punishment -> Arrays.stream(information).anyMatch(punishment::contains)).findFirst().orElse(null);
	}

	public boolean isNetMuted(Player player) {
		PlayerAddresses addresses = player.getValidAddresses();
		if (contains(PunishmentType.NET_MUTE, addresses.getIp()))
			return true;
		if (addresses.getMac() != null && contains(PunishmentType.NET_MUTE, addresses.getMac()))
			return true;
		if (addresses.getUUID() != null && contains(PunishmentType.NET_MUTE, addresses.getUUID()))
			return true;
		return false;
	}

    /**
	 * The punishments to be added
	 * 
	 * @return the add queue
	 */
	public Queue<Punishment> getAddQueue() {
		return toAdd;
	}

	/**
	 * The punishments to be removed
	 * 
	 * @return the remove queue
	 */
	public Queue<Punishment> getRemoveQueue() {
		return toRemove;
	}

}
