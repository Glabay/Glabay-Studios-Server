package io.xeros.content.polls;

import java.util.List;

import io.xeros.model.entity.player.Right;

/**
 * @author Grant_ | www.rune-server.ee/members/grant_ | 2/10/20
 */
public class Poll {
    private final String question;
    private final int hours;
    private int totalVotes;
    private final List<String> answers;
    private List<Integer> votes;
    private List<String> voters;
    private long startTime;
    private final Right right;

    public Poll(String question, int hours, int totalVotes, List<String> answers, List<Integer> votes, List<String> voters, long startTime, Right right) {
        this.question = question;
        this.hours = hours;
        this.answers = answers;
        this.totalVotes = totalVotes;
        this.votes = votes;
        this.voters = voters;
        this.startTime = startTime;
        this.right = right;
    }

    public String getQuestion() {
        return question;
    }

    public int getHours() {
        return hours;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public List<Integer> getVotes() {
        return votes;
    }

    public void setVotes(List<Integer> votes) {
        this.votes = votes;
    }

    public List<String> getVoters() {
        return voters;
    }

    public void setVoters(List<String> voters) {
        this.voters = voters;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Right getRight() {
        return right;
    }
}
