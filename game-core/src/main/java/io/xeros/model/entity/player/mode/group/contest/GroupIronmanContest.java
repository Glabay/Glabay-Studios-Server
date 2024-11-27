package io.xeros.model.entity.player.mode.group.contest;

import io.xeros.Server;
import io.xeros.content.collection_log.CollectionLog;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.group.GroupIronmanGroup;
import io.xeros.sql.gim.AddGimContestEntryQuery;
import io.xeros.sql.gim.GetGimTopThreeTeamsQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GroupIronmanContest {

    public static void openInterface(Player c) {
        if (c.hitDatabaseRateLimit(true))
            return;

        Server.getDatabaseManager().exec(((context, connection) -> {
            Map<ContestType, List<GimContestEntry>> entries = new GetGimTopThreeTeamsQuery().execute(context, connection);
            c.addQueuedAction(plr -> {
                plr.getPA().sendFrame126("GIM Leaderboards", 39902);
                plr.getPA().sendFrame126("Collect Log Entries", 39905);
                plr.getPA().sendFrame126("Pets", 39909);
                plr.getPA().sendFrame126("Earned Exchange Points", 39913);
                plr.getPA().sendFrame126("ToB Completions", 39917);

                Arrays.stream(ContestType.values()).forEach(type -> {
                    List<GimContestEntry> list = entries.get(type);
                    for (int i = 0; i < 3; i++) {
                        int string = type.getListStartInterfaceId() + i;
                        if (list.size() > i) {
                            GimContestEntry entry = list.get(i);
                            plr.getPA().sendString(string, entry.rank() + ". " + entry.groupName() + " (" + entry.value() + ")");
                        } else {
                            plr.getPA().sendString(string, (i + 1) + ". N/A");
                        }
                    }
                });

                c.getPA().showInterface(39900);
            });
            return null;
        }));
    }

    public static void insertContestEntry(Player c, GroupIronmanGroup group) {
        int pets = group.getCollectionLog().getUnlocked(CollectionLog.PETS_ID).size();
        int collectionLogEntries = group.getCollectionLog().getUniquesUnlocked();
        Server.getDatabaseManager().exec(new AddGimContestEntryQuery(group, c.getLoginName(),
                collectionLogEntries, pets, c.totalEarnedExchangePoints, c.tobCompletions));
    }

}
