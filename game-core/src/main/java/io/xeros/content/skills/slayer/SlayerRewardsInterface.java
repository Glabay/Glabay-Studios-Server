package io.xeros.content.skills.slayer;

import java.util.Arrays;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.util.Misc;


public class SlayerRewardsInterface {

    public static void open(Player player, SlayerRewardsInterfaceData.Tab tab) {
        switch (tab) {
            case UNLOCK:
                Arrays.stream(SlayerRewardsInterfaceData.Unlock.values()).forEach(unlock ->
                        player.getPA().sendConfig(unlock.getConfig(), player.getSlayer().getUnlocks().contains(unlock.getUnlock()) ? 1 : 0));
                break;
            case EXTEND:
                Arrays.stream(SlayerRewardsInterfaceData.Extend.values()).forEach(extend ->
                        player.getPA().sendConfig(extend.getConfig(), player.getSlayer().getExtensions().contains(extend.getUnlock()) ? 1 : 0));
                break;
            case BUY:
                player.getShops().openShop(10);
                player.sendMessage("You have <col=a30027>" + Misc.insertCommas(player.getSlayer().getPoints()) + " </col>slayer points.");
                return;
            case TASK:
                // Blocked tasks
                for (int index = 0; index < SlayerRewardsInterfaceData.BLOCKED_TASK_STRINGS.length; index++) {
                    String blocked = player.getSlayer().getRemoved()[index];
                    if (blocked == null || blocked.length() == 0) {
                        if (index > 2 && !player.getRights().isOrInherits(Right.REGULAR_DONATOR)) {
                            blocked = "@red@Donator rank required for this slot.";
                        } else {
                            blocked = "Empty";
                        }
                    } else {
                        blocked = Misc.formatPlayerName(blocked);
                    }
                    player.getPA().sendString(blocked, SlayerRewardsInterfaceData.BLOCKED_TASK_STRINGS[index]);
                }

                // Current task
                String task = "None";
                if (player.getSlayer().getTask().isPresent()) {
                    Task current = player.getSlayer().getTask().get();
                    task = Misc.formatPlayerName(current.getPrimaryName()) + " x" + player.getSlayer().getTaskAmount();
                }
                player.getPA().sendString(task, SlayerRewardsInterfaceData.CURRENT_ASSIGNMENT_STRING);
                break;
        }

        player.getPA().sendString(Misc.insertCommas(String.valueOf(player.getSlayer().getPoints())), SlayerRewardsInterfaceData.CURRENT_SLAYER_POINTS_STRING);
        player.getPA().showInterface(tab.getInterfaceId());
    }

