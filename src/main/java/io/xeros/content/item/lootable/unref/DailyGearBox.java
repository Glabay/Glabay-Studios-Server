package io.xeros.content.item.lootable.unref;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

/**
 * Revamped a simple means of receiving a random item based on chance.
 * 
 * @author Jason MacKeigan
 * @date Oct 29, 2014, 1:43:44 PM
 */
public class DailyGearBox extends CycleEvent {

	/**
	 * The item id of the mystery box required to trigger the event
	 */
	public static final int MYSTERY_BOX = 20703;

	/**
	 * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
	 */
	private static final Map<Rarity, List<GameItem>> items = new HashMap<>();

	/**
	 * Stores an array of items into each map with the corresponding rarity to the list
	 */
	static {
		items.put(Rarity.COMMON, //
			Arrays.asList(
				new GameItem(1163),
				new GameItem(1080),  
				new GameItem(1127),  
				new GameItem(2491),  
				new GameItem(2493),  
				new GameItem(2503),  
				new GameItem(4089),  
				new GameItem(4093),  
				new GameItem(4091),  
				new GameItem(4109),  
				new GameItem(4113),  
				new GameItem(4111),  
				new GameItem(6328),  
				new GameItem(7460),  
				new GameItem(4131),  
				new GameItem(995, 150000),   
				new GameItem(2677))
		);
		
	items.put(Rarity.UNCOMMON,
			Arrays.asList(
					new GameItem(6920),
					new GameItem(7461),   
					new GameItem(10828),  
					new GameItem(20050),  
					new GameItem(4224),  
					new GameItem(6524),  
					new GameItem(6528),  
					new GameItem(6522, 200),  
					new GameItem(3204),  
					new GameItem(4587),  
					new GameItem(9185),  
					new GameItem(9144, 250),  
					new GameItem(892, 250),  
					new GameItem(5698),  
					new GameItem(4153),  
					new GameItem(995, 300000),    
					new GameItem(2801))
	);
		
		items.put(Rarity.RARE,
				Arrays.asList(
						new GameItem(11840),
						new GameItem(1409),
						new GameItem(2572),
						new GameItem(4675),
						new GameItem(11230, 15),
						new GameItem(4212),
						new GameItem(4151),
						new GameItem(4716),
						new GameItem(4720),
						new GameItem(4723),
						new GameItem(4718),
						new GameItem(4708),
						new GameItem(4710),
						new GameItem(4712),
						new GameItem(4714),
						new GameItem(4753),
						new GameItem(4756),
						new GameItem(4759),
						new GameItem(4757),
						new GameItem(4745),
						new GameItem(4749),
						new GameItem(4751),
						new GameItem(4747),
						new GameItem(4724),
						new GameItem(4726),
						new GameItem(4728),
						new GameItem(4730),
						new GameItem(4732),
						new GameItem(4734),
						new GameItem(4736),
						new GameItem(4738),
						new GameItem(995, 500000),
						new GameItem(2722)));
	}

	/**
	 * The player object that will be triggering this event
	 */
	private final Player player;

	/**
	 * Constructs a new myster box to handle item receiving for this player and this player alone
	 * 
	 * @param player the player
	 */
	public DailyGearBox(Player player) {
		this.player = player;
	}

	/**
	 * Opens a mystery box if possible, and ultimately triggers and event, if possible.
	 * 
	 * @param player the player triggering the evnet
	 */
	public void open() {
		if (System.currentTimeMillis() - player.lastMysteryBox < 150 * 4) {
			return;
		}
		if (player.getItems().freeSlots() < 2) {
			player.sendMessage("You need at least two free slots to open a mystery box.");
			return;
		}
		if (!player.getItems().playerHasItem(MYSTERY_BOX)) {
			player.sendMessage("You need a daily gear box to do this.");
			return;
		}
		player.getItems().deleteItem(MYSTERY_BOX, 1);
		player.lastMysteryBox = System.currentTimeMillis();
		CycleEventHandler.getSingleton().stopEvents(this);
		CycleEventHandler.getSingleton().addEvent(this, this, 2);
	}

	/**
	 * Executes the event for receiving the mystery box
	 */
	@Override
	public void execute(CycleEventContainer container) {
		if (player.isDisconnected() || Objects.isNull(player)) {
			container.stop();
			return;
		}
		int random = Misc.random(10);
		List<GameItem> itemList = random < 5 ? items.get(Rarity.COMMON) : random >= 5 && random <= 8 ? items.get(Rarity.UNCOMMON) : items.get(Rarity.RARE);
		GameItem item = Misc.getRandomItem(itemList);
		GameItem itemDouble = Misc.getRandomItem(itemList);
		
		if (Misc.random(200) == 1) {
			PlayerHandler.executeGlobalMessage("[<col=CC0000>Daily Box</col>] @cr20@ <col=255>" + player.getDisplayName()
					+ "</col> hit the jackpot on a Daily Gear Box!");
			switch(Misc.random(2)) {
			case 0:
				player.getItems().addItemUnderAnyCircumstance(12004, 1);
				break;
			case 1:
				player.getItems().addItemUnderAnyCircumstance(11286, 1);
				break;
			case 2:
				player.getItems().addItemUnderAnyCircumstance(11907, 1);
				break;
			}
		}

		if (Misc.random(25) == 0) {
			player.getItems().addItem(item.id(), item.amount());
			player.getItems().addItem(itemDouble.id(), itemDouble.amount());
			player.sendMessage("You receive <col=255>" + item.amount() + " x " + ItemAssistant.getItemName(item.id()) + "</col>.");
			player.sendMessage("You receive <col=255>" + itemDouble.amount() + " x " + ItemAssistant.getItemName(itemDouble.id()) + "</col>.");
		} else {
			//player.getItems().addItem(995, coins);
			player.getItems().addItem(item.id(), item.amount());
			player.sendMessage("You receive <col=255>" + item.amount() + " x " + ItemAssistant.getItemName(item.id()) + "</col>.");
		}
		container.stop();
	}

	/**
	 * Represents the rarity of a certain list of items
	 */
	enum Rarity {
		UNCOMMON, COMMON, RARE
	}

}