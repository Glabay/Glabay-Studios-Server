package io.xeros.model.world;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;
import lombok.Getter;

@Getter
public class ClanManager {

	public ArrayList<Clan> clans = new ArrayList<>();

	public static String getSaveDirectory() {
		return Server.getSaveDirectory() + "/clan/";
	}

	public void create(Player paramClient) {
		if (paramClient.clan != null) {
			paramClient.sendMessage("@red@You must leave your current clan-chat before making your own.");
			return;
		}
		if (paramClient.inArdiCC) {
			return;
		}
		Clan localClan = new Clan(paramClient);
		this.clans.add(localClan);
		localClan.addMember(paramClient);
		localClan.save();
		paramClient.getPA().setClanData();
		paramClient.sendMessage("@red@You may change your clan settings by clicking the 'Clan Setup' button.");
	}

	public Clan getClan(String paramString) {
		return clans.stream()
			.filter(clan -> clan.getFounder().equalsIgnoreCase(paramString))
			.findFirst()
			.orElseGet(() -> {
				var localClan = read(paramString);
				if (localClan != null) {
					this.clans.add(localClan);
					return localClan;
				}
				return null;
			});
	}

	/**
	 * Returns the Help clan or creates it if it doesn't exist yet.
	 * 
	 * @return The Help clan.
	 */
	public Clan getHelpClan() {
        for (Clan clan : this.clans) {
            if (clan.getFounder().equalsIgnoreCase("Help")) {
                return clan;
            }
        }

		Clan localClan = read("Help");
		if (localClan != null) {
			clans.add(localClan);
			return localClan;
		}
		localClan = new Clan("Help", "Help");
		clans.add(localClan);
		localClan.save();
		return localClan;
	}

	public void delete(Clan paramClan) {
		if (paramClan == null) {
			return;
		}
		Misc.createDirectory(getSaveDirectory());
		File localFile = new File(getSaveDirectory() + paramClan.getFounder() + ".cla");
		if (localFile.delete()) {
			Player localClient = PlayerHandler.getPlayerByLoginName(paramClan.getFounder());
			if (localClient != null) {
				localClient.sendMessage("@red@Your clan has been deleted.");
			}
			this.clans.remove(paramClan);
		}
	}

	public void save(Clan paramClan) {
		if (paramClan == null) {
			return;
		}
		Misc.createDirectory(getSaveDirectory());
		File localFile = new File(getSaveDirectory() + paramClan.getFounder() + ".cla");
		try {
			RandomAccessFile localRandomAccessFile = new RandomAccessFile(localFile, "rwd");

			localRandomAccessFile.writeUTF(paramClan.getTitle());
			localRandomAccessFile.writeByte(paramClan.whoCanJoin);
			localRandomAccessFile.writeByte(paramClan.whoCanTalk);
			localRandomAccessFile.writeByte(paramClan.whoCanKick);
			localRandomAccessFile.writeByte(paramClan.whoCanBan);
			if ((paramClan.rankedMembers != null) && (!paramClan.rankedMembers.isEmpty())) {
				localRandomAccessFile.writeShort(paramClan.rankedMembers.size());
				for (int i = 0; i < paramClan.rankedMembers.size(); i++) {
					localRandomAccessFile.writeUTF(paramClan.rankedMembers.get(i));
					localRandomAccessFile.writeShort(paramClan.ranks.get(i));
				}
			} else {
				localRandomAccessFile.writeShort(0);
			}

			localRandomAccessFile.close();
		} catch (IOException localIOException) {
			localIOException.printStackTrace(System.err);
		}
	}

	private Clan read(String paramString) {
		Misc.createDirectory(getSaveDirectory());
		File localFile = new File(getSaveDirectory() + paramString + ".cla");
		if (!localFile.exists()) {
			return null;
		}
		try {
			RandomAccessFile localRandomAccessFile = new RandomAccessFile(localFile, "rwd");

			Clan localClan = new Clan(localRandomAccessFile.readUTF(), paramString);
			localClan.whoCanJoin = localRandomAccessFile.readByte();
			localClan.whoCanTalk = localRandomAccessFile.readByte();
			localClan.whoCanKick = localRandomAccessFile.readByte();
			localClan.whoCanBan = localRandomAccessFile.readByte();
			int i = localRandomAccessFile.readShort();
			if (i != 0) {
				for (int j = 0; j < i; j++) {
					localClan.rankedMembers.add(localRandomAccessFile.readUTF());
					localClan.ranks.add((int) localRandomAccessFile.readShort());
				}
			}
			localRandomAccessFile.close();

			return localClan;
		} catch (IOException localIOException) {
			localIOException.printStackTrace(System.err);
		}
		return null;
	}

	/**
	 * Attempt to rejoin the last Clan channel upon login.
	 *
     */
	public void joinOnLogin(Player client) {
		String lastChannel = client.getLastClanChat();
		if (lastChannel != null && !lastChannel.isEmpty()) {
			Clan localClan = getClan(lastChannel);
			if (localClan != null) {
				localClan.addMember(client);
			} else {
				client.sendMessage(lastChannel + " no longer exists.");
			}
		}
	}

	public boolean clanExists(String paramString) {
		Misc.createDirectory(getSaveDirectory());
		File localFile = new File(getSaveDirectory() + paramString + ".cla");
		return localFile.exists();
	}

}