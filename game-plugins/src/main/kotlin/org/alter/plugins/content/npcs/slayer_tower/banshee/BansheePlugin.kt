package org.alter.plugins.content.npcs.slayer_tower.banshee

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
 * Banshee (Slayer 15) combat definition and drop table.
 */
class BansheePlugin(
    r: PluginRepository,
    world: World,
    server: Server,
) : KotlinPlugin(r, world, server) {

    init {
        setCombatDef(*NPC_IDS.toTypedArray()) {
            configs {
                attackSpeed = 4
                respawnDelay = 25
            }
            aggro {
                radius = 4
                searchDelay = 1
            }
            stats {
                hitpoints = 22
                attack = 30
                strength = 25
                defence = 25
                magic = 1
                ranged = 1
            }
            bonuses {
                defenceStab = 0
                defenceSlash = 0
                defenceCrush = 0
                defenceMagic = 10
                defenceRanged = 0
            }
            anims {
                attack = Animation.BANSHEE_ATTACK
                block = Animation.BANSHEE_HIT
                death = Animation.BANSHEE_DEATH
            }
            sound {
                attackSound = Sound.BANSHEE_ATTACK
                deathSound = Sound.BANSHEE_DEATH
                blockSound = Sound.BANSHEE_HIT
            }
            slayerData {
                levelRequirement = 15
                xp = 22.0
            }
        }

        NPC_IDS.forEach { npcId ->
            onNpcDeath(npcId) {
                val npc = ctx as? Npc ?: return@onNpcDeath
                val killer = npc.attr[KILLER_ATTR]?.get() as? Player ?: return@onNpcDeath
                val tile = npc.tile

                val commonRoll = world.random(1..128)
                when {
                    commonRoll <= 11 -> world.spawn(GroundItem(pureEssenceId, 13, tile, killer))
                    commonRoll <= 22 -> world.spawn(GroundItem(fishingBaitId, 15, tile, killer))
                    commonRoll <= 27 -> world.spawn(GroundItem(fishingBaitId, 7, tile, killer))
                    commonRoll <= 32 -> world.spawn(GroundItem(coinsId, 13, tile, killer))
                    commonRoll <= 47 -> world.spawn(GroundItem(coinsId, 35, tile, killer))
                }

                val runeRoll = world.random(1..128)
                when {
                    runeRoll <= 3 -> world.spawn(GroundItem(airRuneId, 3, tile, killer))
                    runeRoll <= 6 -> world.spawn(GroundItem(cosmicRuneId, 2, tile, killer))
                    runeRoll <= 8 -> world.spawn(GroundItem(chaosRuneId, 3, tile, killer))
                    runeRoll <= 9 -> world.spawn(GroundItem(fireRuneId, 7, tile, killer))
                    runeRoll <= 10 -> world.spawn(GroundItem(chaosRuneId, 7, tile, killer))
                }

                val uncommonRoll = world.random(1..128)
                when (uncommonRoll) {
                    1 -> world.spawn(GroundItem(ironKiteshieldId, 1, tile, killer))
                    2 -> world.spawn(GroundItem(ironOreId, 1, tile, killer))
                    3 -> world.spawn(GroundItem(eyeOfNewtId, 1, tile, killer))
                }

                if (world.random(1..128) <= 2) {
                    world.spawn(GroundItem(uncutSapphireId, 1, tile, killer))
                }
            }
        }
    }

    companion object {
        val NPC_IDS = listOf("npc.banshee_414")

        private val pureEssenceId get() = getRSCM("item.pure_essence")
        private val fishingBaitId get() = getRSCM("item.fishing_bait")
        private val coinsId get() = getRSCM("item.coins")
        private val airRuneId get() = getRSCM("item.air_rune")
        private val cosmicRuneId get() = getRSCM("item.cosmic_rune")
        private val chaosRuneId get() = getRSCM("item.chaos_rune")
        private val fireRuneId get() = getRSCM("item.fire_rune")
        private val ironKiteshieldId get() = getRSCM("item.iron_kiteshield")
        private val ironOreId get() = getRSCM("item.iron_ore")
        private val eyeOfNewtId get() = getRSCM("item.eye_of_newt")
        private val uncutSapphireId get() = getRSCM("item.uncut_sapphire")
    }
}
