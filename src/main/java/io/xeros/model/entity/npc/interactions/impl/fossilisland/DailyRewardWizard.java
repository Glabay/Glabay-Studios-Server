package io.xeros.model.entity.npc.interactions.impl.fossilisland;

import io.xeros.content.dailyrewards.DailyRewardsDialogue;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.interactions.NpcOptionAction;
import io.xeros.model.entity.player.Player;

import static io.xeros.content.dailyrewards.DailyRewardsDialogue.DAILY_REWARDS_NPC;

/**
 * @author Zei | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Z
 * @social Github: <a href="https://github.com/Zeighe">Zeighe</a>
 * @since 10/1/2024
 */
public class DailyRewardWizard extends NpcOptionAction {
    @Override
    protected Integer[] getIds() {
        return new Integer[] { DAILY_REWARDS_NPC };
    }

    @Override
    public Boolean handleActionOne(Player player, NPC npc) {
        int totalReq = (player.getMode().is5x() ? 100 : 500);
        if (player.totalLevel > totalReq) {
            player.start(new DailyRewardsDialogue(player));
        }
        else {
            player.sendMessage("@red@ You need a total level of 500 to start collecting your daily reward!");
        }
        return true;
    }

    @Override
    public Boolean handleActionTwo(Player player, NPC npc) {
        int totalReq = (player.getMode().is5x() ? 100 : 500);
        if (player.totalLevel > totalReq) {
            player.getDailyRewards().openInterface();
            player.sendMessage("@red@[Reminder] @bla@Prizes are reset every month, log in everyday to reach the best prizes!");
        } else {
            player.sendMessage("@red@ You need a total level of 500 to start collecting your daily reward!");
        }
        return true;
    }
}
