package io.xeros.util;

import io.xeros.model.definition.Items;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemConstants {

    private Map<Integer, String> idsToNames = new HashMap<>();
    private Map<String, Integer> namesToIds = new HashMap<>();

    public ItemConstants load() {
        List<Pair<Integer, String>> list = Arrays.stream(Items.class.getFields()).map(field -> {
            try {
                return Pair.of((int) field.get(null), field.getName());
            } catch (IllegalAccessException e) {
                e.printStackTrace(System.err);
                return null;
            }
        }).collect(Collectors.toList());

        list.forEach(pair -> {
            int id = pair.getLeft();
            String name = pair.getRight();
            idsToNames.put(id, name);
            namesToIds.put(name, id);
        });

        return this;
    }

    public String get(int id) {
        String string = idsToNames.get(id);
        if (string == null) {
            System.err.println("No name for item id: " + id);
            return "" + id;
        }
        return "Items." + string;
    }

    public int get(String name) {
        name = name.toUpperCase().replaceAll(" ", "_");
        if (!namesToIds.containsKey(name)) {
            throw new IllegalArgumentException("No id for name: " + name);
        }

        return namesToIds.get(name);
    }

    public Map<Integer, String> getMap() {
        return idsToNames;
    }
}
