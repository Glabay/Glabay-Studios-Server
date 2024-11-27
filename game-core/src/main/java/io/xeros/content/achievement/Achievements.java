package io.xeros.content.achievement;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import io.xeros.content.achievement.inter.TasksInterface;
import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.bosses.wildypursuit.TheUnbearable;
import io.xeros.model.definition.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ImmutableItem;
import io.xeros.util.Misc;
import org.apache.commons.lang3.text.WordUtils;

/**
 * @author Jason http://www.rune-server.org/members/jason
 * @date Mar 26, 2014
 */
public class Achievements {

    /**
     * README (WARNING)
     * You cannot change the enum names because they are used in the save files.
     * If you need to change the name of an achievement for display purposes then
     * set the first argument as a string.
     */
    public enum Achievement {

        Outlast("Outlast Noob", 0, AchievementTier.STARTER, AchievementType.TOURNAMENT, "Enter %d Outlast\\nTournaments", 1, 0, new GameItem(995, 500000)),
        Voter("I Voted", 1, AchievementTier.STARTER, AchievementType.VOTER, "Vote %d Time", 1, 0, new GameItem(995, 1_000_000)),
        Collector("Collector", 2, AchievementTier.STARTER, AchievementType.COLLECTOR, "Add %d item to your\\nCollection Log", 1, 0, new GameItem(21046, 2), new GameItem(995, 300000)),
        Hespori("Hello Hespori", 3, AchievementTier.STARTER, AchievementType.HESPORI, "Finish %d Hespori\\nEvent", 1, 0, new GameItem(21046, 2)),
        WOGW_Donation("The Giver", 4, AchievementTier.STARTER, AchievementType.WOGW, "Donate %d to\\nWell of Good Will", 2_000_000, 0, new GameItem(21046, 2)),
        Presets("Save a Preset", 5, AchievementTier.STARTER, AchievementType.PRESETS, "Save %d Preset", 1, 0, new GameItem(2841, 1)),
        The_Slayer("The Slayer", 6, AchievementTier.STARTER, AchievementType.SLAY, "Complete %d Slayer Tasks", 1, 1, new GameItem(7460, 1), new GameItem(405), new GameItem(7629)),
        Daily("Dailyscape", 7, AchievementTier.STARTER, AchievementType.DAILY, "Collect %d Daily Reward", 1, 1, new GameItem(Items.VOTE_CRYSTAL, 3), new GameItem(21046, 2)),
        Burner("Burn It", 8, AchievementTier.STARTER, AchievementType.FOE_POINTS, "Burn %d Exchange\\nPoints at FoE", 5000, 1, new GameItem(21046, 5)),

