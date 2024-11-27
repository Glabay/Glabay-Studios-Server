package io.xeros.model.world;

import java.util.*;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;
import lombok.Getter;
import lombok.Setter;

/**
 * This class stores all information about the clan. This includes active members, banned members, ranked members and their ranks, clan title, and clan founder. All clan joining,
 * leaving, and moderation/setup is also handled in this class.
 * 
 * @author Galkon
 * 
 */
public class Clan {

	/**
	 * The attribute flag for the Player to limit clan chat spam
	 */
	public static String CLAN_CHAT_FLAG = "CLAN_CHAT_FLAG";

	/**
	 * The time it takes to send another CC message
	 */
	public static long CLAN_CHAT_DELAY = 1500;

	/**
	 * The title of the clan.
     * -- GETTER --
     *  Gets the title of the clan.
     * -- SETTER --
     *  Sets the title.


     */
	@Setter
    @Getter
    public String title;

	/**
	 * The founder of the clan.
     * -- GETTER --
     *  Gets the founder of the clan.
     * -- SETTER --
     *  Sets the founder.


     */
	@Setter
    @Getter
    public String founder;

	/**
	 * The active clan members.
	 */
	public ArrayList<ClanMember> activeMembers = new ArrayList<>();

	/**
	 * The banned members.
	 */
	public ArrayList<String> bannedMembers = new ArrayList<String>();

	/**
	 * The ranked clan members.
	 */
	public ArrayList<String> rankedMembers = new ArrayList<String>();

	/**
	 * The clan member ranks.
	 */
	public ArrayList<Integer> ranks = new ArrayList<Integer>();

	/**
	 * Needs to be updated for players.
	 */
	@Setter
    @Getter
    public boolean dirty = false;

	/**
	 * The clan ranks.
	 *
	 * @author Galkon
	 *
	 */
	public static class Rank {
		public final static int ANYONE = -1;
		public final static int FRIEND = 0;
		public final static int RECRUIT = 1;
		public final static int CORPORAL = 2;
		public final static int SERGEANT = 3;
		public final static int LIEUTENANT = 4;
		public final static int CAPTAIN = 5;
		public final static int GENERAL = 6;
		public final static int OWNER = 7;
	}

	/**
	 * The ranks privileges require (joining, talking, kicking, banning).
	 */
	public int whoCanJoin = Rank.ANYONE;
	public int whoCanTalk = Rank.ANYONE;
	public int whoCanKick = Rank.GENERAL;
	public int whoCanBan = Rank.OWNER;

	public void updateIfDirty(Player player) {
		if (isDirty()) {
			updateInterface(player);
		}
	}

	public void updateDisplayName(Player player) {
		if (activeMembers.removeIf(it -> it.getLoginName().equalsIgnoreCase(player.getLoginName()))) {
			activeMembers.add(new ClanMember(player.getLoginName(), player.getDisplayName()));
			sort();
			setDirty(true);
		}
	}

	/**
	 * Adds a member to the clan.
	 *
     */
	public void addMember(Player player) {
		if (isBanned(player.getLoginName())) {
			player.sendMessage("<col=FF0000>You are currently banned from this clan chat.</col>");
			return;
		}
		if (whoCanJoin > Rank.ANYONE && !isFounder(player.getLoginName())) {
			if (getRank(player.getLoginName()) < whoCanJoin) {
				player.sendMessage("Only " + getRankTitle(whoCanJoin) + "s+ may join this chat.");
				return;
			}
		}
		player.clan = this;
		player.setLastClanChat(getFounder());
		if (activeMembers.stream().noneMatch(it -> it.is(player))) {
			activeMembers.add(new ClanMember(player));
			sort();
			setDirty(true);
		}
	}

	private boolean remove(String displayName) {
		if (activeMembers.removeIf(it -> it.getDisplayName().equalsIgnoreCase(displayName))) {
			sort();
			setDirty(true);
			return true;
		}

		return false;
	}

	private boolean remove(Player player) {
		if (activeMembers.removeIf(it -> it.getLoginName().equals(player.getLoginName()))) {
			sort();
			setDirty(true);
			return true;
		}

		return false;
	}

	private void sort() {
		activeMembers.sort(Comparator.comparing(a -> a.getDisplayName().toLowerCase()));
	}

	/**
	 * Removes the player from the clan.
	 *
     */
	public void removeMember(Player player) {
		if (remove(player)) {
			player.clan = null;
			resetInterfaceOnLeave(player);
		}
	}

	/**
	 * Removes the player from the clan.
	 */
	public void removeMember(String name) {
		if (remove(name)) {
			Player player = PlayerHandler.getPlayerByDisplayName(name);
			if (player != null){
				player.clan = null;
				resetInterfaceOnLeave(player);
			}
		}
	}

