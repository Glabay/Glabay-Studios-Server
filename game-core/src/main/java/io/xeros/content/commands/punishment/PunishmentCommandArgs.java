package io.xeros.content.commands.punishment;

import com.google.common.base.Preconditions;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.util.dateandtime.TimeSpan;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

import static io.xeros.content.commands.punishment.PunishmentCommand.SEPARATOR;

public class PunishmentCommandArgs {

    private final String[] splitArgs;

    public PunishmentCommandArgs(String args) {
        Preconditions.checkArgument(args != null && args.length() > 0, "not enough arguments");
        this.splitArgs = args.split(SEPARATOR);
        Preconditions.checkArgument(splitArgs.length > 0, "not enough " + SEPARATOR + " arguments");
    }

    public String index(int index) {
        Preconditions.checkArgument(index < splitArgs.length, "not enough arguments");
        return splitArgs[index];
    }

    public Player getPlayerForDisplayName() {
        String displayName = index(0).toLowerCase();
        Player other = PlayerHandler.getPlayerByDisplayName(displayName);
        Preconditions.checkState(other != null, "no online player with display name '" + displayName + "'");
        Preconditions.checkArgument(other.getRights().isNot(Right.OWNER), "can't punish owner");
        return other;
    }

    /**
     * Gets the duration (2nd argument) (zero means forever).
     */
    public TimeSpan getDuration() {
        Preconditions.checkState(splitArgs.length >= 2, "no length specified");
        String durationText = index(1);

        if (StringUtils.isNumeric(durationText)) {
            long duration = Long.parseLong(durationText);
            if (duration == 0)
                duration = TimeUnit.DAYS.toMinutes(365 * 5);
            return new TimeSpan(TimeUnit.MINUTES, duration);
        } else {
            try {
                String[] args = durationText.split(" ");
                long duration = Long.parseLong(args[0]);
                TimeUnit unit = TimeUnit.valueOf(args[1].toUpperCase());
                return new TimeSpan(unit, duration);
            } catch (Exception e) {
                throw new IllegalArgumentException("invalid duration, try '5 seconds/minutes/hours/days'");
            }
        }
    }
}
