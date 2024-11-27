package io.xeros.content.minigames.inferno;

import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.formula.MagicMaxHit;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

class RangersAndMagers {

    private static boolean verifyRangerState(Player player, Tzkalzuk ctx) {
        return verifyState(player, ctx) && ctx.ranger != null && !ctx.ranger.isDead();
    }

    private static boolean verifyMagerState(Player player, Tzkalzuk ctx) {
        return verifyState(player, ctx) && ctx.mager != null && !ctx.mager.isDead();
    }

    private static boolean verifyState(Player player, Tzkalzuk ctx) {
        return player.getInferno() != null && !player.isDead && !player.getInferno().zukDead && ctx.glyph != null && !ctx.glyph.isDead()
                && Boundary.isIn(player, Boundary.INFERNO);
    }

    static void spawnRanger(Player player, Tzkalzuk ctx) {
        ctx.ranger = NPCSpawning.spawnNpcOld(player, InfernoWaveData.JAL_XIL, 2278, 5351, ctx.getHeight(), 0, InfernoWaveData.getHp(InfernoWaveData.JAL_XIL), InfernoWaveData.getMax(InfernoWaveData.JAL_XIL), InfernoWaveData.getAtk(InfernoWaveData.JAL_XIL), InfernoWaveData.getDef(InfernoWaveData.JAL_XIL), false, false);
        player.getInferno().kill.add(ctx.ranger);

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int tick;

            @Override
            public void execute(CycleEventContainer container) {
                if (!verifyRangerState(player, ctx)) {
                    container.stop();
                    return;
                }

                if (ctx.ranger.lastDamageTaken > 0 || player.getInferno().glyph.isDead()) {
                    ctx.ranger.facePlayer(player.getIndex());
                } else {
                    ctx.ranger.facePosition(player.getInferno().glyph.getX(), player.getInferno().glyph.getY());
                }

                if (tick % 6 == 0) {
                    ctx.ranger.startAnimation(7605);

                    CycleEventHandler.getSingleton().addEvent(player, rangerHit(player, ctx), 1);
                }

                tick++;
            }
        }, 1);
    }

    static void spawnMager(Player player, Tzkalzuk ctx) {
        ctx.mager = NPCSpawning.spawnNpcOld(player, InfernoWaveData.JAL_ZEK, 2264, 5351, ctx.getHeight(), 0, InfernoWaveData.getHp(InfernoWaveData.JAL_ZEK), InfernoWaveData.getMax(InfernoWaveData.JAL_ZEK), InfernoWaveData.getAtk(InfernoWaveData.JAL_ZEK), InfernoWaveData.getDef(InfernoWaveData.JAL_ZEK), false, false);
        player.getInferno().kill.add(ctx.mager);

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int tick;

            @Override
            public void execute(CycleEventContainer container) {
                if (!verifyMagerState(player, ctx)) {
                    container.stop();
                    return;
                }

                // Face
                if (ctx.mager.lastDamageTaken > 0 || player.getInferno().glyph.isDead()) {
                    ctx.mager.facePlayer(player.getIndex());
                } else {
                    ctx.mager.facePosition(player.getInferno().glyph.getX(), player.getInferno().glyph.getY());
                }

                // Attack
                if (tick % 8 == 0) {
                    ctx.mager.startAnimation(7610);
                    CycleEventHandler.getSingleton().addEvent(player, getMageHit(player, ctx), 1);
                }

                tick++;
            }
        }, 1);
    }

    private static CycleEvent rangerHit(Player player, Tzkalzuk ctx) {
        return new CycleEvent() {
            int tick;
            int amount = Misc.random(0, 46);

            @Override
            public void execute(CycleEventContainer container) {
                if (!verifyRangerState(player, ctx)) {
                    container.stop();
                    return;
                }

                if (tick == 0) {

                    if (ctx.ranger.lastDamageTaken > 0 || player.getInferno().glyph.isDead()) {
                        int nX = ctx.ranger.getX();
                        int nY = ctx.ranger.getY();
                        int pX = player.getX();
                        int pY = player.getY();
                        int offX = (nX - pX) * -1;
                        int offY = (nY - pY) * -1;
                        int centerX = nX + ctx.ranger.getSize() / 2;
                        int centerY = nY + ctx.ranger.getSize() / 2;

                        int speed = 85;
                        int startHeight = 43;
                        int endHeight = 31;
                        int delay = 0;
                        if (player.protectingRange()) {
                            amount = 0;
                        }
                        player.getPA().createPlayersProjectile(centerX, centerY, player.getX(), player.getY(), 50, speed, 1376, startHeight, endHeight, -player.getIndex() - 1, 65, delay);
                    } else {
                        int nX = ctx.ranger.getX();
                        int nY = ctx.ranger.getY();
                        int pX = player.getInferno().glyph.getX();
                        int pY = player.getInferno().glyph.getY();
                        int offX = (nX - pX) * -1;
                        int offY = (nY - pY) * -1;
                        int centerX = nX + ctx.ranger.getSize() / 2;
                        int centerY = nY + ctx.ranger.getSize() / 2;

                        int speed = 85;
                        int startHeight = 43;
                        int endHeight = 31;
                        int delay = 0;
                        player.getPA().createPlayersProjectile(centerX, centerY, player.getX(), player.getY(), 50, speed, 1376, startHeight, endHeight, player.getInferno().glyph.getIndex() + 1,
                                65, delay);
                    }
                }

                if (tick == 1) {
                    if (player.protectingRange()) {
                        amount = 0;
                    }
                }

                if (tick == 2) {
                    if (ctx.ranger.lastDamageTaken > 0 || player.getInferno().glyph.isDead()) {
                        if (player.protectingRange()) {
                            amount = 0;
                        }

                        if (Misc.random(500) + 200 > Misc.random(MagicMaxHit.mageDefence(player))) {
                            if (amount == 0) {
                                player.appendDamage(ctx.ranger, amount, Hitmark.MISS);
                            } else {
                                player.appendDamage(ctx.ranger, amount, Hitmark.HIT);
                            }
                        } else {
                            player.appendDamage(ctx.ranger, 0, Hitmark.MISS);
                        }
                    } else {
                        amount = Misc.random(0, 70);
                        if (amount == 0) {
                            player.getInferno().glyph.appendDamage(ctx.ranger, amount, Hitmark.MISS);
                        } else {
                            player.getInferno().glyph.appendDamage(ctx.ranger, amount, Hitmark.HIT);
                        }
                    }
                    container.stop();
                }

                tick++;
            }
        };
    }

    private static CycleEvent getMageHit(Player player, Tzkalzuk ctx) {
        return new CycleEvent() {
            int tick;
            int amount = Misc.random(0, 70);

            @Override
            public void execute(CycleEventContainer container) {
                if (!verifyMagerState(player, ctx)) {
                    container.stop();
                    return;
                }

                if (tick == 0) {
                    if (ctx.mager.lastDamageTaken > 0) {
                        int nX = ctx.mager.getX();
                        int nY = ctx.mager.getY();
                        int pX = player.getX();
                        int pY = player.getY();
                        int offX = (nX - pX) * -1;
                        int offY = (nY - pY) * -1;
                        int centerX = nX + ctx.mager.getSize() / 2;
                        int centerY = nY + ctx.mager.getSize() / 2;

                        int speed = 85;
                        int startHeight = 43;
                        int endHeight = 31;
                        int delay = 0;
                        if (player.protectingMagic()) {
                            amount = 0;
                        }
                        player.getPA().createPlayersProjectile(centerX, centerY, offX, offY, 50, speed, 1378, startHeight, endHeight, -player.getIndex() - 1, 65, delay);
                    } else {
                        int nX = ctx.mager.getX();
                        int nY = ctx.mager.getY();
                        int pX = player.getInferno().glyph.getX();
                        int pY = player.getInferno().glyph.getY();
                        int offX = (nX - pX) * -1;
                        int offY = (nY - pY) * -1;
                        int centerX = nX + ctx.mager.getSize() / 2;
                        int centerY = nY + ctx.mager.getSize() / 2;

                        int speed = 85;
                        int startHeight = 43;
                        int endHeight = 31;
                        int delay = 0;
                        player.getPA().createPlayersProjectile(centerX, centerY, offX, offY, 50, speed, 1378, startHeight, endHeight, player.getInferno().glyph.getIndex() + 1,
                                65, delay);
                    }
                }

                if (tick == 2) {
                    if (ctx.mager.lastDamageTaken > 0 || player.getInferno().glyph.isDead()) {
                        if (player.protectingMagic()) {
                            amount = 0;
                        }
                        if (Misc.random(500) + 200 > Misc.random(MagicMaxHit.mageDefence(player))) {
                            if (amount == 0) {
                                player.appendDamage(ctx.mager, amount, Hitmark.MISS);
                            } else {
                                player.appendDamage(ctx.mager, amount, Hitmark.HIT);
                            }
                        } else {
                            player.appendDamage(ctx.mager, 0, Hitmark.MISS);
                        }
                    } else {
                        amount = Misc.random(0, 70);
                        if (amount == 0) {
                            player.getInferno().glyph.appendDamage(ctx.mager, amount, Hitmark.MISS);
                        } else {
                            player.getInferno().glyph.appendDamage(ctx.mager, amount, Hitmark.HIT);
                        }
                    }
                    container.stop();
                }

                tick++;
            }
        };
    }

}
