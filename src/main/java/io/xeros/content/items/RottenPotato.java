package io.xeros.content.items;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.world.objects.GlobalObject;

import java.util.Objects;

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-08-17
 */
public class RottenPotato {

    private Player player;
    private static RottenPotato instance;

    public static RottenPotato getInstance() {
        if (Objects.isNull(instance)){
            synchronized (RottenPotato.class) {
                if (Objects.isNull(instance)) {
                    instance = new RottenPotato();
                }
            }
        }
        return instance;
    }

    public RottenPotato forPlayer(Player player) {
        this.player = player;
        return this;
    }

    public boolean onPlayer(Player target) {
        return false;
    }
    public boolean onItem(GameItem target) {
        return false;
    }
    public boolean onNpc(NPC target) {
        return false;
    }
    public boolean onObject(GlobalObject target) {
        return false;
    }

    public boolean eat() {
        player.sendMessage("eat the potato");
        System.out.println("eat the potato");
        return true;
    }
    public boolean slice() {
        player.sendMessage("slice the potato");
        System.out.println("slice the potato");
        return true;
    }
    public boolean peel() {
        player.sendMessage("peel the potato");
        System.out.println("peel the potato");
        return true;
    }
    public boolean mash() {
        player.sendMessage("mash the potato");
        System.out.println("mash the potato");
        return true;
    }
}