        /**
         * Tier 1 Achievement Start
         */
        PvMer_I("Mob Killer %d", 4, AchievementTier.TIER_1, AchievementType.SLAY_ANY_NPCS, "Kill %d NPCs", 1000, 1, new GameItem(995, 500000), new GameItem(989, 2)),
        DRAGON_SLAYER_I("Dragon Hunter %d", 3, AchievementTier.TIER_1, AchievementType.SLAY_DRAGONS, "Kill %d Dragons", 25, 1, new GameItem(995, 100000), new GameItem(537, 40), new GameItem(405)),
        Boss_Hunter_I(5, AchievementTier.TIER_1, AchievementType.SLAY_BOSSES, "Kill 100 Bosses", 100, 1, new GameItem(995, 250000), new GameItem(405)),
        Fishing_Task_I("Fishing %d", 6, AchievementTier.TIER_1, AchievementType.FISH, "Catch %d Fish", 1000, 1, new GameItem(995, 100000), new GameItem(13258, 1), new GameItem(13261, 1)),
        Cooking_Task_I("Cooking %d", 7, AchievementTier.TIER_1, AchievementType.COOK, "Cook %d Fish", 1000, 1, new GameItem(995, 50000), new GameItem(3145, 100)),
        Mining_Task_I("Mining %d", 8, AchievementTier.TIER_1, AchievementType.MINE, "Mine %d Rocks", 1000, 1, new GameItem(995, 100000), new GameItem(12013), new GameItem(12016)),
        Smithing_Task_I("Smithing %d", 9, AchievementTier.TIER_1, AchievementType.SMITH, "Smith %d Bars", 1000, 1, new GameItem(995, 350000), new GameItem(2360, 300)),
        Farming_Task_I("Farming %d", 10, AchievementTier.TIER_1, AchievementType.FARM, "Harvest %d Crops", 500, 1, new GameItem(995, 250000), new GameItem(13646), new GameItem(13644), new GameItem(Items.TORSTOL_SEED, 5), new GameItem(Items.SNAPDRAGON_SEED, 5), new GameItem(Items.RANARR_SEED, 5)),
        Herblore_Task_I("Herblore %d", 11, AchievementTier.TIER_1, AchievementType.HERB, "Create %d Potions", 500, 1, new GameItem(995, 100_000), new GameItem(Items.GRIMY_TORSTOL_NOTED, 10), new GameItem(Items.GRIMY_SNAPDRAGON_NOTED, 10), new GameItem(Items.GRIMY_RANARR_WEED_NOTED, 10)),
        Woodcutting_Task_I("Woodcutting %d", 12, AchievementTier.TIER_1, AchievementType.WOODCUT, "Cut %d Trees", 1000, 1, new GameItem(995, 100000), new GameItem(10941, 1), new GameItem(10933, 1)),
        Fletching_Task_I("Fletching %d", 13, AchievementTier.TIER_1, AchievementType.FLETCH, "Fletch %d Logs", 1000, 1, new GameItem(995, 100000), new GameItem(63, 150),  new GameItem(1778, 100)),
        Firemaking_Task_I("Firemaking %d", 14, AchievementTier.TIER_1, AchievementType.FIRE, "Light %d Logs", 500, 1, new GameItem(995, 10000), new GameItem(20710)),
        Theiving_Task_I("Thieving %d", 15, AchievementTier.TIER_1, AchievementType.THIEV, "Steal %d Times", 500, 1, new GameItem(995, 100000), new GameItem(5557), new GameItem(5556)),
        Agility_Task_I("Agility %d", 16, AchievementTier.TIER_1, AchievementType.AGIL, "Complete v Agility\\nCourse Laps", 100, 1, new GameItem(995, 50000), new GameItem(11849, 5), new GameItem(12792, 1)),
        Slayer_Task_I("Slayer %d", 17, AchievementTier.TIER_1, AchievementType.SLAY, "Complete %d Slayer Tasks", 50, 1, new GameItem(995, 450000), new GameItem(405, 2), new GameItem(7629, 2)),
        CKey_Task("Crystal Clear %d", 18, AchievementTier.TIER_1, AchievementType.LOOT_CRYSTAL_CHEST, "Loot Crystal Chest %d Times", 50, 1, new GameItem(995, 500000), new GameItem(990, 4)),
        Pc_Task("Pest Control %d", 19, AchievementTier.TIER_1, AchievementType.PEST_CONTROL_ROUNDS, "Complete Pest Control\\n%d Times", 50, 1, new GameItem(995, 500000), new GameItem(405), new GameItem(8841)),
        Jad_Task("Fight Caves %d", 20, AchievementTier.TIER_1, AchievementType.FIGHT_CAVES_ROUNDS, "Complete the Fight Caves", 1, 1, new GameItem(995, 500_000), new GameItem(405)),
        Barrows_Task_I("Barrows %d", 21, AchievementTier.TIER_1, AchievementType.BARROWS_KILLS, "Kill %d barrows npcs", 100, 1, new GameItem(995, 150_000), new GameItem(405)),
        ClueScroll_Task("Treasure Trails %d", 22, AchievementTier.TIER_1, AchievementType.CLUES, "Loot %d Clue Caskets", 50, 1, new GameItem(995, 250_000)),
        NEWB_VOTER("Democracy %d", 23, AchievementTier.TIER_1, AchievementType.VOTE_CHEST_UNLOCK, "Open %d Vote Chest", 1, 1, new GameItem(995, 500_000), new GameItem(23933, 5)),
        COMPETITOR(25, AchievementTier.TIER_1, AchievementType.TOURNAMENT, "Enter %d Outlast\\nTournaments", 5, 1, new GameItem(989, 1), new GameItem(11739, 1)),

