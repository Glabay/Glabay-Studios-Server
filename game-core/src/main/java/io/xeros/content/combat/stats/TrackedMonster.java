package io.xeros.content.combat.stats;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.util.JsonUtil;

public class TrackedMonster {
    private static final TypeToken<List<TrackedMonster>> TOKEN = new TypeToken<>() {
    };
    private static List<TrackedMonster> trackedMonsterList;

    public static void init() throws IOException {
        trackedMonsterList = JsonUtil.fromJson(Server.getDataDirectory() + "/cfg/tracked_monsters.json", TOKEN);
        trackedMonsterList.sort((t1, t2) -> {
            if (t1.isTrackKillTime() && !t2.isTrackKillTime()) return -1;
            if (!t1.isTrackKillTime() && t2.isTrackKillTime()) return 1;
            return t1.getName().compareTo(t2.getName());
        });
        trackedMonsterList = Collections.unmodifiableList(trackedMonsterList);
    }

    public static List<TrackedMonster> getTrackedMonsterList() {
        return trackedMonsterList;
    }

    private final String name;
    private final boolean trackKillTime;

    public TrackedMonster(final String name, final boolean trackKillTime) {
        this.name = name;
        this.trackKillTime = trackKillTime;
    }

    public String getName() {
        return this.name;
    }

    public boolean isTrackKillTime() {
        return this.trackKillTime;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof TrackedMonster)) return false;
        final TrackedMonster other = (TrackedMonster) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.isTrackKillTime() != other.isTrackKillTime()) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TrackedMonster;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isTrackKillTime() ? 79 : 97);
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "TrackedMonster(name=" + this.getName() + ", trackKillTime=" + this.isTrackKillTime() + ")";
    }
}
