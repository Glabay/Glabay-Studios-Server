package io.xeros.net.packets;

import java.util.Arrays;

import io.xeros.content.combat.magic.CombatSpellData;
import io.xeros.content.combat.magic.MagicRequirements;
import io.xeros.content.combat.weapon.WeaponData;
import io.xeros.model.Spell;
import io.xeros.model.SpellBook;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;

public class AutocastSpell implements PacketType {

    public static final int PACKET_OPCODE = 5;

    @Override
    public void processPacket(Player c, int packetType, int packetSize) {
        if (c.getMovementState().isLocked())
            return;
        if (c.isNpc) {
            return;
        }
        c.interruptActions();
        int spellId = c.getInStream().readUnsignedWord();
        boolean defensive = c.getInStream().readUnsignedByte() == 1;
        if (c.debugMessage) {
            c.sendMessage("Received autocast packet: id=" + spellId + ", defensive=" + defensive);
        }

        setAutocast(c, spellId, defensive);
    }

    public static boolean setAutocast(Player c, int spellId, boolean defensive) {
        if (Arrays.stream(CombatSpellData.DISABLE_AUTOCAST_STAFFS).anyMatch(staff -> c.getItems().isWearingItem(staff, Player.playerWeapon))) {
            c.sendMessage("You can't autocast with this staff.");
            return false;
        }

        Spell spell = Spell.forId(spellId);
        if (spell == null || !spell.isAutocastable()) {
            c.sendMessage("You can't autocast this spell.");
            return false;
        } else {
            if (c.getCombatConfigs().getWeaponData() == WeaponData.STAFF || c.getCombatConfigs().getWeaponData() == WeaponData.SOTD) {
                if (c.getSpellBook() != spell.getSpellBook()) {
                    c.sendMessage("You have to be using " + spell.getSpellBook() + " magics to autocast that spell.");
                } else {
                    for (int spellIndex = 0; spellIndex < CombatSpellData.MAGIC_SPELLS.length; spellIndex++) {
                        if (CombatSpellData.MAGIC_SPELLS[spellIndex][0] == spell.getId()) {
                            if (c.autocasting && c.autocastId == spellIndex && c.autocastingDefensive == defensive) {
                                c.getPA().resetAutocast();
                                c.debug("Reset autocast");
                                return false;
                            } else if (MagicRequirements.checkMagicReqs(c, spellIndex, false) && verifyStaff(c, spell)) {
                                c.autocasting = true;
                                c.autocastingDefensive = defensive;
                                c.autocastId = spellIndex;
                                updateConfig(c, spell);

                                if (c.debugMessage) {
                                    c.sendMessage("Set autocast to " + spell.getId());
                                }

                                return true;
                            }

                        }
                    }

                    c.sendMessage("You can't autocast this spell.");
                    return false;
                }
            } else {
                c.sendMessage("You can't autocast without a staff!");
                return false;
            }
        }

        return false;
    }

    public static void updateConfig(Player c, Spell spell) {
        c.getPA().sendConfig(CombatSpellData.AUTOCAST_CONFIG, spell.getId());
        c.getPA().sendConfig(CombatSpellData.AUTOCAST_DEFENCE_CONFIG, c.autocastingDefensive ? 1 : 0);
        c.getPA().sendConfig(ItemAssistant.FIGHT_MODE_CONFIG, 3);

        if (c.autocastingDefensive) {
            c.getPA().sendFrame36(109, 1);
            c.getPA().sendFrame36(108, 0);
        } else {
            c.getPA().sendFrame36(108, 1);
            c.getPA().sendFrame36(109, 0);
        }
    }

    private static boolean verifyStaff(Player player, Spell spell) {
        if (spell.getSpellBook() == SpellBook.ANCIENT && Arrays.stream(CombatSpellData.ANCIENT_AUTOCAST_STAFFS)
                .noneMatch(weapon -> weapon == player.playerEquipment[Player.playerWeapon])) {
            player.sendMessage("You can't autocast Ancient magicks with this staff.");
            return false;
        } else if (spell.getSpellBook() == SpellBook.LUNAR) {
            player.sendMessage("You can't autocast Lunar magic.");
            return false;
        }

        return true;
    }
}
