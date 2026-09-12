package org.alter.plugins.content.npcs.slayer_tower.gargoyle

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
 * Gargoyle (Slayer 75) combat definition and drop table.
 */
class GargoylePlugin(
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
                hitpoints = 115
                attack = 111
                strength = 118
                defence = 120
                magic = 1
                ranged = 1
            }
            bonuses {
                defenceStab = 40
                defenceSlash = 30
                defenceCrush = 20
                defenceMagic = 0
                defenceRanged = 30
            }
            anims {
                attack = Animation.GARGOYLE_ATTACK
                block = Animation.GARGOYLE_HIT
                death = Animation.GARGOYLE_DEATH
            }
            sound {
                attackSound = Sound.GARGOYLE_ATTACK
                deathSound = Sound.GARGOYLE_DEATH
                blockSound = Sound.GARGOYLE_HIT
            }
            slayerData {
                levelRequirement = 75
                xp = 105.0
            }

            drops {
                main(weight = 128) {
                    add(item = coinsId, amount = 10000, weight = 5)
                    add(item = coinsId, min = 400, max = 800, weight = 20)
                    add(item = coinsId, min = 500, max = 1000, weight = 28)
                }

                main(weight = 128) {
                    add(item = fireRuneId, amount = 150, weight = 6)
                    add(item = fireRuneId, amount = 75, weight = 10)
                    add(item = chaosRuneId, amount = 30, weight = 8)
                    add(item = deathRuneId, amount = 15, weight = 5)
                }

                main(weight = 128) {
                    add(item = adamantPlatelegsId, amount = 1, weight = 4)
                    add(item = runeFullHelmId, amount = 1, weight = 3)
                    add(item = rune2hSwordId, amount = 1, weight = 2)
                    add(item = adamantBootsId, amount = 1, weight = 1)
                    add(item = runeBattleaxeId, amount = 1, weight = 1)
                    add(item = runePlatelegsId, amount = 1, weight = 1)
                }

                main(weight = 128) {
                    add(item = runiteOreId, amount = 1, weight = 2)
                    add(item = mithrilBarId, amount = 15, weight = 2)
                    add(item = goldBarId, min = 10, max = 15, weight = 3)
                    add(item = steelBarId, amount = 15, weight = 6)
                    add(item = pureEssenceId, amount = 150, weight = 6)
                    add(item = goldOreId, min = 10, max = 20, weight = 10)
                }

                main(weight = 512) {
                    add(item = graniteMaulId, amount = 1, weight = 1)
                    add(item = mysticRobeTopDarkId, amount = 1, weight = 1)
                }

                main(weight = 128) {
                    add(item = uncutSapphireId, amount = 1, weight = 5)
                }
            }
        }
    }

    companion object {
        val NPC_IDS = listOf("npc.gargoyle_412", "npc.gargoyle_413")

        private val coinsId get() = getRSCM("item.coins")
        private val fireRuneId get() = getRSCM("item.fire_rune")
        private val chaosRuneId get() = getRSCM("item.chaos_rune")
        private val deathRuneId get() = getRSCM("item.death_rune")
        private val adamantPlatelegsId get() = getRSCM("item.adamant_platelegs")
        private val runeFullHelmId get() = getRSCM("item.rune_full_helm")
        private val rune2hSwordId get() = getRSCM("item.rune_2h_sword")
        private val adamantBootsId get() = getRSCM("item.adamant_boots")
        private val runeBattleaxeId get() = getRSCM("item.rune_battleaxe")
        private val runePlatelegsId get() = getRSCM("item.rune_platelegs")
        private val runiteOreId get() = getRSCM("item.runite_ore")
        private val mithrilBarId get() = getRSCM("item.mithril_bar")
        private val goldBarId get() = getRSCM("item.gold_bar")
        private val steelBarId get() = getRSCM("item.steel_bar")
        private val pureEssenceId get() = getRSCM("item.pure_essence")
        private val goldOreId get() = getRSCM("item.gold_ore")
        private val graniteMaulId get() = getRSCM("item.granite_maul")
        private val mysticRobeTopDarkId get() = getRSCM("item.mystic_robe_top_dark")
        private val uncutSapphireId get() = getRSCM("item.uncut_sapphire")
    }
}
