package io.xeros.content.skills.slayer;

import java.util.Arrays;

public enum TaskExtension {
    BLOODVELD("bloodveld"),
    DUST_DEVIL("dust devil"),
    GARGOYLE("gargoyle"),
    NECHS("nechryael"),
    KRAKEN("kraken"),
    GREATER_DEMON("greater demon"),
    BLACK_DEMON("black demon");

    private final String[] names;

    TaskExtension(String...names) {
        this.names = names;
    }

    public boolean extended(String name) {
        return Arrays.stream(names).anyMatch(string -> string.toLowerCase().contains(name.toLowerCase()));
    }

    public String[] getNames() {
        return names;
    }
}
