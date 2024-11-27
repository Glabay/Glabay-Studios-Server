package io.xeros.content.fireofexchange;

import com.google.common.base.Preconditions;
import io.xeros.content.bosspoints.JarsToPoints;
import io.xeros.model.definition.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;
import io.xeros.model.shops.ShopItem;
import io.xeros.model.world.ShopHandler;

import java.util.*;
import java.util.stream.Collectors;

public class FireOfExchangeBurnPrice {

    public static int SHOP_ID;

    public static void init() {
        checkPrices();
        createBurnPriceShop();
    }

    public static void createBurnPriceShop() {
        Map<Integer, Integer> burnPrices = new HashMap<>();
        for (int i = 0; i < 60_000; i++) {
            int price = getBurnPrice(null, i, false);
            if (price > 0)
                burnPrices.put(i, price);
        }

        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(burnPrices.entrySet());

        list.sort((a, b) -> {
            int comparison = b.getValue().compareTo(a.getValue());
            if (comparison == 0) {
                return a.getKey().compareTo(b.getKey());
            }

            return comparison;
        });

        List<ShopItem> shopItems = list.stream().map(it -> new ShopItem(it.getKey() + 1 /* shops need this +1 lol */,
                it.getValue(), it.getValue())).collect(Collectors.toList());
        SHOP_ID = ShopHandler.addShopAnywhere("Fire of Exchange Rates", shopItems);
    }

    private static void checkPrices() {
        for (int i = 0; i < 40_000; i++) {
            int shopBuyPrice = FireOfExchange.getExchangeShopPrice(i);
            int burn = getBurnPrice(null, i, false);
            if (shopBuyPrice != Integer.MAX_VALUE) {
                Preconditions.checkState(shopBuyPrice >= burn, "Item burns for more than shop price: " + i);
            }
        }
    }

    public static void openExchangeRateShop(Player player) {
        player.getShops().openShop(SHOP_ID);
        player.sendMessage("<icon=282> @red@You cannot buy anything here.@bla@ This interface only displays @pur@Fire of Exchange Rates!");
    }

    public static boolean hasValue(int itemId) {
        return getBurnPrice(null, itemId, false) != -1;
    }