	/**
	 * Updates the members on the interface for the player.
	 *
     */
	public void updateInterface(Player player) {
		player.getPA().sendString("Leave chat", 18135);
		player.getPA().sendString("Talking in: <col=FDFF38>" + getTitle() + "</col>", 18139);
		player.getPA().sendString("Owner: <col=FFFFFF>" + Misc.formatPlayerName(getFounder()) + "</col>", 18140);

		for (int index = 0; index < 100; index++) {
			if (index < activeMembers.size()) {
				player.getPA().sendFrame126("<clan=" + getRank(activeMembers.get(index).getLoginName()) + ">" + activeMembers.get(index).getDisplayName(), 18144 + index);
			} else {
				player.getPA().sendFrame126("", 18144 + index);
			}
		}
	}

	/**
	 * Resets the clan interface.
	 *
     */
	public void resetInterfaceOnLeave(Player player) {
		player.getPA().sendString("Join chat", 18135);
		player.getPA().sendString("Talking in: Not in chat", 18139);
		player.getPA().sendString("Owner: None", 18140);
		for (int index = 0; index < 100; index++) {
			player.getPA().sendString("", 18144 + index);
		}
	}

	/**
	 * Determines if a Player can send a clan chat message
	 * @param player The player to check
	 * @return True if the player can send a clan chat message
	 */
	public static boolean canSendMessage(Player player) {
		if (player.getRights().hasStaffPosition())
			return true;
		long timeStamp = player.getAttributes().getLong(CLAN_CHAT_FLAG, System.currentTimeMillis());
		return timeStamp <= System.currentTimeMillis();
	}

	/**
	 * Adds a clan chat timer
	 * @param player The player to add a clan chat timer to
	 */
	public static void addChatDelay(Player player) {
		player.getAttributes().setLong(CLAN_CHAT_FLAG, System.currentTimeMillis() + CLAN_CHAT_DELAY);
	}

