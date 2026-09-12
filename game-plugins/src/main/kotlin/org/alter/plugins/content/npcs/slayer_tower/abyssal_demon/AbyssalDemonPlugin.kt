package org.alter.plugins.content.npcs.slayer_tower.abyssal_demon

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
 * Abyssal Demon (Slayer 85) combat definition and drop table.
 */
class AbyssalDemonPlugin(
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
                hitpoints = 150
                attack = 138
                strength = 135
                defence = 140
                magic = 1
                ranged = 1
            }
            bonuses {
                defenceStab = 30
                defenceSlash = 30
                defenceCrush = 30
                defenceMagic = 0
                defenceRanged = 30
            }
            anims {
                attack = Animation.ABYSSAL_DEMON_ATTACK
                block = Animation.ABYSSAL_DEMON_HIT
                death = Animation.ABYSSAL_DEMON_DEATH
            }
            sound {
                attackSound = Sound.ABYSSAL_ATTACK
                deathSound = Sound.ABYSSAL_DEATH
                blockSound = Sound.ABYSSAL_HIT
            }
            slayerData {
                levelRequirement = 85
                xp = 150.0
            }
        }

        NPC_IDS.forEach { npcId ->
            onNpcDeath(npcId) {
                val npc = ctx as? Npc ?: return@onNpcDeath
                val killer = npc.attr[KILLER_ATTR]?.get() as? Player ?: return@onNpcDeath
                val tile = npc.tile

                world.spawn(GroundItem(abyssalAshesId, 1, tile, killer))

                val coinRoll = world.random(1..128)
                when {
                    coinRoll <= 1 -> world.spawn(GroundItem(coinsId, 460, tile, killer))
                    coinRoll <= 10 -> world.spawn(GroundItem(coinsId, 30, tile, killer))
                    coinRoll <= 16 -> world.spawn(GroundItem(coinsId, 44, tile, killer))
                    coinRoll <= 23 -> world.spawn(GroundItem(coinsId, 220, tile, killer))
                    coinRoll <= 58 -> world.spawn(GroundItem(coinsId, 132, tile, killer))
                }

                val runeRoll = world.random(1..128)
                when {
                    runeRoll <= 8 -> world.spawn(GroundItem(airRuneId, 50, tile, killer))
                    runeRoll <= 15 -> world.spawn(GroundItem(chaosRuneId, 10, tile, killer))
                    runeRoll <= 19 -> world.spawn(GroundItem(bloodRuneId, 7, tile, killer))
                    runeRoll <= 20 -> world.spawn(GroundItem(lawRuneId, 3, tile, killer))
                }

                val weaponRoll = world.random(1..128)
                when {
                    weaponRoll <= 4 -> world.spawn(GroundItem(blackSwordId, 1, tile, killer))
                    weaponRoll <= 7 -> world.spawn(GroundItem(steelBattleaxeId, 1, tile, killer))
                    weaponRoll <= 9 -> world.spawn(GroundItem(blackAxeId, 1, tile, killer))
                    weaponRoll <= 10 -> world.spawn(GroundItem(mithrilKiteshieldId, 1, tile, killer))
                    weaponRoll <= 11 -> world.spawn(GroundItem(runeChainbodyId, 1, tile, killer))
                    weaponRoll <= 12 -> world.spawn(GroundItem(runeMedHelmId, 1, tile, killer))
                }

                val otherRoll = world.random(1..128)
                when {
                    otherRoll <= 2 -> world.spawn(GroundItem(lobsterId, 1, tile, killer))
                    otherRoll <= 4 -> world.spawn(GroundItem(adamantiteBarId, 1, tile, killer))
                    otherRoll <= 9 -> world.spawn(GroundItem(pureEssenceId, 60, tile, killer))
                }

                val preRoll = world.random(1..512)
                when (preRoll) {
                    1 -> world.spawn(GroundItem(abyssalWhipId, 1, tile, killer))
                }

                if (world.random(1..128) <= 5) {
                    world.spawn(GroundItem(uncutSapphireId, 1, tile, killer))
                }
            }
        }
    }

    companion object {
        val NPC_IDS = listOf("npc.abyssal_demon_415", "npc.abyssal_demon_416")

        private val abyssalAshesId get() = getRSCM("item.abyssal_ashes")
        private val coinsId get() = getRSCM("item.coins")
        private val airRuneId get() = getRSCM("item.air_rune")
        private val chaosRuneId get() = getRSCM("item.chaos_rune")
        private val bloodRuneId get() = getRSCM("item.blood_rune")
        private val lawRuneId get() = getRSCM("item.law_rune")
        private val blackSwordId get() = getRSCM("item.black_sword")
        private val steelBattleaxeId get() = getRSCM("item.steel_battleaxe")
        private val blackAxeId get() = getRSCM("item.black_axe")
        private val mithrilKiteshieldId get() = getRSCM("item.mithril_kiteshield")
        private val runeChainbodyId get() = getRSCM("item.rune_chainbody")
        private val runeMedHelmId get() = getRSCM("item.rune_med_helm")
        private val lobsterId get() = getRSCM("item.lobster")
        private val adamantiteBarId get() = getRSCM("item.adamantite_bar")
        private val pureEssenceId get() = getRSCM("item.pure_essence")
        private val abyssalWhipId get() = getRSCM("item.abyssal_whip")
        private val uncutSapphireId get() = getRSCM("item.uncut_sapphire")
    }
}
