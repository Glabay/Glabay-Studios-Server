package io.xeros.model.definitions;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.xeros.model.entity.npc.combat.CombatMethod;
import io.xeros.model.entity.npc.combat.CombatScript;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class DynamicClassLoader {
    public static Object2ObjectMap<Class<? extends CombatMethod>, Class<? extends CombatMethod>> scriptmap = new Object2ObjectOpenHashMap<>();
    public static void load() {
        try (var scanResult = new ClassGraph().enableAllInfo().enableAnnotationInfo().scan()) {
            ClassInfoList directBoxes = scanResult.getClassesWithAnnotation(CombatScript.class);
            for (var d : directBoxes) {
                for (var n : d.getSubclasses().directOnly()) {
                    scriptmap.put((Class<? extends CombatMethod>) n.loadClass(), (Class<? extends CombatMethod>) n.loadClass());
                }
            }
        }
    }
}
