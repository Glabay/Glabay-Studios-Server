package io.xeros.model.entity.player.mode;

import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.content.minigames.pest_control.PestControlRewards;
import io.xeros.model.definition.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class IronmanMode extends Mode {

    public IronmanMode(ModeType type) {
        super(type);
    }

    @Override
    public boolean isTradingPermitted(Player player, Player other) {
        return false;
    }

    @Override
    public boolean isStakingPermitted() {
        return false;
    }

    @Override
    public boolean isItemScavengingPermitted() {
        return false;
    }

    @Override
    public boolean isPVPCombatExperienceGained() {
        return true;
    }

    @Override
    public boolean isDonatingPermitted() {
        return true;
    }

    @Override
    public boolean isVotingPackageClaimable(String packageName) {
        return packageName.equals("Vote Ticket");
    }

    @Override
    public boolean isShopAccessible(int shopId) {
        if (shopId == FireOfExchangeBurnPrice.SHOP_ID)
            return true;
        return switch (shopId) {
            /*
             * case 78: case 77: case 48: case 44: case 40: case 26: case 22: case 20: case 16: case 14: case 12: case 2:
             */
            case 15, 77, 171, 178, 179, 172, 173, 121, 131, 10, 41, 40, 44, 78, 22, 23, 20, 16, 26, 13, 14, 17, 18, 19, 29, 79, 80, 9, 116, 117, 81,
                 120, 118, 12, 82, 119, 122, 114, 190, 147 -> true;
            default -> false;
        };
    }

    @Override
    public boolean isItemPurchasable(int shopId, int itemId) {
        switch (shopId) {
            case 15:
            case 171:
            case 172:
            case 178:
            case 179:
            case 173:
            case 26:
            case 14:
            case 121:
            case 131:
            case 40:
            case 41:
            case 17:
            case 13:
            case 29:
            case 18:
            case 19:
            case 79:
            case 80:
            case 9:
            case 23:
            case 77:
            case 115:
            case 116:
            case 117:
            case 81:
            case 118:
            case 119:
            case 3842:
            case 44:
            case 10:
            case 114:
            case 190:
            case 147:
                return true;
            case 6:
                if (ItemDef.forId(itemId).getName().contains("rune")) {
                    return true;
                }
                switch (itemId) {
                    case 1391:
                    case 4089:
                    case 4091:
                    case 4093:
                    case 4095:
                    case 4097:
                        return true;
                }
                break;

		/*case 121:
			switch (itemId) {
			case 12695:
			case 11824:
			case 10551:
			case 6746:
			case 536:
			case 6571:
			case 11889:
			case 2572:
				return true;
			}
			break;*/ //cancelled because i was just going to make it so they can buy all items

            case 82:
                switch (itemId) {
                    case 10548:
                    case 10551:
                    case 11898:
                    case 11897:
                    case 11896:
                    case 11899:
                    case 11900:
                        return true;
                }
                break;

            case 78:
                if (itemId != 6199 && itemId != 2677) {
                    return true;
                }
                break;

            case 48:
                if (itemId == 10499) {
                    return true;
                }
                break;

            case 22:
                if (itemId != 314 && itemId != 317 && itemId != 335 && itemId != 377) {
                    return true;
                }
                break;

            case 20:
                if (itemId == 1755 || itemId == 1597 || itemId == 1592 || itemId == 1595
                    || itemId == 1734 || itemId == 1733 || itemId == 1775 || itemId == 1785
                    || itemId == Items.BRACELET_MOULD) {
                    return true;
                }
                break;

            case 16:
                if (itemId == 5341 || itemId == 5343 || itemId == 5340 || itemId == 1925) {
                    return true;
                }
                break;

            case 12:
                if (itemId != 2996 && itemId != 13066 && itemId != 12751) {
                    return true;
                }
                break;

            case 2:
                int[] disallowed = {314, 1437, 1275, 1359, 1778, 221, 235, 1526, 223, 9736, 231, 225, 239, 243, 6049, 3138, 6693, 245, 6051};
                return Misc.linearSearch(disallowed, itemId) == -1;
        }
        return false;
    }

    @Override
    public boolean isItemSellable(int shopId, int itemId) {
        switch (shopId) {
            case 26:
            case 122:
            case 29:
            case 18:
            case 115:
            case 116:
                return true;
            case 44:
                if (ItemDef.forId(itemId).getName().contains("head")) {
                    return true;
                }
                break;
            case 41:
                if (itemId == 6651 || itemId == 6652) {
                    return true;
                }
                break;
        }
        return false;
    }

    @Override
    public int getModifiedShopPrice(int shopId, int itemId, int price) {
        switch (shopId) {
            // case 113:
            case 81:
                if (itemId == 2368) {
                    price = 5000000;
                }
                break;
            case 77:
                if (itemId == 2572) {
                    price = 30;
                }
                if (itemId == 6199) {
                    price = 45;
                }
                break;
            case 41:
                if (itemId == 385) {
                    price = 4500;
                }
                else if (itemId == 3026) {
                    price = 30000;
                }
                else if (itemId == 139) {
                    price = 15000;
                }
                else if (itemId == 6687) {
                    price = 22500;
                    // break;
                    // case 48:
                }
                else if (itemId == 10499 || itemId == 10498) {
                    price = 100000;
                }
                else if (itemId == 3105) {
                    price = 200000;
                }
                else if (itemId == 9470) {
                    price = 1500000;
                }
                else if (itemId == 430 || itemId == 10394 || itemId == 662 || itemId == 1833 || itemId == 1835 || itemId == 1837) {
                    price = 150000;
                }
                else if (itemId == 554 || itemId == 555 || itemId == 556 || itemId == 557 || itemId == 558 || itemId == 559) {
                    price = 200;
                }
                else if (itemId == 562) {
                    price = 450;
                }
                else if (itemId == 565) {
                    price = 2000;
                }
                else if (itemId == 560) {
                    price = 1400;
                }
                else if (itemId == 566) {
                    price = 1500;
                }
                else if (itemId == 9075) {
                    price = 1500;
                }

                break;
        }
        return price;
    }

    @Override
    public boolean isBankingPermitted() {
        return true;
    }

    @Override
    public boolean getCoinRewardsFromTournaments() {
        return false;
    }

    @Override
    public boolean isRewardSelectable(PestControlRewards.RewardButton reward) {
        return switch (reward) {
            case VOID_KNIGHT_ROBE, VOID_MAGE_HELM, VOID_MELEE_HELM, VOID_KNIGHT_TOP, VOID_KNIGHT_GLOVES, VOID_RANGE_HELM -> true;
            default -> false;
        };
    }

    @Override
    public boolean canBuyExperienceRewards() {
        return false;
    }

    @Override
    public boolean hasAccessToIronmanNpc() {
        return true;
    }

}
