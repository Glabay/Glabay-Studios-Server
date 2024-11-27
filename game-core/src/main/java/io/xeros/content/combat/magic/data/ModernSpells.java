package io.xeros.content.combat.magic.data;

import io.xeros.model.Graphic;

public enum ModernSpells {
    WIND_STRIKE(0, 91, 51, 43, 31, 90, 92, 10, 1162,  Graphic.GraphicHeight.HIGH),
    WATER_STRIKE(1, 94, 51, 43, 31, 93, 95, 10, 1162,  Graphic.GraphicHeight.HIGH),
    EARTH_STRIKE(2, 97, 51, 43, 31, 96, 98, 10, 1162,  Graphic.GraphicHeight.HIGH),
    FIRE_STRIKE(3, 100, 51, 43, 31, 99, 101, 10, 1162,  Graphic.GraphicHeight.HIGH),
    WIND_BOLT(4, 118, 51, 43, 31, 117, 119, 10, 1162,  Graphic.GraphicHeight.HIGH),
    WATER_BOLT(5, 121, 51, 43, 31, 120, 122, 10, 1162,  Graphic.GraphicHeight.HIGH),
    EARTH_BOLT(6, 124, 51, 43, 31, 123, 125, 10, 1162,  Graphic.GraphicHeight.HIGH),
    FIRE_BOLT(7, 127, 51, 43, 31, 126, 128, 10, 1162,  Graphic.GraphicHeight.HIGH),
    WIND_BLAST(8, 133, 51, 43, 31, 132, 134, 10, 1162,  Graphic.GraphicHeight.HIGH),
    WATER_BLAST(9, 136, 51, 43, 31, 135, 137, 10, 1162,  Graphic.GraphicHeight.HIGH),
    EARTH_BLAST(10, 139, 51, 43, 31, 138, 140, 10, 1162,  Graphic.GraphicHeight.HIGH),
    FIRE_BLAST(11, 130, 51, 43, 31, 129, 131, 10, 1162,  Graphic.GraphicHeight.HIGH),
    SARADOMIN_STRIKE(28, -1, 51, 43, 31, -1, 76, 10, 811, Graphic.GraphicHeight.HIGH),
    CLAWS_OF_GUTHIX(29, -1, 51, 43, 31, -1, 77, 10, 811,  Graphic.GraphicHeight.HIGH),
    FLAMES_OF_ZAMORAK(30, -1, 51, 43, 31, -1, 78, 10, 811,  Graphic.GraphicHeight.HIGH),
    WIND_WAVE(12, 159, 51, 43, 31, 158, 160, 10, 1167,  Graphic.GraphicHeight.HIGH),
    WATER_WAVE(13, 162, 51, 43, 31, 161, 163, 10, 1167,  Graphic.GraphicHeight.HIGH),
    EARTH_WAVE(14, 165, 51, 43, 31, 164, 166, 10, 1167,  Graphic.GraphicHeight.HIGH),
    FIRE_WAVE(15, 156, 51, 43, 31, 155, 157, 10, 1167,  Graphic.GraphicHeight.HIGH),
    SNARE(23, 178, 75, 43, 0, 177, 180, 10, 1161,  Graphic.GraphicHeight.LOW),
    VULNERABILITY(19, 168, 34, 36, 31, 167, 169, 10, 718,  Graphic.GraphicHeight.LOW),
    MAGIC_DART(27, 328, 51, 43, 31, -1, 329, 10, 1576,  Graphic.GraphicHeight.LOW),
    IBAN_BLAST(26, 89, 51, 36, 31, 87, 89, 10, 708,  Graphic.GraphicHeight.LOW),
    BIND(22, 178, 75, 45, 0, 177, 181, 10, 1161,  Graphic.GraphicHeight.LOW),
    CURSE(18, 109, 51, 43, 31, 108, 110, 10, 1165,  Graphic.GraphicHeight.LOW),
    WEAKEN(17, 106, 44, 36, 31, 105, 107, 10, 1164,  Graphic.GraphicHeight.HIGH),
    CONFUSE(16, 103, 61, 36, 31, 102, 104, 10, 1163,  Graphic.GraphicHeight.HIGH),
    CRUMBLE_UNDEAD(25, 146, 46, 31, 31, 145, 147, 10, 724,  Graphic.GraphicHeight.LOW),
    ENFEEBLE(20, 171, 48, 36, 31, 170, 172, 10, 728,  Graphic.GraphicHeight.LOW),
    STUN(21, 174, 52, 36, 31, 173, 80, 10, 729,  Graphic.GraphicHeight.LOW),
    ENTANGLE(24, 178, 75, 43, 0, 177, 180, 10, 1161,  Graphic.GraphicHeight.LOW),
    TELEBLOCK(31, 1299, 75, 43, 31, -1, 345, 10, 1820,  Graphic.GraphicHeight.LOW),
    AIR_SURGE(94, 1456, 51, 43, 31, 1455, 1457, 10, 7855,  Graphic.GraphicHeight.HIGH),
    WATER_SURGE(95, 1459, 51, 43, 31, 1458, 1460, 10, 7855,  Graphic.GraphicHeight.HIGH),
    EARTH_SURGE(96, 1462, 51, 43, 31, 1461, 1463, 10, 7855,  Graphic.GraphicHeight.HIGH),
    FIRE_SURGE(97, 1465, 51, 43, 31, 1464, 1466, 10, 7855,  Graphic.GraphicHeight.HIGH);

    public final int spellID, projectile, castAnimation, startSpeed, startHeight, endHeight, startGraphic, endGraphic, stepMultiplier;

    public final Graphic.GraphicHeight endGraphicHeight;

    ModernSpells(int spellID, int projectile, int startSpeed, int startHeight, int endHeight, int startGraphic, int endGraphic, int stepMultiplier, int castAnimation, Graphic.GraphicHeight endGraphicHeight) {
        this.spellID = spellID;
        this.projectile = projectile;
        this.startSpeed = startSpeed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.startGraphic = startGraphic;
        this.endGraphic = endGraphic;
        this.stepMultiplier = stepMultiplier;
        this.castAnimation = castAnimation;
        this.endGraphicHeight = endGraphicHeight;
    }

    public int getProjectile() {
        return projectile;
    }

    public int getStartHeight() {
        return startHeight;
    }

    public int getEndHeight() {
        return endHeight;
    }

    public static ModernSpells findSpellProjectileData(int spellID, Graphic.GraphicHeight endGraphicHeight) {
        if (spellID != -1) {
            for (ModernSpells spell : ModernSpells.values()) {
                if (spell.spellID == spellID) {
                    return spell;
                }
            }
        }
        if (endGraphicHeight != null) {
            for (ModernSpells spell : ModernSpells.values()) {
                if (spell.endGraphicHeight == endGraphicHeight) {
                    return spell;
                }
            }
        }
        return null;
    }
}