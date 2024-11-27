package io.xeros.content.minigames.inferno;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public class AncestralGlyph {

    /**
     * The amount of time Glyph will freeze for on each side
     */
    private static int FREEZE_TIME = 5;

    private static Position STARTING_POSITION = new Position(2270, 5361);
    private static Position EAST_POSITION = new Position(2283, 5361);
    private static Position WEST_POSITION = new Position(2257, 5361);

    /**
     * Movement, <code>false</code> is left, <code>true</code> is right
     */
    private static final String MOVE_ATTRIBUTE_KEY = "ancestral_glyph_move";

    private static void flip(NPC npc) {
        npc.getAttributes().flipBoolean(MOVE_ATTRIBUTE_KEY);
    }

    private static boolean isWest(NPC npc) {
        return !npc.getAttributes().getBoolean(MOVE_ATTRIBUTE_KEY);
    }

    public static void freezeGlyph(NPC glyph) {
        glyph.freezeTimer = AncestralGlyph.FREEZE_TIME;
        glyph.getAttributes().setBoolean("GLYPH_FREEZE", true);
    }

    public static void removeFreeze(NPC glyph) {
        glyph.getAttributes().setBoolean("GLYPH_FREEZE", false);
    }

    public static boolean isFrozen(NPC glyph) {
        return glyph.getAttributes().getBoolean("GLYPH_FREEZE", false);
    }

    public static void handleMovement(Player player, NPC npc) {
        if (player != null && player.getInferno() != null) {
            npc.walkingHome = false;

            if (player.getInferno().glyphCanMove && npc.freezeTimer == 0) {
                if (npc.absX == 2270 && npc.absY > 5361) { // Starting position
                    npc.moveTowards(STARTING_POSITION.getX(), STARTING_POSITION.getY());

                } else if (!isWest(npc)) {                     // East
                    npc.moveTowards(EAST_POSITION.getX(), EAST_POSITION.getY());

                    if (npc.getPosition().getX() == EAST_POSITION.getX())
                        handleFlip(npc);
                } else {                                    // West
                    npc.moveTowards(WEST_POSITION.getX(), WEST_POSITION.getY());

                    if (npc.getPosition().getX() == WEST_POSITION.getX())
                        handleFlip(npc);

                }

                player.getInferno().glyphCurrentX = npc.absX;
                player.getInferno().glyphCurrentY = npc.absY;
            }
        }
    }

    /**
     * Handles the flip for the glyph to change direction
     * @param glyph The glyph npc
     */
    public static void handleFlip(NPC glyph) {
        if (AncestralGlyph.isFrozen(glyph)) {
            removeFreeze(glyph);
            flip(glyph);
        } else
            AncestralGlyph.freezeGlyph(glyph);
    }

}
