package org.alter.api.dsl

import org.alter.api.NpcCombatBuilder
import org.alter.game.model.entity.Player
import org.alter.game.model.weightedTableBuilder.Loot
import org.alter.game.model.weightedTableBuilder.LootTable
import org.alter.game.model.weightedTableBuilder.TableType

/**
 * Builds the list of [LootTable]s attached to an NPC combat definition.
 *
 * Each `always {}` / `main {}` / `preroll {}` / `tertiary {}` block produces
 * exactly one table, so a single NPC can declare multiple independent tables of
 * the same [TableType] (for example two separate MAIN slots). Every table is
 * resolved by [org.alter.game.model.weightedTableBuilder.roll].
 */
class WeightedTableBuilder {
    var combatBuilder: NpcCombatBuilder? = null

    /**
     * Collection of drop tables.
     */
    var LootTables: MutableList<LootTable> = mutableListOf()

    inner class AlwaysTableBuilder {
        private val drops = mutableSetOf<Loot>()

        fun add(item: Any?, min: Int = 1, max: Int = 1, steepness: Int = 1, description: String ? = null, announce : Boolean = false, block: (Player) -> Boolean = { true }) {
            drops.add(Loot(item = item, min = min, max = max, steepness = steepness, description = description, announce = announce, block = block))
        }
        fun add(item: Int, min: Int = 1, max: Int = 1, steepness: Int = 1, description: String ? = null, announce : Boolean = false, block: (Player) -> Boolean = { true }) {
            drops.add(Loot(item = item, min = min, max = max, steepness = steepness, description = description, announce = announce, block = block))
        }
        fun add(item: Int, amount: Int, description: String ? = null, announce : Boolean = false, block: (Player) -> Boolean = { true }) {
            drops.add(Loot(item = item, min = amount, max = amount, steepness = 1, description = description, announce = announce, block = block))
        }

        internal fun build() = LootTable(tableType = TableType.ALWAYS, tableWeight = 0, drops = drops)
    }
    fun always(init: AlwaysTableBuilder.() -> Unit) {
        val builder = AlwaysTableBuilder()
        builder.init()
        addTable(builder.build())
    }

    inner class MainTableBuilder(val weight: Int = 0) {
        private val drops = mutableSetOf<Loot>()

        fun add(item: Any?, min: Int = 1, max: Int = 1, steepness: Int = 1, description: String ? = null, announce : Boolean = false, block: (Player) -> Boolean = { true }) {
            drops.add(Loot(item = item, min = min, max = max, steepness = steepness, description = description, announce = announce, block = block))
        }
        fun add(item: Any?, amount: Int, description: String ? = null, announce : Boolean = false, block: (Player) -> Boolean = { true }) {
            drops.add(Loot(item = item, min = amount, max = amount, steepness = 1, description = description, announce = announce, block = block))
        }
        fun add(item: Any?, min: Int = 1, max: Int = 1, steepness: Int = 1, weight: Int = 0, description: String ? = null, announce : Boolean = false, block: (Player) -> Boolean = { true }) {
            drops.add(Loot(item = item, min = min, max = max, steepness = steepness, weight = weight, description = description, announce = announce, block = block))
        }
        fun add(item: Any?, amount: Int, weight: Int = 0, description: String ? = null, announce : Boolean = false, block: (Player) -> Boolean = { true }) {
            drops.add(Loot(item = item, min = amount, max = amount, steepness = 1, weight = weight, description = description, announce = announce, block = block))
        }

        internal fun build() = LootTable(tableType = TableType.MAIN, tableWeight = weight, drops = drops)
    }
    fun main(weight: Int = 0 ,init: MainTableBuilder.() -> Unit) {
        val builder = MainTableBuilder(weight = weight)
        builder.init()
        addTable(builder.build())
    }

    inner class preroll {
        private val drops = mutableSetOf<Loot>()

        fun add(item: Any?, min: Int = 1, max: Int = 1, steepness: Int = 1, weight: Int = 0, description: String ? = null, announce : Boolean = false, block: (Player) -> Boolean = { true }) {
            drops.add(Loot(item = item, min = min, max = max, steepness = steepness, weight = weight, description = description, announce = announce, block = block))
        }
        fun add(item: Any?, amount: Int, weight: Int = 0, description: String ? = null, announce : Boolean = false, block: (Player) -> Boolean = { true }) {
            drops.add(Loot(item = item, min = amount, max = amount, steepness = 1, weight = weight, description = description, announce = announce, block = block))
        }

        internal fun build() = LootTable(tableType = TableType.PRE_ROLL, tableWeight = 0, drops = drops)
    }
    fun preroll(init: preroll.() -> Unit) {
        val builder = preroll()
        builder.init()
        addTable(builder.build())
    }

    inner class tertiary(val weight: Int = 0) {
        private val drops = mutableSetOf<Loot>()

        fun add(item: Any?, min: Int = 1, weight: Int = 0, max: Int = 1, steepness: Int = 1, description: String ? = null, announce : Boolean = false, block: (Player) -> Boolean = { true }) {
            drops.add(Loot(item = item, min = min, max = max, steepness = steepness, weight = weight, description = description, announce = announce, block = block))
        }
        fun add(item: Any?, amount: Int, weight: Int = 0, description: String ? = null, announce : Boolean = false, block: (Player) -> Boolean = { true }) {
            drops.add(Loot(item = item, min = amount, max = amount, steepness = 1, weight = weight, description = description, announce = announce, block = block))
        }

        internal fun build() = LootTable(tableType = TableType.TERTIARY, tableWeight = weight, drops = drops)
    }
    fun tertiary(weight: Int = 0, init: tertiary.() -> Unit) {
        val builder = tertiary(weight = weight)
        builder.init()
        addTable(builder.build())
    }

    private fun addTable(table: LootTable) {
        LootTables.add(table)
        combatBuilder?.LootTable = LootTables
    }
}
