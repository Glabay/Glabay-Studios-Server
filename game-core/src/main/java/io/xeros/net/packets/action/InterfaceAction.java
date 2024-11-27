package io.xeros.net.packets.action;

import io.xeros.content.party.PartyInterface;
import io.xeros.net.packets.PacketType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.Clan;
import io.xeros.model.world.ClanMember;
import io.xeros.util.Misc;

public class InterfaceAction implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		if (player.getMovementState().locked() || player.getLock().cannotInteract(player))
			return;
		int id = player.getInStream().readUnsignedWord();
		int action = player.getInStream().readUnsignedWord();
		player.debug(String.format("InterfaceAction id=%d, action=%d", action, id));

		if (PartyInterface.handleInterfaceAction(player, id, action))
			return;

		switch (id) {
		case 18304:
			if (action == 1) {
				player.getPA().getClan().delete();
				player.getPA().setClanData();
			}
			break;
		case 18307:
		case 18310:
		case 18313:
		case 18316:
			Clan clan = player.getPA().getClan();
			if (clan != null) {
				if (id == 18307) {
					clan.setRankCanJoin(action == 0 ? -1 : action);
				} else if (id == 18310) {
					clan.setRankCanTalk(action == 0 ? -1 : action);
				} else if (id == 18313) {
					clan.setRankCanKick(action == 0 ? -1 : action);
				} else if (id == 18316) {
					clan.setRankCanBan(action == 0 ? -1 : action);
				}
				String title = "";
				if (id == 18307) {
					title = clan.getRankTitle(clan.whoCanJoin) + (clan.whoCanJoin > Clan.Rank.ANYONE && clan.whoCanJoin < Clan.Rank.OWNER ? "+" : "");
				} else if (id == 18310) {
					title = clan.getRankTitle(clan.whoCanTalk) + (clan.whoCanTalk > Clan.Rank.ANYONE && clan.whoCanTalk < Clan.Rank.OWNER ? "+" : "");
				} else if (id == 18313) {
					title = clan.getRankTitle(clan.whoCanKick) + (clan.whoCanKick > Clan.Rank.ANYONE && clan.whoCanKick < Clan.Rank.OWNER ? "+" : "");
				} else if (id == 18316) {
					title = clan.getRankTitle(clan.whoCanBan) + (clan.whoCanBan > Clan.Rank.ANYONE && clan.whoCanBan < Clan.Rank.OWNER ? "+" : "");
				}
				player.getPA().sendString(title, id + 2);
			}
			break;

		default:
			// System.out.println("Interface action: [id=" + id +",action=" +
			// action +"]");
			break;
		}
		if (id >= 18323 && id < 18423) {
			Clan clan = player.getPA().getClan();
			if (clan != null && clan.rankedMembers != null && !clan.rankedMembers.isEmpty()) {
				String member = clan.rankedMembers.get(id - 18323);
				switch (action) {
				case 0:
					clan.demote(member);
					break;
				default:
					clan.setRank(member, action);
					break;
				}
				player.getPA().setClanData();
			}
		}
		if (id >= 18424 && id < 18524) {
			Clan clan = player.getPA().getClan();
			if (clan != null && clan.bannedMembers != null && !clan.bannedMembers.isEmpty()) {
				String member = clan.bannedMembers.get(id - 18424);
				switch (action) {
				case 0:
					clan.unbanMember(member);
					break;
				}
				player.getPA().setClanData();
			}
		}
		if (id >= 18144 && id < 18244) {
			for (int index = 0; index < 100; index++) {
				if (id == index + 18144) {
					ClanMember member = player.clan.activeMembers.get(id - 18144);
					switch (action) {
					case 0:
						if (player.clan.isFounder(player.getLoginName())) {
							player.getPA().showInterface(18300);
						}
						break;
					case 1:
						if (member.is(player)) {
							player.sendMessage("You can't kick yourself!");
						} else {
							if (player.clan.canKick(player.getLoginName())) {
								player.clan.kickMember(member.getLoginName());
							} else {
								player.sendMessage("You do not have sufficient privileges to do this.");
							}
						}
						break;
					case 2:
						if (member.is(player)) {
							break;
						}
						Clan clan = player.getPA().getClan();
						if (clan != null) {
							if (clan.isRanked(member.getLoginName())) {
								player.sendMessage("You cannot ban a ranked member.");
							} else {
								clan.banMember(Misc.formatPlayerName(member.getLoginName()));
								player.getPA().setClanData();
								clan.save();
							}
						}
						break;
					}
					break;
				}
			}
		}
	}
}