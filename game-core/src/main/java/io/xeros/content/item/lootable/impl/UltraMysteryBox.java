package io.xeros.content.item.lootable.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.MysteryBoxLootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

//import QuickUltra.Rarity;

/**
 * Revamped a simple means of receiving a random item based on chance.
 * 
 * @author Jason MacKeigan
 * @date Oct 29, 2014, 1:43:44 PM
 */
public class UltraMysteryBox extends MysteryBoxLootable {

	/**
	 * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
	 */
	private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

	/**
	 * Stores an array of items into each map with the corresponding rarity to the list
	 */
	static {
		items.put(LootRarity.COMMON, //50% chance
				Arrays.asList(
					new GameItem(21012),//Dragon Crossbow

					new GameItem(13271),//Abby Dagger (p++)
					new GameItem(12929),//Serp Helm
					new GameItem(19550),//ring of sufferin
					new GameItem(12924),//blowpipe
					new GameItem(19481),//heavy ballista
					new GameItem(13198),//magma helm
					new GameItem(13196),//tangz helm
					new GameItem(19547),//anguish
					new GameItem(19544),//tormented

					new GameItem(11785),//ACB
					new GameItem(21633),//ancient wyvern shield
					new GameItem(11832),//BCP
					new GameItem(11834),//TASSETS
					new GameItem(11828),//Arma Chest
					new GameItem(11830),//Arma Legs
					new GameItem(11802),//AGS
					new GameItem(21003),//Elder Maul
					new GameItem(19553),//Amulet of Torture
					new GameItem(19544),//tormented bracelet
					new GameItem(21902),//dragon crossbow
					new GameItem(21000),//twisted buckler
					new GameItem(21034),//dex prayer scroll
					new GameItem(13576),//dragon warhammer
					new GameItem(21079),//arcane prayer scroll
					new GameItem(12809),//saradomin blessed sword
					new GameItem(12902),//toxic staff of the dead

		            new GameItem(13239),//primordial boots
		            new GameItem(13235),//eternal boots
		            new GameItem(13237),//pegasian boots)

				new GameItem(21012),//Dragon Crossbow

				new GameItem(13271),//Abby Dagger (p++)
				new GameItem(12929),//Serp Helm
				new GameItem(12006),//abby tent whip
				new GameItem(19550),//ring of sufferin
				new GameItem(12924),//blowpipe
				new GameItem(19481),//heavy ballista
				new GameItem(13198),//magma helm
				new GameItem(13196),//tangz helm
				new GameItem(19547),//anguish
				new GameItem(19544),//tormented

				new GameItem(11785),//ACB
				new GameItem(21633),//ancient wyvern shield
				new GameItem(11832),//BCP
				new GameItem(11834),//TASSETS
				new GameItem(11828),//Arma Chest
				new GameItem(11830),//Arma Legs
				new GameItem(11802),//AGS
				new GameItem(21003),//Elder Maul
				new GameItem(19553),//Amulet of Torture
				new GameItem(19544),//tormented bracelet
				new GameItem(21902),//dragon crossbow
				new GameItem(21000),//twisted buckler
				new GameItem(21034),//dex prayer scroll
				new GameItem(13576),//dragon warhammer
				new GameItem(21079),//arcane prayer scroll
				new GameItem(12809),//saradomin blessed sword
				new GameItem(12902),//toxic staff of the dead
				new GameItem(12002),//occult necklace
	            new GameItem(13239),//primordial boots
	            new GameItem(13235),//eternal boots
	            new GameItem(13237))//pegasian boots)
				
				);
			items.put(LootRarity.UNCOMMON, //50% chance
					Arrays.asList(
							new GameItem(19550),//ring of sufferin
							new GameItem(12924),//blowpipe
							new GameItem(19481),//heavy ballista
							new GameItem(13198),//magma helm
							new GameItem(13196),//tangz helm
							new GameItem(19547),//anguish
							new GameItem(19544),//tormented
							new GameItem(12899),//toxic trident
							new GameItem(11785),//ACB
							new GameItem(21633),//ancient wyvern shield
							new GameItem(11832),//BCP
							new GameItem(11834),//TASSETS
							new GameItem(11828),//Arma Chest
							new GameItem(11830),//Arma Legs
							new GameItem(11802),//AGS
							new GameItem(21003),//Elder Maul
							new GameItem(19553),//Amulet of Torture
							new GameItem(19544),//tormented bracelet
							new GameItem(21000),//twisted buckler
							new GameItem(21034),//dex prayer scroll
							new GameItem(13576),//dragon warhammer
							new GameItem(21079),//arcane prayer scroll
							new GameItem(12809),//saradomin blessed sword
							new GameItem(12902),//toxic staff of the dead
							new GameItem(12002),//occult necklace
				            new GameItem(13239),//primordial boots
				            new GameItem(13235),//eternal boots
							new GameItem(19550),//ring of sufferin
							new GameItem(12924),//blowpipe
							new GameItem(19481),//heavy ballista
							new GameItem(13198),//magma helm
							new GameItem(13196),//tangz helm
							new GameItem(19547),//anguish
							new GameItem(19544),//tormented

							new GameItem(11785),//ACB
							new GameItem(11832),//BCP
							new GameItem(11834),//TASSETS
							new GameItem(11828),//Arma Chest
							new GameItem(11830),//Arma Legs
							new GameItem(11802),//AGS
							new GameItem(21003),//Elder Maul
							new GameItem(19553),//Amulet of Torture
							new GameItem(19544),//tormented bracelet
							new GameItem(21034),//dex prayer scroll
							new GameItem(13576),//dragon warhammer
							new GameItem(21079),//arcane prayer scroll
							new GameItem(12902),//toxic staff of the dead
				            new GameItem(13239),//primordial boots
				            new GameItem(13235),//eternal boots
							new GameItem(21015),//dihn bulwark
							new GameItem(21006),//kodai wand						
							new GameItem(21015),//dihn bulwark
							new GameItem(21006),//kodai wand
			            new GameItem(20095),//ankou mask
						new GameItem(20104),//ankou leggings
						new GameItem(20098),//ankou top
						new GameItem(20107),//ankou socks
						new GameItem(20101),//ankou gloves
						new GameItem(20083),//mummy top
						new GameItem(20089),//mummy legs
						new GameItem(20080),//mummy mask
						new GameItem(20086),//mummy hands
						new GameItem(20092))//mummy feet		
					);
			
			items.put(LootRarity.RARE,//8% chance
					Arrays.asList(
							new GameItem(22325),//scythe
							new GameItem(21295),//infernal cape
							new GameItem(20997),//twisted bow
							new GameItem(1038),//red phat
							new GameItem(1040),//yellow phat
							new GameItem(1042),//blue phat
							new GameItem(1044),//green phat
							new GameItem(1046),//purp phat
							new GameItem(1048),//white phat
							new GameItem(11862),//black phat
							new GameItem(20784),//dragon claws
							new GameItem(12817),//elysian
							new GameItem(13343),//black santa hat
							new GameItem(13344),//inverted santa hat
							new GameItem(11847),//black hween mask
							new GameItem(11863),//rainbow phat
							new GameItem(1053),//green hween
							new GameItem(1055),//blue hween
							new GameItem(1057),//red h ween
							new GameItem(1050),//santa hat
							new GameItem(11863),//rainbow phat
							new GameItem(1053),//green hween
							new GameItem(1055),//blue hween
							new GameItem(1057),//red h ween
							new GameItem(1050),//santa hat
							new GameItem(1038),//red phat
							new GameItem(1040),//yellow phat
							new GameItem(1042),//blue phat
							new GameItem(1044),//green phat
							new GameItem(1046),//purp phat
							new GameItem(1048),//white phat
							new GameItem(11862),//black phat
							new GameItem(20784),//dragon claws
							new GameItem(12817),//elysian
							new GameItem(13343),//black santa hat
							new GameItem(13344),//inverted santa hat
							new GameItem(11847),//black hween mask
							new GameItem(11863),//rainbow phat
							new GameItem(1053),//green hween
							new GameItem(1055),//blue hween
							new GameItem(1057),//red h ween
							new GameItem(1050),//santa hat
							new GameItem(11863),//rainbow phat
							new GameItem(1053),//green hween
							new GameItem(1055),//blue hween
							new GameItem(1057),//red h ween
							new GameItem(1050),//santa hat
							new GameItem(12825),//arcane spirit shield
							new GameItem(12821),//spectral spirit shield
							new GameItem(22326),//justiciar helm
							new GameItem(22327),//justiciar body
							new GameItem(22328),// justiciar legs
							new GameItem(21021),//ancestral body
							new GameItem(21024),//ancesetral legs
							new GameItem(21018),//ancestral hat
							new GameItem(22324),//ghrazi rapier
							new GameItem(10556),//attacker icon
							new GameItem(10557),//collector icon
							new GameItem(10558),//defender icon
							new GameItem(10559),//Healer icon
							new GameItem(12825),//arcane spirit shield
							new GameItem(12821),//spectral spirit shield
							new GameItem(22326),//justiciar helm
							new GameItem(22327),//justiciar body
							new GameItem(22328),// justiciar legs
							new GameItem(21021),//ancestral body
							new GameItem(21024),//ancesetral legs
							new GameItem(21018),//ancestral hat
							new GameItem(22324),//ghrazi rapier
							new GameItem(10556),//attacker icon
							new GameItem(10557),//collector icon
							new GameItem(10558),//defender icon
							new GameItem(10559),//Healer icon
							new GameItem(20784),//dragon claws,
							new GameItem(12825),//arcane spirit shield
							new GameItem(12821),//spectral spirit shield
							new GameItem(22326),//justiciar helm
							new GameItem(22327),//justiciar body
							new GameItem(22328),// justiciar legs
							new GameItem(21021),//ancestral body
							new GameItem(21024),//ancesetral legs
							new GameItem(21018),//ancestral hat
							new GameItem(22324),//ghrazi rapier
							new GameItem(10556),//attacker icon
							new GameItem(10557),//collector icon
							new GameItem(10558),//defender icon
							new GameItem(10559)//Healer icon
							));
		}

    /**
	 * Constructs a new myster box to handle item receiving for this player and this player alone
	 *
	 * @param player the player
	 */
	public UltraMysteryBox(Player player) {
		super(player);
	}

	@Override
	public int getItemId() {
		return 13346;
	}

	@Override
	public Map<LootRarity, List<GameItem>> getLoot() {
		return items;
	}
}