        /**
         * Tier 2 Achievement Start
         */
        PvMer_II("Mob Killer %d", 2, AchievementTier.TIER_2, AchievementType.SLAY_ANY_NPCS, "Kill %d NPCs", 3000, 2, new GameItem(995, 1000000), new GameItem(405)),
        Dragon_Hunter_II("Dragon Hunter %d", 1, AchievementTier.TIER_2, AchievementType.SLAY_DRAGONS, "Kill %d Dragons", 350, 2, new GameItem(995, 500000), new GameItem(537, 60), new GameItem(405)),
        Boss_Hunter_II(3, AchievementTier.TIER_2, AchievementType.SLAY_BOSSES, "Kill %d Bosses", 700, 2, new GameItem(995, 1000000), new GameItem(405, 2)),
        INTERMEDIATE_FISHER("Fishing %d", 4, AchievementTier.TIER_2, AchievementType.FISH, "Catch %d Fish", 2500, 2, new GameItem(995, 500000), new GameItem(13259, 1)),
        INTERMEDIATE_CHEF("Cooking %d", 5, AchievementTier.TIER_2, AchievementType.COOK, "Cook %d Fish", 2500, 2, new GameItem(995, 350000), new GameItem(13441, 75)),
        INTERMEDIATE_MINER("Mining %d", 6, AchievementTier.TIER_2, AchievementType.MINE, "Mine %d Rocks", 2500, 2, new GameItem(995, 400000), new GameItem(12015)),
        INTERMEDIATE_SMITH("Smithing %d", 7, AchievementTier.TIER_2, AchievementType.SMITH, "Smelt or Smith %d Bars", 2500, 2, new GameItem(995, 1250000), new GameItem(2362, 300)),
        INTERMEDIATE_FARMER("Farming %d", 8, AchievementTier.TIER_2, AchievementType.FARM, "Harvest %d Crops", 2500, 2, new GameItem(995, 500_000), new GameItem(13640), new GameItem(Items.TORSTOL_SEED, 15), new GameItem(Items.SNAPDRAGON_SEED, 15), new GameItem(Items.RANARR_SEED, 15)),
        INTERMEDIATE_MIXER("Herblore %d", 9, AchievementTier.TIER_2, AchievementType.HERB, "Create %d Potions", 1000, 2, new GameItem(995, 300_000), new GameItem(Items.GRIMY_TORSTOL_NOTED, 40), new GameItem(Items.GRIMY_SNAPDRAGON_NOTED, 40), new GameItem(Items.GRIMY_RANARR_WEED_NOTED, 40)),
        INTERMEDIATE_CHOPPER("Woodcutting %d", 10, AchievementTier.TIER_2, AchievementType.WOODCUT, "Cut %d Trees", 2500, 2, new GameItem(10939, 1), new GameItem(995, 300_000)),
        INTERMEDIATE_FLETCHER("Fletching %d", 11, AchievementTier.TIER_2, AchievementType.FLETCH, "Fletch %d Logs", 2500, 2, new GameItem(995, 500000), new GameItem(67, 225)),
        INTERMEDIATE_PYRO("Firemaking %d", 12, AchievementTier.TIER_2, AchievementType.FIRE, "Light %d Logs", 1000, 2, new GameItem(995, 500000), new GameItem(20706), new GameItem(20704)),
        INTERMEDIATE_THIEF("Thieving %d", 13, AchievementTier.TIER_2, AchievementType.THIEV, "Steal %d Times", 1000, 2, new GameItem(995, 450000), new GameItem(5555), new GameItem(5553)),
        INTERMEDIATE_RUNNER("Agility %d", 14, AchievementTier.TIER_2, AchievementType.AGIL, "Complete %d Agility\\nCourse Laps", 250, 2, new GameItem(995, 400000), new GameItem(989, 2)),
        SLAYER_DESTROYER("Slayer %d", 15, AchievementTier.TIER_2, AchievementType.SLAY, "Complete %d Slayer Tasks", 80, 2, new GameItem(995, 1500000), new GameItem(405), new GameItem(7629, 2)),
        CHEST_LOOTER("Crystal Clear %d", 16, AchievementTier.TIER_2, AchievementType.LOOT_CRYSTAL_CHEST, "Loot Crystal Chest %d Times", 100, 2, new GameItem(995, 2000000), new GameItem(990, 8)),
        RED_OF_FURY("Fight Caves %d", 18, AchievementTier.TIER_2, AchievementType.FIGHT_CAVES_ROUNDS, "Complete Fight Caves %d Times", 5, 2, new GameItem(995, 500000), new GameItem(405), new GameItem(6570, 2)),
        CLUE_SCROLLER("Treasure Trails %d", 20, AchievementTier.TIER_2, AchievementType.CLUES, "Loot %d Clue Caskets", 120, 2, new GameItem(995, 1700000)),
        ADVANCED_VOTER("Democracy %d", 22, AchievementTier.TIER_2, AchievementType.VOTE_CHEST_UNLOCK, "Open %d Vote Chests", 2, 2, new GameItem(6199, 1), new GameItem(11739, 1)),
        /**
         * Tier 3 Achievement Start
         */
        SLAUGHTERER("Mob Killer %d", 2, AchievementTier.TIER_3, AchievementType.SLAY_ANY_NPCS, "Kill %d NPCs", 10000, 3, new GameItem(995, 2000000), new GameItem(405, 1)),
        EXPERT_DRAGON_SLAYER("Dragon Hunter %d", 1, AchievementTier.TIER_3, AchievementType.SLAY_DRAGONS, "Kill %d Dragons", 950, 3, new GameItem(995, 1500000), new GameItem(405), new GameItem(537, 150)),
        BOSS_SLAUGHTERER("Boss Hunter %d", 3, AchievementTier.TIER_3, AchievementType.SLAY_BOSSES, "Kill %d Bosses", 1500, 3, new GameItem(995, 3000000), new GameItem(405, 6)),
        EXPERT_FISHER("Fishing %d", 4, AchievementTier.TIER_3, AchievementType.FISH, "Catch %d Fish", 5000, 3, new GameItem(995, 1000000), new GameItem(13260, 1)),
        EXPERT_CHEF("Cooking %d", 5, AchievementTier.TIER_3, AchievementType.COOK, "Cook %d Fish", 5000, 3, new GameItem(995, 1000000), new GameItem(13441, 350)),
        EXPERT_MINER("Mining %d", 6, AchievementTier.TIER_3, AchievementType.MINE, "Mine %d Rocks", 5000, 3, new GameItem(995, 2500000), new GameItem(12014)),
        EXPERT_SMITH("Smithing %d", 7, AchievementTier.TIER_3, AchievementType.SMITH, "Smelt or Smith %d Bars", 5000, 3, new GameItem(995, 1000000), new GameItem(2364, 300)),
        EXPERT_FARMER("Farming %d", 8, AchievementTier.TIER_3, AchievementType.FARM, "Harvest %d Crops", 5000, 3, new GameItem(995, 2500000), new GameItem(Items.MAGIC_SECATEURS), new GameItem(13642), new GameItem(Items.TORSTOL_SEED, 35), new GameItem(Items.SNAPDRAGON_SEED, 35), new GameItem(Items.RANARR_SEED, 35)),
        EXPERT_MIXER("Herblore %d", 9, AchievementTier.TIER_3, AchievementType.HERB, "Create %d Potions", 2500, 3, new GameItem(995, 1000000), new GameItem(Items.GRIMY_TORSTOL_NOTED, 125), new GameItem(Items.GRIMY_SNAPDRAGON_NOTED, 125), new GameItem(Items.GRIMY_RANARR_WEED_NOTED, 125)),
        EXPERT_CHOPPER("Woodcutting %d", 10, AchievementTier.TIER_3, AchievementType.WOODCUT, "Cut %d Trees", 5000, 3, new GameItem(995, 2000000), new GameItem(10940, 1)),
        EXPERT_FLETCHER("Fletching %d", 11, AchievementTier.TIER_3, AchievementType.FLETCH, "Fletch %d Logs", 5000, 3, new GameItem(995, 1000000), new GameItem(71, 750),  new GameItem(1778, 200)),
        EXPERT_PYRO("Firemaking %d", 12, AchievementTier.TIER_3, AchievementType.FIRE, "Light %d Logs", 2500, 3, new GameItem(995, 1000000), new GameItem(20708), new GameItem(20712)),
        EXPERT_THIEF("Thieving %d", 13, AchievementTier.TIER_3, AchievementType.THIEV, "Steal %d Times", 3000, 3, new GameItem(995, 1000000), new GameItem(5554)),
        EXPERT_RUNNER("Agility %d", 14, AchievementTier.TIER_3, AchievementType.ROOFTOP, "Complete %d Rooftop Agility\\nCourse Laps", 300, 3, new GameItem(995, 2000000), new GameItem(989, 3)),
        SLAYER_EXPERT("Slayer %d", 15, AchievementTier.TIER_3, AchievementType.SLAY, "Complete %d Slayer Tasks", 150, 3, new GameItem(995, 5000000), new GameItem(13438, 1)),
        DIG_FOR_GOLD("Crystal Clear %d", 16, AchievementTier.TIER_3, AchievementType.LOOT_CRYSTAL_CHEST, "Loot Crystal Chest %d Times", 200, 3, new GameItem(995, 3500000), new GameItem(990, 14)),
        TZHAAR("Fight Caves %d", 18, AchievementTier.TIER_3, AchievementType.FIGHT_CAVES_ROUNDS, "Complete Fight Caves %d Times", 10, 3, new GameItem(995, 2500000), new GameItem(6570, 3), new GameItem(405)),
        BARROWS_GOD("Barrows %d", 19, AchievementTier.TIER_3, AchievementType.BARROWS_KILLS, "Kill %d npcs at barrows", 750, 3, new GameItem(995, 10000000), new GameItem(405, 3)),
        CLUE_CHAMP("Treasure Trails %d", 21, AchievementTier.TIER_3, AchievementType.CLUES, "Loot %d Clue Caskets", 180, 3, new GameItem(995, 2000000), new GameItem(20164), new GameItem(6666)),
        EXTREME_VOTER("Democracy %d", 23, AchievementTier.TIER_3, AchievementType.VOTE_CHEST_UNLOCK, "Open %d Vote Chests", 5, 3, new GameItem(995, 10000000), new GameItem(11739, 2), new GameItem(6199, 2)),
        /**
         * Tier 4 Achievement Start
         */
        HESPORI( 2, AchievementTier.TIER_4, AchievementType.HESPORI, "Finish %d Hespori\\nEvents", 20, 4, new GameItem(22374, 8), new GameItem(995, 10000000)),

