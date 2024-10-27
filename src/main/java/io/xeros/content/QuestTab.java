package io.xeros.content;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.achievement.inter.TasksInterface;
import io.xeros.content.boosts.BoostType;
import io.xeros.content.boosts.Booster;
import io.xeros.content.boosts.Boosts;
import io.xeros.content.collection_log.CollectionLog;
import io.xeros.content.combat.stats.MonsterKillLog;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.item.lootable.LootableInterface;
import io.xeros.content.preset.PresetManager;
import io.xeros.content.worldevent.WorldEventContainer;
import io.xeros.content.worldevent.WorldEventInformation;
import io.xeros.model.Area;
import io.xeros.model.SquareArea;
import io.xeros.model.entity.npc.drops.DropManager;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;

public class QuestTab {

    public enum Tab {
        INFORMATION(50_417),
        COIN(50_419),
        DIARY(50_421),
        DONATOR(50_423)
        ;

        private final int buttonId;

        Tab(int buttonId) {
            this.buttonId = buttonId;
        }

        public int getConfigValue() {
            return ordinal();
        }
    }

    public enum CoinTab {
        COLLECTION_LOG,
        MONSTER_KILL_LOG,
        DROP_TABLE,
        LOOT_TABLES,
        WORLD_EVENTS,
        PRESETS,
        DONATOR_BENEFITS,
        TITLES,
        COMMUNITY_GUIDES,
        VOTE_PAGE,
        ONLINE_STORE,
        FORUMS,
        RULES,
        CALL_FOR_HELP
    }

    private static final int[] COIN_TAB_BUTTONS = {74107, 74112, 74117, 74122, 74127, 74132, 74137, 74142, 74147, 74152, 74157, 74162};

    public static final int INTERFACE_ID = 50414;
    private static final int CONFIG_ID = 1355;

    public static void updateAllQuestTabs() {
        Arrays.stream(PlayerHandler.players).forEach(player -> {
            if (player != null) {
                player.getQuestTab().updateInformationTab();
            }
        });
    }

    private final Player player;

    public QuestTab(Player player) {
        this.player = player;
    }

    private boolean sentDiaries = false;
    public void openTab(Tab tab) {
        if (!sentDiaries && tab == Tab.DIARY) {
            TasksInterface.sendAchievementsEntries(player);
            TasksInterface.sendDiaryEntries(player);
            sentDiaries = true;
        }
        player.getPA().sendConfig(CONFIG_ID, tab.getConfigValue());
    }

    public boolean handleActionButton(int buttonId) {
        for (Tab tab : Tab.values()) {
            if (buttonId == tab.buttonId) {
                openTab(tab);
                return true;
            }
        }

        return false;
    }

    public List<Integer> getLines() {
        return IntStream.range(51901, 51901 + 35).boxed().collect(Collectors.toList());
    }

    /**
     * Testiong View
     */
    public void updateInformationTab() {
        List<Integer> lines = getLines();
        int index = 0;

        // Server Information
        player.getPA().sendFrame126("@cr2@@or1@ Server Information", lines.get(index++));


        if (player.getRights().contains(Right.OWNER)) {
            player.getPA().sendFrame126("@or1@- Players: @gre@" + PlayerHandler.getPlayerCount() + " (u" + PlayerHandler.getUniquePlayerCount() + ") (m" + Configuration.PLAYERMODIFIER + ")", lines.get(index++));
        } else {
            player.getPA().sendFrame126("@or1@- Players online: @gre@" + PlayerHandler.getPlayerCount(), lines.get(index++));
        }
        player.getPA().sendFrame126("@or1@- Wilderness count: @gre@"+(Boundary.getPlayersInBoundary(Boundary.WILDERNESS) + Boundary.getPlayersInBoundary(Boundary.WILDERNESS_UNDERGROUND)), lines.get(index++));


        List<String> events = WorldEventContainer.getInstance().getWorldEventStatuses();
        for (String event : events) {
            player.getPA().sendFrame126("@or1@- " + event, lines.get(index++));
        }

        index = addBoostsInformation(lines, index);

        player.getPA().sendFrame126("@cr1@@or1@ Player Information", lines.get(index++));

        // Player Information
        player.getPA().sendFrame126("@or1@- Rank: @gre@" + player.getRights().buildCrownString() + " " + player.getRights().getPrimary().toString(), lines.get(index++));
        player.getPA().sendFrame126("@or1@- Total donated: @gre@$" + player.amDonated, lines.get(index++));
        player.getPA().sendFrame126("@or1@- Drop rate bonus: @gre@" + DropManager.getModifier1(player), lines.get(index++));

        // Time played
        long milliseconds = (long) player.playTime * 600;
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds - TimeUnit.DAYS.toMillis(days));
        String time = days + " days, " + hours + " hrs";
        player.getPA().sendFrame126("@or1@- Time Played: @gre@"+time, lines.get(index++));

        player.getPA().sendFrame126("@or1@- KDR: @gre@"+ (double)(player.deathcount == 0 ? player.killcount + player.deathcount : player.killcount/player.deathcount), lines.get(index++));

