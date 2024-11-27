package io.xeros.content.combat

import io.xeros.content.combat.core.Hit
import io.xeros.content.combat.melee.CombatPrayer
import io.xeros.model.Animation
import io.xeros.model.Direction
import io.xeros.model.projectile.Projectile
import io.xeros.model.entity.Entity
import io.xeros.model.entity.npc.NPC
import io.xeros.model.entity.player.Player
import io.xeros.model.entity.player.Position

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-10-26
 */

val ProtectFromMelee = CombatPrayer.PROTECT_FROM_MELEE
val ProtectFromMagic = CombatPrayer.PROTECT_FROM_MAGIC
val ProtectFromRanged = CombatPrayer.PROTECT_FROM_RANGED

val North = Direction.NORTH
val South = Direction.SOUTH
val East = Direction.EAST
val West = Direction.WEST

infix fun NPC.hit(target: Entity?) : Hit = Hit(this, target, 0, 0)

infix fun Hit.damage(dmg: Int) : Hit = run { this.damage = dmg; return this }
infix fun Hit.withPoison(dmg: Int) : Hit = run { this.damage = dmg; this.hitType = Hitmark.POISON; return this }
infix fun Hit.withVenom(dmg: Int) : Hit = run { this.damage = dmg; this.hitType = Hitmark.VENOM; return this }

infix fun Entity.seq(sequence: Int) = run { this.animation = Animation(sequence) }
infix fun Entity.anim(animation: Int) = run { this.animation = Animation(animation) }

infix fun Pair<Player, Projectile>.at(target: Entity) = this.first.playerAssistant.sendProjectile(this.second)

infix fun Position.offset(pair: Pair<Int, Int>) : Position = this.translate(pair.first, pair.second)

infix fun Entity.prayerActive(prayer: Int) : Boolean {
    if(this !is Player) return false
    return CombatPrayer.isPrayerOn(this, prayer)
}

