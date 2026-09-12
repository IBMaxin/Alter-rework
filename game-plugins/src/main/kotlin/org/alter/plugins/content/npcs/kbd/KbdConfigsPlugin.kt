package org.alter.plugins.content.npcs.kbd

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

class KbdConfigsPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {
        
    init {
        setMultiCombatRegion(region = 9033)

        setCombatDef(NPC_ID) {
            species {
                +NpcSpecies.DRACONIC
                +NpcSpecies.BASIC_DRAGON
            }

            configs {
                attackSpeed = 3
                respawnDelay = 50
            }

            aggro {
                radius = 16
                searchDelay = 1
            }

            stats {
                hitpoints = 240
                attack = 240
                strength = 240
                defence = 240
                magic = 240
            }

            bonuses {
                defenceStab = 70
                defenceSlash = 90
                defenceCrush = 90
                defenceMagic = 80
                defenceRanged = 70
            }

            anims {
                block = 89
                death = 92
            }

            //slayerData {
            // /**
            //  * @TODO Bug : Currently mobs don't aggro player if he does not have slayer level
            //  */
            //    levelRequirement = 50
            //    xp = 258.0
            //}

            drops {
                always {
                    add(item = dragonBonesId, amount = 1)
                    add(item = blackDragonLeatherId, amount = 1)
                }

                main(weight = MAIN_TABLE_WEIGHT) {
                    add(item = runeLongswordId, min = 1, weight = 10)
                    add(item = adamantPlatebodyId, min = 1, weight = 9)
                    add(item = adamantKiteshieldId, min = 1, weight = 3)
                    add(item = dragonMedHelmId, min = 1, weight = 1)
                    add(item = fireRuneId, min = 300, weight = 5)
                    add(item = airRuneId, min = 300, weight = 10)
                    add(item = ironArrowId, min = 690, weight = 10)
                    add(item = runiteBoltsId, min = 10, weight = 10)
                    add(item = lawRuneId, min = 30, weight = 5)
                    add(item = bloodRuneId, min = 30, weight = 5)
                    add(item = yewLogsNotedId, min = 150, weight = 10)
                    add(item = adamantiteBarId, min = 3, weight = 5)
                    add(item = runiteBarId, min = 1, weight = 3)
                    add(item = goldOreNotedId, min = 100, weight = 2)
                    add(item = amuletOfPowerId, min = 1, weight = 7)
                    add(item = dragonArrowtipsId, min = 5, weight = 5)
                    add(item = dragonDartTipId, min = 5, weight = 5)
                    add(item = dragonJavelinHeadsId, min = 15, weight = 5)
                    add(item = runiteLimbsId, min = 1, weight = 4)
                    add(item = sharkId, min = 4, weight = 4)
                }
            }
        }
    }

    companion object {
        const val NPC_ID = "npc.king_black_dragon"

        private const val MAIN_TABLE_WEIGHT = 128

        private val dragonBonesId get() = getRSCM("item.dragon_bones")
        private val blackDragonLeatherId get() = getRSCM("item.black_dragon_leather")
        private val runeLongswordId get() = getRSCM("item.rune_longsword")
        private val adamantPlatebodyId get() = getRSCM("item.adamant_platebody")
        private val adamantKiteshieldId get() = getRSCM("item.adamant_kiteshield")
        private val dragonMedHelmId get() = getRSCM("item.dragon_med_helm")
        private val fireRuneId get() = getRSCM("item.fire_rune")
        private val airRuneId get() = getRSCM("item.air_rune")
        private val ironArrowId get() = getRSCM("item.iron_arrow")
        private val runiteBoltsId get() = getRSCM("item.runite_bolts")
        private val lawRuneId get() = getRSCM("item.law_rune")
        private val bloodRuneId get() = getRSCM("item.blood_rune")
        private val yewLogsNotedId get() = getRSCM("item.yew_logs_noted")
        private val adamantiteBarId get() = getRSCM("item.adamantite_bar")
        private val runiteBarId get() = getRSCM("item.runite_bar")
        private val goldOreNotedId get() = getRSCM("item.gold_ore_noted")
        private val amuletOfPowerId get() = getRSCM("item.amulet_of_power")
        private val dragonArrowtipsId get() = getRSCM("item.dragon_arrowtips")
        private val dragonDartTipId get() = getRSCM("item.dragon_dart_tip")
        private val dragonJavelinHeadsId get() = getRSCM("item.dragon_javelin_heads")
        private val runiteLimbsId get() = getRSCM("item.runite_limbs")
        private val sharkId get() = getRSCM("item.shark")
    }
}
