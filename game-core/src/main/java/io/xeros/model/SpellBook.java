package io.xeros.model;

import io.xeros.util.Misc;

public enum SpellBook {
    MODERN, ANCIENT, LUNAR
    ;

    @Override
    public String toString() {
        return Misc.formatPlayerName(name().toLowerCase());
    }
}
