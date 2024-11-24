package io.xeros.content.tradingpost;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.google.common.base.Preconditions;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.cheatprevention.CheatEngineBlock;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerAssistant;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.save.PlayerSave;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ImmutableItem;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;
import io.xeros.util.logging.player.TradingPostBuyLog;

/**
 *
 * @author Nighel
 * @credits Nicholas
 *
 */

public class Listing {

	public static final String DIRECTORY = Server.getSaveDirectory() + "/trading_post/";
	public static final String SALE_DIRECTORY = DIRECTORY + "sales/";
	public static final String PLAYERS_DIRECTORY = DIRECTORY + "players/";
	public static final String ITEMS_DIRECTORY = DIRECTORY + "items/";

	// make true to preload all sales and keep them all in the cache
	// make true to preload all sales and keep them all in the cache
	private static final boolean PRELOAD_ALL = false;

	// the next ID to be assigned to a sale, increment every time someone makes a new sale
	private static int NEXT_SALE_ID = 1;

	// Number of sales to keep in memory, irrelevant if PRELOAD_SALES is true
	private static final int CACHE_SIZE = 100;

	// recently read sales kept in memory for faster access
	private static final LinkedList<Sale> cache = new LinkedList<Sale>();

	/**
	 * Loads the total sales on load of server
	 */
	public static void init() {
		Misc.createDirectory(SALE_DIRECTORY, PLAYERS_DIRECTORY, ITEMS_DIRECTORY);
		getFile(SALE_DIRECTORY);
		//System.out.println("NEXT_SALE_ID: " + NEXT_SALE_ID);
	}

	public static void removeSoldItems(Player c) {
		List<Sale> sales = getSales(c.getLoginName());
		if (sales != null) {
			for (Sale sale : sales) {
				if (sale.getTotalSold() >= sale.getQuantity()) {
					sale.setHasSold(true);
					save(sale);
					updateHistory(c, sale.getId(), sale.getTotalSold(), sale.getPrice());
				}
			}
		}
	}

	/**
	 * Counts how much sales there are
	 * @param dirPath
	 */
	private static void getFile(String dirPath) {
		File f = new File(dirPath);
		File[] files = f.listFiles();

		if (files != null)
			for (int i = 0; i < files.length; i++) {
				NEXT_SALE_ID = files.length+1;
				File file = files[i];

				if (file.isDirectory()) {
					getFile(file.getAbsolutePath());
				}
			}
	}

	/**
	 * Loads the sales via player name
	 * @param playerName - player his username
	 * @return
	 */

	public static List<Sale> getSales(String playerName) {
		String line = "";
		LinkedList<Sale> sales = new LinkedList<Sale>();
		// read text file at /players/playerName.txt
		try {
			String loc = PLAYERS_DIRECTORY + playerName + ".txt";
			File file = new File(loc);
			if (!file.exists()) {
				Preconditions.checkState(file.createNewFile());
			}

			BufferedReader br = new BufferedReader(new FileReader(PLAYERS_DIRECTORY + playerName + ".txt"));

			while((line = br.readLine()) != null) {
				int id = Integer.parseInt(line);
				if(sales != null)
					sales.add(getSale(id));
			}

			br.close();

			return sales;
		} catch(Exception e) {
			e.printStackTrace(System.err);
			return new LinkedList<Sale>();
		}
	}

	/**
	 * Loads the sales via item id
	 * @param itemId
	 * @return
	 */

	public static List<Sale> getSales(int itemId) {
		String line = "";
		LinkedList<Sale> sales = new LinkedList<Sale>();
		// read text file at /players/playerName.txt
		try {
			BufferedReader br = new BufferedReader(new FileReader(ITEMS_DIRECTORY+itemId+".txt"));

			while((line = br.readLine()) != null) {
				int id = Integer.parseInt(line);
				if(sales != null)
					sales.add(getSale(id));
			}

			br.close();

			return sales;
		} catch(Exception e) {
			e.printStackTrace(System.err);
			return new LinkedList<Sale>();
		}
	}

	/**
	 * Gets the sale via the id
	 * @param saleId - id of the sale
	 * @return
	 */

	public static Sale getSale(int saleId) {
		String[] split;
		// Check cache for this sale
		for(Sale sale : cache)
			if(sale.getSaleId() == saleId)
				return sale;

		// read text file at /sale/saleId.txt
		try {
			BufferedReader br = new BufferedReader(new FileReader(SALE_DIRECTORY+saleId+".txt"));

			// read information
			split = br.readLine().split("\t");
			Sale sale = new Sale(saleId, split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]), Boolean.parseBoolean(split[6]));

