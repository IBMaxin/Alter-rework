package org.alter.plugins.content.npcs.slayer_tower.crawling_hand

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
 * Crawling Hand (Slayer 5) combat definition and drop table.
 */
class CrawlingHandPlugin(
    r: PluginRepository,
    world: World,
    server: Server,
) : KotlinPlugin(r, world, server) {

    init {
        setCombatDef(*NPC_IDS.toTypedArray()) {
            configs {
                attackSpeed = 3
                respawnDelay = 25
            }
            aggro {
                radius = 4
                searchDelay = 1
            }
            stats {
                hitpoints = 13
                attack = 10
                strength = 10
                defence = 10
                magic = 1
                ranged = 1
            }
            bonuses {
                defenceStab = 0
                defenceSlash = 0
                defenceCrush = 0
                defenceMagic = 0
                defenceRanged = 0
            }
            anims {
                attack = Animation.CRAWLING_HAND_ATTACK
                block = Animation.CRAWLING_HAND_HIT
                death = Animation.CRAWLING_HAND_DEATH
            }
            slayerData {
                levelRequirement = 5
                xp = 12.0
            }

            drops {
                always {
                    add(item = bonesId, amount = 1)
                }

                main(weight = 128) {
                    add(item = leatherGlovesId, amount = 1, weight = 21)
                    add(item = coinsId, amount = 8, weight = 21)
                    add(item = coinsId, amount = 5, weight = 21)
                }

                main(weight = 128) {
                    add(item = goldRingId, amount = 1, weight = 1)
                    add(item = tealGlovesId, amount = 1, weight = 1)
                    add(item = purpleGlovesId, amount = 1, weight = 1)
                    add(item = redGlovesId, amount = 1, weight = 1)
                    add(item = yellowGlovesId, amount = 1, weight = 1)
                    add(item = sapphireRingId, amount = 1, weight = 1)
                    add(item = emeraldRingId, amount = 1, weight = 1)
                }

                main(weight = 128) {
                    add(item = uncutSapphireId, amount = 1, weight = 2)
                }
            }
        }
    }

    companion object {
        val NPC_IDS = listOf(
            "npc.crawling_hand_448", "npc.crawling_hand_449", "npc.crawling_hand_450",
            "npc.crawling_hand_451", "npc.crawling_hand_452",
            "npc.crawling_hand_453", "npc.crawling_hand_454",
        )

        private val bonesId get() = getRSCM("item.bones")
        private val leatherGlovesId get() = getRSCM("item.leather_gloves")
        private val coinsId get() = getRSCM("item.coins")
        private val goldRingId get() = getRSCM("item.gold_ring")
        private val tealGlovesId get() = getRSCM("item.teal_gloves")
        private val purpleGlovesId get() = getRSCM("item.purple_gloves")
        private val redGlovesId get() = getRSCM("item.red_gloves")
        private val yellowGlovesId get() = getRSCM("item.yellow_gloves")
        private val sapphireRingId get() = getRSCM("item.sapphire_ring")
        private val emeraldRingId get() = getRSCM("item.emerald_ring")
        private val uncutSapphireId get() = getRSCM("item.uncut_sapphire")
    }
}
