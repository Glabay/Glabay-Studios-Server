package io.xeros.content.combat.magic.data;

import io.xeros.model.Graphic;

public enum PoweredStaffSpellData {
    TUMMEKENS_SHADOW(99, 2126, 51, 70, 31, 2125, 2127, 10, 9493,  Graphic.GraphicHeight.HIGH),
    //ACCURSED_SCEPTRE(7, 2337, 51, 43, 31, -1, 78, 10, 1167,  Graphic.GraphicHeight.HIGH),
    SANGUINESTI_STAFF(98, 1539, 51, 31, 11, 1540, 1541, 10, 1167,  Graphic.GraphicHeight.LOW),
    //DAWNBRINGER(15, 1547, 51, 31, 11, 1546, 1548, 10, 1167,  Graphic.GraphicHeight.LOW),
    TRIDENT_OF_THE_SEAS(52, 1252, 51, 25, 11, 1251, 1253, 10, 1167,  Graphic.GraphicHeight.LOW),
    TRIDENT_OF_THE_SWAMP(53, 1040, 51, 25, 11, 665, 1042, 10, 1167,  Graphic.GraphicHeight.LOW);
    //STARTER_STAFF(9353, 100, 51, 43, 31, 99, 101, 10, 1162, Graphic.GraphicHeight.HIGH);

    public final int spellID, projectile, castAnimation, startSpeed, startHeight, endHeight, startGraphic, endGraphic, stepMultiplier;

    public final Graphic.GraphicHeight endGraphicHeight;
    PoweredStaffSpellData(int spellID, int projectile, int startSpeed, int startHeight, int endHeight, int startGraphic, int endGraphic, int stepMultiplier, int castAnimation, Graphic.GraphicHeight endGraphicHeight) {
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

    public static PoweredStaffSpellData findSpellProjectileData(int spellID, Graphic.GraphicHeight endGraphicHeight) {
        if (spellID != -1) {
            for (PoweredStaffSpellData spell : PoweredStaffSpellData.values()) {
                if (spell.spellID == spellID) {
                    return spell;
                }
            }
        }
        if (endGraphicHeight != null) {
            for (PoweredStaffSpellData spell : PoweredStaffSpellData.values()) {
                if (spell.endGraphicHeight == endGraphicHeight) {
                    return spell;
                }
            }
        }
        return null;
    }

}
