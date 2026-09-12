package org.alter.plugins.content.npcs.slayer_tower.gargoyle

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
 * Gargoyle (Slayer 75) combat definition and drop table.
 */
class GargoylePlugin(
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
                hitpoints = 115
                attack = 111
                strength = 118
                defence = 120
                magic = 1
                ranged = 1
            }
            bonuses {
                defenceStab = 40
                defenceSlash = 30
                defenceCrush = 20
                defenceMagic = 0
                defenceRanged = 30
            }
            anims {
                attack = Animation.GARGOYLE_ATTACK
                block = Animation.GARGOYLE_HIT
                death = Animation.GARGOYLE_DEATH
            }
            sound {
                attackSound = Sound.GARGOYLE_ATTACK
                deathSound = Sound.GARGOYLE_DEATH
                blockSound = Sound.GARGOYLE_HIT
            }
            slayerData {
                levelRequirement = 75
                xp = 105.0
            }
        }

        NPC_IDS.forEach { npcId ->
            onNpcDeath(npcId) {
                val npc = ctx as? Npc ?: return@onNpcDeath
                val killer = npc.attr[KILLER_ATTR]?.get() as? Player ?: return@onNpcDeath
                val tile = npc.tile

                val coinRoll = world.random(1..128)
                when {
                    coinRoll <= 5 -> world.spawn(GroundItem(coinsId, 10000, tile, killer))
                    coinRoll <= 25 -> world.spawn(GroundItem(coinsId, world.random(400..800), tile, killer))
                    coinRoll <= 53 -> world.spawn(GroundItem(coinsId, world.random(500..1000), tile, killer))
                }

                val runeRoll = world.random(1..128)
                when {
                    runeRoll <= 6 -> world.spawn(GroundItem(fireRuneId, 150, tile, killer))
                    runeRoll <= 16 -> world.spawn(GroundItem(fireRuneId, 75, tile, killer))
                    runeRoll <= 24 -> world.spawn(GroundItem(chaosRuneId, 30, tile, killer))
                    runeRoll <= 29 -> world.spawn(GroundItem(deathRuneId, 15, tile, killer))
                }

                val weaponRoll = world.random(1..128)
                when {
                    weaponRoll <= 4 -> world.spawn(GroundItem(adamantPlatelegsId, 1, tile, killer))
                    weaponRoll <= 7 -> world.spawn(GroundItem(runeFullHelmId, 1, tile, killer))
                    weaponRoll <= 9 -> world.spawn(GroundItem(rune2hSwordId, 1, tile, killer))
                    weaponRoll <= 10 -> world.spawn(GroundItem(adamantBootsId, 1, tile, killer))
                    weaponRoll <= 11 -> world.spawn(GroundItem(runeBattleaxeId, 1, tile, killer))
                    weaponRoll <= 12 -> world.spawn(GroundItem(runePlatelegsId, 1, tile, killer))
                }

                val otherRoll = world.random(1..128)
                when {
                    otherRoll <= 2 -> world.spawn(GroundItem(runiteOreId, 1, tile, killer))
                    otherRoll <= 4 -> world.spawn(GroundItem(mithrilBarId, 15, tile, killer))
                    otherRoll <= 7 -> world.spawn(GroundItem(goldBarId, world.random(10..15), tile, killer))
                    otherRoll <= 13 -> world.spawn(GroundItem(steelBarId, 15, tile, killer))
                    otherRoll <= 19 -> world.spawn(GroundItem(pureEssenceId, 150, tile, killer))
                    otherRoll <= 29 -> world.spawn(GroundItem(goldOreId, world.random(10..20), tile, killer))
                }

                val preRoll = world.random(1..512)
                when (preRoll) {
                    1 -> world.spawn(GroundItem(graniteMaulId, 1, tile, killer))
                    2 -> world.spawn(GroundItem(mysticRobeTopDarkId, 1, tile, killer))
                }

                if (world.random(1..128) <= 5) {
                    world.spawn(GroundItem(uncutSapphireId, 1, tile, killer))
                }
            }
        }
    }

    companion object {
        val NPC_IDS = listOf("npc.gargoyle_412", "npc.gargoyle_413")

        private val coinsId get() = getRSCM("item.coins")
        private val fireRuneId get() = getRSCM("item.fire_rune")
        private val chaosRuneId get() = getRSCM("item.chaos_rune")
        private val deathRuneId get() = getRSCM("item.death_rune")
        private val adamantPlatelegsId get() = getRSCM("item.adamant_platelegs")
        private val runeFullHelmId get() = getRSCM("item.rune_full_helm")
        private val rune2hSwordId get() = getRSCM("item.rune_2h_sword")
        private val adamantBootsId get() = getRSCM("item.adamant_boots")
        private val runeBattleaxeId get() = getRSCM("item.rune_battleaxe")
        private val runePlatelegsId get() = getRSCM("item.rune_platelegs")
        private val runiteOreId get() = getRSCM("item.runite_ore")
        private val mithrilBarId get() = getRSCM("item.mithril_bar")
        private val goldBarId get() = getRSCM("item.gold_bar")
        private val steelBarId get() = getRSCM("item.steel_bar")
        private val pureEssenceId get() = getRSCM("item.pure_essence")
        private val goldOreId get() = getRSCM("item.gold_ore")
        private val graniteMaulId get() = getRSCM("item.granite_maul")
        private val mysticRobeTopDarkId get() = getRSCM("item.mystic_robe_top_dark")
        private val uncutSapphireId get() = getRSCM("item.uncut_sapphire")
    }
}
