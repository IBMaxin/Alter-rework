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

            drops {
                main(weight = 128) {
                    add(item = pureEssenceId, amount = 13, weight = 11)
                    add(item = fishingBaitId, amount = 15, weight = 11)
                    add(item = fishingBaitId, amount = 7, weight = 5)
                    add(item = coinsId, amount = 13, weight = 5)
                    add(item = coinsId, amount = 35, weight = 15)
                }

                main(weight = 128) {
                    add(item = airRuneId, amount = 3, weight = 3)
                    add(item = cosmicRuneId, amount = 2, weight = 3)
                    add(item = chaosRuneId, amount = 3, weight = 2)
                    add(item = fireRuneId, amount = 7, weight = 1)
                    add(item = chaosRuneId, amount = 7, weight = 1)
                }

                main(weight = 128) {
                    add(item = ironKiteshieldId, amount = 1, weight = 1)
                    add(item = ironOreId, amount = 1, weight = 1)
                    add(item = eyeOfNewtId, amount = 1, weight = 1)
                }

                main(weight = 128) {
                    add(item = uncutSapphireId, amount = 1, weight = 2)
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
