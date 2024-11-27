package io.xeros.content.dialogue.impl;

import io.xeros.content.CompletionistCape;
import io.xeros.content.SkillcapePerks;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueExpression;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.definition.Items;
import io.xeros.model.definition.Npcs;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.Arrays;

/**
 * Mac is an npc that sells max cape on ::maxisland.
 */
public class MacDialogue extends DialogueBuilder {

    public static final int MAX_CAPE_COST = 10_000_000;
    public static final int COMPLETIONIST_CAPE_COST = 25_000_000;

    private static final int NPC = Npcs.MAC;

    private static final DialogueOption WHO_ARE_YOU = new DialogueOption("Who are you?", MacDialogue::whoAreYou);
    private static final DialogueOption WHATS_IN_SACK = new DialogueOption("What do you have in your sack?", MacDialogue::whatDoYouHaveInYourSack);
    private static final DialogueOption WHY_YOU_DIRTY = new DialogueOption("Why are you so dirty?", MacDialogue::whyAreYouSoDirty);
    private static final DialogueOption BYE = new DialogueOption("Bye.", plr -> plr.getPA().closeAllWindows());
    private static final DialogueOption ANYTHING_ELSE_IN_SACK = new DialogueOption("What else do you have in your sack?", MacDialogue::whatElseDoYouHaveInYourSack);

    public MacDialogue(Player player, boolean initial) {
        super(player);
        setNpcId(NPC);

        if (initial) {
            player("Hello.");
            statement("The man glances at you and grunts something unintelligible.");
        }

        if (player.maxRequirements(player)) {
            boolean hasMaxCape = player.getItems().hasAnywhere(SkillcapePerks.MAX_CAPE_ID, SkillcapePerks.MAX_CAPE_HOOD);
            if (hasMaxCape) {
                option(WHO_ARE_YOU, ANYTHING_ELSE_IN_SACK, WHY_YOU_DIRTY, BYE);
            } else {
                option(WHO_ARE_YOU, WHATS_IN_SACK, ANYTHING_ELSE_IN_SACK, WHY_YOU_DIRTY, BYE);
            }
        } else {
            option(WHO_ARE_YOU, WHATS_IN_SACK, WHY_YOU_DIRTY, BYE);
        }
    }

    private static void whoAreYou(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.player("Who are you?");
        builder.npc("Mac. What's it to you?");
        builder.player("Only trying to be friendly.");
        builder.next(new MacDialogue(player, false));
        player.start(builder);
    }

    private static void whatDoYouHaveInYourSack(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.player("What do you have in your sack?");
        builder.npc("S'me cape.");
        builder.player("Your cape?");
        builder.option(
                new DialogueOption("Can I have it?", MacDialogue::canIHaveIt),
                new DialogueOption("Why do you keep it in a sack?", MacDialogue::whyDoYouKeepItInASack)
        );
        player.start(builder);
    }

    private static void whatElseDoYouHaveInYourSack(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.player("What else do you have in your sack?");
        builder.npc(DialogueExpression.ANGER_1, "Have you been looking through my sack?");
        builder.player(DialogueExpression.LAUGH_1, "No.. just curious.");
        builder.npc("Oh.. that's good then.");

        if (!CompletionistCape.hasRequirements(player)) {
            builder.npc("I have something you might be interested in.", "But I'm afraid you're not quite ready for it yet.");
            builder.player("What do I need to do?");
            builder.npc("Here, have a look.");
            builder.exit(CompletionistCape::sendRequirementsInterface);
            player.start(builder);
            return;
        }

        builder.npc(DialogueExpression.HAPPY, "I have something you might be interested in,", "and you seem to be ready to use it.");
        builder.npc("It's a new cape but it's very expensive, I'll need", "25,000,000 coins for it.");
        builder.player(DialogueExpression.DISTRESSED, "25,000,000?? You can't be serious!");
        builder.npc("I am.", "So what will it be?");
        builder.option(
                new DialogueOption("Buy Completionist cape for 25,000,000.", plr -> purchase(plr, COMPLETIONIST_CAPE_COST, CompletionistCape.hasRequirements(plr),
                        Items.COMPLETIONIST_CAPE)),
                new DialogueOption("No way!", plr -> plr.start(new DialogueBuilder(plr).setNpcId(NPC).npc("Suit yourself.")))
        );
        player.start(builder);
    }

    private static void whyAreYouSoDirty(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.player("Why are you so dirty?");
        builder.npc("Bath XP waste.");
        builder.next(new MacDialogue(player, false));
        player.start(builder);
    }

    private static void whyDoYouKeepItInASack(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.player("Why do you keep it in a sack?");
        builder.npc("Get it dirty.");
        builder.next(new MacDialogue(player, false));
        player.start(builder);
    }

    private static void canIHaveIt(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.player("Can I have it?");

        if (!player.maxRequirements(player)) {
            builder.npc("...", "(You haven't maxed all your skills yet)");
            builder.next(new MacDialogue(player, false));
            player.start(builder);
            return;
        }

        builder.npc("Mebe.");
        builder.player("I'm sure I could make it worth your while.");
        builder.npc("How much?");
        builder.player("How about 10,000,000 gold?");
        builder.option(
                "Buy Mac's cape?",
                new DialogueOption("Yes, pay the man.", plr -> purchase(plr, MAX_CAPE_COST, player.maxRequirements(player),
                        SkillcapePerks.MAX_CAPE_ID, SkillcapePerks.MAX_CAPE_HOOD)),
                new DialogueOption("No.", plr -> plr.getPA().closeAllWindows())
        );
        player.start(builder);
    }

    private static void purchase(Player player, int cost, boolean requirement, int...gameItems) {
        if (!requirement) {
            player.getPA().removeAllWindows();
            return;
        }

        if (player.getItems().freeSlots() < gameItems.length) {
            player.start(new DialogueBuilder(player).setNpcId(NPC).npc(
                    "You don't have enough space in your inventory, go throw some",
                    "of it away."
            ));
            return;
        }

        if (!player.getItems().playerHasItem(995, cost)) {
            player.start(new DialogueBuilder(player).setNpcId(NPC).npc(
                    "I'm afraid you don't have enough coins.",
                    "Come back when you have " + Misc.insertCommas(cost) + "."
            ));
            return;
        }

        player.getItems().deleteItem(995, cost);
        Arrays.stream(gameItems).forEach(it -> player.getItems().addItemUnderAnyCircumstance(it, 1));
        player.start(new DialogueBuilder(player).itemStatement(gameItems[0],
                "Mac grunts and hands over his cape, pocketing your\\nmoney swiftly."));
    }
}