    /**
     * Burning price.
     */
    public static int getBurnPrice(Player c, int itemId, boolean displayMessage) {
        if (Arrays.stream(JarsToPoints.JARS).anyMatch(it -> itemId == it)) {
            return JarsToPoints.FOE_POINTS;
        }

        switch (itemId) {
            case Items.TROUVER_PARCHMENT:
                return 2_500;
            case 6739://dragon axe
                return 500;
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
                return 750;
            case 11836://bandos boots
            case Items.BLACK_MASK_10:
            case Items.MAGES_BOOK:
            case Items.DAGONHAI_HAT:
            case Items.DAGONHAI_ROBE_BOTTOM:
            case Items.DAGONHAI_ROBE_TOP:
            case Items.DRAGON_CHAINBODY:
            case Items.LONG_BONE:
                return 1000;
            case 4151://whip
            case 6585://fury
            case 20517://elder top
            case 20520://elder robe
            case 20595://elder hood
                return 1200;
            case 10330://3rd age range begin
            case 10332:
            case 10334:
            case 10336://3rd age range finish
            case 10338://3rd age mage begin
            case 10340:
            case 10342:
            case 10344://3rd age mage finish
            case 10346://3rd age melee begin
            case 10348:
            case 10350:
            case 10352://3rd age melee finish
            case Items.THIRD_AGE_PLATESKIRT:

            case Items.AMULET_OF_THE_DAMNED:
            case 2577://ranger boots
            case 6737://b ring
            case 6733://archer ring
            case 6731://seers ring
            case 11907://trident of the sea
                return 1550;
            case 12006://tent whip
            case Items.BARRELCHEST_ANCHOR:
            case Items.DRAGON_2H_SWORD:
                return 2000;
            case 21892://dragon platebody
            case 21895://dragon kite
            case 12603://tyrannical ring
            case 12605://treasonaus ring
            case 21902://dragon crossbow
            case Items.RING_OF_THE_GODS:
            case Items.THIRD_AGE_BOW:
            case Items.THIRD_AGE_DRUIDIC_ROBE_TOP:
            case Items.THIRD_AGE_DRUIDIC_CLOAK:
            case Items.THIRD_AGE_DRUIDIC_ROBE_BOTTOMS:
            case Items.THIRD_AGE_DRUIDIC_STAFF:
            case Items.THIRD_AGE_LONGSWORD:
            case Items.THIRD_AGE_AXE:
            case Items.THIRD_AGE_PICKAXE:
                return 2500;
            case 11920://dragon pickaxe
            case 12809://sara blessed
                return 3000;
            case 13200://tanz muta
            case 13201://magma muta
            case 11772://warrior i
                return 3800;
            case 13241://infernal pickaxe
                return 4500;
            case 12002: //occult
            case 12899://trident of swamp
            case 12806://malediction
            case 12807://odium
            case Items.SARACHNIS_CUDGEL:
            case Items.RING_OF_THIRD_AGE:
            case Items.BONECRUSHER_NECKLACE:
                return 5000;
            case 11785://arma crossbow
            case 11770://seers i
            case 11771://archer i
            case 11773://b ring i
            case 21015://dihns bulwark
            case 12929://serp helm
            case 13265://abby dagger
            case 11808://zgs
            case 11806://sgs
            case 13271://abby dagger poison
            case 21633://ancient wyvern shield
            case 21000://twisted shield
            case 23975://crystal body
            case 23971://crystal helm
            case 23979://crystal legs
            case 22547://craws bow u
            case 22550://craws bow
            case 22542://viggs mace u
            case 22545://viggs mace
            case 22552://thams sceptre u
            case 22555://thams sceptre
            case 20716://tome of fire
            case 22975://brimstone ring
                return 5300;
            case 12691://tyrannical ring i
            case 12692://tres ring (i)
            case Items.RING_OF_THE_GODS_I:
                return 6000;
            case 19478://light ballista
                return 6500;
            case 13243://infernal axe
            case 11804://bgs
                return 7500;
            case 12902: //toxic staff of the dead
            case 19481://heavy ballista
                return 8500;
            case 11284: //dfs
            case 11283://dfs
            case 21012:// dragon hunter crossbow
            case 19547://anguish
            case 19553://torture
            case 19544://torment brace
            case 19550://ring of suffering
            case 11826://army helm
            case 11828://army plate
            case 11830://arma leg
            case 13196://tanz helm
            case 13198://magma helm
            case Items.IMBUED_HEART:
                return 10_000;
            case 21003://elder maul
            case 21079://arcane scroll
                return 11_400;
            case 11832://bcp
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
                return 12_500;
            case 12922://tanz fang
            case 13263://bludgon
            case 11834://tassets
            case 13239:// primordials
            case 13237://pegasion
            case 13235://eternal
            case 21006://kodai wand
            case 20784://claws
            case 11802://ags
            case 12924://blowpipe
            case 12926://blowpipe
                return 15_000;
            case 13576://d warhammer
            case 21034://dex scroll
            case 23848://crystal corrupt legs
            case 23842://crystal corrupt helm
            case 23845://crystal corrupt plate
            case Items.NEITIZNOT_FACEGUARD:
            case Items.RING_OF_SUFFERING_I:
            case Items.SLED:
                return 20_000;
            case 22322://avernic
            case 22477://avernic hilt
            case 10556://barb icons start
            case 10557:
            case 10558:
            case 10559://barb icons end
            case 1038://phat
            case 1040://phat
            case 1042://phat
            case 1044://phat
            case 1046://phat
            case 1048://phat
            case 1053://hween mask
            case 1055://hween mask
            case 1057://hween mask
            case Items.SANTA_HAT:
                return 22_000;
            case 22978://dragon hunter lance
            case 13343://black santa
            case 11847://gold h ween mask
            case 12821://spectral
            case 12825://arcane
            case 21018://ancestral
            case 21021://ancestral
            case 21024://ancestral
            case 22326://justiciar
            case 22327://justiciar
            case 22328://justiciar
                return 25_000;
            case 22981://ferocious gloves
            case 11863://rainbow partyhat
            case 11862://black phat
            case 13344://inverted santa
            case 24419://inquisitor helm
            case 24420://inquisitor plate
            case 24421://inquisitor skirt
            case 24422://nightmare staff
                return 30_000;
            case Items.SANGUINESTI_STAFF:
            case 24517://eldritch orb
            case 24511://harmonised orb
            case 24514://volatile orb
                return 50_000;
            case 24417://inquisitor mace
            case Items.RING_OF_WEALTH_I:
            case 23995://crystal blade
            case Items.GHRAZI_RAPIER:
                return 75_000;
            case Items.ZURIELS_HOOD:
            case Items.ZURIELS_ROBE_BOTTOM:
            case Items.ZURIELS_ROBE_TOP:
            case Items.STATIUSS_FULL_HELM:
            case Items.STATIUSS_PLATEBODY:
            case Items.STATIUSS_PLATELEGS:
                return 125_000;
            case Items.VESTAS_CHAINBODY:
            case Items.VESTAS_PLATESKIRT:
            case Items.MORRIGANS_COIF:
            case Items.MORRIGANS_LEATHER_BODY:
            case Items.MORRIGANS_LEATHER_CHAPS:
            case Items.VESTAS_SPEAR:
            case Items.ZURIELS_STAFF:
                return 150_000;
            case Items.VESTAS_LONGSWORD:
            case Items.STATIUSS_WARHAMMER:
                return 200_000;
            case 12817://ely
                return 250_000;
            case 20997://t bow
                return 750_000;
            case 22325://scythe
                return 1_200_000;
            case 2399: // FOE KEY
                return 5_000;
            //SKILLING ARTEFACTS
            case 11180://ancient coin
                return 200;
            case 681://ancient talisman
                return 300;
            case 9034://golden stat
                return 2500;
            //CHEST ARTIFACTS
            case 21547://small enriched bone
                return 400;
            case 21549://medium enriched bone
                return 650;
            case 21551://large enriched bone
                return 1200;
            case 21553://rare enriched bone
                return 1550;
            case 10933://Lumberjack Boots
            case 10939://Lumberjack Top
            case 10940://Lumberjack legs
            case 10941://Lumberjack Hat
            case 13258://Angler Hat
            case 13259://Angler Top
            case 13260://Angler Waders
            case 13261://Angler Boots
            case 5553://Rogue Top
            case 5554://Rogue Mask
            case 5555://Rogue Trousers
            case 5556://Rogue Gloves
            case 5557://Rogue Boots
            case 13642://Farmers Jacket
            case 13640://Farmers Boro Trousers
            case 13644://Farmers Boots
            case 13646://Farmers Strawhat
            case 12013://Prospector Helmet
            case 12014://Prospector Jacket
            case 12015://Prospector Legs
            case 12016://Prospector Boots
            case 20704://Pyromancer Garb
            case 20706://Pyromancer Robe
            case 20708://Pyromancer Hood
            case 20710://Pyromancer Boots
                return 500;
            //SLAYER HEADS
            case 7980:
            case 7981:
            case 7979:
            case 21275:
            case 23077:
            case 24466:
                return 350;
            case 2425://vorkath head
                return 500;
            //special items
            case 21046://chest rate relic
            case 22316://sword of xeros
                return 500;
            //FOE PETS START
            case 30010://postie pete
                return 16250;
            case 30012://toucan
            case 30011://imp
                return 19500;
            case 30013://penguin king
                return 22750;
            case 30014://klik
                return 97500;
            case 30015://melee pet
            case 30016://range pet
            case 30017://magic pet
                return 48750;
            case 30018://healer
            case 30019://prayer
                return 52000;
            case 30020://corrupt beast
                return 325000;
            case 30021://roc pet
                return 325000;
            case 30022://yama pet
                return 975000;
            case 23939://seren
                return 65000;
            //dark pets
            case 30110://postie pete
                return 16250;
            case 30112://toucan
            case 30111://imp
                return 19500;
            case 30113://penguin king
                return 22750;
            case 30114://klik
                return 97500;
            case 30115://melee pet
            case 30116://range pet
            case 30117://magic pet
                return 48750;
            case 30118://healer
            case 30119://prayer
                return 52000;
            case 30120://corrupt beast
                return 325000;
            case 30121://roc pet
                return 325000;
            case 30122://yama pet
                return 975000;
            case 30123://seren
                return 65000;
            //FOE PETS END


            case 691://foe cert
                return 10000;
            case 692://foe cert
                return 25000;
            case 693://foe cert
                return 50000;
            case 696://foe cert
                return 250000;
            case 8866://uim key
                return 100;
            case 8868://perm uim key
                return 4000;
            default:
                if (c != null && displayMessage)
                    c.sendMessage("@red@You cannot exchange @blu@" + ItemAssistant.getItemName(itemId) + " for @red@ Exchange Points.");
                return -1;
        }
    }


}
