CREATE TABLE PlayerModel
(
    playerId                      BIGINT PRIMARY KEY,
    uuid                          VARCHAR(255),
    loginName                     VARCHAR(255),
    displayName                   VARCHAR(255),
    playerPass                    VARCHAR(255),
    characterRights               INTEGER,
    secondaryRights               VARCHAR(255),
    macAddress                    VARCHAR(255),
    connectedFrom                 VARCHAR(255),
    migrationVersion              INTEGER,
    revertOption                  VARCHAR(255),
    dropBoostStart                BIGINT,
    revertModeDelay               BIGINT,
    gameMode                      VARCHAR(255),
    heightLevel                   INTEGER,
    currentHealth                 INTEGER,
    playTime                      INTEGER,
    lastClanChat                  VARCHAR(255),
    requiresPinUnlock             BOOLEAN,
    specRestore                   INTEGER,
    absX                          INTEGER,
    absY                          INTEGER,
    bankPin                       VARCHAR(255),
    isAppendingCancellation       BOOLEAN,
    unlockDelay                   BIGINT,
    placeholders                  BOOLEAN,
    bankPinCancellationDelay      BIGINT,
    showDropWarning               BOOLEAN,
    alchemyWarning                BOOLEAN,
    hourlyBoxToggle               BOOLEAN,
    fracturedCrystalToggle        BOOLEAN,
    acceptAid                     BOOLEAN,
    didYouKnow                    BOOLEAN,
    spectatingTournament          BOOLEAN,
    lootValue                     INTEGER,
    raidPoints                    INTEGER,
    raidCount                     INTEGER,
    tobCompletions                INTEGER,
    experienceCounter             BIGINT,
    currentTitle                  VARCHAR(255),
    receivedVoteStreakRefund      BOOLEAN,
    rfdRound                      INTEGER,
    removedSlayerTask             VARCHAR(255)[],
    historyItems                  INTEGER[],
    historyItemsN                 INTEGER[],
    historyPrice                  INTEGER[],
    activeMageArena2BossId        INTEGER[],
    mageArena2SpawnsX             INTEGER[],
    mageArena2SpawnsY             INTEGER[],
    mageArenaBossKills            BOOLEAN[],
    mageArena2Stages              BOOLEAN[],
    waveInfo                      INTEGER[],
    masterClueRequirements        INTEGER[],
    counters                      INTEGER[],
    maxCape                       BOOLEAN[],
    voidStatus                    INTEGER[],
    quickPrayers                  BOOLEAN[],
    runeEssencePouch              INTEGER[],
    pureEssencePouch              INTEGER[],
    playerStats                   INTEGER[],
    playerEquipment               INTEGER[],
    playerEquipmentN              INTEGER[],
    playerLevel                   INTEGER[],
    playerXP                      INTEGER[],
    prestigeLevel                 INTEGER[],
    skillLock                     BOOLEAN[],
    playerItems                   INTEGER[],
    playerItemsN                  INTEGER[],
    claimDegradableItem           BOOLEAN[],
    degradableItem                INTEGER[],
    lastLoginDate                 INTEGER,
    hasFollower                   BOOLEAN,
    petSummonId                   INTEGER,
    completedTutorial             BOOLEAN,
    unlockedUltimateChest         BOOLEAN,
    augury                        BOOLEAN,
    rigour                        BOOLEAN,
    crystalDrop                   BOOLEAN,
    spawnedBarrows                BOOLEAN,
    collectedCoins                BOOLEAN,
    printAttackStats              BOOLEAN,
    printDefenceStats             BOOLEAN,
    absorption                    BOOLEAN,
    announce                      BOOLEAN,
    lootPickup                    BOOLEAN,
    breakVials                    BOOLEAN,
    barbarian                     BOOLEAN,
    startDate                     INTEGER,
    xpScroll                      BOOLEAN,
    xpScrollTicks                 BIGINT,
    fasterClueScroll              BOOLEAN,
    fasterCluesTicks              BIGINT,
    skillingPetRateScroll         BOOLEAN,
    skillingPetRateTicks          BIGINT,
    flamesOfZamorakCasts          INTEGER,
    clawsOfGuthixCasts            INTEGER,
    saradominStrikeCasts          INTEGER,
    exchangePoints                INTEGER,
    totalEarnedExchangePoints     INTEGER,
    usedFc                        BOOLEAN,
    setPin                        BOOLEAN,
    biggerBossTasks               BOOLEAN,
    cerberusRout                  BOOLEAN,
    slayerTasksCompleted          INTEGER,
    claimedReward                 BOOLEAN,
    dragonfireShieldCharges       INTEGER,
    rfdGloves                     INTEGER,
    waveId                        INTEGER,
    fightCavesWaveType            INTEGER,
    helpCcMuted                   BOOLEAN,
    gambleBanned                  BOOLEAN,
    usedReferral                  BOOLEAN,
    zulrahBestTime                BIGINT,
    toxicStaffOfTheDeadCharges    INTEGER,
    toxicBlowpipeAmmo             INTEGER,
    toxicBlowpipeAmount           INTEGER,
    toxicBlowpipeCharge           INTEGER,
    serpentineHelmCharge          INTEGER,
    tridentCharge                 INTEGER,
    toxicTridentCharge            INTEGER,
    arcLightCharge                INTEGER,
    sangStaffCharge               INTEGER,
    bryophytaStaffCharge          INTEGER,
    slayerPoints                  INTEGER,
    lastLoginYear                 INTEGER,
    lastLoginMonth                INTEGER,
    lastLoginDay                  INTEGER,
    loginStreak                   INTEGER,
    crystalBowArrowCount          INTEGER,
    skullTimer                    INTEGER,
    playerMagicBook               INTEGER,
    specAmount                    INTEGER,
    prayerPointDrain              DOUBLE PRECISION,
    pvpKillCount                  INTEGER,
    pcpDeathCount                 INTEGER,
    bountyHunterKills             INTEGER,
    rougeKills                    INTEGER,
    bountyHunterTargetDelay       INTEGER,
    bountyHunterWarnings          INTEGER,
    bountyHunterBounties          INTEGER,
    bountyHunterStatisticsVisible BOOLEAN,
    bountyHunterSpellAccessible   BOOLEAN,
    autoretaliate                 INTEGER,
    pkPoints                      INTEGER,
    elvenCharge                   INTEGER,
    slaughterCharge               INTEGER,
    tomeOfFirePages               INTEGER,
    tomeOfFireCharges             INTEGER,
    braceletEtherCount            INTEGER,
    crawsbowCharge                INTEGER,
    thammaronCharge               INTEGER,
    viggoraCharge                 INTEGER,
    bossPoints                    INTEGER,
    bossPointsRefund              INTEGER,
    tournamentWins                INTEGER,
    tournamentPoints              INTEGER,
    streak                        INTEGER,
    outlastKills                  INTEGER,
    outlastDeaths                 INTEGER,
    tournamentTotalGames          INTEGER,
    xpMaxSkill                    INTEGER,
    referralFlag                  INTEGER,
    loyaltyPoints                 INTEGER,
    voteKeyPoints                 INTEGER,
    donatorPoints                 INTEGER,
    amountDonated                 INTEGER,
    prestigePoints                INTEGER,
    votePoints                    INTEGER,
    bloodPoints                   INTEGER,
    d1Complete                    BOOLEAN,
    d2Complete                    BOOLEAN,
    d3Complete                    BOOLEAN,
    d4Complete                    BOOLEAN,
    d5Complete                    BOOLEAN,
    d6Complete                    BOOLEAN,
    d7Complete                    BOOLEAN,
    d8Complete                    BOOLEAN,
    d9Complete                    BOOLEAN,
    d10Complete                   BOOLEAN,
    d11Complete                   BOOLEAN,
    achievementPoints             INTEGER,
    expLock                       BOOLEAN,
    tbTime                        INTEGER,
    pcPoints                      INTEGER,
    totalRaidsFinished            INTEGER,
    killStreak                    INTEGER,
    bonusXpTime                   BIGINT,
    jailEnd                       BIGINT,
    muteEnd                       BIGINT,
    lastYell                      BIGINT,
    splitChat                     BOOLEAN,
    lastVote                      BIGINT,
    slayerTaskName                VARCHAR(255),
    slayerTaskAmount              INTEGER,
    lastTask                      VARCHAR(255),
    runningToggled                BOOLEAN,
    slayerMaster                  VARCHAR(255),
    konarSlayerLocation           VARCHAR(255),
    consecutiveTaskStreak         INTEGER,
    mageArenaPoints               INTEGER,
    shayzienPoints                INTEGER,
    accountFlagged                BOOLEAN,
    keepTitle                     BOOLEAN,
    killTitle                     BOOLEAN,
    privateChatMode               INTEGER,
    lootingBagUseAction           VARCHAR(255),
    inPkDistrict                  BOOLEAN,
    safeBoxSlots                  INTEGER,
    serpentineHelmCombatTicks     BIGINT,
    gargoyleStairsUnlocked        BOOLEAN,
    controller                    VARCHAR(255),
    joinedIronmanGroup            BOOLEAN
);

