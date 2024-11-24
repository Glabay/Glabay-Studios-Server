package io.xeros.content.bosses.nightmare.totem;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.xeros.content.bosses.nightmare.Nightmare;
import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.model.projectile.ProjectileBaseBuilder;

public class Totems {

    private final Nightmare nightmare;
    private final List<Totem> totems = Lists.newArrayList();
    private boolean charged;
    private int phaseTick = -1;

    public Totems(Nightmare nightmare) {
        this.nightmare = nightmare;
        for (TotemSpawn totemSpawn : TotemSpawn.values()) {
            totems.add(new Totem(totemSpawn, totemSpawn.getUnattackableNpcId(), nightmare.getInstance()));
        }
    }

    public void scaleHealth() {
        int health = NightmareConstants.TOTEM_DEFAULT_HEALTH;
        if (nightmare.getInstance().getPlayers().size() > 3) {
            health += (nightmare.getInstance().getPlayers().size() - 3) * NightmareConstants.TOTEM_HEALTH_SCALE;
        }

        final int healthSet = health;
        totems.forEach(totem -> {
            totem.getHealth().setMaximumHealth(healthSet);
            totem.getHealth().reset();
        });
    }

    public void becomeVulnerable() {
        totems.forEach(totem -> {
            totem.requestTransform(totem.getTotemSpawn().getAttackableNpcId());
            totem.getHealth().reset();
            charged = false;
            phaseTick = -1;
        });
    }

    public void process() {
        Preconditions.checkState(!getTotems().isEmpty(), "No totems present!");
        if (phaseTick >= 0) {
            if (phaseTick > 0) {
                phaseTick--;
            } else {
                phaseTick = -1;
            }
        } else {
            if (isActive() && !charged && totems.stream().allMatch(totem -> totem.getHealth().getCurrentHealth() == 0)) {
                nightmare.getHealth().reduce(NightmareConstants.TOTEMS_HIT);
                totems.forEach(totem -> {
                    totem.requestTransform(totem.getTotemSpawn().getUnattackableNpcId());
                    new ProjectileBaseBuilder()
                            .setProjectileId(1768)
                            .setStartHeight(90)
                            .setStartHeight(90)
                            .setCurve(0)
                            .createProjectileBase()
                            .createTargetedProjectile(totem, nightmare)
                            .send(totem.getInstance());
                });
                charged = true;
                phaseTick = 2;
            }
        }
    }

    public boolean readyToPhase() {
        return totems.stream().allMatch(totem -> totem.getNpcId() == totem.getTotemSpawn().getUnattackableNpcId()) && phaseTick == -1;
    }

    public boolean isActive() {
        return totems.stream().allMatch(totem -> totem.getNpcId() == totem.getTotemSpawn().getAttackableNpcId()
                || totem.getNpcId() == totem.getTotemSpawn().getChargedNpcId()) || phaseTick != -1;
    }

    public List<Totem> getTotems() {
        return Collections.unmodifiableList(totems);
    }
}