        /**
         * Removed because the boss was really broken. I would avoid using the 4 identification from now on.
         * If we need to add this achievement back set it above the other achievements (and don't forget to update it with players kc on login).
         */
        //GROTESQUE_GUARDIANS("Grotesque",  4, AchievementTier.TIER_4, AchievementType.GROTESQUES, "Kill %d Grotesques\\nGuardians", 100, 4, new GameItem(6828, 1), new GameItem(995, 10000000)),

        THE_NIGHTMARE( 5, AchievementTier.TIER_4, AchievementType.NIGHTMARE, "Kill %d Nightmare", 100, 4, new GameItem(6828, 1), new GameItem(995, 10000000)),
        ALCHEMICAL_HYDRA( 6, AchievementTier.TIER_4, AchievementType.HYDRA, "Kill %d Hydra", 100, 4, new GameItem(6828, 1), new GameItem(995, 10000000)),
        MIMIC( 8, AchievementTier.TIER_4, AchievementType.MIMIC, "Kill %d Mimics", 20, 4, new GameItem(6199, 1), new GameItem(995, 10000000)),
        HUNLLEF( 9, AchievementTier.TIER_4, AchievementType.HUNLLEF, "Kill %d Hunllefs", 25, 4, new GameItem(23776, 1), new GameItem(995, 10000000)),
        TOB_CHAMPION( 0, AchievementTier.TIER_4, AchievementType.TOB, "Complete %d Theatre\\nof Blood runs (Raids 2)", 50, 4, new GameItem(13346, 1), new GameItem(995, 10_000_000)),
        COX_CHAMPION( 1, AchievementTier.TIER_4, AchievementType.COX, "Complete %d Chamber\\nof Xeric runs (Raids 1)", 100, 4, new GameItem(6828, 1), new GameItem(995, 10000000)),
        FIRE_OF_EXCHANGE(  7, AchievementTier.TIER_4, AchievementType.FOE_POINTS, "Burn %d Exchange\\nPoints", 150000, 4, new GameItem(30010, 1), new GameItem(995, 10000000)),
        WILDY_EVENT( 3, AchievementTier.TIER_4, AchievementType.WILDY_EVENT, "Finish %d\\nWildy Events", 15, 4, new GameItem(TheUnbearable.KEY, 4), new GameItem(FragmentOfSeren.KEY, 1), new GameItem(995, 10000000)),
        MAGE_ARENA_II( 10, AchievementTier.TIER_4, AchievementType.MAGE_ARENA_II, "Complete %d\\nMage Arena II", 1, 4, new GameItem(2528, 4), new GameItem(995, 10000000)),
        MAX( 11, AchievementTier.TIER_4, AchievementType.MAX, "Achieve level 99 in\\nall skills", 1, 4, new GameItem(13346, 1), new GameItem(691, 1), new GameItem(995, 10000000));

