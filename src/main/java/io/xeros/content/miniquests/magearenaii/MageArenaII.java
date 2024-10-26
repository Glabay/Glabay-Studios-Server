package io.xeros.content.miniquests.magearenaii;

import com.google.common.collect.Lists;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.miniquests.magearenaii.npcs.Derwen;
import io.xeros.content.miniquests.magearenaii.npcs.JusticiarZachariah;
import io.xeros.content.miniquests.magearenaii.npcs.Porazdir;
import io.xeros.content.miniquests.magearenaii.npcs.type.MageArenaBossType;
import io.xeros.model.SlottedItem;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.*;
import io.xeros.util.Misc;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class MageArenaII {

    public static final int ENTS_ROOT = 21798, DEMONS_HEART = 21799, JUSTICIARS_HAND = 21797;

    public static boolean isUntradable(int itemId) {
        List<Integer> untradables = Arrays.asList(ENTS_ROOT, DEMONS_HEART, JUSTICIARS_HAND, SYMBOL_ID);
        return untradables.stream().filter(Objects::nonNull).anyMatch(i -> i.intValue() == itemId);
    }

    public static boolean hasRequirements(Player player) {
        return player.playerLevel[6] >= 75 || player.saradominStrikeCasts >= 100 && player.flamesOfZamorakCasts >= 100 && player.clawsOfGuthixCasts >= 100;
    }

    private static final int MAXIMUM_DAMAGE_FROM_SYMBOL = 16;

    public static final int SYMBOL_ID = 21800;

    /**
     * Possible npc boss spawns
     */
    private static List<Position> possible_spawns = Arrays.asList(
            new Position(3018, 3831, 0),//south of kbd
            new Position(3151, 3881, 0),//west of spiderhill
            new Position(3173, 3898, 0),//north of spiderhill
            new Position(3158, 3841, 0),//south of spiderhill level 41
            new Position(3165, 3799, 0),//east of chins level 35
            new Position(3256, 3785, 0),//north-east of corp lvl 34
            new Position(3246, 3830, 0),//south of gap lvl 39
            new Position(3269, 3843, 0),//east of gap lvl 41
            new Position(3214, 3886, 0),//west of white plato lvl 46
            new Position(3236, 3878, 0),//west of white plato lvl 45
            new Position(3259, 3886, 0),//white plato lvl 46
            new Position(3298, 3878, 0),//south of gdz hut lvl 45
            new Position(3334, 3903, 0),//new gate level 48
            new Position(3305, 3940, 0),//east of castle lvl 53
            new Position(3228, 3913, 0));//old gate lvl 50;


    public static void assignSpawns(Player player) {
        /**
         * Creates temp list
         */
        List<Position> temp = Lists.newArrayList();
        /**
         * Adds all spawns to new temp list
         */
        temp.addAll(possible_spawns);
        /**
         * Shuffles the temp list
         */
        Collections.shuffle(temp);
        /**
         * Sets array
         */
        player.mageArena2Spawns = new Position[3];
        /**
         * Sets bosses array
         */
        player.activeMageArena2BossId = new int[3];
        /**
         * Loops through array of 3
         */
        for (int i = 0; i < player.mageArena2Spawns.length; i++) {
            /**
             * Fetches random spawn within the temp list
             */
            Position pos = temp.get(Misc.random(temp.size() - 1));
            /**
             * Sets the position within the saved index to the position
             */
            //player.mageArena2Spawns[i] = pos;
            player.mageArena2SpawnsX[i] = pos.getX();
            player.mageArena2SpawnsY[i] = pos.getY();
            /**
             * Removes the position from the temp list.
             */
            temp.remove(pos);
        }
        player.mageArena2Stages[0] = true;
    }

    public static void handleEnchantedSymbol(Player player) {

        if (!Boundary.isIn(player, Boundary.WILDERNESS_PARAMETERS)) {
            player.sendMessage("<col=ff0000>You can only use this tool when you are inside the wilderness!");
            return;
        }

        player.start(new DialogueBuilder(player).
                option(
                        new DialogueOption("Saradomin", p -> findBoss(p, 0)),
                        new DialogueOption("Guthix", p -> findBoss(p,1)),
                        new DialogueOption("Zamorak", p -> findBoss(p,2)),
                        new DialogueOption("Cancel", p -> player.getPA().closeAllWindows())));

        /**
         *
         * Teleblock for 1minute randomly
         * or Ice barrage (rare)
         *
         */

    }

    private static void findBoss(Player player, int type) {
        Position position = new Position( player.mageArena2SpawnsX[type],player.mageArena2SpawnsY[type], 0);

        /**
         * Ends options dialogue
         */
        player.getDialogueBuilder().end();

        if (position == null)
            return;

        MageArenaBossType bossType = MageArenaBossType.values()[type];

        if (bossType == null)
            return;

        int bossEnumId = bossType.ordinal();

        String npcName = StringUtils.capitalize(bossType.name().toLowerCase().replaceAll("_", " "));

        if (player.mageArenaBossKills[bossEnumId] && player.hasMageArena2BossItem(bossEnumId)) {
            player.sendMessage("<col=ff0000>You have already defeated " + npcName);
            return;
        }

        if (player.activeMageArena2BossId[type] != 0) {
            /**
             * Only used to prevent multiple spawns at once.
             */
            player.sendMessage("<col=ff0000>The power in the chanted symbol already summoned "+ npcName);
            return;
        }

        int lastCheck = player.lastSymbolDistanceCheck;

        int currentDistance = player.getPosition().getDistance(position);

        int multiplierDamage = (currentDistance / 3);

        int damage = multiplierDamage > MAXIMUM_DAMAGE_FROM_SYMBOL  ? MAXIMUM_DAMAGE_FROM_SYMBOL : multiplierDamage < 1 ? 1 : multiplierDamage;

        player.appendDamage(damage, Hitmark.HIT);

        /**
         * It appears the creatures have moved locations, the symbol glows
         * slightly as it locates their new positions.
         */

        boolean closer = lastCheck == 0 ? true : lastCheck > currentDistance;

        if (currentDistance <= 10) {

            NPC npc = null;

            int npcId = MageArenaBossType.values()[type].npcId;

            if (bossType == MageArenaBossType.JUSTICIAR_ZACHARIAH)
                npc = new JusticiarZachariah(npcId, position, player);
            else if (bossType == MageArenaBossType.DERWEN)
                npc = new Derwen(npcId, position, player);
            else
                npc = new Porazdir(npcId, position, player);

            if (npc == null)
                return;

            player.activeMageArena2BossId[type] = npcId;

        } else {
            player.sendMessage("<col=ff0000>The enchanted symbol is "+getClue(currentDistance)+" and is "+(closer ? "hotter" : "colder")+" than last time.");
        }
        player.sendMessage("<col=ff0000>The power of the enchanted symbol hurts you in the process.");
        player.lastSymbolDistanceCheck = currentDistance;
    }

    private static String getClue(int distance) {
        if (distance <= 15)
            return "incredibly hot";
        else if (distance > 15 && distance <= 20)
            return "very hot";
        else if (distance > 20 && distance < 30)
            return "warm";
        return "cold";
    }

    public static boolean hasSymbol(Player player) {
        return player.getItems().bankContains(SYMBOL_ID) || player.getItems().playerHasItem(SYMBOL_ID);
    }

    public static void removeBossItems(Player player) {
        player.mageArena2Stages[1] = true;
        List<Integer> collectables = Arrays.asList(MageArenaII.ENTS_ROOT, MageArenaII.DEMONS_HEART, MageArenaII.JUSTICIARS_HAND);
        for (int i = 0; i < collectables.size(); i++) {
            player.getItems().deleteItem(collectables.get(i), 1);
        }
    }

    public static boolean hasAllItems(Player player) {
        List<Integer> collectables = Arrays.asList(MageArenaII.ENTS_ROOT, MageArenaII.DEMONS_HEART, MageArenaII.JUSTICIARS_HAND);
        int count = 0;
        for (SlottedItem i : player.getItems().getInventoryItems()) {
            if (i != null) {
                for (int inv = 0; inv < collectables.size(); inv++)
                    if (i.id() == collectables.get(inv))
                        count++;
            }
        }
        return count == 3;
    }

    public static void handleItemOnNPC(Player player, NPC npc, int itemId) {

        if (npc == null) {
            System.err.println("nulled npc");
            return;
        }

        if (!player.completedMageArena2()) {
            player.sendMessage(npc.getName()+" is not interested in that item.");
            return;
        }

        int distance = player.getPosition().getDistance(npc.getPosition());
        /**
         * Fake
         */
        if (distance > 15) {
            System.err.println("npc out of 15 tile radius currentDistance="+distance);
            return;
        }

        if (!player.getItems().playerHasItem(itemId)) {
            System.err.println("player doesn't have itemid ="+itemId);
            return;
        }

        Capes cape = isValidCape(itemId);

        if (cape == null) {
            player.sendMessage(npc.getName()+" is not interested in that item");
            return;
        }
        imbueCape(player, cape);
        return;
    }

    private static void imbueCape(Player player, Capes cape) {

        player.start(new DialogueBuilder(player).option("Imbue your "+StringUtils.capitalize(cape.name().toLowerCase())+" cape?",
                new DialogueOption("Yes", p -> {
                    player.start(new DialogueBuilder(player).itemStatement(cape.imbueCapeId, "Kolodion takes your cape and imbues it with the power", "of the gods before handing it back."));
                    player.getItems().deleteItem(cape.capeId, 1);
                    player.getItems().addItem(cape.imbueCapeId, 1);
                }),
                new DialogueOption("No", p -> {
                    p.getPA().closeAllWindows();
                })));
    }

    public static Capes isValidCape(int itemId) {
        for (Capes cape : Capes.values()) {
            if (cape.capeId == itemId)
                return cape;
        }
        return null;
    }
}
