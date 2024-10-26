package io.xeros.content.combat.death;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import com.google.common.collect.Lists;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.melee.CombatPrayer;
import io.xeros.content.combat.melee.MeleeData;
import io.xeros.content.combat.pvp.PkpRewards;
import io.xeros.content.itemskeptondeath.ItemsLostOnDeath;
import io.xeros.content.minigames.pest_control.PestControl;
import io.xeros.content.minigames.pk_arena.Highpkarena;
import io.xeros.content.minigames.pk_arena.Lowpkarena;
import io.xeros.content.minigames.raids.Raids;

import io.xeros.content.tournaments.TourneyManager;
import io.xeros.model.Items;
import io.xeros.model.collisionmap.doors.Location;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.*;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;
import io.xeros.model.entity.player.save.PlayerSave;
import io.xeros.model.items.GameItem;
import io.xeros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.xeros.model.multiplayersession.MultiplayerSessionStage;
import io.xeros.model.multiplayersession.MultiplayerSessionType;
import io.xeros.model.multiplayersession.duel.DuelSession;
import io.xeros.util.Misc;
import io.xeros.util.logging.player.DeathItemsHeld;
import io.xeros.util.logging.player.DeathItemsKept;
import io.xeros.util.logging.player.DeathItemsLost;
import io.xeros.util.logging.player.DeathLog;

public class PlayerDeath {

    private static void beforeDeath(Player player) {
        TourneyManager.setFog(player, false, 0);
        player.getPA().sendFrame126(":quicks:off", -1);
        player.getItems().setEquipmentUpdateTypes();
        player.getPA().requestUpdates();
        player.respawnTimer = 15;
        player.isDead = false;
        player.graceSum = 0;
        player.freezeTimer = 1;
        player.recoilHits = 0;
        player.totalHunllefDamage = 0;
        player.tradeResetNeeded = true;
        player.setSpellId(-1);
        player.attacking.reset();
        player.getPA().resetAutocast();
        player.usingMagic = false;
    }

    private static void afterDeath(Player player) {
        player.playerStandIndex = 808;
        player.playerWalkIndex = 819;
        player.playerRunIndex = 824;
        PlayerSave.saveGame(player);
        player.getPA().requestUpdates();
        player.getPA().removeAllWindows();
        player.getPA().closeAllWindows();
        player.getPA().resetFollowers();
        player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
        player.specAmount = 10;
        player.attackTimer = 10;
        player.respawnTimer = 15;
        player.lastVeng = 0;
        player.recoilHits = 0;
        player.graceSum = 0;
        player.freezeTimer = 1;
        player.vengOn = false;
        player.isDead = false;
        player.tradeResetNeeded = true;
    }

    public static void applyDead(Player player) {
        beforeDeath(player);

        var session = Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.TRADE);
        if (session != null && Server.getMultiplayerSessionListener().inSession(player, MultiplayerSessionType.TRADE)) {
            player.sendMessage("You have declined the trade.");
            session.getOther(player).sendMessage(player.getDisplayName() + " has declined the trade.");
            session.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
            return;
        }

