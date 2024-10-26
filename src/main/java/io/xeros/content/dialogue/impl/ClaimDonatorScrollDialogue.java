package io.xeros.content.dialogue.impl;

import java.util.Arrays;
import java.util.Optional;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.Npcs;
import io.xeros.model.entity.player.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ClaimDonatorScrollDialogue extends DialogueBuilder {

    public static boolean clickScroll(Player player, int itemId) {
        var scrollOptional = Arrays.stream(DonationScroll.values())
            .filter(scroll -> scroll.itemId == itemId)
            .findFirst();

        if (scrollOptional.isPresent()) {
            player.start(new ClaimDonatorScrollDialogue(player, scrollOptional.get()));
            return true;
        }
        else return false;
    }

    private static final int NPC_ID = Npcs.ELDERLY_ELF;
    private final DonationScroll scroll;

    public ClaimDonatorScrollDialogue(Player player, DonationScroll scroll) {
        super(player);
        this.scroll = scroll;
        setNpcId(NPC_ID);
        npc("Are you sure you want to claim this scroll?", "You will claim $%d in credits.".formatted(scroll.donationAmount));
        option(new DialogueOption("Yes, claim $%d scroll.".formatted(scroll.donationAmount), p -> claim()),
                new DialogueOption("Nevermind", p -> p.getPA().closeAllWindows()));
    }

    private void claim() {
        if (!getPlayer().getItems().playerHasItem(scroll.itemId))
            return;
        getPlayer().getItems().deleteItem(scroll.itemId, 1);
        getPlayer().gfx100(263);
        getPlayer().donatorPoints += scroll.donationAmount;
        getPlayer().amDonated += scroll.donationAmount;
        getPlayer().start(new DialogueBuilder(getPlayer()).setNpcId(NPC_ID).npc("Thank you for donating! %d$ has been added to your total credit.".formatted(scroll.donationAmount)));
        getPlayer().updateRank();
        getPlayer().getPA().closeAllWindows();

         if (getPlayer().amDonated >= 50 && getPlayer().amDonated <= 99)
             getPlayer().sendMessage("@blu@Your next donator rank is @red@Extreme Donator");
         else if (getPlayer().amDonated >= 100 && getPlayer().amDonated <= 249)
             getPlayer().sendMessage("@blu@Your next donator rank is @red@Legendary Donator");
         else if (getPlayer().amDonated >= 250 && getPlayer().amDonated <= 499)
             getPlayer().sendMessage("@blu@Your next donator rank is @red@Diamond Club");
         else if (getPlayer().amDonated >= 500 && getPlayer().amDonated <= 999)
             getPlayer().sendMessage("@blu@Your next donator rank is @red@Oynx Club");
    }
    
    @Getter
    @RequiredArgsConstructor
    public enum DonationScroll {
        FIVE(6769, 5),
        TEN(2403, 10),
        TWENTY_FIVE(2396, 25),
        FIFTY(786, 50),
        ONE_HUNDRED(761, 100),
        TWO_FIFTY(607, 250),
        FIVE_HUNDRED(608, 500)
        ;
        private final int itemId;
        private final int donationAmount;
    }
}
