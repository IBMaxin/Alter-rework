package org.alter.game.model.weightedTableBuilder

import dev.openrune.cache.CacheManager.getItem
import org.alter.game.model.Tile
import org.alter.game.model.entity.GroundItem
import org.alter.game.model.entity.Player
import kotlin.random.Random
import kotlin.reflect.KFunction
import kotlin.reflect.typeOf

enum class TableType {
    ALWAYS,
    PRE_ROLL,
    MAIN,
    TERTIARY,
}

data class Loot(
    val item: Any?,
    val min: Int,
    val max: Int = 1,
    val steepness: Int = 1,
    val weight: Int? = 0,
    val description: String? = null,
    val announce: Boolean = false,
    val block: Player.() -> Boolean = { true }
)

data class LootTable(
    val tableType: TableType,
    val tableWeight: Int? = 0,
    val drops: MutableSet<Loot>
) {
    init {
        when (tableType) {
            TableType.ALWAYS -> {
                require(tableWeight == null || tableWeight == 0) {
                    "ALWAYS loot table must not declare a table weight, but found $tableWeight across " +
                        "${drops.size} entries; ALWAYS entries are guaranteed drops and are never weighted."
                }
            }
            TableType.MAIN -> {
                if (tableWeight != null) {
                    val accumulated = drops.sumOf { it.weight ?: 0 }
                    require(tableWeight >= accumulated) {
                        "${tableType.name} loot table is misconfigured: configured total weight $tableWeight is " +
                            "less than the accumulated item weight $accumulated across ${drops.size} entries; " +
                            "the configured total must be at least $accumulated."
                    }
                }
            }
            TableType.PRE_ROLL, TableType.TERTIARY -> {
                // PRE_ROLL and TERTIARY tables do not use cumulative MAIN
                // selection: each entry's own weight is interpreted
                // independently, so no configured table total is required.
            }
        }
    }
}



fun randomStep(start: Int, stop: Int, step: Int): Int {
    val result = (start..stop step step).toList()
    if (result.isEmpty()) {
        return start
    }
    return (start..stop step step).toList().random()
}
val random = Random
fun random(range: IntRange): Int = random.nextInt(range.endInclusive - range.start + 1) + range.start
fun random(boundInclusive: Int) = random.nextInt(boundInclusive + 1)
/**
 * Table rollers
 */
fun LootTable.mainRoll(rng: Random = random): Loot? {
    // Only positive weights contribute to the cumulative total; null, zero and
    // negative weights are ignored so a misconfigured entry can never be selected.
    val entries = drops.filter { (it.weight ?: 0) > 0 }
    if (entries.isEmpty()) {
        return null
    }
    val configuredTotal = tableWeight ?: 0
    if (configuredTotal <= 0) {
        return null
    }
    val roll = rng.nextInt(configuredTotal) + 1
    var accumulated = 0
    for (loot in entries) {
        accumulated += loot.weight ?: 0
        if (accumulated >= roll) {
            return loot
        }
    }
    // The roll landed in the unallocated remainder of the configured total,
    // which represents a "nothing" result rather than an entry.
    return null
}
/**
 * A pre-roll is resolved with a single die draw from `0..127` (128 outcomes),
 * so a weight is interpreted as a numerator out of 128.
 *
 * - Weight `0` never succeeds.
 * - Weight `128` always succeeds.
 */
private const val PRE_ROLL_WEIGHT_MIN = 0
private const val PRE_ROLL_WEIGHT_MAX = 128

private fun requireValidPreRollWeight(weight: Int) {
    require(weight in PRE_ROLL_WEIGHT_MIN..PRE_ROLL_WEIGHT_MAX) {
        "Invalid pre-roll weight $weight: expected a value in " +
            "$PRE_ROLL_WEIGHT_MIN..$PRE_ROLL_WEIGHT_MAX, where $PRE_ROLL_WEIGHT_MIN never succeeds " +
            "and $PRE_ROLL_WEIGHT_MAX always succeeds."
    }
}

fun LootTable.preRoll(): Loot? {
    drops.forEach { requireValidPreRollWeight(it.weight ?: PRE_ROLL_WEIGHT_MIN) }
    for (loot in drops) {
        val weight = loot.weight ?: PRE_ROLL_WEIGHT_MIN
        if (random.nextInt(PRE_ROLL_WEIGHT_MAX) < weight) {
            return loot
        }
    }
    return null
}
fun LootTable.tertiaryRoll(): List<Loot> {
    val items = ArrayList<Loot>()
    for (loot in drops) {
        val weight = loot.weight ?: PRE_ROLL_WEIGHT_MIN
        if (weight > 0 && Random.nextInt(weight) == 0) {
            items += loot
        }
    }
    return items
}

