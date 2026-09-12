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
        val seedTable = WeightedTableBuilder().apply {
            main(weight = 100) {
                add(item = limpwurtSeedId, amount = 1, weight = 25)
                add(item = toadflaxSeedId, amount = 1, weight = 10)
                add(item = iritSeedId, amount = 1, weight = 7)
                add(item = belladonnaSeedId, amount = 1, weight = 6)
                add(item = poisonIvySeedId, amount = 1, weight = 6)
                add(item = avantoeSeedId, amount = 1, weight = 6)
                add(item = cactusSeedId, amount = 1, weight = 6)
                add(item = potatoCactusSeedId, amount = 1, weight = 6)
                add(item = kwuarmSeedId, amount = 1, weight = 6)
                add(item = snapdragonSeedId, amount = 1, weight = 6)
                add(item = cadantineSeedId, amount = 1, weight = 6)
                add(item = lantadymeSeedId, amount = 1, weight = 6)
                add(item = snapeGrassSeedId, amount = 1, weight = 4)
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

            drops {
                always {
                    add(item = maliciousAshesId, amount = 1)
                }

                main(weight = 116) {
                    add(item = coinsId, amount = 5000, weight = 1)
                    add(item = coinsId, min = 3000, max = 3500, weight = 3)
                    add(item = coinsId, min = 2500, max = 2999, weight = 6)
                    add(item = coinsId, min = 1500, max = 2000, weight = 10)
                    add(item = coinsId, min = 1000, max = 1499, weight = 13)
                    add(item = coinsId, min = 500, max = 999, weight = 3)
                }

                main(weight = 116) {
                    add(item = chaosRuneId, amount = 37, weight = 8)
                    add(item = deathRuneId, amount = 5, weight = 6)
                    add(item = deathRuneId, amount = 10, weight = 6)
                    add(item = lawRuneId, min = 25, max = 35, weight = 5)
                    add(item = bloodRuneId, min = 15, max = 20, weight = 4)
                }

                main(weight = 116) {
                    add(item = adamantPlatelegsId, amount = 1, weight = 4)
                    add(item = rune2hSwordId, amount = 1, weight = 4)
                    add(item = runeFullHelmId, amount = 1, weight = 3)
                    add(item = adamantKiteshieldId, amount = 1, weight = 2)
                    add(item = runeBootsId, amount = 1, weight = 1)
                }

                main(weight = 116) {
                    add(item = seedTable, amount = 1, weight = 18)
                }

                main(weight = 116) {
                    add(item = seedTable, amount = 1, weight = 18)
                }

                main(weight = 116) {
                    add(item = tunaId, amount = 1, weight = 3)
                    add(item = softClayId, amount = 25, weight = 4)
                }

                main(weight = 116) {
                    add(item = uncutSapphireId, amount = 1, weight = 5)
                }
            }
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
