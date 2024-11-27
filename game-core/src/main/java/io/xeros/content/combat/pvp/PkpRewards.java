package io.xeros.content.combat.pvp;

import io.xeros.Configuration;
import io.xeros.content.minigames.bounty_hunter.TargetState;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.util.discord.Discord;

public class PkpRewards {

    public static void award(Player dying, Player killer) {
        if (dying == killer)
            return;

        if (!Boundary.isIn(killer, Boundary.OUTLAST_AREA)) {
            Discord.writeServerSyncMessage("[Kill]" + killer.getDisplayName() + " killed " + dying.getDisplayName() + " at" + killer.absX + ", " + killer.absY);
        }

        boolean canReceiveRewards = WildAntiFarm.canReceiveRewards(killer, dying);
        boolean didReceiveRewards = false;

        dying.deathcount++;
        killer.killcount++;

        killer.getPA().sendFrame126("@or1@Hunter KS: @gre@"
                + killer.getKillstreak().getAmount(Killstreak.Type.HUNTER) + "@or1@, "
                + "Rogue KS: @gre@"
                + killer.getKillstreak().getAmount(Killstreak.Type.ROGUE), 29165);

        /*
         * Killing targets
         */
        if (Configuration.BOUNTY_HUNTER_ACTIVE) {
            if (canReceiveRewards) {
                dying.getBH().dropPlayerEmblem(killer);
                didReceiveRewards = true;
            }

            if (dying.getBH().isTarget(killer) && killer.getBH().isTarget(dying)) {
//            if (dying.getBH().hasTarget()
//                    && dying.getBH().getTarget().getName().equalsIgnoreCase(killer.playerName)
//                    && killer.getBH().hasTarget() && killer.getBH().getTarget().getName().equalsIgnoreCase(dying.playerName)) {
                killer.getBH().setCurrentHunterKills(killer.getBH().getCurrentHunterKills() + 1);
                if (killer.getBH().getCurrentHunterKills() > killer.getBH().getRecordHunterKills()) {
                    killer.getBH().setRecordHunterKills(killer.getBH().getCurrentHunterKills());
                }

                if (canReceiveRewards) {
                    killer.getKillstreak().increase(Killstreak.Type.HUNTER);
                    killer.getBH().upgradePlayerEmblem();
                    didReceiveRewards = true;
                }

                killer.getBH().setTotalHunterKills(killer.getBH().getTotalHunterKills() + 1);
                killer.getBH().removeTarget();
                dying.getBH().removeTarget();
                killer.getBH().setTargetState(TargetState.RECENT_TARGET_KILL);
                killer.sendMessage("<col=255>You have killed your target: " + dying.getDisplayName() + ".");
            } else {
                if (canReceiveRewards) {
                    killer.getKillstreak().increase(Killstreak.Type.ROGUE);
                }
                killer.getBH().setCurrentRogueKills(killer.getBH().getCurrentRogueKills() + 1);
                killer.getBH().setTotalRogueKills(killer.getBH().getTotalRogueKills() + 1);
                if (killer.getBH().getCurrentRogueKills() > killer.getBH().getRecordRogueKills()) {
                    killer.getBH().setRecordRogueKills(killer.getBH().getCurrentRogueKills());
                }
            }
            killer.getBH().updateStatisticsUI();
            killer.getBH().updateTargetUI();
        }

        int personDieingKillstreak = dying.getKillstreak().getAmount(Killstreak.Type.ROGUE);

        if (Boundary.isIn(dying, Boundary.WILDERNESS_PARAMETERS)) {
            if (personDieingKillstreak > 10) {
                killer.getItems().addItemUnderAnyCircumstance(2996, 30);
                killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ 10 pkp @bla@for ending a kill streak.");

            }

            if (canReceiveRewards) {
                didReceiveRewards = true;
                int random = 30;
                killer.pkp += random;
                    if (dying.amDonated >= 50 && dying.amDonated <= 99) { //regular donator
                        int bonuspkp = 3;
                        killer.pkp += bonuspkp;
                        killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ " + bonuspkp + " pkp @bla@for your donator rank.");

                    } else if (killer.amDonated >= 100 && killer.amDonated <= 249) { //extreme donator
                        int bonuspkp = 5;
                        killer.pkp += bonuspkp;
                        killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ " + bonuspkp + " pkp @bla@for your donator rank.");

                    } else if (killer.amDonated >= 250 && killer.amDonated <= 499) { //legendary donator
                        int bonuspkp = 8;
                        killer.pkp += bonuspkp;
                        killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ " + bonuspkp + " pkp @bla@for your donator rank.");

                    } else if (killer.amDonated >= 500 && killer.amDonated <= 999) { //diamond club
                        int bonuspkp = 10;
                        killer.pkp += bonuspkp;
                        killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ " + bonuspkp + " pkp @bla@for your donator rank.");

                    } else if (killer.amDonated >= 1000) { //onyx club
                        int bonuspkp = 12;
                        killer.pkp += bonuspkp;
                        killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ " + bonuspkp + " pkp @bla@for your donator rank.");
                    }
            }

            dying.getKillstreak().resetAll();
        }

        if (didReceiveRewards) {
            WildAntiFarm.addReceivedRewards(killer, dying);
        }

        if (!canReceiveRewards) {
            killer.sendMessage("You do not get any rewards as you have recently defeated @blu@"
                    + dying.getDisplayName() + "@bla@.");
        }

        killer.getQuestTab().updateInformationTab();
        dying.getQuestTab().updateInformationTab();
    }


}