CREATE TABLE player_active_mage_arena2_boss_id
(
    player_id                  BIGINT,
    active_mage_arena2_boss_id INTEGER
);

CREATE TABLE player_mage_arena2_spawns_x
(
    player_id            BIGINT,
    mage_arena2_spawns_x INTEGER
);

CREATE TABLE player_mage_arena2_spawns_y
(
    player_id            BIGINT,
    mage_arena2_spawns_y INTEGER
);

CREATE TABLE player_mage_arena_boss_kills
(
    player_id             BIGINT,
    mage_arena_boss_kills BOOLEAN
);

CREATE TABLE player_mage_arena2_stages
(
    player_id          BIGINT,
    mage_arena2_stages BOOLEAN
);

CREATE TABLE player_wave_info
(
    player_id BIGINT,
    wave_info INTEGER
);

CREATE TABLE player_master_clue_requirements
(
    player_id               BIGINT,
    master_clue_requirement INTEGER
);

CREATE TABLE player_counters
(
    player_id BIGINT,
    counter   INTEGER
);

CREATE TABLE player_max_cape
(
    player_id BIGINT,
    max_cape  BOOLEAN
);

CREATE TABLE player_void_status
(
    player_id   BIGINT,
    void_status INTEGER
);

CREATE TABLE player_quick_prayers
(
    player_id    BIGINT,
    quick_prayer BOOLEAN
);

