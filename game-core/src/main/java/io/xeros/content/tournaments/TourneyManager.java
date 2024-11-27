package io.xeros.content.tournaments;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.content.QuestTab;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.melee.CombatPrayer;
import io.xeros.content.combat.melee.MeleeData;
import io.xeros.content.events.eventcalendar.EventChallenge;
import io.xeros.content.skills.Skill;
import io.xeros.content.leaderboards.LeaderboardType;
import io.xeros.content.leaderboards.LeaderboardUtils;
import io.xeros.content.worldevent.WorldEventContainer;
import io.xeros.content.worldevent.impl.TournamentWorldEvent;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.*;
import io.xeros.model.items.GameItem;
import io.xeros.sql.outlast.OutlastLeaderboardAdd;
import io.xeros.sql.outlast.OutlastRecentWinnersAdd;
import io.xeros.util.Misc;

/**
 * @author Grant_ | www.rune-server.ee/members/grant_ | 10/3/19
 * A tournament system that allows players to join a lobby and fight until there is only one player left.
 * Top 3 are rewarded.
 */
public class TourneyManager {

    private static volatile TourneyManager singleton;
    private static final int FOG_DURATION_TICKS = Misc.toCycles(4, TimeUnit.MINUTES) + Misc.toCycles(30, TimeUnit.SECONDS);
    private static final int DAMAGE_FROM_FOG_DEFAULT = 0;

    private final ArrayList<String> currentPlayers = new ArrayList<>();
    private List<Player> possibleTargets = Lists.newCopyOnWriteArrayList();
    private final ArrayList<String> victors = new ArrayList<>();

    public static String WINNER = "";
    private static final Object ARENA_TASK_OBJECT = new Object();
    private static final int ARENA_X = 3290, ARENA_Y = 4958;
    private static final int LOBBY_TICK_INTERVAL = 1;
    private static int _secondsUntilLobbyEnds;
    private static int _playersToStart;

    public static final String[] tourneyOrder = {"Dharok", "Rune Melee", "Pure", "Vesta", "Range AGS", "Monk Robes", "Dharok", "Rune Melee", "Pure", "Vesta", "Monk Robes", "Range AGS", "Dharok", "NH"};
    private final Object timerObject = new Object();
    private final int[] itemsToRemove = {157, 159, 161, 145, 147, 149, 6687, 6689, 6691, 3026, 3028, 229, 3030, 3024, 12695, 12697, 12699, 12701, 3024, 6685, 229, 229, 229, 229, 385, 3144};
    private boolean lobbyOpen;
    private boolean arenaActive;
    private int amountOfPlayers;
    private int tournamentIndex;
    private int secondsUntilLobbyEnds = _secondsUntilLobbyEnds;
    private HashMap<String, TourneySetup> tourneySetups;
    private TourneySetup currentSetup;

    private static final int JOIN_INTERFACE_ID = 270;

    public static void initialiseSingleton() {
        if (!Server.isDebug()) {
            _secondsUntilLobbyEnds = 3 * 60;
            _playersToStart = 2;
        } else {
            _secondsUntilLobbyEnds = 10;
            _playersToStart = 1;
        }
        singleton = new TourneyManager();
    }

    public static TourneyManager getSingleton() {
        return singleton;
    }

    private TourneyManager() {
        if (singleton != null) {
            throw new RuntimeException("Use getSingleton() method to get the single instance of this class.");
        }
    }

    public void init() {
        try {
            Path path = Paths.get(Server.getDataDirectory() + "/cfg/tournament/default_tourney.json");
            File file = path.toFile();
            JsonParser parser = new JsonParser();
            if (!file.exists()) {
                return;
            }
            Object obj = parser.parse(new FileReader(file));
            JsonObject jsonUpdates = (JsonObject) obj;
            Type listType = new TypeToken<HashMap<String, TourneySetup>>() {
            }.getType();
            tourneySetups = new Gson().fromJson(jsonUpdates, listType);
            System.out.println("Loaded " + tourneySetups.size() + " default tourney setups.");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.out.println("No default presets found!");
            tourneySetups = new HashMap<>();
        }
    }

    public boolean setNextTourneyType(String type) {
        for (int index = 0; index < tourneyOrder.length; index++) {
            if (tourneyOrder[index].equalsIgnoreCase(type)) {
                tournamentIndex = (index == 0 ? tourneyOrder.length - 1 : index - 1);
                return true;
            }
        }
        return false;
    }

