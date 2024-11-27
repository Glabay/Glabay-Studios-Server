package io.xeros.model.entity.player;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.projectile.Projectile;
import io.xeros.model.SoundType;
import io.xeros.model.StillGraphic;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.mode.group.GroupIronmanRepository;
import io.xeros.model.projectile.ProjectileEntity;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.net.PacketBuilder;
import io.xeros.net.login.LoginReturnCode;
import io.xeros.net.login.RS2LoginProtocol;
import io.xeros.util.Misc;
import io.xeros.util.Stream;
import io.xeros.util.discord.Discord;
import io.xeros.util.logging.global.LoginLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerHandler {

	private static final Logger logger = LoggerFactory.getLogger(PlayerHandler.class);
	public static Player[] players = new Player[Configuration.MAX_PLAYERS];

	public static boolean updateAnnounced;
	public static boolean updateRunning;
	public static int updateSeconds;
	public static long updateStartTime;
	public static boolean kickAllPlayers;

	private static final Queue<Runnable> queuedActions = new ConcurrentLinkedQueue<>();
	private static final Queue<Player> loginQueue = new ConcurrentLinkedQueue<>();
	private static final Queue<PlayerSaveExecutor> logoutQueue = new ConcurrentLinkedQueue<>();

	public static void addQueuedAction(Runnable action) {
		queuedActions.add(action);
	}

	public static void processQueuedActions() {
		Runnable action;
		while ((action = queuedActions.poll()) != null) {
			try {
				action.run();
			} catch (Exception e) {
				logger.error("Error during queued actions.", e);
				e.printStackTrace(System.err);
			}
		}
	}

	public static Player getPlayerByLoginName(String name) {
		for (int d = 0; d < Configuration.MAX_PLAYERS; d++) {
			if (players[d] != null) {
				Player o = players[d];
				if (o.getLoginName().equalsIgnoreCase(name)) {
					return o;
				}
			}
		}
		return null;
	}

	public static Player getPlayerByIndex(int playerIndex) {
		return getOptionalPlayerByIndex(playerIndex).orElse(null);
	}

	public static Optional<Player> getOptionalPlayerByIndex(int playerIndex) {
		if (playerIndex >= players.length || playerIndex < 0 || players[playerIndex] == null) {
			return Optional.empty();
		}

		return Optional.of(players[playerIndex]);
	}

	public static Optional<Player> getOptionalPlayerByLoginName(String name) {
		return getPlayers().stream().filter(Objects::nonNull).filter(client -> client.getLoginName().equalsIgnoreCase(name)).findFirst();
	}

	public static Player getPlayerByLoginNameLong(long name) {
		for (int i = 0; i < Configuration.MAX_PLAYERS; i++) {
			if (players[i] == null)
				continue;
			if (players[i].getNameAsLong() == name)
				return players[i];
		}
		return null;
	}

	public static Optional<Player> getOptionalPlayerByDisplayName(String displayName) {
		Player player = getPlayerByDisplayName(displayName);
		return player != null ? Optional.of(player) : Optional.empty();
	}

	public static Player getPlayerByDisplayName(String displayName) {
		long longName = Misc.playerNameToInt64(displayName.toLowerCase());
		for (int i = 0; i < Configuration.MAX_PLAYERS; i++) {
			if (players[i] == null)
				continue;
			if (players[i].getDisplayNameLong() == longName)
				return players[i];
		}
		return null;
	}

	/**
	 * Finds a player that has the provided {@param name} as their {@link Player#getLoginName()}
	 * or {@link Player#getDisplayName()} ()}.
	 * @param name The login or display name
	 * @return the Player, if any was found.
	 */
	public static Player getPlayerByLoginOrDisplayName(String name) {
		long loginNameLong = Misc.playerNameToInt64(name);
		long displayName = Misc.playerNameToInt64(name.toLowerCase());
		return nonNullStream().filter(it -> it.getNameAsLong() == loginNameLong || it.getDisplayNameLong() == displayName)
				.findFirst().orElse(null);
	}

	/**
	 * @deprecated use {@link PlayerHandler#getPlayerByLoginName(String)} and {@link Player#getIndex()}.
	 */
	@Deprecated
	public static int getPlayerID(String playerName) {
		for (int i = 0; i < Configuration.MAX_PLAYERS; i++) {
			if (players[i] != null) {
				Player p = players[i];
				if (p.getLoginName().equalsIgnoreCase(playerName)) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * The next available slot between 1 and {@link Configuration#MAX_PLAYERS}.
	 *
	 * @return the next slot
	 */
	public int nextSlot() {
		for (int index = 1; index < Configuration.MAX_PLAYERS; index++) {
			if (players[index] == null) {
				return index;
			}
		}
		return -1;
	}

	public static int getPlayerCount() {
		return (int) Arrays.stream(players).filter(Objects::nonNull).count();
	}

	/**
	 * Create an int array of the specified length, containing all values between 0 and length once at random positions.
	 *
	 * @return The randomly shuffled array.
	 */
	private int[] shuffledList() {
		int[] array = new int[Configuration.MAX_PLAYERS];
		for (int i = 0; i < array.length; i++) {
			array[i] = i;
		}
		Random rand = new Random();
		for (int i = 0; i < array.length; i++) {
			int index = rand.nextInt(i + 1);
			int a = array[index];
			array[index] = array[i];
			array[i] = a;
		}
		return array;
	}

	private final List<Player> onlinePlayers = new ArrayList<>();

	public void updateOnlinePlayers() {
		onlinePlayers.clear();
		int[] randomOrder = shuffledList();
		for (int i = 0; i < Configuration.MAX_PLAYERS; i++) {
			Player player = players[randomOrder[i]];
			if (player != null && player.isActive) {
				onlinePlayers.add(player);
			}
		}
	}

	public static void addLoginQueue(Player player) {
		Preconditions.checkState(player.getIndex() == 0, "Player is already registered.");
		loginQueue.add(player);
	}

	private void processLoginQueue() {
		Player playerLoggingIn;
		int processed = 0;
		while (processed++ < 20 && (playerLoggingIn = loginQueue.poll()) != null) {
			int slot = nextSlot();
			try {
				int sameOnline = PlayerHandler.getSameComputerPlayerCount(playerLoggingIn.getMacAddress(), playerLoggingIn.getIpAddress(), playerLoggingIn.getUUID());
				if (!playerLoggingIn.isBot() && sameOnline >= Configuration.SAME_COMPUTER_CONNECTIONS_ALLOWED && !Server.isDebug()) {
					RS2LoginProtocol.sendReturnCode(playerLoggingIn.getSession(), playerLoggingIn, LoginReturnCode.LOGIN_LIMIT_EXCEEDED);
					return;
				}

				if (getPlayerByLoginNameLong(playerLoggingIn.getNameAsLong()) != null) {
					RS2LoginProtocol.sendReturnCode(playerLoggingIn.getSession(), playerLoggingIn, LoginReturnCode.ACCOUNT_ALREADY_ONLINE);
					return;
				}

				if (slot == -1) {
					RS2LoginProtocol.sendReturnCode(playerLoggingIn.getSession(), playerLoggingIn, LoginReturnCode.WORLD_FULL);
					return;
				}

				if (playerLoggingIn.getSession() != null) {
					final PacketBuilder bldr = new PacketBuilder();
					bldr.put((byte) 2);
					bldr.put((byte) playerLoggingIn.getRights().getPrimary().getValue());
					bldr.put((byte) 0);
					playerLoggingIn.getSession().write(bldr.toPacket());
				}

				players[slot] = playerLoggingIn;
				playerLoggingIn.setIndex(slot);
				playerLoggingIn.initialized = true;
				playerLoggingIn.finishLogin();
				players[slot].isActive = true;
				if (!playerLoggingIn.isBot()) {
					Server.getLogging().batchWrite(new LoginLog("Logged in", playerLoggingIn));
				}
			} catch (Exception e) {
				playerLoggingIn.forceLogout();
				playerLoggingIn.getPA().sendLogout();
				playerLoggingIn.initialized = false;
				playerLoggingIn.saveCharacter = false;
				playerLoggingIn.isActive = false;
				if (slot != -1) {
					players[slot] = null;
				}

				logger.error("Error during logging in {}", playerLoggingIn.getStateDescription(), e);
			}
		}
	}

	public static boolean isLoggingOut(String username) {
		return logoutQueue.stream().anyMatch(it -> it.getPlayer().getLoginName().equalsIgnoreCase(username));
	}

	private void processLogoutQueue() {
		PlayerSaveExecutor playerLoggingOut;
		int processed = 0;
		while (processed++ < 12 && (playerLoggingOut = logoutQueue.poll()) != null) {
			if (playerLoggingOut.finished()) {
				playerLoggingOut.getPlayer().isActive = false;
				players[playerLoggingOut.getPlayer().getIndex()] = null;
				playerLoggingOut.getPlayer().setIndex(0);
				if (Server.isTest() && !playerLoggingOut.getPlayer().isBot() || updateRunning) {
					logger.info("Logged out '{}', {} in queue.", playerLoggingOut.getPlayer().getLoginName(), logoutQueue.size());
				}
			} else {
				logoutQueue.add(playerLoggingOut);
			}
		}
	}

	public void process() {
		processLoginQueue();
		processLogoutQueue();
		processQueuedActions();

		nonNullStream().forEach(player -> {
			if (player.isReadyToLogout() || kickAllPlayers) {
				player.destruct();
				player.isActive = false;
				if (!isLoggingOut(player.getLoginName())) {
					PlayerSaveExecutor playerSaveExecutor = new PlayerSaveExecutor(player);
					playerSaveExecutor.request();
					logoutQueue.add(playerSaveExecutor);
				}
			}
		});
		
		updateOnlinePlayers();
		
		onlinePlayers.forEach(player -> {
			try {
				player.preProcessing();
				player.processQueuedPackets(true);
				player.processQueuedPackets(false);
			} catch (Exception e) {
				logger.error("Error during pre-processing {}", player.getStateDescription(), e);
				e.printStackTrace(System.err);
				player.forceLogout();
			}
		});

		onlinePlayers.forEach(player -> {
			try {
				if (player.playerFollowingIndex > 0) {
					player.getPA().followPlayer();
				} else if (player.npcFollowingIndex > 0) {
					player.getPA().followNpc();
				}
			} catch (Exception e) {
				logger.error("Error during following {}", player.getStateDescription(), e);
				e.printStackTrace(System.err);
				player.forceLogout();
			}
		});

		onlinePlayers.forEach(player -> {
			try {
				player.process();
			} catch (Exception e) {
				logger.error("Error during processing/logout {}", player.getStateDescription(), e);
				e.printStackTrace(System.err);
				player.forceLogout();
			}
		});

		onlinePlayers.forEach(player -> {
			try {
				player.attacking.stopCombatMovement();
				player.postProcessing();
				player.getNextPlayerMovement();
				player.checkInstanceCoords();
			} catch (Exception e) {
				logger.error("Error during movement {}", player.getStateDescription(), e);
				e.printStackTrace(System.err);
				player.forceLogout();
			}
		});

		onlinePlayers.forEach(player -> {
			try {
				player.processCombat();
				player.getPA().sendXpDrops();
				if (player.clan != null)
					player.clan.updateIfDirty(player);
			} catch (Exception e) {
				logger.error("Error during combat {}", player.getStateDescription(), e);
				e.printStackTrace(System.err);
				player.forceLogout();
			}
		});

		onlinePlayers.forEach(player -> {
			try {
				player.getDamageQueue().execute();
			} catch (Exception e) {
				logger.error("Error during damage processing {}", player.getStateDescription(), e);
				e.printStackTrace(System.err);
				player.forceLogout();
			}
		});

		onlinePlayers.forEach(player -> {
			try {
				player.update();
			} catch (Exception e) {
				logger.error("Error during player/npc updating {}", player.getStateDescription(), e);
				e.printStackTrace(System.err);
				player.forceLogout();
			}
		});

		onlinePlayers.forEach(player -> {
			try {
				player.clearUpdateFlags();
			} catch (Exception e) {
				logger.error("Error during clear player update flags {}", player.getStateDescription(), e);
				e.printStackTrace(System.err);
				player.forceLogout();
			}
		});

		// Reset npcs after update packet
		Server.npcHandler.resetUpdateFlags();
		Server.clanManager.clans.forEach(it -> it.setDirty(false));

		if (updateRunning && !updateAnnounced) {
			updateAnnounced = true;
			Server.UpdateServer = true;
		}

		if (updateRunning && (System.currentTimeMillis() - updateStartTime > (updateSeconds * 1000L))) {
			if (!kickAllPlayers) {
				kickAllPlayers = true;
				GroupIronmanRepository.serializeAllInstant();
			}
		}
	}

	public void sendObjectAnimation(GlobalObject object, int animation) {
		for (Player player : players) {
			if (player != null && player.distance(object.getPosition()) < 24
					&& player.getInstance() == object.getInstance() && player.getHeightLevel() == object.getPosition().getHeight()) {
				player.getPA().sendPlayerObjectAnimation(object.getPosition().getX(), object.getPosition().getY(), animation, object.getType(), object.getFace());
			}
		}
	}

	public void sendProjectile(ProjectileEntity projectile, InstancedArea instancedArea) {
		for (Player player : players) {
			if (player != null && player.distanceToPoint(projectile.getStart().getX(), projectile.getStart().getY()) < 24 && player.getInstance() == instancedArea && player.getHeightLevel() == projectile.getStart().getHeight()) {
				player.getPA().sendProjectile(projectile.getStart().getX(), projectile.getStart().getY(), projectile.getTarget().getX(), projectile.getTarget().getY(), projectile.getSlope(), projectile.getSpeed(), projectile.getProjectileId(), projectile.getStartHeight(), projectile.getEndHeight(), projectile.getLockon(), projectile.getDelay(), 16, 1, projectile.getStartDistanceOffset());
			}
		}
	}

	public void sendProjectile(Projectile projectile, InstancedArea instancedArea) {
		for (Player player : players) {
			if (player != null && player.distanceToPoint(projectile.getStart().getX(), projectile.getStart().getY()) < 24 && player.getInstance() == instancedArea && player.getHeightLevel() == projectile.getStart().getHeight()) {
				player.getPA().sendProjectile(projectile);
			}
		}
	}

	public void sendStillGfx(StillGraphic graphic, InstancedArea instancedArea) {
		for (Player player : players) {
			if (player != null && player.distance(graphic.getPosition()) < 24
					&& player.getInstance() == instancedArea && player.getHeightLevel() == graphic.getPosition().getHeight()) {
				player.getPA().stillGfx(graphic.getId(), graphic.getPosition().getX(), graphic.getPosition().getY(),
						graphic.getHeight(), graphic.getDelay());
			}
		}
	}

	public void sendSound(int id, Position position, InstancedArea instancedArea) {
		for (Player player : players) {
			if (player != null && player.distance(position) < 24
					&& player.getInstance() == instancedArea && player.getHeightLevel() == position.getHeight()) {
				player.getPA().sendSound(id, SoundType.AREA_SOUND, null);
			}
		}
	}

	public void sendSound(int id, Entity source) {
		Preconditions.checkState(source != null, "Source is null, consider using the position variant.");
		for (Player player : players) {
			if (player != null && player.distance(source.getPosition()) < 24
					&& player.getInstance() == source.getInstance() && player.getHeightLevel() == source.getPosition().getHeight()) {
				player.getPA().sendSound(id, source == player || source.isNPC() && player.npcAttackingIndex == source.getIndex() ? SoundType.SOUND
						: SoundType.AREA_SOUND, source);
			}
		}
	}

	public void updateNPC(Player plr, Stream str) {
		if (plr.getOutStream() == null)
			return;

		updateBlock.currentOffset = 0;

		str.createFrameVarSizeWord(65);
		str.initBitAccess();

		str.writeBits(8, plr.npcListSize);
		int size = plr.npcListSize;
		plr.npcListSize = 0;

		HashSet<NPC> hashes = new HashSet<>();

		// Update current list
		for (int i = 0; i < size; i++) {
			NPC npc = plr.npcList[i];
			if (plr.viewable(npc, true)) {
				npc.updateNPCMovement(str);
				npc.appendNPCUpdateBlock(plr, updateBlock);
				plr.npcList[plr.npcListSize++] = npc;
				hashes.add(npc);
			} else {
				str.writeBits(1, 1);
				str.writeBits(2, 3);
			}
		}

		// Clear npcs list of everything past the declared size
		for (int i = plr.npcListSize; i < plr.npcList.length; i++) {
			plr.npcList[i] = null;
		}

		// Add new npcs to the list
		int newNpcs = 0;
		for (int i = 0; i < NPCHandler.maxNPCs; i++) {
			if (NPCHandler.npcs[i] == null)
				continue;
			NPC npc = NPCHandler.npcs[i];

			if (!plr.viewable(npc, false) || hashes.contains(npc)) {
				continue;
			}

			if (plr.npcListSize + 1 < plr.npcList.length) {
				plr.addNewNPC(npc, str, updateBlock, npc.teleporting);

				// Don't add too many npcs in one tick
				if (newNpcs++ >= 20) {
					break;
				}
			} else {
				break;
			}
		}

		if (updateBlock.currentOffset > 0) {
			str.writeBits(14, 16383);
			str.finishBitAccess();
			str.writeBytes(updateBlock.buffer, updateBlock.currentOffset, 0);
		} else {
			str.finishBitAccess();
		}
		str.endFrameVarSizeWord();
	}

	private final Stream updateBlock = new Stream(new byte[Configuration.BUFFER_SIZE]);

	public void updatePlayer(Player plr, Stream str) {
		if (plr.getOutStream() == null)
			return;

		updateBlock.currentOffset = 0;

		if (plr.mapRegionDidChange) {
			str.createFrame(73);
			str.writeWordA(plr.mapRegionX + 6);
			str.writeUShort(plr.mapRegionY + 6);
			plr.resetAggressionTimer();
		}

		plr.updateThisPlayerMovement(str);
		boolean saveChatTextUpdate = plr.isChatTextUpdateRequired();
		plr.setChatTextUpdateRequired(false);
		plr.appendPlayerUpdateBlock(updateBlock);
		plr.setChatTextUpdateRequired(saveChatTextUpdate);
		str.writeBits(8, plr.playerListSize);
		int size = plr.playerListSize;
		if (size >= Configuration.MAX_PLAYERS_IN_LOCAL_LIST) {
			size = Configuration.MAX_PLAYERS_IN_LOCAL_LIST;
		}
		plr.playerListSize = 0;
		for (int i = 0; i < size; i++) {
			if (!plr.didTeleport && !plr.playerList[i].didTeleport && plr.withinDistance(plr.playerList[i])
					&& plr.getInstance() == plr.playerList[i].getInstance() && plr.playerList[i].isActive) {
				plr.playerList[i].updatePlayerMovement(str);
				plr.playerList[i].appendPlayerUpdateBlock(updateBlock);
				plr.playerList[plr.playerListSize++] = plr.playerList[i];
			} else {
				int id = plr.playerList[i].getIndex();
				plr.playerInListBitmap[id >> 3] &= ~(1 << (id & 7));
				str.writeBits(1, 1);
				str.writeBits(2, 3);
			}
		}

		for (int k = plr.playerListSize; k < plr.playerList.length; k++) {
			plr.playerList[k] = null;
		}

		for (int i = 0; i < Configuration.MAX_PLAYERS; i++) {
			if (players[i] == null || !players[i].isActive || players[i] == plr || players[i].getInstance() != plr.getInstance()) {
				continue;
			}
			int id = players[i].getIndex();

			if ((plr.playerInListBitmap[id >> 3] & (1 << (id & 7))) != 0) {
				continue;
			}
			if (!plr.withinDistance(players[i])) {
				continue;
			}
			plr.addNewPlayer(players[i], str, updateBlock);
		}

		if (updateBlock.currentOffset > 0) {
			str.writeBits(11, 2047);
			str.finishBitAccess();
			str.writeBytes(updateBlock.buffer, updateBlock.currentOffset, 0);
		} else {
			str.finishBitAccess();
		}

		str.endFrameVarSizeWord();
	}

	/**
	 * int id = plr.playerList[index].getIndex();
	 * 				plr.playerInListBitmap[id >> 3] &= ~(1 << (id & 7));
	 * 				str.writeBits(1, 1);
	 * 				str.writeBits(2, 3);
     */

	public static void executeGlobalStaffMessage(String message) {
		for (Player player : players) {
			if (player != null && player.getRights().hasStaffPosition()) {
				player.sendMessage(message);
			}
		}
	}

	public static void executeGlobalMessage(String message) {
		executeGlobalMessage(message, null);
	}

	public static void executeGlobalMessage(String message, Predicate<Player> sendPredicate) {
		Player[] clients = new Player[players.length];
		System.arraycopy(players, 0, clients, 0, players.length);
		Arrays.stream(clients).filter(Objects::nonNull).forEach(player -> {
			if (sendPredicate == null || sendPredicate.test(player))
				player.sendMessage(message);
		});
	}

	public static void message(Right right, String message) {
		List<Player> staff = nonNullStream().filter(Objects::nonNull).filter(p -> p.getRights().isOrInherits(right)).collect(Collectors.toList());
		sendMessage(message, staff);
	}

	public static void staffMessage(boolean discord, String message) {
		if (discord) {
			Discord.writeServerSyncMessage(message);
		}

		executeGlobalStaffMessage(message);
	}

	public static void sendMessage(String message, List<Player> players) {
		for (Player player : players) {
			if (Objects.isNull(player)) {
				continue;
			}
			player.sendMessage(message);
		}
	}

	public static int getSameComputerPlayerCount(String macAddress, String ipAddress, String uuid) {
		if (macAddress == null)
			macAddress = "";
		if (uuid == null)
			uuid = "";
		int online = 0;
		for (Player p : players) {
			if (p == null) continue;
			if (!macAddress.isEmpty() && p.getMacAddress().equals(macAddress)
					|| p.getIpAddress().equals(ipAddress)
					|| !uuid.isEmpty() && p.getUUID().equals(uuid)) {
				online++;
			}
		}
		return online;
	}

	public static int getUniquePlayerCount() {
		HashSet<String> ips = new HashSet<>();
		Arrays.stream(players).forEach(p -> {
			if (p != null) {
				ips.add(p.getIpAddress());
			}
		});
		return ips.size();
	}

	public static List<Player> getPlayers() {
		Player[] clients = new Player[players.length];
		System.arraycopy(players, 0, clients, 0, players.length);
		return Arrays.stream(clients).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Deprecated
	public static List<Player> getPlayerList() {
		return Arrays.stream(players).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public static java.util.stream.Stream<Player> stream() {
		return Arrays.stream(players);
	}

	public static java.util.stream.Stream<Player> nonNullStream() {
		return Arrays.stream(players).filter(Objects::nonNull);
	}
}
