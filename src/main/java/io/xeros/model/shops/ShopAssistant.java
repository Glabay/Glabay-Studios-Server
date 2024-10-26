package io.xeros.model.shops;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementTier;
import io.xeros.content.achievement.Achievements.Achievement;
import io.xeros.content.achievement_diary.impl.LumbridgeDraynorDiaryEntry;
import io.xeros.content.fireofexchange.FireOfExchange;
import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.content.lootbag.LootingBag;
import io.xeros.content.questing.hftd.HftdQuest;
import io.xeros.content.wogw.Wogwitems;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.ShopDef;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.save.PlayerSave;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.model.world.ShopHandler;
import io.xeros.util.Misc;
import io.xeros.util.logging.player.ShopBuyLog;
import io.xeros.util.logging.player.ShopSellLog;

public class ShopAssistant {

    public static final int SHOP_INTERFACE_ID = 64000;
    public static final int SHOP_INTERFACE_ID2 = 3824;

    private final Player player;
    public int[] skillCapes = {9747, 9753, 9750, 9768, 9756, 9759, 9762, 9801, 9807, 9783, 9798, 9804, 9780, 9795, 9792, 9774, 9771, 9777, 9786, 9810, 9765, 9948};

    public ShopAssistant(Player client) {
        this.player = client;
    }

    public static int getBuyFromShopPrice(int shopId, int itemId) {
        ShopDef def = ShopDef.get(shopId);
        if (def != null) {
            int definitionPrice = def.getPrice(itemId);
            if (definitionPrice != Integer.MAX_VALUE && definitionPrice != 0)
                return definitionPrice;
        }
        return getItemShopValue(itemId);
    }

    public static int getItemShopValue(int itemId) {
        return ItemDef.forId(itemId).getShopValue();
    }

    public boolean shopSellsItem(int itemID) {
        for (int i = 0; i < ShopHandler.ShopItems.length; i++)
            if (itemID == (ShopHandler.ShopItems[player.myShopId][i] - 1))
                return true;
        return false;
    }

    public void openShop(int ShopID) {
        if (Server.getMultiplayerSessionListener().inAnySession(player))
            return;

        if (Boundary.isIn(player, Boundary.TOURNAMENT_LOBBIES_AND_AREAS) && ShopID != 147) {
            player.sendMessage("You cannot do this right now.");
            return;
        }
        if (!player.getMode().isShopAccessible(ShopID))
            if (Server.isDebug()) player.sendMessage("@red@You normally can't view this shop but debug mode");
            else {
                player.sendMessage("Your game mode does not permit you to access this shop.");
                player.getPA().closeAllWindows();
                return;
            }
        if (player.getLootingBag().isWithdrawInterfaceOpen() || player.getLootingBag().isDepositInterfaceOpen() || player.viewingRunePouch) {
            player.sendMessage("You should stop what you are doing before opening a shop.");
            return;
        }

        setScrollHeight(ShopID);
        player.getPA().resetScrollPosition(64015);
        player.nextChat = 0;
        player.dialogueOptions = 0;
        player.getItems().sendInventoryInterface(3823);
        resetShop(ShopID);
        player.isShopping = true;
        player.myShopId = ShopID;
        player.getPA().sendFrame248(SHOP_INTERFACE_ID, 3822);
        player.getPA().sendFrame126(ShopHandler.ShopName[ShopID], 64003);
    }

    private void setScrollHeight(int shopId) {
        int size = ShopHandler.getShopItems(shopId).size();
        int defaultHeight = 253;
        int rowHeight = (int) Math.ceil(size / 10.0) * 46;
        player.getPA().setScrollableMaxHeight(64015, Math.max(rowHeight, defaultHeight));
    }

    public void updatePlayerShop() {
        for (int i = 1; i < Configuration.MAX_PLAYERS; i++)
            if (PlayerHandler.players[i] != null)
                if (PlayerHandler.players[i].isShopping && PlayerHandler.players[i].myShopId == player.myShopId && i != player.getIndex())
                    PlayerHandler.players[i].updateShop = true;
    }

    public void resetShop(int ShopID) {
        // synchronized (c) {
        int TotalItems = 0;
        for (int i = 0; i < ShopHandler.MaxShopItems; i++) if (ShopHandler.ShopItems[ShopID][i] > 0) TotalItems++;
        if (TotalItems > ShopHandler.MaxShopItems) TotalItems = ShopHandler.MaxShopItems;
        if (ShopID == 80) {
            player.getPA().sendInterfaceHidden(0, 64017);
            player.getPA().sendFrame126("PKP: " + Misc.insertCommas(Integer.toString(player.pkp)), 64019);
        }
        else player.getPA().sendInterfaceHidden(1, 64017);

        if (player.getOutStream() != null) {
            player.getOutStream().createFrameVarSizeWord(53);
            player.getOutStream().writeUShort(64016);
            player.getOutStream().writeUShort(TotalItems);
            int TotalCount = 0;
            for (int i = 0; i < TotalItems; i++) {
                if (ShopHandler.ShopItems[ShopID][i] > 0 || i <= ShopHandler.ShopItemsStandard[ShopID]) {
                    if (ShopHandler.ShopItemsN[ShopID][i] > 254) {
                        player.getOutStream().writeByte(255);
                        player.getOutStream().writeDWord_v2(ShopHandler.ShopItemsN[ShopID][i]);
                    }
                    else player.getOutStream().writeByte(ShopHandler.ShopItemsN[ShopID][i]);
                    if (ShopHandler.ShopItems[ShopID][i] > Configuration.ITEM_LIMIT || ShopHandler.ShopItems[ShopID][i] < 0)
                        ShopHandler.ShopItems[ShopID][i] = Configuration.ITEM_LIMIT;
                    player.getOutStream().writeWordBigEndianA(ShopHandler.ShopItems[ShopID][i]);
                    TotalCount++;
                }
                if (TotalCount > TotalItems) break;
            }
            player.getOutStream().endFrameVarSizeWord();
            player.flushOutStream();
        }
    }