CREATE TABLE player_rune_essence_pouch
(
    player_id          BIGINT,
    rune_essence_pouch INTEGER
);

CREATE TABLE player_pure_essence_pouch
(
    player_id          BIGINT,
    pure_essence_pouch INTEGER
);

CREATE TABLE player_player_stats
(
    player_id   BIGINT,
    player_stat INTEGER
);

CREATE TABLE player_player_equipment
(
    player_id        BIGINT,
    player_equipment INTEGER
);

CREATE TABLE player_player_equipment_n
(
    player_id          BIGINT,
    player_equipment_n INTEGER
);

CREATE TABLE player_player_level
(
    player_id    BIGINT,
    player_level INTEGER
);

CREATE TABLE player_player_xp
(
    player_id BIGINT,
    player_xp INTEGER
);

CREATE TABLE player_prestige_level
(
    player_id      BIGINT,
    prestige_level INTEGER
);

CREATE TABLE player_skill_lock
(
    player_id  BIGINT,
    skill_lock BOOLEAN
);

CREATE TABLE player_player_items
(
    player_id   BIGINT,
    player_item INTEGER
);

CREATE TABLE player_player_items_n
(
    player_id     BIGINT,
    player_item_n INTEGER
);

CREATE TABLE player_claim_degradable_item
(
    player_id             BIGINT,
    claim_degradable_item BOOLEAN
);

CREATE TABLE player_degradable_item
(
    player_id       BIGINT,
    degradable_item INTEGER
);

CREATE TABLE player_achievement_tiers
(
    player_id        BIGINT,
    achievement_tier VARCHAR(255)
);

CREATE TABLE player_purchased_titles
(
    player_id       BIGINT,
    purchased_title VARCHAR(255)
);

CREATE TABLE player_bank_tabs
(
    player_id BIGINT,
    bank_tab  VARCHAR(255)
);
CREATE TABLE player_herb_sack_items
(
    player_id BIGINT,
    id        BIGINT,
    amount    BIGINT
);

