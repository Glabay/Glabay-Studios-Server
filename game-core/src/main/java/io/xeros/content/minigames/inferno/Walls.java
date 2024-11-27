package io.xeros.content.minigames.inferno;

import io.xeros.content.combat.Hitmark;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCDumbPathFinder;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

import static io.xeros.content.minigames.inferno.Inferno.reset;

public class Walls {

    private Wall wall1;
    private Wall wall2;
    private Wall wall3;
    private NPC nib1;
    private NPC nib2;
    private NPC nib3;
    private int wall1Alive;
    private int wall1Hp;
    private int wall2Alive;
    private int wall2Hp;
    private int wall3Alive;
    private int wall3Hp;

    public Walls() {
    }

    public void setDefaultWallAttributes() {
        wall1Alive = 0;
        wall1Hp = 1000;
        wall2Alive = 0;
        wall2Hp = 1000;
        wall3Alive = 0;
        wall3Hp = 1000;
    }

    public void resetWallAttributes() {
        wall1Alive = 0;
        wall1Hp = 0;
        wall2Alive = 0;
        wall2Hp = 0;
        wall3Alive = 0;
        wall3Hp = 0;
    }

    public void killWalls() {
        if (wall1 != null) {
            wall1.setDead(true);
        }
        if (wall2 != null) {
            wall2.setDead(true);
        }
        if (wall3 != null) {
            wall3.setDead(true);
        }
    }

    public void killNibs() {
        if (nib1 != null) {
            nib1.setDead(true);
        }
        if (nib2 != null) {
            nib2.setDead(true);
        }
        if (nib3 != null) {
            nib3.setDead(true);
        }
    }

    public void createWalls(Player player, Inferno ctx) {
        if (wall1Alive == 0 || wall1Hp > 0) {
            if(wall1 == null) {
                wall1 = new Wall(new Position(2270, 5333, ctx.getHeight()), ctx);
                wall1.getHealth().setCurrentHealth(wall1Hp);
                wall1Alive = 1;
            }
        }
        if (wall2Alive == 0 || wall2Hp > 0) {
            if(wall2 == null) {
                wall2 = new Wall(new Position(2259, 5349, ctx.getHeight()), ctx);
                wall2.getHealth().setCurrentHealth(wall2Hp);
                wall2Alive = 1;
            }
        }
        if (wall3Alive == 0 || wall3Hp > 0) {
            if(wall3 == null) {
                wall3 = new Wall(new Position(2278, 5349, ctx.getHeight()), ctx);
                wall3.getHealth().setCurrentHealth(wall3Hp);
                wall3Alive = 1;
            }
        }
    }
    