    public void openLobby() {
        initializeLobbyTimer();
        tournamentIndex = (tournamentIndex + 1) % tourneyOrder.length;
        currentSetup = tourneySetups.get(tourneyOrder[tournamentIndex]);
    }

    public String getTournamentType() {
        return tourneyOrder[tournamentIndex];
    }

    private boolean clearCurrentArena(Player player) {
        if (isArenaActive()) {
            if (Boundary.getPlayersInBoundary(Boundary.LUMBRIDGE_OUTLAST_AREA) == 0) {
                player.sendMessage("Killing arena, 0 entites found inside outlast but outlast @red@WAS@bla@ still active.");
                victors.clear();
                arenaActive = false;
                return true;
            } else {
                player.sendMessage("The arena is already active.");
                return false;
            }
        } else {
            return true;
        }
    }

    private void clearActiveLobby() {
        if (isLobbyOpen()) {
            lobbyOpen = false;
            ArrayList<Player> toLeave = Lists.newArrayList();
            for (String name : currentPlayers) {
                Player p = PlayerHandler.getPlayerByLoginName(name);
                if (p != null) {
                    toLeave.add(p);
                }
            }
            for (Player p : toLeave) {
                if (p != null) {
                    leaveLobby(p, false);
                }
            }
            toLeave.clear();
            currentPlayers.clear();
        }
    }

    public void endGame() {
        getActivePlayers().forEach(it -> leaveLobby(it, false));
        currentPlayers.clear();
        possibleTargets.clear();
        victors.clear();
        arenaActive = false;
        CycleEventHandler.getSingleton().stopEvents(ARENA_TASK_OBJECT);
        lobbyOpen = false;
    }

    public static boolean handleObjects(Player player, WorldObject object, int optionId) {
        if (object == null || object.getId() != 31622)
            return false;

        switch (optionId) {

            case 1:
                if (!TourneyManager.getSingleton().isLobbyOpen()) {
                    player.sendMessage("The tournament lobby is not currently open.");
                    return true;
                }
                player.getPA().showInterface(JOIN_INTERFACE_ID);
                player.objectDistance = 1;
                return true;

            case 2:
                int players = TourneyManager.getSingleton().getPlayers();
                String msg = TourneyManager.getSingleton().isLobbyOpen() ? "There are currently " + players + " in the lobby." : TourneyManager.getSingleton().isArenaActive() ? "There is " + players + " remaining in the fight!" : "The Outlast event hasn't started yet. It will begin in another: " + TourneyManager.getSingleton().getTimeRemaining();
                player.sendMessage(msg);
                return true;

            case 3:
                player.sendMessage(TourneyManager.getSingleton().isLobbyOpen() ? "The tournament lobby is open and will start shortly." : TourneyManager.getSingleton().isArenaActive() ? "The Outlast tournament has already begun." : "The next Outlast tournament will begin in: " + TourneyManager.getSingleton().getTimeRemaining());
                return true;
        }
        return false;
    }

    public boolean canEnter() {
        return secondsUntilLobbyEnds <= 5;
    }

