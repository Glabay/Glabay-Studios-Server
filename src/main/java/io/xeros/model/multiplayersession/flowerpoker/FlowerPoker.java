package io.xeros.model.multiplayersession.flowerpoker;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.multiplayersession.Multiplayer;
import io.xeros.model.multiplayersession.MultiplayerSession;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.trade.Trade;

import java.util.Arrays;
import java.util.Objects;

public class FlowerPoker extends Multiplayer {

    public static final Boundary BOUNDARIES = Boundary.FLOWER_POKER_AREA;

    public FlowerPoker(Player player) {
        super(player);
    }

    @Override
    public boolean requestable(Player requested) {
        if (Server.getMultiplayerSessionListener().requestAvailable(requested, player, MultiplayerSessionType.FLOWER_POKER) != null) {
            player.sendMessage("You have already sent a request to this player.");
            return false;
        }
        if (Configuration.DISABLE_FLOWER_POKER) {
            player.sendMessage("Flower poker is currently disabled.");
            return false;
        }
        return Trade.requestable(player, requested);
    }

    @Override
    public void request(Player requested) {
        if (Configuration.DISABLE_FLOWER_POKER) {
            player.sendMessage("Flower poker is currently disabled.");
            return;
        }
        if (Objects.isNull(requested)) {
            player.sendMessage("The player cannot be found, try again shortly.");
            return;
        }
        if (Objects.equals(player, requested)) {
            player.sendMessage("You cannot gamble yourself.");
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
        if (requested.isFping()) {
            player.sendMessage("Other player is busy");
            return;
        }
        if (player.isFping()) {
            return;
        }
        if (!Boundary.isIn(player, Boundary.FLOWER_POKER_AREA)) {
            player.sendMessage("You can only gamble north-east of home");
            return;
        }
        if (requested.underAttackByPlayer > 0 || requested.underAttackByNpc > 0 && requested.underAttackByNpc != requested.lastNpcAttacked && !requested.getPosition().inMulti()) {
            player.sendMessage("You cannot gamble this person whilst he has been recently in combat or in multi.");
            return;
        }
        if (player.getItems().getItemAmount(299) < 5) {
            player.sendMessage("<col=ff0000>You need 5 mithril seeds in order to gamble someone.");
            return;
        }
        if (requested.getItems().getItemAmount(299) < 5) {
            player.sendMessage("<col=ff0000>"+requested.getDisplayName()+" needs 5 mithril seeds in order to accept requests.");
            return;
        }
        if (requested.isBusy()) {
            player.sendMessage("That player is busy at the moment.");
            return;
        }
        player.faceUpdate(requested.getIndex());
        MultiplayerSession session = Server.getMultiplayerSessionListener().requestAvailable(player, requested, MultiplayerSessionType.FLOWER_POKER);
        if (session != null) {
            session.getStage().setStage(MultiplayerSessionStage.OFFER_ITEMS);
            session.populatePresetItems();
            session.updateMainComponent();
            Server.getMultiplayerSessionListener().removeOldRequests(player);
            Server.getMultiplayerSessionListener().removeOldRequests(requested);
            session.getStage().setAttachment(null);
        } else {

            session = new FlowerPokerSession(Arrays.asList(player, requested), MultiplayerSessionType.FLOWER_POKER);
            if (Server.getMultiplayerSessionListener().appendable(session)) {
                player.sendMessage("<img=29><col=1950ce>Sending Gambling request to "+requested.getDisplayName()+"...");
                requested.sendMessage(player.getDisplayName() + ":gamblereq:");
                session.getStage().setAttachment(player);
                Server.getMultiplayerSessionListener().add(session);
            }
        }
    }

}
