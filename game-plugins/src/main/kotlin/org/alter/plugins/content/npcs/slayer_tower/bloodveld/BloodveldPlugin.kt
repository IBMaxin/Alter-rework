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

            drops {
                always {
                    add(item = vileAshesId, amount = 1)
                }

                main(weight = 128) {
                    add(item = coinsId, amount = 450, weight = 1)
                    add(item = coinsId, amount = 10, weight = 10)
                    add(item = coinsId, amount = 40, weight = 29)
                    add(item = coinsId, amount = 120, weight = 30)
                    add(item = coinsId, amount = 200, weight = 10)
                }

                main(weight = 128) {
                    add(item = fireRuneId, amount = 60, weight = 8)
                    add(item = bloodRuneId, amount = 3, weight = 3)
                    add(item = bloodRuneId, amount = 10, weight = 5)
                    add(item = bloodRuneId, amount = 30, weight = 1)
                }

                main(weight = 128) {
                    add(item = steelAxeId, amount = 1, weight = 4)
                    add(item = steelFullHelmId, amount = 1, weight = 4)
                    add(item = steelScimitarId, amount = 1, weight = 2)
                    add(item = blackBootsId, amount = 1, weight = 1)
                    add(item = mithrilSqShieldId, amount = 1, weight = 1)
                    add(item = mithrilChainbodyId, amount = 1, weight = 1)
                    add(item = runeMedHelmId, amount = 1, weight = 1)
                }

                main(weight = 128) {
                    add(item = meatPizzaId, amount = 1, weight = 3)
                    add(item = goldOreId, amount = 1, weight = 2)
                    add(item = bigBonesId, amount = 1, weight = 10)
                    add(item = bigBonesId, amount = 3, weight = 3)
                    add(item = bonesId, amount = 1, weight = 7)
                }

                main(weight = 32) {
                    add(item = uncutSapphireId, amount = 1, weight = 1)
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
