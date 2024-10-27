package io.xeros.model.entity.npc.drops;

import java.util.ArrayList;
import java.util.List;

import io.xeros.content.bosses.nightmare.Nightmare;
import io.xeros.content.combat.death.NPCDeath;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.shops.ShopAssistant;
import io.xeros.util.Misc;
import lombok.Getter;
import org.apache.commons.lang3.Range;

@Getter
public class TableGroup extends ArrayList<Table> {

    /**
     * The non-playable character that has access to this group of tables
     * -- GETTER --
     *  The non-playable character identification values that have access to this group of tables.
     *
     * @return the non-playable character id values

     */
    private final List<Integer> npcIds;

    /**
     * Creates a new group of tables
     *
     */
    public TableGroup(List<Integer> npcsIds) {
        this.npcIds = npcsIds;
    }

    /**
     * Accesses each {@link Table} in this {@link TableGroup} with hopes of retrieving a {@link List} of {@link GameItem} objects.
     *
     * @return
     */
    public List<GameItem> access(Player player, NPC npc, double modifier, int repeats, int npcId) {
        List<GameItem> items = new ArrayList<>();
        for (Table table : this) {
            TablePolicy policy = table.getPolicy();
            boolean rareTableDrop = policy.equals(TablePolicy.VERY_RARE) || policy.equals(TablePolicy.RARE);

            if (npc instanceof Nightmare nightmare) {
                if (nightmare.getRareRollPlayers().isEmpty()) {
                    int players = nightmare.getInstance() == null ? 0 : nightmare.getInstance().getPlayers().size();
                    System.err.println("No players on nightmare roll table, but " + players + " in instance.");
                }
                else if (!nightmare.getRareRollPlayers().contains(player) && rareTableDrop) {
                    continue;
                }
            }

            if (policy.equals(TablePolicy.CONSTANT)) {
                for (Drop drop : table) {
                    int minimumAmount = drop.getMinimumAmount();
                    items.add(new GameItem(drop.getItemId(), minimumAmount + Misc.random(drop.getMaximumAmount() - minimumAmount)));
                }
            } else {
                for (int i = 0; i < repeats; i++) {
                    double chance = (1.0 / (table.getAccessibility() * modifier)) * 100D;
                    double roll = Misc.preciseRandom(Range.of(0.0, 100.0));

                    if (chance > 100.0)
                        chance = 100.0;

                    if (roll <= chance) {
                        Drop drop = table.fetchRandom();
                        int minimumAmount = drop.getMinimumAmount();
                        GameItem item = new GameItem(drop.getItemId(),
                                minimumAmount + Misc.random(drop.getMaximumAmount() - minimumAmount));

                        if (rareTableDrop)
                            player.getCollectionLog().handleDrop(player, drop.getNpcIds().getFirst(), item.id(), item.amount());
                        // Rare drop announcements
                        // Any item names here will always announce when dropped
                        var itemNameLowerCase = ItemDef.forId(item.id()).getName().toLowerCase();

                        if (itemNameLowerCase.contains("crystalline"))
                            player.sendMessage("@pur@You notice a @blu@crystalline key@pur@ in the pile of shards!");

                        items.add(item);
                        if (rareTableDrop && !applyFilter(itemNameLowerCase, item)) {
                            NPCDeath.announce(player, item, npcId);
                            // TODO: Discord Integration; AnnouncedDrop
                        }
                    }
                }
            }
        }
        return items;
    }

    private boolean applyFilter(String itemNameLowerCase, GameItem item) {
        // Any item names here will never announce
        return itemKeywords.stream().anyMatch(itemNameLowerCase::contains)
            || ShopAssistant.getItemShopValue(item.id()) <= 100_000
            || item.id() >= 23490 && item.id() <= 23491
            || item.id() >= 23083 && item.id() <= 23084;
    }

    private final List<String> itemKeywords = List.of(
        "cowhide",
        "feather",
        "dharok",
        "guthan",
        "karil",
        "ahrim",
        "verac",
        "torag",
        "arrow",
        "sq shield",
        "dragon dagger",
        "rune warhammer",
        "rock-shell",
        "eye of newt",
        "dragon spear",
        "rune battleaxe",
        "casket",
        "silver ore",
        "spined",
        "wine of zamorak",
        "rune spear",
        "grimy",
        "skeletal",
        "jangerberries",
        "goat horn dust",
        "yew roots",
        "white berries",
        "bars",
        "blue dragonscales",
        "kebab",
        "potato",
        "shark",
        "red",
        "spined body",
        "prayer",
        "anchovy",
        "runite",
        "adamant",
        "magic roots",
        "earth battlestaff",
        "torstol",
        "dragon battle axe",
        "helm of neitiznot",
        "mithril",
        "sapphire",
        "rune",
        "toktz",
        "steal",
        "seed",
        "ancient",
        "monk",
        "splitbark",
        "pure",
        "zamorak robe",
        "null",
        "coins",
        "essence",
        "crushed",
        "snape",
        "unicorn",
        "mystic",
        "eye patch",
        "steel darts",
        "steel bar",
        "limp",
        "darts",
        "dragon longsword",
        "dust battlestaff",
        "granite",
        "coal",
        "crystalline key",
        "leaf-bladed sword",
        "dragon plateskirt",
        "dragon platelegs",
        "dragon scimitar",
        "abyssal head",
        "cockatrice head",
        "dragon chainbody",
        "dragon battleaxe",
        "dragon boots",
        "overload",
        "bones",
        "amulet of the damned"
    );
}
