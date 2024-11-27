package io.xeros.model.multiplayersession.duel;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.multiplayersession.Multiplayer;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.util.Misc;

public class Duel extends Multiplayer {

	public Duel(Player player) {
		super(player);
	}

	@Override
	public boolean requestable(Player requested) {
		if (!Configuration.NEW_DUEL_ARENA_ACTIVE) {
			player.getDH().sendStatement("@red@Dueling Temporarily Disabled", "The duel arena minigame is currently being rewritten.",
					"No player has access to this minigame during this time.", "", "Thank you for your patience, Developer J.");
			player.nextChat = -1;
			return false;
		}
		long milliseconds = (long) requested.playTime * 600;
		long days = TimeUnit.MILLISECONDS.toDays(milliseconds);

		if (!player.ignoreNewPlayerRestriction(requested)) {
			if (player.hasNewPlayerRestriction()) {
				player.sendMessage("You cannot request a duel, you must play for at least "
						+ Configuration.NEW_PLAYER_RESTRICT_TIME_MIN + " minutes.");
				return false;
			}
			if (requested.hasNewPlayerRestriction()) {
				player.sendMessage("You cannot duel this player, they have not played for "
						+ Configuration.NEW_PLAYER_RESTRICT_TIME_MIN + " minutes.");
				return false;
			}
		}

		/*if(days < 1){
			requested.sendMessage("@red@ You need to be at least 1 day old to stake.");
			requested.sendMessage("@red@ This is to prevent our new players from getting cleaned.");
			requested.sendMessage("@red@ Please enjoy all other aspects of the game though. Thanks.");
			return false;
		}*/

		if (player.getLootingBag().isWithdrawInterfaceOpen() || player.getLootingBag().isDepositInterfaceOpen() ||
				requested.getLootingBag().isWithdrawInterfaceOpen() || requested.getLootingBag().isDepositInterfaceOpen()) {
			return false;
		}
		if (!player.getMode().isStakingPermitted()) {
			player.sendMessage("You are not permitted to stake other players.");
			return false;
		}
		if (!requested.getMode().isStakingPermitted()) {
			player.sendMessage("That player is on a game mode that restricts staking.");
			return false;
		}
		if (Server.getMultiplayerSessionListener().requestAvailable(requested, player, MultiplayerSessionType.DUEL) != null) {
			player.sendMessage("You have already sent a request to this player.");
			return false;
		}
		if (Server.UpdateServer) {
			player.sendMessage("You cannot request or accept a duel request at this time.");
			player.sendMessage("The server is currently being updated.");
			return false;
		}
		if (player.connectedFrom.equalsIgnoreCase(requested.connectedFrom) && !player.getRights().isOrInherits(Right.MODERATOR)) {
			player.sendMessage("You cannot request a duel from someone on the same network.");
			return false;
		}
		if (Misc.distanceToPoint(player.getX(), player.getY(), requested.getX(), requested.getY()) > 15) {
			player.sendMessage("You are not close enough to the other player to request or accept.");
			return false;
		}
		if (!player.getPosition().inDuelArena()) {
			player.sendMessage("You must be in the duel arena area to do this.");
			return false;
		}
		if (!requested.getPosition().inDuelArena()) {
			player.sendMessage("The challenger must be in the duel arena area to do this.");
			return false;
		}
		if (player.getBH().hasTarget()) {
			if (player.getBH().getTarget().getLoginName().equalsIgnoreCase(requested.getLoginName())) {
				player.sendMessage("You cannot duel your bounty hunter target.");
				return false;
			}
		}
		if (Server.getMultiplayerSessionListener().inAnySession(player)) {
			player.sendMessage("You cannot request a duel whilst in a session.");
			return false;
		}
		if (Server.getMultiplayerSessionListener().inAnySession(requested)) {
			player.sendMessage("This player is currently is a session with another player.");
			return false;
		}
		if (player.teleTimer > 0 || requested.teleTimer > 0) {
			player.sendMessage("You cannot request or accept whilst you, or the other player are teleporting.");
			return false;
		}
		return true;
	}

	@Override
	public void request(Player requested) {

		if (Objects.isNull(requested)) {
			player.sendMessage("The player cannot be found, try again shortly.");
			return;
		}
		if (Objects.equals(player, requested)) {
			player.sendMessage("You cannot trade yourself.");
			return;
		}

		if (player.isGambleBanned()) {
			player.sendMessage("You cannot gamble.");
			return;
		}
		if (requested.isGambleBanned()) {
			player.sendMessage("That player cannot gamble.");
			return;
		}

		player.faceUpdate(requested.getIndex());
		DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().requestAvailable(player, requested, MultiplayerSessionType.DUEL);
		if (session != null) {
			session.getStage().setStage(MultiplayerSessionStage.OFFER_ITEMS);
			session.populatePresetItems();
			session.updateMainComponent();
			session.sendDuelEquipment();
			Server.getMultiplayerSessionListener().removeOldRequests(player);
			Server.getMultiplayerSessionListener().removeOldRequests(requested);
			session.getStage().setAttachment(null);
		} else {
			session = new DuelSession(Arrays.asList(player, requested), MultiplayerSessionType.DUEL);
			if (Server.getMultiplayerSessionListener().appendable(session)) {
				player.sendMessage("Sending duel request...");
				requested.sendMessage(player.getDisplayName() + ":duelreq:");
				session.getStage().setAttachment(player);
				Server.getMultiplayerSessionListener().add(session);
			}
		}
	}

}
