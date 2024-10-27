package io.xeros.model.entity.healthbar;

import io.xeros.model.entity.Entity;
import lombok.Getter;

@Getter
public class StaticHealthBarUpdate extends HealthBarUpdate {
    private final int curHealth;
    private final int maxHealth;
    private final int delay;
    private final int barWidth;


    public StaticHealthBarUpdate(int id, int curHealth, int maxHealth, int delay) {
        super(id);
        this.curHealth = curHealth;
        this.maxHealth = maxHealth;
        this.delay = delay;
        this.barWidth = (int) (((double) curHealth / maxHealth) * this.template.getWidth());
    }

    public StaticHealthBarUpdate(Entity entity) {
        super(entity.healthBar);
        this.curHealth = entity.getHealth().getCurrentHealth();
        this.maxHealth = entity.getHealth().getMaximumHealth();
        this.delay = 0;
        this.barWidth = (int) (((double) curHealth / maxHealth) * this.template.getWidth());
    }

}