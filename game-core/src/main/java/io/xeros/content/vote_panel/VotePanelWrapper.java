package io.xeros.content.vote_panel;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * @author Grant_ | www.rune-server.ee/members/grant_ | 3/24/20
 */
public class VotePanelWrapper {

    //Map of all the users that have voted, ever.
    private final Map<String, VoteUser> votes;
    private Instant startTime;
    private long finishTime; //Pre-determined finish time from the start of the event
    private final List<String> lastWeeksTopVoters;

    public VotePanelWrapper(long finishTime, Map<String, VoteUser> votes, List<String> lastWeeksTopVoters) {
        this.finishTime = finishTime;
        this.votes = votes;
        this.lastWeeksTopVoters = lastWeeksTopVoters;
    }

    public Map<String, VoteUser> getVotes() {
        return votes;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public List<String> getLastWeeksTopVoters() {
        return lastWeeksTopVoters;
    }
}
