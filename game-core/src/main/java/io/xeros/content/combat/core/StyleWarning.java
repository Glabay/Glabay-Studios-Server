package io.xeros.content.combat.core;

import io.xeros.content.combat.weapon.CombatStyle;
import io.xeros.model.entity.CombatType;
import io.xeros.model.definition.Npcs;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

public class StyleWarning {
    public static final String STYLE_WARNING_TOGGLE_KEY = "style_warning_enabled";
    private static final String STYLE_WARNING_LAST_NPC_KEY = "style_warning_last_npc";
    private static final String STYLE_WARNING_TIMER_KEY = "style_warning_last_time";
    private static final int STYLE_WARNING_CUTOFF = 50;
    private static final int STYLE_WARNING_MESSAGE_DELAY = 30_000;

    static void styleWarning(Player attacker, Entity defender, CombatType combatType) {
        if (!attacker.getAttributes().getBoolean(STYLE_WARNING_TOGGLE_KEY)) {
            return;
        }

        NPC npc = defender.asNPC();

        switch (npc.getNpcId()) {
            case Npcs.DAGANNOTH_REX:
                return; // Ignore
        }

        if (attacker.getAttributes().getInt(STYLE_WARNING_LAST_NPC_KEY) != npc.getNpcId()
                && System.currentTimeMillis() - attacker.getAttributes().getLong(STYLE_WARNING_TIMER_KEY) >= STYLE_WARNING_MESSAGE_DELAY) {
            int meleeSlash = npc.getNpcStats().getDefenceLevel() + npc.getNpcStats().getSlashDef();
            int meleeCrush = npc.getNpcStats().getDefenceLevel() + npc.getNpcStats().getCrushDef();
            int meleeStab = npc.getNpcStats().getDefenceLevel() + npc.getNpcStats().getStabDef();
            int magic = npc.getNpcStats().getDefenceLevel() + npc.getNpcStats().getMagicDef();
            int ranged = npc.getNpcStats().getDefenceLevel() + npc.getNpcStats().getRangeDef();

            if (meleeSlash < STYLE_WARNING_CUTOFF && meleeCrush < STYLE_WARNING_CUTOFF && meleeStab < STYLE_WARNING_CUTOFF
                    && magic < STYLE_WARNING_CUTOFF && ranged < STYLE_WARNING_CUTOFF) {
                return;
            }

            CombatType optimalCombatType;
            CombatStyle optimalMeleeStyle = null;

            if (magic <= meleeCrush && magic <= meleeStab && magic <= meleeSlash && magic <= ranged) {
                optimalCombatType = CombatType.MAGE;
            } else if (ranged <= meleeCrush && ranged <= meleeStab && ranged <= magic && ranged <= meleeSlash) {
                optimalCombatType = CombatType.RANGE;
            } else if (meleeSlash <= meleeCrush && meleeSlash <= meleeStab) {
                optimalCombatType = CombatType.MELEE;
                optimalMeleeStyle = CombatStyle.SLASH;
            } else if (meleeCrush <= meleeSlash && meleeCrush <= meleeStab) {
                optimalCombatType = CombatType.MELEE;
                optimalMeleeStyle = CombatStyle.CRUSH;
            } else {
                optimalCombatType = CombatType.MELEE;
                optimalMeleeStyle = CombatStyle.STAB;
            }

            if (combatType != optimalCombatType || combatType == CombatType.MELEE && attacker.getCombatConfigs().getWeaponMode().getCombatStyle() != optimalMeleeStyle) {
                attacker.getAttributes().setInt(STYLE_WARNING_LAST_NPC_KEY, npc.getNpcId());
                attacker.getAttributes().setLong(STYLE_WARNING_TIMER_KEY, System.currentTimeMillis());

                attacker.sendMessage("<col=eb3434>This npc is weak against "
                        + (optimalMeleeStyle != null ? optimalMeleeStyle.toString().toLowerCase() : optimalCombatType)
                        + ", consider switching styles (::stylewarning)."
                );
            }
        }
    }
}
