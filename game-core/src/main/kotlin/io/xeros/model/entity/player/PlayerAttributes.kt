package io.xeros.model.entity.player

import io.xeros.model.entity.attribute
import io.xeros.model.entity.persistentAttribute

/**
 * @author Glabay | Glabay-Studios
 * @project Glabay-Studios-Server
 * @social Discord: Glabay
 * @since 2024-11-21
 */


var Player.sanityValue by attribute("dt2_whispy_sanity", 100)

var Player.flaggedAsBot: Boolean by persistentAttribute("flaggedAsBot", false)

var Player.pvpKills : Int by persistentAttribute("pvp-kills", 0)
var Player.pvpDeaths : Int by persistentAttribute("pvp-deaths", 0)
var Player.pvpKillStreak: Int by persistentAttribute("current-killstreak", 0)

/* Bounty Hunter */
var Player.bountyHunterPoints: Int by persistentAttribute("bountyHunterPoints", 0)
var Player.bountyHunterInfoDisplay: Int by persistentAttribute("bountyHunterInfoDisplayIdx", 0)
var Player.bountyHunterInterfaceRateLimit: Int by attribute("bountyHunterRateLimit", 0)
var Player.bountyHunterInfoCooldown: Int by attribute("bountyHunterInfoCooldown", 0)
var Player.bountyHunterKills: Int by persistentAttribute("bountyHunterKills", 0)
var Player.bountyHunterDeaths: Int by persistentAttribute("bountyHunterDeaths", 0)
var Player.bountyHunterKillstreak: Int by persistentAttribute("bountyHunterKillstreak", 0)
var Player.bountyHunterSkipCount: Int by persistentAttribute("bountyHunterSkipCount", 0)
var Player.bountyHunterLastTarget: String by persistentAttribute("bountyHunterLastTarget", "")

