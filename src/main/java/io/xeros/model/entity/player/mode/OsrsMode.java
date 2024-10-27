package io.xeros.model.entity.player.mode;

import io.xeros.content.minigames.pest_control.PestControlRewards;
import io.xeros.model.entity.player.Player;

public class OsrsMode extends Mode {

    /**
     * Creates a new default mode
     *
     * @param type the default mode
     */
    public OsrsMode(ModeType type) {
        super(type);
    }

    @Override
    public double getDropModifier() {
        return -0.1;
    }

    @Override
    public boolean isTradingPermitted(Player player, Player other) {
        return true;
    }

    @Override
    public boolean isStakingPermitted() {
        return true;
    }

    @Override
    public boolean isItemScavengingPermitted() {
        return true;
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
        return true;
    }

    @Override
    public boolean isShopAccessible(int shopId) {
        return shopId != 41;
    }

    @Override
    public boolean isItemPurchasable(int shopId, int itemId) {
        if (shopId == 171) {
            return itemId != 8866 && itemId != 8868;
        }
        return true;
    }

    @Override
    public int getModifiedShopPrice(int shopId, int itemId, int price) {
        switch (shopId) {
            case 81:
                if (itemId == 2368) {
                    price = 5000000;
                }
                break;
            case 113:
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
                break;
        }
        return price;
    }

    @Override
    public boolean isItemSellable(int shopId, int itemId) {
        if (shopId == 113) {
            return itemId != 385 && itemId != 139 && itemId != 3026 && itemId != 6687;
        }
        return true;
    }

    @Override
    public boolean isBankingPermitted() {
        return true;
    }

    @Override
    public boolean getCoinRewardsFromTournaments() {
        return true;
    }

    @Override
    public boolean canBuyExperienceRewards() {
        return false;
    }

    @Override
    public boolean hasAccessToIronmanNpc() {
        return true;
    }

    @Override
    public boolean isRewardSelectable(PestControlRewards.RewardButton reward) {
        return true;
    }

    @Override
    public int getTotalLevelNeededForRaids() {
        return 750;
    }

    @Override
    public int getTotalLevelForTob() {
        return 1000;
    }
}
