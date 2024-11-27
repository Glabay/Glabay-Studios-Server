package io.xeros.content.questing.MonkeyMadness;

import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueExpression;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.questing.Quest;
import io.xeros.model.definition.Npcs;
import io.xeros.model.SkillLevel;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.ModeType;

public class MonkeyMadnessQuest extends Quest {


    private static final int SHOP_ID = 190;

    public MonkeyMadnessQuest(Player player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Monkey Madness";
    }

    @Override
    public List<SkillLevel> getStartRequirements() {
        return Lists.newArrayList();
    }

    @Override
    public List<String> getJournalText(int stage) {
        List<String> lines = Lists.newArrayList();
        switch (stage) {
            case 0:
                lines.add("To start this quest go to the minigames teleport and teleport");
                lines.add("to 'Monkey Madness' then speak to King Narnode.");
                lines.add("");
                lines.add("Rewards:");
                lines.add("Access to dragon scimitar shop.");
                lines.add("Experience lamp x 2.");
                lines.add("1m coins.");
                lines.addAll(getStartRequirementLines());
                break;
            case 1:
                lines.add("King Narnode wants me to check in with his spy at Ape Atoll.");
                lines.add("I should use the Gnome Glider at the top of the tree.");
                break;
            case 2:
                lines.add("The Gnome Glider mentioned the 10th squad being near the");
                lines.add("monkey king. I should go check it out.");
                break;
            case 3:
                lines.add("The spy gave me the 10th squad sigil.");
                lines.add("I should use it once I'm ready to fight the demon.");
                break;
            case 4:
                lines.add("The demon is dead! I should report back to the king.");
                break;
        }
        return lines;
    }

    @Override
    public int getCompletionStage() {
        return 5;
    }

    @Override
    public List<String> getCompletedRewardsList() {
        return Lists.newArrayList("Access to the King's shop.", "2 Experience Lamps.", "1m coins");
    }

    @Override
    public void giveQuestCompletionRewards() {
        player.getItems().addItemUnderAnyCircumstance(995, 1_000_000);
        player.getItems().addItemUnderAnyCircumstance(2528, 1);
        player.getItems().addItemUnderAnyCircumstance(2528, 1);
    }

