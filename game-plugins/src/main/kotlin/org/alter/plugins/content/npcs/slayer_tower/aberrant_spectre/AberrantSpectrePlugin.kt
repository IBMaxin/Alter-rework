package org.alter.plugins.content.npcs.slayer_tower.aberrant_spectre

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
 * Aberrant Spectre (Slayer 60) combat definition and drop table.
 */
class AberrantSpectrePlugin(
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
                hitpoints = 96
                attack = 86
                strength = 84
                defence = 80
                magic = 1
                ranged = 1
            }
            bonuses {
                defenceStab = 0
                defenceSlash = 0
                defenceCrush = 0
                defenceMagic = 30
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
                levelRequirement = 60
                xp = 90.0
            }
        }

        NPC_IDS.forEach { npcId ->
            onNpcDeath(npcId) {
                val npc = ctx as? Npc ?: return@onNpcDeath
                val killer = npc.attr[KILLER_ATTR]?.get() as? Player ?: return@onNpcDeath
                val tile = npc.tile

                if (world.random(1..128) <= 78) {
                    val herbCount = world.random(1..3)
                    val herbId = when (world.random(1..26)) {
                        in 1..2 -> guamLeafId
                        in 3..4 -> marrentillId
                        in 5..6 -> tarrominId
                        in 7..8 -> harralanderId
                        in 9..10 -> ranarrWeedId
                        in 11..12 -> iritLeafId
                        in 13..14 -> avantoeId
                        in 15..16 -> kwuarmId
                        in 17..18 -> cadantineId
                        in 19..20 -> lantadymeId
                        else -> dwarfWeedId
                    }
                    world.spawn(GroundItem(herbId, herbCount, tile, killer))
                }

                if (world.random(1..128) <= 19) {
                    val seedId = when (world.random(1..14)) {
                        1 -> toadflaxSeedId
                        2 -> iritSeedId
                        3 -> belladonnaSeedId
                        4 -> poisonIvySeedId
                        5 -> avantoeSeedId
                        6 -> cactusSeedId
                        7 -> potatoCactusSeedId
                        8 -> kwuarmSeedId
                        9 -> snapdragonSeedId
                        10 -> cadantineSeedId
                        11 -> lantadymeSeedId
                        12 -> snapeGrassSeedId
                        13 -> dwarfWeedSeedId
                        else -> torstolSeedId
                    }
                    world.spawn(GroundItem(seedId, 1, tile, killer))
                }

                val weaponRoll = world.random(1..128)
                when {
                    weaponRoll <= 3 -> world.spawn(GroundItem(steelAxeId, 1, tile, killer))
                    weaponRoll <= 4 -> world.spawn(GroundItem(mithrilKiteshieldId, 1, tile, killer))
                    weaponRoll <= 5 -> world.spawn(GroundItem(lavaBattlestaffId, 1, tile, killer))
                    weaponRoll <= 6 -> world.spawn(GroundItem(adamantPlatelegsId, 1, tile, killer))
                    weaponRoll <= 7 -> world.spawn(GroundItem(runeFullHelmId, 1, tile, killer))
                }

                if (world.random(1..512) == 1) {
                    world.spawn(GroundItem(mysticRobeBottomDarkId, 1, tile, killer))
                }

                if (world.random(1..128) == 1) {
                    world.spawn(GroundItem(coinsId, 460, tile, killer))
                }

                if (world.random(1..128) <= 5) {
                    world.spawn(GroundItem(uncutSapphireId, 1, tile, killer))
                }
            }
        }
    }

    companion object {
        val NPC_IDS = listOf(
            "npc.aberrant_spectre_2", "npc.aberrant_spectre_3", "npc.aberrant_spectre_4",
            "npc.aberrant_spectre_5", "npc.aberrant_spectre_6", "npc.aberrant_spectre_7",
        )

        private val guamLeafId get() = getRSCM("item.grimy_guam_leaf")
        private val marrentillId get() = getRSCM("item.grimy_marrentill")
        private val tarrominId get() = getRSCM("item.grimy_tarromin")
        private val harralanderId get() = getRSCM("item.grimy_harralander")
        private val ranarrWeedId get() = getRSCM("item.grimy_ranarr_weed")
        private val iritLeafId get() = getRSCM("item.grimy_irit_leaf")
        private val avantoeId get() = getRSCM("item.grimy_avantoe")
        private val kwuarmId get() = getRSCM("item.grimy_kwuarm")
        private val cadantineId get() = getRSCM("item.grimy_cadantine")
        private val lantadymeId get() = getRSCM("item.grimy_lantadyme")
        private val dwarfWeedId get() = getRSCM("item.grimy_dwarf_weed")

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
        private val torstolSeedId get() = getRSCM("item.torstol_seed")

        private val steelAxeId get() = getRSCM("item.steel_axe")
        private val mithrilKiteshieldId get() = getRSCM("item.mithril_kiteshield")
        private val lavaBattlestaffId get() = getRSCM("item.lava_battlestaff")
        private val adamantPlatelegsId get() = getRSCM("item.adamant_platelegs")
        private val runeFullHelmId get() = getRSCM("item.rune_full_helm")
        private val mysticRobeBottomDarkId get() = getRSCM("item.mystic_robe_bottom_dark")
        private val coinsId get() = getRSCM("item.coins")
        private val uncutSapphireId get() = getRSCM("item.uncut_sapphire")
    }
}
