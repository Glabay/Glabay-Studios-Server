package io.xeros.content.combat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;

@Data
public class WeaponAnimation {
    int attackAnimation;
    private static final Gson gson = new Gson();
    public static Int2ObjectMap<WeaponAnimation> animationMap = new Int2ObjectOpenHashMap<>();

    public static void loadEquipmentDefinitions(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Type linkedData = new TypeToken<Int2ObjectOpenHashMap<WeaponAnimation>>() {
            }.getType();
            animationMap = gson.fromJson(reader, linkedData);
            System.out.println("loaded: " + animationMap.size() + " Weapon Animations...");
        }
    }

    public WeaponAnimation getInfo(int id) {
        return animationMap.get(id);
    }

    @Override
    public String toString() {
        return "WeaponAnimation{" +
                "attackAnimation=" + attackAnimation +
                '}';
    }
}