    private void initializeLobbyTimer() {
        // Initalize the variables to control the lobby
        clearActiveLobby();
        CycleEventHandler.getSingleton().stopEvents(timerObject);
        secondsUntilLobbyEnds = _secondsUntilLobbyEnds;
        lobbyOpen = true;
        QuestTab.updateAllQuestTabs();
        // Start lobby timer
        CycleEventHandler.getSingleton().addEvent(timerObject, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                int remaining = secondsUntilLobbyEnds < 1 ? 0 : (secondsUntilLobbyEnds -= LOBBY_TICK_INTERVAL);
                if (currentPlayers.size() < _playersToStart) {
                    secondsUntilLobbyEnds = _secondsUntilLobbyEnds;
                } else if (remaining <= 0) {
                    beginTournament();
                    container.stop();
                }
                updateInterface(true);
                QuestTab.updateAllQuestTabs();
            }
        }, 2);
    }

    private void checkPlayerList() {
        currentPlayers.stream().filter(Objects::nonNull).forEach(p -> {
            Player plr = PlayerHandler.getPlayerByLoginName(p);
            possibleTargets.add(plr);
        });
        Collections.shuffle(possibleTargets);
    }

    private boolean isFinal() {
        return currentPlayers.size() == 2;
    }

    private boolean hasTarget(Player player) {
        return player.tournamentTarget != null;
    }

    public void findTarget() {
        //If more than 1 person looking for a target
        if (possibleTargets.size() > 1) {
            //loops the target list
            for (Player p : possibleTargets) {
                //if player is NOT null
                if (p != null) {
                    //if player is dead or NOT in bounds then skip
                    if (p.isDead() || !isInArenaBounds(p) || p.spectatingTournament) {
                        possibleTargets.remove(p);
                        continue;
                    }
                    //IF the you DO NOT have a target and your cooldown is expired (10 seconds upon kill)
                    if (p.tournamentTarget == null && p.tournamentTargetCooldown < System.currentTimeMillis() && isInArenaBounds(p)) {
                        //Gets all players in the list starting with SMFH
                        Player target = possibleTargets.get(Misc.random(possibleTargets.size() - 1));//mfer
                        if (p != target && p.getIndex() != target.getIndex() && target.tournamentTarget == null && target.tournamentTargetCooldown < System.currentTimeMillis() && isInArenaBounds(target)) {
                            possibleTargets.remove(p);
                            possibleTargets.remove(target);

                            p.tournamentTarget = target;
                            resetFogTimers(p);

                            target.tournamentTarget = p;
                            resetFogTimers(target);

                            p.sendMessage(target.getDisplayName() + " has been assigned to you as a target! "+(isFinal() ? "This is the finals! good luck!" : ""));
                            p.getPA().displayPlayerHintIcon(target.getIndex());
                            target.sendMessage(p.getDisplayName() + " has been assigned to you as a target!"+(isFinal() ? "This is the finals! good luck!" : ""));
                            target.getPA().displayPlayerHintIcon(p.getIndex());
                        }
                    }
                }
            }
        }
    }

    /**
     * Begins the tournament, granted there are more than 1 person in the lobby
     * <p>
     * Stats are set here not in the lobby
     */
    private void beginTournament() {
        amountOfPlayers = currentPlayers.size();
        ArrayList<String> toRemove = Lists.newArrayList();
        possibleTargets = Lists.newCopyOnWriteArrayList();

        for (String p : currentPlayers) {
            Player player = PlayerHandler.getPlayerByLoginName(p);
            if (player == null) {
                toRemove.add(p);
                continue;
            }
            if (!isInLobbyBounds(player)) {
                player.sendMessage("You will no longer compete in the tournament, as you were");
                player.sendMessage("not in the lobby when it began.");
                toRemove.add(p);
                continue;
            }
            if (player.hasOverloadBoost) {
                player.getPotions().resetOverload();
            }
            player.sendMessage("Welcome to the Tournament. Goodluck!");
            player.getPA().movePlayer(new Coordinate(ARENA_X + Misc.random(6), ARENA_Y + Misc.random(6)));
        }

        for (String r : toRemove) {
            PlayerHandler.getOptionalPlayerByLoginName(r).ifPresent(plr -> leaveLobby(plr, false));
            currentPlayers.remove(r);
        }

        if (currentPlayers.size() == 1 && !Server.isDebug()) {
            handleDeath(currentPlayers.get(0), false);
            return;
        } else if (currentPlayers.size() == 0) {
            handleVictors();
            return;
        }
        arenaActive = true;
        lobbyOpen = false;
        QuestTab.updateAllQuestTabs();
        // End arena tasks
        CycleEventHandler.getSingleton().stopEvents(ARENA_TASK_OBJECT);
        checkPlayerList();
        CycleEventHandler.getSingleton().addEvent(ARENA_TASK_OBJECT, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (currentPlayers.isEmpty() || Boundary.getPlayersInBoundary(Boundary.OUTLAST) == 0) {
                    endGame();
                    container.stop();
                }
            }
        }, 10);

        CycleEventHandler.getSingleton().addEvent(ARENA_TASK_OBJECT, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {

                findTarget();
                tickFog();
                updateInterface(false);

                if (currentPlayers.size() < 2) {
                    container.stop();
                }
            }
        }, 1);

        CycleEventHandler.getSingleton().addEvent(ARENA_TASK_OBJECT, new CycleEvent() {
            int tick;

            @Override
            public void execute(CycleEventContainer container) {
                tick++;
                for (String name : currentPlayers) {
                    Player player = PlayerHandler.getPlayerByLoginName(name);
                    if (player != null) {
                        if (tick == 10) {
                            resetFogTimers(player);
                            player.tournamentTotalGames++;
                            player.forcedChat("FIGHT!");
                            player.sendMessage("The tournament has begun! FIGHT!");
                            Achievements.increase(player, AchievementType.TOURNAMENT, 1);
                            player.canAttack = true;
                        } else {
                            player.forcedChat("" + (10 - tick));
                        }
                    }
                }
                updateInterface(true);
                if (tick == 10) {
                    container.stop();
                    return;
                }
            }
        }, 1);
    }

    private void tickFog() {
        for (String name : currentPlayers) {
            Player player = PlayerHandler.getPlayerByLoginName(name);
            if (player != null) {

                if (player.tournamentTarget == null)
                    continue;
                if (--player.tournamentFogDuration > 0)
                    continue;
                if (player.isDead() || !isInArenaBounds(player) || targetIsDead(player) || currentPlayers.size() <= 1 || !player.canAttack)
                    return;

                setFog(player, player.tournamentDamageFromFog > 0, player.tournamentDamageFromFog);
                int damagePerTick = player.tournamentDamageFromFog > 80 ? 5 : Misc.random(1, 2);

                if (player.tournamentTarget != null) {
                    player.tournamentDamageFromFog += damagePerTick;
                    player.appendDamage(damagePerTick, Hitmark.HIT);
                }
            }
        }
    }

    public static void resetCombatVariables(Player p) {
        resetFogTimers(p);
        setFog(p, false, 0);
    }

    private void announce(String message) {
        PlayerHandler.getPlayers().forEach(p -> {
            if (p != null) {
                p.sendMessage("<col=255>" + message);
            }
        });
    }

    public void join(Player player) {
        if (player.hasFollower) {
            player.sendMessage("@red@You have a pet spawned, bank it.");
            return;
        }
        if (!TourneyManager.getSingleton().isLobbyOpen()) {
            player.sendMessage("The tournament lobby is not currently open.");
            return;
        }
        if (isPlayerInTournament(player)) {
            player.sendMessage("You are already a part of the tournament");
            return;
        }
        if (isArenaActive()) {
            player.sendMessage("You are unable to join an active tournament.");
            return;
        }
        if (checkMacAddress(player) && !Server.isDebug()) {
            player.sendMessage("You can only play with one account per computer.");
            return;
        }
        if (player.getPotions().hasPotionBoost()) {
            player.getPotions().resetPotionBoost();
        }

        player.setRunEnergy(100, true);
        player.playerLevel[5] = player.getPA().getLevelForXP(player.playerXP[5]);
        player.getHealth().removeAllStatuses();
        player.getHealth().reset();
        player.getPA().refreshSkill(5);
        CombatPrayer.resetPrayers(player);
        player.spectatingTournament = false;
        player.resetVengeance();
        player.specRestore = 120;
        player.specAmount = 10.0;
        player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
        currentPlayers.add(player.getLoginName());
        player.tourneyItemsReceived.clear();
        player.getPA().movePlayer(new Coordinate(Misc.random(3322, 3324), Misc.random(4944, 4954)));
        player.sendMessage("You have joined the Tournament lobby.");
        player.sendMessage("Remain in this lobby until the Tournament begins.");
        updateInterface(true);
    }

    /**
     * Checks a players mac address against all in the current lobby
     */
    public boolean checkMacAddress(Player player) {
        for (String playerName : currentPlayers) {
            Player p = PlayerHandler.getPlayerByLoginName(playerName);
            if (p != null) {
                if (p.getMacAddress().equalsIgnoreCase(player.getMacAddress())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateInterface(boolean updatePlayers) {
        currentPlayers.forEach(p -> {
            Player player = PlayerHandler.getPlayerByLoginName(p);
            if (player != null) {
                player.getPA().sendFrame126(isLobbyOpen() ? getLobbyTime() :  hasTarget(player) ? getFogTime(player) : "Waiting for target..", 266);
                if (updatePlayers)
                    player.getPA().sendFrame126(isLobbyOpen() ? "Current Players In Lobby: @or1@" + currentPlayers.size() : "Players Left In Arena: @or1@" + currentPlayers.size(), 267);
                //player.getPA().sendFrame126(isLobbyOpen() ? "" : "Assigned Target: @or1@" + player.tournamentTarget == null ? "N/A" : StringUtils.capitalize(player.tournamentTarget.getName()), 267);
            }
        });
    }

    public void outlastEquip(Player player) {
        player.canAttack = false;

        for (int i = 0; i < currentSetup.getTournamentLevels().length; i++) {
            int xpForLevel = player.getPA().getXPForLevel(currentSetup.getTournamentLevels()[i] + 1);
            player.setLevel(Skill.forId(i), xpForLevel, true);
        }

        player.specAmount = 10.0;
        player.recoilHits = 0;
        switch (currentSetup.getMagicBook()) {
            case "NORMAL":
                player.setSidebarInterface(6, 938);
                player.playerMagicBook = 0;
                break;
            case "ANCIENT":
                player.setSidebarInterface(6, 838);
                player.playerMagicBook = 1;
                break;
            case "LUNAR":
                player.setSidebarInterface(6, 29999);
                player.playerMagicBook = 2;
                break;
        }
        for (int i = 0; i < currentSetup.getTournamentInventory().length; i++) {
            int inventoryItem = currentSetup.getTournamentInventory()[i].getItemID();
            int amount = currentSetup.getTournamentInventory()[i].getItemAmount();
            if (inventoryItem != -1) {
                player.getItems().addItem(inventoryItem, amount);
            }
        }
        //hat, cape, amulet, weapon, chest, shield, EMPTY, legs, EMPTY, hands, feet, EMPTY, ring, arrows
        for (int i = 0; i < currentSetup.getTournamentEquipment().length; i++) {
            if (currentSetup.getTournamentEquipment()[i] != -1) {
                if (i == 13) {
                    player.getItems().setEquipment(currentSetup.getTournamentEquipment()[i], currentSetup.getAmmoAmount(), i, false);
                } else {
                    player.getItems().setEquipment(currentSetup.getTournamentEquipment()[i], 1, i, false);
                }
                if (i == 3) {
                    player.setSpellId(-1);
                    player.usingMagic = false;
                    player.autocasting = false;
                    player.autocastId = 0;
                    player.getPA().sendFrame36(108, 0);
                    player.usingSpecial = false;
                    player.getItems().addSpecialBar(currentSetup.getTournamentEquipment()[i]);
                    player.getItems().updateSpecialBar();
                    player.getPA().resetAutocast();
                    if (currentSetup.getTournamentEquipment()[i] != 4153 && currentSetup.getTournamentEquipment()[i] != 12848) {
                        player.attacking.reset();
                    }
                }
            }
        }
        player.getItems().calculateBonuses();
        player.getItems().sendWeapon(player.playerEquipment[Player.playerWeapon]);
        MeleeData.setWeaponAnimations(player);
    }

    public void leaveLobby(Player player, boolean xlog) {
        if (!player.spectatingTournament && isParticipant(player)) {
            currentPlayers.remove(player.getLoginName());
            possibleTargets.remove(player);

            player.sendMessage("You have left the tournament lobby.");
            updateInterface(true);

            if (xlog)
                player.getPA().forceMove(3080, 3510, 0, true);
            else
                player.getPA().movePlayer(new Coordinate(3080, 3510));
            player.getPA().sendFog(false, 0);

            player.getPA().removePlayerHintIcon();
        } else {
            ViewingOrb.kickSpectators();
        }
    }

    /**
     * Handles a player that logs in within the lobby bounds
     */
    public void handleLoginWithinLobby(Player player) {
        getSingleton().leaveLobby(player, false);
    }

    /**
     * Handles a player that logs in within the arena bounds
     */
    public void handleLoginWithinArena(Player player) {
        if (!player.spectatingTournament) {
            CombatPrayer.resetPrayers(player);
            player.playerWalkIndex = 819;
            player.playerStandIndex = 808;
            player.playerRunIndex = 824;
        } else {
            ViewingOrb.leaveSpectatorTournament(player);
        }
    }

    /**
     * Handles the killer of a player getting rewarded
     */
    public void handleKill(Entity player) {
        if (player == null) {
            return;
        }
        if (currentPlayers.size() == 0 || !isArenaActive()) {
            return;
        }
        if (player instanceof Player) {
            Player killer = (Player) player;
            if (!isInArena(killer)) {
                return;
            }
            killer.outlastKills++;
            GameItem rewardItem = new GameItem(0, 1);
            int count = 0;
            boolean contains = false;
            while (!contains) {
                int category = Misc.random(999);
                if (category <= 150) {
                    //Very Rare
                    rewardItem = new GameItem(currentSetup.getVeryRareItems()[Misc.random(currentSetup.getVeryRareItems().length - 1)], 1);
                } else if (category <= 500) {
                    //Common
                    rewardItem = new GameItem(currentSetup.getCommonItems()[Misc.random(currentSetup.getCommonItems().length - 1)], 1);
                } else if (category <= 800) {
                    //Uncommon
                    rewardItem = new GameItem(currentSetup.getUncommonItems()[Misc.random(currentSetup.getUncommonItems().length - 1)], 1);
                } else if (category <= 999) {
                    //Rare
                    rewardItem = new GameItem(currentSetup.getRareItems()[Misc.random(currentSetup.getRareItems().length - 1)], 1);
                }
                count++;
                if (!killer.tourneyItemsReceived.contains(rewardItem.id())) {
                    contains = true;
                }
                if (count == (currentSetup.getVeryRareItems().length + currentSetup.getRareItems().length + currentSetup.getCommonItems().length + currentSetup.getUncommonItems().length)) {
                    killer.sendMessage("You\'ve already received all possible items.");
                    break;
                }
            }

            killer.tourneyItemsReceived.add(rewardItem.id());
            if (rewardItem.id() != 0) {
                if (killer.getItems().freeSlots() >= rewardItem.amount()) {
                    killer.getItems().addItem(rewardItem.id(), rewardItem.amount());
                } else {
                    killer.sendMessage("Your inventory was full and your rewarded items were dropped beneath you.");
                    Server.itemHandler.createGroundItem(killer, rewardItem.id(), killer.absX, killer.absY, killer.getHeight(), rewardItem.amount(), killer.getIndex());
                }
            }
            for (int i = 0; i < itemsToRemove.length; i++) {
                if (killer.getItems().playerHasItem(itemsToRemove[i])) {
                    int amount = killer.getItems().getItemAmount(itemsToRemove[i]);
                    killer.getItems().deleteItem2(itemsToRemove[i], amount);
                }
            }


            if (killer.getItems().freeSlots() > 0) {
                killer.getItems().addItem(12695, 1);
                killer.getItems().addItem(6685, 1);
                killer.getItems().addItem(3024, 2);
                killer.getItems().addItem(3144, 4);
                killer.getItems().addItem(11936, 6);
                killer.getItems().addItem(11936, killer.getItems().freeSlots());
            }
            killer.specRestore = 120;
            killer.specAmount = 10.0;
            killer.setRunEnergy(100, true);
            killer.getItems().addSpecialBar(killer.playerEquipment[Player.playerWeapon]);
            killer.playerLevel[5] = killer.getPA().getLevelForXP(killer.playerXP[5]);
            killer.getHealth().removeAllStatuses();
            killer.getHealth().reset();
            killer.getPA().refreshSkill(5);
            killer.tournamentPoints += 1;
        }
    }

    public static void setFog(Player player, boolean on, int strength) {
        player.getPA().sendFog(on, strength);
    }

    private static void resetFogTimers(Player p) {
        p.tournamentFogDuration = FOG_DURATION_TICKS;
        p.tournamentDamageFromFog = DAMAGE_FROM_FOG_DEFAULT;
    }

    public boolean targetIsDead(Player player) {
        return player.tournamentTarget != null && player.tournamentTarget.getHealth().getCurrentHealth() == 0;
    }

    /**
     * Handles a player dying within the arena
     *
     * @param playerName the player name
     */
    public void handleDeath(String playerName, boolean logout) {
        if (!currentPlayers.contains(playerName)) {
            System.err.println("containers players name??? " + playerName);
            return;
        }
        if (currentPlayers.size() <= 3) {
            victors.add(playerName);
        }
        currentPlayers.remove(playerName);
        updateInterface(true);

        Player player = PlayerHandler.getPlayerByLoginName(playerName);
        if (player != null) {
            if (currentPlayers.size() > 0) {
                player.sendMessage("You have been defeated!");
            }

            possibleTargets.remove(player);
            player.getEventCalendar().progress(EventChallenge.PARTICIPATE_IN_X_OUTLAST_TOURNIES);
            player.tournamentPoints += 1;
            player.sendMessage("@blu@You have gained an extra point for participating.");
            player.getPA().removePlayerHintIcon();
            removeTarget(player);
            player.attacking.reset();
            Server.getDatabaseManager().exec(new OutlastLeaderboardAdd(new OutlastLeaderboardEntry(player)));
            player.getPA().forceMove(3080, 3510, 0, logout);
        }
        if (currentPlayers.size() == 1) {
            handleDeath(currentPlayers.get(0), false);
        } else if (currentPlayers.size() == 0) {
            handleVictors();
        }
    }

    private void removeTarget(Player player) {
        if (player.tournamentTarget != null) {
            Player t = player.tournamentTarget;
            t.healEverything();
            t.sendMessage("You will receive a new target shortly!");
            t.tournamentTargetCooldown = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
            resetCombatVariables(t);
            possibleTargets.add(t);
            possibleTargets.remove(player);
            t.getPA().removePlayerHintIcon();
            t.tournamentTarget = null;
            player.tournamentTarget = null;
        }
    }

    /**
     * Handle the victors that won the tournament
     */
    private void handleVictors() {
        int tier = amountOfPlayers / 2;
        currentPlayers.clear();
        for (int i = victors.size() - 1; i >= 0; i--) {
            String name = victors.get(i);
            Player player = PlayerHandler.getPlayerByLoginName(name);
            if (player != null) {
                player.sendMessage("Congratulations you placed " + ((2 - i) + 1) + (i == 2 ? "st" : i == 1 ? "nd" : "rd") + " place in the Tournament!");
                if (player.getMode().getCoinRewardsFromTournaments()) {
                    if (i == 2) {
                        player.getItems().addItemToBankOrDrop(995, (1000000 * tier) + (10000000));//1st place gets 6m + 1m every 2 people
                    } else {
                        player.getItems().addItemToBankOrDrop(995, (500000 * tier) + (4000000));//2nd and 3rd get 2m + 500k ever 2 people
                    }
                    player.tournamentPoints += 1; //winner of tournament gets  a tournament win
                    player.attacking.reset();
                    player.sendMessage("@blu@You gained an extra point for placing.");
                } else {
                    if (i == 2) {
                        player.getItems().addItemToBankOrDrop(995, (500000 * tier) + (6000000));//1st place gets 6m + 1m every 2 people
                    } else if (i != 2) {
                        player.getItems().addItemToBankOrDrop(995, (250000 * tier) + (1000000));//2nd and 3rd get 2m + 500k ever 2 people
                    }
                    player.tournamentPoints += 1; //winner of tournament gets  a tournament win
                    player.attacking.reset();
                    player.sendMessage("@blu@You gained an extra point for placing.");
                }
                if (i == 2) {
                    player.streak += 1;
                    if (player.streak == 5) {
                        player.sendMessage("@red@You receive an extra 5m coins for having a 5 win streak.");
                        announce("@blu@" + name + " has just reached 5 wins in a row in Outlast!");
                        player.getItems().addItemToBankOrDrop(995, (5000000));
                        player.attacking.reset();
                    } else if (player.streak == 10) {
                        player.sendMessage("@red@You receive an extra 10m coins for having a 10 win streak.");
                        announce("@blu@" + name + " has just reached 10 wins in a row in Outlast!");
                        player.getItems().addItemToBankOrDrop(995, (10000000));
                    }
                    announce(player.getDisplayNameFormatted() + " has won the Tournament. Congratulations!");
                    announce(player.getDisplayNameFormatted() + " Current streak is " + player.streak + "!");
                    player.tournamentWins += 1; //winner of tournament gets  a tournament win
                    player.tournamentPoints += 3; //winner of tournament gets  a tournament win
                    player.getEventCalendar().progress(EventChallenge.WIN_AN_OUTLAST_TOURNAMENT);
                    player.getEventCalendar().progress(EventChallenge.PARTICIPATE_IN_X_OUTLAST_TOURNIES);
                    LeaderboardUtils.addCount(LeaderboardType.OUTLAST_WINS, player, 1);
                    player.sendMessage("@blu@You now have a total of @bla@" + player.tournamentPoints + " tournament points.");
                    player.sendMessage("@blu@You have won a total of @bla@" + player.tournamentWins + " @blu@tournament wins.");
                    player.sendMessage("@blu@You have gained an extra point for participating.");
                    player.attacking.reset();
                    player.sendMessage("You current winstreak is @red@" + player.streak + ".");
                    WINNER = "" + name + "";
                    Server.getDatabaseManager().exec(new OutlastRecentWinnersAdd(new OutlastRecentWinner(player)));
                    Server.getDatabaseManager().exec(new OutlastLeaderboardAdd(new OutlastLeaderboardEntry(player)));
                }
                if (i != 2) {
                    player.streak = 0;
                    player.sendMessage("@blu@You now have a total of @bla@" + player.tournamentPoints + " tournament points.");
                    player.getEventCalendar().progress(EventChallenge.PARTICIPATE_IN_X_OUTLAST_TOURNIES);
                    player.attacking.reset();
                }
            }
        }
        victors.clear();
        lobbyOpen = false;
        arenaActive = false;
        ViewingOrb.kickSpectators();
    }

    /**
     * Checks if a player is in the list of current players
     *
     * @param player
     * @return
     */
    private boolean isPlayerInTournament(Player player) {
        return currentPlayers.contains(player.getLoginName());
    }

    public boolean isParticipant(Player player) {
        return currentPlayers.contains(player.getLoginName());
    }

    /**
     * Checks if a player is standing in the lobby
     *
     * @param player
     * @return
     */
    public boolean isInLobbyBounds(Player player) {
        return player.absX >= 3320 && player.absX <= 3326 && player.absY >= 4939 && player.absY <= 4959;
    }

    /**
     * Checks if a player is standing in the arena
     *
     * @param player
     * @return
     */
    public boolean isInArenaBounds(Player player) {
        return player.absX >= 3266 && player.absX <= 3319 && player.absY >= 4930 && player.absY <= 4989;
    }

    /**
     * (THIS IS BECAUSE ASCEND DOESNT UNDERSTAND LOGIN LOCATION LOGIC...)
     *
     * @param player
     * @return
     */
    public boolean isInLobbyBoundsOnLogin(Player player) {
        return player.tourneyX >= 3320 && player.tourneyX <= 3326 && player.tourneyY >= 4939 && player.tourneyY <= 4959;
    }

    /**
     * (THIS IS BECAUSE ASCEND DOESNT UNDERSTAND LOGIN LOCATION LOGIC...)
     *
     * @param player
     * @return
     */
    public boolean isInArenaBoundsOnLogin(Player player) {
        return player.tourneyX >= 3266 && player.tourneyX <= 3319 && player.tourneyY >= 4930 && player.tourneyY <= 4989;
    }

    /**
     * Checks if a player is in the currently active and fighting arena
     *
     * @param player
     * @return
     */
    public boolean isInArena(Player player) {
        if (!isParticipant(player)) {
            return false;
        }
        return isArenaActive() && isInArenaBounds(player);
    }

    /**
     * Handles force logging out either in the arena or lobby
     *
     * @param player
     */
    public void handleLogout(Player player) {
        if (!isParticipant(player))
            return;
        if (isArenaActive()) {
            handleDeath(player.getLoginName(), true);
        } else {
            leaveLobby(player, true);
        }
    }

    /**
     * Get the time left until the fog begins
     *
     * @return
     */
    private String getFogTime(Player p) {
        String status = "";
        int fog = p.tournamentFogDuration;
        if (p.tournamentFogDuration < 0)
            status = "Fog Status: @gre@Forming..";
        else if (fog > 0 || !p.canAttack)
            status = "Time Until Fog: @or1@ "+Misc.convertTicksToShortTime(p.tournamentFogDuration);
        else
            status = "Fog Status: @red@Approaching";
        return status;
    }

    /**
     * Get the time left until the lobby closes and tournament starts
     *
     * @return
     */
    private String getLobbyTime() {
        int minutes = secondsUntilLobbyEnds / 60;
        int seconds = secondsUntilLobbyEnds % 60;

        if (secondsUntilLobbyEnds == 0)
            return "Moving to game..";
        return "Starts in: "+String.format("%d:%02d", minutes, seconds);
    }

    /**
     * Gets the number of players in the tournament
     *
     * @return
     */
    public int getPlayers() {
        return currentPlayers.size();
    }

    /**
     * Handles all action buttons within the login interface
     *
     * @param player
     * @param button
     * @return
     */
    public boolean handleActionButtons(Player player, int button) {
        switch (button) {
            case 1017:
                if (!Boundary.isIn(player, Boundary.OUTLAST_HUT)) {
                    return false;
                }
                player.getPA().closeAllWindows();
                join(player);
                return true;
            case 1020:
                player.getPA().closeAllWindows();
                return true;
            default:
                return false;
        }
    }

    /**
     * Gets the current prayer option for the current setup
     *
     * @return
     */
    public String getCurrentPrayerBlock() {
        if (currentSetup != null) {
            return currentSetup.getPrayerBlocked();
        }
        return "";
    }

    /**
     * Gets the time left to display on the quest tab
     *
     * @return
     */
    public String getTimeLeft() {
        if (isArenaActive()) {
            return "Outlast: @gre@Active";
        } else if (isLobbyOpen()) {
            return "Outlast: @whi@" + getLobbyTime();
        } else {
            return "Outlast: @red@" + WorldEventContainer.getInstance().getTimeUntilEvent(new TournamentWorldEvent()) + " minutes";
        }
    }

    public String getTimeRemaining() {
        if (isArenaActive()) {
            return "Active";
        } else if (isLobbyOpen()) {
            return getLobbyTime();
        } else {
            return WorldEventContainer.getInstance().getTimeUntilEvent(new TournamentWorldEvent()) + " minutes";
        }
    }

    public HashMap<String, TourneySetup> getSetups() {
        return tourneySetups;
    }

    public boolean isLobbyOpen() {
        return lobbyOpen;
    }

    public boolean isArenaActive() {
        return arenaActive;
    }

    private List<Player> getActivePlayers() {
        return currentPlayers.stream().map(PlayerHandler::getPlayerByLoginName).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