			// If the cache is full, remove the last Sale. Add this one to the beginning either way.
			if(!PRELOAD_ALL && cache.size() == CACHE_SIZE)
				cache.removeLast();
			cache.addFirst(sale);

			br.close();

			return sale;
		} catch(Exception e) {
			e.printStackTrace(System.err);
			return null;
		}
	}

	/**
	 * Opens up the first interface for the trading post.
	 * And then loading all the data thats needed.
	 * @param c
	 */

	public static void openPost(Player c, boolean soldItem) {
		if (!c.getMode().isTradingPermitted()) {
			c.sendMessage("You are not permitted to make use of this.");
			return;
		}
		removeSoldItems(c);
		c.setInTradingPost(true);
		resetEverything(c);
		c.getPA().showInterface(48600);
		c.getPA().sendFrame106(3);
		if(soldItem) {
			String each = c.quantity > 1 ? "each" : "";
			c.sendMessage("[@red@Trading Post@bla@] You successfully list "+c.quantity+"x "+ formatItemName(c.item)+" for "+Misc.format(c.price)+" GP " + each);
			c.item = -1;
			c.uneditItem = -1;
			c.quantity = -1;
			c.price = -1;
		}
		loadPlayersListings(c);
		c.insidePost = true;
		loadHistory(c);
	}

	/**
	 * Makes all the listings show up for the player.
	 * @param c
	 */

	public static void loadPlayersListings(Player c) {
		int start = 48788, id = 0, moneyCollectable = 0;

		LinkedList<Sale> sales = (LinkedList<Sale>) getSales(c.getLoginName());

		for(Sale sale : sales) {
			c.getPA().sendTradingPost(48847, sale.getId(), id, sale.getQuantity());
			id++;
			c.getPA().sendFrame126(formatItemName(sale.getId()), start);
			start++;
			c.getPA().sendFrame126("" + Misc.formatCoins(sale.getPrice()), start);
			start++;
			c.getPA().sendFrame126(sale.getTotalSold() + " / " + sale.getQuantity() , start);
			start += 2;
			moneyCollectable += (sale.getPrice() * sale.getLastCollectedAmount());
		}
		c.getPA().sendFrame126(Misc.format(moneyCollectable) + " GP", 48610);
		for (int k = id; k < 15; k++) {
			c.getPA().sendTradingPost(48847, -1, k, -1);
		}
		for(int i = start; i < 48850; i++) {
			c.getPA().sendFrame126("", i);
		}
	}

	/**
	 * Shows the last 10 latest sales you have done.
	 * @param c
	 */

	public static void loadHistory(Player c) {
		for(int i = 0, start1 = 48636, start2 = 48637; i < c.saleItems.size(); i++) {
			if(c.saleItems.get(i).intValue() > 0 && c.saleAmount.get(i).intValue() > 0 && c.salePrice.get(i).intValue() > 0) {
				String each = c.saleAmount.get(i).intValue() > 1 ? "each" : "coins";
				c.getPA().sendFrame126(c.saleAmount.get(i).intValue() + " x " + ItemDef.forId(c.saleItems.get(i).intValue()).getName(), start1);
				c.getPA().sendFrame126("sold for "+zerosintomills(c.salePrice.get(i).intValue())+" " + each, start2);
				start1 += 2;
				start2 += 2;
			}
		}
	}

	/**
	 * Opens up the selected item using offer 1/5/10/all/x
	 * @param c
	 * @param itemId
	 * @param amount
	 * @param p
	 */

	public static void openSelectedItem(Player c, int itemId, int amount, int p) {
		if (!c.getItems().playerHasItem(itemId, amount)) {
			c.sendMessage("[@red@Trading Post@bla@] You don't have that many "+formatItemName(itemId) + (amount > 1 ? "s" : "")+".");
			return;
		}
		if (ItemDef.forId(itemId) != null) {
			if (!ItemDef.forId(itemId).isTradable()) {
				c.sendMessage("[@red@Trading Post@bla@] You can't sell that item");
				return;
			}
		}
		if (!ItemDef.forId(itemId).isTradable()) {
			c.sendMessage("[@red@Trading Post@bla@] You can't sell that item");
			return;
		}

		if (ItemDef.forId(itemId) == null) {
			c.sendMessage("Item definition is null for " + itemId + ", please report this error.");
			return;
		}

		if (itemId == Items.COINS) {
			c.sendMessage("You can't sell coins in the trading post.");
			return;
		}

		//if(c.uneditItem <= 0) - Caused a dupe if you changed items
		c.uneditItem = itemId;
		//Config.trade
		c.item = -1;

		c.inSelecting = false;
		c.isListing = true;
		//boolean noted = Item.itemIsNote[itemId];
		//boolean noted = ItemDefinition.forId(itemId).isNoteable();
		//if(noted)
		//	itemId--;
		c.item = itemId;
		c.quantity = amount;
		//c.price = p >= 1 ? p : (int) itemList.ShopValue;
		//c.getInventory().getItemshopValue(c.item);
		if (p > 0) {
			c.price = p;
		} else if (ItemDef.forId(itemId).getShopValue() < Integer.MAX_VALUE) {
			c.price = ItemDef.forId(itemId).getShopValue();
		} else {
			c.price = Integer.MAX_VALUE;
		}
		//c.price = p >= 1 ? p : (int) ItemDefinition.forId(itemId).getValue();
		c.getPA().showInterface(48598);
		c.getPA().sendTradingPost(48962, itemId, 0, amount);
		c.getPA().sendFrame126(formatItemName(itemId), 48963); //item name
		c.getPA().sendFrame126("Price (each): "+Misc.format(c.price)+"", 48964); //price each
		c.getPA().sendFrame126("Quantity: " + amount, 48965); //quantity
		//c.getPA().sendFrame(s, 48966); //guide
		//c.getPA().sendFrame(s, 48967); //listings
	}

	/**
	 * Writes every thing the the proper files.
	 * @param c
	 */

	public static void confirmListing(Player c) {
		int itemId = c.item;
		int amount = c.quantity;
		if (itemId == Items.COINS) {
			c.sendMessage("You can't sell coins in the trading post.");
			return;
		}
		if (c.quantity <= 0) {
			c.sendMessage("[@red@Trading Post@bla@] You cant have 0 quantity listed.");
			return;
		}
		if (!c.getItems().playerHasItem(itemId, amount)) {
			c.sendMessage("[@red@Trading Post@bla@] You no longer have that many"+formatItemName(itemId) + (amount > 1 ? "s" : "")+".");
			return;
		}
		if (c.uneditItem == -1) {
			if (c.debugMessage)
				c.sendMessage("Stopped");
			return;
		}

		if ((long) c.price * ((long) amount) > Integer.MAX_VALUE) {
			c.sendMessage("That offer requires too much money to buy, it's invalid.");
			return;
		}

		if (c.hasNewPlayerRestriction()) {
			c.sendMessage("You cannot use the trade post, you must play for at least "
					+ Configuration.NEW_PLAYER_RESTRICT_TIME_MIN + " minutes.");
			return;
		}

		BufferedWriter sale_id;
		BufferedWriter item_id;
		BufferedWriter name;
		try {
			sale_id = new BufferedWriter(new FileWriter(SALE_DIRECTORY+NEXT_SALE_ID+".txt", true));
			item_id = new BufferedWriter(new FileWriter(ITEMS_DIRECTORY+c.item+".txt", true));
			name = new BufferedWriter(new FileWriter(PLAYERS_DIRECTORY+ c.getLoginName() +".txt", true));

			sale_id.write(c.getLoginName() + "\t" + c.item + "\t" + c.quantity + "\t0\t" + c.price + "\t0\t" + "false");
			sale_id.newLine();

			item_id.write("" + NEXT_SALE_ID);
			item_id.newLine();

			name.write("" + NEXT_SALE_ID);
			name.newLine();

			//try {
			//CreateListing.getSingleton().createListing(NEXT_SALE_ID, c.item, c.getPA().getItemName(c.item), c.quantity, c.price, c.playerName, 0);
			//} catch (Exception e) {
			//	e.printStackTrace(System.err);
			//}
			Sale sale = new Sale(NEXT_SALE_ID, c.getLoginName(), c.item, c.quantity, 0, c.price, 0, false);

			++NEXT_SALE_ID;

			cache.addFirst(sale);

			if(!PRELOAD_ALL && cache.size() == CACHE_SIZE)
				cache.removeLast();
			sale_id.close();
			item_id.close();
			name.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
		if (c.debugMessage)
			c.sendMessage("uneditItem "+c.uneditItem+" - c.item "+c.item+" - quanity: "+c.quantity);
		c.getItems().deleteItem2(c.uneditItem, c.quantity);
		openPost(c, true);
		PlayerSave.saveGame(c);
	}

	/**
	 * Cancel a listing via its sale id
	 */
	public static void cancelListing(Player c, int id, int itemId) {
		if (id < 0 || itemId < 0)
			return;
		List<Sale> sales = getSales(c.getLoginName());
		if (sales.size() <= id) {
			c.sendMessage("There is no item at that slot.");
			return;
		}

		Sale sale = sales.get(id);
		int quantity = sale.getQuantity() - sale.getTotalSold(), saleItem = sale.getId();
		boolean stackable = ItemDef.forId(saleItem).isStackable();
		boolean isNoted = ItemDef.forId(saleItem).isNoted();
		if(!stackable && !isNoted && quantity > 1) {
			saleItem++;
		}
		if(sale.getId() == itemId) { //needed to put
			if(quantity > 0) {
				sale.setHasSold(true); //this code
				save(sale); //
				updateHistory(c, sale.getId(), sale.getTotalSold(), sale.getPrice()); //
				c.getInventory().addAnywhere(new ImmutableItem(itemId, quantity));
				loadPlayersListings(c); //
				PlayerSave.saveGame(c); //
				//into the leftOver>0 if statement
			} else {
				c.sendMessage("This item has already been sold/cancelled.");
				sale.setHasSold(true); //this code
				save(sale); //
			}
		}
	}
	/**
	 * Collecting your money via the button
	 * @param c
	 */

	public static void collectMoney(Player c) {
		LinkedList<Sale> sales = (LinkedList<Sale>) getSales(c.getLoginName());
		long moneyCollectable = 0;
		for(Sale sale : sales) {
			moneyCollectable += (sale.getPrice() * sale.getLastCollectedAmount());
			sale.setLastCollectedSold(0);
			save(sale);
		}
		if (moneyCollectable <= 0) {
			c.sendMessage("[@red@Trading Post@bla@] You collected no coins due to your coffer being empty.");
			moneyCollectable = 0;
			c.getPA().sendFrame126(Misc.format(moneyCollectable) + " GP", 48610);
			PlayerSave.saveGame(c);
			return;
		}

		c.getInventory().addAnywhere(new ImmutableItem(995, (int)moneyCollectable));
		c.sendMessage("[@red@Trading Post@bla@] You successfully collect "+Misc.format(moneyCollectable)+" coins from your coffer.");
		moneyCollectable = 0;
		c.getPA().sendFrame126(Misc.format(moneyCollectable) + " GP", 48610);
		PlayerSave.saveGame(c);
	}

	public static void save(Sale sale) {
		String line;
		String newLine = "";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(SALE_DIRECTORY+sale.getSaleId()+".txt"));
			writer.write(sale.getName() + "\t" + sale.getId() + "\t" + sale.getQuantity() + "\t" + sale.getTotalSold() + "\t" + sale.getPrice() + "\t" + sale.getLastCollectedAmount() + "\t" + sale.hasSold());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}

		if(sale.hasSold()) {
			if(sale.getLastCollectedAmount() > 0) {
				Player c = PlayerHandler.players[PlayerHandler.getPlayerID(sale.getName())];
				c.getInventory().addAnywhere(new ImmutableItem(995, sale.getLastCollectedAmount() * sale.getPrice()));
				c.sendMessage("@red@Added " + (sale.getLastCollectedAmount() * sale.getPrice()) + " gp from your sales to your inventory.");
				sale.setLastCollectedSold(0);
			}
			try {
				/*try {
					if(sale.getTotalSold() != sale.getQuantity())
						CreateListing.getSingleton().updateListing(sale.getSaleId(), sale.getQuantity(), false);
					else
						CreateListing.getSingleton().updateListing(sale.getSaleId(), sale.getQuantity(), true);
				} catch (SQLException e) {
					e.printStackTrace(System.err);
				}*/
				BufferedReader read = new BufferedReader(new FileReader(PLAYERS_DIRECTORY+sale.getName()+".txt"));
				while((line = read.readLine()) != null) {
					if(line.equals(Integer.toString(sale.getSaleId())))
						continue;
					newLine += line + System.getProperty("line.separator");
				}
				read.close();
				BufferedWriter write = new BufferedWriter(new FileWriter(PLAYERS_DIRECTORY+sale.getName()+".txt"));
				write.write(newLine);
				write.close();
				newLine = "";
				read = new BufferedReader(new FileReader(ITEMS_DIRECTORY+sale.getId()+".txt"));
				while((line = read.readLine()) != null) {
					if(line.equals(Integer.toString(sale.getSaleId())))
						continue;
					newLine += line + System.getProperty("line.separator");
				}
				read.close();
				write = new BufferedWriter(new FileWriter(ITEMS_DIRECTORY+sale.getId()+".txt"));
				write.write(newLine);
				write.close();
				newLine = "";
				write = new BufferedWriter(new FileWriter(SALE_DIRECTORY+sale.getSaleId()+".txt"));
				newLine = sale.getName() + "\t" + sale.getId() + "\t" + sale.getQuantity() + "\t" + sale.getTotalSold() + "\t" + sale.getPrice() + "\t" + sale.getLastCollectedAmount() + "\t" + sale.hasSold();
				write.write(newLine);
				write.close();
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}
	}

	/**
	 * Displays the 6 sales based on pages and item name/player name and recent
	 * @param sales
	 * @param c
	 */

	public static void displayResults(List<Sale> sales, Player c, Boolean loadRecent) {
		c.sendMessage(":resetpost:");
		List<GameItem> result = new ArrayList<GameItem>();
		List<Integer> idk = new ArrayList<Integer>();
		int total = 0, skipped = 0, start = 26023;
		/** Sort highest price **/
		if (!loadRecent) {
			sales.sort(Comparator.comparingInt(Sale::getPrice));
		}
		for(Sale sale : sales) {
			if(sale.hasSold() || sale.getTotalSold() == sale.getQuantity())
				continue;
			result.add(new GameItem(sale.getId(), sale.getQuantity() - sale.getTotalSold()));
			idk.add(sale.getSaleId());
			c.getPA().sendFrame126(formatItemName(sale.getId()), start);
			start++;
			String each = sale.getQuantity() - sale.getTotalSold() > 1 ? " each" : "";
			c.getPA().sendFrame126(Misc.format(sale.getPrice()) + each, start);
			start++;
			c.getPA().sendFrame126(sale.getName(), start);
			start++;
			c.getPA().sendFrame126(Integer.toString(sale.getTotalSold()), start);
			start++;
			total++;
			if(total == 250) {
				//System.out.println("Reached 50 recent sales");
				break;
			}
		}
		PlayerAssistant.sendItems(c, 26022, result, 250);
		for(int i = start; i < 27023; i++) {
			c.getPA().sendFrame126("", i);
		}
		c.saleResults = idk;
	}

	/**
	 * Loads the recent sales
	 * @param c
	 */

	public static void loadRecent(Player c) {
		if (System.currentTimeMillis() - c.lastTradingPostView <= 5000) {
			c.sendMessage("You must wait before doing this again.");
			return;
		}
		c.lastTradingPostView = System.currentTimeMillis();
		c.pageId = 0;
		c.searchId = 3;
		c.getPA().sendFrame126("Trading Post - Recent listings", 48019);
		c.getPA().showInterface(48000);
		List<Sale> sales = new LinkedList<Sale>();
		int total = 0;

		for(int i = NEXT_SALE_ID - 1; i > 0; i--) {
			Sale sale = getSale(i);
			if(sale.hasSold())
				continue;
			total++;
			sales.add(sale);
			if(total == 250)
				break;
		}
		displayResults(sales, c, true);
	}

	public static void buyListing(Player c, int slot, int amount) {
		if (!c.inTradingPost && System.currentTimeMillis() - c.clickDelay <= 2200) {
			return;
		}

		if (!c.inTradingPost) {
			CheatEngineBlock.tradingPostAlert(c);
			return;
		}

		if (!c.getMode().isTradingPermitted()) {
			c.sendMessage("You are not permitted to make use of this.");
			return;
		}

		Sale sales = getSale(c.saleResults.get(slot));

		int totalQuantity = sales.getQuantity();
		int alreadySold = sales.getTotalSold();
		int totalStock = (totalQuantity) - alreadySold;

		if (ItemDef.forId(sales.getId()) != null) {
			if (!ItemDef.forId(sales.getId()).isTradable()) {
				c.sendMessage("[@red@Trading Post@bla@] That item isn't tradeable.");
				return;
			}
		}

		if (sales.hasSold()) {
			c.sendMessage("This item has already been sold/cancelled.");
			return;
		}
		if(sales.getQuantity() == sales.getTotalSold()) {
			return;
		}
		if(totalStock < amount) {
			amount = totalStock;
		}
		if (totalStock < 1) {
			c.sendMessage("Seems to be no more stock, try again.");
			c.sendMessage("Total Stock = "+totalStock+"");
			c.sendMessage("Purchase Amount = "+amount+"");
			return;
		}

		if(sales.getQuantity() == sales.getTotalSold()) {
			return;
		}

		if(sales.getName().equalsIgnoreCase(c.getLoginName())) {
			c.sendMessage("[@red@Trading Post@bla@] You cannot buy your own listings.");
			return;
		}

		if(amount > sales.getQuantity()) {
			amount = sales.getQuantity();
		}

		if (((long) sales.getPrice()) * ((long) amount) > Integer.MAX_VALUE) {
			c.sendMessage("That item requires too many coins to buy, try buying a smaller amount.");
			return;
		}

		int fullPrice = sales.getPrice() * amount;

		if(!c.getItems().playerHasItem(995, sales.getPrice() * amount)) {
			c.sendMessage("[@red@Trading Post@bla@] You need at least "+Misc.getPriceFormat(fullPrice)+" Coins to buy the "+amount+"x "+formatItemName(sales.getId())+".");
			return;
		}

		int slotsNeeded = amount;
		int saleItem = sales.getId();

		ItemDef def = ItemDef.forId(saleItem);

		if (amount > 1 && !def.isNoted() && def.getNoteId() > 0) {
			saleItem = def.getNoteId();
		}

		if(c.getItems().freeSlots() < slotsNeeded && (!ItemDef.forId(sales.getId()+1).isNoted() && !ItemDef.forId(sales.getId()).isStackable())) {// TODO sort this, why is there a +1
			c.sendMessage("[@red@Trading Post@bla@] You need at least "+ slotsNeeded +" free slots to buy this.");
			return;
		}

		Server.getLogging().write(new TradingPostBuyLog(c, new GameItem(saleItem, amount), fullPrice, sales.getName()));
		c.getInventory().addAnywhere(new ImmutableItem(saleItem, amount));
		c.getItems().deleteItem(995, fullPrice);
		c.sendMessage("[@red@Trading Post@bla@] You purchase "+ amount +"x "+formatItemName(sales.getId())+".");
		c.getItems().sendInventoryInterface(3214);
		PlayerSave.saveGame(c);
		sales.setLastCollectedSold(sales.getLastCollectedAmount() + amount);
		sales.setTotalSold(sales.getTotalSold() + amount);
		save(sales);
		if(PlayerHandler.getPlayerID(sales.getName()) != -1) {
			Player seller = PlayerHandler.players[PlayerHandler.getPlayerID(sales.getName())];
			Discord.writeServerSyncMessage("[TRADING POST] " + c.getDisplayName() + " bought " + formatItemName(sales.getId()) + " x" + amount + " from " + seller.getDisplayName());
			if(seller != null) {
				if(seller.getLoginName().equalsIgnoreCase(sales.getName())) {
					if(sales.getTotalSold() < sales.getQuantity()) {
						seller.sendMessage("[@red@Trading Post@bla@] You sold "+ amount +"x of your "+formatItemName(sales.getId())+".");
					} else {
						seller.sendMessage("[@red@Trading Post@bla@] Finished selling your "+formatItemName(sales.getId())+".");
						emptyInterface(seller, false);
					}
					PlayerSave.saveGame(seller);
					if(seller.isListing) {
						loadPlayersListings(seller);
					}
				}
			}
		}
	}
	/**
	 * Loads the sales via playerName
	 * @param c
	 * @param playerName
	 */

	public static void loadPlayerName(Player c, String playerName) {
		if (System.currentTimeMillis() - c.lastTradingPostView <= 5000) {
			c.sendMessage("You must wait before doing this again.");
			return;
		}
		c.lastTradingPostView = System.currentTimeMillis();
		c.lookup = playerName;
		playerName = playerName.replace("_"," ");
		c.searchId = 2;
		c.getPA().showInterface(48000);
		c.getPA().sendFrame126("Trading Post - Searching for player: " + playerName, 48019);

		List<Sale> sales = new LinkedList<Sale>();

		for(String s : new File(PLAYERS_DIRECTORY).list()) {
			s = s.substring(0, s.indexOf(".")).toLowerCase();
			if(s.contains(playerName.toLowerCase()))
				sales.addAll(getSales(s));
		}
		displayResults(sales, c, false);
	}

	/**
	 * Loads the sales via itemName
	 * @param c
	 * @param itemName
	 */

	public static void loadItemName(Player c, String itemName) {
		if (System.currentTimeMillis() - c.lastTradingPostView <= 5000) {
			c.sendMessage("You must wait before doing this again.");
			return;
		}
		c.lastTradingPostView = System.currentTimeMillis();
		c.lookup = itemName;
		itemName = itemName.replace("_"," ");
		c.searchId = 1;
		c.getPA().showInterface(48000);
		c.getPA().sendFrame126("Trading Post - Searching for item: " + itemName, 48019);

		List<Integer> items = new LinkedList<Integer>();
		List<Sale> sales = new LinkedList<Sale>();

		for(String s : new File(ITEMS_DIRECTORY).list())
			items.add(Integer.parseInt(s.substring(0, s.indexOf("."))));

		for(int item : items) {
			//System.out.println("item: "+ItemAssistant.getItemName(item)+", itemName: " + itemName);
			if(formatItemName(item).toLowerCase().contains(itemName.toLowerCase())) {
				sales.addAll(getSales(item));
			}
		}
		displayResults(sales, c,false);
	}

	/**
	 * Resets all the necessary stuff;
	 * @param c
	 */

	public static void resetEverything(Player c) {
		c.inSelecting = false;
		c.isListing = true;
		c.insidePost = false;
		c.setSidebarInterface(3, 3213);
	}

	/**
	 * Handles the opening of the interface for offering an item
	 * @param c
	 */

	public static void openNewListing(Player c) {
		c.getPA().showInterface(48599);
		c.setSidebarInterface(3, 48500); // ++ tab
		for (int k = 0; k < 28; k++) {
			c.getPA().sendTradingPost(48501, c.playerItems[k]-1, k, c.playerItemsN[k]);
		}
	}

	/*
	 *
	 * Handles the buttons of the interfaces
	 *
	 */

	public static void postButtons(Player c, int button) {
		switch(button) {
			case 189237:
				if (!c.inTradingPost &&System.currentTimeMillis() - c.clickDelay <= 2200) {
					return;
				}
				if (!c.inTradingPost) {
					CheatEngineBlock.tradingPostAlert(c);
					return;
				}
				if (!c.getMode().isTradingPermitted()) {
					c.sendMessage("You are not permitted to make use of this.");
					return;
				}
				int total = 0;
				LinkedList<Sale> sales = (LinkedList<Sale>) getSales(c.getLoginName());

				for(Sale sale : sales)
					total++;
				if(c.amDonated <= 49 && total >= 6) {
					c.sendMessage("[@red@Trading Post@bla@] You cannot have more then 6 listings as a regular player.");
					return;
				} else if(c.amDonated >= 50 && c.amDonated <= 99 && total >= 7) { //regular donator
					c.sendMessage("[@red@Trading Post@bla@] You cannot have more then 7 listings as a regular donator rank.");
					return;
				} else if(c.amDonated >= 100 && c.amDonated <= 249 && total >= 8) { //extreme donator
					c.sendMessage("[@red@Trading Post@bla@] You cannot have more then 8 listings as a extreme donator rank.");
					return;
				} else if(c.amDonated >= 250 && c.amDonated <= 499 && total >= 9) { //legendary donator
					c.sendMessage("[@red@Trading Post@bla@] You cannot have more then 9 listings as a legendary donator rank.");
					return;
				} else if(c.amDonated >= 500 && c.amDonated <= 999 && total >= 10) { //diamond club
					c.sendMessage("[@red@Trading Post@bla@] You cannot have more then 10 listings as a diamond club rank.");
					return;
				} else if(c.amDonated >= 1000 && total >= 11) { //onyx club
					c.sendMessage("[@red@Trading Post@bla@] You cannot have more then 11 listings as a onyx club rank.");
					return;
				}
				if(!c.inSelecting) {
					openNewListing(c);
					c.inSelecting = true;
					c.getPA().sendFrame106(3);
				} else {
					resetEverything(c);
					c.getPA().showInterface(48600);
					c.getPA().sendFrame106(3);
				}
				break;

			case 59229: //Close select item
				if (!c.inTradingPost &&System.currentTimeMillis() - c.clickDelay <= 2200) {
					return;
				}
				c.inTradingPost = false;
				c.getPA().closeAllWindows();
				resetEverything(c);
				break;

			case 191072:
				if (!c.inTradingPost &&System.currentTimeMillis() - c.clickDelay <= 2200) {
					return;
				}
				if (!c.inTradingPost) {
					CheatEngineBlock.tradingPostAlert(c);
					return;
				}
				if (!c.getMode().isTradingPermitted()) {
					c.sendMessage("You are not permitted to make use of this.");
					return;
				}
				synchronized (c) {
					c.outStream.createFrame(191);
				}
				c.xInterfaceId = 191072;
				break;

			case 191075: // Removed quantity button
				if (!c.inTradingPost &&System.currentTimeMillis() - c.clickDelay <= 2200) {
					return;
				}
				if (!c.inTradingPost) {
					CheatEngineBlock.tradingPostAlert(c);
					return;
				}
				if (!c.getMode().isTradingPermitted()) {
					c.sendMessage("You are not permitted to make use of this.");
					return;
				}
				synchronized (c) {
					c.outStream.createFrame(192);
				}
				c.xInterfaceId = 191075;
				break;

			case 191078:
				if (!c.inTradingPost && System.currentTimeMillis() - c.clickDelay <= 2200) {
					return;
				}
				if (!c.inTradingPost) {
					CheatEngineBlock.tradingPostAlert(c);
					return;
				}
				if (!c.getMode().isTradingPermitted()) {
					c.sendMessage("You are not permitted to make use of this.");
					return;
				}
				confirmListing(c);
				break;

			case 189223:
				if (!c.inTradingPost && System.currentTimeMillis() - c.clickDelay <= 2200) {
					return;
				}
				if (!c.inTradingPost) {
					CheatEngineBlock.tradingPostAlert(c);
					return;
				}
				if (!c.getMode().isTradingPermitted()) {
					c.sendMessage("You are not permitted to make use of this.");
					return;
				}
				if (c.getItems().freeSlots() < 2) {
					c.sendMessage("Please clear up your inventory before collecting.");
					return;
				}
				collectMoney(c);
				break;

			case 189234:
				if (!c.inTradingPost && System.currentTimeMillis() - c.clickDelay <= 2200) {
					return;
				}
				if (!c.inTradingPost) {
					CheatEngineBlock.tradingPostAlert(c);
					return;
				}
				if (!c.getMode().isTradingPermitted()) {
					c.sendMessage("You are not permitted to make use of this.");
					return;
				}
				loadRecent(c);
				break;

			case 187133:
				if (!c.inTradingPost &&System.currentTimeMillis() - c.clickDelay <= 2200) {
					return;
				}
				if (!c.inTradingPost) {
					//CheatEngineBlock.tradingPostAlert(c);
					return;
				}
				if (!c.getMode().isTradingPermitted()) {
					c.sendMessage("You are not permitted to make use of this.");
					return;
				}
				openPost(c, false);
				break;

			case 187136:
				if (!c.inTradingPost &&System.currentTimeMillis() - c.clickDelay <= 2200) {
					return;
				}
				if (!c.inTradingPost) {
					CheatEngineBlock.tradingPostAlert(c);
					return;
				}
				if (!c.getMode().isTradingPermitted()) {
					c.sendMessage("You are not permitted to make use of this.");
					return;
				}
				if(c.pageId > 1)
					c.pageId--;
				c.sendMessage("You are already on the previous page.");
				switch(c.searchId) {
					case 1:
						loadItemName(c, c.lookup);
						break;
					case 2:
						loadPlayerName(c, c.lookup);
						break;
					case 3:
						loadRecent(c);
						break;
				}
				break;

			case 187139:
				if (!c.inTradingPost && System.currentTimeMillis() - c.clickDelay <= 2200) {
					return;
				}
				if (!c.inTradingPost) {
					CheatEngineBlock.tradingPostAlert(c);
					return;
				}
				if (!c.getMode().isTradingPermitted()) {
					c.sendMessage("You are not permitted to make use of this.");
					return;
				}
				c.pageId+=1;
				c.sendMessage("You can only view recents on this page for now.");
				switch(c.searchId) {
					case 1:
						loadItemName(c, c.lookup);
						break;
					case 2:
						loadPlayerName(c, c.lookup);
						break;
					case 3:
						loadRecent(c);
						break;
				}
				break;
		}
	}


	public static void emptyInterface(Player c, boolean b) {
		for(int i = 0; i < 15; i++) {
			c.getPA().sendTradingPost(48847, -1, i, -1);
		}
		if(b) {
			for(int i = 48636; i < 48656; i++) {
				c.getPA().sendFrame126("", i);
			}
		}
		for(int i = 48787; i < 48847; i++) {
			c.getPA().sendFrame126("", i);
		}
	}

	private static String zerosintomills(int j) {
		if(j >= 0 && j < 1000)
			return String.valueOf(j);
		if(j >= 1000 && j < 10000000)
			return j / 1000 + "K";
		if(j >= 10000000 && j  < 2147483647)
			return j / 1000000 + "M";
		return String.valueOf(j);
	}

	private static void updateHistory(Player c, int itemId, int amount, int price) {
		c.saleItems.add(0, itemId);
		c.saleItems.remove(c.saleItems.size()-1);
		c.saleAmount.add(0, amount);
		c.saleAmount.remove(c.saleAmount.size()-1);
		c.salePrice.add(0, price);
		c.salePrice.remove(c.salePrice.size()-1);
		loadHistory(c);
	}

	public static String formatItemName(int id) {
		String name = ItemDef.forId(id).getName();
		if (name.length() < 21)
			return name;
		return name.substring(0, 20) + ".";
	}
}