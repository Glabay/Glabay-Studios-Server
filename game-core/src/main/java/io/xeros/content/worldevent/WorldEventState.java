package io.xeros.content.worldevent;

import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.util.JsonUtil;
import org.apache.commons.io.FileUtils;

public class WorldEventState {

    private static String getSaveFile() {
        return Server.getSaveDirectory() + "world_event_state.json";
    }

    public static WorldEventState load() throws IOException {
        File file = new File(getSaveFile());
        if (file.exists()) {
            return new Gson().fromJson(FileUtils.readFileToString(new File(getSaveFile())), new TypeToken<WorldEventState>() {}.getType());
        }

        return new WorldEventState(-1, WorldEventContainer.getInstance().getCyclesBetweenEvents());
    }

    private int worldEventIndex;
    private int ticksUntilNextEvent;

    public WorldEventState(int worldEventIndex, int ticksUntilNextEvent) {
        this.worldEventIndex = worldEventIndex;
        this.ticksUntilNextEvent = ticksUntilNextEvent;
    }

    private void serialize() {
        Server.getIoExecutorService().submit(() -> {
            JsonUtil.toJson(this, getSaveFile());
        });
    }

    public int getWorldEventIndex() {
        return worldEventIndex;
    }

    public void setWorldEventIndex(int worldEventIndex) {
        this.worldEventIndex = worldEventIndex;
        serialize();
    }

    public int getTicksUntilNextEvent() {
        return ticksUntilNextEvent;
    }

    public void setTicksUntilNextEvent(int ticksUntilNextEvent) {
        this.ticksUntilNextEvent = ticksUntilNextEvent;
        serialize();
    }
}
