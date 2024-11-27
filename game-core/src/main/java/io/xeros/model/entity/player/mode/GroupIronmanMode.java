package io.xeros.model.entity.player.mode;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.group.GroupIronmanGroup;
import io.xeros.model.entity.player.mode.group.GroupIronmanRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

public class GroupIronmanMode extends IronmanMode {

    private static final LocalDate DONATION_DATE = LocalDate.of(2021, 7, 16);

    public GroupIronmanMode(ModeType type) {
        super(type);
    }

    @Override
    public boolean isTradingPermitted(Player player, Player other) {
        if (player == null || other == null)
            return false;
        GroupIronmanGroup group = GroupIronmanRepository.getGroupForOnline(player).orElse(null);
        return group != null && group.isGroupMember(other);
    }

    @Override
    public boolean isDonatingPermitted() {
        return LocalDate.now().isAfter(DONATION_DATE);
    }

}
