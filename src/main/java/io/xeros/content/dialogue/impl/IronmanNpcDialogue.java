package io.xeros.content.dialogue.impl;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueExpression;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.definition.Items;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeRevertType;
import io.xeros.model.entity.player.mode.ModeType;
import io.xeros.model.entity.player.mode.group.GroupIronmanGroup;
import io.xeros.model.entity.player.mode.group.GroupIronmanRepository;
import io.xeros.model.items.ImmutableItem;

import static io.xeros.model.entity.player.Right.*;

public class IronmanNpcDialogue extends DialogueBuilder {

    private static final Map<Mode, EnumSet<Right>> SWITCHES;

    static {
        SWITCHES = new HashMap<>();

        SWITCHES.put(Mode.forType(ModeType.ROGUE), EnumSet.of(PLAYER));
        SWITCHES.put(Mode.forType(ModeType.ROGUE_IRONMAN), EnumSet.of(PLAYER, ROGUE));
        SWITCHES.put(Mode.forType(ModeType.ROGUE_HARDCORE_IRONMAN), EnumSet.of(PLAYER, ROGUE));

        SWITCHES.put(Mode.forType(ModeType.IRON_MAN), EnumSet.of(PLAYER, GROUP_IRONMAN));
        SWITCHES.put(Mode.forType(ModeType.HC_IRON_MAN), EnumSet.of(PLAYER, GROUP_IRONMAN));
        SWITCHES.put(Mode.forType(ModeType.GROUP_IRONMAN), EnumSet.of(PLAYER));
        SWITCHES.put(Mode.forType(ModeType.ULTIMATE_IRON_MAN), EnumSet.of(PLAYER, IRONMAN));

    }

    private final NPC npc;
    private static final String RIGHT_REVERT_ATTRIBUTE_KEY = "revert_mode_right";
    private static final String NO_ACCESS_MESSAGE = "I only deal with Ironman and Rogue players, begone!";

    public IronmanNpcDialogue(Player player, NPC npc) {
        super(player);
        this.npc = npc;
        setNpcId(npc.getNpcId());

        if (SWITCHES.containsKey(player.getMode())) {
            DialogueOption changeGameMode = new DialogueOption("I would like to change my game mode.", p -> changeGameMode());
            npc("Hello friend, how can I be of assistance?");

            if (!player.getMode().isIronmanType()) {
                option(changeGameMode, DialogueOption.nevermind());
                return;
            }

            option(new DialogueOption("I would like to make my Ironman mode permanent.", p -> makeIronmanPermanent()),
                    changeGameMode,
                    new DialogueOption("Do you have any extra ironman armours?", p -> giveIronmanArmour(p, npc)));
        } else {
            setNpcId(npc.getNpcId()).npc(DialogueExpression.ANGER_1, NO_ACCESS_MESSAGE);
        }
    }

    private void changeGameMode() {
        if (getPlayer().getModeRevertType() == ModeRevertType.PERMANENT) {
            getPlayer().start(new DialogueBuilder(getPlayer()).setNpcId(npc.getNpcId())
                    .npc("Your game mode was set to permanent.", "You cannot change it."));
            return;
        }

        if (getPlayer().getRights().contains(Right.GROUP_IRONMAN)) {
            GroupIronmanGroup group = GroupIronmanRepository.getGroupForOnline(getPlayer()).orElse(null);
            if (group != null) {
                getPlayer().start(new DialogueBuilder(getPlayer()).setNpcId(npc.getNpcId())
                        .npc("You must leave your group before changing modes.", "@red@Then you can change to a regular Player only."));
                return;
            }
        }

        if (getPlayer().getBankPin().requiresUnlock()) {
            getPlayer().getBankPin().open(2);
            return;
        }

        EnumSet<Right> switchable = SWITCHES.get(getPlayer().getMode());
        if (switchable == null || switchable.isEmpty()) {
            new DialogueBuilder(getPlayer()).npc(npc.getNpcId()).npc("You can't swap to any modes.").send();
            return;
        }

        List<DialogueOption> optionList = switchable.stream().map(it -> new DialogueOption("Switch to " + it.getFormattedName(), plr -> {
            getPlayer().getAttributes().set(RIGHT_REVERT_ATTRIBUTE_KEY, it);
            sendRevertConfirmationDialogue();
        })).collect(Collectors.toList());

        DialogueOption[] options = new DialogueOption[switchable.size() + 1];

        int i = 0;
        for (; i < optionList.size(); i++)
            options[i] = optionList.get(i);
        options[i] = DialogueOption.nevermind();

        new DialogueBuilder(getPlayer()).option(options).send();
    }

