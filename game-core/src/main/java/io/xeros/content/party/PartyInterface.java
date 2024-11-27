package io.xeros.content.party;

import io.xeros.model.entity.player.Player;

import java.util.List;
import java.util.Optional;

public abstract class PartyInterface {

    private static final int INTERFACE_ID = 30_370;
    private static final int PARTY_TYPE_STRING = 30_372;
    private static final int LEAVE_OR_CREATE_BUTTON = 30_374;
    private static final int LEAVE_OR_CREATE_BUTTON_STRING = 30_375;
    private static final int LEADER_STRING = 30_373;
    private static final int PLAYER_NAME_CONTAINER = 30_377;
    private static final int PLAYER_NAME_CONTAINER_DEFAULT_HEIGHT = 159;

    public static int PLAYER_LIST_SIZE = 100;

    private static final int[] BUTTONS = new int[PLAYER_LIST_SIZE];
    private static final int BUTTON_START_ID = 30_378;
    private static final int BUTTON_STEP = 1;

    static {
        for (int i = 0; i < BUTTONS.length; i++)
            BUTTONS[i] = BUTTON_START_ID + (i * BUTTON_STEP);
    }

    private static boolean inPartyThatMatchesArea(Player player) {
        PartyFormAreaController controller = PartyFormAreaController.get(player);
        if (controller == null)
            return false;
        PlayerParty party = player.getParty();
        return party != null && party.getPartyType().equals(controller.getKey());
    }

    public static void open(Player player) {
        PartyFormAreaController controller = PartyFormAreaController.get(player);
        if (controller == null)
            return;
        player.setSidebarInterface(13, INTERFACE_ID);
        updateInterface(player);
    }

    public static void close(Player player) {
        player.setSidebarInterface(13, 0);
    }

    public static void refreshOnJoinOrLeave(Player joinedOrLeft, PlayerParty party) {
        updateInterface(joinedOrLeft);
        party.getEntityList().forEach(PartyInterface::sendMembers);
    }

    public static void updateInterface(Player player) {
        PartyFormAreaController controller = PartyFormAreaController.get(player);
        if (controller == null)
            return;

        if (!inPartyThatMatchesArea(player)) {
            player.getPA().sendString(PARTY_TYPE_STRING, controller.getKey());
            player.getPA().sendString(LEADER_STRING, "-");
            player.getPA().sendString(LEAVE_OR_CREATE_BUTTON_STRING, "Create");
            player.getPA().runClientScript(4, BUTTON_START_ID, PLAYER_LIST_SIZE);
            return;
        }

        player.getPA().sendString(LEAVE_OR_CREATE_BUTTON_STRING, "Leave");
        sendMembers(player);
    }

    private static void sendMembers(Player player) {
        int index = 0;
        List<Player> players = player.getParty().getEntityList();
        for (; index < players.size(); index++) {
            player.getPA().sendString(BUTTONS[index], players.get(index).getDisplayNameFormatted());
        }

        player.getPA().runClientScript(4, BUTTONS[index], BUTTONS.length - index);

        PlayerParty party = player.getParty();
        Optional<Player> owner = party.getOwner();
        owner.ifPresent(value -> player.getPA().sendString(LEADER_STRING, "Leader: " + value.getDisplayNameFormatted()));
        player.getPA().sendString(PARTY_TYPE_STRING, party.getPartyType() + " (" + party.getEntityList().size() + "/" + party.getMaxPlayers() + ")");
        player.getPA().setScrollableMaxHeight(PLAYER_NAME_CONTAINER, Math.max(index * 13, PLAYER_NAME_CONTAINER_DEFAULT_HEIGHT));
    }

    public static boolean handleButton(Player player, int buttonId) {
        if (buttonId != LEAVE_OR_CREATE_BUTTON)
            return false;

        PartyFormAreaController controller = PartyFormAreaController.get(player);
        if (controller == null)
            return false;

        if (inPartyThatMatchesArea(player)) {
            player.getParty().remove(player);
            updateInterface(player);
            return true;
        }

        if (player.getParty() != null)
            player.getParty().remove(player);
        PlayerParty newParty = controller.createParty();
        newParty.add(player);
        updateInterface(player);
        return true;
    }

    public static boolean handleInterfaceAction(Player player, int interfaceId, int action) {
        int index = interfaceId - BUTTON_START_ID;
        if (index < 0 || index > PLAYER_LIST_SIZE)
            return false;
        if (player.getParty() == null)
            return true;
        if (player.getParty().isNotOwner(player))
            return true;
        if (player.getParty().getEntityList().size() < index)
            return true;
        Player kick = player.getParty().getEntityList().get(index);
        if (kick == player)
            return true;

        player.getParty().remove(kick);
        player.sendMessage("Kicked " + player.getDisplayNameFormatted() + " from the party.");
        return true;
    }
}
