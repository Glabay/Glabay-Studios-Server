package io.xeros.content.party;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.EntityList;
import io.xeros.model.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a party of players that can be joined by other players.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public abstract class PlayerParty extends EntityList<Player> {

    private static final Logger logger = LoggerFactory.getLogger(PlayerParty.class);

    private final String partyType;
    private boolean disbanded;
    private int maxPlayers;

    public abstract boolean canJoin(Player invitedBy, Player invited);
    public abstract void onJoin(Player player);
    public abstract void onLeave(Player player);

    public PlayerParty(String partyType) {
        this(partyType, 100);
    }

    public PlayerParty(String partyType, int maxPlayers) {
        this.partyType = partyType;
        this.maxPlayers = maxPlayers;
    }

    public Map<Boolean, List<Player>> getReadyPlayersPartition(Predicate<Player> extraReadyConditions) {
        Predicate<Player> ready = it -> !it.isBusy() && it.getInstance() == null && !it.teleporting && it.teleTimer == 0
                && (extraReadyConditions == null || extraReadyConditions.test(it));
        return getEntityList().stream().collect(Collectors.partitioningBy(ready));
    }

    public void openStartActivityDialogue(Player player, String name, Predicate<Player> readyPlayerPredicate,
                                          Consumer<List<Player>> startActivity) {
        if (isNotOwner(player)) {
            player.sendStatement("Only the party leader can start the activity.");
            return;
        }

        List<Player> readyPlayers = getReadyPlayersPartition(readyPlayerPredicate).get(true);

        new DialogueBuilder(player).statement("Do you wish to start " + name + "?",
                readyPlayers.size() + "/" + getEntityList().size() + " players are ready.",
                "Any non-ready players will be left behind.")
                .option(
                        new DialogueOption("Yes, start " + name + ".", plr -> {
                            Map<Boolean, List<Player>> readyPlayersPartition = getReadyPlayersPartition(readyPlayerPredicate);
                            List<Player> ready = readyPlayersPartition.get(true);
                            List<Player> notReady = readyPlayersPartition.get(false);

                            ready.forEach(it -> it.getPA().closeAllWindows());
                            startActivity.accept(ready);
                            notReady.forEach(notReadyPlr -> notReadyPlr.sendMessage("The party started without you because you weren't ready."));
                        }),
                        new DialogueOption("Never mind, we aren't ready.", plr -> plr.getPA().closeAllWindows())
                ).send();
    }

    @Override
    public void add(Player player) {
        if (player.getParty() != null) {
            player.getParty().remove(player);
        }

        if (!disbanded && !getEntityList().contains(player)) {
            super.add(player);
            player.setParty(this);
            getEntityList().forEach(plr -> plr.sendMessage(player.getDisplayNameFormatted() + " has joined the party."));
            onJoin(player);
            logger.debug("{} joined party.", player);
        }
    }

    @Override
    public void remove(Player player) {
        if (getEntityList().contains(player)) {
            Optional<Player> owner = getOwner();
            if (owner.isPresent() && owner.get().equals(player) && getEntityList().size() > 1) {
                Player newOwner = getEntityList().get(1);
                getEntityList().forEach(it -> player.sendMessage(newOwner.getDisplayNameFormatted() + " is now the group leader."));
            }

            boolean switchLeader = getEntityList().get(0) == player;
            super.remove(player);
            player.setParty(null);
            onLeave(player);

            if (!disbanded) {
                getEntityList().forEach(plr -> plr.sendMessage(player.getDisplayNameFormatted() + " has left the party."));
                player.sendMessage("You left the party.");

                if (switchLeader && !getEntityList().isEmpty())
                    getEntityList().get(0).sendMessage("<img=2>You are now the party leader.");
            }
        }
    }

    public void invite(Player invitedBy, Player invited) {
        if (invitedBy.equals(invited)) {
            invitedBy.sendMessage("Fuck off.");
            return;
        }

        if (isNotOwner(invitedBy)) {
            invitedBy.sendMessage("Only the party leader can invite players.");
            return;
        }

        if (getEntityList().contains(invited)) {
            invitedBy.sendMessage("That player is already in your party!");
            return;
        }

        if (invited.getParty() != null && invited.inParty(partyType)) {
            invitedBy.sendMessage("This player is already in a party.");
            return;
        }

        if (getEntityList().size() >= maxPlayers) {
            invitedBy.sendMessage("The party is full.");
            return;
        }

        if (canJoin(invitedBy, invited)) {
            if (!invited.isBusy()) {
                invited.start(new DialogueBuilder(invited).option("Accept " + invitedBy.getDisplayNameFormatted() + "'s " + partyType + " invite?",
                        new DialogueOption("Accept", invited2 -> acceptInvitation(invitedBy, invited2)),
                        new DialogueOption("Decline", invited2 -> declineInvitation(invitedBy, invited2))));
            } else {
                invitedBy.sendMessage("That player is busy at the moment.");
            }
        }
    }

    private void acceptInvitation(Player invitedBy, Player invited) {
        invited.getPA().closeAllWindows();

        if (!getEntityList().contains(invited) && canJoin(invitedBy, invited)) {
            if (getEntityList().size() >= maxPlayers) {
                invitedBy.sendMessage("The party is full.");
                invited.sendMessage("The party is full.");
                return;
            }

            add(invited);
        }
    }

    private void declineInvitation(Player invitedBy, Player invited) {
        invitedBy.sendMessage(invited.getDisplayNameFormatted() + " declined to join the " + partyType + " party.");
        invited.sendMessage("Declined invite.");
    }

    private void disband() {
        disbanded = true;
        List<Player> all = Lists.newArrayList(getEntityList());
        all.forEach(plr -> {
            plr.sendMessage("The party has been disbanded.");
            remove(plr);
        });
        clear();
    }

    public boolean isNotOwner(Player player) {
         return getOwner().isEmpty() || !getOwner().get().equals(player);
    }

    public Optional<Player> getOwner() {
        if (getEntityList().size() > 0) {
            return Optional.of(getEntityList().get(0));
        } else {
            return Optional.empty();
        }
    }

    public boolean isType(String type) {
        return partyType.equals(type);
    }

    public String getPartyType() {
        return partyType;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
