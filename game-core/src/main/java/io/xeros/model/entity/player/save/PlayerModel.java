package io.xeros.model.entity.player.save;//package io.xeros.model.entity.player.save;
//
//import io.xeros.content.achievement.AchievementTier;
//import io.xeros.content.achievement_diary.DifficultyAchievementDiary;
//import io.xeros.content.achievement_diary.impl.*;
//import io.xeros.content.combat.pvp.Killstreak;
//import io.xeros.content.events.eventcalendar.EventChallengeKey;
//import io.xeros.content.lootbag.LootingBagItem;
//import io.xeros.content.skills.slayer.SlayerUnlock;
//import io.xeros.content.skills.slayer.TaskExtension;
//import io.xeros.content.titles.Title;
//import io.xeros.model.items.GameItem;
//import io.xeros.model.items.bank.BankTab;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//
///**
// * @author Glabay | Glabay-Studios
// * @project Glabay-Studios-Server
// * @social Discord: Glabay
// * @since 2024-11-24
// */
//@Getter
//@Entity(name = "player")
//@Table(name = "PLAYER_MODEL")
//public class PlayerModel {
//    @Id
//    @SequenceGenerator(
//        name = "player_uuid_sequence",
//        sequenceName = "player_uuid_sequence",
//        allocationSize = 1
//    )
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "player_uuid_sequence")
//    private Long playerId;
//
//    private String uuid;
//    private String loginName;
//    private String displayName;
//    private String playerPass;
//    private Integer characterRights;
//    private String secondaryRights;
//    private String macAddress;
//    private String connectedFrom;
//    private Integer migrationVersion;
//    private String revertOption;
//    private Long dropBoostStart;
//    private Long revertModeDelay;
//    private String gameMode;
//    private Integer heightLevel;
//    private Integer currentHealth;
//    private Integer playTime;
//    private String lastClanChat;
//    private Boolean requiresPinUnlock;
//    private Integer specRestore;
//    private Integer absX;
//    private Integer absY;
//    private String bankPin;
//    private Boolean isAppendingCancellation;
//    private Long unlockDelay;
//    private Boolean placeholders;
//    private Long bankPinCancellationDelay;
//    private Boolean showDropWarning;
//    private Boolean alchemyWarning;
//    private Boolean hourlyBoxToggle;
//    private Boolean fracturedCrystalToggle;
//    private Boolean acceptAid;
//    private Boolean didYouKnow;
//    private Boolean spectatingTournament;
//    private Integer lootValue;
//    private Integer raidPoints;
//    private Integer raidCount;
//    private Integer tobCompletions;
//    private Long experienceCounter;
//    private String currentTitle;
//    private Boolean receivedVoteStreakRefund;
//    private Integer rfdRound;
//
//    private String[] removedSlayerTask;
//    private Integer[] historyItems;
//    private Integer[] historyItemsN;
//    private Integer[] historyPrice;
//    private Integer[] activeMageArena2BossId;
//    private Integer[] mageArena2SpawnsX;
//    private Integer[] mageArena2SpawnsY;
//    private Boolean[] mageArenaBossKills;
//    private Boolean[] mageArena2Stages;
//    private Integer[] waveInfo;
//    private Integer[] masterClueRequirements;
//    private Integer[] counters;
//    private Boolean[] maxCape;
//    private Integer[] voidStatus;
//    private Boolean[] quickPrayers;
//    private Integer[] runeEssencePouch;
//    private Integer[] pureEssencePouch;
//    private Integer[] playerStats;
//    private Integer[] playerEquipment;
//    private Integer[] playerEquipmentN;
//    private Integer[] playerLevel;
//    private Integer[] playerXP;
//    private Integer[] prestigeLevel;
//    private Boolean[] skillLock;
//    private Integer[] playerItems;
//    private Integer[] playerItemsN;
//    private Boolean[] claimDegradableItem;
//    private Integer[] degradableItem;
//    private AchievementTier[] achievementTiers;
//    private Title[] purchasedTitles;
//    private BankTab[] bankTabs;
//
//    private Integer lastLoginDate;
//    private Boolean hasFollower;
//    private Integer petSummonId;
//    private Boolean completedTutorial;
//    private Boolean unlockedUltimateChest;
//    private Boolean augury;
//    private Boolean rigour;
//    private Boolean crystalDrop;
//    private Boolean spawnedBarrows;
//    private Boolean collectedCoins;
//    private Boolean printAttackStats;
//    private Boolean printDefenceStats;
//    private Boolean absorption;
//    private Boolean announce;
//    private Boolean lootPickup;
//    private Boolean breakVials;
//    private Boolean barbarian;
//    private Integer startDate;
//    private Boolean xpScroll;
//    private Long xpScrollTicks;
//    private Boolean fasterClueScroll;
//    private Long fasterCluesTicks;
//    private Boolean skillingPetRateScroll;
//    private Long skillingPetRateTicks;
//    private Integer flamesOfZamorakCasts;
//    private Integer clawsOfGuthixCasts;
//    private Integer saradominStrikeCasts;
//    private Integer exchangePoints;
//    private Integer totalEarnedExchangePoints;
//    private Boolean usedFc;
//    private Boolean setPin;
//    private Boolean biggerBossTasks;
//    private Boolean cerberusRout;
//    private Integer slayerTasksCompleted;
//    private Boolean claimedReward;
//    private Integer dragonfireShieldCharges;
//    private Integer rfdGloves;
//    private Integer waveId;
//    private Integer fightCavesWaveType;
//    private Boolean helpCcMuted;
//    private Boolean gambleBanned;
//    private Boolean usedReferral;
//    private Long zulrahBestTime;
//    private Integer toxicStaffOfTheDeadCharges;
//    private Integer toxicBlowpipeAmmo;
//    private Integer toxicBlowpipeAmount;
//    private Integer toxicBlowpipeCharge;
//    private Integer serpentineHelmCharge;
//    private Integer tridentCharge;
//    private Integer toxicTridentCharge;
//    private Integer arcLightCharge;
//    private Integer sangStaffCharge;
//    private Integer bryophytaStaffCharge;
//    private Integer slayerPoints;
//    private Integer lastLoginYear;
//    private Integer lastLoginMonth;
//    private Integer lastLoginDay;
//    private Integer loginStreak;
//    private Integer crystalBowArrowCount;
//    private Integer skullTimer;
//    private Integer playerMagicBook;
//    private Integer specAmount;
//    private Double prayerPointDrain;
//    private Integer pvpKillCount;
//    private Integer pcpDeathCount;
//    private Integer bountyHunterKills;
//    private Integer rougeKills;
//    private Integer bountyHunterTargetDelay;
//    private Integer bountyHunterWarnings;
//    private Integer bountyHunterBounties;
//    private Boolean bountyHunterStatisticsVisible;
//    private Boolean bountyHunterSpellAccessible;
//    private Integer autoretaliate;
//    private Integer pkPoints;
//    private Integer elvenCharge;
//    private Integer slaughterCharge;
//    private Integer tomeOfFirePages;
//    private Integer tomeOfFireCharges;
//    private Integer braceletEtherCount;
//    private Integer crawsbowCharge;
//    private Integer thammaronCharge;
//    private Integer viggoraCharge;
//    private Integer bossPoints;
//    private Integer bossPointsRefund;
//    private Integer tournamentWins;
//    private Integer tournamentPoints;
//    private Integer streak;
//    private Integer outlastKills;
//    private Integer outlastDeaths;
//    private Integer tournamentTotalGames;
//    private Integer xpMaxSkill;
//    private Integer referralFlag;
//    private Integer loyaltyPoints;
//    private Integer voteKeyPoints;
//    private Integer donatorPoints;
//    private Integer amountDonated;
//    private Integer prestigePoints;
//    private Integer votePoints;
//    private Integer bloodPoints;
//    private Boolean d1Complete;
//    private Boolean d2Complete;
//    private Boolean d3Complete;
//    private Boolean d4Complete;
//    private Boolean d5Complete;
//    private Boolean d6Complete;
//    private Boolean d7Complete;
//    private Boolean d8Complete;
//    private Boolean d9Complete;
//    private Boolean d10Complete;
//    private Boolean d11Complete;
//    private Integer achievementPoints;
//    private Boolean expLock;
//    private Integer tbTime;
//    private Integer pcPoints;
//    private Integer totalRaidsFinished;
//    private Integer killStreak;
//    private Long bonusXpTime;
//    private Long jailEnd;
//    private Long muteEnd;
//    private Long lastYell;
//    private Boolean splitChat;
//    private Long lastVote;
//    private String slayerTaskName;
//    private Integer slayerTaskAmount;
//    private String lastTask;
//    private Boolean runningToggled;
//    private String slayerMaster;
//    private String konarSlayerLocation;
//    private Integer consecutiveTaskStreak;
//    private Integer mageArenaPoints;
//    private Integer shayzienPoints;
//    private Boolean accountFlagged;
//    private Boolean keepTitle;
//    private Boolean killTitle;
//    private Integer privateChatMode;
//    private String lootingBagUseAction;
//    private Boolean inPkDistrict;
//    private Integer safeBoxSlots;
//    private Long serpentineHelmCombatTicks;
//    private Boolean gargoyleStairsUnlocked;
//    private String controller;
//    private Boolean joinedIronmanGroup;
//
//    private List<GameItem> runePouchItems;
//    private List<GameItem> herbSackItems;
//    private List<GameItem> gemBagItems;
//
//    private List<LootingBagItem> lootingBagItems;
//
//    private List<TaskExtension> slayerTaskExtensions;
//
//    private List<SlayerUnlock> slayerUnlocks;
//
//    private List<Integer> saleResults;
//    private List<Integer> saleItems;
//    private List<Integer> saleAmount;
//    private List<Integer> salePrice;
//
//    @ElementCollection
//    @CollectionTable(name = "item_values", joinColumns = @JoinColumn(name = "player_id"))
//    @MapKeyColumn(name = "event_key")
//    @Column(name = "event_value")
//    private Map<Integer, Integer> itemValues;
//    private Map<Integer, String> itemLastUsed;
//
//    private Map.Entry<String, Integer> npcDeathTracker;
//    private Map.Entry<Killstreak.Type, Integer> killStreaks;
//
//    private Set<Map.Entry<EventChallengeKey, Integer>> eventCalendarProgress;
//
//    private List<DifficultyAchievementDiary.EntryDifficulty> varrockClaimedDiaries;
//    private List<DifficultyAchievementDiary.EntryDifficulty> ardougneClaimedDiaries;
//    private List<DifficultyAchievementDiary.EntryDifficulty> DesertClaimedDiaries;
//    private List<DifficultyAchievementDiary.EntryDifficulty> faldorClaimedDiaries;
//    private List<DifficultyAchievementDiary.EntryDifficulty> fremenikClaimedDiaries;
//    private List<DifficultyAchievementDiary.EntryDifficulty> kandarinClaimedDiaries;
//    private List<DifficultyAchievementDiary.EntryDifficulty> karamjaClaimedDiaries;
//    private List<DifficultyAchievementDiary.EntryDifficulty> lumbridgeClaimedDiaries;
//    private List<DifficultyAchievementDiary.EntryDifficulty> morytaniaClaimedDiaries;
//    private List<DifficultyAchievementDiary.EntryDifficulty> westernClaimedDiaries;
//    private List<DifficultyAchievementDiary.EntryDifficulty> wildernessClaimedDiaries;
//
//    private List<VarrockDiaryEntry> varrockDiaries;
//    private List<ArdougneDiaryEntry> ardougneDiaries;
//    private List<DesertDiaryEntry> desertDiaries;
//    private List<FaladorDiaryEntry> faldorDiaries;
//    private List<FremennikDiaryEntry> fremenikDiaries;
//    private List<KandarinDiaryEntry> kandarinDiaries;
//    private List<KaramjaDiaryEntry> karamjaDiaries;
//    private List<LumbridgeDraynorDiaryEntry> lumbridgeDiaries;
//    private List<MorytaniaDiaryEntry> morytaniaDiaries;
//    private List<WesternDiaryEntry> westernDiaries;
//    private List<WildernessDiaryEntry> wildernessDiaries;
//
//    private Map.Entry<VarrockDiaryEntry, Integer> varrockPartialDiaries;
//    private Map.Entry<ArdougneDiaryEntry, Integer> ardougnePartialDiaries;
//    private Map.Entry<DesertDiaryEntry, Integer> desertPartialDiaries;
//    private Map.Entry<FaladorDiaryEntry, Integer> faldorPartialDiaries;
//    private Map.Entry<FremennikDiaryEntry, Integer> fremenikPartialDiaries;
//    private Map.Entry<KandarinDiaryEntry, Integer> kandarinPartialDiaries;
//    private Map.Entry<KaramjaDiaryEntry, Integer> karamjaPartialDiaries;
//    private Map.Entry<LumbridgeDraynorDiaryEntry, Integer> lumbridgePartialDiaries;
//    private Map.Entry<MorytaniaDiaryEntry, Integer> morytaniaPartialDiaries;
//    private Map.Entry<WesternDiaryEntry, Integer> westernPartialDiaries;
//    private Map.Entry<WildernessDiaryEntry, Integer> wildernessPartialDiaries;
//}
//
