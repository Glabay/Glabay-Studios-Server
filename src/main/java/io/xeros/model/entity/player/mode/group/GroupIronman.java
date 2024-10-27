package io.xeros.model.entity.player.mode.group;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.Censor;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.model.controller.DefaultController;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.punishments.PunishmentType;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Ynneh
 */
public class GroupIronman {

    /**
     * Variable to repersent the maximum capacity a group can have
     */
    private static final int MAXIMUM_MEMBERS = 5;
    public static final int GROUP_FORM_NPC = 8973;
    public static final Position GROUP_FORM_START_POSITION = new Position(3138, 3086);

    public static void sendGroupMessage(Player p, String msg) {
        Optional<GroupIronmanGroup> g = GroupIronmanRepository.getFromGroupList(p);

        if (g.isEmpty()) {
            p.sendMessage("You need to be in a Group Ironman team in order to use this feature.");
            return;
        }

        GroupIronmanGroup group = g.get();

        if (Server.getPunishments().contains(PunishmentType.MUTE, p.getLoginName()) || Server.getPunishments().contains(PunishmentType.NET_BAN, p.connectedFrom)) {
            p.sendMessage("You are muted for breaking a rule.");
            return;
        }
        if (Censor.isCensored(p, msg))
            return;
        if (msg.length() < 2)
            return;

        msg = msg.replace(":tradereq", "");
        msg = msg.replace(":gamblereq:", "");

        group.sendGroupChat(p, msg.replace("//", ""));
    }

    public static String displayStats(GroupIronmanGroup group) {
        /*
         * P.I doesn't allow use of \n ???????????????????????????
         */
        StringBuilder s = new StringBuilder("- "+group.getName()+" Statistics - \n");
        long totalXp = 0;
        int totalVotePoints = 0, totalSlayerPoints = 0, totalExchangePoints = 0, totalPkp = 0, totalBossPoints = 0;
        for (Player p : group.getOnline()) {
            if (p != null) {
                totalXp += p.getTotalXp();
                totalVotePoints += p.votePoints;
                totalSlayerPoints += p.getSlayer().getPoints();
                totalExchangePoints += p.exchangePoints;
                totalBossPoints += p.bossPoints;
                totalPkp += p.pkp;
            }
        }
        s.append("\nMembers: (").append(group.size()).append("/").append(MAXIMUM_MEMBERS).append(")");
        s.append("\nXP: ").append(totalXp);
        s.append("\nVote Points: ").append(totalVotePoints);
        s.append("\nSlayer Points: ").append(totalSlayerPoints);
        s.append("\nExchange Points: ").append(totalExchangePoints);
        s.append("\nBoss Points: ").append(totalBossPoints);
        s.append("\nPkp: ").append(totalPkp);
        return s.toString();
    }

    public static void moveToFormingLocation(Player player) {
        player.moveTo(GROUP_FORM_START_POSITION); // Tutorial island, last house
        player.setController(new GroupIronmanFormingController());

        String[] text = {
                "Welcome to the Group Ironman Forming area!",
                "Get your group ready and start your adventure!",
                "@red@You can join one group per account, if you're", "@red@going to join another, leave without forming a group."
        };

        player.start(new DialogueBuilder(player).npc(GROUP_FORM_NPC, text));
        Arrays.stream(text).forEach(player::sendMessage);
    }

    public static void formGroup(Player player) {
        GroupIronmanGroup group = GroupIronmanRepository.getGroupForOnline(player).orElse(null);
        if (group == null || group.isFinalized())
            return;

        GroupIronmanRepository.finalize(group);
        group.getOnline().forEach(plr -> moveAfterJoin(plr, group));
    }

    public static void moveAfterJoin(Player plr, GroupIronmanGroup group) {
        plr.moveTo(Configuration.START_POSITION);
        plr.setController(new DefaultController());
        String[] text;

        if (group != null) {
            text = new String[] {
                    "You're group, '" + group.getName() + "', has been formed!",
                    "Remember to use // to communicate easily with your team!",
                    "@red@The leader can use ::giminvite to invite more players.",
                    "You can join 5 total players, you have " + (5 - group.getJoined()) + " joins left."
            };
        } else {
            text = new String[] {
                "You haven't joined a group yet.",
                "You can join or create in the ironman room.",
                "@red@You can only join/form one group, choose wisely!",
            };
        }

        plr.start(new DialogueBuilder(plr).npc(GROUP_FORM_NPC, text));
        Arrays.stream(text).forEach(plr::sendMessage);
    }

}
