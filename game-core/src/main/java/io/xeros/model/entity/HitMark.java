package io.xeros.model.entity;

import io.xeros.content.combat.Hitmark;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HitMark {

    // Getters and Setters
    private Hitmark type;
    private int damage;
    private int delay;
    private int damageType;

    // Constructor with all parameters
    public HitMark(Hitmark type, int damage, int delay, int damageType) {
        this.type = type;
        this.damage = damage;
        this.delay = delay;
        this.damageType = damageType;
    }

    // Overloaded constructor for default 'delay'
    public HitMark(Hitmark type, int damage, int damageType) {
        this(type, damage, 0, damageType); // Calling the main constructor with 'delay' as 0
    }

    // Optionally, toString method for easy printing
    @Override
    public String toString() {
        return "HitMark{" +
                "type=" + type +
                ", damage=" + damage +
                ", delay=" + delay +
                ", damageType=" + damageType +
                '}';
    }
}