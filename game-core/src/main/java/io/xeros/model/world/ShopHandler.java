package io.xeros.model.world;

import com.google.common.base.Preconditions;
import io.xeros.Configuration;
import io.xeros.model.definitions.ShopDef;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.model.shops.ShopItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Shops
 **/

public class ShopHandler {

	public static int MaxShops = 200;
	public static int MaxShopItems = 400;
	public static int MaxShowDelay = 10;
	public static int MaxSpecShowDelay = 60;
	public static int[][] ShopItems = new int[MaxShops][MaxShopItems];
	public static int[][] ShopItemsN = new int[MaxShops][MaxShopItems];
	public static int[][] ShopItemsDelay = new int[MaxShops][MaxShopItems];
	public static int[][] ShopItemsSN = new int[MaxShops][MaxShopItems];
	public static int[] ShopItemsStandard = new int[MaxShops];
	public static String[] ShopName = new String[MaxShops];
	public static int[] ShopSModifier = new int[MaxShops];

	public ShopHandler() {
		for (int i = 0; i < MaxShops; i++) {
			for (int j = 0; j < MaxShopItems; j++) {
				ResetItem(i, j);
				ShopItemsSN[i][j] = 0;
			}
			ShopItemsStandard[i] = 0;
			ShopSModifier[i] = 0;
			ShopName[i] = "";
		}
	}

	public static void load() {
		ShopDef.getDefinitions().values().forEach(shop -> {
			addShop(shop.getId(), shop.getName(), shop.getItems());
		});
	}

	public static int addShopAnywhere(String name, List<ShopItem> items) {
		for (int i = 1; i < ShopName.length; i++) {
			if (ShopName[i].isEmpty() && ShopItems[i][0] == 0) {
				addShop(i, name, items);
				return i;
			}
		}

		throw new IllegalStateException("No open shop slot.");
	}
	
	public static void addShop(int id, String name, List<ShopItem> items) {
		Preconditions.checkState(id > 0, "Shop id must be more than zero.");
		ShopName[id] = name;
		int itemIndex = 0;
		for (GameItem item : items) {
			ShopItems[id][itemIndex] = item.id();
			ShopItemsN[id][itemIndex] = item.amount();
			ShopItemsSN[id][itemIndex] = item.amount();
			ShopItemsStandard[id]++;
			itemIndex++;
		}
	}

	public static List<GameItem> getShopItems(int shopId) {
		ArrayList<GameItem> list = new ArrayList<>();
		for (int i = 0; i < ShopItems[shopId].length; i++) {
			int id = ShopItems[shopId][i];
			int amount = ShopItems[shopId][i];
			if (id > 0) {
				list.add(new GameItem(id, amount));
			}
		}
		return list;
	}

	public void process() {
		boolean DidUpdate = false;
		for (int i = 1; i <= MaxShops - 1; i++) {
			for (int j = 0; j < MaxShopItems; j++) {
				if (ShopItems[i][j] > 0) {
					if (ShopItemsDelay[i][j] >= MaxShowDelay) {
						if (j <= ShopItemsStandard[i] && ShopItemsN[i][j] <= ShopItemsSN[i][j]) {
							if (ShopItemsN[i][j] < ShopItemsSN[i][j]) {
								ShopItemsN[i][j] += 1;
								DidUpdate = true;
								ShopItemsDelay[i][j] = 1;
								ShopItemsDelay[i][j] = 0;
                            }
						} else if (ShopItemsDelay[i][j] >= MaxSpecShowDelay) {
							//DiscountItem(i, j);
							ShopItemsDelay[i][j] = 0;
							DidUpdate = true;
						}
					}
					ShopItemsDelay[i][j]++;
				}
			}
			if (DidUpdate) {
				for (int k = 1; k < Configuration.MAX_PLAYERS; k++) {
					if (PlayerHandler.players[k] != null) {
						if (PlayerHandler.players[k].isShopping && PlayerHandler.players[k].myShopId == i) {
							PlayerHandler.players[k].updateShop = true;
                            PlayerHandler.players[k].updateshop(i);
						}
					}
				}
				DidUpdate = false;
			}
		}
	}
	public void ResetItem(int ShopID, int ArrayID) {
		ShopItems[ShopID][ArrayID] = 0;
		ShopItemsN[ShopID][ArrayID] = 0;
		ShopItemsDelay[ShopID][ArrayID] = 0;
	}
}
