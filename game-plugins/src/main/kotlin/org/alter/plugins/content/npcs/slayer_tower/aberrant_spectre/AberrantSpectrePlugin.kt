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
        val herbTable = WeightedTableBuilder().apply {
            main(weight = 26) {
                add(item = guamLeafId, min = 1, max = 3, weight = 2)
                add(item = marrentillId, min = 1, max = 3, weight = 2)
                add(item = tarrominId, min = 1, max = 3, weight = 2)
                add(item = harralanderId, min = 1, max = 3, weight = 2)
                add(item = ranarrWeedId, min = 1, max = 3, weight = 2)
                add(item = iritLeafId, min = 1, max = 3, weight = 2)
                add(item = avantoeId, min = 1, max = 3, weight = 2)
                add(item = kwuarmId, min = 1, max = 3, weight = 2)
                add(item = cadantineId, min = 1, max = 3, weight = 2)
                add(item = lantadymeId, min = 1, max = 3, weight = 2)
                add(item = dwarfWeedId, min = 1, max = 3, weight = 6)
            }
        }.LootTables.single()

        val spectreSeedTable = WeightedTableBuilder().apply {
            main(weight = 14) {
                add(item = toadflaxSeedId, amount = 1, weight = 1)
                add(item = iritSeedId, amount = 1, weight = 1)
                add(item = belladonnaSeedId, amount = 1, weight = 1)
                add(item = poisonIvySeedId, amount = 1, weight = 1)
                add(item = avantoeSeedId, amount = 1, weight = 1)
                add(item = cactusSeedId, amount = 1, weight = 1)
                add(item = potatoCactusSeedId, amount = 1, weight = 1)
                add(item = kwuarmSeedId, amount = 1, weight = 1)
                add(item = snapdragonSeedId, amount = 1, weight = 1)
                add(item = cadantineSeedId, amount = 1, weight = 1)
                add(item = lantadymeSeedId, amount = 1, weight = 1)
                add(item = snapeGrassSeedId, amount = 1, weight = 1)
                add(item = dwarfWeedSeedId, amount = 1, weight = 1)
                add(item = torstolSeedId, amount = 1, weight = 1)
            }
        }.LootTables.single()

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

            drops {
                main(weight = 128) {
                    add(item = herbTable, amount = 1, weight = 78)
                }

                main(weight = 128) {
                    add(item = spectreSeedTable, amount = 1, weight = 19)
                }

                main(weight = 128) {
                    add(item = steelAxeId, amount = 1, weight = 3)
                    add(item = mithrilKiteshieldId, amount = 1, weight = 1)
                    add(item = lavaBattlestaffId, amount = 1, weight = 1)
                    add(item = adamantPlatelegsId, amount = 1, weight = 1)
                    add(item = runeFullHelmId, amount = 1, weight = 1)
                }

                main(weight = 512) {
                    add(item = mysticRobeBottomDarkId, amount = 1, weight = 1)
                }

                main(weight = 128) {
                    add(item = coinsId, amount = 460, weight = 1)
                }

                main(weight = 128) {
                    add(item = uncutSapphireId, amount = 1, weight = 5)
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
