package org.alter.plugins.content.npcs.slayer_tower.infernal_mage

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
 * Infernal Mage (Slayer 45) combat definition and drop table.
 */
class InfernalMagePlugin(
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
                hitpoints = 64
                attack = 68
                strength = 65
                defence = 60
                magic = 80
                ranged = 1
            }
            bonuses {
                defenceStab = 0
                defenceSlash = 0
                defenceCrush = 0
                defenceMagic = 20
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
                levelRequirement = 45
                xp = 60.0
            }

            drops {
                always {
                    add(item = bonesId, amount = 1)
                }

                main(weight = 7) {
                    add(item = deathRuneId, amount = 7, weight = 1)
                }

                main(weight = 128) {
                    add(item = staffOfFireId, amount = 1, weight = 1)
                    add(item = staffId, amount = 1, weight = 8)
                }

                main(weight = 128) {
                    add(item = earthRuneId, amount = 36, weight = 5)
                    add(item = earthRuneId, amount = 10, weight = 5)
                    add(item = fireRuneId, amount = 10, weight = 5)
                    add(item = airRuneId, amount = 10, weight = 5)
                    add(item = waterRuneId, amount = 10, weight = 5)
                    add(item = airRuneId, amount = 18, weight = 5)
                    add(item = waterRuneId, amount = 18, weight = 5)
                    add(item = earthRuneId, amount = 18, weight = 5)
                    add(item = fireRuneId, amount = 18, weight = 5)
                }

                main(weight = 128) {
                    add(item = mindRuneId, amount = 18, weight = 18)
                    add(item = bodyRuneId, amount = 18, weight = 18)
                    add(item = bloodRuneId, amount = 4, weight = 4)
                }

                main(weight = 1000) {
                    add(item = mysticBootsDarkId, amount = 1, weight = 2)
                    add(item = mysticHatDarkId, amount = 1, weight = 2)
                    add(item = lavaBattlestaffId, amount = 1, weight = 2)
                }
            }
        }
    }

    companion object {
        val NPC_IDS = listOf(
            "npc.infernal_mage_443", "npc.infernal_mage_444", "npc.infernal_mage_445",
            "npc.infernal_mage_446", "npc.infernal_mage_447",
        )

        private val bonesId get() = getRSCM("item.bones")
        private val deathRuneId get() = getRSCM("item.death_rune")
        private val staffOfFireId get() = getRSCM("item.staff_of_fire")
        private val staffId get() = getRSCM("item.staff")
        private val earthRuneId get() = getRSCM("item.earth_rune")
        private val fireRuneId get() = getRSCM("item.fire_rune")
        private val airRuneId get() = getRSCM("item.air_rune")
        private val waterRuneId get() = getRSCM("item.water_rune")
        private val mindRuneId get() = getRSCM("item.mind_rune")
        private val bodyRuneId get() = getRSCM("item.body_rune")
        private val bloodRuneId get() = getRSCM("item.blood_rune")
        private val mysticBootsDarkId get() = getRSCM("item.mystic_boots_dark")
        private val mysticHatDarkId get() = getRSCM("item.mystic_hat_dark")
        private val lavaBattlestaffId get() = getRSCM("item.lava_battlestaff")
    }
}
