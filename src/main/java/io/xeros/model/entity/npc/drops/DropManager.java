package io.xeros.model.entity.npc.drops;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import dev.openrune.cache.CacheManager;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.bosses.godwars.Godwars;
import io.xeros.content.bosses.grotesqueguardians.GrotesqueInstance;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.lootbag.LootingBag;
import io.xeros.content.skills.slayer.SlayerMaster;
import io.xeros.content.skills.slayer.Task;
import io.xeros.content.trails.RewardLevel;
import io.xeros.content.vote_panel.VotePanelManager;
import io.xeros.content.wogw.Wogw;
import io.xeros.model.Items;
import io.xeros.model.Npcs;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.definitions.NpcStats;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.ItemConstants;
import io.xeros.util.Location3D;
import io.xeros.util.Misc;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DropManager {

    private static final Logger logger = LoggerFactory.getLogger(DropManager.class);
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DropManager.class);
    private static final int DEFAULT_NPC = 9425; // Vorkath
    private static final int NPC_RESULTS_CONTAINER_INTERFACE_ID = 39507;
    private static final int DROP_TABLE__CONTAINER_INTERFACE_ID = 34000;
    private static final int DROP_AMOUNT_CONFIG_ID = 1356;
    private static final String LAST_OPENED_TABLE_KEY = "drop_manager_last_opened";

    private static final Comparator<Integer> COMPARE_NAMES = (o1, o2) -> {
        String name1 = NpcDef.forId(o1).getName();
        String name2 = NpcDef.forId(o2).getName();
        return name1.compareToIgnoreCase(name2);
    };

    private final Map<List<Integer>, TableGroup> groups = new HashMap<>();

    private final List<Integer> ordered = new ArrayList<>();

    public void openDefault(Player player) {
        if (player.getAttributes().getInt(LAST_OPENED_TABLE_KEY) == -1) {
            openForNpcId(player, DEFAULT_NPC);
            player.getPA().showInterface(39500);
            //player.sendMessage("@red@Note: @bla@True drop rates are shown. Example: If 5 items all have a 1/20 chance,");
            //player.sendMessage("@bla@those items are actually on a 1/4 drop table but a unique item will still be 1/20.");
        } else {
            player.getPA().showInterface(39500);
        }
    }

    public void read() throws IOException, ParseException {
        ItemConstants itemConstants = new ItemConstants().load();
        readFromDirectory(new File(Server.getDataDirectory() + "/cfg/drops/"), itemConstants);
        ordered.clear();

        for (TableGroup group : groups.values()) {
            if (group.getNpcIds().size() == 1) {
                ordered.add(group.getNpcIds().get(0));
                continue;
            }
            for (int id : group.getNpcIds()) {
                String name = NpcDef.forId(id).getName();
                if (ordered.stream().noneMatch(i -> NpcDef.forId(i).getName().equals(name))) {
                    ordered.add(id);
                }
            }
        }

        ordered.sort(COMPARE_NAMES);
        log.info("Loaded " + ordered.size() + " drop tables.");
    }

    private void readFromDirectory(File directory, ItemConstants itemConstants) throws IOException, ParseException {
        Preconditions.checkState(directory.isDirectory());
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                readFromDirectory(file, itemConstants);
            } else {
                readFromFile(file, itemConstants);
            }
        }
    }

    private void readFromFile(File file, ItemConstants itemConstants) throws IOException, ParseException {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            JsonNode jsonNode = mapper.readTree(file);

            List<Integer> npcIds = new ArrayList<>();

            if (jsonNode.get("npc_id").isArray()) {
                TreeNode idArray = jsonNode.get("npc_id");
                for (int i = 0; i < idArray.size(); i++) {
                    npcIds.add(((JsonNode) idArray.get(i)).intValue());
                }
            } else {
                npcIds.add(jsonNode.get("npc_id").intValue());
            }

            TableGroup group = new TableGroup(npcIds);
            for (TablePolicy policy : TablePolicy.POLICIES) {
                if (!jsonNode.has(policy.name().toLowerCase())) {
                    continue;
                }
                ObjectNode dropTable = (ObjectNode) jsonNode.get(policy.name().toLowerCase());
                Table table = new Table(policy, dropTable.get("accessibility").intValue());
                TreeNode tableItems = dropTable.get("items");

                for (int i = 0; i < tableItems.size(); i++) {
                    ObjectNode item = (ObjectNode) tableItems.get(i);
                    int id;
                    if (item.has("item")) {
                        id = Integer.parseInt(item.get("item").toString());
                    } else if (item.has("name")) {
                        id = itemConstants.get(item.get("name").toString().replaceAll("\"", ""));
                    } else throw new IllegalStateException("No item id or name: " + file);

                    int minimumAmount = item.get("minimum").intValue();
                    int maximumAmount = item.get("maximum").intValue();
                    table.add(new Drop(npcIds, id, minimumAmount, maximumAmount));
                }

                group.add(table);
            }
            groups.put(npcIds, group);
        } catch (Exception e) {
            System.err.println("Error in " + file);
            throw e;
        }
    }

    static int[] wildybosses = {6611, 6609, 6615, 6609, 6615, 6610, 6619, 2054, 6618, 6607};
    public static int[] wildybossesforgiveaway = {6611, 6609, 6615, 6609, 6615, 6610, 6619, 2054, 6618, 6607};
    static int[] revs = {7930, 7931, 7932, 7933, 7934, 7935, 7936, 7937, 7938, 7939, 7940};

    public static void dropCoinBag(Player player, int npcId, int dropX, int dropY, int dropZ) {
        NpcDef npcDefinition = NpcDef.forId(npcId);
        boolean smallNpc = npcDefinition.getCombatLevel() > 0 && npcDefinition.getCombatLevel() <= 80;
        boolean mediumNpc = npcDefinition.getCombatLevel() > 80 && npcDefinition.getCombatLevel() <= 110;
        boolean largeNpc = npcDefinition.getCombatLevel() > 110 && npcDefinition.getCombatLevel() <= 331;
        boolean buldgingNpc = npcDefinition.getCombatLevel() > 331;
        if (Misc.random(8) == 5) {
            int itemId = 0;
            int petPerkChance = Misc.random(100);
            itemId = Items.SMALL_COIN_BAG;
            int correctPetId = 30013;
            if (mediumNpc) {
                itemId = Items.MEDIUM_COIN_BAG;
            } else if (largeNpc) {
                itemId = Items.LARGE_COIN_BAG;
            } else if (buldgingNpc) {
                itemId = Items.BULDGING_COIN_BAG;
            }
            int extraBag = 0;
            boolean hasDarkVersion = (player.petSummonId == 30113 || player.petSummonId == 30122);

            if (player.hasFollower &&
                    ((player.petSummonId == correctPetId || player.petSummonId == 30022) && petPerkChance < 80)
                    || (hasDarkVersion)) {
                if (hasDarkVersion && petPerkChance < 25) {
                    extraBag = 1;
                }
                player.sendMessage("@bla@[@red@Pet@bla@] Your pet found a @blu@coin bag!");
                player.getItems().addItem(itemId, 1 + extraBag);
            } else {
                player.sendMessage("@bla@You notice a @blu@coin bag@bla@ on the floor.");
                Server.itemHandler.createGroundItem(player, itemId, dropX, dropY, dropZ, 1, player.getIndex());
            }
        }
    }

    public static void dropResourcePack(Player player, int npcId, int dropX, int dropY, int dropZ) {
        int petPerkChance = Misc.random(100);
        int correctPetId = 30012;
        int box = Items.RESOURCE_BOXSMALL;
        NpcDef npcDefinition = NpcDef.forId(npcId);
        if (npcDefinition != null && Misc.random(20) == 1) {
            int combatLevel = npcDefinition.getCombatLevel();
            if (combatLevel >= 96) {
                box = Items.RESOURCE_BOXLARGE;
            } else if (combatLevel >= 62) {
                box = Items.RESOURCE_BOXMEDIUM;
            }
            boolean hasDarkVersion = (player.petSummonId == 30112 || player.petSummonId == 30122);
            int extraPack = 0;
            if (player.hasFollower &&
                    ((player.petSummonId == correctPetId || player.petSummonId == 30022) && petPerkChance < 80)
                    || (hasDarkVersion)) {
                if (hasDarkVersion && petPerkChance < 25) {
                    extraPack = 1;
                }
                player.sendMessage("@bla@[@red@Pet@bla@] Your pet found a @blu@resource box!");
                player.getItems().addItem(box, 1 + extraPack);
            } else {
                player.sendMessage("@bla@You notice a @blu@resource box@bla@ on the floor.");
                Server.itemHandler.createGroundItem(player, box, dropX, dropY, dropZ, 1, player.getIndex());
            }
        }
    }

    public List<GameItem> getDropSample(Player player, int npcId) {
        Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(npcId)).findFirst();
        if (group.isPresent()) {
            double modifier = getModifier(player);
            return group.get().access(player, null, modifier, 1, npcId);
        } else {
            return null;
        }
    }

    public void handle(Player player, NPC npc, Location3D location, int repeats, int npcId) {
        boolean smallNpc = npc.getDefinition().getCombatLevel() > 0 && npc.getDefinition().getCombatLevel() <= 80;
        boolean mediumNpc = npc.getDefinition().getCombatLevel() > 80 && npc.getDefinition().getCombatLevel() <= 110;
        boolean largeNpc = npc.getDefinition().getCombatLevel() > 110 && npc.getDefinition().getCombatLevel() <= 331;
        boolean buldgingNpc = npc.getDefinition().getCombatLevel() > 331;
        int petPerkChance = Misc.random(100);

        int correctPetId = -1;
        int specialItemId = Items.CRYSTAL_KEY;

        /**
         * Looting bag and rune pouch
         */
        if (npc.getPosition().inWild()) {
            switch (Misc.random(40)) {
                case 2:
                    if (!player.getItems().playerHasItem(LootingBag.LOOTING_BAG_OPEN) && !player.getItems().playerHasItem(LootingBag.LOOTING_BAG)) {
                        Server.itemHandler.createGroundItem(player, LootingBag.LOOTING_BAG, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
                    }
                    break;

                case 8:
                    if (player.getItems().getItemCount(12791, true) < 1) {
                        Server.itemHandler.createGroundItem(player, 12791, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
                    }
                    break;
            }
        }
        /**
         * Slayer's staff enchantment and Emblems
         */
        Optional<Task> task = player.getSlayer().getTask();
        Optional<SlayerMaster> myMaster = SlayerMaster.get(player.getSlayer().getMaster());
        task.ifPresent(t -> {
            String name = npc.getDefinition().getName().toLowerCase().replaceAll("_", " ");

            if (name.equals(t.getPrimaryName()) || ArrayUtils.contains(t.getNames(), name)) {
                myMaster.ifPresent(m -> {
                    if (npc.getNpcId() == Npcs.GARGOYLE_1543 && (!player.gargoyleStairsUnlocked && Misc.random(120) == 1 || Server.isDebug())) {
                        Server.itemHandler.createGroundItem(player, new GameItem(GrotesqueInstance.GROTESQUE_GUARDIANS_KEY), location.toPosition());
                        player.sendMessage("@red@You receive a brittle key.");
                        logger.debug("Grotesque guardians key received because server is on debug mode.");
                    }

                    if (npc.getPosition().inWild() && m.getId() == 7663) {
                        int slayerChance = 650;
                        int emblemChance = 100;
                        if (Misc.random(emblemChance) == 1) {
                            Server.itemHandler.createGroundItem(player, 12746, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
                            player.sendMessage("@red@You receive a mysterious emblem!");
                        }
                        if (Misc.random(slayerChance) == 1) {
                            Server.itemHandler.createGroundItem(player, 21257, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
                            //player.sendMessage("@red@A slayer's enchantment has dropped from your foe!");
                            //PlayerHandler.executeGlobalMessage(
                            //"@red@" + Misc.capitalize(player.playerName) + " received a drop: Slayer's Enchantment.</col>.");
                            //PlayerHandler.executeGlobalMessage("<col=FF0000>[Lootations] @cr19@ </col><col=255>"+ Misc.capitalize(player.playerName) + "</col> received a <col=255>Slayer's Enchantment</col>.");
                        }
                    }
                });
            }
        });

        /**
         * Clue scrolls
         */
        int chance = player.getRechargeItems().hasItem(13118) ? 82 : player.getRechargeItems().hasItem(13119) ? 80 : player.getRechargeItems().hasItem(13120) ? 75 : 85;
        int clueAmount = 1;
        if (player.fasterCluesScroll) {
            chance = chance / 2;
        }
        if (Hespori.activeGolparSeed) {
            clueAmount = 2;
        }
        if (Misc.random(chance) == 1) {
            specialItemId = Items.CLUE_SCROLL_EASY;
            mediumNpc = npc.getDefinition().getCombatLevel() > 40 && npc.getDefinition().getCombatLevel() <= 100;
            largeNpc = npc.getDefinition().getCombatLevel() > 100;
            if (mediumNpc) {
                specialItemId = Items.CLUE_SCROLL_MEDIUM;
            } else if (largeNpc) {
                specialItemId = Items.CLUE_SCROLL_HARD;
            }
            boolean hasDarkVersion = player.petSummonId == 30111 || player.petSummonId == 30122;
            int extraKey = 0;
            if (player.hasFollower &&
                    ((player.petSummonId == 30011 || player.petSummonId == 30022) && petPerkChance < 80)
                    || (hasDarkVersion)) {
                if (player.getItems().freeSlots() > 0) {
                    if (hasDarkVersion && petPerkChance < 25) {
                        extraKey = 1;
                    }
                    player.sendMessage("@bla@[@red@Pet@bla@] Your pet found a @blu@clue scroll!");
                    player.getItems().addItem(specialItemId, clueAmount + extraKey);
                } else {
                    player.sendMessage("@bla@[@red@Pet@bla@] Your pet noticed a @blu@clue scroll,@bla@ but your inventory is full!");
                    Server.itemHandler.createGroundItem(player, specialItemId, location.getX(), location.getY(), location.getZ(), clueAmount, player.getIndex());
                }
            } else {
                player.sendMessage("@bla@You notice a @blu@clue scroll@bla@ on the floor.");
                Server.itemHandler.createGroundItem(player, specialItemId, location.getX(), location.getY(), location.getZ(), clueAmount, player.getIndex());
            }
        }

        /**
         * Coin Bags and resource boxes
         */

        if (Misc.random(1) == 1 || Hespori.activeGolparSeed) {
            dropResourcePack(player, npcId, location.getX(), location.getY(), location.getZ());
        } else {
            dropCoinBag(player, npcId, location.getX(), location.getY(), location.getZ());
        }

        // Runecrafting pouches
        if (Misc.random(80) == 10 || Server.isDebug()) {
            if (player.getItems().getItemCount(5509, false) == 1 && player.getItems().getItemCount(5510, false) == 0) {
                player.sendMessage("You receive a {}.", CacheManager.INSTANCE.getItem(5510).getName());
                player.getItems().addItemUnderAnyCircumstance(5510, 1);
            } else if (player.getItems().getItemCount(5510, false) == 1 && player.getItems().getItemCount(5512, false) == 0) {
                player.sendMessage("You receive a {}.", CacheManager.INSTANCE.getItem(5512).getName());
                player.getItems().addItemUnderAnyCircumstance(5512, 1);
            }
        }

        /**
         * Crystal keys
         */
        int crystalRandom = 115;
        int keyAmount = 1;
        if (Hespori.activeGolparSeed) {
            keyAmount = 2;
        }
        if (Misc.random(crystalRandom) == 1) {
            specialItemId = Items.CRYSTAL_KEY;
            correctPetId = 30010;
            int extraKey = 0;
            boolean hasDarkVersion = (player.petSummonId == 30110 || player.petSummonId == 30122);
            if (player.hasFollower &&
                    ((player.petSummonId == correctPetId || player.petSummonId == 30022) && petPerkChance < 80)
                    || (hasDarkVersion)) {
                if (player.getItems().freeSlots() > 0) {
                    player.sendMessage("@bla@[@red@Pet@bla@] Your pet found a @blu@crystal key!");
                    if (hasDarkVersion && petPerkChance < 25) {
                        extraKey = 1;
                    }
                    player.getItems().addItem(specialItemId, keyAmount + extraKey);
                } else {
                    player.sendMessage("@bla@[@red@Pet@bla@] Your pet noticed a @blu@crystal key,@bla@ but your inventory is full!");
                    Server.itemHandler.createGroundItem(player, specialItemId, location.getX(), location.getY(), location.getZ(), keyAmount, player.getIndex());
                }
            } else {
                player.sendMessage("@bla@You notice a @blu@crystal key@bla@ on the floor.");
                Server.itemHandler.createGroundItem(player, specialItemId, location.getX(), location.getY(), location.getZ(), keyAmount, player.getIndex());
            }
        }
        NpcDef npcDefinition = NpcDef.forId(npcId);
        if (npcDefinition != null && Misc.random(200) == 1) {
            int artefactRoll = Misc.random(100);
            int combatLevel = npcDefinition.getCombatLevel();
            if (combatLevel >= 96) {
                if (artefactRoll < 65) {//1/300
                    player.getItems().addItemUnderAnyCircumstance(11180, 1);//ancient coin foe for 200
                    player.sendMessage("You found a coin that can be used in the Fire of Exchange!");
                } else if (artefactRoll >= 65 && artefactRoll < 99) {//1/600
                    player.getItems().addItemUnderAnyCircumstance(681, 1);//anicent talisman foe for 300
                    player.sendMessage("You found a talisman that can be used in the Fire of Exchange!");
                } else if (artefactRoll > 99) {//1/1000
                    player.getItems().addItemUnderAnyCircumstance(9034, 1);//golden statuette foe for 500
                    PlayerHandler.executeGlobalMessage("@bla@[@red@PvM@bla@]@blu@ " + player.getDisplayName() + " @red@just found a Golden statuette.");
                }
            }
        }
        /**
         * Ecumenical Keys
         */
        if (Boundary.isIn(npc, Boundary.WILDERNESS_GOD_WARS_BOUNDARY)) {
            if (Misc.random(60 + 10 * player.getItems().getItemCount(Godwars.KEY_ID, true)) == 1) {
                /**
                 * Key will not drop if player owns more than 3 keys already
                 */
                int key_amount = player.getDiaryManager().getWildernessDiary().hasCompleted("ELITE") ? 6 : 3;
                if (player.getItems().getItemCount(Godwars.KEY_ID, true) > key_amount) {
                    return;
                }
                Server.itemHandler.createGroundItem(player, Godwars.KEY_ID, npc.getX(), npc.getY(), player.heightLevel, 1, player.getIndex());
                //player.sendMessage("@pur@An Ecumenical Key drops from your foe.");
            }
        }
        int random = Misc.random(1200);
        if (IntStream.of(wildybosses).anyMatch(id -> id == npcId) && player.getPosition().inWild())
            if (random == 1) {
                Server.itemHandler.createGroundItem(player, 12746, player.absX,
                        player.absY, player.heightLevel, 1, player.getIndex());
                if (chance == 1 && player.getItems().playerHasItem(12746, 1)) {
                    player.getItems().deleteItem(12746, 1);
                    player.getItems().addItem(12748, 1);
                    player.sendMessage("@blu@You have received a mysterious emblem.");
                    player.sendMessage("@blu@You have upgraded your @red@ [TIER 1 EMBLEM]@blu@ to @red@[TIER 2 EMBLEM]@blu@.");
                } else if (chance == 1 && player.getItems().playerHasItem(12748, 1)) {
                    player.getItems().deleteItem(12748, 1);
                    player.getItems().addItem(12749, 1);
                    player.sendMessage("@blu@You have upgraded your @red@ [TIER 2 EMBLEM]@blu@ to @red@[TIER 3 EMBLEM]@blu@.");
                } else if (chance == 1 && player.getItems().playerHasItem(12749, 1)) {
                    player.getItems().deleteItem(12749, 1);
                    player.getItems().addItem(12750, 1);
                    player.sendMessage("@blu@You have upgraded your @red@ [TIER 3 EMBLEM]@blu@ to @red@[TIER 4 EMBLEM]@blu@.");
                } else if (chance == 1 && player.getItems().playerHasItem(12750, 1)) {
                    player.getItems().deleteItem(12750, 1);
                    player.getItems().addItem(12751, 1);
                    player.sendMessage("@blu@You have received a mysterious emblem.");
                    player.sendMessage("@blu@You have upgraded your @red@ [TIER 4 EMBLEM]@blu@ to @red@[TIER 5 EMBLEM]@blu@.");
                } else if (chance == 1 && player.getItems().playerHasItem(12751, 1)) {
                    player.getItems().deleteItem(12751, 1);
                    player.getItems().addItem(12752, 1);
                    player.sendMessage("@blu@You have received a mysterious emblem.");
                    player.sendMessage("@blu@You have upgraded your @red@ [TIER 5 EMBLEM]@blu@ to @red@[TIER 6 EMBLEM]@blu@.");
                } else if (chance == 1 && player.getItems().playerHasItem(12752, 1)) {
                    player.getItems().deleteItem(12752, 1);
                    player.getItems().addItem(12753, 1);
                    player.sendMessage("@blu@You have received a mysterious emblem.");
                    player.sendMessage("@blu@You have upgraded your @red@ [TIER 6 EMBLEM]@blu@ to @red@[TIER 7 EMBLEM]@blu@.");
                } else if (chance == 1 && player.getItems().playerHasItem(12753, 1)) {
                    player.getItems().deleteItem(12753, 1);
                    player.sendMessage("@blu@You have received a mysterious emblem.");
                    player.getItems().addItem(12754, 1);
                    player.sendMessage("@blu@You have upgraded your @red@ [TIER 7 EMBLEM]@blu@ to @red@[TIER 8 EMBLEM]@blu@.");
                } else if (chance == 1 && player.getItems().playerHasItem(12754, 1)) {
                    player.getItems().deleteItem(12754, 1);
                    player.getItems().addItem(12755, 1);
                    player.sendMessage("@blu@You have received a mysterious emblem.");
                    player.sendMessage("@blu@You have upgraded your @red@ [TIER 8 EMBLEM]@blu@ to @red@[TIER 9 EMBLEM]@blu@.");
                } else if (chance == 1 && player.getItems().playerHasItem(12755, 1)) {
                    player.getItems().deleteItem(12755, 1);
                    player.getItems().addItem(12756, 1);
                    player.sendMessage("@blu@You have received a mysterious emblem.");
                    player.sendMessage("@blu@You have upgraded your @red@ [TIER 9 EMBLEM]@blu@ to @red@[TIER 10 EMBLEM]@blu@.");
                } else if (chance == 1 && player.getItems().playerHasItem(12756, 1)) {
                    player.getItems().deleteItem(12756, 1);
                    player.getItems().addItem(12746, 1);
                    player.sendMessage("@blu@You have received a mysterious emblem.");
                    player.sendMessage("@blu@You are max tier so you was rewarded with just the Mysterious emblem added..");
                }
            }

        /**
         * Dark Light
         */
        if (Boundary.isIn(npc, Boundary.CATACOMBS)) {
            if (Misc.random(1000) == 1) {

                //PlayerHandler.executeGlobalMessage("<col=FF0000>[Lootations] @cr19@ </col><col=255>"+ Misc.capitalize(player.playerName) + "</col> received a <col=255>Darklight!</col>.");
                Server.itemHandler.createGroundItem(player, 6746, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
            }
        }

        /**
         * Dark totem Pieces
         */
        if (Boundary.isIn(npc, Boundary.CATACOMBS)) {
            switch (Misc.random(25)) {
                case 1:
                    if (player.getItems().getItemCount(19679, false) < 1) {
                        Server.itemHandler.createGroundItem(player, 19679, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
                        player.sendMessage("@red@A surge of dark energy fills your body as you notice something on the ground.");
                    }
                    break;

                case 2:
                    if (player.getItems().getItemCount(19681, false) < 1) {
                        Server.itemHandler.createGroundItem(player, 19681, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
                        player.sendMessage("@red@A surge of dark energy fills your body as you notice something on the ground.");
                    }
                    break;

                case 3:
                    if (player.getItems().getItemCount(19683, false) < 1) {
                        Server.itemHandler.createGroundItem(player, 19683, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
                        player.sendMessage("@red@A surge of dark energy fills your body as you notice something on the ground.");
                    }
                    break;
            }
        }
    }

    public void create(Player player, NPC npc, Location3D location, int repeats, int npcId) {
        if (npcId == Npcs.THE_MIMIC_8633) {
            if (player.getItems().playerHasItem(Items.MIMIC)) {
                Achievements.increase(player, AchievementType.MIMIC, 1);
                player.getItems().deleteItem(Items.MIMIC, 1);
                PetHandler.roll(player, PetHandler.Pets.MIMIC);

                List<GameItem> drops = player.getTrails().generateRewardList(RewardLevel.MASTER, 6);
                for (GameItem item : drops) {
                    onDrop(player, item, npcId);
                    Server.itemHandler.createGroundItem(player, item.id(), location.getX(), location.getY(),
                            location.getZ(), item.amount(), player.getIndex());
                }

                handle(player, npc, location, repeats, npcId);
            } else {
                player.sendMessage("You were missing the mimic casket and did not get a drop.");
            }
        }

        Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(npcId)).findFirst();
        group.ifPresent(g -> {
            double modifier = getModifier(player);
            List<GameItem> drops = g.access(player, npc, modifier, repeats, npcId);
            for (GameItem item : drops) {
                onDrop(player, item, npcId);
                Server.itemHandler.createGroundItem(player, item.id(), location.getX(), location.getY(),
                        location.getZ(), item.amount(), player.getIndex());
            }
            handle(player, npc, location, repeats, npcId);
        });
    }

    private double getModifier(Player player) {
        double modifier = 1.0;
        modifier += player.getMode().getDropModifier();
        if (player.getItems().isWearingItem(2572)) {
            modifier -= .03;
        } else if (player.getItems().isWearingItem(12785)) {
            modifier -= .08;
        }
        if (player.getItems().playerHasItem(30121) || (player.hasFollower && player.petSummonId == 30121)) {
            modifier -= .20;
        } else if (player.getItems().playerHasItem(30021) || player.getItems().playerHasItem(30114)
                || (player.hasFollower && (player.petSummonId == 30114 || player.petSummonId == 30120))
                || (player.hasFollower && (player.petSummonId == 30021 || player.petSummonId == 30122))) {
            modifier -= .10;
        } else if (player.getItems().playerHasItem(30014) || (player.hasFollower && (player.petSummonId == 30014 || player.petSummonId == 30020 || player.petSummonId == 30022))) {
            modifier -= .05;
        }
        if (player.isSkulled == true && Boundary.isIn(player, Boundary.REV_CAVE)) {
            modifier -= .100;
        }
        if (VotePanelManager.hasDropBoost(player)) {
            modifier -= .100;
        }
        if (player.getRights().contains(Right.ONYX_CLUB)) {
            modifier -= 0.080;
        } else if (player.getRights().contains(Right.DIAMOND_CLUB)) {
            modifier -= 0.060;
        } else if (player.getRights().contains(Right.LEGENDARY_DONATOR)) {
            modifier -= 0.050;
        } else if (player.getRights().contains(Right.EXTREME_DONOR)) {
            modifier -= 0.040;
        } else if (player.getRights().contains(Right.REGULAR_DONATOR)) {
            modifier -= 0.030;
        }

        if (Wogw._20_PERCENT_DROP_RATE_TIMER > 0)
            modifier -= 0.2;

        return modifier;
    }

    public static double getModifier1(Player player) {
        int modifier = 0;
        if (player.getItems().isWearingItem(2572)) {
            modifier += 3;
        } else if (player.getItems().isWearingItem(12785)) {
            modifier += 8;
        }
        if (player.getItems().playerHasItem(30121) || (player.hasFollower && player.petSummonId == 30121)) {
            modifier += 20;
        } else if (player.getItems().playerHasItem(30021) || player.getItems().playerHasItem(30114)
                || (player.hasFollower && (player.petSummonId == 30114 || player.petSummonId == 30120))
                || (player.hasFollower && (player.petSummonId == 30021 || player.petSummonId == 30122))) {
            modifier += 10;
        } else if (player.getItems().playerHasItem(30014) || (player.hasFollower && (player.petSummonId == 30014 || player.petSummonId == 30020 || player.petSummonId == 30022))) {
            modifier += 5;
        }
        if (Hespori.KRONOS_TIMER > 0) {
            modifier += 10;
        }
        if (player.isSkulled == true && Boundary.isIn(player, Boundary.REV_CAVE)) {
            modifier += 10;
        }
        modifier += -(player.getMode().getDropModifier() * 100);
        if (player.getRights().contains(Right.ONYX_CLUB)) {
            modifier += 8;
        } else if (player.getRights().contains(Right.DIAMOND_CLUB)) {
            modifier += 6;
        } else if (player.getRights().contains(Right.LEGENDARY_DONATOR)) {
            modifier += 5;
        } else if (player.getRights().contains(Right.EXTREME_DONOR)) {
            modifier += 4;
        } else if (player.getRights().contains(Right.REGULAR_DONATOR)) {
            modifier += 3;
        }
        return modifier;
    }

    public void clearSearch(Player player) {
        for (int i = 0; i < 150; i++) {
            player.getPA().sendFrame126("", 33008 + i);
        }
        player.dropInterfaceSearchList.clear();
    }

    private void clearDrops(Player player) {
        player.getPA().sendFrame126("", 43110);
        player.getPA().sendFrame126("", 43111);
        player.getPA().sendFrame126("", 43112);
        player.getPA().sendFrame126("", 43113);

        for (int i = 0; i < 80; i++) {
            player.getPA().itemOnInterface(-1, 0, 34010 + i, 0);
            player.getPA().sendString("", 34200 + i);
            player.getPA().sendString("", 34300 + i);
            player.getPA().sendString("", 34100 + i);
            player.getPA().sendString("", 34400 + i);
        }
    }

    /**
     * Clears the interface of all parts.
     * <p>
     * Used on searching and initial load.
     *
     * @param player
     */
    public void clear(Player player) {
        clearSearch(player);
        clearDrops(player);
    }

    public void open2(Player player) {
        clear(player);

        for (int index = 0; index < ordered.size(); index++) {
            player.getPA().sendFrame126(StringUtils.capitalize(NpcDef.forId(ordered.get(index)).getName().toLowerCase().replaceAll("_", " ")), 33008 + index);
        }

        player.getPA().showInterface(33000);
    }

    public List<GameItem> getNPCdrops(int id) {
        Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(id)).findFirst();
        try {
            return group.map(g -> {
                List<GameItem> items = new ArrayList<>();
                for (TablePolicy policy : TablePolicy.POLICIES) {
                    if (policy == TablePolicy.RARE || policy == TablePolicy.VERY_RARE) {
                        Optional<Table> table = g.stream().filter(t -> t.getPolicy() == policy).findFirst();
                        if (table.isPresent()) {
                            for (Drop d : table.get()) {
                                items.add(new GameItem(d.getItemId(), d.getMaximumAmount()));
                            }
                        }
                    }
                }
                return items;
            }).orElse(new ArrayList<>());
        } catch (Exception e) {
            logger.error("Error getting npc drops {}", id);
            e.printStackTrace(System.err);
        }
        return null;
    }

    public void getDrops(Player player, int id) {
        Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(id)).findFirst();
        group.ifPresent(g -> {
            List<GameItem> items = new ArrayList<>();
            for (TablePolicy policy : TablePolicy.POLICIES) {
                if (policy == TablePolicy.RARE || policy == TablePolicy.VERY_RARE) {
                    Optional<Table> table = g.stream().filter(t -> t.getPolicy() == policy).findFirst();
                    if (table.isPresent()) {
                        for (Drop d : table.get()) {
                            items.add(new GameItem(d.getItemId(), d.getMaximumAmount()));
                        }
                    }
                }
            }
            player.dropItems = items;
        });
    }

    /**
     * Searchers after the player inputs a npc name
     *
     * @param player
     * @param name
     */
    public void search(Player player, String name) {
        if (name.matches("^(?=.*[A-Z])(?=.*[0-9])[A-Z0-9]+$")) {
            player.sendMessage("You may not search for alphabetical and numerical combinations.");
            return;
        }
        if (System.currentTimeMillis() - player.lastDropTableSearch < TimeUnit.SECONDS.toMillis(1)
                && !player.getRights().contains(Right.OWNER)) {
            player.sendMessage("You can only do this once every few seconds.");
            return;
        }
        player.lastDropTableSearch = System.currentTimeMillis();

        clearSearch(player);

        List<Integer> definitions = ordered.stream().filter(Objects::nonNull).filter(def -> NpcDef.forId(def).getName() != null).filter(def -> NpcDef.forId(def).getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());

        if (definitions.isEmpty()) {
            definitions = ordered.stream().filter(Objects::nonNull).collect(Collectors.toList());
            List<Integer> npcs = new ArrayList<>();
            int count = 0;

            player.getPA().setScrollableMaxHeight(NPC_RESULTS_CONTAINER_INTERFACE_ID, 250 + (definitions.size() > 16 ? (definitions.size() - 16) * 14 : 0));

            for (Integer index : definitions) {
                Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(index)).findFirst();
                if (group.isPresent()) {
                    TableGroup g = group.get();

                    for (TablePolicy policy : TablePolicy.values()) {
                        Optional<Table> table = g.stream().filter(t -> t.getPolicy() == policy).findFirst();
                        if (table.isPresent()) {
                            for (Drop drop : table.get()) {
                                if (drop == null) {
                                    continue;
                                }

                                if (ItemAssistant.getItemName(drop.getItemId()).toLowerCase().contains(name.toLowerCase())) {
                                    /** Fix for duplicate entries. **/
                                    if (npcs.contains(index))
                                        continue;
                                    npcs.add(index);
                                    player.getPA().sendFrame126(StringUtils.capitalize(NpcDef.forId(index).getName().toLowerCase().replaceAll("_", " ")), 33008 + count);
                                    count++;
                                }
                            }
                        }
                    }
                }

            }

            player.dropInterfaceSearchList = npcs;
            return;

        }

        player.getPA().setScrollableMaxHeight(NPC_RESULTS_CONTAINER_INTERFACE_ID, 251 + (definitions.size() > 17 ? (definitions.size() - 17) * 13 : 0));

        for (int index = 0; index < definitions.size(); index++) {
            if (index >= 150) {
                break;
            }
            player.getPA().sendFrame126(StringUtils.capitalize(NpcDef.forId(definitions.get(index)).getName().toLowerCase().replaceAll("_", " ")), 33008 + index);
        }

        player.dropInterfaceSearchList = definitions;
    }

    /**
     * Loads the selected npc choosen by the player to view their drops
     *
     * @param player
     * @param button
     */
    public void select(Player player, int button) {
        int listIndex;

        //So the idiot client dev didn't organize the buttons in a singulatiry order. So i had to shift around the id's
        //so if you have 50 npcs in the search you can click them all fine
        if (button <= 128255) {
            listIndex = button - 128240;
        } else {
            listIndex = (128255 - 128240) + 1 + button - 129000;
        }

        if (listIndex < 0) {
            return;
        }

        //Finding NPC ID
        if (player.dropInterfaceSearchList.isEmpty() && ordered.size() > listIndex) {
            openForNpcId(player, ordered.get(listIndex));
        } else if (player.dropInterfaceSearchList.size() > listIndex) {
            openForNpcId(player, player.dropInterfaceSearchList.get(listIndex));
        }
    }

    public void openForPacket(Player player, int npcId) {
        openForNpcId(player, npcId);
        player.getPA().showInterface(39500);
    }

    public void openForNpcId(Player player, int npcId) {
        player.getAttributes().setInt(LAST_OPENED_TABLE_KEY, npcId);
        Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(npcId)).findFirst();

        //If the group in the search area contains this NPC
        group.ifPresent(g -> {
            if (System.currentTimeMillis() - player.lastDropTableSelected < TimeUnit.SECONDS.toMillis(5)
                    && player.getRights().isNotAdmin()) {
                player.sendMessage("You can only do this once every 5 seconds.");
                return;
            }

            //Loads the definition and maxhit/aggressiveness to display
            NpcDef npcDef = NpcDef.forId(npcId);

            if (player.getRights().contains(Right.OWNER)) {
                player.getPA().sendString(npcId + ", " + npcDef.getName(), 43005);
            } else {
                player.getPA().sendString("Monster Drop Viewer", 43005);
            }

            player.getPA().sendFrame126("Health: @whi@" + NpcStats.forId(npcId).getHitpoints(), 43110);
            player.getPA().sendFrame126("Combat Level: @whi@" + npcDef.getCombatLevel()
                    + (player.getRights().isOrInherits(Right.OWNER) ? "\\nshowing specific item rates, no modifier" : ""), 43111);
            if (NPCHandler.getNpc(npcId) != null) {
                player.getPA().sendFrame126("Max Hit: @whi@" + NPCHandler.getNpc(npcId).maxHit, 43112); // TODO fix this
            } else {
                player.getPA().sendFrame126("Max Hit: @whi@?", 43112);
            }
            player.getPA().sendFrame126("", 43113);

            player.lastDropTableSelected = System.currentTimeMillis();

            double modifier = getModifier(player);

            player.getPA().resetScrollBar(DROP_TABLE__CONTAINER_INTERFACE_ID);

            //Iterates through all 5 drop table's (Found in TablePolicy -> Enum)
            for (TablePolicy policy : TablePolicy.POLICIES) {
                Optional<Table> table = g.stream().filter(t -> t.getPolicy() == policy).findFirst();
                if (table.isPresent()) {

                    int rate = (int) (table.get().getAccessibility() * modifier);
                    if (player.getRights().isOrInherits(Right.OWNER)) {
                        rate = (int) Math.ceil((table.get().getAccessibility())) * table.get().size(); // Effective rate
                    }

                    if (!updateAmounts(player, policy, table.get(), rate)) {
                        break;
                    }
                } else {
                    if (!updateAmounts(player, policy, new ArrayList<>(), -10)) {
                        break;
                    }
                }
            }

            PetHandler.Pets pet = PetHandler.getPet(npcId);
            if (pet != null) {
                writeItem(player, pet.getItemId(), 1, 1, "Rare", pet.getDroprate());
            }

            player.getPA().setScrollableMaxHeight(DROP_TABLE__CONTAINER_INTERFACE_ID, 225 + (player.dropSize > 7 ? (player.dropSize - 7) * 32 : 26));
            player.getPA().sendConfig(DROP_AMOUNT_CONFIG_ID, player.dropSize);

            //If the game has displayed all drops and there are empty slots that haven't been filled, clear them
            if (player.dropSize < 80) {
                for (int i = player.dropSize; i < 80; i++) {
                    player.getPA().sendString("", 34200 + i);
                    player.getPA().itemOnInterface(-1, 0, 34010 + i, 0);
                    player.getPA().sendString("", 34300 + i);
                    player.getPA().sendString("", 34100 + i);
                    player.getPA().sendString("", 34400 + i);
                }
            }
            player.dropSize = 0;
        });
    }

    /**
     * Updates the interface for the selected NPC
     *
     * @param player
     * @param policy
     * @param drops
     * @param dropChance
     */
    private boolean updateAmounts(Player player, TablePolicy policy, List<Drop> drops, int dropChance) {
        drops = drops.stream().distinct().collect(Collectors.toList());

        for (Drop drop : drops) {
            boolean space = writeItem(player, drop.getItemId(), drop.getMinimumAmount(), drop.getMaximumAmount(), policy.name().toLowerCase().replaceAll("_", " "), dropChance);
            if (!space) return false;
        }

        return true;
    }

    private boolean writeItem(Player player, int itemId, int minAmount, int maxAmount, String rarityString, int dropChance) {
        int frame = (34200 + player.dropSize);//collumnOffset + (index * 2);

        //if max = min, just send the max
        if (maxAmount == maxAmount) {
            player.getPA().sendString(Misc.getValueWithoutRepresentation(minAmount), frame);
        } else {
            player.getPA().sendString(Misc.getValueWithoutRepresentation(minAmount) + " - " + Misc.getValueWithoutRepresentation(maxAmount), frame);
        }

        var def = CacheManager.INSTANCE.getItem(itemId);
        var itemName = CacheManager.INSTANCE.getItem(itemId).getName();
        var id = itemId;
        if (def.getNoted()) {
            id = itemId - 1;
            itemName = CacheManager.INSTANCE.getItem(id).getName();
        }

        if (itemName.length() > 17) {
            itemName = itemName.substring(0, 17) + "..";
        }

        player.getPA().itemOnInterface(id, minAmount, 34010 + player.dropSize, 0);
        player.getPA().sendString(Misc.optimizeText(rarityString.toLowerCase().replaceAll("_", " ")), 34300 + player.dropSize);
        player.getPA().sendString(itemName, 34100 + player.dropSize);

        if (dropChance == -10) {
            player.getPA().sendString(1 + "/?", 34400 + player.dropSize);
        } else {
            if (dropChance == 0)
                dropChance = 1;
            player.getPA().sendString(1 + "/" + dropChance, 34400 + player.dropSize);
        }

        player.dropSize++;
        return player.dropSize < 80;
    }

    static int amountt;

    /**
     * Testing droptables of chosen npcId
     *
     * @param player The player who is testing the droptable
     * @param npcId  The npc who of which the player is testing the droptable from
     * @param amount The amount of times the player want to grab a drop from the npc droptable
     */

    public void test(Player player, int npcId, int amount) {
        Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(npcId)).findFirst();

        amountt = amount;

        while (amount-- > 0) {
            group.ifPresent(g -> {
                List<GameItem> drops = g.access(player, null, 1.0, 1, npcId);

                for (GameItem item : drops) {
                    player.getItems().addItemToBankOrDrop(item.id(), item.amount());
                }
            });
        }
        player.sendMessage("Completed " + amountt + " drops from " + NpcDef.forId(npcId).getName() + ".");
    }

    public void onDrop(Player player, GameItem item, int npcId) {
        if (item.id() == 536) {
            if (player.getRechargeItems().hasItem(13111) && player.getPosition().inWild()) {
                item.changeDrop(537, item.amount());
            }
        }
        if (player.getItems().isWearingItem(21816) && player.absorption == true && item.id() == 21820 && (IntStream.of(revs).anyMatch(id -> id == npcId))) {
            item.changeDrop(-1, 1);
            int amount = Misc.random(5);
            player.braceletIncrease(amount);
            player.sendMessage("@red@The bracelet has collected some ether for you.");
        }
//		if (player.getItems().isWearingItem(21816) && player.absorption == false && item.getId() == 21820 && (IntStream.of(revs).anyMatch(id -> id == npcId))) {
//			int ether = 6 + Misc.random(6);
//			item.changeDrop(21820, ether); //basically just changes coins for ether
//		}
        if (item.id() == 995 && player.collectCoins == true && (player.getItems().freeSlots() > 0 || player.getItems().playerHasItem(995))) {
            if ((player.getItems().isWearingItem(12785)) || (player.getItems().isWearingItem(2572))) {
                player.getItems().addItem(995, item.amount());
                item.changeDrop(-1, item.amount());
                player.sendMessage("@red@The ring of wealth has collected some coins for you.");
            }
        }
        if (item.id() == 6529 && player.collectCoins == true && (player.getItems().freeSlots() > 0 || player.getItems().playerHasItem(6529))) {
            if ((player.getItems().isWearingItem(12785)) || (player.getItems().isWearingItem(2572))) {
                player.getItems().addItem(6529, item.amount());
                item.changeDrop(-1, item.amount());
                player.sendMessage("@red@The ring of wealth has collected some tokkul for you.");
            }
        }


        if (item.id() == 6529) {
            if (player.getRechargeItems().hasItem(11136)) {
                item.changeDrop(6529, (int) (item.amount() * 1.20));
            }
            if (player.getRechargeItems().hasItem(11138)) {
                item.changeDrop(6529, (int) (item.amount() * 1.50));
            }
            if (player.getRechargeItems().hasItem(11140)) {
                item.changeDrop(6529, (int) (item.amount() * 1.70));
            }
            if (player.getRechargeItems().hasItem(13103)) {
                item.changeDrop(6529, (int) (item.amount() * 1.90));
            }
        }
        if (item.id() == 6729 && player.getRechargeItems().hasItem(13132)) {
            item.changeDrop(6730, item.amount());
        }

        if (player.playerEquipment[Player.playerHands] == 22975) {
            if (item.id() == 22988 || item.id() == 22971 || item.id() == 22973 &&
                    player.getItems().getItemCount(22988, true) > 0 || player.getItems().getItemCount(22971, true) > 0
                    || player.getItems().getItemCount(22973, true) > 0 || player.getItems().getItemCount(22969, true) > 0) { //if they got all of the items, it just give them there usual item id
                item.changeDrop(item.id(), item.amount());
            }
            if (item.id() == 22988 && player.getItems().getItemCount(22988, true) > 0 &&
                    player.getItems().getItemCount(22971, false) > 0 && player.getItems().getItemCount(22969, true) > 0 && player.getItems().getItemCount(22973, true) > 0) {
                item.changeDrop(22971, item.amount());
                player.sendMessage("@red@The Brimstone ring has swapped the Hydra tail for the Hydra eye..");
            } else if (item.id() == 22988 && player.getItems().getItemCount(22988, true) > 0 &&
                    player.getItems().getItemCount(22971, true) > 0 && player.getItems().getItemCount(22969, true) > 0 && player.getItems().getItemCount(22973, false) > 0) {
                item.changeDrop(22973, item.amount());
                player.sendMessage("@red@The Brimstone ring has swapped the Hydra tail for the Hydra fang."); //thats the hydra tail drop done
            } else if (item.id() == 22988 && player.getItems().getItemCount(22988, true) > 0 &&
                    player.getItems().getItemCount(22971, true) > 0 && player.getItems().getItemCount(22969, false) > 0 && player.getItems().getItemCount(22973, true) > 0) {
                item.changeDrop(22969, item.amount());
                player.sendMessage("@red@The Brimstone ring has swapped the Hydra tail for the Hydra heart."); //thats the hydra tail drop done
            }
            if (item.id() == 22971 && player.getItems().getItemCount(22971, true) > 0 &&
                    player.getItems().getItemCount(22988, true) > 0 && player.getItems().getItemCount(22969, true) > 0 && player.getItems().getItemCount(22973, false) > 0) {
                item.changeDrop(22973, item.amount());
                player.sendMessage("@red@The Brimstone ring has swapped the Hydra fang for the Hydra eye.");
            } else if (item.id() == 22971 && player.getItems().getItemCount(22971, true) > 0 &&
                    player.getItems().getItemCount(22988, false) > 0 && player.getItems().getItemCount(22969, true) > 0 && player.getItems().getItemCount(22973, true) > 0) {
                item.changeDrop(22988, item.amount());
                player.sendMessage("@red@The Brimstone ring has swapped the Hydra fang for the Hydra tail."); //thats the hydra fang drop done
            } else if (item.id() == 22971 && player.getItems().getItemCount(22971, true) > 0 &&
                    player.getItems().getItemCount(22988, true) > 0 && player.getItems().getItemCount(22969, false) > 0 && player.getItems().getItemCount(22973, true) > 0) {
                item.changeDrop(22969, item.amount());
                player.sendMessage("@red@The Brimstone ring has swapped the Hydra fang for the Hydra heart."); //thats the hydra fang drop done
            }
            if (item.id() == 22973 && player.getItems().getItemCount(22973, true) > 0 && player.getItems().getItemCount(22969, true) > 0 &&
                    player.getItems().getItemCount(22988, true) > 0 && player.getItems().getItemCount(22971, false) > 0) {
                item.changeDrop(22971, item.amount());
                player.sendMessage("@red@The Brimstone ring has swapped the Hydra eye for the Hydra fang.");
            } else if (item.id() == 22973 && player.getItems().getItemCount(22973, true) > 0 && player.getItems().getItemCount(22969, true) > 0 &&
                    player.getItems().getItemCount(22988, false) > 0 && player.getItems().getItemCount(22971, true) > 0) {
                item.changeDrop(22988, item.amount());
                player.sendMessage("@red@The Brimstone ring has swapped the Hydra eye for the Hydra tail."); //thats the hydra eye drop done
            } else if (item.id() == 22973 && player.getItems().getItemCount(22973, true) > 0 && player.getItems().getItemCount(22969, false) > 0 &&
                    player.getItems().getItemCount(22988, true) > 0 && player.getItems().getItemCount(22971, true) > 0) {
                item.changeDrop(22969, item.amount());
                player.sendMessage("@red@The Brimstone ring has swapped the Hydra eye for the Hydra heart."); //thats the hydra eye drop done
            }
            if (item.id() == 22969 && player.getItems().getItemCount(22973, true) > 0 && player.getItems().getItemCount(22969, true) > 0 &&
                    player.getItems().getItemCount(22988, true) > 0 && player.getItems().getItemCount(22971, false) > 0) {
                item.changeDrop(22971, item.amount());
                player.sendMessage("@red@The Brimstone ring has swapped the Hydra heart for the Hydra fang.");
            } else if (item.id() == 22969 && player.getItems().getItemCount(22971, true) > 0 && player.getItems().getItemCount(22969, true) > 0 &&
                    player.getItems().getItemCount(22988, false) > 0 && player.getItems().getItemCount(22971, true) > 0) {
                item.changeDrop(22988, item.amount());
                player.sendMessage("@red@The Brimstone ring has swapped the Hydra heart for the Hydra tail."); //thats the hydra eye drop done
            } else if (item.id() == 22969 && player.getItems().getItemCount(22971, true) > 0 && player.getItems().getItemCount(22969, true) > 0 &&
                    player.getItems().getItemCount(22988, true) > 0 && player.getItems().getItemCount(22973, false) > 0) {
                item.changeDrop(22973, item.amount());
                player.sendMessage("@red@The Brimstone ring has swapped the Hydra heart for the Hydra eye."); //thats the hydra eye drop done
            }
        }
        if (item.id() == 13233 && !Boundary.isIn(player, Boundary.CERBERUS_BOSSROOMS)) {
            player.sendMessage("@red@Something hot drops from the body of your vanquished foe");
        }
    }
}