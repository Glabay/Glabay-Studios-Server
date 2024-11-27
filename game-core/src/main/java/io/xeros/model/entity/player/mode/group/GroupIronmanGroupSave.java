package io.xeros.model.entity.player.mode.group;

import com.google.common.collect.Lists;
import io.xeros.model.entity.player.mode.group.log.GimDropItemLog;
import io.xeros.model.entity.player.mode.group.log.GimWithdrawItemLog;
import io.xeros.model.items.GameItem;
import lombok.Getter;

import java.util.*;

public class GroupIronmanGroupSave {

    public static GroupIronmanGroupSave toSave(GroupIronmanGroup group) {
        return new GroupIronmanGroupSave(group.getName(),
                group.getMembers(),
                group.isFinalized(),
                group.getBank().getInventory().getItems(),
                group.getJoined(),
                group.getMergedCollectionLogs(),
                group.getWithdrawItemLog(),
                group.getDropItemLog()
        );
    }

    @Getter
    private final String name;
    private final List<String> members;
    private final boolean finalized;
    private final GameItem[] bank;
    private final int joins;
    private final List<String> mergedCollectionLogs;
    private final Deque<GimWithdrawItemLog> withdrawItemLog;
    private final Deque<GimDropItemLog> dropItemLog;

    // For Jackson
    private GroupIronmanGroupSave() {
        this(null, null, false, new GameItem[GroupIronmanBank.BANK_SIZE], 0, new ArrayList<>(), new ArrayDeque<>(), new ArrayDeque<>());
    }

    public GroupIronmanGroupSave(String name, List<String> members, boolean finalized, GameItem[] bank, int joins,
                                 List<String> mergedCollectionLogs, Deque<GimWithdrawItemLog> withdrawItemLog, Deque<GimDropItemLog> dropItemLog) {
        this.name = name;
        this.members = members;
        this.finalized = finalized;
        this.bank = bank;
        this.joins = joins;
        this.mergedCollectionLogs = mergedCollectionLogs;
        this.withdrawItemLog = withdrawItemLog;
        this.dropItemLog = dropItemLog;
    }

    public GroupIronmanGroup toGroup() {
        GroupIronmanGroup group = new GroupIronmanGroup(name, members);
        group.setFinalized(finalized);
        group.getBank().getInventory().set(bank);
        group.setJoined(joins);
        group.getMergedCollectionLogs().addAll(mergedCollectionLogs);
        group.getWithdrawItemLog().addAll(withdrawItemLog);
        group.getDropItemLog().addAll(dropItemLog);
        return group;
    }

}
