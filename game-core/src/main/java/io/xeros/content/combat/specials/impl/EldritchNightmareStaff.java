package io.xeros.content.combat.specials.impl;

import io.xeros.Server;
import io.xeros.content.combat.Damage;
import io.xeros.content.combat.specials.Special;
import io.xeros.model.Graphic;
import io.xeros.model.cycleevent.impl.StaffOfTheDeadEvent;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

public class EldritchNightmareStaff extends Special {

    public EldritchNightmareStaff() {
        super(7.5, 1.0, 1.0, new int[] { 24425 });
    }

    public static final Graphic ELDRITCHGFX = new Graphic(1761);

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.usingMagic = true;
        player.startAnimation(8532);
        player.gfx0(1762);
        target.startGraphic(new Graphic(1761));
        Server.getEventHandler().stop(player, "nightmare_staff");
        Server.getEventHandler().submit(new StaffOfTheDeadEvent(player));
        player.usingMagic = false;
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {
        if (target instanceof Player) {
            player.usingMagic = true;
            if (damage.getAmount() > 0) {
                player.playerLevel[5] += (damage.getAmount() / 2);
                if (player.playerLevel[5] > 120) {
                    player.playerLevel[5] = 120;
                    player.getPA().refreshSkill(5);
                }
                player.getPA().refreshSkill(5);
            }
        } else {
            player.usingMagic = true;
            if (damage.getAmount() > 0) {
                player.playerLevel[5] += (damage.getAmount() / 2);
                if (player.playerLevel[5] > 120) {
                    player.playerLevel[5] = 120;
                    player.getPA().refreshSkill(5);
                }
                player.getPA().refreshSkill(5);
            }
        }
        player.usingMagic = false;
    }

}

