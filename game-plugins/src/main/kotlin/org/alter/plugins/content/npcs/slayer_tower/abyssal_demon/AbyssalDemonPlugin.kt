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

            drops {
                always {
                    add(item = abyssalAshesId, amount = 1)
                }

                main(weight = 128) {
                    add(item = coinsId, amount = 460, weight = 1)
                    add(item = coinsId, amount = 30, weight = 9)
                    add(item = coinsId, amount = 44, weight = 6)
                    add(item = coinsId, amount = 220, weight = 7)
                    add(item = coinsId, amount = 132, weight = 35)
                }

                main(weight = 128) {
                    add(item = airRuneId, amount = 50, weight = 8)
                    add(item = chaosRuneId, amount = 10, weight = 7)
                    add(item = bloodRuneId, amount = 7, weight = 4)
                    add(item = lawRuneId, amount = 3, weight = 1)
                }

                main(weight = 128) {
                    add(item = blackSwordId, amount = 1, weight = 4)
                    add(item = steelBattleaxeId, amount = 1, weight = 3)
                    add(item = blackAxeId, amount = 1, weight = 2)
                    add(item = mithrilKiteshieldId, amount = 1, weight = 1)
                    add(item = runeChainbodyId, amount = 1, weight = 1)
                    add(item = runeMedHelmId, amount = 1, weight = 1)
                }

                main(weight = 128) {
                    add(item = lobsterId, amount = 1, weight = 2)
                    add(item = adamantiteBarId, amount = 1, weight = 2)
                    add(item = pureEssenceId, amount = 60, weight = 5)
                }

                main(weight = 512) {
                    add(item = abyssalWhipId, amount = 1, weight = 1)
                }

                main(weight = 128) {
                    add(item = uncutSapphireId, amount = 1, weight = 5)
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