    public static boolean clickButton(Player player, int buttonId) {
        // Open tab
        for (SlayerRewardsInterfaceData.Tab tab : SlayerRewardsInterfaceData.Tab.values()) {
            if (buttonId == tab.getButton()) {
                open(player, tab);
                return true;
            }
        }

        // Unlocks
        for (SlayerRewardsInterfaceData.Unlock unlock : SlayerRewardsInterfaceData.Unlock.values()) {
            if (buttonId == unlock.getButton()) {
                if (!player.getSlayer().getUnlocks().contains(unlock.getUnlock())) {
                    Consumer<Player> accept = plr -> {
                        if (plr.getSlayer().unlock(unlock.getUnlock(), unlock.getCost())) {
                            open(plr, SlayerRewardsInterfaceData.Tab.UNLOCK);
                        }
                    };

                    info(player, accept, plr -> open(plr, SlayerRewardsInterfaceData.Tab.UNLOCK), unlock.getInformation());
                } else {
                    player.sendMessage("You already own this unlock.");
                }
                return true;
            }
        }

        // Extensions
        for (SlayerRewardsInterfaceData.Extend extend : SlayerRewardsInterfaceData.Extend.values()) {
            if (buttonId == extend.getButton()) {
                if (!player.getSlayer().getExtensions().contains(extend.getUnlock())) {
                    Consumer<Player> accept = plr -> {
                        if (plr.getSlayer().extend(extend.getUnlock(), extend.getCost())) {
                            open(plr, SlayerRewardsInterfaceData.Tab.EXTEND);
                        }
                    };

                    info(player, accept, plr -> open(plr, SlayerRewardsInterfaceData.Tab.EXTEND), extend.getInformation());
                } else {
                    player.sendMessage("You already own this extension.");
                }
                return true;
            }
        }

        // Unblock
        for (int index = 0; index < SlayerRewardsInterfaceData.UNBLOCK_TASK_BUTTONS.length; index++) {
            final int indexId = index;
            if (buttonId == SlayerRewardsInterfaceData.UNBLOCK_TASK_BUTTONS[index]) {
                String name = player.getSlayer().getRemoved()[indexId];
                if (name == null || name.length() == 0) {
                    player.sendMessage("You don't have a blocked task in this slot.");
                } else {
                    info(player, plr -> unblock(player, indexId), plr -> open(player, SlayerRewardsInterfaceData.Tab.TASK),
                            "<ul>" + Misc.formatPlayerName(name).replaceAll("_", " ") + "</ul>",
                            "",
                            "Slayer masters will assign this task again."
                    );
                }
                return true;
            }
        }

        // Other buttons
        switch (buttonId) {
            case SlayerRewardsInterfaceData.BLOCK_TASK_BUTTON:
                if (!player.getSlayer().getTask().isPresent()) {
                    player.sendMessage("You don't have a Slayer task.");
                } else {
                    info(player, SlayerRewardsInterface::block, plr -> open(player, SlayerRewardsInterfaceData.Tab.TASK),
                            "<ul>" + player.getSlayer().getTask().get().getFormattedName() + "</ul>",
                            "",
                            "Slayer masters will no longer assign this task.",
                            "",
                            String.format("@red@It will cost %d Slayer points.", player.getSlayer().getBlockTaskCost())
                    );
                }
                return true;
            case SlayerRewardsInterfaceData.CANCEL_TASK_BUTTON:
                if (!player.getSlayer().getTask().isPresent()) {
                    player.sendMessage("You don't have a Slayer task.");
                } else {
                    info(player, SlayerRewardsInterface::cancel, plr -> open(player, SlayerRewardsInterfaceData.Tab.TASK),
                            "<ul>" + player.getSlayer().getTask().get().getFormattedName() + "</ul>",
                            "",
                            "You will cancel this task.",
                            "This will not reset your task streak.",
                            String.format("@red@It will cost %d Slayer points.", player.getSlayer().getCancelTaskCost())
                    );
                }
                return true;
            case SlayerRewardsInterfaceData.INFO_CONFIRM_BUTTON:
                Object consumer = player.getAttributes().get(SlayerRewardsInterfaceData.INFO_BOX_ACCEPT_CONSUMER_KEY);
                if (consumer != null) {
                    ((Consumer<Player>) consumer).accept(player);
                }
                return true;
            case SlayerRewardsInterfaceData.INFO_DECLINE_BUTTON:
                consumer = player.getAttributes().get(SlayerRewardsInterfaceData.INFO_BOX_DECLINE_CONSUMER_KEY);
                if (consumer != null) {
                    ((Consumer<Player>) consumer).accept(player);
                }
                return true;
            default:
                return false;
        }
    }

    private static void cancel(Player player) {
        player.getSlayer().cancelTask();
        open(player, SlayerRewardsInterfaceData.Tab.TASK);
    }

    private static void block(Player player) {
        player.getSlayer().removeTask();
        open(player, SlayerRewardsInterfaceData.Tab.TASK);
    }

    private static void unblock(Player player, int index) {
        String[] removed = player.getSlayer().getRemoved();
        String[] newRemoved = new String[removed.length];
        removed[index] = "";
        int count = 0;

        for (int idx = 0; idx < removed.length; idx++)
            newRemoved[idx] = "";
        for (int idx = 0; idx < removed.length; idx++) {
            if (removed[idx] != null && removed[idx].length() > 0) {
                newRemoved[count++] = removed[idx];
            }
        }

        player.getSlayer().setRemoved(newRemoved);
        open(player, SlayerRewardsInterfaceData.Tab.TASK);
    }

    private static void info(Player player, Consumer<Player> accept, Consumer<Player> decline, String...strings) {
        Preconditions.checkArgument(strings.length <= SlayerRewardsInterfaceData.INFO_BOX_STRINGS.length, "Too many strings!");
        player.getAttributes().set(SlayerRewardsInterfaceData.INFO_BOX_ACCEPT_CONSUMER_KEY, accept);
        player.getAttributes().set(SlayerRewardsInterfaceData.INFO_BOX_DECLINE_CONSUMER_KEY, decline);
        for (int index = 0; index < SlayerRewardsInterfaceData.INFO_BOX_STRINGS.length; index++) {
            if (index < strings.length) {
                player.getPA().sendString(strings[index], SlayerRewardsInterfaceData.INFO_BOX_STRINGS[index]);
            } else {
                player.getPA().sendString("", SlayerRewardsInterfaceData.INFO_BOX_STRINGS[index]);
            }
        }
        player.getPA().showInterface(SlayerRewardsInterfaceData.INFO_BOX_INTERFACE_ID);
    }
}