    @Override
    public boolean handleNpcClick(NPC npc, int option) {
        if (npc.getNpcId() == Npcs.CAPTAIN_ERRDO) {
            if (option == 1) {
                if (getStage() == 0) {
                    player.start(getCaptainErrdo()
                            .player("Hello there.")
                            .npc(DialogueExpression.HAPPY, "Hello.")
                            .exit(plr -> {
                            }));
                } else if (getStage() == 1) {
                    player.start(getCaptainErrdo()
                            .player(DialogueExpression.DISTRESSED, "Do you know anything about the 10th squad?")
                            .npc("I sure do, I can take you to the island they are", "currently at. I think one of them already", "made there way towards the monkey King.")
                            .option(new DialogueOption("Yes, take me there.", p -> {
                                        p.getPA().startTeleport(2811, 2755, 0, "modern", false);
                                        incrementStage();
                                    }

                                    ),
                                    new DialogueOption("No.", p -> p.getPA().closeAllWindows())
                            ));
                } else if (getStage() >= 2 || isQuestCompleted()) {
                    player.start(getCaptainErrdo()
                            .npc(DialogueExpression.HAPPY, "Would you like to go back to Ape Atoll?")
                            .option(new DialogueOption("Yes, take me back.", p -> p.getPA().startTeleport(2811, 2755, 0, "modern", false)),
                                    new DialogueOption("No.", p -> p.getPA().closeAllWindows())

                            ));
                }
            } else if (option == 2) {
                if (isQuestCompleted()) {
                    player.getPA().startTeleport(2811, 2755, 0, "modern", false);
                } else {
                    if (getStage() >= 2) {
                          player.start(getCaptainErrdo()
                                .npc("Do you want to go back to the island?")
                                .option(new DialogueOption("Yes, take me there.", p -> {
                                            p.getPA().startTeleport(2811, 2755, 0, "modern", false);
                                            incrementStage();
                                        }

                                        ),
                                        new DialogueOption("No.", p -> p.getPA().closeAllWindows())
                                ));
                    } else {
                        player.start(getCaptainErrdo().npc(DialogueExpression.ANGER_1, "I don't have time for this right now!"));
                    }
                }
            }
            return true;
        }

        if (npc.getNpcId() == Npcs.GARKOR) {
            if (option == 1) {
                if (player.getItems().freeSlots() < 1) {
                    player.sendMessage("You need at least one free slot to talk to Garkor.");
                } else {
                    if (getStage() == 0) {
                        player.start(getGarkor()
                                .npc(DialogueExpression.DISTRESSED, "How did you get here?")
                        );
                    } else if (getStage() == 2) {
                        player.start(getGarkor()
                                .player("Hello?")
                                .npc(DialogueExpression.LAUGH_1, "A fine day you have chosen to visit this hellish island, human.")
                                .player("Good day to you Sergeant.", "I've been sent by your King Narnode to...")
                                .npc(DialogueExpression.SPEAKING_CALMLY, "Investigate the circumstances surrounding the mysterious", "disappearance of my squad. Yes, I know.")
                                .player("So what am I doing here?")
                                .npc(DialogueExpression.SPEAKING_CALMLY, "Long story short, we have to kill a demon.")
                                .npc(DialogueExpression.SPEAKING_CALMLY, "When you are ready to fight, use this to teleport to the demon.", "Good luck.")
                                .itemStatement(4035, "Garkor hands you the sigil.")
                                .exit(plr -> {
                                    incrementStage();
                                    player.getItems().addItem(4035, 1);
                                }));
                    } else if (getStage() == 3) {
                        player.start(getGarkor()
                                .npc(DialogueExpression.CALM, "Use the sigil when you are ready human.")
                        );
                    } else if (getStage() == getCompletionStage()) {
                        player.start(getGarkor()
                                .npc(DialogueExpression.HAPPY, "Thank you human.")

                        );
                    }
                }
            }
                return true;

        }

        if (npc.getNpcId() == Npcs.KING_NARNODE_SHAREEN) {
            if (option == 1) {
                if (getStage() == 0) {
                    player.start(getKingNarnodeDialogue()
                            .player("Hello King, how fares the tree?")
                            .npc(DialogueExpression.DISTRESSED, "The tree? It is fine..")
                            .player("King, you look worried. Is anything the matter?")
                            .npc(DialogueExpression.ANGER_1, "Nothing in particular... Well actually, yes... there is.", "The 10th squad hasn't reported back from a recent mission.")
                            .player("Maybe I can find them.")
                            .npc(DialogueExpression.HAPPY, "That would be great! Please report", "back to me when you find out anything!", "You should talk to the pilot at the top of the tree.")
                            .exit(plr -> {
                                incrementStage();
                            }));
                } else if (getStage() == 1 || getStage() == 2) {
                    player.start(getKingNarnodeDialogue()
                            .npc(DialogueExpression.ANGER_1, "Please, go find the 10th squad!", "Talk to the pilot at the top of the tree."));
                } else if (getStage() == 3) {
                    player.start(getKingNarnodeDialogue()
                            .player(DialogueExpression.HAPPY,"I've found the 10th squad!")
                            .npc(DialogueExpression.HAPPY, "I know. They told me already.")
                            .player("...")
                            .npc(DialogueExpression.ANNOYED, "Don't you have a demon to kill?")
                            .exit(plr -> {
                                plr.getPA().closeAllWindows();
                            }));
                } else if (getStage() == 4) {
                    player.start(getKingNarnodeDialogue()
                            .npc(DialogueExpression.ANNOYED, "How is the mission going?", "It has been quite some time since I", "you on your way.")
                            .player("I've defeated the demon!")
                            .npc(DialogueExpression.HAPPY, "Well done, human! Please take this as a reward.", "Come by my shop any time you like.")
                            .exit(plr -> {
                                plr.getPA().closeAllWindows();
                                incrementStage();
                                giveQuestCompletionRewards();
                            }));
                }
            } else if (option == 2) {
                if (isQuestCompleted()) {
                    player.getShops().openShop(SHOP_ID);
                } else {
                    if (getStage() == 3) {
                        player.start(getKingNarnodeDialogue().npc(DialogueExpression.ANGER_1, "Don't you have a demon to kill, human."));
                    } else {
                        player.start(getKingNarnodeDialogue().npc(DialogueExpression.ANGER_1, "and you are?"));
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean handleObjectClick(WorldObject object, int option) {
        switch (object.getId()) {
            case 4485:

                return true;
        }

        return false;
    }

    @Override
    public boolean handleItemClick(int itemId) {
        switch (itemId) {
            case 4587:
            case 20000:
                if (!isQuestCompleted() && (player.getMode().getType() == ModeType.IRON_MAN ||
                        player.getMode().getType() == ModeType.ULTIMATE_IRON_MAN ||
                        player.getMode().getType() == ModeType.HC_IRON_MAN ||
                        player.getMode().getType() == ModeType.ROGUE_IRONMAN ||
                        player.getMode().getType() == ModeType.ROGUE_HARDCORE_IRONMAN)) {
                    player.sendMessage("Monkey Madness is required to wield this weapon for your game mode.");
                    return true;
                }
                return false;
            case 4035:
                if (getStage() == 3 && !player.getPosition().inWild()) {
                    player.start(getGarkor()
                            .option(new DialogueOption("Yes, teleport me. I am ready to fight.", p -> {
                                        new MMDemonInstance(player, !isQuestCompleted()).init();
                                    }
                                    ),
                                    new DialogueOption("No, I am not ready to fight.", p -> p.getPA().closeAllWindows())
                            ));
                    return true;
                } else if (player.getPosition().inWild()) {
                    player.sendMessage("Please use this outside of the wilderness.");
                } else {
                    player.sendMessage("You can no longer teleport back.");
                }
                return true;
        }

        return false;
    }

    @Override
    public void handleNpcKilled(NPC npc) {
        if (Boundary.MONKEY_MADNESS_DEMON.in(player) && npc.getNpcId() == Npcs.JUNGLE_DEMON) {
            if (getStage() == 3) {
                incrementStage();
                player.start(new DialogueBuilder(player).player("I should tell the king the good news."));
            }
        }
    }

    private DialogueBuilder getKingNarnodeDialogue() {
        DialogueBuilder builder = new DialogueBuilder(player);
        builder.setNpcId(Npcs.KING_NARNODE_SHAREEN);
        return builder;
    }
    private DialogueBuilder getCaptainErrdo() {
        DialogueBuilder builder = new DialogueBuilder(player);
        builder.setNpcId(Npcs.CAPTAIN_ERRDO);
        return builder;
    }
    private DialogueBuilder getGarkor() {
        DialogueBuilder builder = new DialogueBuilder(player);
        builder.setNpcId(Npcs.GARKOR);
        return builder;
    }
}