        var duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
        if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION)
            duelSession = null;

        if (player.getSlayer().superiorSpawned)
            player.getSlayer().superiorSpawned = false;

        if (player.getRights().isOrInherits(Right.EVENT_MAN)) {
            // TODO: Other areas.
            if (Boundary.isIn(player, Boundary.DUEL_ARENA)
                || Boundary.isIn(player, Boundary.FIGHT_CAVE)
                || player.getPosition().inClanWarsSafe()
                || Boundary.isIn(player, Boundary.INFERNO)
                || Boundary.isIn(player, Boundary.OUTLAST_AREA)
                || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_AREA)
                || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_LOBBY)
                || Boundary.isIn(player, Boundary.PEST_CONTROL_AREA)
                || Boundary.isIn(player, Boundary.RAIDS)
                || Boundary.isIn(player, Boundary.OLM)
                || Boundary.isIn(player, Boundary.RAID_MAIN)
                || Boundary.isIn(player, Boundary.XERIC)) return;
            PlayerHandler.executeGlobalMessage("@red@News: @blu@" + player.getDisplayNameFormatted()
                    + " @pur@has just died, with a skill total of " + player.totalLevel
                    + "!");
        }
        if (player.getMode().isHardcoreIronman()) {
            // TODO: Other areas.
            if (Boundary.isIn(player, Boundary.DUEL_ARENA)
                || Boundary.isIn(player, Boundary.FIGHT_CAVE)
                || player.getPosition().inClanWarsSafe()
                || Boundary.isIn(player, Boundary.INFERNO)
                || Boundary.isIn(player, Boundary.OUTLAST_AREA)
                || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_AREA)
                || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_LOBBY)
                || Boundary.isIn(player, Boundary.PEST_CONTROL_AREA)
                || Boundary.isIn(player, Boundary.RAIDS)
                || Boundary.isIn(player, Boundary.OLM)
                || Boundary.isIn(player, Boundary.RAID_MAIN)
                || Boundary.isIn(player, Boundary.XERIC)) return;

            if (!Configuration.DISABLE_HC_LOSS_ON_DEATH) {
                if (player.totalLevel > 500)
                    PlayerHandler.executeGlobalMessage(
                        "@red@News: @blu@%s @pur@has just died in hardcore ironman mode, with a skill total of %d!"
                            .formatted(
                                player.getDisplayNameFormatted(),
                                player.totalLevel)
                    );

                if (player.getMode().getType() == ModeType.HC_IRON_MAN) {
                    player.getRights().remove(Right.HC_IRONMAN);
                    player.setMode(Mode.forType(ModeType.IRON_MAN));
                    player.getRights().setPrimary(Right.IRONMAN);
                    player.sendMessage("You are now a normal Ironman.");
                }
                else if (player.getMode().getType() == ModeType.ROGUE_HARDCORE_IRONMAN) {
                    player.getRights().remove(Right.ROGUE_HARDCORE_IRONMAN);
                    player.setMode(Mode.forType(ModeType.ROGUE_IRONMAN));
                    player.getRights().setPrimary(Right.ROGUE_IRONMAN);
                    player.sendMessage("You are now a rogue Ironman.");
                }
                else throw new IllegalStateException("Not a hardcore: " + player.getMode());

                PlayerSave.saveGame(player);
            }
        }

        // PvP Death
        if (Objects.isNull(duelSession)) {
            var killer = player.calculateKiller();
            if (killer != null) {
                player.setKiller(killer);
                if (killer instanceof Player playerKiller)
                    player.killerId = playerKiller.getIndex();
                player.sendMessage("Oh dear you are dead!");
            }
        }

        /*
         * Reset bounty hunter statistics
         */
        if (Configuration.BOUNTY_HUNTER_ACTIVE) {
            player.getBH().setCurrentHunterKills(0);
            player.getBH().setCurrentRogueKills(0);
            player.getBH().updateStatisticsUI();
            player.getBH().updateTargetUI();
        }

        player.startAnimation(2304);
        player.faceUpdate(0);
        player.stopMovement();

        /*
         * Death within the duel arena
         */
        if (duelSession != null && duelSession.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERATION) {
            if (duelSession.getWinner().isEmpty()) {
                Player opponent = duelSession.getOther(player);
                if (opponent.getHealth().getCurrentHealth() != 0) {
                    player.sendMessage("You have lost the duel!");
                    player.setDuelLossCounter(player.getDuelLossCounter() + 1);
                    player.sendMessage("You have now lost a total of @blu@%d @bla@ duels.".formatted(player.getDuelLossCounter()));

                    opponent.logoutDelay = System.currentTimeMillis();
                    if (duelSession.getWinner().isEmpty())
                        duelSession.setWinner(opponent);
                    PlayerSave.saveGame(opponent);
                }
            } else player.sendMessage("Congratulations, you have won the duel.");
            player.logoutDelay = System.currentTimeMillis();
        }

        if (player.prayerActive[CombatPrayer.RETRIBUTION] && !Boundary.isIn(player, Boundary.OUTLAST_AREA)) {
            player.gfx0(437);

            List<Entity> possibleTargets = Lists.newArrayList();

            if (player.getPosition().inMulti()) {
                Arrays.stream(PlayerHandler.players)
                    .filter(Objects::nonNull)
                    .filter(p -> p != player && p.getPosition().withinDistance(player.getPosition(), 1))
                    .forEach(possibleTargets::add);

                Arrays.stream(NPCHandler.npcs)
                    .filter(Objects::nonNull)
                    .filter(n -> n.getPosition().withinDistance(player.getPosition(), n.getSize()))
                    .forEach(possibleTargets::add);
            }

            Entity killer = player.getKiller();

            if (killer != null)
                if (!possibleTargets.contains(killer))
                    possibleTargets.add(killer);

            if (!possibleTargets.isEmpty())
                possibleTargets.forEach(e -> e.appendDamage(Misc.random(1, Misc.random(1, (player.playerLevel[5] / 4))), Hitmark.HIT));
        }
        afterDeath(player);
    }

    /**
     * Handles what happens after a player death
     */
    public static void giveLife(Player player) {
        // Set the visual masks of a player
        player.isDead = false;
        player.totalHunllefDamage = 0;
        player.faceUpdate(-1);
        player.freezeTimer = 1;
        player.isAnimatedArmourSpawned = false;
        player.setTektonDamageCounter(0);
        if (player.getGlodDamageCounter() >= 80 || player.getIceQueenDamageCounter() >= 80) {
            player.setGlodDamageCounter(79);
            player.setIceQueenDamageCounter(79);
        }
        if (player.hasFollower) if (player.petSummonId > 0) {
            var pet = PetHandler.forItem(player.petSummonId);
            if (pet != null)
                PetHandler.spawn(player, pet, true, false);
        }
        player.getQuestTab().updateInformationTab();
        player.getPA().stopSkilling();
        Arrays.fill(player.activeMageArena2BossId, 0);

        handleAreaBasedDeath(player);

        MeleeData.setWeaponAnimations(player);
        player.getItems().setEquipmentUpdateTypes();
        CombatPrayer.resetPrayers(player);
        IntStream.range(0, 20).forEach(i -> {
            player.playerLevel[i] = player.getPA().getLevelForXP(player.playerXP[i]);
            player.getPA().refreshSkill(i);
        });
        player.startAnimation(65535);
        PlayerSave.saveGame(player);
        player.resetOnDeath();
    }

    private static void handleAreaBasedDeath(Player player) {
        if (player.getInstance() != null && player.getInstance().handleDeath(player))
            return;
        player.removeFromInstance();
        Server.getLogging().write(new DeathLog(player));
        // Unsafe death
        if (player.getPosition().inWild()) {
            if (player.getRights().isOrInherits(Right.OWNER)) return;
            var itemsLostOnDeathList = ItemsLostOnDeath.generateModified(player);

            Server.getLogging().write(new DeathItemsHeld(player, player.getItems().getInventoryItems(), player.getItems().getEquipmentItems()),
                new DeathItemsKept(player, itemsLostOnDeathList.getKept()),
                new DeathItemsLost(player, itemsLostOnDeathList.getLost()));

            var killer = player.getKiller();
            var playerKiller = killer != null && killer.isPlayer() ? killer.asPlayer() : null;

            player.getItems().deleteAllItems();
            var lostItems = itemsLostOnDeathList.getLost();
            // Drop untradeable cash for killer and put in lost property shop for victim
            var untradeables = lostItems.stream().filter(it -> !it.getDef().isTradable()).toList();

            // Drop untradeable coins for killer, otherwise drop nothing
            if (playerKiller != null) {
                int coins = untradeables.stream().mapToInt(it -> it.getDef().getShopValue()).sum();
                if (coins > 0)
                    lostItems.add(new GameItem(Items.COINS, coins));
            }

            lostItems.removeAll(untradeables);
            untradeables.forEach(item -> player.getPerduLostPropertyShop().add(player, item));

            dropItemsForKiller(player, playerKiller, new GameItem(Items.BONES));
            lostItems.forEach(item -> dropItemsForKiller(player, playerKiller, item));

            if (playerKiller != null)
                PkpRewards.award(player, playerKiller);

            for (GameItem item : itemsLostOnDeathList.getKept())
                if (player.getItems().hasRoomInInventory(item.id(), item.amount()))
                    player.getItems().addItem(item.id(), item.amount());
                else
                    player.getItems().sendItemToAnyTab(item.id(), item.amount());

        }
        else if (TourneyManager.getSingleton().isInArena(player)) {
            var tourneyKiller = player.calculateTourneyKiller();
            player.outlastDeaths++;
            TourneyManager.getSingleton().handleDeath(player.getLoginName(), false);
            TourneyManager.getSingleton().handleKill(tourneyKiller);
        }
        else if (TourneyManager.getSingleton().isInLobbyBounds(player))
            TourneyManager.getSingleton().leaveLobby(player, false);

        if (Boundary.isIn(player, Boundary.PEST_CONTROL_AREA))
            player.getPA().movePlayer(2657, 2639, 0);
        else if (Boundary.isIn(player, PestControl.GAME_BOUNDARY))
            player.getPA().movePlayer(2656 + Misc.random(2), 2614 - Misc.random(3), 0);
        else if (Boundary.isIn(player, Boundary.ZULRAH))
            player.getPA().movePlayer(2202, 3056, 0);
        else if (Boundary.isIn(player, Boundary.KRAKEN_CAVE))
            player.getPA().movePlayer(2280, 10016, 0);

        if (Boundary.isIn(player, Boundary.CERBERUS_BOSSROOMS))
            player.getPA().movePlayer(1309, 1250, 0);
        else if (Boundary.isIn(player, Boundary.SKOTIZO_BOSSROOM))
            player.getPA().movePlayer(1665, 10045, 0);
        else if (Boundary.isIn(player, Boundary.DUEL_ARENA)) {
            DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
            if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERATION)
                duelSession.finish(MultiplayerSessionFinalizeType.GIVE_ITEMS);
        }
        else if (Boundary.isIn(player, Boundary.HYDRA_BOSS_ROOM))
            player.getPA().movePlayer(Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0);
        else if (Boundary.isIn(player, Boundary.FIGHT_CAVE))
            player.getFightCave().handleDeath();
        else if (Boundary.isIn(player, Boundary.INFERNO) && player.getInferno() != null)
            player.getInferno().handleDeath();
        else if (Boundary.isIn(player, Boundary.XERIC))
            player.getXeric().leaveGame(player, true);
        else if (Boundary.isIn(player, Boundary.OLM)) {
            Raids raidInstance = player.getRaidsInstance();
            if (raidInstance != null) {
                Location olmWait = raidInstance.getOlmWaitLocation();
                player.getPA().movePlayer(olmWait.getX(), olmWait.getY(), raidInstance.currentHeight);
                raidInstance.resetOlmRoom(player);
            }
        }
        else if (Boundary.isIn(player, Boundary.RAIDS)) {
            Raids raidInstance = player.getRaidsInstance();
            if (raidInstance != null) {
                Location startRoom = raidInstance.getStartLocation();
                player.getPA().movePlayer(startRoom.getX(), startRoom.getY(), raidInstance.currentHeight);
                raidInstance.resetRoom(player);
            }
        }
        else if (Highpkarena.getState(player) != null)
            Highpkarena.handleDeath(player);
        else if (Lowpkarena.getState(player) != null)
            Lowpkarena.handleDeath(player);
        else if (player.getPosition().inClanWars() || player.getPosition().inClanWarsSafe())
            player.getPA().movePlayer(player.absX, 4759, 0);
        else if (Boundary.isIn(player, Boundary.SAFEPKSAFE)) {
            player.getPA().movePlayer(Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0);
            onRespawn(player);
        }
        else {
            if (Boundary.isIn(player, Boundary.OUTLAST))
                player.getPA().movePlayer(new Coordinate(3080, 3510));
            else
                player.getPA().movePlayer(Configuration.RESPAWN_X, Configuration.RESPAWN_Y, 0);
            onRespawn(player);
        }
    }

    private static void dropItemsForKiller(Player killed, Player killer, GameItem item) {
        if (killer != null) {
            // Removes the PvP HP overlay from the killers screen when the target dies
            if (killed.equals(killer.getTargeted())) {
                killer.setTargeted(null);
                killer.getPA().sendEntityTarget(0, killed);
            }
            if (!killer.getMode().isItemScavengingPermitted()) Server.itemHandler.createGroundItem(item, killed.getPosition());
            else
                Server.itemHandler.createGroundItem(killer, item, killed.getPosition());
        }
        else
            Server.itemHandler.createGroundItem(item, killed.getPosition(), Misc.toCycles(3, TimeUnit.MINUTES));
    }

    private static void onRespawn(Player player) {
        player.isSkulled = false;
        player.skullTimer = 0;
        player.attackedPlayers.clear();
        player.getPA().removeAllWindows();
        player.getPA().closeAllWindows();
        player.resetDamageTaken();
        player.setKiller(null);
        player.killerId = 0;
    }

}
