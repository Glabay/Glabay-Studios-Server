package io.xeros.content.item.lootable.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.MysteryBoxLootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

/**
 * Revamped a simple means of receiving a random item based on chance.
 *
 * @author Jason MacKeigan
 * @date Oct 29, 2014, 1:43:44 PM
 *
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public class NormalMysteryBox extends MysteryBoxLootable {

    /**
     * A map containing a List of {@link GameItem}'s that contain items relevant to their LootRarity.
     */
    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    /**
     * Stores an array of items into each map with the corresponding rarity to the list
     */
    static {

        items.put(LootRarity.COMMON, //50% chance
                Arrays.asList(
                        new GameItem(990, 15),//crystal key
                        new GameItem(268, 150),//dwarf weed
                        new GameItem(270, 150),//torstol
                        new GameItem(3001, 150),//snapdragon
                        new GameItem(12696, 150),//super combat potion(4)
                        new GameItem(3025, 200),//super restore(4)
                        new GameItem(11937, 500),//dark crab
                        new GameItem(13442, 500),//anglerfish
                        new GameItem(3145, 500),//cooked karambwan
                        new GameItem(6570),//fire cape
                        new GameItem(535, 500),//babydragon bones
                        new GameItem(11235),//dark bow
                        new GameItem(4151),//abyssal whip
                        new GameItem(12873),//guthan set
                        new GameItem(12875),//verac set
                        new GameItem(12877),//dharok set
                        new GameItem(12879),//torags set
                        new GameItem(12881),//ahrim set
                        new GameItem(12883)//karil set

                ));

        items.put(LootRarity.UNCOMMON,//40% Chance
                Arrays.asList(

                        new GameItem(11840),//dragon boots
                        new GameItem(11836),//bandos boots
                        new GameItem(6585),//amulet of fury
                        new GameItem(6737),//berserker ring
                        new GameItem(6889),//mages book
                        new GameItem(12873),//guthan set
                        new GameItem(12875),//verac set
                        new GameItem(12877),//dharok set
                        new GameItem(12879),//torags set
                        new GameItem(12881),//ahrim set
                        new GameItem(12883),//karil set
                        new GameItem(537, 250),//dragon bones
                        new GameItem(2364, 300),//runite bar
                        new GameItem(1514, 500),// magic logs
                        new GameItem(1632, 250),//uncut dragonstone
                        new GameItem(2577),//ranger boots
                        new GameItem(12596),//rangers tunic
                        new GameItem(11920),//dragon pickaxe
                        new GameItem(6739),//dragon axe
                        new GameItem(6733),//archers ring
                        new GameItem(6731),//seers ring
                        new GameItem(6735)//warrior ring
                )
        );

        items.put(LootRarity.RARE,//8% chance
                Arrays.asList(
                        new GameItem(12002),//occult necklace
                        new GameItem(11804),//BGS
                        new GameItem(11806),//SGS
                        new GameItem(11808),//ZGS
                        new GameItem(11832),//bandos chestplate
                        new GameItem(11834),//bandos tassets

                        new GameItem(11826),//armadyl helmet
                        new GameItem(11828),//armadyl chestplate
                        new GameItem(11830),//armadyl chainskirt
                        new GameItem(10346),//3rd age platelegs
                        new GameItem(10348),//3rd age platebody
                        new GameItem(10350),//3rd age helm
                        new GameItem(10352),//3rd age kite
                        new GameItem(10330),//3rd age range top
                        new GameItem(10332),//3rd age range bottom
                        new GameItem(10338),//3rd age robe top
                        new GameItem(10340),//3rd age robe bottom
                        new GameItem(10334),//3rd age range coif
                        new GameItem(10342),//3rd age mage hat
                        new GameItem(10344)));//3rd age amulet

    }

    /**
     * Constructs a new myster box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public NormalMysteryBox(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 6199;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }
}
