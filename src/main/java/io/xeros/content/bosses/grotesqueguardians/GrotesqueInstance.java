package io.xeros.content.bosses.grotesqueguardians;

import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.definition.Items;
import io.xeros.model.definition.Npcs;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;

public class GrotesqueInstance extends InstancedArea {

    public static final int GROTESQUE_GUARDIANS_KEY = Items.BRITTLE_KEY;

    public static Position[] DAWN_ENERGY_POS = {
            //31678
            new Position(1701, 4574),
            //31679
            new Position(1692, 4574),
            //31680
            new Position(1696, 4579)
    };
    public int dawnEnergyTicks = 0;
    public boolean dawnFlownAway = false;
    public boolean executingLightningAttack;
    public int phase = 1;

    public GrotesqueInstance() {
        super(InstanceConfiguration.CLOSE_ON_EMPTY, Boundary.GROTESQUE_LAIR);
    }

    public void enter(Player plr) {
        try {
            add(plr);
            GrotesqueGuardianNpc dawn = new GrotesqueGuardianNpc(Npcs.DAWN_7852, new Position(1700, 4576, getHeight()), this);
            GrotesqueGuardianNpc dusk = new GrotesqueGuardianNpc(Npcs.DUSK_7851, new Position(1692, 4576, getHeight()), this);
            dawn.setCounterpart(dusk);
            dusk.setCounterpart(dawn);

            plr.moveTo(new Position(1696, 4567, getHeight()));
            dawnEnergyTicks = 0;
            dawnFlownAway = false;
            executingLightningAttack = false;
            phase = 1;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject object, int option) {
        if (object.getId() >= 31678 && object.getId() <= 31680) {//energy ball on floor
            Position placement = object.getPosition();
            player.getPA().createProjectile(placement.getX(), placement.getY(),1, 1, 64,
                    400, 16,
                    75, 1439, 40, 60, player.getIndex(), 50);
            player.sendMessage("You absorb the energy sphere.");
            int dam = getDamageForOrbId(object.getId());
            player.appendDamage(null, dam, Hitmark.HIT);
            Server.getGlobalObjects().remove(object.getId(), placement.getX(), placement.getY(), placement.getHeight(), player.getInstance());
        }
        if (object.getId() == 31674) {
            player.moveTo(new Position(3428, 3541, 2));
            return true;
        }
        return false;
    }

    @Override
    public void onDispose() {
        getPlayers().stream().forEach(plr -> {
            remove(plr);
        });
    }

    public int getPhase() {
        return phase;
    }

    public int getOrbIdForTicks() {
        if (dawnEnergyTicks >= 30 && dawnEnergyTicks <= 60)
            return 31679;
        else if (dawnEnergyTicks >= 60 && dawnEnergyTicks <= 90)
            return 31680;
        return 31678;
    }

    public int getDamageForOrbId(int id) {
        if (id == 31679)
            return 15;
        else if (id == 31680)
            return 20;
        return 10;
    }

    public int getOrbsLeft() {
        int c = 0;
        for (int i = 31678; i < 31680; i++) {
            for (int l = 0; l < 3; l++) {
                Position pos = GrotesqueInstance.DAWN_ENERGY_POS[l];
                if (Server.getGlobalObjects().exists(i, pos.getX(), pos.getY(), getHeight())) {
                    c++;
                }
            }
        }
        return c;
    }

    public void clearOrbsWithDamageAndHeal(NPC npc, Player target) {
        for (int i = 31678; i < 31680; i++) {
            for (int l = 0; l < 3; l++) {
                Position pos = GrotesqueInstance.DAWN_ENERGY_POS[l];
                if (Server.getGlobalObjects().exists(i, pos.getX(), pos.getY(), getHeight())) {
                    int dam = getDamageForOrbId(i);
                    target.appendDamage(null, dam, Hitmark.HIT);
                    npc.appendHeal(90, Hitmark.NPC_HEAL);
                    npc.getHealth().increase(90);
                    Server.getGlobalObjects().remove(i, pos.getX(), pos.getY(), getHeight(), this);
                }
            }
        }
        dawnEnergyTicks = 0;    
    }

    public void clearOrbs() {
        for (int i = 31678; i < 31680; i++) {
            for (int l = 0; l < 3; l++) {
                Position pos = GrotesqueInstance.DAWN_ENERGY_POS[l];
                if (Server.getGlobalObjects().exists(i, pos.getX(), pos.getY(), getHeight())) {
                    Server.getGlobalObjects().remove(i, pos.getX(), pos.getY(), getHeight(), this);
                }
            }
        }
    }

    public void updateOrbs() {
        for (int i = 31678; i < 31680; i++) {
            int newId = getOrbIdForTicks();
            for (int l = 0; l < 3; l++) {
                Position pos = GrotesqueInstance.DAWN_ENERGY_POS[l];
                GlobalObject obj = Server.getGlobalObjects().get(i, pos.getX(), pos.getY(), getHeight());
                if (obj != null && obj.getObjectId() != newId) {
                    Server.getGlobalObjects().placeObject(obj, newId);
                }
            }
        }
    }
}