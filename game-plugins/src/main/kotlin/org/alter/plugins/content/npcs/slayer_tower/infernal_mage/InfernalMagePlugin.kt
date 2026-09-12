package org.alter.plugins.content.npcs.slayer_tower.infernal_mage

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
 * Infernal Mage (Slayer 45) combat definition and drop table.
 */
class InfernalMagePlugin(
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
                hitpoints = 64
                attack = 68
                strength = 65
                defence = 60
                magic = 80
                ranged = 1
            }
            bonuses {
                defenceStab = 0
                defenceSlash = 0
                defenceCrush = 0
                defenceMagic = 20
                defenceRanged = 0
            }
            anims {
                attack = Animation.SPECTRE_ATTACK
                block = Animation.SPECTRE_HIT
                death = Animation.SPECTRE_DEATH
            }
            sound {
                attackSound = Sound.SPECTRE_ATTACK
                deathSound = Sound.SPECTRE_DEATH
                blockSound = Sound.SPECTRE_HIT
            }
            slayerData {
                levelRequirement = 45
                xp = 60.0
            }
        }

        NPC_IDS.forEach { npcId ->
            onNpcDeath(npcId) {
                val npc = ctx as? Npc ?: return@onNpcDeath
                val killer = npc.attr[KILLER_ATTR]?.get() as? Player ?: return@onNpcDeath
                val tile = npc.tile

                world.spawn(GroundItem(bonesId, 1, tile, killer))

                if (world.random(1..7) == 1) {
                    world.spawn(GroundItem(deathRuneId, 7, tile, killer))
                }

                val staffRoll = world.random(1..128)
                when {
                    staffRoll == 1 -> world.spawn(GroundItem(staffOfFireId, 1, tile, killer))
                    staffRoll <= 9 -> world.spawn(GroundItem(staffId, 1, tile, killer))
                }

                val elemRoll = world.random(1..128)
                when {
                    elemRoll <= 5 -> world.spawn(GroundItem(earthRuneId, 36, tile, killer))
                    elemRoll <= 10 -> world.spawn(GroundItem(earthRuneId, 10, tile, killer))
                    elemRoll <= 15 -> world.spawn(GroundItem(fireRuneId, 10, tile, killer))
                    elemRoll <= 20 -> world.spawn(GroundItem(airRuneId, 10, tile, killer))
                    elemRoll <= 25 -> world.spawn(GroundItem(waterRuneId, 10, tile, killer))
                    elemRoll <= 30 -> world.spawn(GroundItem(airRuneId, 18, tile, killer))
                    elemRoll <= 35 -> world.spawn(GroundItem(waterRuneId, 18, tile, killer))
                    elemRoll <= 40 -> world.spawn(GroundItem(earthRuneId, 18, tile, killer))
                    elemRoll <= 45 -> world.spawn(GroundItem(fireRuneId, 18, tile, killer))
                }

                val cataRoll = world.random(1..128)
                when {
                    cataRoll <= 18 -> world.spawn(GroundItem(mindRuneId, 18, tile, killer))
                    cataRoll <= 36 -> world.spawn(GroundItem(bodyRuneId, 18, tile, killer))
                    cataRoll <= 40 -> world.spawn(GroundItem(bloodRuneId, 4, tile, killer))
                }

                val preRoll = world.random(1..1000)
                when (preRoll) {
                    in 1..2 -> world.spawn(GroundItem(mysticBootsDarkId, 1, tile, killer))
                    in 3..4 -> world.spawn(GroundItem(mysticHatDarkId, 1, tile, killer))
                    in 5..6 -> world.spawn(GroundItem(lavaBattlestaffId, 1, tile, killer))
                }
            }
        }
    }

    companion object {
        val NPC_IDS = listOf(
            "npc.infernal_mage_443", "npc.infernal_mage_444", "npc.infernal_mage_445",
            "npc.infernal_mage_446", "npc.infernal_mage_447",
        )

        private val bonesId get() = getRSCM("item.bones")
        private val deathRuneId get() = getRSCM("item.death_rune")
        private val staffOfFireId get() = getRSCM("item.staff_of_fire")
        private val staffId get() = getRSCM("item.staff")
        private val earthRuneId get() = getRSCM("item.earth_rune")
        private val fireRuneId get() = getRSCM("item.fire_rune")
        private val airRuneId get() = getRSCM("item.air_rune")
        private val waterRuneId get() = getRSCM("item.water_rune")
        private val mindRuneId get() = getRSCM("item.mind_rune")
        private val bodyRuneId get() = getRSCM("item.body_rune")
        private val bloodRuneId get() = getRSCM("item.blood_rune")
        private val mysticBootsDarkId get() = getRSCM("item.mystic_boots_dark")
        private val mysticHatDarkId get() = getRSCM("item.mystic_hat_dark")
        private val lavaBattlestaffId get() = getRSCM("item.lava_battlestaff")
    }
}