CREATE TABLE player_gem_bag_items
(
    player_id BIGINT,
    id        BIGINT,
    amount    BIGINT
);

CREATE TABLE player_slayer_task_extensions
(
    player_id BIGINT,
    name      VARCHAR
);

CREATE TABLE player_slayer_unlocks
(
    player_id BIGINT,
    unlock_id VARCHAR
);

CREATE TABLE player_sale_results
(
    player_id      BIGINT,
    sale_result_id BIGINT
);

CREATE TABLE player_sale_items
(
    player_id    BIGINT,
    sale_item_id BIGINT
);

CREATE TABLE player_sale_amount
(
    player_id      BIGINT,
    sale_amount_id BIGINT
);

CREATE TABLE player_sale_price
(
    player_id     BIGINT,
    sale_price_id BIGINT
);

CREATE TABLE player_item_last_used
(
    player_id BIGINT,
    item_id   INTEGER,
    last_used VARCHAR(255)
);

CREATE TABLE player_npc_death_tracker
(
    player_id   BIGINT,
    npc_name    VARCHAR(255),
    death_count INTEGER
);

CREATE TABLE player_kill_streaks
(
    player_id         BIGINT,
    kill_streak_type  VARCHAR(255),
    kill_streak_count INTEGER
);

CREATE TABLE player_event_calendar_progress
(
    player_id           BIGINT,
    event_challenge_key VARCHAR(255),
    progress_value      INTEGER
);

CREATE TABLE player_desert_partial_diaries
(
    player_id      BIGINT,
    diary_entry    VARCHAR(255),
    progress_value INTEGER
);

CREATE TABLE player_faldor_partial_diaries
(
    player_id      BIGINT,
    diary_entry    VARCHAR(255),
    progress_value INTEGER
);

CREATE TABLE player_fremenik_partial_diaries
(
    player_id      BIGINT,
    diary_entry    VARCHAR(255),
    progress_value INTEGER
);

CREATE TABLE player_kandarin_partial_diaries
(
    player_id      BIGINT,
    diary_entry    VARCHAR(255),
    progress_value INTEGER
);

CREATE TABLE player_karamja_partial_diaries
(
    player_id      BIGINT,
    diary_entry    VARCHAR(255),
    progress_value INTEGER
);

CREATE TABLE player_lumbridge_partial_diaries
(
    player_id      BIGINT,
    diary_entry    VARCHAR(255),
    progress_value INTEGER
);

CREATE TABLE player_morytania_partial_diaries
(
    player_id      BIGINT,
    diary_entry    VARCHAR(255),
    progress_value INTEGER
);

CREATE TABLE player_western_partial_diaries
(
    player_id      BIGINT,
    diary_entry    VARCHAR(255),
    progress_value INTEGER
);

CREATE TABLE player_wilderness_partial_diaries
(
    player_id      BIGINT,
    diary_entry    VARCHAR(255),
    progress_value INTEGER
);

CREATE TABLE player_desert_claimed_diaries
(
    player_id   BIGINT,
    diary_entry VARCHAR(255)
);

CREATE TABLE player_faldor_claimed_diaries
(
    player_id   BIGINT,
    diary_entry VARCHAR(255)
);

CREATE TABLE player_fremenik_claimed_diaries
(
    player_id   BIGINT,
    diary_entry VARCHAR(255)
);

CREATE TABLE player_kandarin_claimed_diaries
(
    player_id   BIGINT,
    diary_entry VARCHAR(255)
);

CREATE TABLE player_karamja_claimed_diaries
(
    player_id   BIGINT,
    diary_entry VARCHAR(255)
);

CREATE TABLE player_lumbridge_claimed_diaries
(
    player_id   BIGINT,
    diary_entry VARCHAR(255)
);
CREATE TABLE player_morytania_claimed_diaries
(
    player_id   BIGINT,
    diary_entry VARCHAR(255)
);

CREATE TABLE player_western_claimed_diaries
(
    player_id   BIGINT,
    diary_entry VARCHAR(255)
);

CREATE TABLE player_wilderness_claimed_diaries
(
    player_id   BIGINT,
    diary_entry VARCHAR(255)
);
