package io.xeros.model.definitions;

import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.model.entity.npc.combat.CombatMethod;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NpcStats {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(NpcStats.class.getName());
    @Getter
    public static Int2ObjectOpenHashMap<NpcStats> npcStatsMap;

    public static void load() {
        try (FileReader fr = new FileReader(Server.getDataDirectory() + "/cfg/npc/npc_stats.json")) {
            npcStatsMap = new Gson().fromJson(fr, new TypeToken<Int2ObjectOpenHashMap<NpcStats>>() {}.getType());
            log.info("Loaded " + npcStatsMap.size() + " npc stats.");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public static NpcStats forId(int npcId) {
        if (!npcStatsMap.containsKey(npcId)) {
            npcStatsMap.put(npcId, DEFAULT);
        }
        return npcStatsMap.get(npcId);
    }

    static final NpcStats DEFAULT = builder().createNpcStats();

    public static NpcStatsBuilder builder() {
        return new NpcStatsBuilder();
    }

    public NpcStatsBuilder from(NpcStats npcStats) {
        NpcStatsBuilder builder = new NpcStatsBuilder();
        builder.from(npcStats);
        return builder;
    }


    @Getter
    private final String name;
    @Getter
    private final int hitpoints;
    @Getter
    private final int combatLevel;
    @Getter
    private final int slayerLevel;
    @Getter
    private final int attackSpeed;
    @Getter
    private final int attackLevel;
    @Getter
    private final int strengthLevel;
    @Getter
    private final int defenceLevel;
    @Getter
    private final int rangeLevel;
    @Getter
    private final int magicLevel;
    @Getter
    private final int stab;
    @Getter
    private final int slash;
    @Getter
    private final int crush;
    @Getter
    private final int range;
    @Getter
    private final int magic;
    @Getter
    private final int stabDef;
    @Getter
    private final int slashDef;
    @Getter
    private final int crushDef;
    @Getter
    private final int rangeDef;
    @Getter
    private final int magicDef;
    @Getter
    private final int bonusAttack;
    @Getter
    private final int bonusStrength;
    @Getter
    private final int bonusRangeStrength;
    @Getter
    private final int bonusMagicDamage;
    @Getter
    private final boolean poisonImmune;
    @Getter
    private final boolean venomImmune;
    @Getter
    private final boolean dragon;
    @Getter
    private final boolean demon;
    @Getter
    private final boolean undead;
    public Scripts scripts;

    @Override
    public String toString() {
        return "NpcCombatDefinition{" + "name='" + name + '\'' + ", attackLevel=" + attackLevel + ", strengthLevel=" + strengthLevel + ", defenceLevel=" + defenceLevel + ", rangeLevel=" + rangeLevel + ", magicLevel=" + magicLevel + ", stab=" + stab + ", slash=" + slash + ", crush=" + crush + ", range=" + range + ", magic=" + magic + ", stabDef=" + stabDef + ", slashDef=" + slashDef + ", crushDef=" + crushDef + ", rangeDef=" + rangeDef + ", magicDef=" + magicDef + ", bonusAttack=" + bonusAttack + ", bonusStrength=" + bonusStrength + ", bonusRangeStrength=" + bonusRangeStrength + ", bonusMagicDamage=" + bonusMagicDamage + ", poisonImmune=" + poisonImmune + ", venomImmune=" + venomImmune + ", dragon=" + dragon + ", demon=" + demon + ", undead=" + undead + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof NpcStats other)) return false;
        if (!other.canEqual((Object) this)) return false;
        if (this.getHitpoints() != other.getHitpoints()) return false;
        if (this.getCombatLevel() != other.getCombatLevel()) return false;
        if (this.getSlayerLevel() != other.getSlayerLevel()) return false;
        if (this.getAttackSpeed() != other.getAttackSpeed()) return false;
        if (this.getAttackLevel() != other.getAttackLevel()) return false;
        if (this.getStrengthLevel() != other.getStrengthLevel()) return false;
        if (this.getDefenceLevel() != other.getDefenceLevel()) return false;
        if (this.getRangeLevel() != other.getRangeLevel()) return false;
        if (this.getMagicLevel() != other.getMagicLevel()) return false;
        if (this.getStab() != other.getStab()) return false;
        if (this.getSlash() != other.getSlash()) return false;
        if (this.getCrush() != other.getCrush()) return false;
        if (this.getRange() != other.getRange()) return false;
        if (this.getMagic() != other.getMagic()) return false;
        if (this.getStabDef() != other.getStabDef()) return false;
        if (this.getSlashDef() != other.getSlashDef()) return false;
        if (this.getCrushDef() != other.getCrushDef()) return false;
        if (this.getRangeDef() != other.getRangeDef()) return false;
        if (this.getMagicDef() != other.getMagicDef()) return false;
        if (this.getBonusAttack() != other.getBonusAttack()) return false;
        if (this.getBonusStrength() != other.getBonusStrength()) return false;
        if (this.getBonusRangeStrength() != other.getBonusRangeStrength()) return false;
        if (this.getBonusMagicDamage() != other.getBonusMagicDamage()) return false;
        if (this.isPoisonImmune() != other.isPoisonImmune()) return false;
        if (this.isVenomImmune() != other.isVenomImmune()) return false;
        if (this.isDragon() != other.isDragon()) return false;
        if (this.isDemon() != other.isDemon()) return false;
        if (this.isUndead() != other.isUndead()) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        return Objects.equals(this$name, other$name);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof NpcStats;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getHitpoints();
        result = result * PRIME + this.getCombatLevel();
        result = result * PRIME + this.getSlayerLevel();
        result = result * PRIME + this.getAttackSpeed();
        result = result * PRIME + this.getAttackLevel();
        result = result * PRIME + this.getStrengthLevel();
        result = result * PRIME + this.getDefenceLevel();
        result = result * PRIME + this.getRangeLevel();
        result = result * PRIME + this.getMagicLevel();
        result = result * PRIME + this.getStab();
        result = result * PRIME + this.getSlash();
        result = result * PRIME + this.getCrush();
        result = result * PRIME + this.getRange();
        result = result * PRIME + this.getMagic();
        result = result * PRIME + this.getStabDef();
        result = result * PRIME + this.getSlashDef();
        result = result * PRIME + this.getCrushDef();
        result = result * PRIME + this.getRangeDef();
        result = result * PRIME + this.getMagicDef();
        result = result * PRIME + this.getBonusAttack();
        result = result * PRIME + this.getBonusStrength();
        result = result * PRIME + this.getBonusRangeStrength();
        result = result * PRIME + this.getBonusMagicDamage();
        result = result * PRIME + (this.isPoisonImmune() ? 79 : 97);
        result = result * PRIME + (this.isVenomImmune() ? 79 : 97);
        result = result * PRIME + (this.isDragon() ? 79 : 97);
        result = result * PRIME + (this.isDemon() ? 79 : 97);
        result = result * PRIME + (this.isUndead() ? 79 : 97);
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    public static class Scripts {
        public String combat;
        public CombatMethod combat_;
        public Class<CombatMethod> combatMethodClass;

        public void resolve() {
            try {
                combat_ = resolveCombat(combat);
                if (combat != null) combatMethodClass = (Class<CombatMethod>) resolveCCM(combat);
            } catch (ClassNotFoundException e) {
                log.info("Missing script, no such class: " + e);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        public CombatMethod newCombatInstance() {
            if (combatMethodClass != null) {
                try {
                    return combatMethodClass.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                   log.info("issue initializing new combat instance!");
                }
            }
            return null;
        }

        public Class<? extends CombatMethod> resolveCCM(String className) throws ClassNotFoundException {
            Class<? extends CombatMethod> clazz = null;
            for (var v : DynamicClassLoader.scriptmap.values()) {
                if (v.getSimpleName().equalsIgnoreCase(className)) {
                    clazz = v;
                    break;
                }
            }
            return clazz;
        }

        public static CombatMethod resolveCombat(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
            CombatMethod result = null;
            for (var c : DynamicClassLoader.scriptmap.keySet()) {
                if (c == null) continue;
                if (c.getSimpleName().equalsIgnoreCase(className)) {
                    result = c.getDeclaredConstructor().newInstance();
                    break;
                }
            }
            return result;
        }
    }
}
