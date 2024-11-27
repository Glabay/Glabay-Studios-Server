package io.xeros.content.questing.hftd;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueExpression;
import io.xeros.content.questing.Quest;
import io.xeros.content.skills.Skill;
import io.xeros.model.definition.Npcs;
import io.xeros.model.SkillLevel;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.ImmutableItem;

public class HftdQuest extends Quest {

    public static final int CASKET_TO_BUY_BOOK = 3849;
    private static final int AGILITY_REQUIREMENT = 35;
    private static final int SHOP_ID = 13;

    public HftdQuest(Player player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Horror From The Deep";
    }

    @Override
    public List<SkillLevel> getStartRequirements() {
        return Lists.newArrayList(new SkillLevel(Skill.AGILITY, AGILITY_REQUIREMENT));
    }

    @Override
    public List<String> getJournalText(int stage) {
        List<String> lines = Lists.newArrayList();
        switch (stage) {
            case 0:
                lines.add("To start this quest go to the minigames teleport and teleport");
                lines.add("to 'Horror From The Deep' then speak to Jossik.");
                lines.add("");
                lines.add("Rewards:");
                lines.add("Access to god books.");
                lines.add("Ability to kill the Dagannoth mother.");
                lines.add("");
                lines.addAll(getStartRequirementLines());
                break;
            case 1:
                lines.add("Jossik says there's a Dagannoth in his basement.");
                lines.add("I should go check it out.");
                break;
            case 2:
                lines.add("I've defeated the creature, I should talk to Jossik again.");
                break;
            case 3:
                lines.add("Jossik was happy I destroyed that creature that was disturbing him.");
                lines.add("Unfortunately there's still more of them down in the basement.");
                lines.add("He said he would trade any caskets I get from killing the creatures");
                lines.add("for his books.");
                break;
        }
        return lines;
    }

    @Override
    public int getCompletionStage() {
        return 3;
    }

    @Override
    public List<String> getCompletedRewardsList() {
        return Lists.newArrayList("Access to God Books.", "Ability to kill the Dagannoth mother.", "2 Experience Lamps.");
    }

    @Override
    public void giveQuestCompletionRewards() {
        player.getItems().addItemUnderAnyCircumstance(2528, 1);
        player.getItems().addItemUnderAnyCircumstance(2528, 1);
    }

    @Override
    public boolean handleNpcClick(NPC npc, int option) {
        if (npc.getNpcId() == Npcs.JOSSIK) {
            if (option == 1) {
                if (getStage() == 0) {
                    player.start(getJossikDialogue()
                            .player("Hello there.")
                            .npc(DialogueExpression.ANGER_1, "Oh, thank Armadyl! I am in such a worry...", "Please help me!")
                            .player("With what?")
                            .npc(DialogueExpression.ANGER_1, "There's a massive Dagannoth in my basement", "terrorizing me, go and destroy it!")
                            .exit(plr -> {
                                incrementStage();
                            }));
                } else if (getStage() == 1) {
                    player.start(getJossikDialogue()
                            .npc(DialogueExpression.ANGER_1, "There's no time to chat, go down the stairs and", "kill that damn thing!"));
                } else if (getStage() == 2) {
                    player.start(getJossikDialogue()
                            .player("I've defeated the creature!")
                            .npc(DialogueExpression.HAPPY, "Thank you so much, " + player.getDisplayName() + "!", "Unfortunately there's more beasts spawning down there.")
                            .player("The beast also dropped this weird casket..")
                            .npc(DialogueExpression.HAPPY, "I see, let me examine it.")
                            .itemStatement(CASKET_TO_BUY_BOOK, "You hand Jossik the casket.")
                            .npc(DialogueExpression.HAPPY, "Very strange indeed.", "I'll take it off your hands if you want.", "I have some books that might be of use to you.",
                                    "Let me know if your interested, and thanks again.")
                            .exit(plr -> {
                                incrementStage();
                                giveQuestCompletionRewards();
                            }));
                } else if (getStage() == getCompletionStage()) {
                    player.start(getJossikDialogue().npc(DialogueExpression.HAPPY, "Thanks for killing that damned thing.", "Unfortunately there's more spawning, let me know when",
                            "you kill more and I'll give you some books."));
                }
            } else if (option == 2) {
                if (isQuestCompleted()) {
                    player.getShops().openShop(SHOP_ID);
                } else {
                    if (getStage() == 1) {
                        player.start(getJossikDialogue().npc(DialogueExpression.ANGER_1, "Are you retarded? Kill that fucking Dagannoth!"));
                    } else {
                        player.start(getJossikDialogue().npc(DialogueExpression.ANGER_1, "I don't have time for this right now!"));
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
                new DagannothMotherInstance(player, !isQuestCompleted()).init();
                return true;
            case 4383:
                if (getStage() == 0) {
                    player.start(new DialogueBuilder(player).player("I can't just go climbing anywhere I want!", "Perhaps I should talk to Jossik first."));
                } else {
                    if (player.getLevel(Skill.AGILITY) < AGILITY_REQUIREMENT) {
                        player.sendMessage("This ladder is very high, you need 35 Agility to climb down.");
                    } else {
                        player.climbLadderTo(new Position(2519, 4618));
                    }
                }
                return true;
            case 4413:
                player.climbLadderTo(new Position(2515, 4629, 0));
                return true;
            case 4412:
                player.climbLadderTo(new Position(2510, 3644, 0));
                return true;
            case 4545:
            case 4546:
                if (player.getY() > 4626) {
                    player.moveTo(player.getPosition().withY(4626));
                } else {
                    player.moveTo(player.getPosition().withY(4627));
                }

                return true;
        }

        return false;
    }

    @Override
    public void handleNpcKilled(NPC npc) {
        if (Boundary.DAGANNOTH_MOTHER_HFTD.in(player) && Arrays.stream(DagannothMother.DAGANNOTH_MOTHER_TRANSFORMS).anyMatch(id -> npc.getNpcId() == id)) {
            Achievements.increase(player, AchievementType.SLAY_DAGGANOTH_MOTHER, 1);
            player.getInventory().addAnywhere(new ImmutableItem(CASKET_TO_BUY_BOOK, 1));
            player.sendMessage("You receive a casket from killing the beast.");
            if (getStage() == 1) {
                incrementStage();
                player.start(new DialogueBuilder(player).player("I've defeated the damned beast, I should return to Jossik."));
            }
        }
    }

    private DialogueBuilder getJossikDialogue() {
        DialogueBuilder builder = new DialogueBuilder(player);
        builder.setNpcId(Npcs.JOSSIK);
        return builder;
    }

}
