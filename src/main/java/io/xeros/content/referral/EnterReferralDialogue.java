package io.xeros.content.referral;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.Npcs;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnterReferralDialogue extends DialogueBuilder {

    private static final Logger logger = LoggerFactory.getLogger(EnterReferralDialogue.class);
    private static final List<GameItem> STANDARD_REWARD = Lists.newArrayList(new GameItem(2528, 1), new GameItem(12789, 1), new GameItem(995, 1_500_000), new GameItem(11937, 100));
    private static final String PHRASE = "How did you hear about us?";

    public EnterReferralDialogue(Player player) {
        super(player);
        int totalReq = (player.getMode().is5x() ? 100 : 500);
        setNpcId(Npcs.PILES);
        npc(PHRASE);
        if (!Server.isDebug() && (!ReferralRegister.canGetReward(player) || player.usedReferral)) {
            npc("You've already used your one referral!");
        } else if (player.totalLevel < totalReq) {
            npc(13, "You need a total level of " + totalReq + " to claim a referral.");
        } else {
            statement("All referrals give the same reward, please let us \\nknow where you came from to help the server!");
            option("Choose Referral", new DialogueOption("Enter referral code", this::enteredReferral),
                    new DialogueOption(ReferralSource.DISCORD.toString(), p -> standardReferral(p, ReferralSource.DISCORD)),
                    new DialogueOption(ReferralSource.RUNE_LOCUS.toString(), p -> standardReferral(p, ReferralSource.RUNE_LOCUS)),
                    new DialogueOption("Next page", this::nextPage));
        }
    }

    private void nextPage(Player player) {
        player.start(new DialogueBuilder(player).option("Choose Referral",
                new DialogueOption(ReferralSource.RSPS_LIST.toString(), p -> standardReferral(p, ReferralSource.RSPS_LIST)),
                new DialogueOption(ReferralSource.TOP_G.toString(), p -> standardReferral(p, ReferralSource.TOP_G)),
                new DialogueOption(ReferralSource.RUNE_SERVER.toString(), p -> standardReferral(p, ReferralSource.RUNE_SERVER)),
                new DialogueOption("Go back", p -> player.start(new EnterReferralDialogue(p)))));
    }

    private static void register(Player player, ReferralSource source, String qualifier, List<GameItem> rewards, String message) {
        int totalReq = (player.getMode().is5x() ? 100 : 500);
        logger.debug("Skipping already claimed on network check and Player#usedReferral.");
        if (!Server.isDebug() && (!ReferralRegister.canGetReward(player) || player.usedReferral)) {
            player.start(new DialogueBuilder(player).statement("You've already used your one referral!"));
        } else if (player.totalLevel < totalReq) {
            player.start(new DialogueBuilder(player).npc(13, "You need a total level of " + totalReq + " to claim a referral."));
        } else {
            player.start(new DialogueBuilder(player).itemStatement(rewards.get(0).id(), message, "Thanks for trying out the server, we hope you stay!"));
            rewards.forEach(reward -> player.getItems().addItemUnderAnyCircumstance(reward.id(), reward.amount()));
            ReferralRegister.register(player, source, qualifier);
            player.usedReferral = true;
        }
    }

    private void enteredReferral(Player player) {
        player.getPA().sendEnterString("Enter the referral code", ((player1, string) -> {
            Optional<ReferralCode> referralCodeOptional = ReferralCode.getReferralCodes().stream().filter(ref -> ref.getCode().equalsIgnoreCase(string)).findFirst();
            referralCodeOptional.ifPresentOrElse(referralCode -> {
                register(player, ReferralSource.YOUTUBE, string, referralCode.getRewards(), "Redeemed '" + string + "' referral!");
            },() -> player.start(new DialogueBuilder(player).statement("No code found!").exit(this::enteredReferral)));
        }));
    }

    private void standardReferral(Player player, ReferralSource source) {
        register(player, source, null, STANDARD_REWARD,"You receive 1.5m!");
    }

}
