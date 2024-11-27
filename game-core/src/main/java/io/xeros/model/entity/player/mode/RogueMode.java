package io.xeros.model.entity.player.mode;

import io.xeros.content.minigames.pest_control.PestControlRewards;
import io.xeros.model.entity.player.Player;

public class RogueMode extends Mode {

    /**
     * Creates a new mode for a specific type
     *
     * @param type the type of mode
     */
    public RogueMode(ModeType type) {
        super(type);
    }

    @Override
    public double getDropModifier() {
        return -0.05;
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
        return true;
    }

    @Override
    public boolean isItemPurchasable(int shopId, int itemId) {
        if (shopId == 171) {
            return itemId != 8866 && itemId != 8868;
        }
        return true;
    }

    @Override
    public boolean isItemSellable(int shopId, int itemId) {
        return true;
    }

    @Override
    public boolean isRewardSelectable(PestControlRewards.RewardButton reward) {
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
    public int getTotalLevelNeededForRaids() {
        return 750;
    }

    @Override
    public int getTotalLevelForTob() {
        return 1000;
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
