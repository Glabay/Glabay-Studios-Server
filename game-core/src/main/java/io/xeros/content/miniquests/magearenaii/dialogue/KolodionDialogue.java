package io.xeros.content.miniquests.magearenaii.dialogue;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.miniquests.magearenaii.MageArenaII;
import io.xeros.model.entity.player.Player;

public class KolodionDialogue extends DialogueBuilder {

    private static final int NPC_ID = 1603;

    private Player player;

    @Override
    public void initialise() {
        super.initialise();
    }

    public KolodionDialogue(Player player) {
        super(player);
        this.player = player;

        if (!player.mageArena2Stages[0]) {
            setNpcId(NPC_ID).
                    player("Hello, Kolodion.").
                    npc("Hey there, how are you? Are you enjoying the bloodshed?").
                    player("It's not bad; I've seen worse.").
                    option(
                            new DialogueOption("I think I've had enough for now.", p -> firstOption(1)),
                            new DialogueOption("How can I use my new spells outside of the arena?", p -> secondOption(1)),
                            new DialogueOption("Are there any more challenges available?", p -> thirdOption(1)));
            return;
        }

        if (!MageArenaII.hasSymbol(player)) {
            setNpcId(NPC_ID).npc("Here, take this enchanted symbol of the gods. It will", "guide you to the creatures").
                    itemStatement(MageArenaII.SYMBOL_ID, "Kolodion hands you an enchanted symbol.").exit(player1 -> {
                player1.getItems().addItemUnderAnyCircumstance(MageArenaII.SYMBOL_ID, 1);
                afterSymbolDialogue();
            });
            return;
        }

        if (player.mageArena2Stages[1]) {
            setNpcId(NPC_ID).npc("If you would like me to imbue any more capes, just", "use the cape of the respective", "god on me and I'll imbue it for you");
            return;
        }

        if (MageArenaII.hasAllItems(player) && !player.mageArena2Stages[1]) {
            MageArenaII.removeBossItems(player);
            Achievements.increase(player, AchievementType.MAGE_ARENA_II, 1);
            setNpcId(NPC_ID).npc("Excellent work, " + player.getDisplayName() + ", now you just need to", "hand me the god cape you wish to have imbued with", "great power.");
            return;
        }


        setNpcId(NPC_ID).npc("Bring me the remains of ALL THREE magical beings. Do", "this and I will imbue their combined power into a single", "god cape of your choice.").exit(player1 -> {
            player.start(option(
                    new DialogueOption("Where can I find these beings?", p -> firstOption(3)),
                    new DialogueOption("Any advice on fighting the beings?", p -> secondOption(3)),
                    new DialogueOption("Tell me more about these beings.", p -> thirdOption(3)),
                    new DialogueOption("Thanks, bye.", p -> fourthOption(3))));
        });
        return;
    }

    /**
     * Kolodion takes your cape and imbues it with the power, of the gods before handing it back.
     *
     * @return If you would like me to imbue any more capes, just", "use the cape of the respective god on me and I'll imbue it for you"
     */

    private void firstOption(int stageId) {
        switch (stageId) {

            case 1:
                player.start(new DialogueBuilder(getPlayer()).setNpcId(NPC_ID).
                        player("I think I've had enough for now.").
                        npc("A shame. You're a good battle mage. I hope to see you", "soon."));
                return;
            case 2:
                //if clicked first

                player.start(new DialogueBuilder(getPlayer()).setNpcId(NPC_ID).
                                player("Great, I've been waiting for an improvement!").
                                player("What do you need me to do?").
                                npc("I will need you to kill these three beings and bring me", "their remains. Even in death, their power should be", "great enough for me to harness it for our own uses.").
                                player("Where am I supposed to find these beings?").
                                npc("Here, take this enchanted symbol of the gods. It will", "guide you to the creatures.").
                                itemStatement(MageArenaII.SYMBOL_ID, "Kolodion hands you an enchanted symbol.").exit(player1 -> {
                            player1.getItems().addItemUnderAnyCircumstance(MageArenaII.SYMBOL_ID, 1);
                            afterSymbolDialogue();
                        })
                );
                return;

            case 3:
                player.start(new DialogueBuilder(getPlayer()).setNpcId(NPC_ID).
                        player("Where can I find these beings?").
                        npc("They can all be found in the Wilderness. The", "enchanted symbol can track their unique magical", "energies, leading you to their exact locations.").
                        npc("Be warned though, the symbol will take a blood sacrifice", "every time it's used."));//exit


                return;
        }
    }