	/**
	 * Sends a message to the clan.
	 */
	public void sendChat(Player paramClient, String paramString) {
		if (getRank(paramClient.getLoginName()) < this.whoCanTalk) {
			paramClient.sendMessage("Only " + getRankTitle(this.whoCanTalk) + "s+ may talk in this chat.");
			return;
		}
		if (paramClient.isHelpCcMuted() && "help".equalsIgnoreCase(paramClient.getLastClanChat())) {
			paramClient.sendMessage("You are muted from talking in the 'help' clan chat.");
			return;
		}
		if (Configuration.DISABLE_CC_MESSAGE)
			return;
		if (System.currentTimeMillis() < paramClient.muteEnd || Server.getPunishments().isNetMuted(paramClient)) {
			paramClient.sendMessage("You are muted, you cannot talk in this chat.");
			return;
		}

		paramString = paramString.replace(":tradereq", "");
		paramString = paramString.replace(":gamblereq:", "");

		if (paramString.length() <= 1) {
			return;
		}

		if (!Clan.canSendMessage(paramClient)) {
			paramClient.sendMessage("Please wait a moment before sending another message.");
			return;
		} else {
			Clan.addChatDelay(paramClient);
		}

		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Player c = PlayerHandler.players[j];
				if (c != null && activeMembers.stream().anyMatch(it -> it.is(c))) {
					if (!c.getFriendsList().getRepository().isIgnore(paramClient)) {
						c.sendMessage("/@bla@[@blu@" + getTitle() + "@bla@] " + paramClient.getDisplayNameFormatted() + ": @dre@"
								+ Misc.formatPlayerName(paramString.substring(1)));
					}
				}
			}
		}
	}

	/**
	 * Sends a message to the clan.
	 *
	 */
	public void sendMessage(String message) {
		for (int index = 0; index < Configuration.MAX_PLAYERS; index++) {
			Player p = PlayerHandler.players[index];
			if (p != null) {
				if (activeMembers.stream().anyMatch(it -> it.is(p))) {
					p.sendMessage(message);
				}
			}
		}
	}

	/**
	 * Sets the rank for the specified name.
	 *
     */
	public void setRank(String name, int rank) {
		if (rankedMembers.contains(name)) {
			ranks.set(rankedMembers.indexOf(name), rank);
		} else {
			rankedMembers.add(name);
			ranks.add(rank);
		}
		save();
	}

	/**
	 * Demotes the specified name.
	 *
     */
	public void demote(String name) {
		if (!rankedMembers.contains(name)) {
			return;
		}
		int index = rankedMembers.indexOf(name);
		rankedMembers.remove(index);
		ranks.remove(index);
		save();
	}

	/**
	 * Gets the rank of the specified name.
	 *
     */
	public int getRank(String name) {
		name = Misc.formatPlayerName(name);
		if (rankedMembers.contains(name)) {
			return ranks.get(rankedMembers.indexOf(name));
		}
		if (isFounder(name)) {
			return Rank.OWNER;
		}
		// This is doing I/O on the main thread and it's not even worth doing cause
		// clan chat friends isn't used lol.
//		if (PlayerSave.isFriend(getFounder(), name)) {
//			return Rank.FRIEND;
//		}
		return -1;
	}

	/**
	 * Can they kick?
	 *
     */
	public boolean canKick(String name) {
		if (isFounder(name)) {
			return true;
		}
        return getRank(name) >= whoCanKick;
    }

	/**
	 * Can they ban?
	 *
     */
	public boolean canBan(String name) {
		if (isFounder(name)) {
			return true;
		}
        return getRank(name) >= whoCanBan;
    }

	/**
	 * Returns whether or not the specified name is the founder.
	 *
     */
	public boolean isFounder(String name) {
        return getFounder().equalsIgnoreCase(name);
    }

	/**
	 * Returns whether or not the specified name is a ranked user.
	 *
     */
	public boolean isRanked(String name) {
		name = Misc.formatPlayerName(name);
        return rankedMembers.contains(name);
    }

	/**
	 * Returns whether or not the specified name is banned.
	 *
     */
	public boolean isBanned(String name) {
		name = Misc.formatPlayerName(name);
        return bannedMembers.contains(name);
    }

	/**
	 * Kicks the name from the clan chat.
	 *
     */
	public void kickMember(String name) {
		if (activeMembers.stream().noneMatch(it -> it.is(name))) {
			return;
		}
		if (name.equalsIgnoreCase(getFounder())) {
			return;
		}
		removeMember(name);
		Player player = PlayerHandler.getPlayerByLoginName(name);
		if (player != null) {
			player.sendMessage("You have been kicked from the clan chat.");
		}
		sendMessage("@blu@[Attempting to kick/ban @dre@'" + Misc.formatPlayerName(name) + "'" + " @blu@from this friends chat]");
	}

	/**
	 * Bans the name from entering the clan chat.
	 *
     */
	public void banMember(String name) {
		name = Misc.formatPlayerName(name);
		if (bannedMembers.contains(name)) {
			return;
		}
		if (name.equalsIgnoreCase(getFounder())) {
			return;
		}
		if (isRanked(name)) {
			return;
		}
		removeMember(name);
		bannedMembers.add(name);
		save();
		Player player = PlayerHandler.getPlayerByLoginName(name);
		if (player != null) {
			player.sendMessage("You have been kicked from the clan chat.");
		}
		sendMessage("@blu@[Attempting to kick/ban @dre@'" + Misc.formatPlayerName(name) + "'" + " @blu@from this friends chat]");
	}

	/**
	 * Unbans the name from the clan chat.
	 *
     */
	public void unbanMember(String name) {
		name = Misc.formatPlayerName(name);
		if (bannedMembers.contains(name)) {
			bannedMembers.remove(name);
			save();
		}
	}

	/**
	 * Saves the clan.
	 */
	public void save() {
		Server.clanManager.save(this);
	}

	/**
	 * Deletes the clan.
	 */
	public void delete() {
		for (ClanMember name : activeMembers) {
			remove(name.getLoginName());
			Player player = PlayerHandler.getPlayerByLoginName(name.getLoginName());
			if (player != null) {
				player.sendMessage("The clan you were in has been deleted.");
			}
		}
		Server.clanManager.delete(this);
	}

	/**
	 * Creates a new clan for the specified player.
	 *
     */
	public Clan(Player player) {
		setTitle(player.getLoginName());
		setFounder(player.getLoginName().toLowerCase());
	}

	/**
	 * Creates a new clan for the specified title and founder.
	 *
     */
	public Clan(String title, String founder) {
		setTitle(title);
		setFounder(founder);
	}

    /**
	 * Gets the rank title as a string.
	 *
     */
	public String getRankTitle(int rank) {
        return switch (rank) {
            case -1 -> "Anyone";
            case 0 -> "Friend";
            case 1 -> "Recruit";
            case 2 -> "Corporal";
            case 3 -> "Sergeant";
            case 4 -> "Lieutenant";
            case 5 -> "Captain";
            case 6 -> "General";
            case 7 -> "Only Me";
            default -> "";
        };
    }

	/**
	 * Sets the minimum rank that can join.
	 *
     */
	public void setRankCanJoin(int rank) {
		whoCanJoin = rank;
	}

	/**
	 * Sets the minimum rank that can talk.
	 *
     */
	public void setRankCanTalk(int rank) {
		whoCanTalk = rank;
	}

	/**
	 * Sets the minimum rank that can kick.
	 *
     */
	public void setRankCanKick(int rank) {
		whoCanKick = rank;
	}

	/**
	 * Sets the minimum rank that can ban.
	 *
     */
	public void setRankCanBan(int rank) {
		whoCanBan = rank;
	}

}