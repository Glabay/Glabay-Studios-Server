package io.xeros.net.packets;

import java.util.Objects;

import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

import static io.xeros.model.Items.ROTTEN_POTATO;
import static io.xeros.model.Items.WRENCH;

public class ItemOnPlayer implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		if (c.getMovementState().locked() || c.getLock().cannotInteract(c))
			return;
		if (c.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		c.interruptActions();
		int interfaceId = c.getInStream().readUnsignedWordBigEndianA();
		int playerIndex = c.getInStream().readUnsignedWord();
		int itemId = c.getInStream().readUnsignedWord();
		int slotId = c.getInStream().readUnsignedWordBigEndian();
		c.setItemOnPlayer(null);
		if (c.teleTimer > 0)
			return;
		if (playerIndex > PlayerHandler.players.length) {
			return;
		}
		if (slotId > c.playerItems.length) {
			return;
		}
		if (PlayerHandler.players[playerIndex] == null) {
			return;
		}
		if (!c.getItems().playerHasItem(itemId, 1, slotId)) {
			return;
		}
		Player other = PlayerHandler.players[playerIndex];
		if (other == null) {
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if (other.getBankPin().requiresUnlock()) {
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (other.getInterfaceEvent().isActive()) {
			c.sendMessage("This player is busy.");
			return;
		}
		if (Misc.distanceBetween(c, other) > 15) {
			c.sendMessage("You need to move closer to do this.");
			return;
		}
		if (Misc.isInDuelSession(c)) return;
		c.setItemOnPlayer(other);

		if (c.getRights().isOrInherits(Right.OWNER) && itemId != ROTTEN_POTATO && itemId != WRENCH) {
			c.sendMessage("You gave " + other.getDisplayName() + " some " + ItemAssistant.getItemName(itemId) + ".");
			other.sendMessage("You were given some " + ItemAssistant.getItemName(itemId) + " from " + c.getDisplayName() + ".");
			other.getItems().addItem(itemId, c.getItems().isStackable(itemId) ? c.getItems().getItemAmount(itemId) : 1);
			c.getItems().deleteItem(itemId, c.getItems().isStackable(itemId) ? c.getItems().getItemAmount(itemId) : 1);
		}

		var itemActionManager = Server.getItemOptionActionManager();
		if (itemActionManager.findHandlerById(itemId).isPresent()) {
			var itemAction = itemActionManager.findHandlerById(itemId).get();
			if (itemAction.performActionOnEntity(c, other, itemId, slotId))
				return;
		}

		switch (itemId) {
			case 6769://5 scroll
				if (c.getPosition().inWild() || c.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
					return;
				}
				if (other.getPosition().inWild() || other.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(other)) {
					return;
				}
				if (c.getItems().playerHasItem(6769, 1)) {
					c.getDH().sendDialogues(4005, -1);
				}
				break;
		case 2403://10 scroll
			if (c.getPosition().inWild() || c.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (other.getPosition().inWild() || other.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(other)) {
				return;
			}
			if (c.getItems().playerHasItem(2403, 1)) {
				c.getDH().sendDialogues(4006, -1);
			}
			break;
		case 2396://25 scroll
			if (c.getPosition().inWild() || c.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (other.getPosition().inWild() || other.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(other)) {
				return;
			}
			if (c.getItems().playerHasItem(2396, 1)) {
				c.getDH().sendDialogues(4007, -1);
			}
			break;
		case 786://50 scroll
			if (c.getPosition().inWild() || c.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (other.getPosition().inWild() || other.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(other)) {
				return;
			}
			if (c.getItems().playerHasItem(786, 1)) {
				c.getDH().sendDialogues(4008, -1);
			}
			break;
		case 761://100 scroll
			if (c.getPosition().inWild() || c.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (other.getPosition().inWild() || other.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(other)) {
				return;
			}
			if (c.getItems().playerHasItem(761, 1)) {
				c.getDH().sendDialogues(4009, -1);
			}
			break;
		case 607://250 scroll
			if (c.getPosition().inWild() || c.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (other.getPosition().inWild() || other.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(other)) {
				return;
			}
			if (c.getItems().playerHasItem(607, 1)) {
				c.getDH().sendDialogues(4010, -1);
			}
			break;
		case 608://500 scroll
			if (c.getPosition().inWild() || c.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(c)) {
				return;
			}
			if (other.getPosition().inWild() || other.getPosition().inDuelArena() || Server.getMultiplayerSessionListener().inAnySession(other)) {
				return;
			}
			if (c.getItems().playerHasItem(608, 1)) {
				c.getDH().sendDialogues(4011, -1);
			}
			break;
		case 962:
			if (other.connectedFrom.equalsIgnoreCase(c.connectedFrom)) {
				c.sendMessage("You cannot use this on another player that is on the same host as you.");
				return;
			}
			if (c.getItemOnPlayer() == null) {
				return;
			}
			if (!c.getItems().playerHasItem(962)) {
				return;
			}
			if (c.getItemOnPlayer().getItems().freeSlots() < 1) {
				c.sendMessage("The other player must have at least 1 free slot.");
				return;
			}
			int[] partyHats = { 1038, 1040, 1042, 1044, 1046, 1048 };
			int hat = partyHats[Misc.random(partyHats.length - 1)];
			Player winner = Misc.random(1) == 0 ? c : c.getItemOnPlayer();
			Player loser = winner == c ? c.getItemOnPlayer() : c;
			if (Objects.equals(winner, loser)) {
				return;
			}
			c.getPA().closeAllWindows();
			loser.facePosition(winner.getX(), winner.getY());
			winner.facePosition(loser.getX(), loser.getY());
			winner.startAnimation(881);
			loser.startAnimation(881);
			c.getItems().deleteItem(962, 1);
			winner.getItems().addItem(hat, 1);
			winner.sendMessage(
					"You have received a " + ItemAssistant.getItemName(hat) + " from the christmas cracker.");
			loser.sendMessage("Awee you didn't get the partyhat.");
			break;
			
		case 13345:
			if (other.connectedFrom.equalsIgnoreCase(c.connectedFrom)) {
				c.sendMessage("You cannot use this on another player that is on the same host as you.");
				return;
			}
			if (!c.getItems().playerHasItem(13345)) {
				return;
			}
			if (other.getItems().freeSlots() == 0) {
				c.sendMessage("This player does not have any space.");
				return;
			}
			c.facePosition(other.getX(), other.getY());
			c.getItems().deleteItem(13345, 1);
			int random = Misc.random(4);
			switch (random) {
			case 0:
			case 1:
			case 2:
				c.sendMessage("How unlucky, this present was empty.");
				other.sendMessage(""+ c.getDisplayName() +" used a present on you and it was empty!");
				break;
				
			case 3:
				other.getItems().addItem(13343, 1);
				c.sendMessage("How unlucky, "+ other.getDisplayName() +" got the Black Santa Hat!");
				other.sendMessage(""+ c.getDisplayName() +" used a present on you and you got a Black Santa Hat!");
				break;
				
			case 4:
				c.getItems().addItem(13343, 1);
				c.sendMessage("You were lucky enough to find a Black Santa Hat in the present!");
				other.sendMessage(""+ c.getDisplayName() +" used a present on you and got a Black Santa Hat!");
				break;
		}
			break;
			
		}
	}
}