package io.xeros.model.lobby;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.event.CyclicEvent;
import io.xeros.model.world.event.CyclicEventResult;

/**
 * System used to handle player lobbies
 *
 * @author Patrity & James
 * @version 1.0.1
 */
public abstract class Lobby {
    public Lobby() {
    }

    /**
     * The time left until the lobby calls <code>OnTimerFinished</code>
     */
    protected long timeLeft;
    /**
     * The CyclicEvent attached to this lobby. Runs on a seperate thread and gets called once every game tick.
     */
    private CyclicEvent timer;
    /**
     * The list of players currently in this lobby
     */
    private final List<Player> waitingPlayers = Lists.newArrayList();

    /**
     * Checks if the lobby has space and the player can join. Adds the player to <code>waitingPlayers</code> if all checks pass.
     *
     * @param player The player requesting to join the lobby
     */
    public void attemptJoin(Player player) {
        filterList();
        if (waitingPlayers.size() >= capacity()) {
            player.sendMessage(lobbyFullMessage());
            return;
        }
        if (canJoin(player)) {
            waitingPlayers.add(player);
            onJoin(player);
        }
    }

    /**
     * Checks if the player is in <code>waitingPlayers</code>. If true, the player is removed and <code>onLeave</code> is called
     *
     * @param player The player attempting to leave
     */
    public void attemptLeave(Player player) {
        if (!waitingPlayers.contains(player)) return;
        waitingPlayers.remove(player);
        onLeave(player);
    }

    /**
     * Filters the waiting players and re-adds them to the <code>waitingPlayers</code> list.
     */
    protected void filterList() {
        List<Player> filteredList = getFilteredPlayers();
        waitingPlayers.clear();
        waitingPlayers.addAll(filteredList);
    }

    /**
     * Filters null or disconnected players from the <code>waitingPlayers</code> list.
     *
     * @return The filtered <code>waitingPlayers</code> list.
     */
    protected List<Player> getFilteredPlayers() {
        List<Player> filtered = Lists.newArrayList(waitingPlayers);
        return filtered.stream().filter(Objects::nonNull).filter(player -> !player.isDisconnected()).collect(Collectors.toList());
    }

    /**
     * Sends the <code>onTimerUpdate</code> method to all players in the <code>waitingPlayers</code> list.
     */
    protected void update() {
        //getFilteredPlayers().stream().filter(player -> !Boundary.isIn(player, this.getBounds())).forEach(player -> waitingPlayers.remove(player));
        getFilteredPlayers().stream().forEach(this::onTimerUpdate);
    }

    /**
     * Handles events for a player successfully joining the lobby
     *
     * @param player The player joining
     */
    public abstract void onJoin(Player player);

    /**
     * Handles events for a player successfully leaving the lobby
     *
     * @param player The player leaving
     */
    public abstract void onLeave(Player player);

    /**
     * Checks whether the supplied player has requirements to join this lobby
     *
     * @param player The player to check
     * @return <value>true</value> if the player passes all requirements, otherwise <value>false</value>
     */
    public abstract boolean canJoin(Player player);

    /**
     * The event that is called when the lobby timer ends
     *
     * @param lobbyPlayers The players that were in the lobby when the timer finished
     */
    public abstract void onTimerFinished(List<Player> lobbyPlayers);

    /**
     * Called each time the timer cycles, used for updating interfaces or sending messages
     *
     * @param player The player to update
     */
    public abstract void onTimerUpdate(Player player);

    /**
     * Used at the start of the timer event to initilize the countdown
     *
     * @return The time in milliseconds between each lobby being sent out
     */
    public abstract long waitTime();

    /**
     * Checked when the player attemps to join the lobby
     *
     * @return The maximum number of players that this lobby supports
     */
    public abstract int capacity();

    /**
     * Sent to the player via a game message
     *
     * @return A string to display when the lobby is full
     */
    public abstract String lobbyFullMessage();

    /**
     * Checks whether the timer event should reset the timer
     *
     * @return <value>true</value> if the timer should reset back to <code>waitTime()</code>
     */
    public abstract boolean shouldResetTimer();

    /**
     * Used to check if the player is within the bounds of the lobby
     *
     * @return The boundary of the lobby area
     */
    public abstract Boundary getBounds();

    /**
     * Checks whether the specified player is in the lobby area specified in <code>getBounds()</code>
     *
     * @param player The player to check
     * @return <value>true</value> if the player is within the bounds
     */
    public boolean inLobby(Player player) {
        return Boundary.isIn(player, getBounds());
    }

    /**
     * Gets a formatted String for minutes and seconds left on the timer.
     *
     * @return timeLeft in seconds if minutes < 1, otherwise timeLeft in minutes and seconds
     */
    public String formattedTimeLeft() {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft);
        if (minutes < 1) {
            return String.format("%d sec", TimeUnit.MILLISECONDS.toSeconds(timeLeft));
        } else {
            return String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(timeLeft), TimeUnit.MILLISECONDS.toSeconds(timeLeft) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeft)));
        }
    }

    /**
     * Begins the timer for this lobby. The timer will count down from <code>waitTime()</code> in 600ms increments,
     * unless the <code>shouldResetTimer()</code> returns true, then it will reset the timer back to the
     * initial <code>waitTime()</code> and continue to loop.
     * <p>
     * Once the timer has reached 0 or less, the <code>onTimerFinished()</code> method will be called
     * with the list of filtered waitingPlayers, the <code>waitingPlayers</code> list will be cleared
     * and the timer will start again.
     */
    public void startTimer() {
        if (timer != null) timer.destroy();
        timer = new CyclicEvent();
        timer.onStart(() -> {
            timeLeft = waitTime();
        }).onCycle(() -> {
            filterList();
            if (shouldResetTimer()) timeLeft = waitTime();
            timeLeft -= 600;
            update();
            if (timeLeft <= 0) return CyclicEventResult.END;
            return CyclicEventResult.CONTINUE;
        }).onFinish(() -> {
            List<Player> lobbyPlayers = getFilteredPlayers();
            waitingPlayers.clear();
            if (lobbyPlayers.size() > 0) onTimerFinished(lobbyPlayers);
            startTimer();
        }).begin();
    }

    /**
     * The list of players currently in this lobby
     */

    public List<Player> getWaitingPlayers() {
        return this.waitingPlayers;
    }
}
