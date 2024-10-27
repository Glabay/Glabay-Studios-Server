package io.xeros.model.entity.player.mode.group;

import io.xeros.content.dialogue.*;
import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.model.Items;
import io.xeros.model.SlottedItem;
import io.xeros.model.entity.player.*;
import io.xeros.model.entity.player.mode.ModeRevertType;
import io.xeros.model.items.ContainerUpdate;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemInterface;
import io.xeros.model.items.inventory.Inventory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GroupIronmanDialogue extends DialogueBuilder {

    private static final int NPC_ID = GroupIronman.GROUP_FORM_NPC;

    public GroupIronmanDialogue(Player player) {
        super(player);
        if (!player.getMode().isGroupIronman()) {
            npc(NPC_ID, "Pfft.. I only speak to Group Ironman members..");
            return;
        }

        setNpcId(NPC_ID);
        GroupIronmanGroup group = currentGroup();

        if (group == null) {
            if (player.isJoinedIronmanGroup()) {
                npc("You've already joined and left a formed group,", "you can't join or create any more groups.", "If you want to convert to standard mode talk to Adam.");
                return;
            }
            if (isTutIsland()) {
                npc("Greetings, " + player.getDisplayName() + ".", "Would you like to form an Ironman Group?");
            } else {
                npc("Greetings, " + player.getDisplayName() + ".", "Would you like to form an Ironman Group?", "@red@You can only join/form one group per account!");
            }
        }

        DialogueOption createGroup = new DialogueOption("Create Group", this::createIronmanGroup);
        DialogueOption formGroup = new DialogueOption("Start Your Adventure", this::formGroupDialogue);
        DialogueOption disbandGroup = new DialogueOption("Disband Group", this::disbandGroup);
        DialogueOption leaveGroup = new DialogueOption("Leave Group", this::leaveGroup);
        DialogueOption invitePlayer = new DialogueOption("Invite Player", this::invitePlayerToGroup);
        DialogueOption kickPlayer = new DialogueOption("Kick Player", this::kickPlayerFromGroup);
        DialogueOption neverMind = new DialogueOption("Never mind", plr -> plr.getPA().closeAllWindows());
        DialogueOption leaveWithoutForming = new DialogueOption("Leave now, form/join group later", plr -> GroupIronman.moveAfterJoin(plr, null));
        DialogueOption viewLogs = new DialogueOption("View logs", GroupIronmanLogsInterface::open);

        DialogueOption convertToStandard = new DialogueOption("Convert to Standard (non-ironman)", plr -> {
            if (plr.getModeRevertType() == ModeRevertType.PERMANENT) {
                npcn("Your mode is set to permanent, you can't change it.");
                return;
            }

            npcn("To convert to standard you must leave your group.", "Afterwards talk to Adam to convert to standard mode.");
        });

        if (group == null) {
            if (isTutIsland()) {
                option(createGroup, leaveWithoutForming, neverMind);
            } else {
                option(createGroup, convertToStandard, neverMind);
            }
            return;
        }

        if (group.isLeader(player)) {
            if (isFormed()) {
                // Use to have kick but we removed it to prevent griefing
                option(invitePlayer, viewLogs, convertToStandard, leaveGroup);
            } else {
                option(formGroup, invitePlayer, kickPlayer, leaveGroup, disbandGroup);
            }
            return;
        }

        option(leaveGroup, viewLogs, convertToStandard, neverMind);
    }

    private boolean isTutIsland() {
        return isTutIsland(getPlayer());
    }

    private boolean isTutIsland(Player player) {
        return player.getController().getKey().equals(new GroupIronmanFormingController().getKey());
    }

    private boolean isFormed() {
        GroupIronmanGroup group = currentGroup();
        return group != null && group.isFinalized();
    }

    private GroupIronmanGroup currentGroup() {
        return GroupIronmanRepository.getGroupForOnline(getPlayer()).orElse(null);
    }

    private DialogueBuilder npcn(String...message) {
        reset().npc(message).send();
        return this;
    }

    private boolean isGroupLeader() {
        GroupIronmanGroup group = currentGroup();
        if (group == null) {
            npcn("You're not in a group.");
            return false;
        }

        if (!group.isLeader(getPlayer())) {
            npcn("Only the leader can do that!");
            return false;
        }

        return true;
    }

    /**
     * Kicking is only supported for groups that are in the forming phase (tutorial island).
     * You can't enable this for formed groups without changes to the mechanics.
     */
    private void kickPlayerFromGroup(Player player) {
        Optional<GroupIronmanGroup> gr = GroupIronmanRepository.getGroupForOnline(player);

        if (gr.isEmpty()) {
            npcn("You need to be in a Group Ironman team", "and be the leader in order to kick", "someone from your group");
            return;
        }

        GroupIronmanGroup group = gr.get();

        if (group.isFinalized()) // Disabled kick for formed groups
            return;

        if (!group.isLeader(player)) {
            npcn("Only your group leader can kick players", "from your group ironman team.");
            return;
        }

        if (isTutIsland()) {
            npcn("Enter the player's username and they", "will be kicked from the group.");
        } else {
            npcn("@red@Once you kick a player they can't join another group!", "Be certain this is what you want to do.");
        }

        exit(plr -> player.getPA().sendEnterString("Enter the players username you wish to kick from your team.", (p, otherName) -> {
            if (otherName == null) {
                npcn("An error occured, try again.");
                return;
            }

            Player other = PlayerHandler.getPlayerByDisplayName(otherName);

            if (other == null) {
                /*
                  doesn't exist.
                 */
                npcn("Ehhh.. I couldn't find that person in your group..", "try again");
                return;
            }

            if (other.equals(player)) {
                npcn("You cannot kick yourself from your group.");
                return;
            }

            if (!group.isLeader(player)) {
                return;
            }

            GroupIronmanRepository.removeFromGroup(other, group);
            other.sendStatement("You were kicked from the '" + group.getName() + "' group.");
            npcn("Perfect, I have kicked '"+otherName+"'", "from your group ironman team.");
        }));
    }

    public void invitePlayerToGroup(Player player) {
        String header = "Enter username";
        GroupIronmanGroup group1 = currentGroup();
        if (group1.isFinalized())
            header += ", you have " + (5 - group1.getJoined()) + " joins left.";
        player.getPA().sendEnterString(header, (p, otherName) -> {
            if (otherName == null) {
                npcn("An error occured, try again.");
                return;
            }

            GroupIronmanGroup group = currentGroup();

            if (group == null) {
                npcn("You don't have a group.");
                return;
            }

            if (!isGroupLeader()) {
                end();
                npcn("You're not the leader of your group!");
                return;
            }
            final Player other = PlayerHandler.getPlayerByDisplayName(otherName);
            if (other == null) {
                /*
                  Is not online or doesn't exist.
                 */
                npcn("Ehhh.. I couldn't find that person..", "try again or invite another person");
                return;
            }

            if (other.equals(player)) {
                npcn("Hhhh..", "you cannot invite yourself to your own group.");
                return;
            }

            if (other.isBusy()) {
                npcn("That player is busy at the moment.");
                return;
            }

            if (!isTutIsland() && isTutIsland(other)) {
                npcn("That player is on tutorial island, they must leave", "before inviting them.");
                return;
            }

            if (isTutIsland() && !isTutIsland(other)) {
                npcn("That player isn't on tutorial island, you must leave", "before inviting them.");
                return;
            }

            if (!group.canJoin(player, other)) {
                return;
            }

            player.getPA().closeAllWindows();
            npcn("Perfect, I have invited '"+other.getDisplayName()+"'", "To your team, they need to accept/decline the request");
            DialogueBuilder db = new DialogueBuilder(other);

            db.setNpcId(NPC_ID);

            if (group.isFinalized()) {
                db.npc("You've been invited to join the group '" + group.getName() + "'.",
                        "Members: " + group.getOnline().stream().map(Player::getLoginName).collect(Collectors.joining(", ")),
                        "@red@This group is formed already, when you accept you", "@red@won't be able to join any new groups!");
            } else {
                db.npc("You've been invited to join the group '" + group.getName() + "'.",
                        "Members: " + group.getOnline().stream().map(Player::getLoginName).collect(Collectors.joining(", ")),
                        "Would you like to join the group?");
            }

            db.option(new DialogueOption("Join '" + group.getName() + "'", plr -> join(plr, group)),
                    new DialogueOption("No thanks.", plr -> {
                        plr.getPA().closeAllWindows();
                        player.sendMessage("@red@" + plr.getDisplayNameFormatted() + " declined.");
                    })
            );


            other.start(db);
        });
    }

    private static void join(Player player, GroupIronmanGroup group) {
        if (!group.canJoin(player, player)) {
            player.getPA().closeAllWindows();
            return;
        }

        player.getPA().closeAllWindows();
        GroupIronmanRepository.addToGroup(player, group);
        if (group.isFinalized() && Boundary.isIn(player, Boundary.GROUP_IRONMAN_FORMING)) {
            GroupIronman.moveAfterJoin(player, group);
        }
        if (!group.isFinalized()) {
            new DialogueBuilder(player).setNpcId(NPC_ID)
                    .npc("You've joined the group '" + group.getName() + "'.",
                            "You can speak to me if you wish to leave the group.").send();
        }
    }

    private void createIronmanGroup(Player player) {
        Consumer<Player> createGroup = plr -> {
            player.getPA().closeAllWindows();
            Optional<GroupIronmanGroup> gr = GroupIronmanRepository.getFromGroupList(player);

            if (gr.isPresent()) {
                npcn("You are already apart of a group.", "You need to leave your current group", "if you wish to make a new one.");
                return;
            }

            player.getPA().sendEnterString("Enter a name you wish to register as your group..", (p, groupName) -> {
                groupName = groupName.trim();
                if (GroupIronmanRepository.groupExistsWithName(groupName)) {
                    npcn("A group already exists with that name.", "Try again.");
                    return;
                }
                int nameSize = groupName.length();
                boolean under = nameSize == 0;
                if (under || nameSize > 12) {
                    npcn((under ? "You cannot have a group name under 2 characters" : "You cannot have a group name over 12 characters"));
                    return;
                }

                if (!StringUtils.isAlphanumericSpace(groupName)) {
                    npcn("Your group name contains illegal characters!", "Use alpha numeric characters - letters, numbers and spaces!");
                    return;
                }

                npcn("Perfect, I have created your ironman group '"+groupName+"'", "To manage your team, speak to me again.");
                GroupIronmanGroup group = GroupIronmanRepository.addFormingGroup(player, groupName);
                if (group != null && !isTutIsland()) {
                    GroupIronmanRepository.finalize(group);
                }
            });
        };

        if (isTutIsland()) {
            createGroup.accept(player);
        } else {
            new DialogueBuilder(player).setNpcId(NPC_ID)
                    .npc("Since you've left tutorial island this will be final.",
                            "@red@Once you start a group you can't leave or join another.",
                            "Make sure you coordinate with your group!")
                    .option(new DialogueOption("Yes, I know I can't go back.", createGroup), DialogueOption.nevermind())
                    .send();
        }
    }

    /**
     * Disbanding is only supported for groups that are in the forming phase (tutorial island).
     * You can't enable this for formed groups without changes to the mechanics.
     */
    private void disbandGroup(Player player) {
        if (!isGroupLeader())
            return;

        GroupIronmanGroup group = currentGroup();
        if (group == null || group.isFinalized())
            return;
        group.getOnline().forEach(it -> {
            it.sendMessage("Your Ironman Group was disbanded by the leader.");
            GroupIronmanRepository.removeFromGroup(it, group);
        });
        npcn("You're group has been disbanded.");
    }

    private void leaveGroup(Player player) {
        GroupIronmanGroup group = currentGroup();
        if (group == null) {
            npcn("You're not in a group.");
            return;
        }

        if (isFormed()) {
            npcn("@red@Once you leave you won't be able to join another group.",
                    "@red@All items you have that have FOE value or coins over 100k", "@red@will be deposited into the GIM bank.",
                    "Make sure this is what you want!");
            npc("@red@Attempting to go around the item forfeit may", "@red@result in action against your account.");

            option(new DialogueOption("Leave group (can never join another group, forfeit items)", plr -> leaveFormedGroup(plr, group)),
                    new DialogueOption("Never mind.", plr -> plr.getPA().closeAllWindows())
            );
        } else {
            GroupIronmanRepository.removeFromGroup(player, group);
            npcn("You've been removed from the group.");
        }
    }

    private void leaveFormedGroup(Player player, GroupIronmanGroup group) {
        // Attempt to forfeit items
        Inventory inventory = group.getBank().getInventory();
        Optional<GameItem> gameItem = forfeitItemsIntoInventory(player, inventory, true);
        if (gameItem.isPresent()) {
            npcn("You weren't able to leave the group because there", "wasn't enough space in the GIM bank to forfeit", "the following item: " + gameItem.get().getFormattedString());
            return;
        }

        // Then forfeit items for reals
        gameItem = forfeitItemsIntoInventory(player, inventory, false);
        gameItem.ifPresent(it -> player.sendMessage("There was an error forfeiting items but you were still removed from the group."));

        GroupIronmanRepository.removeFromGroup(player, group);
        npcn("You've been removed from the group.", "You can't join any more groups.");
    }

    private Optional<GameItem> forfeitItemsIntoInventory(Player player, Inventory into, boolean dryRun) {
        if (dryRun)
            into = into.copy();

        Predicate<ItemInterface> forfeit = it -> it.getDef().isTradable() && (
                FireOfExchangeBurnPrice.getBurnPrice(null, it.id(), false) != -1
                || it.id() == Items.COINS && it.amount() > 100_000
        );

        List<SlottedItem> inventory = player.getItems().getInventoryItems().stream().filter(forfeit).toList();
        List<SlottedItem> equipment = player.getItems().getEquipmentItems().stream().filter(forfeit).toList();
        List<GameItem> bank = player.getItems().getBankItems().stream().filter(forfeit).toList();

        for (SlottedItem gameItem : inventory) {
            Optional<GameItem> remaining = into.add(gameItem.toGameItem());
            if (remaining.isPresent() && dryRun)
                return remaining;
            if (!dryRun)
                player.getItems().setInventoryItemSlot(gameItem.slot(), null);
        }

        for (SlottedItem gameItem : equipment) {
            Optional<GameItem> remaining = into.add(gameItem.toGameItem());
            if (remaining.isPresent() && dryRun)
                return remaining;
            if (!dryRun)
                player.getItems().setEquipmentSlot(gameItem.slot(), null);
        }

        for (GameItem gameItem : bank) {
            Optional<GameItem> remaining = into.add(gameItem);
            if (remaining.isPresent() && dryRun)
                return remaining;
            if (!dryRun)
                player.getItems().removeFromAnyTabWithoutAdding(gameItem.id(), gameItem.amount(), false);
        }

        if (!dryRun) {
            player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
            player.getItems().addContainerUpdate(ContainerUpdate.EQUIPMENT);
            player.getItems().calculateBonuses();
            player.resetOnDeath();
        }

        return Optional.empty();
    }

    private void formGroupDialogue(Player player) {
        DialogueBuilder db = new DialogueBuilder(player).setNpcId(NPC_ID);
        db.npc("This will finalize your group and move everyone home.", "@red@All players inside this group will not", "@red@be able to join another group after this.", "Are you sure?");
        db.option(new DialogueOption("Yes, form my group and leave", plr -> formGroup(player)),
                new DialogueOption("No, I'm not finished yet.", plr -> plr.getPA().closeAllWindows()));
        db.send();
    }

    private void formGroup(Player player) {
        if (!isGroupLeader())
            return;
        GroupIronman.formGroup(player);
    }

    private boolean forfeitOnLeave(int itemId, int amount) {
        return FireOfExchangeBurnPrice.getBurnPrice(null, itemId, false) != 0 || itemId == Items.COINS && amount > 100_000;
    }
}