        private String formattedName;
        private final AchievementTier tier;
        private final AchievementType type;
        private final String description;
        private final int amount;
        private final int identification;
        private final int points;
        private final GameItem[] rewards;

        Achievement(int identification, AchievementTier tier, AchievementType type,
                    String description, int amount, int points, GameItem... rewards) {
            this(null, identification, tier, type, description, amount, points, rewards);
        }

        Achievement(String formattedName, int identification, AchievementTier tier, AchievementType type,
                    String description, int amount, int points, GameItem... rewards) {
            this.formattedName = formattedName == null ? null : formattedName.replace("%d", tier.getTierText());
            this.identification = identification;
            this.tier = tier;
            this.type = type;
            this.description = description.replace("%d", Misc.insertCommas(amount));
            this.amount = amount;
            this.points = points;
            this.rewards = rewards;

            //format the items
            for (GameItem b : rewards) if (b.amount() == 0) b.setAmount(1);
        }

        @Override
        public String toString() {
            return "Achievement{" +
                    "formattedName='" + formattedName + '\'' +
                    ", tier=" + tier +
                    ", type=" + type +
                    ", description='" + description + '\'' +
                    ", amount=" + amount +
                    ", identification=" + identification +
                    ", points=" + points +
                    ", rewards=" + Arrays.toString(rewards) +
                    '}';
        }

