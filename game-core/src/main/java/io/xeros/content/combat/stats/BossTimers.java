package io.xeros.content.combat.stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.xeros.Server;
import io.xeros.content.bosses.zulrah.Zulrah;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;
import org.apache.commons.lang3.tuple.Pair;

public class BossTimers {

    private final Player player;
    private final Map<String, FightDuration> personalBests = new HashMap<>();
    private final Map<String, FightDuration> currentFights = new HashMap<>();

    public BossTimers(Player player) {
        this.player = player;
    }

    public void track(NPC npc) {
       track(name(npc));
    }

    public void track(String name) {
        if (!currentFights.containsKey(name)) {
            if (TrackedMonster.getTrackedMonsterList().stream().anyMatch(tracked -> tracked.isTrackKillTime() && tracked.getName().equalsIgnoreCase(name))) {
                currentFights.put(name, new FightDuration(name, Server.getTickCount()));
            }
        }
    }

    public void death(NPC npc) {
        death(name(npc));
    }

    public void death(String name) {
        if (currentFights.containsKey(name)) {
            submit(new FightDuration(name, Server.getTickCount() - currentFights.get(name).getTicks()));
            currentFights.remove(name);
        }
    }

    private void submit(FightDuration fightDuration) {
        if (!personalBests.containsKey(fightDuration.getName()) || personalBests.get(fightDuration.getName()).getTicks() > fightDuration.getTicks()) {
            personalBests.put(fightDuration.getName(), fightDuration);
            message(fightDuration, true);
        } else {
            message(fightDuration, false);
        }
    }

    private String name(NPC npc) {
        return npc.getDefinition().getName().toLowerCase();
    }

    public void remove(String name) {
        currentFights.remove(name);
    }

    private void message(FightDuration fightDuration, boolean personalBest) {
        player.sendMessage("Fight duration: <col=E9362B>" + Misc.cyclesToDottedTime(fightDuration.getTicks())
                + "<col=0>" + (personalBest ? " (new personal best)" : ""));
    }

    public String getPersonalBest(String npcName) {
        return Misc.cyclesToDottedTime(personalBests.getOrDefault(npcName, new FightDuration("", 0)).getTicks());
    }

    public String encode() {
        StringBuilder builder = new StringBuilder();
        for (FightDuration duration : personalBests.values()) {
            builder.append(duration.getName()).append(":").append(duration.getTicks()).append(":");
        }
        return builder.toString();
    }

    public void mapAll(List<FightDuration> fightDurationList) {
        fightDurationList.forEach(fightDuration -> personalBests.put(fightDuration.getName(), fightDuration));
    }
}
