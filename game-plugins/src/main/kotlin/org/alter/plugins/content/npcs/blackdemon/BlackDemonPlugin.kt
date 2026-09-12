package org.alter.plugins.content.npcs.blackdemon

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
import org.alter.game.model.weightedTableBuilder.Loot
import org.alter.game.model.weightedTableBuilder.LootTable
import org.alter.game.model.weightedTableBuilder.TableType
import org.alter.game.plugin.*
import org.alter.rscm.RSCM.getRSCM

/**
 * Black Demon (`npc.black_demon_1432`) combat definition and drop table.
 *
 * Loot is authored through the `drops {}` DSL:
 * - Malicious ashes are always dropped.
 * - A single weighted bonus roll awards at most one reward. The outer table's
 *   unallocated remainder represents the chance of dropping nothing, and a
 *   successful roll selects a rarity tier whose nested table then picks a
 *   uniformly random reward.
 */
class BlackDemonPlugin(
    r: PluginRepository,
    world: World,
    server: Server,
) : KotlinPlugin(r, world, server) {

    init {
        val maliciousAshesId = getRSCM("item.malicious_ashes_noted")

        val commonRewards = uniformPool(
            Loot(item = getRSCM("item.fire_rune"), min = 50, max = 100, weight = 1),
            Loot(item = getRSCM("item.nature_rune"), min = 20, max = 40, weight = 1),
            Loot(item = getRSCM("item.coal_noted"), min = 10, max = 25, weight = 1),
            Loot(item = getRSCM("item.death_rune"), min = 15, max = 30, weight = 1),
            Loot(item = getRSCM("item.mithril_arrow"), min = 20, max = 40, weight = 1),
            Loot(item = getRSCM("item.iron_arrow"), min = 40, max = 80, weight = 1),
        )

        val uncommonRewards = uniformPool(
            Loot(item = getRSCM("item.shark_noted"), min = 2, max = 4, weight = 1),
            Loot(item = getRSCM("item.prayer_potion4_noted"), min = 1, max = 3, weight = 1),
            Loot(item = getRSCM("item.blood_rune"), min = 15, max = 25, weight = 1),
            Loot(item = getRSCM("item.adamantite_bar_noted"), min = 2, max = 5, weight = 1),
            Loot(item = getRSCM("item.runite_ore_noted"), min = 1, max = 3, weight = 1),
            Loot(item = getRSCM("item.rune_arrow"), min = 15, max = 30, weight = 1),
            Loot(item = getRSCM("item.law_rune"), min = 15, max = 25, weight = 1),
            Loot(item = getRSCM("item.adamant_arrow"), min = 20, max = 40, weight = 1),
        )

        val rareRewards = uniformPool(
            Loot(item = getRSCM("item.rune_scimitar_noted"), min = 1, weight = 1),
            Loot(item = getRSCM("item.rune_longsword_noted"), min = 1, weight = 1),
            Loot(item = getRSCM("item.rune_kiteshield_noted"), min = 1, weight = 1),
            Loot(item = getRSCM("item.rune_full_helm_noted"), min = 1, weight = 1),
            Loot(item = getRSCM("item.uncut_diamond_noted"), min = 1, max = 2, weight = 1),
        )

        val ultraRareRewards = uniformPool(
            Loot(item = getRSCM("item.rune_platebody_noted"), min = 1, weight = 1),
            Loot(item = getRSCM("item.rune_platelegs_noted"), min = 1, weight = 1),
            Loot(item = getRSCM("item.black_demon_mask_noted"), min = 1, weight = 1),
            Loot(item = getRSCM("item.adamant_kiteshield_noted"), min = 1, weight = 1),
            Loot(item = getRSCM("item.crystal_key_noted"), min = 1, max = 2, weight = 1),
        )

        setCombatDef(NPC_ID) {
            species {
                +NpcSpecies.DEMON
            }

            configs {
                attackSpeed = 4
                respawnDelay = 25
            }

            aggro {
                radius = 4
                searchDelay = 1
            }

            stats {
                hitpoints = 157
                attack = 145
                strength = 148
                defence = 152
                magic = 1
                ranged = 1
            }

            bonuses {
                defenceStab = 50
                defenceSlash = 40
                defenceCrush = 30
                defenceMagic = 0
                defenceRanged = 50
            }

            anims {
                death = Animation.DEMON_DEATH
            }

            sound {
                attackSound = Sound.BLACK_DEMON_ATTACK
                deathSound = Sound.BLACK_DEMON_DEATH
                blockSound = Sound.BLACK_DEMON_HIT
            }

            drops {
                always {
                    add(item = maliciousAshesId, amount = 1)
                }

                main(weight = BONUS_ROLL_TOTAL) {
                    add(item = commonRewards, amount = 1, weight = BONUS_TIER_COMMON)
                    add(item = uncommonRewards, amount = 1, weight = BONUS_TIER_UNCOMMON)
                    add(item = rareRewards, amount = 1, weight = BONUS_TIER_RARE)
                    add(item = ultraRareRewards, amount = 1, weight = BONUS_TIER_ULTRA_RARE)
                }
            }
        }
    }

    /**
     * Builds a nested MAIN table that selects one of [entries] uniformly.
     */
    private fun uniformPool(vararg entries: Loot): LootTable {
        val drops = entries.toMutableSet()
        return LootTable(
            tableType = TableType.MAIN,
            tableWeight = drops.size,
            drops = drops,
        )
    }

    companion object {
        const val NPC_ID = "npc.black_demon_1432"

        /**
         * Total weight of the bonus roll. The gap between the sum of the tier
         * weights below and this total is the "no bonus drop" remainder.
         */
        private const val BONUS_ROLL_TOTAL = 200

        private const val BONUS_TIER_COMMON = 50
        private const val BONUS_TIER_UNCOMMON = 25
        private const val BONUS_TIER_RARE = 15
        private const val BONUS_TIER_ULTRA_RARE = 10
    }
}