    public void walls(Player player, Inferno ctx, int[][] type) {
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int ticks;

            @Override
            public void execute(CycleEventContainer container) {
                if (player == null) {
                    container.stop();
                    return;
                }
                if (!Boundary.isIn(player, Boundary.INFERNO)) {
                    reset(player);
                    container.stop();
                    return;
                }
                if (ctx.getInfernoWaveId() >= type.length && ctx.getInfernoWaveType() > 0) {
                    onStopped();
                    container.stop();
                    return;
                }
                if (ctx.started) {
                    if (ticks == 0) {
                        nib1 = NPCSpawning.spawnNpcOld(player, InfernoWaveData.JAL_NIB, 2270, 5339, ctx.getHeight(), 0, InfernoWaveData.getHp(InfernoWaveData.JAL_NIB), InfernoWaveData.getMax(InfernoWaveData.JAL_NIB), InfernoWaveData.getAtk(InfernoWaveData.JAL_NIB), InfernoWaveData.getDef(InfernoWaveData.JAL_NIB), false, false);
                        nib2 = NPCSpawning.spawnNpcOld(player, InfernoWaveData.JAL_NIB, 2269, 5339, ctx.getHeight(), 0, InfernoWaveData.getHp(InfernoWaveData.JAL_NIB), InfernoWaveData.getMax(InfernoWaveData.JAL_NIB), InfernoWaveData.getAtk(InfernoWaveData.JAL_NIB), InfernoWaveData.getDef(InfernoWaveData.JAL_NIB), false, false);
                        nib3 = NPCSpawning.spawnNpcOld(player, InfernoWaveData.JAL_NIB, 2271, 5339, ctx.getHeight(), 0, InfernoWaveData.getHp(InfernoWaveData.JAL_NIB), InfernoWaveData.getMax(InfernoWaveData.JAL_NIB), InfernoWaveData.getAtk(InfernoWaveData.JAL_NIB), InfernoWaveData.getDef(InfernoWaveData.JAL_NIB), false, false);
                        ctx.add(nib1);
                        ctx.add(nib2);
                        ctx.add(nib3);
                    }
                    if (wall1 != null) {
                        if (!wall1.isDead()) {
                            NPCDumbPathFinder.walkTowards(nib1, 2270, 5336);
                            NPCDumbPathFinder.walkTowards(nib2, 2271, 5336);
                            NPCDumbPathFinder.walkTowards(nib3, 2272, 5336);
                            if (ticks % 4 == 0) {
                                if (!nib1.isDead()) {
                                    if (wall1.getDistance(nib1.getX(), nib1.getY()) <= 1) {
                                        nib1.facePosition(wall1.getX(), wall1.getY());
                                        nib1.startAnimation(7574);
                                        int amount = Misc.random(0, 5);
                                        wall1Hp -= amount;
                                        if (amount == 0) {
                                            wall1.appendDamage(nib1, amount, Hitmark.MISS);
                                        } else {
                                            wall1.appendDamage(nib1, amount, Hitmark.HIT);
                                        }
                                    }
                                }
                                if (!nib2.isDead()) {
                                    if (wall1.getDistance(nib2.getX(), nib2.getY()) <= 1) {
                                        nib2.facePosition(wall1.getX() + 1, wall1.getY());
                                        nib2.startAnimation(7574);
                                        int amount = Misc.random(0, 5);
                                        wall1Hp -= amount;
                                        if (amount == 0) {
                                            wall1.appendDamage(nib2, amount, Hitmark.MISS);
                                        } else {
                                            wall1.appendDamage(nib2, amount, Hitmark.HIT);
                                        }
                                    }
                                }
                                if (!nib3.isDead()) {
                                    if (wall1.getDistance(nib3.getX(), nib3.getY()) <= 1) {
                                        nib3.facePosition(wall1.getX() + 2, wall1.getY());
                                        nib3.startAnimation(7574);
                                        int amount = Misc.random(0, 5);
                                        wall1Hp -= amount;
                                        if (amount == 0) {
                                            wall1.appendDamage(nib3, amount, Hitmark.MISS);
                                        } else {
                                            wall1.appendDamage(nib3, amount, Hitmark.HIT);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (wall2 != null) {
                        if (!wall2.isDead() && wall1.isDead() && !wall3.isDead()) {
                            NPCDumbPathFinder.walkTowards(nib1, 2261, 5348);
                            NPCDumbPathFinder.walkTowards(nib2, 2260, 5348);
                            NPCDumbPathFinder.walkTowards(nib3, 2259, 5348);
                            if (ticks % 4 == 0) {
                                if (!nib1.isDead()) {
                                    if (wall2.getDistance(nib1.getX(), nib1.getY()) <= 1) {
                                        nib1.facePosition(wall2.getX() + 2, wall2.getY());
                                        nib1.startAnimation(7574);
                                        int amount = Misc.random(0, 5);
                                        wall2Hp -= amount;
                                        if (amount == 0) {
                                            wall2.appendDamage(nib1, amount, Hitmark.MISS);
                                        } else {
                                            wall2.appendDamage(nib1, amount, Hitmark.HIT);
                                        }
                                    }
                                }
                                if (!nib2.isDead()) {
                                    if (wall2.getDistance(nib2.getX(), nib2.getY()) <= 1) {
                                        nib2.facePosition(wall2.getX() + 1, wall2.getY());
                                        nib2.startAnimation(7574);
                                        int amount = Misc.random(0, 5);
                                        wall2Hp -= amount;
                                        if (amount == 0) {
                                            wall2.appendDamage(nib2, amount, Hitmark.MISS);
                                        } else {
                                            wall2.appendDamage(nib2, amount, Hitmark.HIT);
                                        }
                                    }
                                }
                                if (!nib3.isDead()) {
                                    if (wall2.getDistance(nib3.getX(), nib3.getY()) <= 1) {
                                        nib3.facePosition(wall2.getX(), wall2.getY());
                                        nib3.startAnimation(7574);
                                        int amount = Misc.random(0, 5);
                                        wall2Hp -= amount;
                                        if (amount == 0) {
                                            wall2.appendDamage(nib3, amount, Hitmark.MISS);
                                        } else {
                                            wall2.appendDamage(nib3, amount, Hitmark.HIT);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (wall3 != null) {
                        if (!wall3.isDead() && wall1.isDead() && wall2.isDead()) {
                            NPCDumbPathFinder.walkTowards(nib1, 2277, 5350);
                            NPCDumbPathFinder.walkTowards(nib2, 2277, 5350);
                            NPCDumbPathFinder.walkTowards(nib3, 2277, 5352);
                            if (ticks % 4 == 0) {
                                if (!nib1.isDead()) {
                                    if (wall3.getDistance(nib1.getX(), nib1.getY()) <= 1) {
                                        nib1.facePosition(wall3.getX(), wall3.getY());
                                        nib1.startAnimation(7574);
                                        int amount = Misc.random(0, 5);
                                        wall3Hp -= amount;
                                        if (amount == 0) {
                                            wall3.appendDamage(nib1, amount, Hitmark.MISS);
                                        } else {
                                            wall3.appendDamage(nib1, amount, Hitmark.HIT);
                                        }
                                    }
                                }
                                if (!nib2.isDead()) {
                                    if (wall3.getDistance(nib2.getX(), nib2.getY()) <= 1) {
                                        nib2.facePosition(wall3.getX(), wall3.getY() + 1);
                                        nib2.startAnimation(7574);
                                        int amount = Misc.random(0, 5);
                                        wall3Hp -= amount;
                                        if (amount == 0) {
                                            wall3.appendDamage(nib2, amount, Hitmark.MISS);
                                        } else {
                                            wall3.appendDamage(nib2, amount, Hitmark.HIT);
                                        }
                                    }
                                }
                                if (!nib3.isDead()) {
                                    if (wall3.getDistance(nib3.getX(), nib3.getY()) <= 1) {
                                        nib3.facePosition(wall3.getX(), wall3.getY() + 2);
                                        nib3.startAnimation(7574);
                                        int amount = Misc.random(0, 5);
                                        wall3Hp -= amount;
                                        if (amount == 0) {
                                            wall3.appendDamage(nib3, amount, Hitmark.MISS);
                                        } else {
                                            wall3.appendDamage(nib3, amount, Hitmark.HIT);
                                        }
                                    }
                                }
                            }
                        }
                    }


                    if (wall1 != null) {
                        if (wall1.getHealth().getCurrentHealth() == 0) {
                            wall1.setDead(true);
                        }
                    }
                    if (wall2 != null) {
                        if (wall2.getHealth().getCurrentHealth() == 0) {
                            wall2.setDead(true);
                        }
                    }
                    if (wall3 != null) {
                        if (wall3.getHealth().getCurrentHealth() == 0) {
                            wall3.setDead(true);
                        }
                    }


                    if (wall1 != null && wall2 != null && wall3 != null) {
                        if (wall1.isDead() && wall2.isDead() && wall3.isDead()) {
                            if (!nib1.isDead()) {
                                NPCDumbPathFinder.follow(nib1, player);
                                if (ticks % 4 == 0) {
                                    if (nib1.getDistance(player.getX(), player.getY()) <= 1) {
                                        nib1.facePlayer(player.getIndex());
                                        nib1.startAnimation(7574);
                                        int amount = Misc.random(0, 5);
                                        if (player.protectingMelee()) {
                                            amount = 0;
                                        }
                                        if (amount == 0) {
                                            player.appendDamage(nib1, amount, Hitmark.MISS);
                                        } else {
                                            player.appendDamage(nib1, amount, Hitmark.HIT);
                                        }
                                    }
                                }
                            }
                            if (!nib2.isDead()) {
                                NPCDumbPathFinder.follow(nib2, player);
                                if (ticks % 4 == 0) {
                                    if (nib2.getDistance(player.getX(), player.getY()) <= 1) {
                                        nib2.facePlayer(player.getIndex());
                                        nib2.startAnimation(7574);
                                        int amount = Misc.random(0, 5);
                                        if (player.protectingMelee()) {
                                            amount = 0;
                                        }
                                        if (amount == 0) {
                                            player.appendDamage(nib2, amount, Hitmark.MISS);
                                        } else {
                                            player.appendDamage(nib2, amount, Hitmark.HIT);
                                        }
                                    }
                                }
                            }
                            if (!nib3.isDead()) {
                                NPCDumbPathFinder.follow(nib3, player);
                                if (ticks % 4 == 0) {
                                    if (nib3.getDistance(player.getX(), player.getY()) <= 1) {
                                        nib3.facePlayer(player.getIndex());
                                        nib3.startAnimation(7574);
                                        int amount = Misc.random(0, 5);
                                        if (player.protectingMelee()) {
                                            amount = 0;
                                        }
                                        if (amount == 0) {
                                            player.appendDamage(nib3, amount, Hitmark.MISS);
                                        } else {
                                            player.appendDamage(nib3, amount, Hitmark.HIT);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (wall1 == null && wall2 == null && wall3 == null) {
                        if (!nib1.isDead()) {
                            NPCDumbPathFinder.follow(nib1, player);
                            if (ticks % 4 == 0) {
                                if (nib1.getDistance(player.getX(), player.getY()) <= 1) {
                                    nib1.facePlayer(player.getIndex());
                                    nib1.startAnimation(7574);
                                    int amount = Misc.random(0, 5);
                                    if (player.protectingMelee()) {
                                        amount = 0;
                                    }
                                    if (amount == 0) {
                                        player.appendDamage(nib1, amount, Hitmark.MISS);
                                    } else {
                                        player.appendDamage(nib1, amount, Hitmark.HIT);
                                    }
                                }
                            }
                        }
                        if (!nib2.isDead()) {
                            NPCDumbPathFinder.follow(nib2, player);
                            if (ticks % 4 == 0) {
                                if (nib2.getDistance(player.getX(), player.getY()) <= 1) {
                                    nib2.facePlayer(player.getIndex());
                                    nib2.startAnimation(7574);
                                    int amount = Misc.random(0, 5);
                                    if (player.protectingMelee()) {
                                        amount = 0;
                                    }
                                    if (amount == 0) {
                                        player.appendDamage(nib2, amount, Hitmark.MISS);
                                    } else {
                                        player.appendDamage(nib2, amount, Hitmark.HIT);
                                    }
                                }
                            }
                        }
                        if (!nib3.isDead()) {
                            NPCDumbPathFinder.follow(nib3, player);
                            if (ticks % 4 == 0) {
                                if (nib3.getDistance(player.getX(), player.getY()) <= 1) {
                                    nib3.facePlayer(player.getIndex());
                                    nib3.startAnimation(7574);
                                    int amount = Misc.random(0, 5);
                                    if (player.protectingMelee()) {
                                        amount = 0;
                                    }
                                    if (amount == 0) {
                                        player.appendDamage(nib3, amount, Hitmark.MISS);
                                    } else {
                                        player.appendDamage(nib3, amount, Hitmark.HIT);
                                    }
                                }
                            }
                        }
                    }
                    if (nib1 != null && nib2 != null && nib3 != null) {
                        if (nib1.isDead() && nib2.isDead() && nib3.isDead()) {
                            ctx.started = false;
                            container.stop();
                        }
                    }
                    if (nib1 == null && nib2 == null && nib3 == null) {
                        ctx.started = false;
                        container.stop();
                    }
                    ticks++;

                }
            }
        }, 1);
    }

}
