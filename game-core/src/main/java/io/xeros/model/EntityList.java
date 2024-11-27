package io.xeros.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.xeros.model.entity.Entity;

public class EntityList<T extends Entity> {

    private final List<T> entityList = new ArrayList<>();

    public void add(T t) {
        if (!entityList.contains(t)) {
            entityList.add(t);
        }
    }

    public void remove(T t) {
        entityList.remove(t);
    }

    public T get(int index) {
        return entityList.get(index);
    }

    public void clear() {
        entityList.clear();
    }

    public List<T> getEntityList() {
        return Collections.unmodifiableList(entityList);
    }
}
