package io.xeros.content.boosts;

import io.xeros.content.skills.Skill;
import io.xeros.model.entity.player.Player;

public class PlayerSkillWrapper {
    private final Player player;
    private final Skill skill;

    public PlayerSkillWrapper(final Player player, final Skill skill) {
        this.player = player;
        this.skill = skill;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Skill getSkill() {
        return this.skill;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof PlayerSkillWrapper)) return false;
        final PlayerSkillWrapper other = (PlayerSkillWrapper) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$player = this.getPlayer();
        final Object other$player = other.getPlayer();
        if (this$player == null ? other$player != null : !this$player.equals(other$player)) return false;
        final Object this$skill = this.getSkill();
        final Object other$skill = other.getSkill();
        if (this$skill == null ? other$skill != null : !this$skill.equals(other$skill)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PlayerSkillWrapper;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $player = this.getPlayer();
        result = result * PRIME + ($player == null ? 43 : $player.hashCode());
        final Object $skill = this.getSkill();
        result = result * PRIME + ($skill == null ? 43 : $skill.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "PlayerSkillWrapper(player=" + this.getPlayer() + ", skill=" + this.getSkill() + ")";
    }
}
