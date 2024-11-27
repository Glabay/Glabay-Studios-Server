package io.xeros.model.entity.healthbar;

import lombok.Getter;

@Getter
public class DynamicHealthBarUpdate extends HealthBarUpdate {
    private final int startHealth;
    private final int endHealth;
    private final int maxHealth;
    private final int decreaseSpeed;
    private final int delay;
    private final int startBarWidth;
    private final int endBarWidth;

    public DynamicHealthBarUpdate(int id, int startHealth, int endHealth, int maxHealth, int decreaseSpeed, int delay) {
        super(id);
        this.startHealth = startHealth;
        this.endHealth = endHealth;
        this.maxHealth = maxHealth;
        this.decreaseSpeed = decreaseSpeed;
        this.delay = delay;
        this.startBarWidth = (int) (((double) startHealth / maxHealth) * this.template.getWidth());
        this.endBarWidth = (int) (((double) endHealth / maxHealth) * this.template.getWidth());
    }

}