package org.alter.plugins.content.npcs.slayer_tower.bloodveld

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
 * Bloodveld (Slayer 50) combat definition and drop table.
 *
 * Covers IDs 484-487 so all spawned tower bloodvelds are registered.
 */
class BloodveldPlugin(
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
                hitpoints = 124
                attack = 96
                strength = 110
                defence = 100
                magic = 1
                ranged = 1
            }
            bonuses {
                defenceStab = 0
                defenceSlash = 20
                defenceCrush = 0
                defenceMagic = 0
                defenceRanged = 0
            }
            anims {
                attack = Animation.BLOODVELD_ATTACK
                block = Animation.BLOODVELD_HIT
                death = Animation.BLOODVELD_DEATH
            }
            sound {
                attackSound = Sound.BLOODVELD_ATTACK
                deathSound = Sound.BLOODVELD_DEATH
                blockSound = Sound.BLOODVELD_HIT
            }
            slayerData {
                levelRequirement = 50
                xp = 120.0
            }
        }

        NPC_IDS.forEach { npcId ->
            onNpcDeath(npcId) {
                val npc = ctx as? Npc ?: return@onNpcDeath
                val killer = npc.attr[KILLER_ATTR]?.get() as? Player ?: return@onNpcDeath
                val tile = npc.tile

                world.spawn(GroundItem(vileAshesId, 1, tile, killer))

                val coinRoll = world.random(1..128)
                when {
                    coinRoll <= 1 -> world.spawn(GroundItem(coinsId, 450, tile, killer))
                    coinRoll <= 11 -> world.spawn(GroundItem(coinsId, 10, tile, killer))
                    coinRoll <= 40 -> world.spawn(GroundItem(coinsId, 40, tile, killer))
                    coinRoll <= 70 -> world.spawn(GroundItem(coinsId, 120, tile, killer))
                    coinRoll <= 80 -> world.spawn(GroundItem(coinsId, 200, tile, killer))
                }

                val runeRoll = world.random(1..128)
                when {
                    runeRoll <= 8 -> world.spawn(GroundItem(fireRuneId, 60, tile, killer))
                    runeRoll <= 11 -> world.spawn(GroundItem(bloodRuneId, 3, tile, killer))
                    runeRoll <= 16 -> world.spawn(GroundItem(bloodRuneId, 10, tile, killer))
                    runeRoll <= 17 -> world.spawn(GroundItem(bloodRuneId, 30, tile, killer))
                }

                val weaponRoll = world.random(1..128)
                when {
                    weaponRoll <= 4 -> world.spawn(GroundItem(steelAxeId, 1, tile, killer))
                    weaponRoll <= 8 -> world.spawn(GroundItem(steelFullHelmId, 1, tile, killer))
                    weaponRoll <= 10 -> world.spawn(GroundItem(steelScimitarId, 1, tile, killer))
                    weaponRoll <= 11 -> world.spawn(GroundItem(blackBootsId, 1, tile, killer))
                    weaponRoll <= 12 -> world.spawn(GroundItem(mithrilSqShieldId, 1, tile, killer))
                    weaponRoll <= 13 -> world.spawn(GroundItem(mithrilChainbodyId, 1, tile, killer))
                    weaponRoll <= 14 -> world.spawn(GroundItem(runeMedHelmId, 1, tile, killer))
                }

                val otherRoll = world.random(1..128)
                when {
                    otherRoll <= 3 -> world.spawn(GroundItem(meatPizzaId, 1, tile, killer))
                    otherRoll <= 5 -> world.spawn(GroundItem(goldOreId, 1, tile, killer))
                    otherRoll <= 15 -> world.spawn(GroundItem(bigBonesId, 1, tile, killer))
                    otherRoll <= 18 -> world.spawn(GroundItem(bigBonesId, 3, tile, killer))
                    otherRoll <= 25 -> world.spawn(GroundItem(bonesId, 1, tile, killer))
                }

                if (world.random(1..32) == 1) {
                    world.spawn(GroundItem(uncutSapphireId, 1, tile, killer))
                }
            }
        }
    }

    companion object {
        val NPC_IDS = listOf(
            "npc.bloodveld_484", "npc.bloodveld_485", "npc.bloodveld_486", "npc.bloodveld_487",
        )

        private val vileAshesId get() = getRSCM("item.vile_ashes")
        private val coinsId get() = getRSCM("item.coins")
        private val fireRuneId get() = getRSCM("item.fire_rune")
        private val bloodRuneId get() = getRSCM("item.blood_rune")
        private val steelAxeId get() = getRSCM("item.steel_axe")
        private val steelFullHelmId get() = getRSCM("item.steel_full_helm")
        private val steelScimitarId get() = getRSCM("item.steel_scimitar")
        private val blackBootsId get() = getRSCM("item.black_boots")
        private val mithrilSqShieldId get() = getRSCM("item.mithril_sq_shield")
        private val mithrilChainbodyId get() = getRSCM("item.mithril_chainbody")
        private val runeMedHelmId get() = getRSCM("item.rune_med_helm")
        private val meatPizzaId get() = getRSCM("item.meat_pizza")
        private val goldOreId get() = getRSCM("item.gold_ore")
        private val bigBonesId get() = getRSCM("item.big_bones")
        private val bonesId get() = getRSCM("item.bones")
        private val uncutSapphireId get() = getRSCM("item.uncut_sapphire")
    }
}
