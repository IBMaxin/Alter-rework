package org.alter.plugins.content.loot

import org.alter.game.model.weightedTableBuilder.LootTable
import org.alter.game.model.weightedTableBuilder.TableType
import org.alter.plugins.content.npcs.CowPlugin
import org.alter.plugins.content.npcs.blackdemon.BlackDemonPlugin
import org.alter.plugins.content.npcs.kbd.KbdConfigsPlugin
import org.alter.plugins.testing.PluginIntegrationSupport
import org.alter.rscm.RSCM
import org.junit.BeforeClass
import kotlin.reflect.KFunction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Smoke test that boots every plugin migrated to the `drops {}` DSL and walks
 * each registered table recursively, asserting the invariants the engine relies
 * on. This catches unresolved RSCM item ids and malformed nested tables without
 * requiring the cache.
 */
class LootTableIntegritySmokeTest {

    @Test
    fun `migrated plugins register only well-formed loot tables`() {
        val world = PluginIntegrationSupport.newWorld("loot-smoke")
        val server = PluginIntegrationSupport.newServer()

        CowPlugin(world.plugins, world, server)
        BlackDemonPlugin(world.plugins, world, server)
        KbdConfigsPlugin(world.plugins, world, server)

        val defs = world.plugins.npcCombatDefs.values.toList()
        assertEquals(3, defs.size, "expected cow, black demon, and kbd definitions")

        defs.forEach { def ->
            val tables = def.LootTables
            assertTrue(!tables.isNullOrEmpty(), "definition has no loot tables")
            tables.forEach { validate(it, "loot table") }
        }
    }

    private fun validate(table: LootTable, path: String) {
        when (table.tableType) {
            TableType.ALWAYS ->
                assertEquals(0, table.tableWeight ?: 0, "$path: ALWAYS tables must not declare a weight")
            TableType.MAIN -> {
                val accumulated = table.drops.sumOf { it.weight ?: 0 }
                assertTrue(
                    accumulated <= (table.tableWeight ?: 0),
                    "$path: item weight $accumulated exceeds table weight ${table.tableWeight}",
                )
            }
            TableType.PRE_ROLL, TableType.TERTIARY -> Unit
        }

        assertTrue(table.drops.isNotEmpty(), "$path: table has no entries")

        table.drops.forEach { loot ->
            when (val item = loot.item) {
                is Int -> assertTrue(item > 0, "$path: unresolved item id $item")
                is LootTable -> validate(item, "$path -> nested")
                is KFunction<*> -> Unit
                else -> error("$path: unsupported loot item type ${item?.javaClass}")
            }
        }
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun initRscm() {
            RSCM.init()
        }
    }
}