    private void secondOption(int stageId) {

        switch (stageId) {

            case 1:
                player.start(new DialogueBuilder(getPlayer()).setNpcId(NPC_ID).
                        player("How can I use my new spells outside of the arena?").
                        npc("Experience, my friend, experience. Once you've used", "the spell enough times in the arena, you'll be able to use", "them in the rest of Gielinor").
                        player("Good stuff.").
                        npc("Not so good for the citizens; they won't stand a chance.").
                        player("How am I doing so far?").
                        npc("You still need to train with the strike spell inside the", "arena before you can use it outside."));//end

                return;

            case 2:
                player.start(new DialogueBuilder(getPlayer()).setNpcId(NPC_ID).
                        player("Actually, my current cape is fine.").
                        npc("I see... If you ever change your mind, you know", "where to find me."));
                return;

            case 3:
                player.start(new DialogueBuilder(getPlayer()).setNpcId(NPC_ID).
                        player("Any advice on fighting the beings?").
                        npc("They can't be harmed by normal magic, melee or", "ranged attacks, you will need to use the god combat", "spell corresponding to the creature you are fighting."));
                return;
        }


    }

    private void thirdOption(int stageId) {

        switch (stageId) {

            case 1:
                if (player.playerLevel[6] < 75) {
                    player.start(new DialogueBuilder(getPlayer()).setNpcId(NPC_ID).npc("You need a Magic Level of 75 before", "trying to accept anymore challenges!"));
                    return;
                }

                if (!player.canUseGodSpellsOutsideOfMageArena()) {
                    player.start(new DialogueBuilder(getPlayer()).setNpcId(NPC_ID).
                            player("Are there any more challenges available?").
                            npc("You still need to learn how to cast all good spells outside", "of the arena.").
                            npc("You need more godstrike casts!", "Claws Of Guthix: " + player.clawsOfGuthixCasts + "/100", "Saradomin Strikes: " + player.saradominStrikeCasts + "/100", "Flames Of Zamorak: " + player.flamesOfZamorakCasts + "/100"));
                    return;
                }//You are not yet experienced enough to use this spell outside the Mage Arena.
                //You are experienced enough to use the spells outside of the mage arena

                //You can now cast Saradomin Strike outside the Arena.


                player.start(new DialogueBuilder(getPlayer()).setNpcId(NPC_ID).
                        player("Are there any more challenges available?").
                        npc("I am one of the most poweful mages in existence. But", "even my power has limitations. There are some beings", "however that have power exceeding even my own.").
                        npc("I have detected three life forces within the Wilderness", "each of them giving out a strange magical power, one", "unlike any I have ever felt before.").
                        npc("If we could harness the power of these beings, I could", "imbue your god cape with that power, greatly increasing", "its potential.").
                        option(
                                new DialogueOption("Great, I've been waiting for an improvement!", p -> firstOption(2)),
                                new DialogueOption("Actually, my current cape is fine", p -> secondOption(2))));
                return;

            case 3:
                player.start(new DialogueBuilder(getPlayer()).setNpcId(NPC_ID).
                        player("Tell me more about these beings.").
                        npc("One is a justiciar. According to legend, the justiciars", "were the elite of Saradomin's forces. Some worked", "alone, operating as assassins behind enemy lines. Others", "led entire armies in Saradomin's name.").
                        npc("I couldn't tell you why this one wanders the", "Wilderness, but I suspect he has good reason.").
                        npc("The second is a Demon of Zamorak. From what I can", "tell, he is completely mad. He roams the Wilderness,", "killing all who he comes across without mercy. Even his", "own kind do not escape his fury.").
                        npc("He's known to battle the justiciar quite regularly,", "perhaps they are old enemies.").
                        npc("The final being is a Guthixian Ent. Like most followers", "of Guthix, he will avoid combat if possible. Make no", "mistake though, he is deadly when threatened.").
                        npc("He seems to take great interest in the other two", "creatures. Maybe he is trying to keep a balance", "between them.").
                        npc("All three of them give off a unique magical energy, one", "unlike any I have ever felt before. The fact that the", "three of them are so different, yet possess powers so", "similar, is very odd indeed.").
                        npc("I'd be very interested in learning more about the", "source of their power."));
                return;
        }
    }

    private void fourthOption(int stage) {

        switch (stage) {

            case 3:
                player.start(new DialogueBuilder(getPlayer()).setNpcId(NPC_ID).player("Thanks, bye.").npc("Farewell, " + getPlayer().getDisplayName() + "."));
                return;

        }

    }

    private void afterSymbolDialogue() {
        MageArenaII.assignSpawns(player);
        player.start(new DialogueBuilder(getPlayer()).setNpcId(NPC_ID).
                npc("Bring me the remains of all three magical beings. Do", "this and I will imbue their combined power into a single", "god cape of your choice.").
            option(
                    new DialogueOption("Where can I find these beings?", p -> firstOption(3)),
                    new DialogueOption("Any advice on fighting the beings?", p -> secondOption(3)),
                    new DialogueOption("Tell me more about these beings.", p -> thirdOption(3)),
                    new DialogueOption("Thanks, bye.", p -> fourthOption(3))));

    }

}