        static {
            for (Achievement a : Achievement.values()) {
                for (Achievement b : Achievement.values()) {
                    if (a != b && a.getId() == b.getId() && a.getTier() == b.getTier()) {
                        throw new IllegalStateException(String.format("Achievements: %s and %s share the same id.", a.name(), b.name()));
                    }
                }
            }
        }

        public String getFormattedName() {
            if (formattedName == null) {
                formattedName = WordUtils.capitalize(name().toLowerCase().replace("_", " "))
                        .replace("Ii", "II")
                        .replace("Iii", "III")
                        .replace("Iv", "IV");
            }

            return formattedName;
        }

        public int getId() {
            return identification;
        }

        public AchievementTier getTier() {
            return tier;
        }

        public AchievementType getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public int getAmount() {
            return amount;
        }

        public int getPoints() {
            return points;
        }

        public GameItem[] getRewards() {
            return rewards;
        }

        public static final Set<Achievement> ACHIEVEMENTS = EnumSet.allOf(Achievement.class);
    }

    public static void increase(Player player, AchievementType type, int amount) {
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (achievement.getType() == type) {
                int currentAmount = player.getAchievements().getAmountRemaining(achievement.getTier().getId(), achievement.getId());
                int tier = achievement.getTier().getId();
                if (currentAmount < achievement.getAmount() && !player.getAchievements().isComplete(achievement.getTier().getId(), achievement.getId())) {
                    player.getAchievements().setAmountRemaining(tier, achievement.getId(), currentAmount + amount);
                    if ((currentAmount + amount) >= achievement.getAmount()) {
                        player.getAchievements().setAmountRemaining(tier, achievement.getId(), achievement.getAmount()); // Set to max amount in case they went over
                        player.getAchievements().setComplete(tier, achievement.getId(), true);
                        player.getAchievements().setPoints(achievement.getPoints() + player.getAchievements().getPoints());
                        player.sendMessage(Misc.colorWrap(AchievementHandler.COLOR, "<clan=6>You've completed the " + achievement.getTier().getName().toLowerCase()
                            + " achievement '" + achievement.getFormattedName() + "'!"));

                        if (player.getAchievements().hasCompletedAll()) {
                            PlayerHandler.executeGlobalStaffMessage(Misc.colorWrap(AchievementHandler.COLOR,
                                    "<clan=6> " + player.getDisplayNameFormatted() + " has completed all achievements!"));
                        }
                    }

                    updateProgress(player, type);
                }
            }
        }
    }

    private static void updateProgress(Player player, AchievementType type) {
        for (Achievement achievement : Achievement.values()) {
            if (achievement.getType() == type) {
                TasksInterface.updateProgress(player, "achievements", achievement);
            }
        }
    }

    public static void addReward(Player player, Achievement achievement) {
        for (GameItem item : achievement.getRewards()) {
            player.getInventory().addAnywhere(new ImmutableItem(item.id(), item.amount()));
        }
    }

    public static void reset(Player player, AchievementType type) {
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (achievement.getType() == type) {
                if (!player.getAchievements().isComplete(achievement.getTier().getId(), achievement.getId())) {
                    player.getAchievements().setAmountRemaining(achievement.getTier().getId(), achievement.getId(),
                            0);
                }
            }
        }
    }

    public static int getMaximumAchievements() {
        return Achievement.ACHIEVEMENTS.size();
    }
}
