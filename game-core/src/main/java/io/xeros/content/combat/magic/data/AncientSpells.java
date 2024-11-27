package io.xeros.content.combat.magic.data;

import io.xeros.model.Graphic;

public enum AncientSpells {

    SMOKE_RUSH(32, 384, 51, 43, 31, -1, 385, 10, 1978, Graphic.GraphicHeight.MIDDLE, Graphic.GraphicHeight.MIDDLE),
    SHADOW_RUSH(33, 378, 51, 43, 0, -1, 379, 10, 1978, Graphic.GraphicHeight.LOW, Graphic.GraphicHeight.LOW),
    BLOOD_RUSH(34, 372, 51, 43, 0, -1, 373, 10, 1978, Graphic.GraphicHeight.MIDDLE, Graphic.GraphicHeight.MIDDLE),
    ICE_RUSH(35, 360, 51, 43, 0, -1, 361, 10, 1978, Graphic.GraphicHeight.LOW, Graphic.GraphicHeight.LOW),
    SMOKE_BURST(36, -1, 51, 43, 31, -1, 389, 10, 1979, Graphic.GraphicHeight.MIDDLE, Graphic.GraphicHeight.MIDDLE),
    SHADOW_BURST(37, -1, 51, 43, 31, -1, 382, 10, 1979, Graphic.GraphicHeight.LOW, Graphic.GraphicHeight.LOW),
    BLOOD_BURST(38, -1, 51, 43, 31, -1, 376, 10, 1979, Graphic.GraphicHeight.MIDDLE, Graphic.GraphicHeight.MIDDLE),
    ICE_BURST(39, -1, 51, 43, 0, -1, 363, 10, 1979, Graphic.GraphicHeight.LOW, Graphic.GraphicHeight.LOW),
    SMOKE_BLITZ(40, 386, 51, 43, 31, -1, 387, 10, 1978, Graphic.GraphicHeight.MIDDLE, Graphic.GraphicHeight.MIDDLE),
    SHADOW_BLITZ(41, 380, 51, 43, 0, -1, 381, 10, 1978, Graphic.GraphicHeight.LOW, Graphic.GraphicHeight.LOW),
    BLOOD_BLITZ(42, 374, 51, 43, 0, -1, 375, 10, 1978, Graphic.GraphicHeight.MIDDLE, Graphic.GraphicHeight.MIDDLE),
    ICE_BLITZ(43, -1, 51, 43, 0, 366, 367, 10, 1978, Graphic.GraphicHeight.MIDDLE, Graphic.GraphicHeight.MIDDLE),
    SMOKE_BARRAGE(44, -1, 51, 43, 31, -1, 391, 10, 1979, Graphic.GraphicHeight.MIDDLE, Graphic.GraphicHeight.MIDDLE),
    SHADOW_BARRAGE(45, -1, 51, 43, 31, -1, 383, 10, 1979, Graphic.GraphicHeight.LOW, Graphic.GraphicHeight.LOW),
    BLOOD_BARRAGE(46, -1, 51, 43, 31, -1, 377, 10, 1979, Graphic.GraphicHeight.MIDDLE, Graphic.GraphicHeight.MIDDLE),
    ICE_BARRAGE(47, -1, 51, 43, 0, -1, 369, 10, 1979,  Graphic.GraphicHeight.LOW, Graphic.GraphicHeight.LOW);

    public final int spellID, projectile, castAnimation, startSpeed, startHeight, endHeight, startGraphic, endGraphic, stepMultiplier;

    public final Graphic.GraphicHeight startGraphicheight;
    public final Graphic.GraphicHeight endGraphicHeight;
    AncientSpells(int spellID, int projectile, int startSpeed, int startHeight, int endHeight, int startGraphic, int endGraphic, int stepMultiplier, int castAnimation, Graphic.GraphicHeight startGraphicheight, Graphic.GraphicHeight endGraphicHeight) {
        this.spellID = spellID;
        this.projectile = projectile;
        this.startSpeed = startSpeed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.startGraphic = startGraphic;
        this.endGraphic = endGraphic;
        this.stepMultiplier = stepMultiplier;
        this.castAnimation = castAnimation;
        this.startGraphicheight = startGraphicheight;
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

    public static AncientSpells findSpellProjectileData(int spellID, Graphic.GraphicHeight startGraphicHeight, Graphic.GraphicHeight endGraphicHeight) {
        if (spellID != -1) {
            for (AncientSpells spell : AncientSpells.values()) {
                if (spell.spellID == spellID) {
                    return spell;
                }
            }
        }
        if (startGraphicHeight != null) {
            for (AncientSpells spell : AncientSpells.values()) {
                if (spell.startGraphicheight == startGraphicHeight) {
                    return spell;
                }
            }
        }
        if (endGraphicHeight != null) {
            for (AncientSpells spell : AncientSpells.values()) {
                if (spell.endGraphicHeight == endGraphicHeight) {
                    return spell;
                }
            }
        }
        return null;
    }
}