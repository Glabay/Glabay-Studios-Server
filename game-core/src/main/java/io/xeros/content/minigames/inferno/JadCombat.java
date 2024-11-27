package io.xeros.content.minigames.inferno;

import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.formula.MagicMaxHit;
import io.xeros.content.combat.formula.MeleeMaxHit;
import io.xeros.content.combat.formula.RangeMaxHit;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class JadCombat {

    public static void start(NPC jad, Player player) {
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int ticks;

            @Override
            public void execute(CycleEventContainer container) {
                if (player == null) {
                    container.stop();
                    return;
                }
                if (player.isDead()) {
                    container.stop();
                    return;
                }
                if (jad != null) {
                    if (!jad.isDead()) {
                        if (!Boundary.isIn(player, Boundary.INFERNO)) {
                            Inferno.reset(player);
                            container.stop();
                            return;
                        }
                        if (player == null) {
                            container.stop();
                            return;
                        }
                        if (player.isDead()) {
                            container.stop();
                            return;
                        }
                        jad.facePlayer(player.getIndex());
                    }
                }
                int type;
                if (jad.getDistance(player.getX(), player.getY()) <= 2) {
                    type = Misc.random(0, 2);
                } else {
                    type = Misc.random(0, 1);
                }
                if (ticks % 10 == 0 && ticks != 0) {
                    if (jad != null) {
                        if (!jad.isDead()) {
                            if (type == 0) {
                                jad.startAnimation(7593);
                            } else if (type == 1) {
                                jad.startAnimation(7592);
                            } else if (type == 2) {
                                jad.startAnimation(7590);
                            }
                        }
                    }
                    CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                        int tick;
                        int amount = Misc.random(0, 113);

                        @Override
                        public void execute(CycleEventContainer container) {
                            if (tick == 0) {
                                if (jad != null) {
                                    if (!jad.isDead()) {
                                        if (!Boundary.isIn(player, Boundary.INFERNO)) {
                                            Inferno.reset(player);
                                            container.stop();
                                            return;
                                        }
                                        if (player == null) {
                                            container.stop();
                                            return;
                                        }
                                        if (player.isDead()) {
                                            container.stop();
                                            return;
                                        }
                                        int nX = jad.getX();
                                        int nY = jad.getY();
                                        int pX = player.getX();
                                        int pY = player.getY();
                                        int offX = (nX - pX) * -1;
                                        int offY = (nY - pY) * -1;
                                        int centerX = nX + jad.getSize() / 2;
                                        int centerY = nY + jad.getSize() / 2;

                                        int speed = 130;
                                        int startHeight = 110;
                                        int endHeight = 31;
                                        int delay = 0;
                                        if (type == 0) {
                                            player.gfx100(451);
                                            if (player.protectingRange()) {
                                                amount = 0;
                                            }
                                        } else if (type == 1) {
                                            if (player.protectingMagic()) {
                                                amount = 0;
                                            }
                                            player.getPA().createPlayersProjectile(centerX, centerY, player.getX(), player.getY(), 50, speed, 448, startHeight, endHeight, -player.getIndex() - 1, 65, delay);
                                        } else if (type == 2) {
                                            if (player.protectingMelee()) {
                                                amount = 0;
                                            }
                                        }
                                    }
                                }
                            }

                            if (tick == 1 || tick == 2) {
                                if (type == 0) {
                                    if (player.protectingRange()) {
                                        amount = 0;
                                    }
                                } else if (type == 1) {
                                    if (player.protectingMagic()) {
                                        amount = 0;
                                    }
                                } else if (type == 2) {
                                    if (player.protectingMelee()) {
                                        amount = 0;
                                    }
                                }
                            }

                            if (tick == 3) {
                                if (jad != null) {
                                    if (jad.isDead()) {
                                        container.stop();
                                        return;
                                    }
                                }
                                if (jad == null) {
                                    container.stop();
                                    return;
                                }

                                if (player == null) {
                                    container.stop();
                                    return;
                                }
                                if (!Boundary.isIn(player, Boundary.INFERNO)) {
                                    Inferno.reset(player);
                                    container.stop();
                                    return;
                                }
                                if (jad != null) {
                                    if (!jad.isDead()) {
                                        if (player == null) {
                                            container.stop();
                                            return;
                                        }
                                        if (player.isDead()) {
                                            container.stop();
                                            return;
                                        }
                                        if (type == 0) {
                                            if (player.protectingRange()) {
                                                amount = 0;
                                            }
                                            if (Misc.random(500) + 200 > Misc.random(RangeMaxHit.calculateRangeDefence(player))) {
                                                if (amount == 0) {
                                                    player.appendDamage(jad, amount, Hitmark.MISS);
                                                } else {
                                                    player.appendDamage(jad, amount, Hitmark.HIT);
                                                }
                                            } else {
                                                player.appendDamage(jad, 0, Hitmark.MISS);
                                            }
                                        } else if (type == 1) {
                                            if (player.protectingMagic()) {
                                                amount = 0;
                                            }
                                            if (Misc.random(500) + 200 > Misc.random(MagicMaxHit.mageDefence(player))) {
                                                if (amount == 0) {
                                                    player.appendDamage(jad, amount, Hitmark.MISS);
                                                } else {
                                                    player.appendDamage(jad, amount, Hitmark.HIT);
                                                }
                                            } else {
                                                player.appendDamage(jad, 0, Hitmark.MISS);
                                            }
                                        } else if (type == 2) {
                                            if (player.protectingMelee()) {
                                                amount = 0;
                                            }
                                            if (Misc.random(500) + 200 > Misc.random(MeleeMaxHit.calculateMeleeDefence(player, jad))) {
                                                if (amount == 0) {
                                                    player.appendDamage(jad, amount, Hitmark.MISS);
                                                } else {
                                                    player.appendDamage(jad, amount, Hitmark.HIT);
                                                }
                                            } else {
                                                player.appendDamage(jad, 0, Hitmark.MISS);
                                            }
                                        }
                                    }
                                }
                                container.stop();
                            }

                            if (jad != null) {
                                if (jad.isDead()) {
                                    container.stop();
                                    return;
                                }
                            }
                            if (jad == null) {
                                container.stop();
                                return;
                            }

                            if (player == null) {
                                container.stop();
                                return;
                            }

                            if (player.isDead()) {
                                container.stop();
                                return;
                            }
                            if (!Boundary.isIn(player, Boundary.INFERNO)) {
                                Inferno.reset(player);
                                container.stop();
                                return;
                            }
                            tick++;
                        }
                    }, 1);
                }
                ticks++;
            }
        }, 1);
    }

}