    private void sendRevertConfirmationDialogue() {
        Consumer<Player> revert = p -> {
            switch (getPlayer().getMode().getType()) {
                case GROUP_IRONMAN:
                    p.getRights().remove(Right.GROUP_IRONMAN);
                    break;
                case IRON_MAN:
                    p.getRights().remove(Right.IRONMAN);
                    break;
                case ULTIMATE_IRON_MAN:
                    p.getRights().remove(Right.ULTIMATE_IRONMAN);
                    break;
                case HC_IRON_MAN:
                    p.getRights().remove(Right.HC_IRONMAN);
                    break;
                case ROGUE_HARDCORE_IRONMAN:
                    p.getRights().remove(Right.ROGUE_HARDCORE_IRONMAN);
                    break;
                case ROGUE_IRONMAN:
                    p.getRights().remove(Right.ROGUE_IRONMAN);
                    break;
                case ROGUE:
                    p.getRights().remove(Right.ROGUE);
                    break;
                default:
                    p.getPA().closeAllWindows();
                    p.sendMessage("A mode switch error occurred.");
                    return;
            }

            Right right = getRevertToRight(p);
            p.setMode(right.getMode());
            p.getRights().setPrimary(right);
            getPlayer().start(new DialogueBuilder(getPlayer()).setNpcId(npc.getNpcId())
                    .npc("Your mode has been switched to " + right.getFormattedName() + "."));
        };

        DialogueBuilder db = new DialogueBuilder(getPlayer()).setNpcId(npc.getNpcId());
        db.npc(DialogueExpression.DISTRESSED,
                "Are you sure you want to change to " + getRevertToRight(getPlayer()).getFormattedName() + "?",
                "You can never change back!")
                .option(
                        new DialogueOption("Yes, change mode to " + getRevertToRight(getPlayer()).getFormattedName() + ".", revert),
                        new DialogueOption("Nevermind!", p -> p.getPA().closeAllWindows())
                );
        getPlayer().start(db);
    }

    private static Right getRevertToRight(Player player) {
        return (Right) player.getAttributes().get(RIGHT_REVERT_ATTRIBUTE_KEY);
    }

    private void makeIronmanPermanent() {
        if (!getPlayer().getMode().isIronmanType()) {
            getPlayer().start(new DialogueBuilder(getPlayer()).setNpcId(npc.getNpcId())
                    .npc(DialogueExpression.ANGER_1, "Only Ironman players can make their mode permanent."));
            return;
        }

        if (getPlayer().getModeRevertType() == ModeRevertType.PERMANENT) {
            getPlayer().start(new DialogueBuilder(getPlayer()).setNpcId(npc.getNpcId()).npc("Your ironman status is already permanent!"));
        } else {
            Consumer<Player> perm = p -> {
                p.setModeRevertType(ModeRevertType.PERMANENT);
                p.start(new DialogueBuilder(getPlayer()).setNpcId(npc.getNpcId()) .npc("Your ironman mode is now permanent."));
            };

            getPlayer().start(new DialogueBuilder(getPlayer()).setNpcId(npc.getNpcId())
                    .npc("Are you sure?", "You can never change even through asking staff.")
                    .option(new DialogueOption("Yes, make my ironman mode permanent.", perm),
                            new DialogueOption("Nevermind!", p -> p.getPA().closeAllWindows())));
        }
    }

    public static void giveIronmanArmour(Player player, NPC npc) {
        if (!player.getMode().isIronmanType()) {
            player.start(new DialogueBuilder(player).setNpcId(npc.getNpcId()).npc(DialogueExpression.ANGER_1, NO_ACCESS_MESSAGE));
            return;
        }

        List<ImmutableItem> armours = Lists.newArrayList();
        if (player.getMode().isHardcoreIronman()) {
            armours.addAll(Lists.newArrayList(
                    new ImmutableItem(Items.HARDCORE_IRONMAN_HELM),
                    new ImmutableItem(Items.HARDCORE_IRONMAN_PLATEBODY),
                    new ImmutableItem(Items.HARDCORE_IRONMAN_PLATELEGS)));
        } else {
            armours.addAll(Lists.newArrayList(
                    new ImmutableItem(Items.IRONMAN_HELM),
                    new ImmutableItem(Items.IRONMAN_PLATEBODY),
                    new ImmutableItem(Items.IRONMAN_PLATELEGS)));
        }

        armours.forEach(item -> player.getInventory().addAnywhere(item));
        player.start(new DialogueBuilder(player).setNpcId(npc.getNpcId())
                .itemStatement(armours.get(0).getId(), "Adam hands you the armour.").npc("There you go, enjoy!"));
    }
}
