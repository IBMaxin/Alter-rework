package org.alter.plugins.content.npcs.blackdemon

import org.alter.api.*
import org.alter.api.cfg.*
import org.alter.api.dsl.*
import org.alter.api.ext.*
import org.alter.game.*
import org.alter.game.model.*
import org.alter.game.model.attr.*
import org.alter.game.model.container.*
import org.alter.game.model.container.key.*
import org.alter.game.model.entity.*
import org.alter.game.model.item.*
import org.alter.game.model.queue.*
import org.alter.game.model.shop.*
import org.alter.game.model.timer.*
import org.alter.game.plugin.*
import org.alter.rscm.RSCM.getRSCM

/**
 * Plugin-only Black Demon combat definition and enhanced loot for NPC ID 1432.
 *
 * - Registers a combat definition for `npc.black_demon_1432`.
 * - Drops noted Malicious ashes x1 on every eligible kill.
 * - Awards a 50% chance of one enhanced bonus reward per kill.
 */
class BlackDemonPlugin(
    r: PluginRepository,
    world: World,
    server: Server,
) : KotlinPlugin(r, world, server) {

    init {
        val notedAshesId = getRSCM("item.malicious_ashes_noted")

        val commonRewards = listOf(
            Reward(getRSCM("item.fire_rune"), 50, 100),
            Reward(getRSCM("item.nature_rune"), 20, 40),
            Reward(getRSCM("item.coal_noted"), 10, 25),
            Reward(getRSCM("item.death_rune"), 15, 30),
            Reward(getRSCM("item.mithril_arrow"), 20, 40),
            Reward(getRSCM("item.iron_arrow"), 40, 80),
        )
        val uncommonRewards = listOf(
            Reward(getRSCM("item.shark_noted"), 2, 4),
            Reward(getRSCM("item.prayer_potion4_noted"), 1, 3),
            Reward(getRSCM("item.blood_rune"), 15, 25),
            Reward(getRSCM("item.adamantite_bar_noted"), 2, 5),
            Reward(getRSCM("item.runite_ore_noted"), 1, 3),
            Reward(getRSCM("item.rune_arrow"), 15, 30),
            Reward(getRSCM("item.law_rune"), 15, 25),
            Reward(getRSCM("item.adamant_arrow"), 20, 40),
        )
        val rareRewards = listOf(
            Reward(getRSCM("item.rune_scimitar_noted"), 1, 1),
            Reward(getRSCM("item.rune_longsword_noted"), 1, 1),
            Reward(getRSCM("item.rune_kiteshield_noted"), 1, 1),
            Reward(getRSCM("item.rune_full_helm_noted"), 1, 1),
            Reward(getRSCM("item.uncut_diamond_noted"), 1, 2),
        )
        val ultraRareRewards = listOf(
            Reward(getRSCM("item.rune_platebody_noted"), 1, 1),
            Reward(getRSCM("item.rune_platelegs_noted"), 1, 1),
            Reward(getRSCM("item.black_demon_mask_noted"), 1, 1),
            Reward(getRSCM("item.adamant_kiteshield_noted"), 1, 1),
            Reward(getRSCM("item.crystal_key_noted"), 1, 2),
        )

        setCombatDef("npc.black_demon_1432") {
            species {
                +NpcSpecies.DEMON
            }

            configs {
                attackSpeed = 4
                respawnDelay = 25
            }

            aggro {
                radius = 4
                searchDelay = 1
            }

            stats {
                hitpoints = 157
                attack = 145
                strength = 148
                defence = 152
                magic = 1
                ranged = 1
            }

            bonuses {
                defenceStab = 50
                defenceSlash = 40
                defenceCrush = 30
                defenceMagic = 0
                defenceRanged = 50
            }

            anims {
                death = Animation.DEMON_DEATH
            }

            sound {
                attackSound = Sound.BLACK_DEMON_ATTACK
                deathSound = Sound.BLACK_DEMON_DEATH
                blockSound = Sound.BLACK_DEMON_HIT
            }
        }

        onNpcDeath("npc.black_demon_1432") {
            val npc = ctx as? Npc ?: return@onNpcDeath
            val killer = npc.attr[KILLER_ATTR]?.get() as? Player ?: return@onNpcDeath

            val tile = npc.tile

            world.spawn(GroundItem(notedAshesId, 1, tile, killer))

            if (world.random(1..2) == 1) {
                val roll = world.random(1..100)
                val pool = when {
                    roll <= 50  -> commonRewards
                    roll <= 75  -> uncommonRewards
                    roll <= 90  -> rareRewards
                    roll <= 100 -> ultraRareRewards
                    else -> null
                }
                if (pool != null && pool.isNotEmpty()) {
                    val reward = pool[world.random(0..pool.lastIndex)]
                    val amount = if (reward.min == reward.max) reward.min else world.random(reward.min..reward.max)
                    world.spawn(GroundItem(reward.itemId, amount, tile, killer))
                }
            }
        }
    }

    private data class Reward(val itemId: Int, val min: Int, val max: Int)
}
