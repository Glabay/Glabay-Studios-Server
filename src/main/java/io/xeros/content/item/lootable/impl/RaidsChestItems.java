package io.xeros.content.item.lootable.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.minigames.raids.Raids;
import io.xeros.model.definition.Items;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.GameItemVariableAmount;
import io.xeros.util.Misc;

public class RaidsChestItems {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static Map<LootRarity, List<GameItem>> getItems() {
        return items;
    }

    static {
        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(Raids.RARE_KEY, 1),
                new GameItemVariableAmount(23686, 1, 3),  //divine combat 4
                new GameItemVariableAmount(23734, 1, 3),  //divine range 4
                new GameItemVariableAmount(23746, 1, 3),  //divine mage 4
                new GameItem(11733, 1),  //overload (1)
                new GameItemVariableAmount(566, 50, 100),  //soul rune   6
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune
                new GameItemVariableAmount(892, 50, 100), //rune arrow
                new GameItemVariableAmount(11212, 10, 22), //dragon arrow
                new GameItemVariableAmount(3050, 10, 20), //grimy toadflax
                new GameItemVariableAmount(210, 10, 15), //grimy irit
                new GameItemVariableAmount(212, 10, 20), //grimy avantoe
                new GameItemVariableAmount(214, 10, 20), //grimy kwuarm
                new GameItemVariableAmount(216, 10, 21), //grimy candatine
                new GameItemVariableAmount(2486, 10, 23), //grimy landatyme
                new GameItemVariableAmount(218, 10, 22), //dwarf weed
                new GameItemVariableAmount(220, 10, 10), //torstol
                new GameItemVariableAmount(454, 20, 150), //coal
                new GameItemVariableAmount(13440, 10, 30), //anglerfish
                new GameItemVariableAmount(448, 10, 45), //mith ore
                new GameItemVariableAmount(450, 10, 28), //addy ore
                new GameItemVariableAmount(19484, 10, 18), //dragon javelin
                new GameItemVariableAmount(452, 10, 15), //runite ore
                new GameItemVariableAmount(1776, 10, 20), //molten glass
                new GameItemVariableAmount(1624, 10, 10), //uncut saphire
                new GameItemVariableAmount(1374, 10, 12), //rune b axe
                new GameItemVariableAmount(1080, 3, 10), //rune platelegs
                new GameItemVariableAmount(1128, 5, 8), //rune platebody
                new GameItemVariableAmount(3139, 10, 17), //potato cactus
                new GameItemVariableAmount(1392, 10, 13), //battle staff
                new GameItemVariableAmount(1622, 5, 18), //uncut emerald
                new GameItemVariableAmount(1620, 5, 12), //uncut ruby
                new GameItemVariableAmount(384, 10, 20), //raw shark
                new GameItemVariableAmount(7937, 200, 400), //pure essence
                new GameItemVariableAmount(1618, 6, 55), //uncut diamonds
                new GameItemVariableAmount(23686, 1, 3),  //divine combat 4
                new GameItemVariableAmount(23734, 1, 3),  //divine range 4
                new GameItemVariableAmount(23746, 1, 3),  //divine mage 4
                new GameItem(6694, 5 + Misc.random(10)), //crushed nests grass
                new GameItem(2971, 5 + Misc.random(10)), //mort myre fungus
                new GameItem(246, 5 + Misc.random(10)), //wine of zamoraks
                new GameItem(3055, 1 + Misc.random(3)), //lava battlestaff
                new GameItemVariableAmount(23686, 1, 3),  //divine combat 4
                new GameItemVariableAmount(23734, 1, 3),  //divine range 4
                new GameItemVariableAmount(23746, 1, 3),  //divine mage 4
                new GameItem(11733, 1),  //overload (1)
                new GameItemVariableAmount(566, 50, 100),  //soul rune   6
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune
                new GameItemVariableAmount(892, 50, 100), //rune arrow
                new GameItemVariableAmount(11212, 10, 22), //dragon arrow
                new GameItemVariableAmount(3050, 10, 20), //grimy toadflax
                new GameItemVariableAmount(210, 10, 15), //grimy irit
                new GameItemVariableAmount(212, 10, 20), //grimy avantoe
                new GameItemVariableAmount(214, 10, 20), //grimy kwuarm
                new GameItemVariableAmount(216, 10, 21), //grimy candatine
                new GameItemVariableAmount(2486, 10, 23), //grimy landatyme
                new GameItemVariableAmount(218, 10, 22), //dwarf weed
                new GameItemVariableAmount(220, 10, 10), //torstol
                new GameItemVariableAmount(454, 20, 150), //coal
                new GameItemVariableAmount(13440, 10, 30), //anglerfish
                new GameItemVariableAmount(448, 10, 45), //mith ore
                new GameItemVariableAmount(450, 10, 28), //addy ore
                new GameItemVariableAmount(19484, 10, 18), //dragon javelin
                new GameItemVariableAmount(452, 10, 15), //runite ore
                new GameItemVariableAmount(1776, 10, 20), //molten glass
                new GameItemVariableAmount(1624, 10, 10), //uncut saphire
                new GameItemVariableAmount(1374, 10, 12), //rune b axe
                new GameItemVariableAmount(1080, 3, 10), //rune platelegs
                new GameItemVariableAmount(1128, 5, 8), //rune platebody
                new GameItemVariableAmount(3139, 10, 17), //potato cactus
                new GameItemVariableAmount(1392, 10, 13), //battle staff
                new GameItemVariableAmount(1622, 5, 18), //uncut emerald
                new GameItemVariableAmount(1620, 5, 12), //uncut ruby
                new GameItemVariableAmount(384, 10, 20), //raw shark
                new GameItemVariableAmount(7937, 200, 400), //pure essence
                new GameItemVariableAmount(1618, 6, 55), //uncut diamonds
                new GameItemVariableAmount(23686, 1, 3),  //divine combat 4
                new GameItemVariableAmount(23734, 1, 3),  //divine range 4
                new GameItemVariableAmount(23746, 1, 3),  //divine mage 4
                new GameItem(11733, 1),  //overload (1)
                new GameItemVariableAmount(566, 50, 100),  //soul rune   6
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune
                new GameItemVariableAmount(892, 50, 100), //rune arrow
                new GameItemVariableAmount(11212, 10, 22), //dragon arrow
                new GameItemVariableAmount(3050, 10, 20), //grimy toadflax
                new GameItemVariableAmount(210, 10, 15), //grimy irit
                new GameItemVariableAmount(212, 10, 20), //grimy avantoe
                new GameItemVariableAmount(214, 10, 20), //grimy kwuarm
                new GameItemVariableAmount(216, 10, 21), //grimy candatine
                new GameItemVariableAmount(2486, 10, 23), //grimy landatyme
                new GameItemVariableAmount(218, 10, 22), //dwarf weed
                new GameItemVariableAmount(220, 10, 10), //torstol
                new GameItemVariableAmount(454, 20, 150), //coal
                new GameItemVariableAmount(13440, 10, 30), //anglerfish
                new GameItemVariableAmount(448, 10, 45), //mith ore
                new GameItemVariableAmount(450, 10, 28), //addy ore
                new GameItemVariableAmount(19484, 10, 18), //dragon javelin
                new GameItemVariableAmount(452, 10, 15), //runite ore
                new GameItemVariableAmount(1776, 10, 20), //molten glass
                new GameItemVariableAmount(1624, 10, 10), //uncut saphire
                new GameItemVariableAmount(1374, 10, 12), //rune b axe
                new GameItemVariableAmount(1080, 3, 10), //rune platelegs
                new GameItemVariableAmount(1128, 5, 8), //rune platebody
                new GameItemVariableAmount(3139, 10, 17), //potato cactus
                new GameItemVariableAmount(1392, 10, 13), //battle staff
                new GameItemVariableAmount(1622, 5, 18), //uncut emerald
                new GameItemVariableAmount(1620, 5, 12), //uncut ruby
                new GameItemVariableAmount(384, 10, 20), //raw shark
                new GameItemVariableAmount(7937, 200, 400), //pure essence
                new GameItemVariableAmount(1618, 6, 55), //uncut diamonds
                new GameItem(Items.DARK_RELIC, 1), //dark relic
                new GameItem(224, 5 + Misc.random(10)), //snape grass
                new GameItem(222, 5 + Misc.random(10)), //eye of newt
                new GameItem(240, 5 + Misc.random(10)), //white berries
                new GameItem(226, 5 + Misc.random(10)), //limp roots
                new GameItem(232, 5 + Misc.random(10)), //snape grass
                new GameItem(6694, 5 + Misc.random(10)), //crushed nests grass
                new GameItem(2971, 5 + Misc.random(10)), //mort myre fungus
                new GameItem(246, 5 + Misc.random(10)), //wine of zamoraks
                new GameItem(3055, 1 + Misc.random(3)), //lava battlestaff
                new GameItemVariableAmount(23686, 1, 3),  //divine combat 4
                new GameItemVariableAmount(23734, 1, 3),  //divine range 4
                new GameItemVariableAmount(23746, 1, 3),  //divine mage 4
                new GameItem(11733, 1),  //overload (1)
                new GameItemVariableAmount(566, 50, 100),  //soul rune   6
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune
                new GameItemVariableAmount(892, 50, 100), //rune arrow
                new GameItemVariableAmount(11212, 10, 22), //dragon arrow
                new GameItemVariableAmount(3050, 10, 20), //grimy toadflax
                new GameItemVariableAmount(210, 10, 15), //grimy irit
                new GameItemVariableAmount(212, 10, 20), //grimy avantoe
                new GameItemVariableAmount(214, 10, 20), //grimy kwuarm
                new GameItemVariableAmount(216, 10, 21), //grimy candatine
                new GameItemVariableAmount(2486, 10, 23), //grimy landatyme
                new GameItemVariableAmount(218, 10, 22), //dwarf weed
                new GameItemVariableAmount(220, 10, 10), //torstol
                new GameItemVariableAmount(454, 20, 150), //coal
                new GameItemVariableAmount(13440, 10, 30), //anglerfish
                new GameItemVariableAmount(448, 10, 45), //mith ore
                new GameItemVariableAmount(450, 10, 28), //addy ore
                new GameItemVariableAmount(19484, 10, 18), //dragon javelin
                new GameItemVariableAmount(452, 10, 15), //runite ore
                new GameItemVariableAmount(1776, 10, 20), //molten glass
                new GameItemVariableAmount(1624, 10, 10), //uncut saphire
                new GameItemVariableAmount(1374, 10, 12), //rune b axe
                new GameItemVariableAmount(1080, 3, 10), //rune platelegs
                new GameItemVariableAmount(1128, 5, 8), //rune platebody
                new GameItemVariableAmount(3139, 10, 17), //potato cactus
                new GameItemVariableAmount(1392, 10, 13), //battle staff
                new GameItemVariableAmount(1622, 5, 18), //uncut emerald
                new GameItemVariableAmount(1620, 5, 12), //uncut ruby
                new GameItemVariableAmount(384, 10, 20), //raw shark
                new GameItemVariableAmount(7937, 200, 400), //pure essence
                new GameItemVariableAmount(1618, 6, 55), //uncut diamonds
                new GameItemVariableAmount(23686, 1, 3),  //divine combat 4
                new GameItemVariableAmount(23734, 1, 3),  //divine range 4
                new GameItemVariableAmount(23746, 1, 3),  //divine mage 4
                new GameItem(11733, 1),  //overload (1)
                new GameItemVariableAmount(566, 50, 100),  //soul rune   6
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune
                new GameItemVariableAmount(892, 50, 100), //rune arrow
                new GameItemVariableAmount(11212, 10, 22), //dragon arrow
                new GameItemVariableAmount(3050, 10, 20), //grimy toadflax
                new GameItemVariableAmount(210, 10, 15), //grimy irit
                new GameItemVariableAmount(212, 10, 20), //grimy avantoe
                new GameItemVariableAmount(214, 10, 20), //grimy kwuarm
                new GameItemVariableAmount(216, 10, 21), //grimy candatine
                new GameItemVariableAmount(2486, 10, 23), //grimy landatyme
                new GameItemVariableAmount(218, 10, 22), //dwarf weed
                new GameItemVariableAmount(220, 10, 10), //torstol
                new GameItemVariableAmount(454, 20, 150), //coal
                new GameItemVariableAmount(13440, 10, 30), //anglerfish
                new GameItemVariableAmount(448, 10, 45), //mith ore
                new GameItemVariableAmount(450, 10, 28), //addy ore
                new GameItemVariableAmount(19484, 10, 18), //dragon javelin
                new GameItemVariableAmount(452, 10, 15), //runite ore
                new GameItemVariableAmount(1776, 10, 20), //molten glass
                new GameItemVariableAmount(1624, 10, 10), //uncut saphire
                new GameItemVariableAmount(1374, 10, 12), //rune b axe
                new GameItemVariableAmount(1080, 3, 10), //rune platelegs
                new GameItemVariableAmount(1128, 5, 8), //rune platebody
                new GameItemVariableAmount(3139, 10, 17), //potato cactus
                new GameItemVariableAmount(1392, 10, 13), //battle staff
                new GameItemVariableAmount(1622, 5, 18), //uncut emerald
                new GameItemVariableAmount(1620, 5, 12), //uncut ruby
                new GameItemVariableAmount(384, 10, 20), //raw shark
                new GameItemVariableAmount(7937, 200, 400), //pure essence
                new GameItemVariableAmount(1618, 6, 55), //uncut diamonds
                new GameItem(21027, 1), //dark relic
                new GameItem(224, 5 + Misc.random(10)), //snape grass
                new GameItem(222, 5 + Misc.random(10)), //eye of newt
                new GameItem(240, 5 + Misc.random(10)), //white berries
                new GameItem(226, 5 + Misc.random(10)), //limp roots
                new GameItem(232, 5 + Misc.random(10)), //snape grass
                new GameItem(6694, 5 + Misc.random(10)), //crushed nests grass
                new GameItem(2971, 5 + Misc.random(10)), //mort myre fungus
                new GameItem(246, 5 + Misc.random(10)), //wine of zamoraks
                new GameItem(3055, 1 + Misc.random(3)), //lava battlestaff
                new GameItemVariableAmount(23686, 1, 3),  //divine combat 4
                new GameItemVariableAmount(23734, 1, 3),  //divine range 4
                new GameItemVariableAmount(23746, 1, 3),  //divine mage 4
                new GameItem(11733, 1),  //overload (1)
                new GameItemVariableAmount(566, 50, 100),  //soul rune   6
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune
                new GameItemVariableAmount(892, 50, 100), //rune arrow
                new GameItemVariableAmount(11212, 10, 22), //dragon arrow
                new GameItemVariableAmount(3050, 10, 20), //grimy toadflax
                new GameItemVariableAmount(210, 10, 15), //grimy irit
                new GameItemVariableAmount(212, 10, 20), //grimy avantoe
                new GameItemVariableAmount(214, 10, 20), //grimy kwuarm
                new GameItemVariableAmount(216, 10, 21), //grimy candatine
                new GameItemVariableAmount(2486, 10, 23), //grimy landatyme
                new GameItemVariableAmount(218, 10, 22), //dwarf weed
                new GameItemVariableAmount(220, 10, 10), //torstol
                new GameItemVariableAmount(454, 20, 150), //coal
                new GameItemVariableAmount(13440, 10, 30), //anglerfish
                new GameItemVariableAmount(448, 10, 45), //mith ore
                new GameItemVariableAmount(450, 10, 28), //addy ore
                new GameItemVariableAmount(19484, 10, 18), //dragon javelin
                new GameItemVariableAmount(452, 10, 15), //runite ore
                new GameItemVariableAmount(1776, 10, 20), //molten glass
                new GameItemVariableAmount(1624, 10, 10), //uncut saphire
                new GameItemVariableAmount(1374, 10, 12), //rune b axe
                new GameItemVariableAmount(1080, 3, 10), //rune platelegs
                new GameItemVariableAmount(1128, 5, 8), //rune platebody
                new GameItemVariableAmount(3139, 10, 17), //potato cactus
                new GameItemVariableAmount(1392, 10, 13), //battle staff
                new GameItemVariableAmount(1622, 5, 18), //uncut emerald
                new GameItemVariableAmount(1620, 5, 12), //uncut ruby
                new GameItemVariableAmount(384, 10, 20), //raw shark
                new GameItemVariableAmount(7937, 200, 400), //pure essence
                new GameItemVariableAmount(1618, 6, 55), //uncut diamonds
                new GameItemVariableAmount(23686, 1, 3),  //divine combat 4
                new GameItemVariableAmount(23734, 1, 3),  //divine range 4
                new GameItemVariableAmount(23746, 1, 3),  //divine mage 4
                new GameItem(11733, 1),  //overload (1)
                new GameItemVariableAmount(566, 50, 100),  //soul rune   6
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune
                new GameItemVariableAmount(892, 50, 100), //rune arrow
                new GameItemVariableAmount(11212, 10, 22), //dragon arrow
                new GameItemVariableAmount(3050, 10, 20), //grimy toadflax
                new GameItemVariableAmount(210, 10, 15), //grimy irit
                new GameItemVariableAmount(212, 10, 20), //grimy avantoe
                new GameItemVariableAmount(214, 10, 20), //grimy kwuarm
                new GameItemVariableAmount(216, 10, 21), //grimy candatine
                new GameItemVariableAmount(2486, 10, 23), //grimy landatyme
                new GameItemVariableAmount(218, 10, 22), //dwarf weed
                new GameItemVariableAmount(220, 10, 10), //torstol
                new GameItemVariableAmount(454, 20, 150), //coal
                new GameItemVariableAmount(13440, 10, 30), //anglerfish
                new GameItemVariableAmount(448, 10, 45), //mith ore
                new GameItemVariableAmount(450, 10, 28), //addy ore
                new GameItemVariableAmount(19484, 10, 18), //dragon javelin
                new GameItemVariableAmount(565, 50, 100), //blood rune
                new GameItemVariableAmount(892, 50, 100), //rune arrow
                new GameItemVariableAmount(11212, 10, 22), //dragon arrow
                new GameItemVariableAmount(3050, 10, 20), //grimy toadflax
                new GameItemVariableAmount(210, 10, 15), //grimy irit
                new GameItemVariableAmount(212, 10, 20), //grimy avantoe
                new GameItemVariableAmount(214, 10, 20), //grimy kwuarm
                new GameItemVariableAmount(216, 10, 21), //grimy candatine
                new GameItemVariableAmount(2486, 10, 23), //grimy landatyme
                new GameItemVariableAmount(218, 10, 22), //dwarf weed
                new GameItemVariableAmount(220, 10, 10), //torstol
                new GameItemVariableAmount(454, 20, 150), //coal
                new GameItemVariableAmount(13440, 10, 30), //anglerfish
                new GameItemVariableAmount(448, 10, 45), //mith ore
                new GameItemVariableAmount(450, 10, 28), //addy ore
                new GameItemVariableAmount(19484, 10, 18), //dragon javelin
                new GameItemVariableAmount(452, 10, 15), //runite ore
                new GameItemVariableAmount(1776, 10, 20), //molten glass
                new GameItemVariableAmount(1624, 10, 10), //uncut saphire
                new GameItemVariableAmount(1374, 10, 12), //rune b axe
                new GameItemVariableAmount(1080, 3, 10), //rune platelegs
                new GameItemVariableAmount(1128, 5, 8), //rune platebody
                new GameItemVariableAmount(3139, 10, 17), //potato cactus
                new GameItemVariableAmount(1392, 10, 13), //battle staff
                new GameItemVariableAmount(1622, 5, 18), //uncut emerald
                new GameItemVariableAmount(1620, 5, 12), //uncut ruby
                new GameItemVariableAmount(384, 10, 20), //raw shark
                new GameItemVariableAmount(7937, 200, 400), //pure essence
                new GameItemVariableAmount(1618, 6, 55), //uncut diamonds
                new GameItemVariableAmount(23686, 1, 3),  //divine combat 4
                new GameItemVariableAmount(23734, 1, 3),  //divine range 4
                new GameItemVariableAmount(23746, 1, 3),  //divine mage 4
                new GameItem(11733, 1),  //overload (1)
                new GameItemVariableAmount(566, 50, 100),  //soul rune   6
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune
                new GameItemVariableAmount(892, 50, 100), //rune arrow
                new GameItemVariableAmount(11212, 10, 22), //dragon arrow
                new GameItemVariableAmount(3050, 10, 20), //grimy toadflax
                new GameItemVariableAmount(210, 10, 15), //grimy irit
                new GameItemVariableAmount(212, 10, 20), //grimy avantoe
                new GameItemVariableAmount(214, 10, 20), //grimy kwuarm
                new GameItemVariableAmount(216, 10, 21), //grimy candatine
                new GameItemVariableAmount(2486, 10, 23), //grimy landatyme
                new GameItemVariableAmount(218, 10, 22), //dwarf weed
                new GameItemVariableAmount(220, 10, 10), //torstol
                new GameItemVariableAmount(454, 20, 150), //coal
                new GameItemVariableAmount(13440, 10, 30), //anglerfish
                new GameItemVariableAmount(448, 10, 45), //mith ore
                new GameItemVariableAmount(450, 10, 28), //addy ore
                new GameItemVariableAmount(19484, 10, 18), //dragon javelin
                new GameItemVariableAmount(452, 10, 15), //runite ore
                new GameItemVariableAmount(1776, 10, 20), //molten glass
                new GameItemVariableAmount(1624, 10, 10), //uncut saphire
                new GameItemVariableAmount(1374, 10, 12), //rune b axe
                new GameItemVariableAmount(1080, 3, 10), //rune platelegs
                new GameItemVariableAmount(1128, 5, 8), //rune platebody
                new GameItemVariableAmount(3139, 10, 17), //potato cactus
                new GameItemVariableAmount(1392, 10, 13), //battle staff
                new GameItemVariableAmount(1622, 5, 18), //uncut emerald
                new GameItemVariableAmount(1620, 5, 12), //uncut ruby
                new GameItemVariableAmount(384, 10, 20), //raw shark
                new GameItemVariableAmount(7937, 200, 400), //pure essence
                new GameItemVariableAmount(1618, 6, 55), //uncut diamonds
                new GameItem(21027, 1), //dark relic
                new GameItem(224, 5 + Misc.random(10)), //snape grass
                new GameItem(222, 5 + Misc.random(10)), //eye of newt
                new GameItem(240, 5 + Misc.random(10)), //white berries
                new GameItem(226, 5 + Misc.random(10)), //limp roots
                new GameItem(232, 5 + Misc.random(10)), //snape grass
                new GameItem(6694, 5 + Misc.random(10)), //crushed nests grass
                new GameItem(2971, 5 + Misc.random(10)), //mort myre fungus
                new GameItem(246, 5 + Misc.random(10)), //wine of zamoraks
                new GameItem(3055, 1 + Misc.random(3)), //lava battlestaff
                new GameItemVariableAmount(23686, 1, 3),  //divine combat 4
                new GameItemVariableAmount(23734, 1, 3),  //divine range 4
                new GameItemVariableAmount(23746, 1, 3),  //divine mage 4
                new GameItem(11733, 1),  //overload (1)
                new GameItemVariableAmount(566, 50, 100),  //soul rune   6
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune
                new GameItemVariableAmount(892, 50, 100), //rune arrow
                new GameItemVariableAmount(11212, 10, 22), //dragon arrow
                new GameItemVariableAmount(3050, 10, 20), //grimy toadflax
                new GameItemVariableAmount(210, 10, 15), //grimy irit
                new GameItemVariableAmount(212, 10, 20), //grimy avantoe
                new GameItemVariableAmount(214, 10, 20), //grimy kwuarm
                new GameItemVariableAmount(216, 10, 21), //grimy candatine
                new GameItemVariableAmount(2486, 10, 23), //grimy landatyme
                new GameItemVariableAmount(218, 10, 22), //dwarf weed
                new GameItemVariableAmount(220, 10, 10), //torstol
                new GameItemVariableAmount(454, 20, 150), //coal
                new GameItemVariableAmount(13440, 10, 30), //anglerfish
                new GameItemVariableAmount(448, 10, 45), //mith ore
                new GameItemVariableAmount(450, 10, 28), //addy ore
                new GameItemVariableAmount(19484, 10, 18), //dragon javelin
                new GameItemVariableAmount(1776, 10, 20), //molten glass
                new GameItemVariableAmount(1624, 10, 10), //uncut saphire
                new GameItemVariableAmount(1374, 10, 12), //rune b axe
                new GameItemVariableAmount(1080, 3, 10), //rune platelegs
                new GameItemVariableAmount(1128, 5, 8), //rune platebody
                new GameItemVariableAmount(3139, 10, 17), //potato cactus
                new GameItemVariableAmount(1392, 10, 13), //battle staff
                new GameItemVariableAmount(1622, 5, 18), //uncut emerald
                new GameItemVariableAmount(1620, 5, 12), //uncut ruby
                new GameItemVariableAmount(384, 10, 20), //raw shark
                new GameItemVariableAmount(7937, 200, 400), //pure essence
                new GameItemVariableAmount(1618, 6, 55), //uncut diamonds
                new GameItemVariableAmount(23686, 1, 3),  //divine combat 4
                new GameItemVariableAmount(23734, 1, 3),  //divine range 4
                new GameItemVariableAmount(23746, 1, 3),  //divine mage 4
                new GameItem(11733, 1),  //overload (1)
                new GameItemVariableAmount(566, 50, 100),  //soul rune   6
                new GameItemVariableAmount(560, 50, 100),  //death rune
                new GameItemVariableAmount(565, 50, 100), //blood rune
                new GameItemVariableAmount(892, 50, 100), //rune arrow
                new GameItemVariableAmount(11212, 10, 22), //dragon arrow
                new GameItemVariableAmount(3050, 10, 20), //grimy toadflax
                new GameItemVariableAmount(210, 10, 15), //grimy irit
                new GameItemVariableAmount(212, 10, 20), //grimy avantoe
                new GameItemVariableAmount(214, 10, 20), //grimy kwuarm
                new GameItemVariableAmount(216, 10, 21), //grimy candatine
                new GameItemVariableAmount(2486, 10, 23), //grimy landatyme
                new GameItemVariableAmount(218, 10, 22), //dwarf weed
                new GameItemVariableAmount(220, 10, 10), //torstol
                new GameItemVariableAmount(454, 20, 150), //coal
                new GameItemVariableAmount(13440, 10, 30), //anglerfish
                new GameItemVariableAmount(448, 10, 45), //mith ore
                new GameItemVariableAmount(450, 10, 28), //addy ore
                new GameItemVariableAmount(19484, 10, 18), //dragon javelin
                new GameItemVariableAmount(452, 10, 15), //runite ore
                new GameItemVariableAmount(1776, 10, 20), //molten glass
                new GameItemVariableAmount(1624, 10, 10), //uncut saphire
                new GameItemVariableAmount(1374, 10, 12), //rune b axe
                new GameItemVariableAmount(1080, 3, 10), //rune platelegs
                new GameItemVariableAmount(1128, 5, 8), //rune platebody
                new GameItemVariableAmount(3139, 10, 17), //potato cactus
                new GameItemVariableAmount(1392, 10, 13), //battle staff
                new GameItemVariableAmount(1622, 5, 18), //uncut emerald
                new GameItemVariableAmount(1620, 5, 12), //uncut ruby
                new GameItemVariableAmount(384, 10, 20), //raw shark
                new GameItemVariableAmount(7937, 200, 400), //pure essence
                new GameItemVariableAmount(1618, 6, 55), //uncut diamonds
                new GameItem(21027, 1), //dark relic
                new GameItem(224, 5 + Misc.random(10)), //snape grass
                new GameItem(222, 5 + Misc.random(10)), //eye of newt
                new GameItem(240, 5 + Misc.random(10)), //white berries
                new GameItem(226, 5 + Misc.random(10)), //limp roots
                new GameItem(232, 5 + Misc.random(10)), //snape grass
                new GameItem(6694, 5 + Misc.random(10)), //crushed nests grass
                new GameItem(2971, 5 + Misc.random(10)), //mort myre fungus
                new GameItem(246, 5 + Misc.random(10)), //wine of zamoraks
                new GameItem(3055, 1 + Misc.random(3)), //lava battlestaff
                new GameItem(3052, 23))); //grimy snap dragons


        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(21079, 1), //dex scroll   		        COMMON
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll
                new GameItem(21079, 1), //dex scroll   		        COMMON
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll
                new GameItem(21079, 1), //dex scroll   		        COMMON
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll
                new GameItem(21079, 1), //dex scroll    		    COMMON
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll
                new GameItem(21079, 1), //dex scroll       		   COMMON
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll
                new GameItem(21079, 1), //dex scroll      		    COMMON
                new GameItem(21000, 1),  //twisted buckler
                new GameItem(21015, 1),  //dinhs bulwark
                new GameItem(21012, 1),  //dragon hunter crossbow
                new GameItem(21006, 1),  //koadi wand
                new GameItem(21034, 1), //arcane scroll
                new GameItem(21003, 1), //elder maul      	 	 UNCOMMON
                new GameItem(20784, 1),  //D claws
                new GameItem(21018, 1),//ancestral hat
                new GameItem(21021, 1),//ancestral top
                new GameItem(21024, 1),//ancestral bottom
                new GameItem(21003, 1), //elder maul      		  UNCOMMON
                new GameItem(20784, 1),  //D claws
                new GameItem(21018, 1),//ancestral hat
                new GameItem(21021, 1),//ancestral top
                new GameItem(21024, 1),//ancestral bottom
                new GameItem(21079, 1), //dex scroll
                new GameItem(21003, 1), //elder maul      	 	 UNCOMMON
                new GameItem(20784, 1),  //D claws
                new GameItem(21018, 1),//ancestral hat
                new GameItem(21021, 1),//ancestral top
                new GameItem(21024, 1),//ancestral bottom
                new GameItem(21003, 1), //elder maul      	 	 UNCOMMON
                new GameItem(20784, 1),  //D claws
                new GameItem(21018, 1),//ancestral hat
                new GameItem(21021, 1),//ancestral top
                new GameItem(21024, 1),//ancestral bottom
                new GameItem(20997, 1),  //twisted bow			RARE
                new GameItem(20851, 1)));  //olmlet pet
    }

}
