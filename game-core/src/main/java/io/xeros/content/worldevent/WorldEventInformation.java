package io.xeros.content.worldevent;

import java.util.List;

import com.google.common.collect.Lists;
import io.xeros.Configuration;
import io.xeros.model.entity.player.Player;

public class WorldEventInformation {

    public static void openInformationInterface(Player player) {
        List<String> lines = Lists.newArrayList();
        lines.add("World events are events hosted automatically on " + Configuration.SERVER_NAME + ".");

        List<WorldEvent> events = Lists.newArrayList(WorldEventContainer.WORLD_EVENT_LIST);
        events.sort((e1, e2) -> {
            int t1 = WorldEventContainer.getInstance().getCyclesUntilEvent(e1);
            int t2 = WorldEventContainer.getInstance().getCyclesUntilEvent(e2);
            return Integer.compare(t1, t2);
        });

        events.forEach(event -> {
            lines.add(event.getEventName() + " " + event.getStartDescription() + " in "
                    + WorldEventContainer.getInstance().getTimeUntilEvent(event)
                    + " (@dre@::" + event.getTeleportCommand().getSimpleName().toLowerCase() + "@bla@)");
        });

        player.getPA().openQuestInterface("World Events", lines);
    }
}
