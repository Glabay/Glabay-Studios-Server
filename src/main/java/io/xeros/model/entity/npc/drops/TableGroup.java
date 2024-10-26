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
import org.apache.commons.lang3.Range;

@SuppressWarnings("serial")
public class TableGroup extends ArrayList<Table> {

    /**
     * The non-playable character that has access to this group of tables
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

            if (npc instanceof Nightmare) {
                Nightmare nightmare = (Nightmare) npc;
                if (nightmare.getRareRollPlayers().isEmpty()) {
                    int players = nightmare.getInstance() == null ? 0 : nightmare.getInstance().getPlayers().size();
                    System.err.println("No players on nightmare roll table, but " + players + " in instance.");
                } else if (!nightmare.getRareRollPlayers().contains(player) && (policy == TablePolicy.RARE || policy == TablePolicy.VERY_RARE)) {
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

                    double roll = Misc.preciseRandom(Range.between(0.0, 100.0));

                    if (chance > 100.0) {
                        chance = 100.0;
                    }
                    if (roll <= chance) {
                        Drop drop = table.fetchRandom();
                        int minimumAmount = drop.getMinimumAmount();
                        GameItem item = new GameItem(drop.getItemId(),
                                minimumAmount + Misc.random(drop.getMaximumAmount() - minimumAmount));

                        if (policy.equals(TablePolicy.VERY_RARE) || policy.equals(TablePolicy.RARE)) {
                            player.getCollectionLog().handleDrop(player, drop.getNpcIds().get(0), item.id(), item.amount());
                        }

                        // Rare drop announcements

                        // Any item names here will always announce when dropped
                        String itemNameLowerCase = ItemDef.forId(item.id()).getName().toLowerCase();
                        if (itemNameLowerCase.contains("archer ring") || itemNameLowerCase.contains("vasa minirio")
                        		|| itemNameLowerCase.contains("hydra") || itemNameLowerCase.contains("skeletal visage")) {
                            NPCDeath.announce(player, item, npcId);
                        }

                        if (itemNameLowerCase.contains("crystalline")) {
                            player.sendMessage("@pur@You notice a @blu@crystalline key@pur@ in the pile of shards!");
                        }

                        items.add(item);
                        if (policy.equals(TablePolicy.VERY_RARE) || policy.equals(TablePolicy.RARE)) {

                            String name = itemNameLowerCase;

                            // Any item names here will never announce
                            if (
                                    name.contains("cowhide")
                                    || ShopAssistant.getItemShopValue(item.id()) <= 100_000
                                    || name.contains("feather")
                                    || name.contains("dharok")
                                    || name.contains("guthan")
                                    || name.contains("karil")
                                    || name.contains("ahrim")
                                    || name.contains("verac")
                                    || name.contains("torag")
                                    || name.contains("arrow")
                                    || name.contains("sq shield")
                                    || name.contains("dragon dagger")
                                    || name.contains("rune warhammer")
                                    || name.contains("rock-shell")
                                    || name.contains("eye of newt")
                                    || name.contains("dragon spear")
                                    || name.contains("rune battleaxe")
                                    || name.contains("casket")
                                    || name.contains("silver ore")
                                    || name.contains("spined")
                                    || name.contains("wine of zamorak")
                                    || name.contains("rune spear")
                                    || name.contains("grimy")
                                    || name.contains("skeletal")
                                    || name.contains("jangerberries")
                                    || name.contains("goat horn dust")
                                    || name.contains("yew roots")
                                    || name.contains("white berries")
                                    || name.contains("bars")
                                    || name.contains("blue dragonscales")
                                    || name.contains("kebab")
                                    || name.contains("potato")
                                    || name.contains("shark")
                                    || name.contains("red")
                                    || name.contains("spined body")
                                    || name.contains("prayer")
                                    || name.contains("anchovy")
                                    || name.contains("runite")
                                    || name.contains("adamant")
                                    || name.contains("magic roots")
                                    || name.contains("earth battlestaff")
                                    || name.contains("torstol")
                                    || name.contains("dragon battle axe")
                                    || name.contains("helm of neitiznot")
                                    || name.contains("mithril")
                                    || name.contains("sapphire")
                                    || name.contains("rune")
                                    || name.contains("toktz")
                                    || name.contains("steal")
                                    || name.contains("seed")
                                    || name.contains("ancient")
                                    || name.contains("monk")
                                    || name.contains("splitbark")
                                    || name.contains("pure")
                                    || name.contains("zamorak robe")
                                    || name.contains("null")
                                    || name.contains("coins")
                                    || name.contains("essence")
                                    || name.contains("crushed")
                                    || name.contains("snape")
                                    || name.contains("unicorn")
                                    || name.contains("mystic")
                                    || name.contains("eye patch")
                                    || name.contains("steel darts")
                                    || name.contains("steel bar")
                                    || name.contains("limp")
                                    || name.contains("darts")
                                    || name.contains("dragon longsword")
                                    || name.contains("dust battlestaff")
                                    || name.contains("granite")
                                    || name.contains("coal")
                                    || name.contains("crystalline key")
                                    || name.contains("leaf-bladed sword")
                                    || name.contains("dragon plateskirt")
                                    || name.contains("dragon platelegs")
                                    || name.contains("dragon scimitar")
                                    || name.contains("abyssal head")
                                    || name.contains("cockatrice head")
                                    || name.contains("dragon chainbody")
                                    || name.contains("dragon battleaxe")
                                    || name.contains("dragon boots")
                                    || name.contains("overload")
                                    || name.contains("bones")
                                            || name.contains("amulet of the damned")
                            || item.id() >= 23490 && item.id() <= 23491 || item.id() >= 23083 && item.id() <= 23084) {
                            } else {
                                NPCDeath.announce(player, item, npcId);
                            }
                        }
                    }
                }
            }
        }
        return items;
    }

    /**
     * The non-playable character identification values that have access to this group of tables.
     *
     * @return the non-playable character id values
     */
    public List<Integer> getNpcIds() {
        return npcIds;
    }
}
