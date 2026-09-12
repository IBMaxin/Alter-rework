package org.alter.plugins.content.npcs.slayer_tower.nechryael

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
 * Nechryael (Slayer 80) combat definition and drop table.
 */
class NechryaelPlugin(
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
                hitpoints = 175
                attack = 140
                strength = 150
                defence = 140
                magic = 1
                ranged = 1
            }
            bonuses {
                defenceStab = 20
                defenceSlash = 20
                defenceCrush = 20
                defenceMagic = 0
                defenceRanged = 20
            }
            anims {
                attack = Animation.NECHRYAEL_ATTACK
                block = Animation.NECHRYAEL_HIT
                death = Animation.NECHRYAEL_DEATH
            }
            slayerData {
                levelRequirement = 80
                xp = 105.0
            }
        }

        NPC_IDS.forEach { npcId ->
            onNpcDeath(npcId) {
                val npc = ctx as? Npc ?: return@onNpcDeath
                val killer = npc.attr[KILLER_ATTR]?.get() as? Player ?: return@onNpcDeath
                val tile = npc.tile

                world.spawn(GroundItem(maliciousAshesId, 1, tile, killer))

                val coinRoll = world.random(1..116)
                when {
                    coinRoll <= 1 -> world.spawn(GroundItem(coinsId, 5000, tile, killer))
                    coinRoll <= 4 -> world.spawn(GroundItem(coinsId, world.random(3000..3500), tile, killer))
                    coinRoll <= 10 -> world.spawn(GroundItem(coinsId, world.random(2500..2999), tile, killer))
                    coinRoll <= 20.5 -> world.spawn(GroundItem(coinsId, world.random(1500..2000), tile, killer))
                    coinRoll <= 33.5 -> world.spawn(GroundItem(coinsId, world.random(1000..1499), tile, killer))
                    coinRoll <= 36 -> world.spawn(GroundItem(coinsId, world.random(500..999), tile, killer))
                }

                val runeRoll = world.random(1..116)
                when {
                    runeRoll <= 8 -> world.spawn(GroundItem(chaosRuneId, 37, tile, killer))
                    runeRoll <= 14 -> world.spawn(GroundItem(deathRuneId, 5, tile, killer))
                    runeRoll <= 20 -> world.spawn(GroundItem(deathRuneId, 10, tile, killer))
                    runeRoll <= 25 -> world.spawn(GroundItem(lawRuneId, world.random(25..35), tile, killer))
                    runeRoll <= 29 -> world.spawn(GroundItem(bloodRuneId, world.random(15..20), tile, killer))
                }

                val weaponRoll = world.random(1..116)
                when {
                    weaponRoll <= 4 -> world.spawn(GroundItem(adamantPlatelegsId, 1, tile, killer))
                    weaponRoll <= 8 -> world.spawn(GroundItem(rune2hSwordId, 1, tile, killer))
                    weaponRoll <= 11 -> world.spawn(GroundItem(runeFullHelmId, 1, tile, killer))
                    weaponRoll <= 13 -> world.spawn(GroundItem(adamantKiteshieldId, 1, tile, killer))
                    weaponRoll <= 14 -> world.spawn(GroundItem(runeBootsId, 1, tile, killer))
                }

                if (world.random(1..116) <= 18) {
                    world.spawn(GroundItem(rollNechryaelSeed(), 1, tile, killer))
                    world.spawn(GroundItem(rollNechryaelSeed(), 1, tile, killer))
                }

                val otherRoll = world.random(1..116)
                when {
                    otherRoll <= 3 -> world.spawn(GroundItem(tunaId, 1, tile, killer))
                    otherRoll <= 7 -> world.spawn(GroundItem(softClayId, 25, tile, killer))
                }

                if (world.random(1..116) <= 5) {
                    world.spawn(GroundItem(uncutSapphireId, 1, tile, killer))
                }
            }
        }
    }

    private fun rollNechryaelSeed(): Int {
        return when (world.random(1..100)) {
            in 1..25 -> limpwurtSeedId
            in 26..35 -> toadflaxSeedId
            in 36..42 -> iritSeedId
            in 43..48 -> belladonnaSeedId
            in 49..54 -> poisonIvySeedId
            in 55..60 -> avantoeSeedId
            in 61..66 -> cactusSeedId
            in 67..72 -> potatoCactusSeedId
            in 73..78 -> kwuarmSeedId
            in 79..84 -> snapdragonSeedId
            in 85..90 -> cadantineSeedId
            in 91..96 -> lantadymeSeedId
            in 97..100 -> snapeGrassSeedId
            else -> dwarfWeedSeedId
        }
    }

    companion object {
        val NPC_IDS = listOf("npc.nechryael_8", "npc.nechryael_11")

        private val maliciousAshesId get() = getRSCM("item.malicious_ashes")
        private val coinsId get() = getRSCM("item.coins")
        private val chaosRuneId get() = getRSCM("item.chaos_rune")
        private val deathRuneId get() = getRSCM("item.death_rune")
        private val lawRuneId get() = getRSCM("item.law_rune")
        private val bloodRuneId get() = getRSCM("item.blood_rune")
        private val adamantPlatelegsId get() = getRSCM("item.adamant_platelegs")
        private val rune2hSwordId get() = getRSCM("item.rune_2h_sword")
        private val runeFullHelmId get() = getRSCM("item.rune_full_helm")
        private val adamantKiteshieldId get() = getRSCM("item.adamant_kiteshield")
        private val runeBootsId get() = getRSCM("item.rune_boots")
        private val tunaId get() = getRSCM("item.tuna")
        private val softClayId get() = getRSCM("item.soft_clay")
        private val uncutSapphireId get() = getRSCM("item.uncut_sapphire")

        private val limpwurtSeedId get() = getRSCM("item.limpwurt_seed")
        private val toadflaxSeedId get() = getRSCM("item.toadflax_seed")
        private val iritSeedId get() = getRSCM("item.irit_seed")
        private val belladonnaSeedId get() = getRSCM("item.belladonna_seed")
        private val poisonIvySeedId get() = getRSCM("item.poison_ivy_seed")
        private val avantoeSeedId get() = getRSCM("item.avantoe_seed")
        private val cactusSeedId get() = getRSCM("item.cactus_seed")
        private val potatoCactusSeedId get() = getRSCM("item.potato_cactus_seed")
        private val kwuarmSeedId get() = getRSCM("item.kwuarm_seed")
        private val snapdragonSeedId get() = getRSCM("item.snapdragon_seed")
        private val cadantineSeedId get() = getRSCM("item.cadantine_seed")
        private val lantadymeSeedId get() = getRSCM("item.lantadyme_seed")
        private val snapeGrassSeedId get() = getRSCM("item.snape_grass_seed")
        private val dwarfWeedSeedId get() = getRSCM("item.dwarf_weed_seed")
    }
}
