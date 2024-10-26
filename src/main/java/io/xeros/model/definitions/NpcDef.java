package io.xeros.model.definitions;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

public class NpcDef {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NpcDef.class);
    @Getter
    private static final Int2ObjectMap<NpcDef> definitions = new Int2ObjectOpenHashMap<>();
    private static final NpcDef DEFAULT = builder().build();

    public static void load() throws IOException {
        try (FileReader fr = new FileReader(Server.getDataDirectory() + "/cfg/npc/npc_definitions.json")) {
            Map<Integer, NpcDef> map = new Gson().fromJson(fr, new TypeToken<Int2ObjectMap<NpcDef>>() {
            }.getType());
            definitions.putAll(map);
        }
        log.info("Loaded {} npc definitions.", definitions.size());
    }

    public static NpcDef forId(int npcId) {
        return definitions.getOrDefault(npcId, DEFAULT);
    }

    private final String name;
    @Getter
    private final int combatLevel;
    @Getter
    private final int size;
    @Getter
    private final boolean runnable;

    public String getName() {
        if (name != null) {
            if (name.contains("@red@")) {
                return name.replace("@red@", "");
            }
            return name;
        }
        return "unknown";
    }

    NpcDef(final String name, final int combatLevel, final int size, final boolean runnable) {
        this.name = name;
        this.combatLevel = combatLevel;
        this.size = size;
        this.runnable = runnable;
    }

    public static class NpcDefBuilder {

        private String name;

        private int combatLevel;

        private int size;

        private boolean runnable;

        NpcDefBuilder() {
        }

        public NpcDef.NpcDefBuilder name(final String name) {
            this.name = name;
            return this;
        }

        public NpcDef.NpcDefBuilder combatLevel(final int combatLevel) {
            this.combatLevel = combatLevel;
            return this;
        }

        public NpcDef.NpcDefBuilder size(final int size) {
            this.size = size;
            return this;
        }

        public NpcDef.NpcDefBuilder runnable(final boolean runnable) {
            this.runnable = runnable;
            return this;
        }

        public NpcDef build() {
            return new NpcDef(this.name, this.combatLevel, this.size, this.runnable);
        }

        @Override
    public String toString() {
            return "NpcDef.NpcDefBuilder(name=" + this.name + ", combatLevel=" + this.combatLevel + ", size=" + this.size + ", runnable=" + this.runnable + ")";
        }
    }

    public static NpcDef.NpcDefBuilder builder() {
        return new NpcDef.NpcDefBuilder();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof NpcDef other)) return false;
        if (!other.canEqual(this)) return false;
        if (this.getCombatLevel() != other.getCombatLevel()) return false;
        if (this.getSize() != other.getSize()) return false;
        if (this.isRunnable() != other.isRunnable()) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        return Objects.equals(this$name, other$name);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof NpcDef;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getCombatLevel();
        result = result * PRIME + this.getSize();
        result = result * PRIME + (this.isRunnable() ? 79 : 97);
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "NpcDef(name=" + this.getName() + ", combatLevel=" + this.getCombatLevel() + ", size=" + this.getSize() + ", runnable=" + this.isRunnable() + ")";
    }
}