fun Loot.handleToItem(killer: Player, dropTile: Tile): List<GroundItem> {
    val items = mutableListOf<GroundItem>()
    if (block.invoke(killer)) {
        when (item) {
            is Int -> {
                items.add(GroundItem(item, amount = randomStep(min, max, steepness), tile = dropTile, owner = killer))
            }
            is LootTable -> {
                items.addAll(roll(killer, listOf(item), dropTile))
            }
            is KFunction<*> -> {
                try {
                    if (item.returnType == typeOf<LootTable>()) {
                        val result = item.call() as LootTable
                        result.drops.forEach {
                            items.addAll(it.handleToItem(killer, dropTile))
                        }
                    } else if (item.returnType == typeOf<Loot>()) {
                        val result = item.call() as Loot
                        items.addAll(result.handleToItem(killer, dropTile))
                    } else {
                        throw IllegalStateException("Unhandled LootTable return type: ${item.returnType}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            else -> throw IllegalStateException("Unhandled drop type: ${item?.javaClass}")
        }
    }
    if (items.isNotEmpty()) {
        announceRareDrop(killer, this)
    }
    return items
}

/**
 * Resolves the display name of this entry's item when it can be determined.
 *
 * Name resolution is best-effort: entries that are not plain item ids (nested
 * tables or deferred functions) and any lookup that cannot be served fall back
 * to `null` so announcements still render without crashing.
 */
private fun Loot.itemNameOrNull(): String? =
    (item as? Int)?.let { id -> runCatching { getItem(id).name }.getOrNull() }

/**
 * Builds the private rare-drop announcement for this entry, or `null` when it
 * must not be announced.
 *
 * The entry only announces when [enabled] is `true` (the world configuration
 * flag) and the entry opts in through [Loot.announce]. The optional
 * [Loot.description] is appended only when it contains non-whitespace text, so a
 * missing or blank description never leaves dangling punctuation, double
 * spacing, or blank text behind.
 */
internal fun Loot.rareDropMessage(
    enabled: Boolean,
    itemName: String? = itemNameOrNull(),
): String? {
    if (!enabled || !announce) {
        return null
    }
    val base = if (itemName.isNullOrBlank()) "You received a rare drop" else "Rare drop: $itemName"
    val description = description?.trim().orEmpty()
    return if (description.isEmpty()) "$base!" else "$base! $description"
}

/**
 * Sends the rare-drop announcement for [loot] to [killer] when the world
 * configuration enables announcements.
 *
 * The message is written only to the credited killer; it is never broadcast
 * globally or partycast, and no external integrations are involved.
 */
fun announceRareDrop(killer: Player, loot: Loot) {
    loot.rareDropMessage(killer.world.gameContext.rareDropAnnouncements)?.let(killer::writeMessage)
}

/**
 * Rolls loot for [killer] against every table in [lootTables].
 *
 * Every table of a given [TableType] is resolved independently, so an NPC may
 * declare more than one MAIN (or PRE_ROLL/TERTIARY) slot:
 *
 * - ALWAYS: every entry of every ALWAYS table is a guaranteed drop.
 * - TERTIARY: every entry rolls independently against its own weight.
 * - PRE_ROLL: each PRE_ROLL table rolls independently and does **not** suppress
 *   the MAIN table.
 * - MAIN: each MAIN table performs its own weighted selection (which may
 *   resolve to "nothing" via the table's unallocated remainder).
 */
fun roll(killer: Player, lootTables: List<LootTable>?, dropTile: Tile): Set<GroundItem> {
    val dropSet = mutableSetOf<GroundItem>()
    val tables = lootTables.orEmpty()

    tables.filter { it.tableType == TableType.ALWAYS }.forEach { table ->
        table.drops.forEach { dropSet.addAll(it.handleToItem(killer, dropTile)) }
    }
    tables.filter { it.tableType == TableType.TERTIARY }.forEach { table ->
        table.tertiaryRoll().forEach { dropSet.addAll(it.handleToItem(killer, dropTile)) }
    }
    // Pre-rolls are independent of the main table: a successful pre-roll no
    // longer suppresses the MAIN drop.
    tables.filter { it.tableType == TableType.PRE_ROLL }.forEach { table ->
        table.preRoll()?.let { dropSet.addAll(it.handleToItem(killer, dropTile)) }
    }
    tables.filter { it.tableType == TableType.MAIN }.forEach { table ->
        table.mainRoll()?.let { dropSet.addAll(it.handleToItem(killer, dropTile)) }
    }
    return dropSet
}

