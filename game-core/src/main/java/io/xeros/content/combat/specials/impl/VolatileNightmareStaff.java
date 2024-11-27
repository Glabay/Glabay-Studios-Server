package io.xeros.content.combat.specials.impl;

import io.xeros.Server;
import io.xeros.content.combat.Damage;
import io.xeros.content.combat.specials.Special;
import io.xeros.model.Graphic;
import io.xeros.model.cycleevent.impl.NightmareStaffEvent;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;

public class VolatileNightmareStaff extends Special {


    public VolatileNightmareStaff() {
        super(5.5, 1.5, 1, new int[] { 24424 });
    }

    public static final Graphic VOLATILEGFX = new Graphic(1759);
    @Override
    public void activate(Player player, Entity target, Damage damage) {

        Server.getEventHandler().stop(player, "volatile_nightmare_staff");
        player.usingMagic = true;
        player.startAnimation(8532);
        player.gfx0(1760);
        target.startGraphic(new Graphic(1759));

        Server.getEventHandler().submit(new NightmareStaffEvent(player));
        player.usingMagic = false;
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {
        if (target instanceof Player) {
            player.usingMagic = true;
                if (((Player) target).specAmount >= 5.5) {
                    ((Player) target).specAmount -= 5.5;
                    ((Player) target).getPA().requestUpdates();
                } else if (((Player) target).specAmount < 5.5)
                ((Player) target).specAmount = 0;
                ((Player) target).getPA().requestUpdates();
            }
        player.usingMagic = false;
            }
    }