        if (player.getSlayer().getTask().isEmpty()) {
            player.getPA().sendFrame126("@or1@- Slayer Task: @gre@ None", lines.get(index++));
        } else {
            player.getPA().sendFrame126("@or1@- Slayer Task: @gre@" +player.getSlayer().getTaskAmount()+" "+player.getSlayer().getTask().get().getFormattedName()+"s", lines.get(index++));
        }

        points(player.donatorPoints, "Donator Points", lines.get(index++));
        points(player.votePoints, "Vote Points", lines.get(index++));
        points(player.pkp, "PK Points", lines.get(index++));
        points(player.bossPoints, "Boss Points", lines.get(index++));
        points(player.getSlayer().getPoints(), "Slayer Points", lines.get(index++));
        points(player.pcPoints, "Pest Control Points", lines.get(index++));
        points(player.exchangePoints, "Exchange Points", lines.get(index++));

        while (index < lines.size()) {
            player.getPA().sendString("", lines.get(index++));
        }
    }

    private void points(int points, String name, int lineId) {
        player.getPA().sendFrame126("@or1@- " + name + " [@gre@" + Misc.insertCommas(points) + "@or1@]", lineId);
    }

    private int addBoostsInformation(List<Integer> lines, int index) {
        List<? extends Booster<?>> boosts = Boosts.getBoostsOfType(player, null, BoostType.EXPERIENCE);
        if (!boosts.isEmpty()) {
            player.getPA().sendFrame126("<col=00c0ff> " + boosts.getFirst().getDescription(), lines.get(index++));
        }

        boosts = Boosts.getBoostsOfType(player, null, BoostType.GENERIC);
        for (Booster<?> boost : boosts) {
            player.getPA().sendFrame126("<col=00c0ff> " + boost.getDescription(), lines.get(index++));
        }

        return index;
    }

    /**
     * Handles all actions within the help tab
     */
    public boolean handleHelpTabActionButton(int button) {
        for (int index = 0; index < COIN_TAB_BUTTONS.length; index++) {
            if (button == COIN_TAB_BUTTONS[index]) {
                CoinTab coinTab = CoinTab.values()[index];
                player.getQuesting().handleHelpTabActionButton(button);
                switch(coinTab) {
                    case WORLD_EVENTS:
                        WorldEventInformation.openInformationInterface(player);
                        return true;
                    case COLLECTION_LOG:
                        CollectionLog group = player.getGroupIronmanCollectionLog();
                        if (group != null) {
                            new DialogueBuilder(player).option(
                                    new DialogueOption("Personal", plr -> player.getCollectionLog().openInterface(plr)),
                                    new DialogueOption("Group", group::openInterface)
                            ).send();
                            return true;
                        }

                        player.getCollectionLog().openInterface(player);
                        return true;
                    case MONSTER_KILL_LOG:
                        MonsterKillLog.openInterface(player);
                        return true;
                    case DROP_TABLE:
                        Server.getDropManager().openDefault(player);
                        return true;
                    case PRESETS:
                        Area[] areas = {
                            new SquareArea(3066, 3521, 3135, 3456),
                        };
                        if (Arrays.stream(areas).anyMatch(area -> area.inside(player))) {
                            PresetManager.getSingleton().open(player);
                			player.inPresets = true;
                        } else {
                            player.sendMessage("You must be in Edgeville to open presets.");
                        }
                        return true;
                    case DONATOR_BENEFITS:
                        player.getPA().sendFrame126(Configuration.DONATOR_BENEFITS_LINK, 12000);
                        return true;
                    case TITLES:
                        player.getTitles().display();
                        return true;
                    case COMMUNITY_GUIDES:
                        player.getPA().sendFrame126(Configuration.GUIDES_LINK, 12000);
                        return true;
                    case VOTE_PAGE:
                        player.getPA().sendFrame126(Configuration.VOTE_LINK, 12000);
                        return true;
                    case ONLINE_STORE:
                        player.getPA().sendFrame126(Configuration.STORE_LINK, 12000);
                        return true;
                    case FORUMS:
                        player.getPA().sendFrame126(Configuration.WEBSITE, 12000);
                        return true;
                    case RULES:
                        player.getPA().sendFrame126(Configuration.RULES_LINK, 12000);
                        return true;
                    case LOOT_TABLES:
                        LootableInterface.openInterface(player);
                        return true;
                    case CALL_FOR_HELP:
                        List<Player> staff = PlayerHandler.nonNullStream().filter(Objects::nonNull).filter(p -> p.getRights().isOrInherits(Right.HELPER))
                                .collect(Collectors.toList());
                        player.sendMessage("@red@You can also type ::help to report something.");
                        if (!staff.isEmpty()) {
                            String message = "@blu@[Help] " + player.getDisplayName()
                                    + " needs help, PM or TELEPORT and help them.";
                            Discord.writeServerSyncMessage(message);
                            PlayerHandler.sendMessage(message, staff);
                        } else {
                            player.sendMessage("@red@You've activated the help command but there are no staff-members online.");
                            player.sendMessage("@red@Please try contacting a staff on the forums and discord and they will respond ASAP.");
                            player.sendMessage("@red@You can also type ::help to report something.");
                        }
                        return true;
                    default:
                        return false;
                }
            }
        }

        return false;
    }
}
