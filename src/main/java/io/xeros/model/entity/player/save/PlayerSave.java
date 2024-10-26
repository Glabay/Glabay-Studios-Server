package io.xeros.model.entity.player.save;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;
import java.util.Map.Entry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementTier;
import io.xeros.content.achievement_diary.DifficultyAchievementDiary;
import io.xeros.content.achievement_diary.impl.ArdougneDiaryEntry;
import io.xeros.content.achievement_diary.impl.DesertDiaryEntry;
import io.xeros.content.achievement_diary.impl.FaladorDiaryEntry;
import io.xeros.content.achievement_diary.impl.FremennikDiaryEntry;
import io.xeros.content.achievement_diary.impl.KandarinDiaryEntry;
import io.xeros.content.achievement_diary.impl.KaramjaDiaryEntry;
import io.xeros.content.achievement_diary.impl.LumbridgeDraynorDiaryEntry;
import io.xeros.content.achievement_diary.impl.MorytaniaDiaryEntry;
import io.xeros.content.achievement_diary.impl.VarrockDiaryEntry;
import io.xeros.content.achievement_diary.impl.WesternDiaryEntry;
import io.xeros.content.achievement_diary.impl.WildernessDiaryEntry;
import io.xeros.content.combat.pvp.Killstreak;
import io.xeros.content.events.eventcalendar.EventCalendar;
import io.xeros.content.events.eventcalendar.EventChallengeKey;
import io.xeros.content.lootbag.LootingBag;
import io.xeros.content.lootbag.LootingBagItem;
import io.xeros.content.privatemessaging.FriendType;
import io.xeros.content.privatemessaging.FriendsListEntry;
import io.xeros.content.skills.slayer.SlayerMaster;
import io.xeros.content.skills.slayer.SlayerUnlock;
import io.xeros.content.skills.slayer.Task;
import io.xeros.content.skills.slayer.TaskExtension;
import io.xeros.content.titles.Title;
import io.xeros.model.controller.ControllerRepository;
import io.xeros.model.entity.player.*;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;
import io.xeros.model.entity.player.mode.group.GroupIronmanRepository;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.bank.BankItem;
import io.xeros.model.items.bank.BankTab;
import io.xeros.util.Misc;
import io.xeros.util.PasswordHashing;
import io.xeros.util.Reflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerSave {

    private static final Logger logger = LoggerFactory.getLogger(PlayerSave.class);

    public static String getSaveDirectory() {
        return Server.getSaveDirectory() + "/character_saves/";
    }

    public static File[] getAllCharacterSaves() {
        return new File(getSaveDirectory()).listFiles();
    }

    /**
     * Save all online users.
     * Don't call this from the main thread!
     */
    public static void saveAll() {
        long count = PlayerHandler.nonNullStream().count();
        GroupIronmanRepository.serializeAllInstant();
        PlayerHandler.nonNullStream().forEach(plr -> {
            try {
                PlayerSave.saveGameInstant(plr);
            } catch (Exception e) {
                logger.error("Error while saving account during player save backup {}", plr, e);
                e.printStackTrace(System.err);
            }
        });

        logger.info("Saved " + count + " online users.");
    }

    /**
     * Tells us whether or not the player exists for the specified name.
     * 
     * @param name
     * @return
     */
    public static boolean playerExists(String name) {
        Misc.createDirectory(getSaveDirectory());
        File file = new File(getSaveDirectory() + name + ".txt");
        return file.exists();
    }

    private static List<PlayerSaveEntry> playerSaveEntryList = Lists.newArrayList();

    /**
     * Reflect and collect {@link PlayerSaveEntry}
     */
    public static void loadPlayerSaveEntries() {
        Reflection.getSubClasses(PlayerSaveEntry.class).forEach(clazz -> {
            try {
                playerSaveEntryList.add((PlayerSaveEntry) clazz.getConstructors()[0].newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace(System.err);
            }
        });
        playerSaveEntryList = Collections.unmodifiableList(playerSaveEntryList);
        logger.info("Loaded " + playerSaveEntryList.size() + " Player Save Entries.");
    }

    public static void login(Player player) {
        playerSaveEntryList.forEach(entry -> entry.login(player));
    }

    /**
     * Loading
     */
    public static LoadGameResult loadGame(Player p, String playerName, String playerPass, boolean passedCaptcha) {
        Misc.createDirectory(getSaveDirectory());
        String line = "";
        String token = "";
        String token2 = "";
        String[] token3 = new String[3];

        boolean EndOfFile = false;
        int ReadMode = 0;
        BufferedReader characterfile = null;
        boolean characterFileExists = false;

        try {
            characterfile = new BufferedReader(new FileReader(getSaveDirectory() + playerName.toLowerCase() + ".txt"));
            characterFileExists = true;
        } catch (FileNotFoundException ignored) { }

        if (!characterFileExists) {
            return LoadGameResult.NEW_PLAYER;
        }

        try {
            line = characterfile.readLine();
        } catch (IOException ioexception) {
            logger.error("Error while loading {}", playerName, ioexception);
            Misc.println(playerName + ": error loading file.");
            return LoadGameResult.ERROR_OCCURRED;
        }
        try {
            p.getFarming().load();
        } catch (Exception e) {
            logger.error("Error while loading farming {}", playerName, e);
            e.printStackTrace(System.err);
        }

        // Migrating old accounts, if new launch this can be removed along with [FRIENDS]/[IGNORES] reading
        List<FriendsListEntry> friends = new ArrayList<>();

        main:
        while (EndOfFile == false && line != null) {
            line = line.trim();
            try {
                int spot = line.indexOf("=");
                if (spot > -1) {
                    token = line.substring(0, spot);
                    token = token.trim();
                    token2 = line.substring(spot + 1);
                    token2 = token2.trim();
                    token3 = token2.split("\t");
                    if (ReadMode == 2) {
                        for (PlayerSaveEntry entry : playerSaveEntryList) {
                            if (entry.getKeys(p).contains(token)) {
                                Preconditions.checkState(entry.decode(p, token, token2), "Failed to decode player save entry: " + entry.getClass() + ", token: " + token);
                                line = characterfile.readLine();
                                continue main;
                            }
                        }
                    }
                    switch (ReadMode) {
                    case 1: 
                        if (token.equals("character-password")) {
                            try {
                                if (PasswordHashing.check(p.playerPass, token2)) {
                                    playerPass = token2;
                                } else {
                                    if (Server.isDebug()) {
                                        System.out.println("Invalid password but server is in debug mode so it\'s ignored.");
                                    } else return LoadGameResult.INVALID_CREDENTIALS;
                                }
                            } catch (IllegalArgumentException e) {
                                logger.error("Error while loading {}", playerName, e);
                                e.printStackTrace(System.err);
                                return LoadGameResult.ERROR_OCCURRED;
                            }
                        }
                        break;
                    case 2: 
                        if (token.equals("character-height")) {
                            p.heightLevel = Integer.parseInt(token2);
                        } else if (token.equals("character-hp")) {
                            p.getHealth().setCurrentHealth(Integer.parseInt(token2));
                            if (p.getHealth().getCurrentHealth() <= 0) {
                                p.getHealth().setCurrentHealth(10);
                            }
                        } else if (token.equals("character-mac-address")) {
                            if (!p.getMacAddress().equalsIgnoreCase(token2)) {
                                if (!Configuration.DISABLE_CHANGE_ADDRESS_CAPTCHA && !passedCaptcha)
                                    return LoadGameResult.REQUIRE_CAPTCHA;
                                p.setAddressChanged("mac", token2, p.getMacAddress(), true);
                            }
                        } else if (token.equals("character-ip-address")) {
                            if (!p.getIpAddress().equalsIgnoreCase(token2)) {
                                p.setAddressChanged("ip", token2, p.getIpAddress(), false);
                            }
                        } else if (token.equals("character-uuid")) {
                            if (!p.getUUID().equalsIgnoreCase(token2)) {
                                if (!Configuration.DISABLE_CHANGE_ADDRESS_CAPTCHA && !passedCaptcha)
                                    return LoadGameResult.REQUIRE_CAPTCHA;
                                p.setAddressChanged("uuid", token2, p.getUUID(), true);
                            }
                        } else if (token.equals("play-time")) {
                            p.playTime = Integer.parseInt(token2);
                        } else if (token.equals("last-clan")) {
                            p.setLastClanChat(token2);
                        } else if (token.equals("require-pin-unlock")) {
                            boolean requiresPinUnlock = Boolean.parseBoolean(token2);
                            if (requiresPinUnlock) {
                                p.setRequiresPinUnlock(requiresPinUnlock);
                                p.addQueuedLoginAction(plr -> {
                                    if (!plr.getBankPin().hasBankPin()) {
                                        plr.setRequiresPinUnlock(false);
                                        return;
                                    }
                                    plr.sendMessage("<img=2>@dre@Your pin is required because you logged in from a different computer");
                                    plr.sendMessage("<img=2>@dre@and logged off without entering your account pin.");
                                    plr.sendMessage("<img=2>@red@If this wasn't you then you should secure your account!");
                                });
                            }
                        } else if (token.equals("character-specRestore")) {
                            p.specRestore = Integer.parseInt(token2);
                        } else if (token.equals("character-posx")) {
                            p.setTeleportToX(p.tourneyX = (Integer.parseInt(token2) <= 0 ? 3210 : Integer.parseInt(token2)));
                        } else if (token.equals("character-posy")) {
                            p.setTeleportToY(p.tourneyY = (Integer.parseInt(token2) <= 0 ? 3424 : Integer.parseInt(token2)));
                        } else if (token.equals("character-rights")) {
                            p.getRights().setPrimary(Right.get(Integer.parseInt(token2)));
                        } else if (token.equals("character-rights-secondary")) {
                            // sound like an activist group
                            Arrays.stream(token3).forEach(right -> p.getRights().add(Right.get(Integer.parseInt(right))));
                        } else if (token.equals("migration-version")) {
                            p.setMigrationVersion(Integer.parseInt(token2));
                        } else if (token.equals("revert-option")) {
                            p.setRevertOption(token2);
                        } else if (token.equals("revert-delay")) {
                            p.setRevertModeDelay(Long.parseLong(token2));
                        } else if (token.equals("dropBoostStart")) {
                            p.dropBoostStart = Long.parseLong(token2);
                        } else if (token.equals("mode")) {
                            ModeType type = null;
                            try {
                                if (token2.equals("NONE")) {
                                    token2 = "REGULAR";
                                }
                                type = Enum.valueOf(ModeType.class, token2);
                            } catch (NullPointerException | IllegalArgumentException e) {
                                e.printStackTrace(System.err);
                                logger.error("Error while loading mode {}, type={}", playerName, token2, e);
                                break;
                            }
                            Mode mode = Mode.forType(type);
                            p.setMode(mode);
                        } else if (token.equals("character-title-updated")) {
                            p.getTitles().setCurrentTitle(token2);
                        } else if (token.equals("receivedVoteStreakRefund")) {
                            p.setReceivedVoteStreakRefund(Boolean.parseBoolean(token2));
                        } else if (token.equals("experience-counter")) {
                            p.setExperienceCounter(Long.parseLong(token2));
                        } else if (token.equals("connected-from")) {
                            p.lastConnectedFrom.add(token2);
                        } else if (token.equals("printAttackStats")) {
                            p.setPrintAttackStats(Boolean.parseBoolean(token2));
                        } else if (token.equals("printDefenceStats")) {
                            p.setPrintDefenceStats(Boolean.parseBoolean(token2));
                        } else if (token.equals("collectCoins")) {
                            p.collectCoins = Boolean.parseBoolean(token2);
                        } else if (token.equals("horror-from-deep")) {
                            p.horrorFromDeep = Integer.parseInt(token2);
                        } else if (token.equals("breakVials")) {
                            p.breakVials = Boolean.parseBoolean(token2);
                        } else if (token.equals("absorption")) {
                            p.absorption = Boolean.parseBoolean(token2);
                        } else if (token.equals("announce")) {
                            p.announce = Boolean.parseBoolean(token2);
                        } else if (token.equals("lootPickUp")) {
                            p.lootPickUp = Boolean.parseBoolean(token2);
                        } else if (token.equals("barbarian")) {
                            p.barbarian = Boolean.parseBoolean(token2);
                        } else if (token.equals("run-energy")) {
                            p.setRunEnergy(Integer.parseInt(token2), false);
                        } else if (token.equals("bank-pin")) {
                            p.getBankPin().setPin(token2);
                        } else if (token.equals("bank-pin-cancellation")) {
                            p.getBankPin().setAppendingCancellation(Boolean.parseBoolean(token2));
                        } else if (token.equals("bank-pin-cancellation-delay")) {
                            p.getBankPin().setCancellationDelay(Long.parseLong(token2));
                        } else if (token.equals("bank-pin-unlock-delay")) {
                            p.getBankPin().setUnlockDelay(Long.parseLong(token2));
                        } else if (token.equals("placeholders")) {
                            p.placeHolders = Boolean.parseBoolean(token2);
                        } else if (token.equals("show-drop-warning")) {
                            p.setDropWarning(Boolean.parseBoolean(token2));
                        } else if (token.equals("show-alch-warning")) {
                            p.setAlchWarning(Boolean.parseBoolean(token2));
                        } else if (token.equals("hourly-box-toggle")) {
                            p.setHourlyBoxToggle(Boolean.parseBoolean(token2));
                        } else if (token.equals("fractured-crystal-toggle")) {
                            p.setFracturedCrystalToggle(Boolean.parseBoolean(token2));
                        } else if (token.equals("accept-aid")) {
                            p.acceptAid = Boolean.parseBoolean(token2);
                        } else if (token.equals("did-you-know")) {
                            p.didYouKnow = Boolean.parseBoolean(token2);
                        } else if (token.equals("spectating-tournament")) {
                            p.spectatingTournament = Boolean.parseBoolean(token2);
                        } else if (token.equals("raidPoints")) {
                            p.setRaidPoints(Integer.parseInt(token2));
                        } else if (token.equals("raidCount")) {
                            p.raidCount = (Integer.parseInt(token2));
                        } else if (token.equals("tobCompletions")) {
                            p.tobCompletions = (Integer.parseInt(token2));
                        } else if (token.equals("lootvalue")) {
                            p.lootValue = Integer.parseInt(token2);
                        } else if (token.equals("startPack")) {
                            p.setCompletedTutorial(Boolean.parseBoolean(token2));
                        } else if (token.equals("unlockedUltimateChest")) {
                            p.unlockedUltimateChest = Boolean.parseBoolean(token2);
                        } else if (token.equals("rigour")) {
                            p.rigour = Boolean.parseBoolean(token2);
                        } else if (token.equals("augury")) {
                            p.augury = Boolean.parseBoolean(token2);
                        } else if (token.equals("crystalDrop")) {
                            p.crystalDrop = Boolean.parseBoolean(token2);
                        } else if (token.equals("spawnedbarrows")) {
                            p.spawnedbarrows = Boolean.parseBoolean(token2);
                        } else if (token.equals("membershipStartDate")) {
                            p.startDate = Integer.parseInt(token2);
                        } else if (token.equals("XpScrollTime")) {
                            p.xpScrollTicks = Long.parseLong(token2);
                        } else if (token.equals("fasterClueScrollTime")) {
                            p.fasterCluesTicks = Long.parseLong(token2);
                        } else if (token.equals("skillingPetRateTime")) {
                            p.skillingPetRateTicks = Long.parseLong(token2);
                        } else if (token.equals("serpHelmCombatTicks")) {
                            p.serpHelmCombatTicks = Long.parseLong(token2);
                        } else if (token.equals("gargoyleStairsUnlocked")) {
                            p.gargoyleStairsUnlocked = Boolean.parseBoolean(token2);
                        } else if (token.equals("controller")) {
                            p.setLoadedController(ControllerRepository.get(token2));
                        } else if (token.equals("joinedIronmanGroup")) {
                            p.setJoinedIronmanGroup(Boolean.parseBoolean(token2));
                        } else if (token.equals("receivedCalendarCosmeticJune2021")) {
                            p.setReceivedCalendarCosmeticJune2021(Boolean.parseBoolean(token2));
                        } else if (token.equals("firstAchievementLoginJune2021")) {
                            p.getAchievements().setFirstAchievementLoginJune2021(Boolean.parseBoolean(token2));
                        } else if (token.equals("XpScroll")) {
                            p.xpScroll = Boolean.parseBoolean(token2);
                        } else if (token.equals("skillingPetRateScroll")) {
                            p.skillingPetRateScroll = Boolean.parseBoolean(token2);
                        } else if (token.equals("fasterClueScroll")) {
                            p.fasterCluesScroll = Boolean.parseBoolean(token2);
                        }else if (token.equals("activeMageArena2BossId")) {
                            for (int i = 0; i < p.activeMageArena2BossId.length; i++) p.activeMageArena2BossId[i] = Integer.parseInt(token3[i]);
                        }else if (token.equals("mageArena2SpawnsX")) {
                            for (int i = 0; i < p.mageArena2SpawnsX.length; i++) p.mageArena2SpawnsX[i] = Integer.parseInt(token3[i]);
                        }else if (token.equals("mageArena2SpawnsY")) {
                            for (int i = 0; i < p.mageArena2SpawnsY.length; i++) p.mageArena2SpawnsY[i] = Integer.parseInt(token3[i]);
                        }else if (token.equals("mageArenaBossKills")) {
                            for (int i = 0; i < p.mageArenaBossKills.length; i++) p.mageArenaBossKills[i] = Boolean.parseBoolean(token3[i]);
                        }else if (token.equals("mageArena2Stages")) {
                            for (int i = 0; i < p.mageArena2Stages.length; i++) p.mageArena2Stages[i] = Boolean.parseBoolean(token3[i]);
                        }else if (token.equals("flamesOfZamorakCasts")) {
                            p.flamesOfZamorakCasts = (Integer.parseInt(token2));
                        }else if (token.equals("flamesOfZamorakCasts")) {
                            p.flamesOfZamorakCasts = (Integer.parseInt(token2));
                        }else if (token.equals("clawsOfGuthixCasts")) {
                            p.clawsOfGuthixCasts = (Integer.parseInt(token2));
                        }else if (token.equals("saradominStrikeCasts")) {
                            p.saradominStrikeCasts = (Integer.parseInt(token2));
                        }else if (token.equals("exchangeP")) {
                            p.exchangePoints = (Integer.parseInt(token2));
                        }else if (token.equals("totalEarnedExchangeP")) {
                            p.totalEarnedExchangePoints = (Integer.parseInt(token2));
                        } else if (token.equals("usedFc")) {
                            p.usedFc = Boolean.parseBoolean(token2);
                        } else if (token.equals("lastLoginDate")) {
                            p.lastLoginDate = Integer.parseInt(token2);
                        } else if (token.equals("summonId")) {
                            p.petSummonId = Integer.parseInt(token2);
                        } else if (token.equals("has-npc")) {
                            p.hasFollower = Boolean.parseBoolean(token2);
                        } else if (token.equals("setPin")) {
                            p.setPin = Boolean.parseBoolean(token2);
                        } else if (token.equals("hasBankpin")) {
                            p.hasBankpin = Boolean.parseBoolean(token2);
                        } else if (token.equals("rfd-gloves")) {
                            p.rfdGloves = Integer.parseInt(token2);
                        } else if (token.equals("wave-id")) {
                            p.waveId = Integer.parseInt(token2);
                        } else if (token.equals("wave-type")) {
                            p.fightCavesWaveType = Integer.parseInt(token2);
                        } else if (token.equals("wave-info")) {
                            for (int i = 0; i < p.waveInfo.length; i++) p.waveInfo[i] = Integer.parseInt(token3[i]);
                        } else if (token.equals("help-cc-muted")) {
                            p.setHelpCcMuted(Boolean.parseBoolean(token2));
                        } else if (token.equals("gamble-banned")) {
                            p.setGambleBanned(Boolean.parseBoolean(token2));
                        } else if (token.equals("usedReferral")) {
                            p.usedReferral = Boolean.parseBoolean(token2);
                        } else if (token.equals("counters")) {
                            for (int i = 0; i < p.counters.length; i++) p.counters[i] = Integer.parseInt(token3[i]);
                        } else if (token.equals("max-cape")) {
                            for (int i = 0; i < p.maxCape.length; i++) p.maxCape[i] = Boolean.parseBoolean(token3[i]);
                        } else if (token.equals("master-clue-reqs")) {
                            for (int i = 0; i < p.masterClueRequirement.length; i++) p.masterClueRequirement[i] = Integer.parseInt(token3[i]);
                        } else if (token.equals("quickprayer")) {
                            for (int j = 0; j < token3.length; j++) {
                                p.getQuick().getNormal()[j] = Boolean.parseBoolean(token3[j]);
                            }
                        } else if (token.equals("zulrah-best-time")) {
                            p.setBestZulrahTime(Long.parseLong(token2));
                        } else if (token.equals("inferno-best-time")) {
                            p.setInfernoBestTime(Long.parseLong(token2));
                        } else if (token.equals("toxic-staff")) {
                            p.setToxicStaffOfTheDeadCharge(Integer.parseInt(token2));
                        } else if (token.equals("toxic-pipe-ammo")) {
                            p.setToxicBlowpipeAmmo(Integer.parseInt(token2));
                        } else if (token.equals("toxic-pipe-amount")) {
                            p.setToxicBlowpipeAmmoAmount(Integer.parseInt(token2));
                        } else if (token.equals("toxic-pipe-charge")) {
                            p.setToxicBlowpipeCharge(Integer.parseInt(token2));
                        } else if (token.equals("serpentine-helm")) {
                            p.setSerpentineHelmCharge(Integer.parseInt(token2));
                        } else if (token.equals("trident-of-the-seas")) {
                            p.setTridentCharge(Integer.parseInt(token2));
                        } else if (token.equals("trident-of-the-swamp")) {
                            p.setToxicTridentCharge(Integer.parseInt(token2));
                        } else if (token.equals("arclight-charge")) {
                            p.setArcLightCharge(Integer.parseInt(token2));
                        } else if (token.equals("sang-staff-charge")) {
                            p.setSangStaffCharge(Integer.parseInt(token2));
                        } else if (token.equals("bryophyta-charge")) {
                            p.bryophytaStaffCharges = Integer.parseInt(token2);
                        } else if (token.equals("crystal-bow-shots")) {
                            p.crystalBowArrowCount = Integer.parseInt(token2);
                        } else if (token.equals("skull-timer")) {
                            p.skullTimer = Integer.parseInt(token2);
                        } else if (token.equals("magic-book")) {
                            p.playerMagicBook = Integer.parseInt(token2);
                        } else if (token.equals("slayer-recipe") || token.equals("slayer-helmet")) {
                            // Backwards compat
                            if (Boolean.parseBoolean(token2)) {
                                p.getSlayer().getUnlocks().add(SlayerUnlock.MALEVOLENT_MASQUERADE);
                            }
                        } else if (token.equals("bigger-boss-tasks")) {
                            p.getSlayer().setBiggerBossTasks(Boolean.parseBoolean(token2));
                        } else if (token.equals("cerberus-route")) {
                            p.getSlayer().setCerberusRoute(Boolean.parseBoolean(token2));
                        } else if (token.equals("superior-slayer")) {
                            // Backwards compat
                            if (Boolean.parseBoolean(token2)) {
                                p.getSlayer().getUnlocks().add(SlayerUnlock.BIGGER_AND_BADDER);
                            }
                        } else if (token.equals("slayer-tasks-completed")) {
                            p.slayerTasksCompleted = Integer.parseInt(token2);
                        } else if (token.equals("claimedReward")) {
                            p.claimedReward = Boolean.parseBoolean(token2);
                        } else if (token.equals("special-amount")) {
                            p.specAmount = Double.parseDouble(token2);
                        } else if (token.equals("prayer-amount")) {
                            p.prayerPoint = Double.parseDouble(token2);
                        } else if (token.equals("dragonfire-shield-charge")) {
                            p.setDragonfireShieldCharge(Integer.parseInt(token2));
                        } else if (token.equals("autoRet")) {
                            p.autoRet = Integer.parseInt(token2);
                        } else if (token.equals("pkp")) {
                            p.pkp = Integer.parseInt(token2);
                        } else if (token.equals("elvenCharge")) {
                            p.elvenCharge = Integer.parseInt(token2);
                        } else if (token.equals("slaughterCharge")) {
                            p.slaughterCharge = Integer.parseInt(token2);
                        } else if (token.equals("pages")) {
                            int oldTomeOfFirePages = Integer.parseInt(token2) / 20;
                            p.getTomeOfFire().addPages(oldTomeOfFirePages);
                        } else if (token.equals("tomeOfFirePages")) {
                            int pages = Integer.parseInt(token2);
                            p.getTomeOfFire().setPages(pages);
                        } else if (token.equals("tomeOfFireCharges")) {
                            int charges = Integer.parseInt(token2);
                            p.getTomeOfFire().setCharges(charges);
                        } else if (token.equals("ether")) {
                            p.braceletEtherCount = Integer.parseInt(token2);
                        } else if (token.equals("bossPoints")) {
                            p.bossPoints = Integer.parseInt(token2);
                        } else if (token.equals("bossPointsRefund")) {
                            p.bossPointsRefund = Boolean.parseBoolean(token2);
                        } else if (token.equals("tWin")) {
                            p.tournamentWins = Integer.parseInt(token2);
                        } else if (token.equals("tPoint")) {
                            p.tournamentPoints = Integer.parseInt(token2);
                        } else if (token.equals("streak")) {
                            p.streak = Integer.parseInt(token2);
                        } else if (token.equals("outlastKills")) {
                            p.outlastKills = Integer.parseInt(token2);
                        } else if (token.equals("outlastDeaths")) {
                            p.outlastDeaths = Integer.parseInt(token2);
                        } else if (token.equals("tournamentTotalGames")) {
                            p.tournamentTotalGames = Integer.parseInt(token2);
                        } else if (token.equals("xpMaxSkills")) {
                            p.xpMaxSkills = Integer.parseInt(token2);
                        } else if (token.equals("LastLoginYear")) {
                            p.LastLoginYear = Integer.parseInt(token2);
                        } else if (token.equals("LastLoginMonth")) {
                            p.LastLoginMonth = Integer.parseInt(token2);
                        } else if (token.equals("LastLoginDate")) {
                            p.LastLoginDate = Integer.parseInt(token2);
                        } else if (token.equals("LoginStreak")) {
                            p.LoginStreak = Integer.parseInt(token2);
                        } else if (token.equals("RefU")) {
                            p.referallFlag = Integer.parseInt(token2);
                        } else if (token.equals("LoyP")) {
                            p.loyaltyPoints = Integer.parseInt(token2);
                        } else if (token.equals("votePoints")) {
                            p.votePoints = Integer.parseInt(token2);
                        } else if (token.equals("dayv")) {
                            p.voteKeyPoints = Integer.parseInt(token2);
                        } else if (token.equals("bloodPoints")) {
                            p.bloodPoints = Integer.parseInt(token2);
                        } else if (token.equals("donP")) {
                            p.donatorPoints = Integer.parseInt(token2);
                        } else if (token.equals("donA")) {
                            p.amDonated = Integer.parseInt(token2);
                        } else if (token.equals("prestige-points")) {
                            p.prestigePoints = Integer.parseInt(token2);
                        } else if (token.equals("xpLock")) {
                            p.expLock = Boolean.parseBoolean(token2);
                        } else if (line.startsWith("KC")) {
                            p.killcount = Integer.parseInt(token2);
                        } else if (line.startsWith("DC")) {
                            p.deathcount = Integer.parseInt(token2);
                        } else if (token.equals("pc-points")) {
                            p.pcPoints = Integer.parseInt(token2);
                        } else if (token.equals("total-raids")) {
                            p.totalRaidsFinished = Integer.parseInt(token2);
                        } else if (token.equals("total-rogue-kills")) {
                            p.getBH().setTotalRogueKills(Integer.parseInt(token2));
                        } else if (token.equals("total-hunter-kills")) {
                            p.getBH().setTotalHunterKills(Integer.parseInt(token2));
                        } else if (token.equals("target-time-delay")) {
                            p.getBH().setDelayedTargetTicks(Integer.parseInt(token2));
                        } else if (token.equals("bh-penalties")) {
                            p.getBH().setWarnings(Integer.parseInt(token2));
                        } else if (token.equals("bh-bounties")) {
                            p.getBH().setBounties(Integer.parseInt(token2));
                        } else if (token.equals("statistics-visible")) {
                            p.getBH().setStatisticsVisible(Boolean.parseBoolean(token2));
                        } else if (token.equals("spell-accessible")) {
                            p.getBH().setSpellAccessible(Boolean.parseBoolean(token2));
                        } else if (token.equals("killStreak")) {
                            p.killStreak = Integer.parseInt(token2);
                        } else if (token.equals("achievement-points")) {
                            p.getAchievements().setPoints(Integer.parseInt(token2));
                        } else if (token.equals("d1Complete")) { //Varrock claimed
                            p.d1Complete = Boolean.parseBoolean(token2);
                        } else if (token.equals("d2Complete")) {
                            p.d2Complete = Boolean.parseBoolean(token2);
                        } else if (token.equals("d3Complete")) {
                            p.d3Complete = Boolean.parseBoolean(token2);
                        } else if (token.equals("d4Complete")) {
                            p.d4Complete = Boolean.parseBoolean(token2);
                        } else if (token.equals("d5Complete")) {
                            p.d5Complete = Boolean.parseBoolean(token2);
                        } else if (token.equals("d6Complete")) {
                            p.d6Complete = Boolean.parseBoolean(token2);
                        } else if (token.equals("d7Complete")) {
                            p.d7Complete = Boolean.parseBoolean(token2);
                        } else if (token.equals("d8Complete")) {
                            p.d8Complete = Boolean.parseBoolean(token2);
                        } else if (token.equals("d9Complete")) {
                            p.d9Complete = Boolean.parseBoolean(token2);
                        } else if (token.equals("d10Complete")) {
                            p.d10Complete = Boolean.parseBoolean(token2);
                        } else if (token.equals("d11Complete")) {
                            p.d11Complete = Boolean.parseBoolean(token2);
                        } else if (token.equals("VarrockClaimedDiaries")) {
                            String[] claimedRaw = token2.split(",");
                            for (String claim : claimedRaw) {
                                DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> p.getDiaryManager().getVarrockDiary().claim(diff));
                            }
                        } else if (token.equals("ArdougneClaimedDiaries")) {
                            String[] claimedRaw = token2.split(",");
                            for (String claim : claimedRaw) {
                                DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> p.getDiaryManager().getArdougneDiary().claim(diff));
                            }
                        } else if (token.equals("DesertClaimedDiaries")) {
                            String[] claimedRaw = token2.split(",");
                            for (String claim : claimedRaw) {
                                DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> p.getDiaryManager().getDesertDiary().claim(diff));
                            }
                        } else if (token.equals("FaladorClaimedDiaries")) {
                            String[] claimedRaw = token2.split(",");
                            for (String claim : claimedRaw) {
                                DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> p.getDiaryManager().getFaladorDiary().claim(diff));
                            }
                        } else if (token.equals("FremennikClaimedDiaries")) {
                            String[] claimedRaw = token2.split(",");
                            for (String claim : claimedRaw) {
                                DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> p.getDiaryManager().getFremennikDiary().claim(diff));
                            }
                        } else if (token.equals("KandarinClaimedDiaries")) {
                            String[] claimedRaw = token2.split(",");
                            for (String claim : claimedRaw) {
                                DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> p.getDiaryManager().getKandarinDiary().claim(diff));
                            }
                        } else if (token.equals("KaramjaClaimedDiaries")) {
                            String[] claimedRaw = token2.split(",");
                            for (String claim : claimedRaw) {
                                DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> p.getDiaryManager().getKaramjaDiary().claim(diff));
                            }
                        } else if (token.equals("LumbridgeClaimedDiaries")) {
                            String[] claimedRaw = token2.split(",");
                            for (String claim : claimedRaw) {
                                DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> p.getDiaryManager().getLumbridgeDraynorDiary().claim(diff));
                            }
                        } else if (token.equals("MorytaniaClaimedDiaries")) {
                            String[] claimedRaw = token2.split(",");
                            for (String claim : claimedRaw) {
                                DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> p.getDiaryManager().getMorytaniaDiary().claim(diff));
                            }
                        } else if (token.equals("WesternClaimedDiaries")) {
                            String[] claimedRaw = token2.split(",");
                            for (String claim : claimedRaw) {
                                DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> p.getDiaryManager().getWesternDiary().claim(diff));
                            }
                        } else if (token.equals("WildernessClaimedDiaries")) {
                            String[] claimedRaw = token2.split(",");
                            for (String claim : claimedRaw) {
                                DifficultyAchievementDiary.EntryDifficulty.forString(claim).ifPresent(diff -> p.getDiaryManager().getWildernessDiary().claim(diff));
                            }
                        } else if (token.equals("diaries")) {
                            try {
                                String raw = token2;
                                String[] components = raw.split(",");
                                for (String comp : components) {
                                    if (comp.isEmpty()) {
                                        continue;
                                    }
                                    // Varrock
                                    Optional<VarrockDiaryEntry> varrock = VarrockDiaryEntry.fromName(comp);
                                    if (varrock.isPresent()) {
                                        p.getDiaryManager().getVarrockDiary().nonNotifyComplete(varrock.get());
                                    }
                                    // Ardougne
                                    Optional<ArdougneDiaryEntry> ardougne = ArdougneDiaryEntry.fromName(comp);
                                    if (ardougne.isPresent()) {
                                        p.getDiaryManager().getArdougneDiary().nonNotifyComplete(ardougne.get());
                                    }
                                    // Desert
                                    Optional<DesertDiaryEntry> desert = DesertDiaryEntry.fromName(comp);
                                    if (desert.isPresent()) {
                                        p.getDiaryManager().getDesertDiary().nonNotifyComplete(desert.get());
                                    }
                                    // Falador
                                    Optional<FaladorDiaryEntry> falador = FaladorDiaryEntry.fromName(comp);
                                    if (falador.isPresent()) {
                                        p.getDiaryManager().getFaladorDiary().nonNotifyComplete(falador.get());
                                    }
                                    // Fremennik
                                    Optional<FremennikDiaryEntry> fremennik = FremennikDiaryEntry.fromName(comp);
                                    if (fremennik.isPresent()) {
                                        p.getDiaryManager().getFremennikDiary().nonNotifyComplete(fremennik.get());
                                    }
                                    // Kandarin
                                    Optional<KandarinDiaryEntry> kandarin = KandarinDiaryEntry.fromName(comp);
                                    if (kandarin.isPresent()) {
                                        p.getDiaryManager().getKandarinDiary().nonNotifyComplete(kandarin.get());
                                    }
                                    // Karamja
                                    Optional<KaramjaDiaryEntry> karamja = KaramjaDiaryEntry.fromName(comp);
                                    if (karamja.isPresent()) {
                                        p.getDiaryManager().getKaramjaDiary().nonNotifyComplete(karamja.get());
                                    }
                                    // Lumbridge
                                    Optional<LumbridgeDraynorDiaryEntry> lumbridge = LumbridgeDraynorDiaryEntry.fromName(comp);
                                    if (lumbridge.isPresent()) {
                                        p.getDiaryManager().getLumbridgeDraynorDiary().nonNotifyComplete(lumbridge.get());
                                    }
                                    // Morytania
                                    Optional<MorytaniaDiaryEntry> morytania = MorytaniaDiaryEntry.fromName(comp);
                                    if (morytania.isPresent()) {
                                        p.getDiaryManager().getMorytaniaDiary().nonNotifyComplete(morytania.get());
                                    }
                                    // Western
                                    Optional<WesternDiaryEntry> western = WesternDiaryEntry.fromName(comp);
                                    if (western.isPresent()) {
                                        p.getDiaryManager().getWesternDiary().nonNotifyComplete(western.get());
                                    }
                                    // Wilderness
                                    Optional<WildernessDiaryEntry> wilderness = WildernessDiaryEntry.fromName(comp);
                                    if (wilderness.isPresent()) {
                                        p.getDiaryManager().getWildernessDiary().nonNotifyComplete(wilderness.get());
                                    }
                                }
                            } catch (Exception e) {
                                logger.error("Error while loading {}", playerName, e);
                                e.printStackTrace(System.err);
                            }
                        } else if (token.equals("partialDiaries")) {
                            String raw = token2;
                            String[] components = raw.split(",");
                            try {
                                for (String comp : components) {
                                    if (comp.isEmpty()) {
                                        continue;
                                    }
                                    String[] part = comp.split(":");
                                    int stage = Integer.parseInt(part[1]);
                                    //Varrock
                                    Optional<VarrockDiaryEntry> varrock = VarrockDiaryEntry.fromName(part[0]);
                                    if (varrock.isPresent()) {
                                        p.getDiaryManager().getVarrockDiary().setAchievementStage(varrock.get(), stage, false);
                                    }
                                    //Ardougne
                                    Optional<ArdougneDiaryEntry> ardougne = ArdougneDiaryEntry.fromName(part[0]);
                                    if (ardougne.isPresent()) {
                                        p.getDiaryManager().getArdougneDiary().setAchievementStage(ardougne.get(), stage, false);
                                    }
                                    //Desert
                                    Optional<DesertDiaryEntry> desert = DesertDiaryEntry.fromName(part[0]);
                                    if (desert.isPresent()) {
                                        p.getDiaryManager().getDesertDiary().setAchievementStage(desert.get(), stage, false);
                                    }
                                    //Falador
                                    Optional<FaladorDiaryEntry> falador = FaladorDiaryEntry.fromName(part[0]);
                                    if (falador.isPresent()) {
                                        p.getDiaryManager().getFaladorDiary().setAchievementStage(falador.get(), stage, false);
                                    }
                                    //Fremennik
                                    Optional<FremennikDiaryEntry> fremennik = FremennikDiaryEntry.fromName(part[0]);
                                    if (fremennik.isPresent()) {
                                        p.getDiaryManager().getFremennikDiary().setAchievementStage(fremennik.get(), stage, false);
                                    }
                                    //Kandarin
                                    Optional<KandarinDiaryEntry> kandarin = KandarinDiaryEntry.fromName(part[0]);
                                    if (kandarin.isPresent()) {
                                        p.getDiaryManager().getKandarinDiary().setAchievementStage(kandarin.get(), stage, false);
                                    }
                                    //Karamja
                                    Optional<KaramjaDiaryEntry> karamja = KaramjaDiaryEntry.fromName(part[0]);
                                    if (karamja.isPresent()) {
                                        p.getDiaryManager().getKaramjaDiary().setAchievementStage(karamja.get(), stage, false);
                                    }
                                    //Lumbridge
                                    Optional<LumbridgeDraynorDiaryEntry> lumbridge = LumbridgeDraynorDiaryEntry.fromName(part[0]);
                                    if (lumbridge.isPresent()) {
                                        p.getDiaryManager().getLumbridgeDraynorDiary().setAchievementStage(lumbridge.get(), stage, false);
                                    }
                                    //Morytania
                                    Optional<MorytaniaDiaryEntry> morytania = MorytaniaDiaryEntry.fromName(part[0]);
                                    if (morytania.isPresent()) {
                                        p.getDiaryManager().getMorytaniaDiary().setAchievementStage(morytania.get(), stage, false);
                                    }
                                    //Western
                                    Optional<WesternDiaryEntry> western = WesternDiaryEntry.fromName(part[0]);
                                    if (western.isPresent()) {
                                        p.getDiaryManager().getWesternDiary().setAchievementStage(western.get(), stage, false);
                                    }
                                    //Wilderness
                                    Optional<WildernessDiaryEntry> wilderness = WildernessDiaryEntry.fromName(part[0]);
                                    if (wilderness.isPresent()) {
                                        p.getDiaryManager().getWildernessDiary().setAchievementStage(wilderness.get(), stage, false);
                                    }
                                }
                            } catch (Exception e) {
                                logger.error("Error while loading {}", playerName, e);
                                e.printStackTrace(System.err);
                            }
                        } else if (token.equals("bonus-end")) {
                            p.bonusXpTime = Long.parseLong(token2);
                        } else if (token.equals("jail-end")) {
                            p.jailEnd = Long.parseLong(token2);
                        } else if (token.equals("mute-end")) {
                            p.muteEnd = Long.parseLong(token2);
                        } else if (token.equals("last-yell")) {
                            p.lastYell = Long.parseLong(token2);
                        } else if (token.equals("splitChat")) {
                            p.splitChat = Boolean.parseBoolean(token2);
                        } else if (token.equals("lastVote")) {
                            p.setLastVote(LocalDate.ofEpochDay(Long.parseLong(token2)));
                        } else if (token.equals("lastVotePanelPoint")) {
                            p.setLastVotePanelPoint(LocalDate.ofEpochDay(Long.parseLong(token2)));
                        } else if (token.equals("slayer-task")) {
                            Optional<Task> task = SlayerMaster.get(token2);
                            p.getSlayer().setTask(task);
                        } else if (token.equals("konar-slayer-location")) {
                            p.setKonarSlayerLocation(token2);
                        } else if (token.equals("last-task")) {
                            p.setLastTask(token2);
                        }else if (token.equals("run-toggled")) {
                            p.setRunningToggled(Boolean.parseBoolean(token2));
                        } else if (token.equals("slayer-master")) {
                            p.getSlayer().setMaster(Integer.parseInt(token2));
                        } else if (token.equals("konar-slayer-location")) {
                            p.setKonarSlayerLocation(token2);
                        } else if (token.equals("slayerPoints")) {
                            p.getSlayer().setPoints(Integer.parseInt(token2));
                        } else if (token.equals("slayer-task-amount")) {
                            p.getSlayer().setTaskAmount(Integer.parseInt(token2));
                        } else if (token.equals("consecutive-tasks")) {
                            p.getSlayer().setConsecutiveTasks(Integer.parseInt(token2));
                        } else if (token.equals("mage-arena-points")) {
                            p.setArenaPoints(Integer.parseInt(token2));
                        } else if (token.equals("shayzien-assault-points")) {
                            p.setShayPoints(Integer.parseInt(token2));
                        } else if (token.equals("flagged")) {
                            p.accountFlagged = Boolean.parseBoolean(token2);
                        } else if (token.equals("keepTitle")) {
                            p.keepTitle = Boolean.parseBoolean(token2);
                        } else if (token.equals("killTitle")) {
                            p.killTitle = Boolean.parseBoolean(token2);
                        } else if (token.equals("character-historyItems")) {
                            //System.err.println("Loading - Length of list="+token3.length+" saleSize="+p.historyItems.length);
                            for (int j = 0; j < token3.length; j++) {
                                p.historyItems[j] = Integer.parseInt(token3[j]);
                                p.saleItems.add(Integer.parseInt(token3[j]));
                            }
                        } else if (token.equals("character-historyItemsN")) {
                            for (int j = 0; j < token3.length; j++) {
                                p.historyItemsN[j] = Integer.parseInt(token3[j]);
                                p.saleAmount.add(Integer.parseInt(token3[j]));
                            }
                        } else if (token.equals("character-historyPrice")) {
                            for (int j = 0; j < token3.length; j++) {
                                p.historyPrice[j] = Integer.parseInt(token3[j]);
                                p.salePrice.add(Integer.parseInt(token3[j]));
                            }
                        } else if (token.equals(EventCalendar.SAVE_KEY)) {
                            if (token3.length >= 2) {
                                for (int index = 0; index < token3.length; index += 2) {
                                    EventChallengeKey key = EventChallengeKey.fromString(token3[index]);
                                    if (key != null) {
                                        int value = Integer.parseInt(token3[index + 1]);
                                        p.getEventCalendar().set(key, value);
                                    }
                                }
                            }
                        } else if (token.equals("removed-slayer-tasks")) {
                            String[] backing = Misc.nullToEmpty(p.getSlayer().getRemoved().length);
                            int index = 0;
                            for (; index < token3.length; index++) {
                                backing[index] = token3[index];
                            }
                            p.getSlayer().setRemoved(backing);
                        } else if (token.equals("slayer-unlocks")) {
                            for (int index = 0; index < token3.length; index++) {
                                try {
                                    SlayerUnlock unlock = SlayerUnlock.valueOf(token3[index]);
                                    if (unlock != null && !p.getSlayer().getUnlocks().contains(unlock)) {
                                        p.getSlayer().getUnlocks().add(unlock);
                                    }
                                } catch (Exception e) {
                                    logger.error("Error while loading {}", playerName, e);
                                    e.printStackTrace(System.err);
                                }
                            }
                        } else if (token.equals("extended-slayer-tasks")) {
                            for (int index = 0; index < token3.length; index++) {
                                try {
                                    TaskExtension extension = TaskExtension.valueOf(token3[index]);
                                    if (extension != null && !p.getSlayer().getExtensions().contains(extension)) {
                                        p.getSlayer().getExtensions().add(extension);
                                    }
                                } catch (Exception e) {
                                    logger.error("Error while loading {}", playerName, e);
                                    e.printStackTrace(System.err);
                                }
                            }
                        } else if (token.startsWith("removedTask")) {
                            int value = Integer.parseInt(token2);
                            if (value > -1) {
                                p.getSlayer().setPoints(p.getSlayer().getPoints() + 100);
                            }
                        } else if (token.equals("wave")) {
                            p.waveId = Integer.parseInt(token2);
                        } else if (token.equals("void")) {
                            for (int j = 0; j < token3.length; j++) {
                                p.voidStatus[j] = Integer.parseInt(token3[j]);
                            }
                        } else if (token.equals("pouch-rune")) {
                            for (int j = 0; j < token3.length; j++) {
                                p.setRuneEssencePouch(j, Integer.parseInt(token3[j]));
                            }
                        } else if (token.equals("pouch-pure")) {
                            for (int j = 0; j < token3.length; j++) {
                                p.setPureEssencePouch(j, Integer.parseInt(token3[j]));
                            }
                        } else if (token.equals("looting_bag_deposit_mode")) {
                            try {
                                LootingBag.LootingBagUseAction useAction = LootingBag.LootingBagUseAction.valueOf(token3[0]);
                                if (useAction != null) {
                                    p.getLootingBag().setUseAction(useAction);
                                }
                            } catch (Exception e) {
                                logger.error("Error while loading {}", playerName, e);
                                e.printStackTrace(System.err);
                            }
                        } else if (token.equals("privatechat")) {
                            p.setPrivateChat(Integer.parseInt(token2));
                        } else if (token.equals("inDistrict")) {
                            p.pkDistrict = Boolean.parseBoolean(token2);
                        } else if (token.equals("safeBoxSlots")) {
                            p.safeBoxSlots = Integer.parseInt(token2);
                        } else if (token.equals("district-levels")) {
                            for (int i = 0; i < p.playerStats.length; i++) p.playerStats[i] = Integer.parseInt(token3[i]);
                        } else if (token.equals("crawsbowCharge")) {
                            p.getPvpWeapons().setCrawsBowCharges(Integer.parseInt(token2));
                        } else if (token.equals("thammaronCharge")) {
                            p.getPvpWeapons().setThammaronSceptreCharges(Integer.parseInt(token2));
                        } else if (token.equals("viggoraCharge")) {
                            p.getPvpWeapons().setViggoraChainmaceCharges(Integer.parseInt(token2));
                        }

                        case 3:
                        if (token.equals("character-equip")) {
                            p.playerEquipment[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                            p.playerEquipmentN[Integer.parseInt(token3[0])] = Integer.parseInt(token3[2]);
                        }
                        break;
                    case 4: 
                        if (token.equals("character-look")) {
                            p.playerAppearance[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                        }
                        break;
                    case 5: 
                        if (token.equals("character-skill")) {
                            p.playerLevel[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                            p.playerXP[Integer.parseInt(token3[0])] = Integer.parseInt(token3[2]);
                            if (token3.length > 3) {
                                p.skillLock[Integer.parseInt(token3[0])] = Boolean.parseBoolean(token3[3]);
                                p.prestigeLevel[Integer.parseInt(token3[0])] = Integer.parseInt(token3[4]);
                            }
                        }
                        break;
                    case 6: 
                        if (token.equals("character-item")) {
                            p.playerItems[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                            p.playerItemsN[Integer.parseInt(token3[0])] = Integer.parseInt(token3[2]);
                        }
                        break;
                    case 46: 
                        if (token.equals("bag-item")) {
                            int id = Integer.parseInt(token3[1]);
                            int amt = Integer.parseInt(token3[2]);
                            p.getLootingBag().getLootingBagContainer().items.add(new LootingBagItem(id, amt));
                        }
                        break;
                    case 52: 
                        if (token.equals("item")) {
                            int itemId = Integer.parseInt(token3[0]);
                            int value = Integer.parseInt(token3[1]);
                            String date = token3[2];
                            p.getRechargeItems().loadItem(itemId, value, date);
                        }
                        break;
                    case 55: 
                        if (token.equals("pouch-item")) {
                            int id = Integer.parseInt(token3[1]);
                            int amt = Integer.parseInt(token3[2]);
                            p.getRunePouch().getItems().add(new GameItem(id, amt));
                        }
                        break;
                    case 56: 
                        if (token.equals("sack-item")) {
                            int id = Integer.parseInt(token3[1]);
                            int amt = Integer.parseInt(token3[2]);
                            p.getHerbSack().getItems().add(new GameItem(id, amt));
                        }
                        break;
                    case 57: 
                        if (token.equals("bag-item")) {
                            int id = Integer.parseInt(token3[1]);
                            int amt = Integer.parseInt(token3[2]);
                            p.getGemBag().getItems().add(new GameItem(id, amt));
                        }
                        break;
                    case 58: 
                        // Deprecated SafeBox items
                        break;
                    case 7: 
                        if (token.equals("bank-tab")) {
                            int tabId = Integer.parseInt(token3[0]);
                            int itemId = Integer.parseInt(token3[1]);
                            int itemAmount = Integer.parseInt(token3[2]);
                            p.getBank().getBankTab()[tabId].add(new BankItem(itemId, itemAmount));
                        }
                        break;
                    case 8: // Legacy
                        if (token.equals("character-friend")) {
                            try {
                                String name = Misc.convertLongToFixedName(Long.parseLong(token3[0]));
                                friends.add(new FriendsListEntry(FriendType.FRIEND, name, ""));
                            } catch (NumberFormatException e) {
                                logger.error("Error adding friend {} on {} friends list.", token3[0], p.getLoginName());
                            }
                        }
                        break;
                    case 12: // Legacy
                        if (token.equals("character-ignore")) {
                            try {
                                String name = Misc.convertLongToFixedName(Long.parseLong(token3[0]));
                                friends.add(new FriendsListEntry(FriendType.IGNORE, name, ""));
                            } catch (NumberFormatException e) {
                                logger.error("Error adding ignore {} on {} ignore list.", token3[0], p.getLoginName());
                            }
                        }
                        break;

                    // Achievements
                    case 9:
                    case 10:
                    case 11:
                    case 19:
                    case 20:
                        if (token3.length < 2) continue; // Legacy condition
                        AchievementTier tier = ReadMode == 9 ? AchievementTier.TIER_1
                                : ReadMode == 10 ? AchievementTier.TIER_2
                                : ReadMode == 11 ? AchievementTier.TIER_3
                                : ReadMode == 19 ? AchievementTier.TIER_4
                                : ReadMode == 20 ? AchievementTier.STARTER
                                : null;
                        if (tier == null)
                            throw new IllegalStateException("Unsupported achievement read mode: " + ReadMode);
                        p.getAchievements().readFromSave(token, token3, tier);
                        break;
                    case 14: 
                        if (token.equals("item")) {
                            p.degradableItem[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                        } else if (token.equals("claim-state")) {
                            for (int i = 0; i < token3.length; i++) {
                                p.claimDegradableItem[i] = Boolean.parseBoolean(token3[i]);
                            }
                        }
                        break;
                    case 16: 
                        try {
                            Killstreak.Type type = Killstreak.Type.get(token);
                            int value = Integer.parseInt(token2);
                            p.getKillstreak().getKillstreaks().put(type, value);
                        } catch (NullPointerException | NumberFormatException e) {
                            logger.error("Error while loading {}", playerName, e);
                            e.printStackTrace(System.err);
                        }
                        break;
                    case 17: 
                        try {
                            if (token2 != null && token2.length() > 0) {
                                Title title = Title.valueOf(token2);
                                if (title != null) {
                                    p.getTitles().getPurchasedList().add(title);
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Error while loading {}", playerName, e);
                            e.printStackTrace(System.err);
                        }
                        break;
                    case 18: 
                        if (token != null && token.length() > 0) {
                            p.getNpcDeathTracker().getTracker().put(token, Integer.parseInt(token2));
                        }
                        break;
                    }
                } else {
                    if (line.equals("[ACCOUNT]")) {
                        ReadMode = 1;
                    } else if (line.equals("[CHARACTER]")) {
                        ReadMode = 2;
                    } else if (line.equals("[EQUIPMENT]")) {
                        ReadMode = 3;
                    } else if (line.equals("[LOOK]")) {
                        ReadMode = 4;
                    } else if (line.equals("[SKILLS]")) {
                        ReadMode = 5;
                    } else if (line.equals("[ITEMS]")) {
                        ReadMode = 6;
                    } else if (line.equals("[LOOTBAG]")) {
                        ReadMode = 46;
                    } else if (line.equals("[RECHARGEITEMS]")) {
                        ReadMode = 52;
                    } else if (line.equals("[RUNEPOUCH]")) {
                        ReadMode = 55;
                    } else if (line.equals("[HERBSACK]")) {
                        ReadMode = 56;
                    } else if (line.equals("[GEMBAG]")) {
                        ReadMode = 57;
                    } else if (line.equals("[SAFEBOX]")) {
                        ReadMode = 58;
                    } else if (line.equals("[BANK]")) {
                        ReadMode = 7;
                    } else if (line.equals("[FRIENDS]")) { // Legacy
                        ReadMode = 8;
                    } else if (line.equals("[IGNORES]")) { // Legacy
                        ReadMode = 12;
                    } else if (line.equals("[ACHIEVEMENTS-TIER-1]")) {
                        ReadMode = 9;
                    } else if (line.equals("[ACHIEVEMENTS-TIER-2]")) {
                        ReadMode = 10;
                    } else if (line.equals("[ACHIEVEMENTS-TIER-3]")) {
                        ReadMode = 11;
                    } else if (line.equals("[HOLIDAY-EVENTS]")) {
                        ReadMode = 13;
                    } else if (line.equals("[DEGRADEABLES]")) {
                        ReadMode = 14;
                    } else if (line.equals("[PRESETS]")) {
                        ReadMode = 15;
                    } else if (line.equals("[KILLSTREAKS]")) {
                        ReadMode = 16;
                    } else if (line.equals("[TITLES]")) {
                        ReadMode = 17;
                    } else if (line.equals("[NPC-TRACKER]")) {
                        ReadMode = 18;
                    } else if (line.equals("[ACHIEVEMENTS-TIER-4]")) {
                        ReadMode = 19;
                    } else if (line.equals("[ACHIEVEMENTS-TIER-5]")) {
                        ReadMode = 20;
                    } else if (line.equals("[EOF]")) {
                        try {
                            characterfile.close();
                        } catch (IOException ioexception) {
                            logger.error("Error while loading {}", playerName, ioexception);
                            ioexception.printStackTrace();
                        }

                        p.getFriendsList().addFromSave(friends);
                        return LoadGameResult.SUCCESS;
                    }
                }
                line = characterfile.readLine();
            } catch (Exception e) {
                logger.error("Error while loading {} on line {}", playerName, line, e);
                e.printStackTrace(System.err);
                return LoadGameResult.ERROR_OCCURRED;
            }
        }
        try {
            characterfile.close();
        } catch (IOException ioexception) {
            logger.error("Error while loading {}", playerName, ioexception);
            ioexception.printStackTrace();
        }

        logger.error("Reached end of load method without reaching EOF, player logging in while save is executing or save file wiped, user={}", p);
        return LoadGameResult.ERROR_OCCURRED;
    }

    public static boolean saveGame(Player p) {
        new PlayerSaveExecutor(p).request();
        return true;
    }

    public static boolean saveGameInstant(Player p) {
        if (!p.saveCharacter) {
            return false;
        }
        if (p.getLoginName() == null || PlayerHandler.players[p.getIndex()] == null) {
            return false;
        }
        if (!p.isBot())
            logger.debug("Saving game for {}", p);
        Misc.createDirectory(getSaveDirectory());
        int tbTime = (int) (p.teleBlockStartMillis - System.currentTimeMillis() + p.teleBlockLength);
        if (tbTime > 300000 || tbTime < 0) {
            tbTime = 0;
        }
        try {
            p.getFarming().save();
        } catch (Exception e) {
            logger.error("Error while saving {}", p, e);
            e.printStackTrace(System.err);
        }
        BufferedWriter characterfile = null;
        try {
            characterfile = new BufferedWriter(new FileWriter(getSaveDirectory() + p.getLoginNameLower() + ".txt"));
            /* ACCOUNT */
            characterfile.write("[ACCOUNT]", 0, 9);
            characterfile.newLine();
            characterfile.write("character-username = ", 0, 21);
            characterfile.write(p.getLoginName(), 0, p.getLoginName().length());
            characterfile.newLine();

            characterfile.write("display-name = " + p.getDisplayName());
            characterfile.newLine();

            characterfile.write("character-password = ", 0, 21);
            String passToWrite = PasswordHashing.hash(p.playerPass);
            characterfile.write(passToWrite, 0, passToWrite.length());
            characterfile.newLine();
            characterfile.newLine();
            /* CHARACTER */
            characterfile.write("[CHARACTER]", 0, 11);
            characterfile.newLine();
            characterfile.write("character-rights = " + p.getRights().getPrimary().getValue());
            characterfile.newLine();
            StringBuilder sb = new StringBuilder();
            p.getRights().getSet().stream().forEach(r -> sb.append(r.getValue() + "\t"));
            characterfile.write("character-rights-secondary = " + sb.substring(0, sb.length() - 1));
            characterfile.newLine();
            characterfile.write("character-mac-address = " + p.getMacAddress());
            characterfile.newLine();
            characterfile.write("character-ip-address = " + p.getIpAddress());
            characterfile.newLine();
            characterfile.write("character-uuid = " + p.getUUID());
            characterfile.newLine();

            characterfile.write("migration-version = " + p.getMigrationVersion());
            characterfile.newLine();

            characterfile.write("revert-option = " + p.getRevertOption());
            characterfile.newLine();
            characterfile.write("dropBoostStart = " + p.dropBoostStart);
            characterfile.newLine();
            if (p.getRevertModeDelay() > 0) {
                characterfile.write("revert-delay = " + p.getRevertModeDelay());
                characterfile.newLine();
            }
            if (p.getMode() != null) {
                characterfile.write("mode = " + p.getMode().getType().name());
                characterfile.newLine();
            }
            characterfile.write("character-height = ", 0, 19);
            characterfile.write(Integer.toString(p.heightLevel), 0, Integer.toString(p.heightLevel).length());
            characterfile.newLine();
            characterfile.write("character-hp = " + p.getHealth().getCurrentHealth());
            characterfile.newLine();
            characterfile.write("play-time = ", 0, 12);
            characterfile.write(Integer.toString(p.playTime), 0, Integer.toString(p.playTime).length());
            characterfile.newLine();


            characterfile.write("last-clan = ", 0, 12);
            characterfile.write(p.getLastClanChat(), 0, p.getLastClanChat().length());
            characterfile.newLine();

            characterfile.write("require-pin-unlock = " + p.isRequiresPinUnlock());
            characterfile.newLine();

            characterfile.write("character-specRestore = ", 0, 24);
            characterfile.write(Integer.toString(p.specRestore), 0, Integer.toString(p.specRestore).length());
            characterfile.newLine();
            characterfile.write("character-posx = ", 0, 17);
            characterfile.write(Integer.toString(p.absX), 0, Integer.toString(p.absX).length());
            characterfile.newLine();
            characterfile.write("character-posy = ", 0, 17);
            characterfile.write(Integer.toString(p.absY), 0, Integer.toString(p.absY).length());
            characterfile.newLine();
            characterfile.write("bank-pin = " + p.getBankPin().getPin());
            characterfile.newLine();
            characterfile.write("bank-pin-cancellation = " + p.getBankPin().isAppendingCancellation());
            characterfile.newLine();
            characterfile.write("bank-pin-unlock-delay = " + p.getBankPin().getUnlockDelay());
            characterfile.newLine();
            characterfile.write("placeholders = " + p.placeHolders);
            characterfile.newLine();
            characterfile.write("bank-pin-cancellation-delay = " + p.getBankPin().getCancellationDelay());
            characterfile.newLine();
            characterfile.write("show-drop-warning = " + p.showDropWarning());
            characterfile.newLine();
            characterfile.write("show-alch-warning = " + p.isAlchWarning());
            characterfile.newLine();
            characterfile.write("hourly-box-toggle = " + p.getHourlyBoxToggle());
            characterfile.newLine();
            characterfile.write("fractured-crystal-toggle = " + p.getFracturedCrystalToggle());
            characterfile.newLine();
            characterfile.write("accept-aid = " + p.acceptAid);
            characterfile.newLine();
            characterfile.write("did-you-know = " + p.didYouKnow);
            characterfile.newLine();
            characterfile.write("spectating-tournament = " + p.spectatingTournament);
            characterfile.newLine();
            characterfile.write("lootvalue = " + p.lootValue);
            characterfile.newLine();
            characterfile.write("raidPoints = " + p.getRaidPoints());
            characterfile.newLine();
            characterfile.write("raidCount = " + p.raidCount);
            characterfile.newLine();
            characterfile.write("tobCompletions = " + p.tobCompletions);
            characterfile.newLine();
            characterfile.write("experience-counter = " + p.getExperienceCounter());
            characterfile.newLine();
            characterfile.write("character-title-updated = " + p.getTitles().getCurrentTitle());
            characterfile.newLine();

            characterfile.write("receivedVoteStreakRefund = " + p.isReceivedVoteStreakRefund());
            characterfile.newLine();

            // EventCalendar
            Set<Entry<EventChallengeKey, Integer>> eventCalendarProgress = p.getEventCalendar().getEntries();
            characterfile.write(EventCalendar.SAVE_KEY + " = ");
            for (Entry<EventChallengeKey, Integer> entry : eventCalendarProgress) {
                characterfile.write(EventChallengeKey.toSerializedString(entry.getKey()));
                characterfile.write("\t");
                characterfile.write(String.valueOf(entry.getValue()));
                characterfile.write("\t");
            }


            characterfile.newLine();
            String[] removed = p.getSlayer().getRemoved();
            characterfile.write("removed-slayer-tasks = ");
            for (int index = 0; index < removed.length; index++) {
                characterfile.write(removed[index]);
                if (index < removed.length - 1) {
                    characterfile.write("\t");
                }
            }
            characterfile.newLine();
            List<TaskExtension> extensions = p.getSlayer().getExtensions();
            if (!extensions.isEmpty()) {
                characterfile.write("extended-slayer-tasks = ");
                for (int index = 0; index < extensions.size(); index++) {
                    characterfile.write(extensions.get(index).toString());
                    if (index < extensions.size() - 1) {
                        characterfile.write("\t");
                    }
                }
            }
            characterfile.newLine();
            List<SlayerUnlock> unlocks = p.getSlayer().getUnlocks();
            if (!unlocks.isEmpty()) {
                characterfile.write("slayer-unlocks = ");
                for (int index = 0; index < unlocks.size(); index++) {
                    characterfile.write(unlocks.get(index).toString());
                    if (index < removed.length - 1) {
                        characterfile.write("\t");
                    }
                }
            }
            characterfile.newLine();
            characterfile.write("rfd-round = ", 0, 12);
            characterfile.write(Integer.toString(p.rfdRound), 0, Integer.toString(p.rfdRound).length());
            characterfile.newLine();
            characterfile.newLine();

            for (int i = 0; i < p.historyItems.length; i++) {
                if (p.saleItems.size() > 0)
                    p.historyItems[i] = p.saleItems.get(i).intValue();
            }
            characterfile.write("character-historyItems = ", 0, 25);
            String toWrite = "";
            for (int i1 = 0; i1 < p.historyItems.length; i1++) {
                toWrite += p.historyItems[i1] + "\t";
            }
            characterfile.write(toWrite);
            characterfile.newLine();
            for (int i = 0; i < p.historyItemsN.length; i++) {
                if (p.saleItems.size() > 0) p.historyItemsN[i] = p.saleAmount.get(i).intValue();
            }
            characterfile.write("character-historyItemsN = ", 0, 26);
            String toWrite2 = "";
            for (int i1 = 0; i1 < p.historyItemsN.length; i1++) {
                toWrite2 += p.historyItemsN[i1] + "\t";
            }
            characterfile.write(toWrite2);
            characterfile.newLine();
            for (int i = 0; i < p.historyPrice.length; i++) {
                if (p.salePrice.size() > 0) p.historyPrice[i] = p.salePrice.get(i).intValue();
            }
            characterfile.write("character-historyPrice = ", 0, 25);
            String toWrite3 = "";
            for (int i1 = 0; i1 < p.historyPrice.length; i1++) {
                toWrite3 += p.historyPrice[i1] + "\t";
            }
            characterfile.write(toWrite3);
            characterfile.newLine();
            characterfile.write("lastLoginDate = ", 0, 16);
            characterfile.write(Integer.toString(p.lastLoginDate), 0, Integer.toString(p.lastLoginDate).length());
            characterfile.newLine();
            characterfile.write("has-npc = ", 0, 10);
            characterfile.write(Boolean.toString(p.hasFollower), 0, Boolean.toString(p.hasFollower).length());
            characterfile.newLine();
            characterfile.write("summonId = ", 0, 11);
            characterfile.write(Integer.toString(p.petSummonId), 0, Integer.toString(p.petSummonId).length());
            characterfile.newLine();
            characterfile.write("startPack = " + p.isCompletedTutorial());
            characterfile.newLine();
            characterfile.write("unlockedUltimateChest = ", 0, 24);
            characterfile.write(Boolean.toString(p.unlockedUltimateChest), 0, Boolean.toString(p.unlockedUltimateChest).length());
            characterfile.newLine();
            characterfile.write("augury = ", 0, 9);
            characterfile.write(Boolean.toString(p.augury), 0, Boolean.toString(p.augury).length());
            characterfile.newLine();
            characterfile.write("rigour = ", 0, 9);
            characterfile.write(Boolean.toString(p.rigour), 0, Boolean.toString(p.rigour).length());
            characterfile.newLine();
            characterfile.write("crystalDrop = ", 0, 14);
            characterfile.write(Boolean.toString(p.crystalDrop), 0, Boolean.toString(p.crystalDrop).length());
            characterfile.newLine();
            characterfile.write("spawnedbarrows = ", 0, 17);
            characterfile.write(Boolean.toString(p.spawnedbarrows), 0, Boolean.toString(p.spawnedbarrows).length());
            characterfile.newLine();
            characterfile.write("collectCoins = ", 0, 15);
            characterfile.write(Boolean.toString(p.collectCoins), 0, Boolean.toString(p.collectCoins).length());
            characterfile.newLine();
            characterfile.write("printAttackStats = " + p.isPrintAttackStats());
            characterfile.newLine();
            characterfile.write("printDefenceStats = " + p.isPrintDefenceStats());
            characterfile.newLine();
            characterfile.write("absorption = ", 0, 13);
            characterfile.write(Boolean.toString(p.absorption), 0, Boolean.toString(p.absorption).length());
            characterfile.newLine();
            characterfile.write("announce = ", 0, 11);
            characterfile.write(Boolean.toString(p.announce), 0, Boolean.toString(p.announce).length());
            characterfile.newLine();
            characterfile.write("lootPickUp = ", 0, 13);
            characterfile.write(Boolean.toString(p.lootPickUp), 0, Boolean.toString(p.lootPickUp).length());
            characterfile.newLine();
            characterfile.write("breakVials = ", 0, 13);
            characterfile.write(Boolean.toString(p.breakVials), 0, Boolean.toString(p.breakVials).length());
            characterfile.newLine();
            characterfile.write("barbarian = ", 0, 12);
            characterfile.write(Boolean.toString(p.barbarian), 0, Boolean.toString(p.barbarian).length());
            characterfile.newLine();
            characterfile.write("membershipStartDate = ", 0, 22);
            characterfile.write(Integer.toString(p.startDate), 0, Integer.toString(p.startDate).length());
            characterfile.newLine();
            characterfile.write("XpScroll = ");
            characterfile.write(Boolean.toString(p.xpScroll));
            characterfile.newLine();
            characterfile.write("XpScrollTime = ");
            characterfile.write(Long.toString(p.xpScrollTicks));
            characterfile.newLine();
            characterfile.write("fasterClueScroll = ");
            characterfile.write(Boolean.toString(p.fasterCluesScroll));
            characterfile.newLine();
            characterfile.write("fasterClueScrollTime = ");
            characterfile.write(Long.toString(p.fasterCluesTicks));
            characterfile.newLine();
            characterfile.write("skillingPetRateScroll = ");
            characterfile.write(Boolean.toString(p.skillingPetRateScroll));
            characterfile.newLine();
            characterfile.write("skillingPetRateTime = ");
            characterfile.write(Long.toString(p.skillingPetRateTicks));
            characterfile.newLine();
            characterfile.write("activeMageArena2BossId  = ");
            for (int i = 0; i < p.activeMageArena2BossId.length; i++) characterfile.write("" + p.activeMageArena2BossId[i] + ((i == p.activeMageArena2BossId.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("mageArena2SpawnsX  = ");
            for (int i = 0; i < p.mageArena2SpawnsX.length; i++) characterfile.write("" + p.mageArena2SpawnsX[i] + ((i == p.mageArena2SpawnsX.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("mageArena2SpawnsY  = ");
            for (int i = 0; i < p.mageArena2SpawnsY.length; i++) characterfile.write("" + p.mageArena2SpawnsY[i] + ((i == p.mageArena2SpawnsY.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("mageArenaBossKills  = ");
            for (int i = 0; i < p.mageArenaBossKills.length; i++) characterfile.write("" + p.mageArenaBossKills[i] + ((i == p.mageArenaBossKills.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("mageArena2Stages  = ");
            for (int i = 0; i < p.mageArena2Stages.length; i++) characterfile.write("" + p.mageArena2Stages[i] + ((i == p.mageArena2Stages.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("flamesOfZamorakCasts  = ");
            characterfile.write(Integer.toString(p.flamesOfZamorakCasts ));
            characterfile.newLine();
            characterfile.write("clawsOfGuthixCasts  = ");
            characterfile.write(Integer.toString(p.clawsOfGuthixCasts ));
            characterfile.newLine();
            characterfile.write("saradominStrikeCasts  = ");
            characterfile.write(Integer.toString(p.saradominStrikeCasts ));
            characterfile.newLine();
            characterfile.write("exchangeP = ", 0, 12);
            characterfile.write(Integer.toString(p.exchangePoints), 0, Integer.toString(p.exchangePoints).length());
            characterfile.newLine();
            characterfile.write("totalEarnedExchangeP = ");
            characterfile.write(Integer.toString(p.totalEarnedExchangePoints));
            characterfile.newLine();
            characterfile.write("usedFc = ", 0, 9);
            characterfile.write(Boolean.toString(p.usedFc), 0, Boolean.toString(p.usedFc).length());
            characterfile.newLine();
            characterfile.write("setPin = ", 0, 9);
            characterfile.write(Boolean.toString(p.setPin), 0, Boolean.toString(p.setPin).length());
            characterfile.newLine();
            characterfile.write("bigger-boss-tasks = " + p.getSlayer().isBiggerBossTasks());
            characterfile.newLine();
            characterfile.write("cerberus-route = " + p.getSlayer().isCerberusRoute());
            characterfile.newLine();
            characterfile.write("slayer-tasks-completed = " + p.slayerTasksCompleted);
            characterfile.newLine();
            characterfile.write("claimedReward = ", 0, 16);
            characterfile.write(Boolean.toString(p.claimedReward), 0, Boolean.toString(p.claimedReward).length());
            characterfile.newLine();
            characterfile.write("dragonfire-shield-charge = " + p.getDragonfireShieldCharge());
            characterfile.newLine();
            characterfile.write("rfd-gloves = " + p.rfdGloves);
            characterfile.newLine();
            characterfile.write("wave-id = " + p.waveId);
            characterfile.newLine();
            characterfile.write("wave-type = " + p.fightCavesWaveType);
            characterfile.newLine();
            characterfile.write("wave-info = " + p.waveInfo[0] + "\t" + p.waveInfo[1] + "\t" + p.waveInfo[2]);
            characterfile.newLine();

            characterfile.write("help-cc-muted = " + p.isHelpCcMuted());
            characterfile.newLine();

            characterfile.write("gamble-banned = " + p.isGambleBanned());
            characterfile.newLine();

            characterfile.write("usedReferral = " + p.usedReferral);
            characterfile.newLine();
            characterfile.write("master-clue-reqs = " + p.masterClueRequirement[0] + "\t" + p.masterClueRequirement[1] + "\t" + p.masterClueRequirement[2] + "\t" + p.masterClueRequirement[3]);
            characterfile.newLine();
            characterfile.write("counters = ");
            for (int i = 0; i < p.counters.length; i++) characterfile.write("" + p.counters[i] + ((i == p.counters.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("max-cape = ");
            for (int i = 0; i < p.maxCape.length; i++) characterfile.write("" + p.maxCape[i] + ((i == p.maxCape.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("zulrah-best-time = " + p.getBestZulrahTime());
            characterfile.newLine();
            characterfile.write("toxic-staff = " + p.getToxicStaffOfTheDeadCharge());
            characterfile.newLine();
            characterfile.write("toxic-pipe-ammo = " + p.getToxicBlowpipeAmmo());
            characterfile.newLine();
            characterfile.write("toxic-pipe-amount = " + p.getToxicBlowpipeAmmoAmount());
            characterfile.newLine();
            characterfile.write("toxic-pipe-charge = " + p.getToxicBlowpipeCharge());
            characterfile.newLine();
            characterfile.write("serpentine-helm = " + p.getSerpentineHelmCharge());
            characterfile.newLine();
            characterfile.write("trident-of-the-seas = " + p.getTridentCharge());
            characterfile.newLine();
            characterfile.write("trident-of-the-swamp = " + p.getToxicTridentCharge());
            characterfile.newLine();
            characterfile.write("arclight-charge = " + p.getArcLightCharge());
            characterfile.newLine();
            characterfile.write("sang-staff-charge = " + p.getSangStaffCharge());
            characterfile.newLine();

            characterfile.write("bryophyta-charge = " + p.bryophytaStaffCharges);
            characterfile.newLine();

            characterfile.write("slayerPoints = " + p.getSlayer().getPoints());
            characterfile.newLine();
            characterfile.write("LastLoginYear = ", 0, 16);
            characterfile.write(Integer.toString(p.LastLoginYear), 0, Integer.toString(p.LastLoginYear).length());
            characterfile.newLine();
            characterfile.write("LastLoginMonth = ", 0, 17);
            characterfile.write(Integer.toString(p.LastLoginMonth), 0, Integer.toString(p.LastLoginMonth).length());
            characterfile.newLine();
            characterfile.write("LastLoginDate = ", 0, 16);
            characterfile.write(Integer.toString(p.LastLoginDate), 0, Integer.toString(p.LastLoginDate).length());
            characterfile.newLine();
            characterfile.write("LoginStreak = ", 0, 14);
            characterfile.write(Integer.toString(p.LoginStreak), 0, Integer.toString(p.LoginStreak).length());
            characterfile.newLine();
            characterfile.write("crystal-bow-shots = ", 0, 20);
            characterfile.write(Integer.toString(p.crystalBowArrowCount), 0, Integer.toString(p.crystalBowArrowCount).length());
            characterfile.newLine();
            characterfile.write("skull-timer = ", 0, 14);
            characterfile.write(Integer.toString(p.skullTimer), 0, Integer.toString(p.skullTimer).length());
            characterfile.newLine();
            characterfile.write("magic-book = ", 0, 13);
            characterfile.write(Integer.toString(p.playerMagicBook), 0, Integer.toString(p.playerMagicBook).length());
            characterfile.newLine();
            characterfile.write("special-amount = ", 0, 17);
            characterfile.write(Double.toString(p.specAmount), 0, Double.toString(p.specAmount).length());
            characterfile.newLine();
            characterfile.write("prayer-amount = " + Double.toString(p.prayerPoint));
            characterfile.newLine();
            characterfile.write("KC = ", 0, 4);
            characterfile.write(Integer.toString(p.killcount), 0, Integer.toString(p.killcount).length());
            characterfile.newLine();
            characterfile.write("DC = ", 0, 4);
            characterfile.write(Integer.toString(p.deathcount), 0, Integer.toString(p.deathcount).length());
            characterfile.newLine();
            characterfile.write("total-hunter-kills = " + p.getBH().getTotalHunterKills());
            characterfile.newLine();
            characterfile.write("total-rogue-kills = " + p.getBH().getTotalRogueKills());
            characterfile.newLine();
            characterfile.write("target-time-delay = " + p.getBH().getDelayedTargetTicks());
            characterfile.newLine();
            characterfile.write("bh-penalties = " + p.getBH().getWarnings());
            characterfile.newLine();
            characterfile.write("bh-bounties = " + p.getBH().getBounties());
            characterfile.newLine();
            characterfile.write("statistics-visible = " + p.getBH().isStatisticsVisible());
            characterfile.newLine();
            characterfile.write("spell-accessible = " + p.getBH().isSpellAccessible());
            characterfile.newLine();
            characterfile.write("zerkAmount = ", 0, 13);
            characterfile.newLine();
            characterfile.write("autoRet = ", 0, 10);
            characterfile.write(Integer.toString(p.autoRet), 0, Integer.toString(p.autoRet).length());
            characterfile.newLine();
            characterfile.write("pkp = ", 0, 6);
            characterfile.write(Integer.toString(p.pkp), 0, Integer.toString(p.pkp).length());
            characterfile.newLine();
            characterfile.write("elvenCharge = ", 0, 14);
            characterfile.write(Integer.toString(p.elvenCharge), 0, Integer.toString(p.elvenCharge).length());
            characterfile.newLine();
            characterfile.write("slaughterCharge = ", 0, 18);
            characterfile.write(Integer.toString(p.slaughterCharge), 0, Integer.toString(p.slaughterCharge).length());
            characterfile.newLine();
            characterfile.write("tomeOfFirePages = ");
            characterfile.write(Integer.toString(p.getTomeOfFire().getPages()));
            characterfile.newLine();
            characterfile.write("tomeOfFireCharges = ");
            characterfile.write(Integer.toString(p.getTomeOfFire().getCharges()));
            characterfile.newLine();
            characterfile.write("ether = ", 0, 7);
            characterfile.write(Integer.toString(p.braceletEtherCount), 0, Integer.toString(p.braceletEtherCount).length());
            characterfile.newLine();
            characterfile.write("crawsbowCharge = ");
            characterfile.write(Integer.toString(p.getPvpWeapons().getCrawsBowCharges()));
            characterfile.newLine();
            characterfile.write("thammaronCharge = ");
            characterfile.write(Integer.toString(p.getPvpWeapons().getThammaronSceptreCharges()));
            characterfile.newLine();
            characterfile.write("viggoraCharge = ");
            characterfile.write(Integer.toString(p.getPvpWeapons().getViggoraChainmaceCharges()));
            characterfile.newLine();

            characterfile.write("bossPoints = ");
            characterfile.write(Integer.toString(p.bossPoints));
            characterfile.newLine();
            characterfile.write("bossPointsRefund = ");
            characterfile.write(Boolean.toString(p.bossPointsRefund));
            characterfile.newLine();


            characterfile.write("tWin = " + p.tournamentWins);
            characterfile.newLine();

            characterfile.write("tPoint = " + p.tournamentPoints);
            characterfile.newLine();

            characterfile.write("streak = " + p.streak);
            characterfile.newLine();

            characterfile.write("outlastKills = " + p.outlastKills);
            characterfile.newLine();

            characterfile.write("outlastDeaths = " + p.outlastDeaths);
            characterfile.newLine();

            characterfile.write("tournamentTotalGames = " + p.tournamentTotalGames);
            characterfile.newLine();


            characterfile.write("xpMaxSkills = ", 0, 14);
            characterfile.write(Integer.toString(p.xpMaxSkills), 0, Integer.toString(p.xpMaxSkills).length());
            characterfile.newLine();
            characterfile.write("RefU = ", 0, 6);
            characterfile.write(Integer.toString(p.referallFlag), 0, Integer.toString(p.referallFlag).length());
            characterfile.newLine();
            characterfile.write("LoyP = ", 0, 6);
            characterfile.write(Integer.toString(p.loyaltyPoints), 0, Integer.toString(p.loyaltyPoints).length());
            characterfile.newLine();
            characterfile.write("dayv = ", 0, 6);
            characterfile.write(Integer.toString(p.voteKeyPoints), 0, Integer.toString(p.voteKeyPoints).length());
            characterfile.newLine();
            characterfile.write("donP = ", 0, 6);
            characterfile.write(Integer.toString(p.donatorPoints), 0, Integer.toString(p.donatorPoints).length());
            characterfile.newLine();
            characterfile.write("donA = ", 0, 6);
            characterfile.write(Integer.toString(p.amDonated), 0, Integer.toString(p.amDonated).length());
            characterfile.newLine();
            characterfile.write("prestige-points = ", 0, 18);
            characterfile.write(Integer.toString(p.prestigePoints), 0, Integer.toString(p.prestigePoints).length());
            characterfile.newLine();
            characterfile.write("votePoints = ", 0, 13);
            characterfile.write(Integer.toString(p.votePoints), 0, Integer.toString(p.votePoints).length());
            characterfile.newLine();
            characterfile.write("bloodPoints = ", 0, 14);
            characterfile.write(Integer.toString(p.bloodPoints), 0, Integer.toString(p.bloodPoints).length());
            characterfile.newLine();
            characterfile.write("d1Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d1Complete), 0, Boolean.toString(p.d1Complete).length());
            characterfile.newLine();
            characterfile.write("d2Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d2Complete), 0, Boolean.toString(p.d2Complete).length());
            characterfile.newLine();
            characterfile.write("d3Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d3Complete), 0, Boolean.toString(p.d3Complete).length());
            characterfile.newLine();
            characterfile.write("d4Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d4Complete), 0, Boolean.toString(p.d4Complete).length());
            characterfile.newLine();
            characterfile.write("d5Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d5Complete), 0, Boolean.toString(p.d5Complete).length());
            characterfile.newLine();
            characterfile.write("d6Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d6Complete), 0, Boolean.toString(p.d6Complete).length());
            characterfile.newLine();
            characterfile.write("d7Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d7Complete), 0, Boolean.toString(p.d7Complete).length());
            characterfile.newLine();
            characterfile.write("d8Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d8Complete), 0, Boolean.toString(p.d8Complete).length());
            characterfile.newLine();
            characterfile.write("d9Complete = ", 0, 13);
            characterfile.write(Boolean.toString(p.d9Complete), 0, Boolean.toString(p.d9Complete).length());
            characterfile.newLine();
            characterfile.write("d10Complete = ", 0, 14);
            characterfile.write(Boolean.toString(p.d10Complete), 0, Boolean.toString(p.d10Complete).length());
            characterfile.newLine();
            characterfile.write("d11Complete = ", 0, 14);
            characterfile.write(Boolean.toString(p.d11Complete), 0, Boolean.toString(p.d11Complete).length());
            characterfile.newLine();
            characterfile.write("achievement-points = " + p.getAchievements().getPoints());
            characterfile.newLine();
            characterfile.write("xpLock = ", 0, 9);
            characterfile.write(Boolean.toString(p.expLock), 0, Boolean.toString(p.expLock).length());
            characterfile.newLine();
            characterfile.write("teleblock-length = ", 0, 19);
            characterfile.write(Integer.toString(tbTime), 0, Integer.toString(tbTime).length());
            characterfile.newLine();
            //Varrock
            String varrockClaimed = "VarrockClaimedDiaries = ";
            characterfile.write(varrockClaimed, 0, varrockClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getVarrockDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Ardougne
            String ardougneClaimed = "ArdougneClaimedDiaries = ";
            characterfile.write(ardougneClaimed, 0, ardougneClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getArdougneDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Desert
            String desertClaimed = "DesertClaimedDiaries = ";
            characterfile.write(desertClaimed, 0, desertClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getDesertDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Falador
            String faladorClaimed = "FaladorClaimedDiaries = ";
            characterfile.write(faladorClaimed, 0, faladorClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getFaladorDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Fremennik
            String fremennikClaimed = "FremennikClaimedDiaries = ";
            characterfile.write(fremennikClaimed, 0, fremennikClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getFremennikDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Kandarin
            String kandarinClaimed = "KandarinClaimedDiaries = ";
            characterfile.write(kandarinClaimed, 0, kandarinClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getKandarinDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Karamja
            String karamjaClaimed = "KaramjaClaimedDiaries = ";
            characterfile.write(karamjaClaimed, 0, karamjaClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getKaramjaDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Lumbridge
            String lumbridgeClaimed = "LumbridgeClaimedDiaries = ";
            characterfile.write(lumbridgeClaimed, 0, lumbridgeClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getLumbridgeDraynorDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Morytania
            String morytaniaClaimed = "MorytaniaClaimedDiaries = ";
            characterfile.write(morytaniaClaimed, 0, morytaniaClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getMorytaniaDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Western
            String westernClaimed = "WesternClaimedDiaries = ";
            characterfile.write(westernClaimed, 0, westernClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getWesternDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            //Wilderness
            String wildernessClaimed = "WildernessClaimedDiaries = ";
            characterfile.write(wildernessClaimed, 0, wildernessClaimed.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                for (DifficultyAchievementDiary.EntryDifficulty entry : p.getDiaryManager().getWildernessDiary().getClaimed()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            String diary = "diaries = ";
            characterfile.write(diary, 0, diary.length());
            {
                String prefix = "";
                StringBuilder bldr = new StringBuilder();
                // Varrock
                for (VarrockDiaryEntry entry : p.getDiaryManager().getVarrockDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Ardougne
                for (ArdougneDiaryEntry entry : p.getDiaryManager().getArdougneDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Desert
                for (DesertDiaryEntry entry : p.getDiaryManager().getDesertDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Falador
                for (FaladorDiaryEntry entry : p.getDiaryManager().getFaladorDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Fremennik
                for (FremennikDiaryEntry entry : p.getDiaryManager().getFremennikDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Kandarin
                for (KandarinDiaryEntry entry : p.getDiaryManager().getKandarinDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Karamja
                for (KaramjaDiaryEntry entry : p.getDiaryManager().getKaramjaDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Lumbridge
                for (LumbridgeDraynorDiaryEntry entry : p.getDiaryManager().getLumbridgeDraynorDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Morytania
                for (MorytaniaDiaryEntry entry : p.getDiaryManager().getMorytaniaDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Western
                for (WesternDiaryEntry entry : p.getDiaryManager().getWesternDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                // Wilderness
                for (WildernessDiaryEntry entry : p.getDiaryManager().getWildernessDiary().getAchievements()) {
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name());
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            String partialDiary = "partialDiaries = ";
            //forEachPartial
            characterfile.write(partialDiary, 0, partialDiary.length()); //Saw that earlier but forgot lol, ahh ty
            {
                StringBuilder bldr = new StringBuilder();
                String prefix = "";
                //Varrock
                for (Entry<VarrockDiaryEntry, Integer> keyval : p.getDiaryManager().getVarrockDiary().getPartialAchievements().entrySet()) {
                    VarrockDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Ardougne
                for (Entry<ArdougneDiaryEntry, Integer> keyval : p.getDiaryManager().getArdougneDiary().getPartialAchievements().entrySet()) {
                    ArdougneDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Desert
                for (Entry<DesertDiaryEntry, Integer> keyval : p.getDiaryManager().getDesertDiary().getPartialAchievements().entrySet()) {
                    DesertDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Falador
                for (Entry<FaladorDiaryEntry, Integer> keyval : p.getDiaryManager().getFaladorDiary().getPartialAchievements().entrySet()) {
                    FaladorDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Fremennik
                for (Entry<FremennikDiaryEntry, Integer> keyval : p.getDiaryManager().getFremennikDiary().getPartialAchievements().entrySet()) {
                    FremennikDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Kandarin
                for (Entry<KandarinDiaryEntry, Integer> keyval : p.getDiaryManager().getKandarinDiary().getPartialAchievements().entrySet()) {
                    KandarinDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Karamja
                for (Entry<KaramjaDiaryEntry, Integer> keyval : p.getDiaryManager().getKaramjaDiary().getPartialAchievements().entrySet()) {
                    KaramjaDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Lumbridge
                for (Entry<LumbridgeDraynorDiaryEntry, Integer> keyval : p.getDiaryManager().getLumbridgeDraynorDiary().getPartialAchievements().entrySet()) {
                    LumbridgeDraynorDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Morytania
                for (Entry<MorytaniaDiaryEntry, Integer> keyval : p.getDiaryManager().getMorytaniaDiary().getPartialAchievements().entrySet()) {
                    MorytaniaDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Western
                for (Entry<WesternDiaryEntry, Integer> keyval : p.getDiaryManager().getWesternDiary().getPartialAchievements().entrySet()) {
                    WesternDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                //Wilderness
                for (Entry<WildernessDiaryEntry, Integer> keyval : p.getDiaryManager().getWildernessDiary().getPartialAchievements().entrySet()) {
                    WildernessDiaryEntry entry = keyval.getKey();
                    int stage = keyval.getValue();
                    bldr.append(prefix);
                    prefix = ",";
                    bldr.append(entry.name() + ":" + stage);
                }
                characterfile.write(bldr.toString(), 0, bldr.toString().length());
            }
            characterfile.newLine();
            characterfile.write("pc-points = ", 0, 12);
            characterfile.write(Integer.toString(p.pcPoints), 0, Integer.toString(p.pcPoints).length());
            characterfile.newLine();
            characterfile.write("total-raids = ", 0, 14);
            characterfile.write(Integer.toString(p.totalRaidsFinished), 0, Integer.toString(p.totalRaidsFinished).length());
            characterfile.newLine();
            characterfile.write("killStreak = ", 0, 13);
            characterfile.write(Integer.toString(p.killStreak), 0, Integer.toString(p.killStreak).length());
            characterfile.newLine();
            characterfile.write("bonus-end = ", 0, 12);
            characterfile.write(Long.toString(p.bonusXpTime), 0, Long.toString(p.bonusXpTime).length());
            characterfile.newLine();
            characterfile.write("jail-end = ", 0, 11);
            characterfile.write(Long.toString(p.jailEnd), 0, Long.toString(p.jailEnd).length());
            characterfile.newLine();
            characterfile.write("mute-end = ", 0, 11);
            characterfile.write(Long.toString(p.muteEnd), 0, Long.toString(p.muteEnd).length());
            characterfile.newLine();
            characterfile.write("last-yell = " + p.lastYell);
            characterfile.newLine();
            characterfile.write("splitChat = ", 0, 12);
            characterfile.write(Boolean.toString(p.splitChat), 0, Boolean.toString(p.splitChat).length());
            characterfile.newLine();
            characterfile.write("lastVote = " + p.getLastVote().toEpochDay());
            characterfile.newLine();
            characterfile.write("lastVotePanelPoint = " + p.getLastVotePanelPoint().toEpochDay());
            characterfile.newLine();

            if (p.getSlayer().getTask().isPresent()) {
                Task task = p.getSlayer().getTask().get();
                characterfile.write("slayer-task = " + task.getPrimaryName());
                characterfile.newLine();
                characterfile.write("slayer-task-amount = " + p.getSlayer().getTaskAmount());
                characterfile.newLine();
            }
            characterfile.write("last-task = " + p.lastTask);
            characterfile.newLine();
            characterfile.write("run-toggled = " + p.isRunningToggled());
            characterfile.newLine();
            characterfile.write("slayer-master = " + p.getSlayer().getMaster());
            characterfile.newLine();
            characterfile.write("konar-slayer-location = " + p.getKonarSlayerLocation());
            characterfile.newLine();
            characterfile.write("consecutive-tasks = " + p.getSlayer().getConsecutiveTasks());
            characterfile.newLine();
            characterfile.write("mage-arena-points = " + p.getArenaPoints());
            characterfile.newLine();
            characterfile.write("shayzien-assault-points = " + p.getShayPoints());
            characterfile.newLine();
            characterfile.write("flagged = ", 0, 10);
            characterfile.write(Boolean.toString(p.accountFlagged), 0, Boolean.toString(p.accountFlagged).length());
            characterfile.newLine();
            characterfile.write("keepTitle = ", 0, 12);
            characterfile.write(Boolean.toString(p.keepTitle), 0, Boolean.toString(p.keepTitle).length());
            characterfile.newLine();
            characterfile.write("killTitle = ", 0, 12);
            characterfile.write(Boolean.toString(p.killTitle), 0, Boolean.toString(p.killTitle).length());
            characterfile.newLine();
            characterfile.write("wave = ", 0, 7);
            characterfile.write(Integer.toString(p.waveId), 0, Integer.toString(p.waveId).length());
            characterfile.newLine();
            characterfile.write("privatechat = ", 0, 14);
            characterfile.write(Integer.toString(p.getPrivateChat()), 0, Integer.toString(p.getPrivateChat()).length());
            characterfile.newLine();
            characterfile.write("void = ", 0, 7);
            String toWrite55 = p.voidStatus[0] + "\t" + p.voidStatus[1] + "\t" + p.voidStatus[2] + "\t" + p.voidStatus[3] + "\t" + p.voidStatus[4];
            characterfile.write(toWrite55);
            characterfile.newLine();
            characterfile.write("quickprayer = ", 0, 14);
            String quick = "";
            for (int i = 0; i < p.getQuick().getNormal().length; i++) {
                quick += p.getQuick().getNormal()[i] + "\t";
            }
            characterfile.write(quick);
            characterfile.newLine();
            characterfile.write("pouch-rune = " + p.getRuneEssencePouch(0) + "\t" + p.getRuneEssencePouch(1) + "\t" + p.getRuneEssencePouch(2));
            characterfile.newLine();
            characterfile.write("pouch-pure = " + p.getPureEssencePouch(0) + "\t" + p.getPureEssencePouch(1) + "\t" + p.getPureEssencePouch(2));
            characterfile.newLine();
            // Looting bag deposit mode
            characterfile.write("looting_bag_deposit_mode = " + p.getLootingBag().getUseAction());
            characterfile.newLine();
            characterfile.write("district-levels = ");
            for (int i = 0; i < p.playerStats.length; i++) characterfile.write("" + p.playerStats[i] + ((i == p.playerStats.length - 1) ? "" : "\t"));
            characterfile.newLine();
            characterfile.write("inDistrict = ", 0, 13);
            characterfile.write(Boolean.toString(p.pkDistrict), 0, Boolean.toString(p.pkDistrict).length());
            characterfile.newLine();
            characterfile.write("safeBoxSlots = ", 0, 15);
            characterfile.write(Integer.toString(p.safeBoxSlots), 0, Integer.toString(p.safeBoxSlots).length());
            characterfile.newLine();

            // Add new stuff below this line
            characterfile.write("serpHelmCombatTicks = ");
            characterfile.write(Long.toString(p.serpHelmCombatTicks));
            characterfile.newLine();
            characterfile.write("gargoyleStairsUnlocked = ");
            characterfile.write(Boolean.toString(p.gargoyleStairsUnlocked));
            characterfile.newLine();
            characterfile.write("firstAchievementLoginJune2021 = ");
            characterfile.write(Boolean.toString(p.getAchievements().isFirstAchievementLoginJune2021()));
            characterfile.newLine();
            characterfile.write("controller = ");
            characterfile.write(p.getController().getKey());
            characterfile.newLine();
            characterfile.write("joinedIronmanGroup = ");
            characterfile.write(p.isJoinedIronmanGroup() + "");
            characterfile.newLine();
            characterfile.write("receivedCalendarCosmeticJune2021 = ");
            characterfile.write(p.isReceivedCalendarCosmeticJune2021() + "");
            characterfile.newLine();

            // Don't add new stuff below this line

            for (PlayerSaveEntry entry : playerSaveEntryList) {
                for (String key : entry.getKeys(p)) {
                    try {
                        String encoded = entry.encode(p, key);
                        if (encoded != null) {
                            characterfile.write(key + " = " + entry.encode(p, key));
                            characterfile.newLine();
                        }
                    } catch (Exception e) {
                        logger.error("Error while saving player save entry class={}, key={}, player={}", entry.getClass(), key, p, e);
                        e.printStackTrace(System.err);
                    }
                }
            }
            characterfile.newLine();
            /* EQUIPMENT */
            characterfile.write("[EQUIPMENT]", 0, 11);
            characterfile.newLine();
            for (int i = 0; i < p.playerEquipment.length; i++) {
                characterfile.write("character-equip = ", 0, 18);
                characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(p.playerEquipment[i]), 0, Integer.toString(p.playerEquipment[i]).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(p.playerEquipmentN[i]), 0, Integer.toString(p.playerEquipmentN[i]).length());
                characterfile.write("\t", 0, 1);
                characterfile.newLine();
            }
            characterfile.newLine();
            /* LOOK */
            characterfile.write("[LOOK]", 0, 6);
            characterfile.newLine();
            for (int i = 0; i < p.playerAppearance.length; i++) {
                characterfile.write("character-look = ", 0, 17);
                characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(p.playerAppearance[i]), 0, Integer.toString(p.playerAppearance[i]).length());
                characterfile.newLine();
            }
            characterfile.newLine();
            /* SKILLS */
            characterfile.write("[SKILLS]", 0, 8);
            characterfile.newLine();
            for (int i = 0; i < p.playerLevel.length; i++) {
                characterfile.write("character-skill = ", 0, 18);
                characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(p.playerLevel[i]), 0, Integer.toString(p.playerLevel[i]).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(p.playerXP[i]), 0, Integer.toString(p.playerXP[i]).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Boolean.toString(p.skillLock[i]), 0, Boolean.toString(p.skillLock[i]).length());
                characterfile.write("\t", 0, 1);
                characterfile.write(Integer.toString(p.prestigeLevel[i]), 0, Integer.toString(p.prestigeLevel[i]).length());
                characterfile.newLine();
            }
            characterfile.newLine();
            /* ITEMS */
            characterfile.write("[ITEMS]", 0, 7);
            characterfile.newLine();
            for (int i = 0; i < p.playerItems.length; i++) {
                if (p.playerItems[i] > 0) {
                    characterfile.write("character-item = ", 0, 17);
                    characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                    characterfile.write("\t", 0, 1);
                    characterfile.write(Integer.toString(p.playerItems[i]), 0, Integer.toString(p.playerItems[i]).length());
                    characterfile.write("\t", 0, 1);
                    characterfile.write(Integer.toString(p.playerItemsN[i]), 0, Integer.toString(p.playerItemsN[i]).length());
                    characterfile.newLine();
                }
            }
            characterfile.newLine();
            /* ITEMVALUES */
            characterfile.write("[RECHARGEITEMS]", 0, 15);
            characterfile.newLine();
            for (int itemId : p.getRechargeItems().getItemValues().keySet()) {
                int value = p.getRechargeItems().getChargesLeft(itemId);
                String itemIdString = Integer.toString(itemId);
                String valueString = Integer.toString(value);
                String lastUsed = p.getRechargeItems().getItemLastUsed(itemId);
                characterfile.write("item = ", 0, 7);
                characterfile.write("\t", 0, 1);
                characterfile.write(itemIdString, 0, itemIdString.length());
                characterfile.write("\t", 0, 1);
                characterfile.write(valueString, 0, valueString.length());
                characterfile.write("\t", 0, 1);
                characterfile.write(lastUsed, 0, lastUsed.length());
                characterfile.newLine();
            }
            characterfile.newLine();
            /* BANK */
            characterfile.write("[BANK]", 0, 6);
            characterfile.newLine();
            for (int bankTabIndex = 0; bankTabIndex < p.getBank().getBankTab().length; bankTabIndex++) {
                BankTab bankTab = p.getBank().getBankTab()[bankTabIndex];
                for (int index = 0; index < bankTab.getItems().size(); index++) {
                    BankItem item = bankTab.getItems().get(index);
                    if (item != null) {
                        characterfile.write("bank-tab = " + bankTabIndex + "\t" + item.id() + "\t" + item.amount());
                        characterfile.newLine();
                    }
                }
            }
            characterfile.newLine();
            characterfile.newLine();
            /* LOOTBAG */
            characterfile.write("[LOOTBAG]", 0, 9);
            characterfile.newLine();
            for (int i = 0; i < p.getLootingBag().getLootingBagContainer().items.size(); i++) {
                if (p.getLootingBag().getLootingBagContainer().items.get(i).id() > 0) {
                    characterfile.write("bag-item = ", 0, 11);
                    characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                    characterfile.write("\t", 0, 1);
                    int id = p.getLootingBag().getLootingBagContainer().items.get(i).id();
                    int amt = p.getLootingBag().getLootingBagContainer().items.get(i).amount();
                    characterfile.write(Integer.toString(id), 0, Integer.toString(id).length());
                    characterfile.write("\t", 0, 1);
                    characterfile.write(Integer.toString(amt), 0, Integer.toString(amt).length());
                    characterfile.newLine();
                }
            }
            characterfile.newLine();
            /* RUNEPOUCH */
            characterfile.write("[RUNEPOUCH]", 0, 11);
            characterfile.newLine();
            for (int i = 0; i < p.getRunePouch().getItems().size(); i++) {
                if (p.getRunePouch().getItems().get(i).id() > 0) {
                    characterfile.write("pouch-item = ", 0, 13);
                    characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                    characterfile.write("\t", 0, 1);
                    int id = p.getRunePouch().getItems().get(i).id();
                    int amt = p.getRunePouch().getItems().get(i).amount();
                    characterfile.write(Integer.toString(id), 0, Integer.toString(id).length());
                    characterfile.write("\t", 0, 1);
                    characterfile.write(Integer.toString(amt), 0, Integer.toString(amt).length());
                    characterfile.newLine();
                }
            }
            characterfile.newLine();
            /* HERBSACK */
            characterfile.write("[HERBSACK]", 0, 10);
            characterfile.newLine();
            for (int i = 0; i < p.getHerbSack().getItems().size(); i++) {
                if (p.getHerbSack().getItems().get(i).id() > 0) {
                    characterfile.write("sack-item = ", 0, 12);
                    characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                    characterfile.write("\t", 0, 1);
                    int id = p.getHerbSack().getItems().get(i).id();
                    int amt = p.getHerbSack().getItems().get(i).amount();
                    characterfile.write(Integer.toString(id), 0, Integer.toString(id).length());
                    characterfile.write("\t", 0, 1);
                    characterfile.write(Integer.toString(amt), 0, Integer.toString(amt).length());
                    characterfile.newLine();
                }
            }
            characterfile.newLine();
            /* GEMBAG */
            characterfile.write("[GEMBAG]", 0, 8);
            characterfile.newLine();
            for (int i = 0; i < p.getGemBag().getItems().size(); i++) {
                if (p.getGemBag().getItems().get(i).id() > 0) {
                    characterfile.write("bag-item = ", 0, 11);
                    characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
                    characterfile.write("\t", 0, 1);
                    int id = p.getGemBag().getItems().get(i).id();
                    int amt = p.getGemBag().getItems().get(i).amount();
                    characterfile.write(Integer.toString(id), 0, Integer.toString(id).length());
                    characterfile.write("\t", 0, 1);
                    characterfile.write(Integer.toString(amt), 0, Integer.toString(amt).length());
                    characterfile.newLine();
                }
            }
            characterfile.newLine();

            characterfile.write("[DEGRADEABLES]");
            characterfile.newLine();
            characterfile.write("claim-state = ");
            for (int i = 0; i < p.claimDegradableItem.length; i++) {
                characterfile.write(Boolean.toString(p.claimDegradableItem[i]));
                if (i != p.claimDegradableItem.length - 1) {
                    characterfile.write("\t");
                }
            }
            characterfile.newLine();
            for (int i = 0; i < p.degradableItem.length; i++) {
                if (p.degradableItem[i] > 0) {
                    characterfile.write("item = " + i + "\t" + p.degradableItem[i]);
                    characterfile.newLine();
                }
            }
            characterfile.newLine();
            characterfile.newLine();

            // Achievement tiers
            for (AchievementTier tier : AchievementTier.values()) {
                characterfile.write("[ACHIEVEMENTS-TIER-" + (tier.getId() + 1) + "]");
                characterfile.newLine();
                p.getAchievements().print(characterfile, tier.getId());
                characterfile.newLine();
                characterfile.newLine();
            }


            characterfile.write("[PRESETS]");
            characterfile.newLine();
            characterfile.write("Names = ");
            characterfile.newLine();
            characterfile.write("[KILLSTREAKS]");
            characterfile.newLine();
            for (Entry<Killstreak.Type, Integer> entry : p.getKillstreak().getKillstreaks().entrySet()) {
                characterfile.write(entry.getKey().name() + " = " + entry.getValue());
                characterfile.newLine();
            }
            characterfile.newLine();
            characterfile.write("[TITLES]");
            characterfile.newLine();
            for (Title title : p.getTitles().getPurchasedList()) {
                characterfile.write("title = " + title.name());
                characterfile.newLine();
            }
            characterfile.newLine();
            characterfile.write("[NPC-TRACKER]");
            characterfile.newLine();
            for (Entry<String, Integer> entry : p.getNpcDeathTracker().getTracker().entrySet()) {
                if (entry != null) {
                    if (entry.getValue() > 0) {
                        characterfile.write(entry.getKey() + " = " + entry.getValue());
                        characterfile.newLine();
                    }
                }
            }
            characterfile.write("[EOF]", 0, 5);
            characterfile.newLine();
            characterfile.newLine();
            characterfile.close();
        } catch (Exception ioexception) {
            logger.error("Error while saving player {}", p, ioexception);
            ioexception.printStackTrace();
            return false;
        }
        return true;
    }
}