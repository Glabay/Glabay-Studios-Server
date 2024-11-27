package io.xeros.model.definitions;

public class NpcStatsBuilder {
    private String name;
    private int hitpoints;
    private int combatLevel;
    private int slayerLevel;
    private int attackSpeed;
    private int attackLevel;
    private int strengthLevel;
    private int defenceLevel;
    private int rangeLevel;
    private int magicLevel;
    private int stab;
    private int slash;
    private int crush;
    private int range;
    private int magic;
    private int stabDef;
    private int slashDef;
    private int crushDef;
    private int rangeDef;
    private int magicDef;
    private int bonusAttack;
    private int bonusStrength;
    private int bonusRangeStrength;
    private int bonusMagicDamage;
    private boolean poisonImmune;
    private boolean venomImmune;
    private boolean dragon;
    private boolean demon;
    private boolean undead;

    public void from(NpcStats npcStats) {
        name = npcStats.getName();
        hitpoints = npcStats.getHitpoints();
        combatLevel = npcStats.getCombatLevel();
        slayerLevel = npcStats.getSlayerLevel();
        attackSpeed = npcStats.getAttackSpeed();
        attackLevel = npcStats.getAttackLevel();
        strengthLevel = npcStats.getStrengthLevel();
        defenceLevel = npcStats.getDefenceLevel();
        rangeLevel = npcStats.getRangeLevel();
        magicLevel = npcStats.getMagicLevel();
        stab = npcStats.getStab();
        slash = npcStats.getSlash();
        crush = npcStats.getCrush();
        range = npcStats.getRange();
        magic = npcStats.getMagic();
        stabDef = npcStats.getStabDef();
        slashDef = npcStats.getSlashDef();
        crushDef = npcStats.getCrushDef();
        rangeDef = npcStats.getRangeDef();
        magicDef = npcStats.getMagicDef();
        bonusAttack = npcStats.getBonusAttack();
        bonusStrength = npcStats.getBonusStrength();
        bonusRangeStrength = npcStats.getBonusRangeStrength();
        bonusMagicDamage = npcStats.getBonusMagicDamage();
        poisonImmune = npcStats.isPoisonImmune();
        venomImmune = npcStats.isVenomImmune();
        dragon = npcStats.isDragon();
        demon = npcStats.isDemon();
        undead = npcStats.isUndead();
    }



    public NpcStatsBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public NpcStatsBuilder setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
        return this;
    }

    public NpcStatsBuilder setCombatLevel(int combatLevel) {
        this.combatLevel = combatLevel;
        return this;
    }

    public NpcStatsBuilder setSlayerLevel(int slayerLevel) {
        this.slayerLevel = slayerLevel;
        return this;
    }

    public NpcStatsBuilder setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
        return this;
    }

    public NpcStatsBuilder setAttackLevel(int attackLevel) {
        this.attackLevel = attackLevel;
        return this;
    }

    public NpcStatsBuilder setStrengthLevel(int strengthLevel) {
        this.strengthLevel = strengthLevel;
        return this;
    }

    public NpcStatsBuilder setDefenceLevel(int defenceLevel) {
        this.defenceLevel = defenceLevel;
        return this;
    }

    public NpcStatsBuilder setRangeLevel(int rangeLevel) {
        this.rangeLevel = rangeLevel;
        return this;
    }

    public NpcStatsBuilder setMagicLevel(int magicLevel) {
        this.magicLevel = magicLevel;
        return this;
    }

    public NpcStatsBuilder setStab(int stab) {
        this.stab = stab;
        return this;
    }

    public NpcStatsBuilder setSlash(int slash) {
        this.slash = slash;
        return this;
    }

    public NpcStatsBuilder setCrush(int crush) {
        this.crush = crush;
        return this;
    }

    public NpcStatsBuilder setRange(int range) {
        this.range = range;
        return this;
    }

    public NpcStatsBuilder setMagic(int magic) {
        this.magic = magic;
        return this;
    }

    public NpcStatsBuilder setStabDef(int stabDef) {
        this.stabDef = stabDef;
        return this;
    }

    public NpcStatsBuilder setSlashDef(int slashDef) {
        this.slashDef = slashDef;
        return this;
    }

    public NpcStatsBuilder setCrushDef(int crushDef) {
        this.crushDef = crushDef;
        return this;
    }

    public NpcStatsBuilder setRangeDef(int rangeDef) {
        this.rangeDef = rangeDef;
        return this;
    }

    public NpcStatsBuilder setMagicDef(int magicDef) {
        this.magicDef = magicDef;
        return this;
    }

    public NpcStatsBuilder setBonusAttack(int bonusAttack) {
        this.bonusAttack = bonusAttack;
        return this;
    }

    public NpcStatsBuilder setBonusStrength(int bonusStrength) {
        this.bonusStrength = bonusStrength;
        return this;
    }

    public NpcStatsBuilder setBonusRangeStrength(int bonusRangeStrength) {
        this.bonusRangeStrength = bonusRangeStrength;
        return this;
    }

    public NpcStatsBuilder setBonusMagicDamage(int bonusMagicDamage) {
        this.bonusMagicDamage = bonusMagicDamage;
        return this;
    }

    public NpcStatsBuilder setPoisonImmune(boolean poisonImmune) {
        this.poisonImmune = poisonImmune;
        return this;
    }

    public NpcStatsBuilder setVenomImmune(boolean venomImmune) {
        this.venomImmune = venomImmune;
        return this;
    }

    public NpcStatsBuilder setDragon(boolean dragon) {
        this.dragon = dragon;
        return this;
    }

    public NpcStatsBuilder setDemon(boolean demon) {
        this.demon = demon;
        return this;
    }

    public NpcStatsBuilder setUndead(boolean undead) {
        this.undead = undead;
        return this;
    }

    public NpcStats createNpcStats() {
        return new NpcStats(name, hitpoints, combatLevel, slayerLevel, attackSpeed, attackLevel, strengthLevel, defenceLevel, rangeLevel, magicLevel, stab,
                slash, crush, range, magic, stabDef, slashDef, crushDef, rangeDef, magicDef, bonusAttack, bonusStrength, bonusRangeStrength, bonusMagicDamage,
                poisonImmune, venomImmune, dragon, demon, undead);
    }
}