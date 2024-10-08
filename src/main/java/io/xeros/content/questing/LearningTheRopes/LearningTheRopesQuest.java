package io.xeros.content.questing.LearningTheRopes;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueExpression;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.questing.Quest;
import io.xeros.model.Npcs;
import io.xeros.model.SkillLevel;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

import java.util.List;

public class LearningTheRopesQuest extends Quest {



    public LearningTheRopesQuest(Player player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Learning The Ropes";
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
                lines.add("To start this quest talk to the Xeros Guide");
                lines.add("next to the well of good will.");
                lines.add("");
                lines.add("Rewards:");
                lines.addAll(getCompletedRewardsList());
                lines.addAll(getStartRequirementLines());
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                lines.add("You still need to open all 5 of those interfaces");
                lines.add("found in the quest tab, and then coin tab.");
                lines.add("Reminder:");
                lines.add("Collection Log, Monster Kill Log, Drop Table,");
                lines.add("Loot Table, and World Events");
                break;
            case 6:
                lines.add("I completed the interface task.");
                lines.add("I should talk to the guide again.");
                break;
            case 7:
                lines.add("I need to go kill a hoboglin.");
                lines.add("The guide said it can be found on skilling island.");
                lines.add("I can use the teleport platform at home.");
                break;
            case 8:
                lines.add("I should bring the sword back to the guide!");
                break;
            case 9:
                lines.add("The guide said the fire is behind the slayer");
                lines.add("house at home.");
                lines.add("I should report back to him after burning the sword.");
                break;
            case 10:
                lines.add("The sword is burned, now to talk to the guide.");
                break;
        }
        return lines;
    }

    @Override
    public int getCompletionStage() {
        return 11;
    }

    @Override
    public List<String> getCompletedRewardsList() {
        return Lists.newArrayList("Cosmetic Sword", "3 Experience Lamps.", "1m coins", "100 shark", "10 Super Combats");
    }

    @Override
    public void giveQuestCompletionRewards() {
        player.getItems().addItemUnderAnyCircumstance(995, 1_000_000);
        player.getItems().addItemUnderAnyCircumstance(2528, 1);
        player.getItems().addItemUnderAnyCircumstance(2528, 1);
        player.getItems().addItemUnderAnyCircumstance(2528, 1);
        player.getItems().addItemUnderAnyCircumstance(22316, 1);
        player.getItems().addItemUnderAnyCircumstance(386, 100);
        player.getItems().addItemUnderAnyCircumstance(12696, 10);
    }

    @Override


    public boolean handleNpcClick(NPC npc, int option) {
        if (npc.getNpcId() == Npcs.MOUNTAIN_GUIDE) {
            if (option == 1) {
                if (getStage() == 0) {

                    player.start(getXerosGuide()
                            .npc("Hello, how can I help you?")
                            .option(new DialogueOption("How do I teleport?", p -> {
                                        player.start(getXerosGuide()
                                                .npc("You can use any highlighted teleport in the spellbook.")
                                                .exit(plr -> {
                                                            plr.getPA().closeAllWindows();
                                        }
                                                ));
                                    }
                                    ),
                                    new DialogueOption("How do I vote?",  p -> {
                                        player.start(getXerosGuide()
                                                .npc("Use ::vote to open our vote page, and ::voted to claim them.")
                                                .exit(plr -> {
                                                            plr.getPA().closeAllWindows();
                                                        }
                                                ));
                                    }
                                    ),
                                    new DialogueOption("How do I open the drop table?",  p -> {
                                        player.start(getXerosGuide()
                                                .npc("Go to the quest tab and then the coin tab to find the drop table.")
                                                .exit(plr -> {
                                                            plr.getPA().closeAllWindows();
                                                        }
                                                ));
                                    }
                                    ),
                                    new DialogueOption("Do you know of any quest?",  p -> {
                                        int totalReq = (player.getMode().is5x() ? 100 : 500);
                                        if (player.totalLevel > totalReq) {
                                            player.start(getXerosGuide()
                                                    .npc(DialogueExpression.HAPPY, "Yes, of course I do!", "Would you like to start one?")
                                                    .option(new DialogueOption("Sure, I can start now!", e -> {
                                                                e.getPA().closeAllWindows();
                                                                incrementStage();
                                                                e.start(getXerosGuide()
                                                                        .npc("For you first task, please open these 5 interfaces", "found in the quest tab, coin tab...")
                                                                        .npc("Collection Log, Monster Kill Log, Drop Table", "Loot Tables, and World Events.")
                                                                        .npc("The information these display", "will help you during your time in Xeros.")
                                                                        .npc("Remember, you can always use your", "quest book if you get lost.")
                                                                );
                                                            }),
                                                            new DialogueOption("No, I hate quests.", e -> e.getPA().closeAllWindows())
                                                    )
                                                    .exit(plr -> {
                                                                plr.getPA().closeAllWindows();
                                                            }
                                                    ));
                                        }else {
                                            player.sendMessage("@red@ You need a total level of 500 to start this quest!");
                                            player.getPA().closeAllWindows();
                                        }
                                    }
                                    ),
                                    new DialogueOption("No, I'm fine.", p -> p.getPA().closeAllWindows())
                            ));

                } else if (getStage() >= 1 && getStage() <= 5) {
                    player.start(getXerosGuide()
                            .npc(DialogueExpression.ANGER_1, "You can use your quest book as a reminder."));
                } else if (getStage() == 6) {
                    player.start(getXerosGuide()
                            .player(DialogueExpression.HAPPY,"I've completed your task!")
                            .npc(DialogueExpression.CALM, "Good Job! This next part will be a little harder.")
                            .player("What is it?")
                            .npc(DialogueExpression.DISTRESSED_2, "I need you to kill a hobgoblin and get back a weapon I lost.")
                            .npc(DialogueExpression.CALM_TALK, "It has a lot of value to me and", "I can reward you if you get it back.")
                            .player("Where can I find it?")
                            .npc(DialogueExpression.CALM_TALK, "Use the teleport platform to open ", "the teleport menu. Then go to 'Skilling Island'.")
                            .npc(DialogueExpression.CALM_TALK, "Once you kill the hobgoblin and get my sword", "come back and show me it.")
                            .exit(plr -> {
                                incrementStage();
                                plr.getPA().closeAllWindows();
                            }));
                } else if (getStage() == 7) {
                    player.start(getXerosGuide()
                            .player(DialogueExpression.HAPPY,"Hello!")
                            .npc(DialogueExpression.CALM, "Hello! Did you get lost?")
                            .player("Yes... What was I suppose to do?")
                            .npc(DialogueExpression.DISTRESSED_2, "I need you to kill a hobgoblin and get back a weapon I lost.")
                            .npc(DialogueExpression.CALM_TALK, "It has a lot of value to me and I can reward you if you get it back.", "I can reward you if you get it back.")
                            .player("Where can I find the hobgoblin?")
                            .npc(DialogueExpression.CALM_TALK, "Use any teleport in your spellbook to open ", "the teleport menu. Then go to 'Skilling Island'.")
                            .npc(DialogueExpression.CALM_TALK, "Once you kill the hobgoblin and get my sword", "come back and show me it.")
                            .exit(plr -> {
                                plr.getPA().closeAllWindows();
                            }));
                }  else if (getStage() == 8) {
                    player.start(getXerosGuide()
                            .player(DialogueExpression.HAPPY,"I defeated the hobgoblin and found your sword!")
                            .npc(DialogueExpression.HAPPY, "Amazing work " + player.getDisplayName() + "! Can you please go burn it now.")
                            .player(DialogueExpression.ANNOYED, "Why would we do that?")
                            .npc(DialogueExpression.CALM, "For the Exchange Points!", "Items can be destroyed for points.")
                            .npc(DialogueExpression.CALM, "These points can be used to buy pets with amazing perks!")
                            .player("Interesting.")
                            .npc(DialogueExpression.CALM_TALK, "Visit the fire by heading west of the slayer masters.", "Once there burn my sword and report back to me.")
                            .exit(plr -> {
                                incrementStage();
                                plr.getPA().closeAllWindows();
                            }));
                }  else if (getStage() == 9) {
                    player.start(getXerosGuide()
                            .player(DialogueExpression.HAPPY,"Hello!")
                            .npc(DialogueExpression.HAPPY, "Hello. Did you forget what to do?")
                            .player(DialogueExpression.HAPPY,"Please remind me.")
                            .npc(DialogueExpression.HAPPY, "Can you please go burn my sword.")
                            .player(DialogueExpression.ANNOYED, "Why would we do that?")
                            .npc(DialogueExpression.CALM, "For the Exchange Points! Items can be destroyed for points.")
                            .npc(DialogueExpression.CALM, "These points can be used to buy pets with amazing perks,", "and other items!")
                            .player("Interesting.")
                            .npc(DialogueExpression.CALM_TALK, "Visit the fire by heading west of the slayer masters.", "Once there burn my sword and report back to me.")
                            .exit(plr -> {
                                plr.getPA().closeAllWindows();
                            }));
                }  else if (getStage() == 10) {
                    player.start(getXerosGuide()
                            .player(DialogueExpression.HAPPY,"Your sword is now burned!")
                            .npc(DialogueExpression.HAPPY, "Thanks again, keep the points, also take this extra sword!")
                            .exit(plr -> {
                                plr.getPA().closeAllWindows();
                                incrementStage();
                                giveQuestCompletionRewards();
                            }));
                } else  if (getStage() == getCompletionStage()) {

                    player.start(getXerosGuide()
                            .npc("Hello, how can I help you?")
                            .option(new DialogueOption("How do I teleport?", p -> {
                                        player.start(getXerosGuide()
                                                .npc("You can use any highlighted teleport in the spellbook.")
                                                .exit(plr -> {
                                                            plr.getPA().closeAllWindows();
                                                        }
                                                ));
                                    }
                                    ),
                                    new DialogueOption("How do I vote?",  p -> {
                                        player.start(getXerosGuide()
                                                .npc("Use ::vote to open our vote page, and ::voted to claim them.")
                                                .exit(plr -> {
                                                            plr.getPA().closeAllWindows();
                                                        }
                                                ));
                                    }
                                    ),
                                    new DialogueOption("How do I open the drop table?",  p -> {
                                        player.start(getXerosGuide()
                                                .npc("Go to the quest tab and then the coin tab to find the drop table.")
                                                .exit(plr -> {
                                                            plr.getPA().closeAllWindows();
                                                        }
                                                ));
                                    }
                                    ),
                                    new DialogueOption("Do you know of any quest?",  p -> {
                                        player.start(getXerosGuide()
                                                .npc(DialogueExpression.HAPPY,"No, but come back another time.")
                                                .exit(plr -> {
                                                            plr.getPA().closeAllWindows();
                                                        }
                                                ));
                                    }
                                    ),
                                    new DialogueOption("No, I'm fine.", p -> p.getPA().closeAllWindows())
                            ));

                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void handleHelpTabActionButton(int button) {
        if (getStage() >= 1 && getStage() <= 5) {
            incrementStage();
        }
        return;
    }

    @Override
    public void exchangeItemForPoints(Player c) {
        if (getStage() == 9 && c.currentExchangeItem == 22316 ) {
            incrementStage();
        } else if (getStage() != 9 && c.currentExchangeItem == 22316){
            c.currentExchangeItem = -1;
            c.sendMessage("This item can only be burned once.");
        }
        return;
    }

    @Override
    public boolean handleObjectClick(WorldObject object, int option) {
        switch (object.getId()) {
        }

        return false;
    }

    @Override
    public boolean handleItemClick(int itemId) {
        switch (itemId) {

        }

        return false;
    }

    @Override
    public void handleNpcKilled(NPC npc) {
        if (Boundary.SKILLING_ISLAND.in(player) && npc.getNpcId() == Npcs.HOBGOBLIN_2241) {
            if (getStage() >= 7 && getStage() <=9){
                Server.itemHandler.createGroundItem(player, 22316, npc.getX(), npc.getY(), player.getHeight(), 1, player.getIndex());
                 if (getStage() == 7) {
                    player.start(new DialogueBuilder(player).player("I think the hobgoblin dropped a sword. I should take it."));
                    incrementStage();
                }
            }
        }
    }


    private DialogueBuilder getXerosGuide() {
        DialogueBuilder builder = new DialogueBuilder(player);
        builder.setNpcId(Npcs.MOUNTAIN_GUIDE);
        return builder;
    }
}
