package io.xeros.content;

import io.xeros.content.questing.Quest;
import io.xeros.model.definition.Items;
import io.xeros.model.entity.player.Player;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CompletionistCape {

    private static final Requirement[] REQUIREMENTS = {
            new Requirement("Level 99 in all skills", plr -> plr.maxRequirements(plr)),
            new Requirement("All achievements", plr -> plr.getAchievements().hasCompletedAll()),
            new Requirement("All quests", plr -> plr.getQuesting().getQuestList().stream().allMatch(Quest::isQuestCompleted)),

            // Line break
            new Requirement("", null),

            kc("chambers of xeric", 100),
            kc("theatre of blood", 100),

            kc("the nightmare", 50),
            kc("corporeal beast", 50),
            kc("alchemical hydra", 50),

            kc("cerberus", 100),
            kc("zulrah", 100),
            kc("kraken", 100),
            kc("daggannoth kings", 100, "dagannoth supreme", "dagannoth prime", "dagannoth rex"),

            kc("general graardor", 100),
            kc("k'ril tsutsaroth", 100),
            kc("kree'arra", 100),
            kc("commander zilyana", 100),

    };

    public static void onLogin(Player player) {
        if (player.getItems().isWearingAnyItem(Items.COMPLETIONIST_CAPE) && !hasRequirements(player)) {
            player.sendMessage("@red@Completionist cape was unequipped as you no longer have the requirements.");
            player.playerEquipment[Player.playerCape] = -1;
            player.getItems().addItemUnderAnyCircumstance(Items.COMPLETIONIST_CAPE, 1);
        }
    }

    public static boolean hasRequirements(Player player) {
        return Arrays.stream(REQUIREMENTS).filter(it -> it.requirement != null).allMatch(it -> it.requirement.test(player));
    }

    public static void sendRequirementsInterface(Player player) {
        List<String> lines = Arrays.stream(REQUIREMENTS).map(it -> it.getFormattedText(player)).collect(Collectors.toList());
        player.getPA().openQuestInterface("Completionist Cape", lines);
    }

    private static Requirement kc(String name, int amount) {
        return kc(name, amount, name);
    }

    private static Requirement kc(String name, int amount, String...names) {
        String action = "Kill ";
        if (name.equals("theatre of blood") || name.equals("chambers of xeric")) {
            action = "Complete ";
        }
        return new Requirement(amount + " " + WordUtils.capitalize(name),
                plr -> Arrays.stream(names).mapToInt(it -> plr.getNpcDeathTracker().getKc(it)).sum() >= amount);
    }

    private static class Requirement {
        private final Predicate<Player> requirement;
        private final String requirementText;

        public Requirement(String requirementText, Predicate<Player> requirement) {
            this.requirement = requirement;
            this.requirementText = requirementText;
        }

        public String getFormattedText(Player player) {
            String text = requirementText;
            if (requirement != null && requirement.test(player)) {
                return "<trans=100><str=8268067>" + text + "</str>";
            }
            return text;
        }
    }
}