    /**
     * buy item from shop (Shop Price)
     **/
    public void buyFromShopPrice(int removeId, int removeSlot) {
        if (player.myShopId == 0)
            return;
        String itemName = ItemAssistant.getItemName(removeId);
        if (player.myShopId == FireOfExchangeBurnPrice.SHOP_ID) {
            String formattedPrice = Misc.formatCoins(FireOfExchangeBurnPrice.getBurnPrice(player, removeId, false));
            player.sendMessage("You can exchange {} for <col=255>{}</col> exchange points.", itemName, formattedPrice);
            return;
        }

        int ShopValue = (int) (double) getBuyFromShopPrice(player.myShopId, removeId);

        ShopValue *= 1.00;
        ShopValue = player.getMode().getModifiedShopPrice(player.myShopId, removeId, ShopValue);
        String ShopAdd = "";

        if (player.myShopId == 179) {
            player.sendMessage(itemName + ": currently costs 10,000,000 coins.");
            return;
        }
        if (player.myShopId == 40) {
            player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " mage arena points.");
            return;
        }
        if (player.myShopId == 83) {
            player.sendMessage("You cannot buy items from this shop.");
            return;
        }
        if (player.myShopId == 44) if (ItemDef.forId(removeId).getName().contains("head")) {
            player.sendMessage("This product cannot be purchased.");
            return;
        }
        if (player.myShopId == 82) {
            player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Assault points.");
            return;
        }
        if (player.myShopId == 118) {
            player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Raid points.");
            return;
        }
        if (player.myShopId == 44) {
            player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " slayer points.");
            return;
        }
        if (player.myShopId == 13) {
            player.sendMessage("Jossik will switch " + itemName + " for " + getSpecialItemValue(removeId) + " rusty casket.");
            return;
        }
        if (player.myShopId == 10) {
            int price = getSpecialItemValue(removeId);
            if (price == 0) {
                player.sendMessage(itemName + " is free.");
                return;
            }
            player.sendMessage(itemName + ": currently costs [ <col=a30027>" + Misc.insertCommas(price) + " </col>] Slayer Points.");
            return;
        }
        if (player.myShopId == 120) {
            player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " prestige points.");
            return;
        }
        if (player.myShopId == 77) {
            player.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Vote points.");
            return;
        }
        if (player.myShopId == 80) {
            player.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] PKP Points.");
            return;
        }
        if (player.myShopId == 121) {
            player.sendMessage(itemName + ": currently costs [ @pur@" + Misc.insertCommas(getSpecialItemValue(removeId)) + " @bla@] Boss Points.");
            return;
        }
        if (player.myShopId == 15) {
            player.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Christmas Points.");
            return;
        }
        if (player.myShopId == 171) {
            player.sendMessage(itemName + "@bla@: currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Exchange Points.");
            return;
        }
        if (player.myShopId == 172 || player.myShopId == 173) {
            player.sendMessage(itemName + ": currently exchanges for  [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Exchange Points.");
            return;
        }
        if (player.myShopId == 131) {
            player.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Tournament Points.");
            return;
        }
        if (player.myShopId == 119) {
            player.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Blood Money points.");
            return;
        }
        if (player.myShopId == 78) {
            player.sendMessage(itemName + ": currently costs [@lre@" + getSpecialItemValue(removeId) + " @bla@] Achievement Points.");
            return;
        }
        if (player.myShopId == 75) {
            player.sendMessage(itemName + ": currently costs [@gre@ " + getSpecialItemValue(removeId) + " @bla@] PC points.");
            return;
        }
        if (player.myShopId == 9) {
            player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Donator points.");
            return;
        }
        if (player.myShopId == 18) {
            player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " marks of grace.");
            return;
        }
        if (player.myShopId == 115) {
            player.sendMessage(itemName + ": is completeley free.");
            return;
        }
        if (player.myShopId == 116) {
            player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " blood money.");
            return;
        }
        if (player.myShopId == 117) {
            player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " blood money.");
            return;
        }
        if (player.myShopId == 29) {
            if (player.getRechargeItems().hasItem(11136)) ShopValue = (int) (ShopValue * 0.95);
            if (player.getRechargeItems().hasItem(11138)) ShopValue = (int) (ShopValue * 0.9);
            if (player.getRechargeItems().hasItem(11140)) ShopValue = (int) (ShopValue * 0.85);
            if (player.getRechargeItems().hasItem(13103)) ShopValue = (int) (ShopValue * 0.75);
            player.sendMessage(itemName + ": currently costs " + ShopValue + " tokkul" + ShopAdd);
            return;
        }
        if (player.myShopId == 79) {
            player.sendMessage("This item currently costs 500,000 coins.");
            return;
        }
        player.sendMessage(itemName + ": currently costs " + Misc.insertCommas(ShopValue) + " coins (" + Misc.formatCoins(ShopValue) + ")");
    }

    public int getSpecialItemValue(int id) {
        ShopDef shopDef = ShopDef.getDefinitions().get(player.myShopId);
        switch (player.myShopId) {
            case 13: // Jossik quest shop
                return 1;
            case 15:
                switch (id) {
                    case 12887:
                    case 12888:
                    case 12889:
                    case 12890:
                    case 12891:
                    case 12892:
                    case 12893:
                    case 12894:
                    case 12895:
                    case 12896:
                        return 80;

                }
                break;
            case 120:
                switch (id) {
                    case 13317:
                    case 13318:
                    case 13319:
                        return 30;
                    case 13188, 4079:
                        return 25;
                    case 1037:
                        return 75;

                }
                break;
            case 82:
                switch (id) {
                    case 10548:
                        return 30;
                    case 10551:
                        return 100;
                    case 11898:
                    case 11897:
                    case 11896, 11899, 11900:
                        return 25;
                    case 11937:
                    case 11936:
                        return 10;
                }
                break;
            case 147://outlast shop
                if (Boundary.isIn(player, Boundary.OUTLAST_AREA) || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_LOBBY)) {
                    return switch (id) {
                        case 385, 3144, 2301, 3024, 12695, 2444, 3040, 10926 -> 0;
                        default -> 5_999;
                    };
                }
                break;
            case 77: //Vote Shop
                int price = shopDef.getPrice(id);
                if (price != 0) return price;

                switch (id) {
                    case 11936:
                        return 3;
                    case 11920, 21028:
                        return 60;
                    case 12797, 22093, 23677:
                        return 55;
                    case 6739, 12526:
                        return 25;
                    case 2577://ranger boots
                        return 40;
                    case 20211:
                    case 20214:
                    case 20217:
                        return 20;
                    case 20050, 12600:
                        return 30;
                    case 13221, 13241, 13242, 13243:
                        return 100;
                    case 20756, 10507:
                        return 50;
                    case 6666:
                        return 15;
                    case 12783:
                        return 400;
                    case 12639:
                    case 12637:
                    case 12638:
                    case 24198:
                    case 24201:
                    case 24204:
                    case 24195:
                    case 24192:
                        return 45;
                    case 20836:
                        return 85;
                    case 5608:
                        return 150;
                    case 19941:
                        return 85;
                    case 20056:
                        return 150;
                    case 9920:
                        return 120;
                    case 21439:
                        return 45;
                    case 13148:
                        return 15;
                    case 23294:
                    case 23285:
                    case 23288:
                    case 23291:
                    case 776:
                        return 10;
                    case 2841:
                        return 15;
                    case 30023:
                        return 160;
                    case 12954:
                        return 10;
                    case 10551:
                        return 10;

                }
                break;

            case 121: // Boss points shop
                return shopDef.getPrice(id);

            case 171: //Exchange Points
                return FireOfExchange.getExchangeShopPrice(id);
            case 172: //Exchange shop showcase
                switch (id) {
                    case 4722://barrows start
                    case 4720:
                    case 4716:
                    case 4718:
                    case 4714:
                    case 4712:
                    case 4708:
                    case 4710:
                    case 4736:
                    case 4738:
                    case 4732:
                    case 4734:
                    case 4753:
                    case 4755:
                    case 4757:
                    case 4759:
                    case 4745:
                    case 4747:
                    case 4749:
                    case 4751:
                    case 4724:
                    case 4726:
                    case 4728:
                    case 4730:// all barrows complete
                    case 11836://bandos boots
                        return 400;
                    case 2577://ranger boots
                    case 6585://fury
                        return 2000;
                    case 4151://whip
                        return 750;
                    case 6737://b ring
                    case 6733://archer ring

                    case 6731://seers ring

                        return 1850;
                    case 11834://bcp
                    case 11832://tassets
                    case 11826://army helm
                    case 11828://army plate
                    case 11830://arma leg
                    case 13239:// primordials ect +
                    case 13237://pegasion
                    case 13235://eternal
                        return 7500;
                    case 13576://d warhammer
                        return 14000;
                    case 11802://ags
                        return 12000;
                    case 20784://claws
                        return 17000;
                    case 13265://abby dagger
                    case 11808://zgs
                    case 11804://bgs
                    case 11806://sgs
                        return 5000;
                    case 13263://bludgon
                    case 19552://zenyte brace
                    case 19547://anguish
                    case 19553://torture

                        return 8500;
                    case 12002: //occult
                        return 800;
                    case 12809://sara blessed
                    case 12006://tent whip
                        return 5000;
                    case 11284://dfs
                    case 19478://light ballista
                    case 12853://amulet of damned
                        return 6500;
                    case 19481://heavy ballista
                        return 13000;
                    case 12821://spectral
                    case 12825://arcane
                        return 25000;
                    case 12817://ely
                        return 45000;
                    case 11785://arma crossbow
                        return 6500;
                    case 21902:
                        return 8500;
                    case 21012://dhcb
                        return 9000;
                    case 12924://blowpipe
                    case 12926:
                        return 20000;
                    case 11770://seers i
                    case 11771://archer i
                    case 11772://warrior i
                    case 11773://b ring i
                        return 5000;
                    case 20997://t bow
                        return 100000;
                    case 12806://malediction
                    case 12807://odium
                        return 6000;
                }
                break;
            case 173: //Exchange shop showcase 2
                switch (id) {
                    case 22322:
                    case 21015:
                    case 21003:
                    case 12902:
                    case 13196:
                    case 13198:
                    case 12929:
                        return 9000;
                    case 20517:
                    case 20520:
                    case 20595:
                        return 1200;
                    case 20095://ankou start
                    case 20098:
                    case 20101:
                    case 20104:
                    case 20107://ankou end
                    case 20080://mummy start
                    case 20083:
                    case 20086:
                    case 20089:
                    case 20092://mummy end
                        return 4750;
                    case 12603:
                    case 12605:
                        return 1850;
                    case 21902:
                        return 7000;
                    case 21006:
                        return 10000;
                    case 21018://justiciar + ancestral
                    case 21021:
                    case 21024:
                    case 22326:
                    case 22327:
                    case 22328:
                        return 25000;

                }
                break;
            case 131: //tournament Shop
                switch (id) {
                    case 22114, 21301, 21304:
                        return 20;
                    case 23351, Items.IMBUE_DUST:
                        return 50;
                    case 10724:
                    case 10725:
                    case 10726:
                    case 10727:
                    case 10728, 21298:
                        return 15;
                    case 23389, 23091, 23101:
                        return 5;
                    case 23206, Items.BONUS_XP_SCROLL, 23097, 23095:
                        return 10;
                    case 23099:
                        return 3;
                    case 23093:
                        return 4;
                    case 8132:
                    case 10591:
                        return 125;
                    case Items.TEN_DOLLAR_SCROLL:
                        return 100;

                }
                break;
            case 119: //Blood Money Shop
                switch (id) {
                    case 13307, 1526, 222, 236, 224, 9737, 232, 226, 240, 244, 6050, 6052, 3139, 6694, 246, 1976, 2971, 248, 9437:
                        return 1;
                    case 7409:
                        return 750;
                    case 20708:
                    case 20704:
                    case 20706:
                    case 20710:
                    case 12013:
                    case 12014:
                    case 12015:
                    case 12016:
                    case 10941:
                    case 10939:
                    case 10940:
                    case 10933:
                    case 13258:
                    case 13259:
                    case 13260:
                    case 13261:
                    case 13646:
                    case 13642:
                    case 13640:
                    case 13644:
                        return 500;

                }
                break;

            case 80: //PK POINTS SHOP
                switch (id) {
                    case 2996, 13066:
                        return 1;
                    case 12004:
                        return 1600;
                    case 11802:
                        return 2400;
                    case 11804:
                    case 11806:
                    case 11808:
                        return 1400;
                    case 13576, 12924, 21012:
                        return 3000;
                    case 13267:
                        return 1800;
                    case 13263:
                        return 2200;
                    case 12765:
                    case 12766:
                    case 12768:
                    case 12767, 12846, 12851:
                        return 1000;
                    case 11785:
                        return 2500;
                    case 19481, 12806, 12807:
                        return 2000;
                    case 12855:
                    case 12856:
                        return 600;
                    case 12804:
                        return 1500;
                    case 12526:
                        return 350;
                    case 12849:
                        return 300;
                    case 12608:
                    case 12610:
                    case 12612:
                        return 225;
                    case 20716:
                        return 1550;
                    case 12786:
                        return 400;
                    case 12791:
                    case LootingBag.LOOTING_BAG:
                        return 250;
                    case 7462:
                        return 100;
                }
                break;

            case 9: //Donator Point Shop
                switch (id) {
                    case 21295, 10556, 10557, 10558, 10559, 11862:
                        return 100;
                    case 962, 13343:
                        return 60;
                    case 1050, 11802:
                        return 40;
                    case 4084:
                        return 80;
                    case 1419:
                        return 45;
                    case 6199, 6585, 12691, 12692, 11235:
                        return 5;
                    case 13346, 12809:
                        return 25;
                    case 6828:
                        return 15;
                    case 12785:
                        return 65;
                    case 12006:
                        return 9;
                    case 13263:
                        return 20;
                    case 13271:
                        return 15;
                    case 12817:
                        return 110;
                    case 11284:
                        return 15;
                    case 12806:
                    case 12807:
                        return 10;
                    case 11785:
                        return 20;
                    case 8167:
                        return 50;
                    case 19481:
                        return 30;
                    case 11770:
                    case 11772:
                        return 8;
                    case 11771:
                    case 11773:
                        return 12;
                    case 12853:
                        return 10;
                    case 13239:
                    case 13235:
                    case 13237:
                        return 35;
                    case 15098:
                    case 21079:
                    case 21034:
                        return 50;
                    case 21259:
                        return 10;
                    case 8899:
                        return 10;
                    case 12954:
                        return 10;
                    case 10551:
                        return 10;
                    case 11666:
                        return 35;
                }
                break;
            case 10: //slayer Shop
                switch (id) {
                    case Items.ENCHANTED_GEM:
                        return 0;
                    case Items.FIGHTER_TORSO:
                        return 400;
                    case Items.PENANCE_SKIRT:
                        return 200;
                    case Items.FIGHTER_HAT:
                    case Items.FREMENNIK_KILT:
                    case Items.RUNNER_HAT:
                    case Items.HEALER_HAT:
                    case Items.RANGER_HAT:
                    case Items.PENANCE_GLOVES, 7462:
                        return 100;
                    case 7509:
                        return 65;
                    case 405:
                        return 275;
                    case 4081:
                        return 475;
                    case 20581:
                        return 25;
                    case 23943:
                        return 300;
                    case 21183:
                        return 1700;
                    case 23824:
                        return 4;
                    case 13438:
                        return 1000;
                    case 11887:
                        return 10;
                }
                break;
            case 117:
                switch (id) {
                    case 4716:
                    case 4720:
                    case 4722:
                    case 4718: //Dharok
                        return 120;
                    case 4724:
                    case 4726:
                    case 4728:
                    case 4730: //Guthan
                    case 4745:
                    case 4747:
                    case 4749:
                    case 4751: //Torag
                    case 4753:
                    case 4755:
                    case 4757:
                    case 4759: //Verac
                        return 100;
                    case 4708:
                    case 4710:
                    case 4712:
                    case 4714: //Ahrim
                    case 4732:
                    case 4734:
                    case 4736:
                    case 4738: //Karil
                        return 200;
                    case 12006: //Abyssal tentacle
                        return 400;
                    case 13263: //Bludgeon
                        return 3500;
                    case 13271: //Abyssal dagger
                        return 800;
                    case 19481: //Ballista
                        return 1500;
                    case 12902: //Toxic staff
                        return 1000;
                    case 12924: //Blowpipe
                        return 3000;
                    case 11286: //Visage
                        return 100;
                    case 11785: //Armadyl crossbow
                        return 2500;
                    case 13227: //Crystals
                    case 13229:
                    case 13231:
                    case 13233:
                        return 1500;
                    case 12695: //Super combat
                        return 1;
                    case 12929: //Serp helm
                        return 1200;
                    case 12831: //Blessed shield
                        return 800;
                    case 19529: //Zenyte shard
                        return 1500;
                    case 11832: //Bandos chest
                    case 11834: //Bandos tassets
                    case 11826: //Armadyl helm
                    case 11828: //Armadyl chest
                    case 11830: //Armadyl skirt
                        return 700;
                    case 6737: //Berseker ring
                        return 500;
                    case 6735: //Warrior ring
                        return 50;
                    case 6733: //Archers ring
                        return 150;
                    case 6731: //Seers ring
                        return 150;
                    case 12603: //Tyrannical ring
                    case 12605: //Treasonous ring
                        return 200;
                    case 12853: //Amulet of the damned
                        return 700;
                    case 6585: //Fury
                        return 150;
                    case 11802: //Ags
                        return 2000;
                    case 11804: //Bgs
                        return 200;
                    case 11806: //Sgs
                        return 1000;
                    case 11808: //Zgs
                        return 300;
                    case 13576: //Dwh
                        return 3000;
                    case 11235: //Dbow
                        return 150;
                    case 11926: //Odium ward
                    case 11924: //Malediction ward
                        return 700;
                    case 10551: //Torso
                        return 150;
                    case 10548: //Fighter hat
                        return 100;
                    case 11663: //Void mage helm
                    case 11665: //Void melee helm
                    case 11664: //Void ranger helm
                    case 8842: //Void gloves
                        return 50;
                    case 8839: //Void top
                    case 8840: //Void bottom
                        return 75;
                }
                break;

            case 2:
                if (id == 527)
                    return 256;
                break;

            case 116:
                if (ItemDef.forId(id).getName().contains("dharok")) return 20;
                if (ItemDef.forId(id).getName().contains("guthan") ||
                    ItemDef.forId(id).getName().contains("torag") ||
                    ItemDef.forId(id).getName().contains("verac") ||
                    ItemDef.forId(id).getName().contains("karil")) return 12;
                if (ItemDef.forId(id).getName().contains("ahrim")) return 14;

                switch (id) {
                    case 12695: //Super combat
                        return 5;

                    case 12831: //Blessed spirit shield
                        return 25;

                    case 11772: //Warriors ring
                    case 12692: //Treasonous ring
                    case 12691: //Tyrannical ring
                        return 50;

                    case 12924: //Blowpipe
                    case 11770: //Rings
                    case 11771:
                    case 11773:
                    case 12851: //Amulet of damned
                    case 12853: //Amulet of damned
                        return 75;

                    case 11235: //Dbow
                        return 150;

                    case 12929: //Serp helmets
                    case 13196:
                    case 13198:
                    case 13235: //Cerb boots
                    case 13237:
                    case 13239:
                    case 19553: //Amulet of torture
                    case 19547: //Anguish
                        return 250;

                    case 12807: //Wards
                    case 12806:
                        return 300;

                    case 11804: //Godswords
                    case 11806:
                    case 11808:
                        return 500;

                    case 12902: //Tsotd
                    case 13271: //Abyssal dagger
                        return 800;

                    case 11802: //Armadyl godsword
                    case 13576: //D warhammer
                    case 13263: //Abyssal bludgeon
                        return 1000;

                    case 19481: //Heavy ballista
                        return 1500;

                    case 12821: //Spectral
                        return 2000;

                    case 12825: //Arcane
                        return 2500;

                    case 12817: //Elysian
                        return 3500;
                }
                break;
        }

        switch (id) {
            /*
             * Graceful store
             */
            case 11850:
                return 35;
            case 11852, 2577, 2581, 7398, 7399, 7400, 1052, 11860:
                return 40;
            case 11854:
                return 55;
            case 11856:
                return 60;
            case 11858:
                return 30;
            case 12792:
                return 15;
            case 12641:
                return 10;
            /*
             * PK STORE
             */
            case 11900:
                return 60;
            case 11899:
                return 70;
            case 11898:
                return 50;
            case 11896:
                return 80;
            case 11897:
                return 70;
            case 12829:
                return 120;
            case 2379:
            case 13066:
            case 2289:
                return 1;
            case 12746:
                return 20;
            case 7582:
            case 3144:
            case 4153:
                return 50;
            case 4224:
                return 25;
            case 1249:
                return 300;
            case 2839:
                return 350;
            case 4202:
                return 200;
            case 6720:
                return 300;
            case 4081:
                return 250;
            case 3481:
            case 3483:
            case 3485:
            case 3486:
            case 3488:
            case 6856:
            case 6857:
            case 6858:
            case 6859:
            case 6860:
            case 6861:
            case 6862:
            case 6863:
            case 989:
                return 10;
            case 4333:
            case 4353:
            case 4373:
            case 4393:
            case 4413:
            case 11212:
                return 2;
            case 2996:
            case 23933:
                return 1;
            case 4170:
                return 100;
            case 12020:
                return 200;

            case 10887:
                if (player.myShopId == 9)
                    return 20;
                else
                    return 200;
            case 8849:
                return 20;
            case 8848:
                return 20;
            case 8845:
                return 10;
            case 12751:
                return 500;
            case 7462:
                if (player.myShopId == 10)
                    return 0;

            case 2437:
            case 2441:
            case 2443:
                return 100;
            case 3025:
            case 6686:
                return 150;
            case 11937:
                return 250;
            case 7461:
                if (player.myShopId == 12)
                    return 80;
                else
                    return 8;
            case 7460:
                if (player.myShopId == 12)
                    return 60;
                else
                    return 50;
            case 7459:
                if (player.myShopId == 12)
                    return 50;
                else
                    return 40;
            case 7458:
                return 45;
            case 12006:
                return 30;
            case 12000:
                return 20;
            case 12002:
                return 200;
            case 2677:
                return 15;
            case 2572:
                return 50;
            case 2722:
                return 10;
            case 12399:
                return 160;
            case 13887:
            case 13893:
                if (player.myShopId == 9)
                    return 50;
                else
                    return 280;
            case 13902:
                return 110;
            case 14484:
                return 400;
            case 13896:
            case 13884:
            case 13890:
                if (player.myShopId == 9)
                    return 40;
                else
                    return 250;
            case 13858:
            case 13861:
            case 13864:
                if (player.myShopId == 9)
                    return 20;
                else
                    return 130;
            case 13870:
            case 13873:
            case 13876:
                if (player.myShopId == 9)
                    return 30;
                else
                    return 130;
            case 10551:
            case 10548:
                if (player.myShopId == 9)
                    return 10;
                else if (player.myShopId == 12)
                    return 150;
                else
                    return 20;
            case 10550:
                return 80;
            case 7509:
                return 5;
            case 13116:
                return 500;
            case 536:
            case 537:
                return 1;
            case 6735:
                return 250;
            case 9437:
                if (player.myShopId == 41)
                    return 1000;
                else
                    return 120;
                /*
                 * case 6916: case 6918: case 6920: case 6922: case 6924: return 75;
                 */
            case 9672:
            case 9674:
            case 9676:
                return 130000;
            case 1333:
                return 60000;
            case 4587:
                return 90000;
            case 10925:
                return 15000;
            case 3204:
                return 1000;
            case 6585:
            case 11840:
                if (player.myShopId == 12)
                    return 20;
                else
                    return 400;
            case 12905:
                return 1;
            case 2417:
            case 2415:
            case 2416:
                return 100;
            case 1409:
                return 150;
            case 3839:
            case 3841:
            case 3843:
                if (player.myShopId == 13)//slayer store
                    return 1;
                else
                    return 50;
            case 6916:
            case 6918:
            case 6922:
            case 6924:
                return 175;
            case 11230:
                return 10;
            case 6920:
                return 350;
            case 11864:
                return 400;
            case 13226:
                return 200;
            case 4168:
            case 4166:
            case 4551:
            case 4164:
            case 4155:
                return 10;
            case 6121:
                if (player.myShopId == 80) return 300;
                return 250;
            case 19641:
            case 19645:
            case 19649:
                return 1000;
            case 6570:
                return 20;
            case 11235:
                return 100;
            case 11785:
                if (player.myShopId == 9)
                    return 30;
                else
                    return 750;
            case 11791:
                if (player.myShopId == 9)
                    return 30;
                else
                    return 400;
            case 11061:
                if (player.myShopId == 9)
                    return 50;
                else
                    return 1000;
            case 11907:
                if (player.myShopId == 9)
                    return 50;
                else
                    return 500;
            case 2528:
                return 6;
            case 11739:
                return 2;
            case 6889:
                return 80;
            case 6914:
                return 200;
            case 4732:
            case 4734:
            case 4736:
            case 4738:
                return 12;
            case 4716:
            case 4718:
            case 4720:
            case 4722:
                return 20;
            case 4712:
            case 4710:
            case 4714:
            case 4708:
                return 14;
            case 4724:
            case 4726:
            case 4728:
            case 4730:
            case 4745:
            case 4747:
            case 4749:
            case 4751:
            case 4753:
            case 4755:
            case 4757:
            case 4759:
                return 12;
            case 10338:
            case 10342:
            case 10340:
            case 1989:
                return 500;
            case 11838:
            case 4151:
            case 1961:
                return 100;
            case 10352:
            case 10350:
            case 10348:
            case 10346:
                return 1000;
            case 11826:
                return 80;
            case 11828:
                return 100;
            case 11830:
                return 100;
            case 11283:
            case 1959:
            case 9703:
                if (player.myShopId == 9)
                    return 50;
                else
                    return 400;
            case 11802:
                if (player.myShopId == 9)
                    return 50;
                else
                    return 900;
            case 11832:
            case 11834:
                if (player.myShopId == 9)
                    return 30;
                else
                    return 500;
            case 11804:
                return 700;
            case 11808:
            case 11806:
                return 600;
            // DONATOR
            case 1042:
                return 50;
            case 1048:
                return 50;
            case 1038:
                return 50;
            case 1046:
                return 50;
            case 1044:
                return 90;
            case 1040:
                return 80;
            case 1053:
            case 1055:
            case 1057:
                return 60;
            case 1419:
                return 40;
            case 4566:
                return 40;
            case 4084:
                return 60;
            case 1037:
                return 50;
            case 11919:
                return 10;
            case 12956:
            case 12957:
            case 12958:
            case 12959:
            case 10933:
            case 10939:
            case 10940:
            case 10941:
            case 13258:
            case 13259:
            case 13260:
            case 13261:
                return 5;
            case 12596:
                return 30;
            case 6199:
                if (player.myShopId == 78)
                    return 30;
                else
                    return 20;
                // VOTE
            case 12748:
                return 2;
            case 7409:
                return 15;
            case 9920:
                return 10;
            case 12637:
            case 12638:
            case 12639:
                return 20;
            case 775:
                return 10;
            case 3057:
            case 3058:
            case 3059:
            case 6654:
            case 6655:
            case 6656:
            case 6180:
            case 6181:
            case 6182:
            case 13640:
            case 13642:
            case 13644:
            case 13646:
            case 5553:
            case 5554:
            case 5555:
            case 5556:
            case 5557:
            case 20704:
            case 20706:
            case 20708:
            case 20710:
            case 12013:
            case 12014:
            case 12015:
            case 12016:
                return 5;
            case 776:
            case 20008:
            case 10071:
                return 15;
            case 6666:
                return 20;
            case 1050:
                return 80;
            case 11887:
                return 10;

        }
        return Integer.MAX_VALUE;
    }

    /**
     * Sell item to shop (Shop Price)
     **/
    public void sellToShopPrice(int removeId, int removeSlot) {
        if (player.myShopId == 0)
            return;
        if (player.myShopId == FireOfExchangeBurnPrice.SHOP_ID)
            return;
        boolean CANNOT_SELL = !ItemDef.forId(removeId).isTradable();

        // Can't sell anglerfish
        if (removeId == 13441 || removeId == 13442) {
            player.sendMessage("You can't sell " + ItemAssistant.getItemName(removeId).toLowerCase() + ".");
            return;
        }
        if (player.myShopId == 147
            || player.myShopId == 18) {
            player.sendMessage("You can't sell items here.");
            return;
        }

        if (player.myShopId != 116 && player.myShopId != 115 && CANNOT_SELL) {
            player.sendMessage("You can't sell " + ItemAssistant.getItemName(removeId).toLowerCase() + ".");
            return;
        }
        boolean IsIn = false;
        if (ShopHandler.ShopSModifier[player.myShopId] > 1) for (int j = 0; j <= ShopHandler.ShopItemsStandard[player.myShopId]; j++)
            if (removeId == (ShopHandler.ShopItems[player.myShopId][j] - 1)) {
                IsIn = true;
                break;
            }
        else IsIn = true;
        if (!IsIn) player.sendMessage("You can't sell that item to this store.");
        else {
            int ShopValue = 0;
            String ShopAdd = "";
            if (player.itemId == 13240) ShopValue *= 1;
            if (player.myShopId != 26 && player.itemId != 13240) ShopValue *= 0.337;
            if (player.myShopId == 147) ShopValue *= 0;
            if (player.myShopId == 83) {
                int i = 0;
                for (final Wogwitems.itemsOnWell t : Wogwitems.itemsOnWell.values())
                    if (t.getItemId() == removeId) {
                        ShopValue = (int) (double) t.getItemWorth();
                        i++;
                    }
                if (i == 0) {
                    player.sendMessage("You can't sell this item to this store.");
                    return;
                }
            }
            else ShopValue = (int) (double) getItemShopValue(removeId);

            if (player.myShopId == 44)
                if (!ItemDef.forId(removeId).getName().contains("head")) player.sendMessage("You cannot sell this to the slayer shop.");
                else player.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + ShopValue + " slayer points" + ShopAdd);
            else if (player.myShopId == 18)
                player.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + ShopValue + " marks of grace" + ShopAdd);
            else if (player.myShopId == 172 || player.myShopId == 173) player.sendMessage("You cannot sell items to this shop.");
            else if (player.myShopId == 116)
                player.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + ((int) Math.ceil((getSpecialItemValue(removeId) * 0.60)) + " blood money"));
            else if (player.myShopId == 29)
                player.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + ShopValue + " tokkul" + ShopAdd);
            else {
                ShopValue *= 0.537;
                if (ShopValue >= 1_000_000)
                    ShopAdd = " (" + Misc.formatCoins(ShopValue) + ")";
                player.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + Misc.insertCommas(ShopValue) + " coins" + ShopAdd + ".");
            }
        }
    }

    /**
     * Selling items back to a store
     *
     * @param itemID   itemID that is being sold
     * @param fromSlot fromSlot the item currently is located in
     * @param amount   amount that is being sold
     * @return true is player is allowed to sell back to the store,
     * else false
     */
    public boolean sellItem(int itemID, int fromSlot, int amount) {
        if (player.myShopId == 0 || !player.isInterfaceOpen(SHOP_INTERFACE_ID) && !player.isInterfaceOpen(SHOP_INTERFACE_ID2))
            return false;
        if (Configuration.DISABLE_SHOP_SELL) {
            player.sendMessage("Selling to shops is disabled atm.");
            return false;
        }
        if (Boundary.isIn(player, Boundary.TOURNAMENT_LOBBIES_AND_AREAS)) {
            player.sendMessage("You cannot do this right now.");
            return false;
        }
        if (Server.getMultiplayerSessionListener().inAnySession(player)) return false;
        if (!player.getMode().isItemSellable(player.myShopId, itemID)) {
            player.sendMessage("Your game mode does not permit you to sell this item to the shop.");
            return false;
        }
        if (Configuration.DISABLE_FOE && player.myShopId == FireOfExchange.FOE_SHOP_ID) {
            player.sendMessage("Fire of Exchange has been temporarily disabled.");
            return false;
        }
        if (player.myShopId == FireOfExchangeBurnPrice.SHOP_ID)
            return false;
        if (player.myShopId != 115) if (itemID == 995) {
            player.sendMessage("You can't sell this item.");
            return false;
        }
        if (player.myShopId == 26) if (itemID != 1893 &&
            itemID != 1894 &&
            itemID != 712 &&
            itemID != 2961 &&
            itemID != 6814 &&
            itemID != 950 &&
            itemID != 2962 &&
            itemID != 1613 &&
            itemID != 1614 &&
            itemID != 1993 &&
            itemID != 1994 &&
            itemID != 4692 &&
            itemID != 19473) {
            player.sendMessage("You can't sell this item.");
            return false;
        }
        switch (player.myShopId) {
            case 9:
            case 12:
            case 13:
            case 14:
            case 21:
            case 22:
            case 23:
            case 75:
            case 77:
            case 121:
            case 15:
            case 80:
            case 171:
            case 172:
            case 173:
            case 120:
            case 131:
            case 78:
            case 147:
            case 117:
            case 18:
                player.sendMessage("You cannot sell items to this shop.");
                return false;
        }
        boolean CANNOT_SELL = !ItemDef.forId(itemID).isTradable();
        if (player.myShopId != 116 && player.myShopId != 115) if (CANNOT_SELL) {
            player.sendMessage("You can't sell " + ItemAssistant.getItemName(itemID).toLowerCase() + ".");
            return false;
        }
        if (amount > 0 && itemID == (player.playerItems[fromSlot] - 1)) {
            if (ShopHandler.ShopSModifier[player.myShopId] > 1) {
                boolean IsIn = false;
                for (int i = 0; i <= ShopHandler.ShopItemsStandard[player.myShopId]; i++)
                    if (itemID == (ShopHandler.ShopItems[player.myShopId][i] - 1)) {
                        IsIn = true;
                        break;
                    }
                if (!IsIn) {
                    player.sendMessage("You can't sell that item to this store.");
                    return false;
                }
            }

            boolean noted = ItemDef.forId(player.playerItems[fromSlot] - 1).isNoted();
            boolean stackable = ItemDef.forId(player.playerItems[fromSlot] - 1).isStackable();
            if (amount > player.playerItemsN[fromSlot] && (noted || stackable)) amount = player.playerItemsN[fromSlot];
            else if (amount > player.getItems().getItemAmount(itemID) && !noted && !stackable) amount = player.getItems().getItemAmount(itemID);
            // double ShopValue;
            // double TotPrice;
            int TotPrice2;
            int TotPrice3;
            int TotPrice4 = 0;
            // int Overstock;
            //for (int i = amount; i > 0; i--) {
            TotPrice2 = (int) (double) getItemShopValue(itemID);
            TotPrice3 = (int) (double) getSpecialItemValue(itemID);
            if (player.myShopId == 83) {
                int i = 0;
                for (final Wogwitems.itemsOnWell t : Wogwitems.itemsOnWell.values())
                    if (t.getItemId() == itemID) {
                        TotPrice4 = (int) (double) t.getItemWorth();
                        i++;
                    }
                if (i == 0) {
                    player.sendMessage("You can't sell that item to this store.");
                    return false;
                }
            }
            if (player.myShopId != 26) TotPrice2 *= 0.537;
            TotPrice2 = TotPrice2 * amount;
            TotPrice4 = TotPrice4 * amount;
            if (player.getItems().freeSlots() > 0 || player.getItems().playerHasItem(995))
                if ((ItemDef.forId(itemID).isStackable() || ItemDef.forId(itemID).isNoted()) && player.getItems().playerHasItem(itemID, amount)) {
                    player.getItems().deleteItemNoSave(itemID, player.getItems().getInventoryItemSlot(itemID), amount);
                    logShop("sell", itemID, amount);
                    if (player.myShopId != 12 && player.myShopId != 29 && player.myShopId != 44 && player.myShopId != 18 && player.myShopId != 83 && player.myShopId != 116 && player.myShopId != 115) {
                        player.getItems().addItem(995, TotPrice2);
                        logShop("received", 995, TotPrice2);
                    }
                    else if (player.myShopId == 29) {
                        player.getItems().addItem(6529, TotPrice2);
                        logShop("received", 6529, TotPrice2);
                    }
                    else if (player.myShopId == 18) {
                        player.getItems().addItem(11849, TotPrice2);
                        logShop("received", 11849, TotPrice2);
                    }
                    else if (player.myShopId == 83) {
                        player.getItems().addItem(995, TotPrice4);
                        logShop("received", 995, TotPrice4);
                        addShopItem(itemID, amount);
                    }
                    else if (player.myShopId == 116) {
                        player.getItems().addItem(13307, (int) (double) (TotPrice3 *= 0.30));
                        logShop("received", 13307, (int) (double) (TotPrice3 *= 0.30));
                    }
                    else if (player.myShopId == 44) if (!ItemDef.forId(itemID).getName().contains("head")) return false;
                    else {
                        player.getSlayer().setPoints(player.getSlayer().getPoints() + TotPrice2);
                        player.getQuestTab().updateInformationTab();
                    }
                    //addShopItem(itemID, amount);
                    ShopHandler.ShopItemsDelay[player.myShopId][fromSlot] = 0;
                    player.getItems().sendInventoryInterface(3823);
                    resetShop(player.myShopId);
                    updatePlayerShop();
                    return false;
                }
                else {
                    if (player.myShopId != 12 && player.myShopId != 29 && player.myShopId != 44 && player.myShopId != 18 && player.myShopId != 83 && player.myShopId != 116 && player.myShopId != 115) {
                        player.getItems().addItem(995, TotPrice2);
                        logShop("received", 995, TotPrice2);
                    }
                    else if (player.myShopId == 29) {
                        player.getItems().addItem(6529, TotPrice2);
                        logShop("received", 995, TotPrice2);
                    }
                    else if (player.myShopId == 18) {
                        player.getItems().addItem(11849, TotPrice2);
                        logShop("received", 995, TotPrice2);
                    }
                    else if (player.myShopId == 83) {
                        player.getItems().addItem(995, TotPrice4);
                        logShop("received", 995, TotPrice4);
                        addShopItem(itemID, amount);
                    }
                    else if (player.myShopId == 116) {
                        player.getItems().addItem(13307, (int) (double) (TotPrice3 *= 0.60));
                        logShop("received", 13307, (int) (double) (TotPrice3 *= 0.60));
                    }
                    else if (player.myShopId == 44) if (!ItemDef.forId(itemID).getName().contains("head")) return false;
                    else {
                        player.getSlayer().setPoints(player.getSlayer().getPoints() + TotPrice2);
                        player.getQuestTab().updateInformationTab();
                    }

                    if (!ItemDef.forId(itemID).isNoted()) {
                        logShop("sell", itemID, amount);
                        player.getItems().deleteItem2(itemID, amount);
                    }
                    // addShopItem(itemID, 1);
                }
            else {
                player.sendMessage("You don't have enough space in your inventory.");
                player.getItems().sendInventoryInterface(3823);
                return false;
            }
            //}
            player.getItems().sendInventoryInterface(3823);
            resetShop(player.myShopId);
            updatePlayerShop();
            PlayerSave.saveGame(player);
            return true;
        }
        return true;
    }

    public boolean addShopItem(int itemID, int amount) {
        boolean Added = false;
        if (amount <= 0) return false;

        if (ItemDef.forId(itemID).isNoted()) itemID = player.getItems().getUnnotedItem(itemID);
        for (int i = 0; i < ShopHandler.ShopItems.length; i++)
            if ((ShopHandler.ShopItems[player.myShopId][i] - 1) == itemID) {
                ShopHandler.ShopItemsN[player.myShopId][i] += amount;
                Added = true;
            }
        if (!Added) for (int i = 0; i < ShopHandler.ShopItems.length; i++)
            if (ShopHandler.ShopItems[player.myShopId][i] == 0) {
                ShopHandler.ShopItems[player.myShopId][i] = (itemID + 1);
                ShopHandler.ShopItemsN[player.myShopId][i] = amount;
                ShopHandler.ShopItemsDelay[player.myShopId][i] = 0;
                break;
            }
        return true;
    }

    /**
     * Buying item(s) from a store
     *
     * @param itemID   itemID that the player is buying
     * @param fromSlot fromSlot the items is currently located in
     * @param amount   amount of items the player is buying
     * @return true if the player is allowed to buy the item(s),
     * else false
     */
    public boolean buyItem(int itemID, int fromSlot, int amount) {
        if (player.myShopId == 0 || !player.isInterfaceOpen(SHOP_INTERFACE_ID) && !player.isInterfaceOpen(SHOP_INTERFACE_ID2))
            return false;
        if (Configuration.DISABLE_SHOP_BUY) {
            player.sendMessage("Buying from shops is disabled atm.");
            return false;
        }
        if (Server.getMultiplayerSessionListener().inAnySession(player)) {
            if (player.debugMessage)
                player.sendMessage("");
            return false;
        }
        if (!Boundary.isIn(player, Boundary.TOURNAMENT_LOBBIES_AND_AREAS) && player.myShopId == 147) {
            player.sendMessage("You cannot do this right now.");
            return false;
        }
        if (Boundary.isIn(player, Boundary.TOURNAMENT_LOBBIES_AND_AREAS) && player.myShopId != 147) {
            player.sendMessage("You cannot do this right now.");
            return false;
        }
        if (!player.getMode().isItemPurchasable(player.myShopId, itemID)) {
            player.sendMessage("Your game mode does not allow you to buy this item.");
            return false;
        }
        if (player.myShopId == 83) {
            player.sendMessage("You cannot buy items from this shop.");
            return false;
        }
        if (player.myShopId == FireOfExchangeBurnPrice.SHOP_ID)
            return false;

        if (Configuration.DISABLE_FOE && player.myShopId == FireOfExchange.FOE_SHOP_ID) {
            player.sendMessage("Fire of Exchange has been temporarily disabled.");
            return false;
        }
        /*
         * ACHIEVMENT LOST N FOUND AREA
         */
        if (player.myShopId == 178)
            if (itemID == 10941 || itemID == 10933 && player.getAchievements().isComplete(AchievementTier.TIER_1.ordinal(), Achievement.Woodcutting_Task_I.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 10939 && !player.getAchievements().isComplete(AchievementTier.TIER_2.ordinal(), Achievement.INTERMEDIATE_CHOPPER.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 10940 && !player.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.EXPERT_CHOPPER.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 13258 && !player.getAchievements().isComplete(AchievementTier.TIER_1.ordinal(), Achievement.Fishing_Task_I.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 13259 || itemID == 13261 && !player.getAchievements().isComplete(AchievementTier.TIER_2.ordinal(), Achievement.INTERMEDIATE_FISHER.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 13260 && !player.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.EXPERT_FISHER.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 12013 || itemID == 12016 && !player.getAchievements().isComplete(AchievementTier.TIER_1.ordinal(), Achievement.Mining_Task_I.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 12014 && !player.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.EXPERT_MINER.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 12015 && !player.getAchievements().isComplete(AchievementTier.TIER_2.ordinal(), Achievement.INTERMEDIATE_MINER.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 13646 && itemID == 13644 && !player.getAchievements().isComplete(AchievementTier.TIER_1.ordinal(), Achievement.Farming_Task_I.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 13640 && !player.getAchievements().isComplete(AchievementTier.TIER_2.ordinal(), Achievement.INTERMEDIATE_FARMER.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 13642 && !player.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.EXPERT_FARMER.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 20710 && !player.getAchievements().isComplete(AchievementTier.TIER_1.ordinal(), Achievement.Firemaking_Task_I.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 20706 || itemID == 20704 && !player.getAchievements().isComplete(AchievementTier.TIER_2.ordinal(), Achievement.INTERMEDIATE_PYRO.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 20708 || itemID == 20712 && !player.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.EXPERT_PYRO.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 5556 || itemID == 5557 && !player.getAchievements().isComplete(AchievementTier.TIER_1.ordinal(), Achievement.Theiving_Task_I.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 5555 || itemID == 5553 && !player.getAchievements().isComplete(AchievementTier.TIER_2.ordinal(), Achievement.INTERMEDIATE_THIEF.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 5554 && !player.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.EXPERT_THIEF.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        if (player.myShopId == 178)
            if (itemID == 20164 || itemID == 6666 && !player.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.CLUE_CHAMP.getId())) {
                player.sendMessage("You have not yet unlocked this item.");
                return false;
            }
        /*
         * ACHIEVMENT LOST N FOUND AREA ENDING
         */
        if (player.myShopId == 81) if (!player.getDiaryManager().getWildernessDiary().hasDoneHard()) {
            player.sendMessage("You must have completed wilderness hard diaries to purchase this.");
            return false;
        }

        if (player.myShopId == 6) if (player.getMode().isIronmanType()) if (!player.getDiaryManager().getVarrockDiary().hasDoneMedium()) {
            player.sendMessage("You must have completed the varrock diary up to medium to purchase this.");
            return false;
        }

        if (player.myShopId == 115) {
            if (!player.getPosition().inClanWarsSafe()) {
                System.out.println("[District] " + player.getDisplayName() + " Attempting to purchase from free shop outside disitrict.");
                return false;
            }
            if (ItemDef.forId(itemID).isStackable()) amount = 1000;
            if (itemID == 12934) amount = 10000;
        }
        if (player.myShopId == 116) if (!player.getPosition().inClanWarsSafe()) {
            System.out.println("[District] " + player.getDisplayName() + " Attempting to purchase from bm shop outside disitrict.");
            return false;
        }

        if (itemID == LootingBag.LOOTING_BAG) if (player.getItems().getItemCount(LootingBag.LOOTING_BAG, true) > 0
            || player.getItems().getItemCount(LootingBag.LOOTING_BAG_OPEN, true) > 0) {
            player.sendMessage("It seems like you already have one of these.");
            return false;
        }
        if (itemID == 10941) if (player.getItems().getItemCount(10941, true) > 0) {
            player.sendMessage("It seems like you already have one of these.");
            return false;
        }
        /*
          Slayer shop
         */
        if (player.myShopId == 44) if (ItemDef.forId(itemID).getName().contains("head")) {
            player.sendMessage("This product cannot be purchased.");
            return false;
        }
        /*
          Avaas
         */
        if (player.myShopId == 19) if (itemID == 10498) {
            if (!player.getItems().playerHasItem(886, 75)) {
                player.sendMessage("You must have 75 steel arrows to exchange for this attractor");
                return false;
            }
            player.getItems().deleteItem(886, 775);
            player.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.ATTRACTOR);
        }
        /*
          RFD Shop
         */
        if (player.myShopId == 14) {
            if (itemID == 7458 && player.rfdGloves < 1) {
                player.sendMessage("You are not eligible to buy these.");
                return false;
            }
            if (itemID == 7459 && player.rfdGloves < 2) {
                player.sendMessage("You are not eligible to buy these.");
                return false;
            }
            if (itemID == 7460 && player.rfdGloves < 3) {
                player.sendMessage("You are not eligible to buy these.");
                return false;
            }
            if (itemID == 7461 && player.rfdGloves < 5) {
                player.sendMessage("You are not eligible to buy these.");
                return false;
            }
            if (itemID == 7462) if (player.rfdGloves < 6) {
                player.sendMessage("You are not eligible to buy these.");
                return false;
            }
        }
        if (player.myShopId == 17) {
            skillBuy(itemID);
            return false;
        }
        if (player.myShopId == 179) {
            millBuy(itemID);
            return false;
        }
        if (!shopSellsItem(itemID))
            return false;
        if (amount > 0) {
            if (fromSlot >= ShopHandler.ShopItemsN[player.myShopId].length) {
                player.sendMessage("There was a problem buying that item, please report it to staff!");
                return false;
            }

            if (player.myShopId != 115)
                if (amount > ShopHandler.ShopItemsN[player.myShopId][fromSlot]) amount = ShopHandler.ShopItemsN[player.myShopId][fromSlot];
            int TotPrice2;
            // int Overstock;
            int Slot;
            int Slot1;

            switch (player.myShopId) {
                case 9:
                case 10:
                case 12:
                case 13:
                case 18:
                case 40:
                case 44:
                case 75:
                case 77:
                case 121:
                case 15:
                case 172:
                case 173:
                case 171:
                case 131:
                case 78:
                case 79:
                case 80:
                case 82:
                case 116:
                case 117:
                case 118:
                case 119:
                case 120:
                    handleOtherShop(itemID, amount);
                    return false;
            }
            TotPrice2 = (int) (double) getBuyFromShopPrice(player.myShopId, itemID);
            TotPrice2 = player.getMode().getModifiedShopPrice(player.myShopId, itemID, TotPrice2);
            Slot = player.getItems().getInventoryItemSlot(995);
            Slot1 = player.getItems().getInventoryItemSlot(6529);
            if (TotPrice2 <= 1) {
                TotPrice2 = (int) (double) getItemShopValue(itemID);
                TotPrice2 *= 1.66;
            }
            if (player.myShopId == 115) TotPrice2 = -1;
            if (player.myShopId == 147) TotPrice2 = 0;
            if (player.myShopId == 124 && player.amDonated >= 150 && itemID == 299) TotPrice2 = 0;

            if (ItemDef.forId(itemID).isStackable()) {
                if (player.myShopId != 29 || player.myShopId != 147) {
                    if (TotPrice2 != 0) if (!player.getItems().playerHasItem(995, TotPrice2 * amount) || TotPrice2 == 0) {
                        int coinAmount = player.getItems().getItemAmount(995);

                        int amountThatCanBeBought = (int) (double) (coinAmount / TotPrice2);
                        if (TotPrice2 == 0) amountThatCanBeBought = 1000;
                        if (amountThatCanBeBought > 0) amount = amountThatCanBeBought;
                        player.sendMessage("You don't have enough coins.");
                    }

                    if (player.getItems().playerHasItem(995, TotPrice2 * amount) || TotPrice2 == 0) if (player.getItems().freeSlots() > 0) {
                        player.getItems().deleteItem(995, TotPrice2 * amount);
                        player.getItems().addItem(itemID, amount);
                        logShop("bought", itemID, amount);
                        if (player.myShopId != 115) {
                            //ShopHandler.ShopItemsN[c.myShopId][fromSlot] -= amount;
                            ShopHandler.ShopItemsDelay[player.myShopId][fromSlot] = 0;
                            if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[player.myShopId])
                                ShopHandler.ShopItems[player.myShopId][fromSlot] = 0;
                        }
                    }
                    else {
                        player.sendMessage("You don't have enough space in your inventory.");
                        player.getItems().sendInventoryInterface(3823);
                        return false;
                    }
                    else {
                        player.sendMessage("You don't have enough coins.");
                        player.getItems().sendInventoryInterface(3823);
                        return false;
                    }
                }
            }
            else if (player.myShopId == 29) {
                if (player.getRechargeItems().hasItem(11136)) TotPrice2 = (int) (TotPrice2 * 0.95);
                if (player.getRechargeItems().hasItem(11138)) TotPrice2 = (int) (TotPrice2 * 0.9);
                if (player.getRechargeItems().hasItem(11140)) TotPrice2 = (int) (TotPrice2 * 0.85);
                if (player.getRechargeItems().hasItem(13103)) TotPrice2 = (int) (TotPrice2 * 0.75);
                if (player.playerItemsN[Slot1] >= TotPrice2 * amount) if (player.getItems().freeSlots() > 0) {
                    player.getItems().deleteItem(6529, TotPrice2 * amount);
                    player.getItems().addItem(itemID, amount);
                    logShop("bought", itemID, amount);
                    ShopHandler.ShopItemsN[player.myShopId][fromSlot] -= amount;
                    ShopHandler.ShopItemsDelay[player.myShopId][fromSlot] = 0;
                    if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[player.myShopId]) ShopHandler.ShopItems[player.myShopId][fromSlot] = 0;
                }
                else {
                    player.sendMessage("You don't have enough space in your inventory.");
                    player.getItems().sendInventoryInterface(3823);
                    return false;
                }
                else {
                    player.sendMessage("You don't have enough tokkul.");
                    player.getItems().sendInventoryInterface(3823);
                    return false;
                }
            }
            else {
                int boughtAmount = 0;


                // Iterate and buy each single item individually
                for (int i = amount; i > 0; i--) {
                    if (player.myShopId == 115) TotPrice2 = -1;
                    if (player.myShopId == 147) TotPrice2 = -1;
                    if (Slot == -1 && player.myShopId != 29 && player.myShopId != 115 && player.myShopId != 147) {
                        player.sendMessage("You don't have enough coins.");
                        return false;
                    }
                    if (Slot1 == -1 && (player.myShopId == 29)) {
                        player.sendMessage("You don't have enough tokkul.");
                        return false;
                    }

                    // not tokkul
                    if (player.myShopId != 29)
                        if (player.getItems().playerHasItem(995, TotPrice2) || TotPrice2 == 0) if (player.getItems().freeSlots() > 0) {
                            player.getItems().deleteItem(995, player.getItems().getInventoryItemSlot(995), TotPrice2);
                            player.getItems().addItem(itemID, 1);
                            boughtAmount++;
                            if (player.myShopId == 6) if (player.getMode().isIronmanType()) {
                                if (!player.getDiaryManager().getVarrockDiary().hasDoneMedium()) {
                                    player.sendMessage("You must have completed the varrock diary up to medium to purchase this.");
                                    return false;
                                }
                                if (player.getRechargeItems().useItem(2, 1)) {

                                }
                                else {
                                    player.sendMessage("You have already purchased 150 battlestaves today.");
                                    return false;
                                }
                            }
                            //if (c.myShopId != 115) {
                            //ShopHandler.ShopItemsN[c.myShopId][fromSlot] -= 1;
                            //}
                            ShopHandler.ShopItemsDelay[player.myShopId][fromSlot] = 0;
                            if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[player.myShopId])
                                ShopHandler.ShopItems[player.myShopId][fromSlot] = 0;
                        }
                        else {
                            player.sendMessage("You don't have enough space in your inventory.");
                            player.getItems().sendInventoryInterface(3823);
                            return false;
                        }
                        else {
                            player.sendMessage("You don't have enough coins.");
                            player.getItems().sendInventoryInterface(3823);
                            return false;
                        }

                    // tokkul
                    if (player.myShopId == 29) {
                        if (player.getRechargeItems().hasItem(11136)) TotPrice2 = (int) (TotPrice2 * 0.95);
                        if (player.getRechargeItems().hasItem(11138)) TotPrice2 = (int) (TotPrice2 * 0.9);
                        if (player.getRechargeItems().hasItem(11140)) TotPrice2 = (int) (TotPrice2 * 0.85);
                        if (player.getRechargeItems().hasItem(13103)) TotPrice2 = (int) (TotPrice2 * 0.75);
                        if (player.playerItemsN[Slot1] >= TotPrice2) if (player.getItems().freeSlots() > 0) {
                            player.getItems().deleteItem(6529, player.getItems().getInventoryItemSlot(6529), TotPrice2);
                            player.getItems().addItem(itemID, 1);
                            ShopHandler.ShopItemsN[player.myShopId][fromSlot] -= 1;
                            ShopHandler.ShopItemsDelay[player.myShopId][fromSlot] = 0;
                            if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[player.myShopId])
                                ShopHandler.ShopItems[player.myShopId][fromSlot] = 0;
                        }
                        else {
                            player.sendMessage("You don't have enough space in your inventory.");
                            player.getItems().sendInventoryInterface(3823);
                            return false;
                        }
                        else {
                            player.sendMessage("You don't have enough tokkul.");
                            player.getItems().sendInventoryInterface(3823);
                            return false;
                        }
                    }
                }


                if (boughtAmount > 0) logShop("bought", itemID, boughtAmount);
            }
            player.getItems().sendInventoryInterface(3823);
            resetShop(player.myShopId);
            updatePlayerShop();
            return true;
        }
        return false;
    }

    public void logShop(String action, int itemId, int amount) {
        try {
            switch (action) {
                case "bought":
                    Server.getLogging().write(new ShopBuyLog(player, player.myShopId, ShopHandler.ShopName[player.myShopId], new GameItem(itemId, amount)));
                    break;
                case "sell":
                    Server.getLogging().write(new ShopSellLog(player, player.myShopId, ShopHandler.ShopName[player.myShopId], new GameItem(itemId, amount)));
                    break;
                case "received": // Ignore this one for now
                    break;
                default:
                    throw new IllegalArgumentException("Action not supported " + action);
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Special currency stores
     *
     * @param itemID itemID that is being bought
     * @param amount amount that is being bought
     */
    public void handleOtherShop(int itemID, int amount) {
        if (amount <= 0) {
            if (player.myShopId == 172 || player.myShopId == 173) {
                player.sendMessage("This item cannot be bought, its on showcase only.");
                return;
            }
            player.sendMessage("You need to buy at least one or more of this item.");
            return;
        }
        if (!player.getItems().isStackable(itemID)) if (amount > player.getItems().freeSlots()) amount = player.getItems().freeSlots();
        if (player.myShopId == 40) {
            if (player.getItems().freeSlots() < 1) {
                player.sendMessage("You need at least one free slot to buy this.");
                return;
            }
            int itemValue = getSpecialItemValue(itemID) * amount;
            if (player.getArenaPoints() < itemValue) {
                player.sendMessage("You do not have enough arena points to buy this from the shop.");
                return;
            }
            player.setArenaPoints(player.getArenaPoints() - itemValue);
            player.getQuestTab().updateInformationTab();
            player.getItems().addItem(itemID, amount);
            player.getItems().sendInventoryInterface(3823);
            logShop("bought", itemID, amount);
            return;
        }
        if (player.myShopId == 82) {
            if (player.getItems().freeSlots() < 1) {
                player.sendMessage("You need at least one free slot to buy this.");
                return;
            }
            int itemValue = getSpecialItemValue(itemID) * amount;
            if (player.getShayPoints() < itemValue) {
                player.sendMessage("You do not have enough assault points to buy this from the shop.");
                return;
            }
            player.setShayPoints(player.getShayPoints() - itemValue);
            player.getItems().addItem(itemID, amount);
            player.getItems().sendInventoryInterface(3823);
            logShop("bought", itemID, amount);
            return;
        }
        if (player.myShopId == 118) {
            if (player.getItems().freeSlots() < 1) {
                player.sendMessage("You need at least one free slot to buy this.");
                return;
            }
            int itemValue = getSpecialItemValue(itemID) * amount;
            if (player.getRaidPoints() < itemValue) {
                player.sendMessage("You do not have enough raid points to buy this from the shop.");
                return;
            }
            player.setRaidPoints(player.getRaidPoints() - itemValue);
            player.getItems().addItem(itemID, amount);
            player.getItems().sendInventoryInterface(3823);
            logShop("bought", itemID, amount);
            return;
        }
        if (player.myShopId == 120) {
            if (player.getItems().freeSlots() < 1) {
                player.sendMessage("You need at least one free slot to buy this.");
                return;
            }
            int itemValue = getSpecialItemValue(itemID) * amount;
            if (player.getPrestigePoints() < itemValue) {
                player.sendMessage("You do not have enough prestige points to buy this from the shop.");
                return;
            }
            player.prestigePoints = (player.getPrestigePoints() - itemValue);
            player.getItems().addItem(itemID, amount);
            player.getItems().sendInventoryInterface(3823);
            logShop("bought", itemID, amount);
            return;
        }
        if (player.myShopId == 13) {
            int itemValue = getSpecialItemValue(itemID) * amount;
            if (!player.getItems().playerHasItem(HftdQuest.CASKET_TO_BUY_BOOK, itemValue)) {
                player.sendMessage("You do not have enough rusty caskets to switch with Jossik.");
                return;
            }
            player.getItems().deleteItem(HftdQuest.CASKET_TO_BUY_BOOK, itemValue);
            player.getItems().addItem(itemID, amount);
            player.getItems().sendInventoryInterface(3823);
            logShop("bought", itemID, amount);
            return;
        }

        if (player.myShopId == 117 && itemID == 12695) {
            int itemValue = getSpecialItemValue(itemID) * amount;
            if (!player.getItems().playerHasItem(13307, itemValue)) {
                player.sendMessage("You do not have enough blood money to purchase this.");
                return;
            }
            player.getItems().deleteItem(13307, itemValue);
            player.getItems().addItem(itemID + 1, amount * 2);
            player.getItems().sendInventoryInterface(3823);
            logShop("bought", itemID, amount);
            return;
        }
        if (player.myShopId == 116/* || c.myShopId == 117*/) {
            int itemValue = getSpecialItemValue(itemID) * amount;
            if (!player.getItems().playerHasItem(13307, itemValue)) {
                player.sendMessage("You do not have enough blood money to purchase this.");
                return;
            }
            player.getItems().deleteItem(13307, itemValue);
            player.getItems().addItem(itemID, amount);
            player.getItems().sendInventoryInterface(3823);
            logShop("bought", itemID, amount);
            return;
        }
        if (player.myShopId == 18) {
            int itemValue = getSpecialItemValue(itemID) * amount;
            if (!player.getItems().playerHasItem(11849, itemValue)) {
                player.sendMessage("You do not have enough marks of grace to purchase this.");
                return;
            }
            player.getItems().deleteItem(11849, itemValue);
            player.getItems().addItem(itemID, amount);
            player.getItems().sendInventoryInterface(3823);
            logShop("bought", itemID, amount);
            return;
        }
        if (player.myShopId == 79) {

            if (player.getItems().playerHasItem(itemID) || player.getItems().isWearingItem(itemID) || player.getItems().bankContains(itemID)) {
                player.sendMessage("You still have this item, you have not lost it.");
                return;
            }
            if (player.getItems().freeSlots() < 1) {
                player.sendMessage("You need atleast one free slot to buy this.");
                return;
            }
            if (!player.getItems().playerHasItem(995, 500_000)) {
                player.sendMessage("You need at least 500,000GP to purchase this item.");
                return;
            }
            player.getItems().deleteItem2(995, 500_000);
            player.getItems().addItem(itemID, 1);
            player.getItems().sendInventoryInterface(3823);
            player.sendMessage("You have redeemed the " + ItemAssistant.getItemName(itemID) + ".");
            logShop("bought", itemID, amount);
            return;
        }
        if (player.myShopId == 44) {
            if (itemID == 13226) if (player.getItems().getItemCount(13226, true) == 1) {
                player.sendMessage("You already have a herb sack, theres no need for another.");
                return;
            }
            if (itemID == 12020) if (player.getItems().getItemCount(12020, true) == 1) {
                player.sendMessage("You already have a gem bag. theres no need for another.");
                return;
            }
            if (player.getSlayer().getPoints() >= getSpecialItemValue(itemID) * amount) {
                if (player.getItems().freeSlots() > 0) {
                    player.getSlayer().setPoints(player.getSlayer().getPoints() - (getSpecialItemValue(itemID) * amount));
                    player.getItems().addItem(itemID, amount);
                    player.getItems().sendInventoryInterface(3823);
                    player.getQuestTab().updateInformationTab();
                    logShop("bought", itemID, amount);
                }
            }
            else player.sendMessage("You do not have enough slayer points to buy this item.");
        }
        else if (player.myShopId == 9) if (player.donatorPoints >= getSpecialItemValue(itemID) * amount) {
            if (player.getItems().freeSlots() > 0) {
                player.donatorPoints -= getSpecialItemValue(itemID) * amount;
                player.getQuestTab().updateInformationTab();
                player.getItems().addItem(itemID, amount);
                player.getItems().sendInventoryInterface(3823);
                logShop("bought", itemID, amount);
            }
        }
        else player.sendMessage("You do not have enough donator points to buy this item.");
        else if (player.myShopId == 10) if (player.getSlayer().getPoints() >= getSpecialItemValue(itemID) * amount) {
            if (player.getItems().freeSlots() > 0) {
                player.getSlayer().setPoints(player.getSlayer().getPoints() - (getSpecialItemValue(itemID) * amount));
                player.getItems().addItem(itemID, amount);
                player.getItems().sendInventoryInterface(3823);
                player.getQuestTab().updateInformationTab();
                logShop("bought", itemID, amount);
            }
        }
        else player.sendMessage("You do not have enough slayer points to buy this item.");
        else if (player.myShopId == 78) if (player.getAchievements().points >= getSpecialItemValue(itemID) * amount) {
            if (player.getItems().freeSlots() > 0) {
                player.getAchievements().points -= getSpecialItemValue(itemID) * amount;
                player.getItems().addItem(itemID, amount);
                player.getItems().sendInventoryInterface(3823);
                logShop("bought", itemID, amount);
            }
        }
        else player.sendMessage("You do not have enough achievement points to buy this item.");
        else if (player.myShopId == 75) if (player.pcPoints >= getSpecialItemValue(itemID) * amount) {
            if (player.getItems().freeSlots() > 0) {
                player.pcPoints -= getSpecialItemValue(itemID) * amount;
                player.getQuestTab().updateInformationTab();
                player.getItems().addItem(itemID, amount);
                player.getItems().sendInventoryInterface(3823);
                logShop("bought", itemID, amount);
            }
        }
        else player.sendMessage("You do not have enough PC Points to buy this item.");
        else if (player.myShopId == 77) if (player.votePoints >= getSpecialItemValue(itemID) * amount) {
            if (player.getItems().freeSlots() > 0) {
                player.votePoints -= getSpecialItemValue(itemID) * amount;
                player.getQuestTab().updateInformationTab();
                player.getItems().addItem(itemID, amount);
                player.getItems().sendInventoryInterface(3823);
                logShop("bought", itemID, amount);
            }
        }
        else player.sendMessage("You do not have enough vote points to buy this item.");
        else if (player.myShopId == 121) if (player.bossPoints >= getSpecialItemValue(itemID) * amount) {
            if (player.getItems().freeSlots() > 0) {
                player.bossPoints -= getSpecialItemValue(itemID) * amount;
                player.getQuestTab().updateInformationTab();
                player.getItems().addItem(itemID, amount);
                player.getItems().sendInventoryInterface(3823);
                logShop("bought", itemID, amount);
            }
        }
        else player.sendMessage("You do not have enough Boss Points to buy this item.");
        else if (player.myShopId == 80) if (player.pkp >= getSpecialItemValue(itemID) * amount) {
            if (player.getItems().freeSlots() > 0) {
                player.pkp -= getSpecialItemValue(itemID) * amount;
                player.getQuestTab().updateInformationTab();
                player.getItems().addItem(itemID, amount);
                player.getItems().sendInventoryInterface(3823);
                player.getShops().openShop(80);
                logShop("bought", itemID, amount);
            }
        }
        else player.sendMessage("You do not have enough PKP Points to buy this item.");
        else if (player.myShopId == 171) if (player.exchangePoints >= getSpecialItemValue(itemID) * amount) {
            if (player.getItems().freeSlots() > 0) {
                player.exchangePoints -= getSpecialItemValue(itemID) * amount;
                player.getQuestTab().updateInformationTab();
                player.getItems().addItem(itemID, amount);
                player.getItems().sendInventoryInterface(3823);
                logShop("bought", itemID, amount);
            }
        }
        else player.sendMessage("You do not have enough Exchange Points to buy this item.");
        else if (player.myShopId == 172 || player.myShopId == 173) if (player.showcase >= getSpecialItemValue(itemID) * amount) {
            if (player.getItems().freeSlots() > 0) {
                player.showcase -= getSpecialItemValue(itemID) * amount;
                player.getQuestTab().updateInformationTab();
                player.getItems().addItem(itemID, amount);
                player.getItems().sendInventoryInterface(3823);
                logShop("bought", itemID, amount);
            }
        }
        else player.sendMessage("This item is only a showcase.");
        else if (player.myShopId == 131) if (player.tournamentPoints >= getSpecialItemValue(itemID) * amount) {
            if (player.getItems().freeSlots() > 0) {
                player.tournamentPoints -= getSpecialItemValue(itemID) * amount;
                player.getQuestTab().updateInformationTab();
                player.getItems().addItem(itemID, amount);
                if (itemID == 8132 && player.getItems().getItemCount(8132, false) == 0) player.getCollectionLog().handleDrop(player, 5, 8132, 1);
                if (itemID == 10591 && player.getItems().getItemCount(10591, false) == 0)
                    player.getCollectionLog().handleDrop(player, 5, 10591, 1);
                player.getItems().sendInventoryInterface(3823);
                logShop("bought", itemID, amount);
            }
        }
        else player.sendMessage("You do not have enough Tournament Points to buy this item.");
        else if (player.myShopId == 119) if (player.bloodPoints >= getSpecialItemValue(itemID) * amount) {
            if (player.getItems().freeSlots() > 0) {
                player.bloodPoints -= getSpecialItemValue(itemID) * amount;
                player.getItems().addItem(itemID, amount);
                player.getItems().sendInventoryInterface(3823);
                logShop("bought", itemID, amount);
            }
        }
        else player.sendMessage("You do not have enough blood money points to buy this item.");
    }

    public void openSkillCape() {
        player.myShopId = 17;
        setupSkillCapes(get99Count());
    }
    public void setupSkillCapes(int capes2) {
        player.getPA().sendInterfaceHidden(1, 28050);
        player.getPA().sendInterfaceHidden(1, 28053);
        player.getItems().sendInventoryInterface(3823);
        player.isShopping = true;
        player.myShopId = 17;
        player.getPA().sendFrame248(SHOP_INTERFACE_ID2, 3822);
        player.getPA().sendFrame126("Skillcape Shop", 3901);

        int TotalItems;
        TotalItems = capes2;
        if (TotalItems > ShopHandler.MaxShopItems) TotalItems = ShopHandler.MaxShopItems;

        if (player.getOutStream() != null) {
            player.getOutStream().createFrameVarSizeWord(53);
            player.getOutStream().writeUShort(3900);
            player.getOutStream().writeUShort(TotalItems);
            for (int i = 0; i < 22; i++) {
                if (player.getLevelForXP(player.playerXP[i]) < 99)
                    continue;
                player.getOutStream().writeByte(1);
                player.getOutStream().writeWordBigEndianA(skillCapes[i] + 2);
            }
            player.getOutStream().endFrameVarSizeWord();
            player.flushOutStream();
        }
    }


// , str35, range37, prayer38, wc41,,
//	  smithing46,  thieving50, slayer51, rc53,

    public void millBuy(int item) {
        int millcapeskill = (item - 33033);
        if (player.getItems().freeSlots() > 1) if (player.getItems().playerHasItem(995, 10000000)) if (player.playerXP[millcapeskill] >= 200000000) {
            player.getItems().deleteItem(995, player.getItems().getInventoryItemSlot(995), 10000000);

            player.getItems().addItem(item, 1);


        }
        else player.sendMessage("You must have 200m XP in the skill of the cape you're trying to buy.");
        else player.sendMessage("You need 10m to buy this item.");
        else player.sendMessage("You must have at least 1 inventory spaces to buy this item.");


    }

    public int get99Count() {
        int count = 0;
        for (int j = 0; j < 22; j++) if (player.getLevelForXP(player.playerXP[j]) >= 99) count++;
        return count;
    }


    public void skillBuy(int item) {
        int nn = get99Count();
        if (nn > 1)
            nn = 1;
        else
            nn = 0;
        for (int j = 0; j < skillCapes.length; j++)
            if (skillCapes[j] == item || skillCapes[j] + 1 == item) if (player.getItems().freeSlots() > 1)
                if (player.getItems().playerHasItem(995, 99000)) if (player.getLevelForXP(player.playerXP[j]) >= 99) {
                    player.getItems().deleteItem(995, player.getItems().getInventoryItemSlot(995), 99000);
                    player.getItems().addItem(skillCapes[j] + nn, 1);
                    player.getItems().addItem(skillCapes[j] + 2, 1);
                }
                else player.sendMessage("You must have 99 in the skill of the cape you're trying to buy.");
                else player.sendMessage("You need 99k to buy this item.");
            else player.sendMessage("You must have at least 1 inventory spaces to buy this item.");
        player.getItems().sendInventoryInterface(3823);
    }

}
