package io.xeros.content.bosses.obor;

import io.xeros.content.instances.*;
import io.xeros.model.definition.Npcs;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.cycleevent.*;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.*;

/**
 * @author Ynneh
 */

public class OborInstance extends InstancedArea {

    public static final int KEY = 20754;

    public OborInstance() {
        super(InstanceConfiguration.CLOSE_ON_EMPTY, Boundary.OBOR_AREA);
    }

    public void begin(Player player) {
        player.sendMessage("You use your key to unlock the gate");
        player.getItems().deleteItem(KEY, 1);
        player.getPA().sendScreenFlash(1, 500);

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

            int tick;

            @Override
            public void execute(CycleEventContainer container) {

                if (player == null || player.isDead() || !player.isOnline()) {
                    container.stop();
                    return;
                }
                if (tick == 4) {
                    enter(player);
                    container.stop();
                }
                tick++;
            }

        }, 1);
    }

    public void enter(Player player) {
        try {
            Position pos = new Position(3092, 9815, getHeight());
            player.moveTo(pos);
            player.facePosition(pos.getX(), pos.getY() - 1);
            add(player);
            NPC npc = new OborNPC(Npcs.OBOR, new Position(3095, 9799, getHeight()), player);
            add(npc);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject object, int option) {
        switch (object.getId()) {
            case 29489:
            case 29488:
                /**
                 * Leave
                 */
                player.moveTo(new Position(3095, 9832, 0));
                return true;

            case 29491://rocks down
                if (player.getInstance().getNpcs().size() > 0) {
                    NPC npc = player.getInstance().getNpcs().get(0);
                    npc.attackEntity(player);
                }

                climbRocks(player, object.getY() >= 9806);
                return true;
        }
        return false;
    }

    private void climbRocks(Player player, boolean enter) {

        int x = player.getX();

        int y = enter ? 9804 : 9807;

        player.setForceMovement(x, y, 0, 75, "NORTH", 1148);
        /**
         * Faces Object
         */
        player.facePosition(x, y);

        player.sendMessage("You climb "+(enter ? "into" : "out")+" of the pit.");

    }

    @Override
    public void onDispose() {
        getPlayers().stream().forEach(plr -> {
            remove(plr);
        });
    }
}
