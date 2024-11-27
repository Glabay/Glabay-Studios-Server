package io.xeros.util.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VariableStringCollection {

    private final List<String> list = new ArrayList<>();
    private String afterEntry;

    public VariableStringCollection() { }

    public VariableStringCollection setLineAfterEachEntry(String afterEntry) {
        this.afterEntry = afterEntry;
        return this;
    }

    public void add(String...strings) {
        list.addAll(Arrays.asList(strings));
        if (afterEntry != null)
            list.add(afterEntry);
    }

    public List<String> getList() {
        return list;
    }
}
