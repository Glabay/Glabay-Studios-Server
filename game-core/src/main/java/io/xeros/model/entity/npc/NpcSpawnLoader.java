package io.xeros.model.entity.npc;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.model.entity.npc.data.NpcMaxHit;
import io.xeros.model.entity.player.Position;

public class NpcSpawnLoader {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(NpcSpawnLoader.class.getName());
    private static final String DIRECTORY = Server.getDataDirectory() + "/cfg/npc/spawns/";

    public static void load() {
        File[] files = Objects.requireNonNull(new File(DIRECTORY).listFiles());
        int loaded = 0;
        loaded += load(new File(Server.getDataDirectory() + "/cfg/npc/npc_spawns.json"));
        for (File file : files) {
            loaded += load(file);
        }
        log.info("Spawned " + loaded + " npcs.");
    }

    private static int load(File file) {
        try (FileReader fr = new FileReader(file.getPath())) {
            List<NpcSpawn> list = new Gson().fromJson(fr, new TypeToken<List<NpcSpawn>>() {}.getType());
            list.forEach(spawn -> {
                int walkingTypeOrdinal = spawn.walkingType == null ? NpcWalkingType.WALK.ordinal() : spawn.walkingType.ordinal();
                NPCHandler.newNPC(spawn.id, spawn.position.getX(), spawn.position.getY(), spawn.position.getHeight(), walkingTypeOrdinal, NpcMaxHit.getMaxHit(spawn.id));
            });
            return list.size();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return 0;
    }


    private record NpcSpawn(int id, Position position, NpcWalkingType walkingType) {

        @Override
            public String toString() {
                return "NpcSpawn{" + "id=" + id + ", position=" + position + ", walkingType=" + walkingType + '}';
            }
        }
}
